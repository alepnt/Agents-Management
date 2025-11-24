package com.example.server.service; // Colloca il servizio nel package delle componenti server.
// Riga vuota mantenuta per separare package e importazioni.
import com.example.common.dto.TeamDTO; // Importa il DTO utilizzato per scambiare dati dei team con il client.
import com.example.server.domain.Team; // Importa l'entità Team che rappresenta la tabella del database.
import com.example.server.repository.TeamRepository; // Importa il repository JPA responsabile delle operazioni sui team.
import com.example.server.service.mapper.TeamMapper; // Importa il mapper che converte tra entità Team e TeamDTO.
import org.springframework.stereotype.Service; // Importa l'annotazione per registrare il componente come servizio Spring.
import org.springframework.transaction.annotation.Transactional; // Importa l'annotazione che abilita la gestione transazionale sui metodi.
import org.springframework.util.StringUtils; // Importa le utility per verificare stringhe non vuote.
// Riga vuota usata per separare gli import di framework dagli altri.
import java.util.List; // Importa la collezione List usata per restituire più elementi.
import java.util.Objects; // Importa le utility per i controlli di nullità.
import java.util.Optional; // Importa la classe Optional per rappresentare valori presenti o assenti.
import java.util.stream.StreamSupport; // Importa StreamSupport per creare stream a partire da Iterable.
// Riga vuota che migliora la leggibilità tra import e definizione di classe.
@Service // Segnala che questa classe è un servizio gestito da Spring.
public class TeamService { // Classe che incapsula la logica applicativa relativa ai team.
// Riga vuota per separare dichiarazioni e costruttori.
    private final TeamRepository teamRepository; // Repository che esegue le operazioni di persistenza sui team.
// Riga vuota per separare i campi dal costruttore.
    public TeamService(TeamRepository teamRepository) { // Costruttore che inietta il repository necessario al servizio.
        this.teamRepository = teamRepository; // Salva il repository in un campo per utilizzi successivi.
    } // Chiusura del costruttore di TeamService.
// Riga vuota prima delle API pubbliche.
    public List<TeamDTO> findAll() { // Recupera tutti i team e li converte in DTO.
        return StreamSupport.stream(teamRepository.findAll().spliterator(), false) // Crea uno stream a partire da tutte le entità trovate nel repository.
                .map(TeamMapper::toDto) // Converte ogni entità Team nel relativo DTO.
                .toList(); // Raccoglie i DTO in una lista immutabile.
    } // Chiusura del metodo findAll.
// Riga vuota tra i metodi pubblici.
    public Optional<TeamDTO> findById(Long id) { // Cerca un team tramite il suo identificativo.
        return teamRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica che l'id non sia nullo e interroga il repository.
                .map(TeamMapper::toDto); // Se presente, converte l'entità in DTO.
    } // Chiusura del metodo findById.
// Riga vuota di separazione per i metodi transazionali.
    @Transactional // Esegue il metodo all'interno di una transazione Spring.
    public TeamDTO create(TeamDTO dto) { // Crea un nuovo team a partire dai dati forniti.
        TeamDTO validated = Objects.requireNonNull(dto, "team must not be null"); // Controlla che il DTO non sia nullo.
        validate(validated); // Applica le regole di validazione al DTO.
        String normalizedName = normalize(validated.getName()); // Rimuove spazi inutili dal nome del team.
        ensureUniqueName(normalizedName, null); // Verifica che non esista già un team con lo stesso nome.
        Team toSave = new Team(null, normalizedName); // Costruisce una nuova entità Team pronta per il salvataggio.
        Team saved = teamRepository.save(toSave); // Persiste l'entità e ottiene la versione salvata.
        return TeamMapper.toDto(saved); // Restituisce il DTO corrispondente al team salvato.
    } // Chiusura del metodo create.
// Riga vuota per separare le operazioni di aggiornamento.
    @Transactional // Garantisce che l'aggiornamento avvenga in una transazione.
    public Optional<TeamDTO> update(Long id, TeamDTO dto) { // Aggiorna un team esistente con i nuovi dati forniti.
        TeamDTO validated = Objects.requireNonNull(dto, "team must not be null"); // Verifica che il DTO sia presente.
        validate(validated); // Applica le regole di validazione sul DTO.
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Verifica che l'id passato sia valorizzato.
        String normalizedName = normalize(validated.getName()); // Normalizza il nome eliminando spazi superflui.
        return teamRepository.findById(requiredId) // Recupera il team da aggiornare dal repository.
                .map(existing -> { // Elabora il risultato solo se è stato trovato un team.
                    ensureUniqueName(normalizedName, requiredId); // Si assicura che il nuovo nome non sia già utilizzato da altri team.
                    Team toSave = new Team(existing.getId(), normalizedName); // Crea un'istanza aggiornata mantenendo l'id originale.
                    return TeamMapper.toDto(teamRepository.save(toSave)); // Salva le modifiche e restituisce il DTO aggiornato.
                }); // Chiude la lambda associata alla mappatura dell'Optional.
    } // Chiusura del metodo update.
// Riga vuota per separare l'operazione di cancellazione.
    @Transactional // Richiede che l'eliminazione avvenga in modo atomico.
    public boolean delete(Long id) { // Elimina il team corrispondente all'id specificato.
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Si assicura che l'id sia stato fornito.
        return teamRepository.findById(requiredId) // Cerca il team nel repository.
                .map(existing -> { // Se il team esiste, procede con la cancellazione.
                    Team nonNullExisting = Objects.requireNonNull(existing, "team must not be null"); // Conferma che l'entità non sia nulla.
                    teamRepository.delete(nonNullExisting); // Rimuove l'entità dal database.
                    return true; // Indica che la cancellazione è avvenuta con successo.
                }) // Chiude la lambda di mappatura.
                .orElse(false); // Restituisce false se il team non è stato trovato.
    } // Chiusura del metodo delete.
// Riga vuota per iniziare i metodi di supporto privati.
    private void validate(TeamDTO dto) { // Controlla che il DTO del team rispetti i requisiti base.
        if (!StringUtils.hasText(dto.getName())) { // Se il nome è assente o vuoto l'input non è valido.
            throw new IllegalArgumentException("Il nome del team è obbligatorio"); // Genera un'eccezione esplicativa per il chiamante.
        } // Fine del controllo sul nome.
    } // Chiusura del metodo validate.
// Riga vuota per il metodo che gestisce l'unicità del nome.
    private void ensureUniqueName(String name, Long currentId) { // Verifica che il nome del team non sia già utilizzato da un'altra entità.
        teamRepository.findByName(name) // Cerca un eventuale team con lo stesso nome.
                .filter(existing -> currentId == null || !existing.getId().equals(currentId)) // Esclude dal controllo il team corrente durante un aggiornamento.
                .ifPresent(existing -> { // Se è stato trovato un duplicato, esegue la logica di errore.
                    throw new IllegalArgumentException("Esiste già un team con questo nome"); // Segnala che il nome è già in uso.
                }); // Chiusura della lambda associata a ifPresent.
    } // Chiusura del metodo ensureUniqueName.
// Riga vuota per l'ultimo metodo di utilità.
    private String normalize(String value) { // Ripulisce una stringa eliminando gli spazi superflui ai margini.
        return value != null ? value.trim() : null; // Restituisce la stringa rifinita o null se l'input è nullo.
    } // Chiusura del metodo normalize.
} // Chiusura della classe TeamService.
