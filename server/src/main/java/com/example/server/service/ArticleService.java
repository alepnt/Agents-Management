package com.example.server.service; // Definisce il package del servizio articoli

import com.example.common.dto.ArticleDTO; // Importa il DTO utilizzato per esporre gli articoli
import com.example.server.domain.Article; // Importa l'entità di dominio Article
import com.example.server.repository.ArticleRepository; // Importa il repository per accedere ai dati degli articoli
import com.example.server.service.mapper.ArticleMapper; // Importa il mapper tra entità e DTO
import org.springframework.stereotype.Service; // Importa l'annotazione di servizio Spring
import org.springframework.transaction.annotation.Transactional; // Importa il supporto transazionale
import org.springframework.util.StringUtils; // Importa utilità per controlli su stringhe

import java.math.BigDecimal; // Importa BigDecimal per gestire importi monetari
import java.util.List; // Importa la collezione List per i risultati
import java.util.Objects; // Importa utilità per verifiche di nullità
import java.util.Optional; // Importa Optional per risultati facoltativi

@Service // Contrassegna la classe come servizio gestito da Spring
public class ArticleService { // Gestisce la logica applicativa relativa agli articoli

    private final ArticleRepository articleRepository; // Repository per operazioni di persistenza sugli articoli

    public ArticleService(ArticleRepository articleRepository) { // Costruttore che riceve il repository tramite injection
        this.articleRepository = articleRepository; // Assegna il repository al campo interno
    }

    public List<ArticleDTO> findAll() { // Recupera tutti gli articoli ordinati per nome
        return articleRepository.findAllByOrderByNameAsc().stream() // Legge tutti gli articoli e li trasforma in stream
                .map(ArticleMapper::toDto) // Converte ciascun articolo in DTO
                .toList(); // Colleziona i DTO in una lista immutabile
    }

    public Optional<ArticleDTO> findById(Long id) { // Recupera un articolo per id
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca l'articolo assicurando che l'id non sia nullo
                .map(ArticleMapper::toDto); // Converte l'entità trovata in DTO
    }

    @Transactional // La creazione avviene in una transazione
    public ArticleDTO create(ArticleDTO dto) { // Crea un nuovo articolo
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null"); // Verifica che il DTO non sia nullo
        validate(validatedDto); // Applica le regole di validazione
        Article source = Objects.requireNonNull(ArticleMapper.fromDto(validatedDto), // Mappa il DTO in entità
                "mapped article must not be null"); // Messaggio se la mappatura restituisce null
        Article toSave = Objects.requireNonNull(Article.create( // Costruisce l'entità da persistere
                normalize(source.getCode()), // Normalizza il codice articolo
                normalize(source.getName()), // Normalizza il nome
                normalize(source.getDescription()), // Normalizza la descrizione
                normalizePrice(source.getUnitPrice()), // Normalizza il prezzo unitario
                source.getVatRate(), // Imposta l'aliquota IVA
                normalize(source.getUnitOfMeasure()) // Normalizza l'unità di misura
        ), "created article must not be null"); // Messaggio nel caso la factory restituisca null
        Article saved = articleRepository.save(toSave); // Salva l'entità nel database
        return ArticleMapper.toDto(saved); // Ritorna il DTO dell'entità salvata
    }

    @Transactional // Anche l'aggiornamento deve essere atomico
    public Optional<ArticleDTO> update(Long id, ArticleDTO dto) { // Aggiorna un articolo esistente
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null"); // Verifica che il DTO sia presente
        validate(validatedDto); // Esegue la validazione dei dati
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca l'articolo da aggiornare
                .map(existing -> { // Se presente, applica l'aggiornamento
                    Article updateSource = Objects.requireNonNull(Article.create( // Crea un'istanza con i nuovi dati
                            normalize(validatedDto.getCode()), // Normalizza il codice
                            normalize(validatedDto.getName()), // Normalizza il nome
                            normalize(validatedDto.getDescription()), // Normalizza la descrizione
                            normalizePrice(validatedDto.getUnitPrice()), // Normalizza il prezzo
                            validatedDto.getVatRate(), // Imposta l'aliquota IVA aggiornata
                            normalize(validatedDto.getUnitOfMeasure()) // Normalizza l'unità di misura
                    ), "created article must not be null"); // Messaggio in caso di fallimento nella creazione
                    Article updated = Objects.requireNonNull(existing.updateFrom(updateSource), // Applica l'aggiornamento all'entità esistente
                            "updated article must not be null"); // Messaggio se l'aggiornamento produce null
                    Article saved = articleRepository.save(updated); // Salva l'entità aggiornata
                    return ArticleMapper.toDto(saved); // Converte l'entità aggiornata in DTO
                });
    }

    @Transactional // La cancellazione è racchiusa in una transazione
    public boolean delete(Long id) { // Cancella un articolo per id
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica l'esistenza dell'articolo
                .map(existing -> { // Se esiste procede con la rimozione
                    articleRepository.deleteById(id); // Elimina l'articolo dal database
                    return true; // Indica che la cancellazione è avvenuta
                })
                .orElse(false); // Ritorna false se l'articolo non esiste
    }

    public Article require(Long id) { // Restituisce l'articolo oppure solleva eccezione se assente
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca l'articolo controllando l'id
                .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato")); // Lancia eccezione se non trovato
    }

    private void validate(ArticleDTO dto) { // Valida i campi obbligatori dell'articolo
        if (dto == null || !StringUtils.hasText(dto.getName())) { // Controlla che il DTO esista e che il nome non sia vuoto
            throw new IllegalArgumentException("Il nome dell'articolo è obbligatorio"); // Solleva eccezione se il nome manca
        }
        BigDecimal price = dto.getUnitPrice(); // Recupera il prezzo unitario fornito
        if (price != null && price.signum() < 0) { // Verifica che il prezzo non sia negativo
            throw new IllegalArgumentException("Il prezzo unitario non può essere negativo"); // Solleva eccezione se il valore è inferiore a zero
        }
    }

    private String normalize(String value) { // Elimina spazi superflui dalle stringhe
        return value != null ? value.trim() : null; // Restituisce la stringa trimmata o null se il valore manca
    }

    private BigDecimal normalizePrice(BigDecimal price) { // Garantisce un valore di prezzo non nullo
        return price != null ? price : BigDecimal.ZERO; // Restituisce il prezzo fornito o zero se nullo
    }
}
