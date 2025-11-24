package com.example.server.service; // Inserisce il servizio nel package principale del server.

import com.example.common.dto.AgentDTO; // Importa il DTO usato per trasferire i dati dell'agente.
import com.example.server.domain.Agent; // Importa l'entità di dominio che rappresenta l'agente nel database.
import com.example.server.repository.AgentRepository; // Importa il repository JPA incaricato di leggere e scrivere gli agenti.
import com.example.server.service.mapper.AgentMapper; // Importa il mapper che traduce tra entità Agent e AgentDTO.
import org.springframework.stereotype.Service; // Importa l'annotazione che registra la classe come servizio Spring.
import org.springframework.transaction.annotation.Transactional; // Importa l'annotazione per la gestione delle transazioni.
import org.springframework.util.StringUtils; // Importa gli helper per validare e manipolare stringhe.

import java.util.List; // Importa l'interfaccia List per restituire collezioni ordinate.
import java.util.Objects; // Importa le utility per controllare i parametri non nulli.
import java.util.Optional; // Importa Optional per modellare risultati facoltativi.

@Service // Marca la classe come servizio gestito dal contenitore Spring.
public class AgentService { // Classe che incapsula le operazioni di business sugli agenti.

    private final AgentRepository agentRepository; // Repository usato per tutte le operazioni di persistenza degli agenti.

    public AgentService(AgentRepository agentRepository) { // Costruttore che riceve il repository tramite dependency injection.
        this.agentRepository = agentRepository; // Assegna il repository al campo interno per utilizzi futuri.
    } // Termine del costruttore di AgentService.

    public List<AgentDTO> findAll() { // Recupera tutti gli agenti e li ordina per codice.
        return agentRepository.findAllByOrderByAgentCodeAsc().stream() // Interroga il database e crea uno stream delle entità trovate.
                .map(AgentMapper::toDto) // Converte ogni entità Agent nel rispettivo DTO.
                .toList(); // Raccoglie tutti i DTO in una lista immutabile.
    } // Chiusura del metodo findAll.

    public Optional<AgentDTO> findById(Long id) { // Cerca un agente tramite il suo identificativo.
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Controlla che l'id sia valorizzato e interroga il repository.
                .map(AgentMapper::toDto); // Se l'entità esiste, la converte in DTO.
    } // Chiusura del metodo findById.

    @Transactional // Esegue la creazione in una transazione per garantire consistenza.
    public AgentDTO create(AgentDTO dto) { // Crea un nuovo agente a partire dal DTO.
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null"); // Impone che il DTO di input non sia nullo.
        validate(validatedDto); // Esegue le validazioni sui campi obbligatori.
        Agent agent = Objects.requireNonNull(AgentMapper.fromDto(validatedDto), "mapped agent must not be null"); // Converte il DTO in entità e verifica che la conversione sia riuscita.
        Agent toSave = Agent.forUser(agent.getUserId(), normalize(agent.getAgentCode()), normalize(agent.getTeamRole())); // Normalizza i valori testuali e costruisce l'entità pronta per il salvataggio.
        Agent saved = agentRepository.save(toSave); // Salva l'entità nel database.
        return AgentMapper.toDto(saved); // Restituisce il DTO dell'agente appena salvato.
    } // Chiusura del metodo create.

    @Transactional // Protegge l'operazione di aggiornamento con una transazione.
    public Optional<AgentDTO> update(Long id, AgentDTO dto) { // Aggiorna un agente esistente con i dati forniti.
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null"); // Verifica che il DTO sia stato passato.
        validate(validatedDto); // Applica le regole di validazione ai dati in ingresso.
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Recupera l'agente da aggiornare assicurandosi che l'id non sia nullo.
                .map(existing -> new Agent(existing.getId(), // Crea una nuova entità mantenendo l'identificativo originale.
                        validatedDto.getUserId(), // Propaga l'utente associato.
                        normalize(validatedDto.getAgentCode()), // Normalizza il codice agente ricevuto.
                        normalize(validatedDto.getTeamRole()))) // Normalizza il ruolo del team.
                .map(agentRepository::save) // Salva l'entità aggiornata nel database.
                .map(AgentMapper::toDto); // Converte il risultato persistito in DTO.
    } // Chiusura del metodo update.

    @Transactional // Avvolge la cancellazione in una transazione per garantire atomicità.
    public boolean delete(Long id) { // Elimina un agente in base al suo identificativo.
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica l'id e cerca l'agente.
                .map(existing -> { // Se l'agente esiste, esegue il blocco di cancellazione.
                    agentRepository.deleteById(id); // Rimuove l'entità dal database.
                    return true; // Segnala che la cancellazione è andata a buon fine.
                }) // Chiusura della lambda di gestione dell'Optional.
                .orElse(false); // Restituisce false se l'agente non è stato trovato.
    } // Chiusura del metodo delete.

    public Agent require(Long id) { // Restituisce l'agente richiesto o lancia un'eccezione se assente.
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Controlla l'id e interroga il repository.
                .orElseThrow(() -> new IllegalArgumentException("Agente non trovato")); // Solleva un errore chiaro quando l'agente manca.
    } // Chiusura del metodo require.

    private void validate(AgentDTO dto) { // Valida che il DTO contenga tutti i campi obbligatori.
        if (dto.getUserId() == null) { // Verifica la presenza dell'utente associato all'agente.
            throw new IllegalArgumentException("L'utente associato è obbligatorio"); // Lancia un'eccezione se il riferimento manca.
        } // Fine del controllo sull'utente.
        if (!StringUtils.hasText(dto.getAgentCode())) { // Controlla che il codice agente non sia vuoto o solo spazi.
            throw new IllegalArgumentException("Il codice agente è obbligatorio"); // Avvisa che il codice è necessario.
        } // Fine del controllo sul codice agente.
        if (!StringUtils.hasText(dto.getTeamRole())) { // Verifica che il ruolo nel team sia specificato.
            throw new IllegalArgumentException("Il ruolo nel team è obbligatorio"); // Solleva un'eccezione se il ruolo manca.
        } // Fine del controllo sul ruolo nel team.
    } // Chiusura del metodo validate.

    private String normalize(String value) { // Ripulisce le stringhe eliminando gli spazi esterni.
        return value != null ? value.trim() : null; // Restituisce la stringa rifinita oppure null se l'input era nullo.
    } // Chiusura del metodo normalize.
} // Fine della classe AgentService.
