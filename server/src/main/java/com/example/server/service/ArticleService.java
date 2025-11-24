package com.example.server.service; // Specifica il package che ospita il servizio dedicato agli articoli.

import com.example.common.dto.ArticleDTO; // Importa il DTO impiegato per esporre i dati degli articoli all'esterno.
import com.example.server.domain.Article; // Importa l'entità Article che rappresenta un prodotto persistito.
import com.example.server.repository.ArticleRepository; // Importa il repository JPA responsabile delle operazioni sugli articoli.
import com.example.server.service.mapper.ArticleMapper; // Importa il mapper che converte tra entità Article e ArticleDTO.
import org.springframework.stereotype.Service; // Importa l'annotazione che registra la classe come servizio Spring.
import org.springframework.transaction.annotation.Transactional; // Importa il supporto transazionale di Spring.
import org.springframework.util.StringUtils; // Importa gli helper per validare stringhe non vuote.

import java.math.BigDecimal; // Importa BigDecimal per gestire importi monetari con precisione.
import java.util.List; // Importa l'interfaccia List usata per restituire collezioni ordinate.
import java.util.Objects; // Importa le utility per i controlli di nullità.
import java.util.Optional; // Importa Optional per rappresentare risultati facoltativi.

@Service // Indica che la classe è un servizio Spring disponibile per l'iniezione.
public class ArticleService { // Classe che gestisce la logica applicativa relativa agli articoli.

    private final ArticleRepository articleRepository; // Repository che esegue le operazioni di persistenza sugli articoli.

    public ArticleService(ArticleRepository articleRepository) { // Costruttore che riceve il repository tramite dependency injection.
        this.articleRepository = articleRepository; // Salva il repository in un campo per utilizzi futuri.
    } // Chiusura del costruttore.

    public List<ArticleDTO> findAll() { // Recupera tutti gli articoli ordinati alfabeticamente.
        return articleRepository.findAllByOrderByNameAsc().stream() // Interroga il database e crea uno stream di articoli ordinati.
                .map(ArticleMapper::toDto) // Converte ogni entità Article nel relativo DTO.
                .toList(); // Raccoglie i DTO in una lista immutabile.
    } // Chiusura del metodo findAll.

    public Optional<ArticleDTO> findById(Long id) { // Cerca un articolo specifico tramite l'identificativo.
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica che l'id sia presente e interroga il repository.
                .map(ArticleMapper::toDto); // Se trovato, converte l'entità in DTO.
    } // Chiusura del metodo findById.

    @Transactional // Esegue la creazione all'interno di una transazione per garantire coerenza.
    public ArticleDTO create(ArticleDTO dto) { // Crea un nuovo articolo utilizzando i dati forniti.
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null"); // Impone che il DTO non sia nullo.
        validate(validatedDto); // Applica le regole di validazione sui campi principali.
        Article source = Objects.requireNonNull(ArticleMapper.fromDto(validatedDto), // Converte il DTO in entità e verifica che la conversione sia riuscita.
                "mapped article must not be null"); // Messaggio usato se la conversione fallisce.
        Article toSave = Objects.requireNonNull(Article.create( // Crea l'entità da salvare applicando le normalizzazioni necessarie.
                normalize(source.getCode()), // Normalizza il codice articolo eliminando spazi superflui.
                normalize(source.getName()), // Normalizza il nome per evitare spazi indesiderati.
                normalize(source.getDescription()), // Normalizza la descrizione per mantenerla pulita.
                normalizePrice(source.getUnitPrice()), // Sostituisce eventuali prezzi nulli con zero.
                source.getVatRate(), // Mantiene l'aliquota IVA fornita.
                normalize(source.getUnitOfMeasure()) // Normalizza l'unità di misura.
        ), "created article must not be null"); // Messaggio usato se la factory restituisce un valore inatteso.
        Article saved = articleRepository.save(toSave); // Persiste l'entità nel database.
        return ArticleMapper.toDto(saved); // Restituisce il DTO corrispondente all'articolo salvato.
    } // Chiusura del metodo create.

    @Transactional // Protegge l'aggiornamento con una transazione per evitare inconsistenze.
    public Optional<ArticleDTO> update(Long id, ArticleDTO dto) { // Aggiorna i dati di un articolo esistente.
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null"); // Controlla che il DTO sia presente.
        validate(validatedDto); // Verifica che i campi obbligatori siano valorizzati.
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Recupera l'articolo da modificare assicurandosi che l'id non sia nullo.
                .map(existing -> { // Se l'articolo esiste, procede con l'aggiornamento.
                    Article updateSource = Objects.requireNonNull(Article.create( // Crea un'istanza con i nuovi dati normalizzati.
                            normalize(validatedDto.getCode()), // Normalizza il codice aggiornato.
                            normalize(validatedDto.getName()), // Normalizza il nome aggiornato.
                            normalize(validatedDto.getDescription()), // Normalizza la descrizione aggiornata.
                            normalizePrice(validatedDto.getUnitPrice()), // Imposta un prezzo non nullo e non negativo.
                            validatedDto.getVatRate(), // Mantiene l'aliquota IVA indicata nel DTO.
                            normalize(validatedDto.getUnitOfMeasure()) // Normalizza l'unità di misura aggiornata.
                    ), "created article must not be null"); // Messaggio per la gestione di conversioni non riuscite.
                    Article updated = Objects.requireNonNull(existing.updateFrom(updateSource), // Applica i nuovi valori all'entità esistente.
                            "updated article must not be null"); // Messaggio nel caso in cui l'aggiornamento restituisca null.
                    Article saved = articleRepository.save(updated); // Salva l'entità aggiornata sul database.
                    return ArticleMapper.toDto(saved); // Converte il risultato persistito in DTO.
                }); // Chiusura della lambda di mappatura dell'Optional.
    } // Chiusura del metodo update.

    @Transactional // Avvolge la cancellazione in una transazione per garantire atomicità.
    public boolean delete(Long id) { // Elimina un articolo a partire dal suo identificativo.
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Controlla l'id e cerca l'articolo corrispondente.
                .map(existing -> { // Se l'articolo è presente, esegue il blocco di cancellazione.
                    articleRepository.deleteById(id); // Rimuove l'entità dal database.
                    return true; // Indica che la cancellazione è stata completata correttamente.
                }) // Chiusura della lambda di gestione dell'Optional.
                .orElse(false); // Restituisce false se l'articolo non è stato trovato.
    } // Chiusura del metodo delete.

    public Article require(Long id) { // Restituisce l'articolo richiesto o solleva un'eccezione se mancante.
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica l'id e recupera l'entità.
                .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato")); // Lancia un'eccezione chiara se l'articolo non esiste.
    } // Chiusura del metodo require.

    private void validate(ArticleDTO dto) { // Controlla che il DTO rispetti le regole minime di validità.
        if (dto == null || !StringUtils.hasText(dto.getName())) { // Se manca il DTO o il nome è vuoto, i dati non sono validi.
            throw new IllegalArgumentException("Il nome dell'articolo è obbligatorio"); // Segnala che il nome è un campo richiesto.
        } // Fine del controllo sul nome.
        BigDecimal price = dto.getUnitPrice(); // Estrae il prezzo unitario fornito.
        if (price != null && price.signum() < 0) { // Se il prezzo esiste ma è negativo, i dati non sono accettabili.
            throw new IllegalArgumentException("Il prezzo unitario non può essere negativo"); // Comunica che il prezzo deve essere maggiore o uguale a zero.
        } // Fine del controllo sul prezzo.
    } // Chiusura del metodo validate.

    private String normalize(String value) { // Ripulisce una stringa eliminando gli spazi superflui ai margini.
        return value != null ? value.trim() : null; // Restituisce la stringa rifinita o null se l'input era assente.
    } // Chiusura del metodo normalize.

    private BigDecimal normalizePrice(BigDecimal price) { // Garantisce che il prezzo unitario abbia sempre un valore valido.
        return price != null ? price : BigDecimal.ZERO; // Usa il prezzo fornito oppure zero quando manca.
    } // Chiusura del metodo normalizePrice.
} // Fine della classe ArticleService.
