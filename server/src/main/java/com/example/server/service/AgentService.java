package com.example.server.service; // Definisce il package del servizio

import com.example.common.dto.AgentDTO; // Importa il DTO che rappresenta l'agente lato API
import com.example.server.domain.Agent; // Importa l'entità di dominio Agent
import com.example.server.repository.AgentRepository; // Importa il repository per persistere gli agenti
import com.example.server.service.mapper.AgentMapper; // Importa il mapper per convertire tra entità e DTO
import org.springframework.stereotype.Service; // Importa l'annotazione per marcare il servizio Spring
import org.springframework.transaction.annotation.Transactional; // Importa il supporto per le transazioni
import org.springframework.util.StringUtils; // Importa utilità per la gestione delle stringhe

import java.util.List; // Importa la lista per collezionare i risultati
import java.util.Objects; // Importa utilità per i controlli di nullità
import java.util.Optional; // Importa il wrapper Optional per i risultati facoltativi

@Service // Indica a Spring che questa classe è un componente di servizio
public class AgentService { // Classe che incapsula la logica di business sugli agenti

    private final AgentRepository agentRepository; // Repository per accedere ai dati degli agenti

    public AgentService(AgentRepository agentRepository) { // Costruttore che riceve il repository in injection
        this.agentRepository = agentRepository; // Assegna il repository al campo interno
    }

    public List<AgentDTO> findAll() { // Restituisce tutti gli agenti ordinati per codice
        return agentRepository.findAllByOrderByAgentCodeAsc().stream() // Recupera gli agenti dal database e li trasforma in stream
                .map(AgentMapper::toDto) // Converte ogni entità Agent in DTO
                .toList(); // Colleziona i DTO in una lista immutabile
    }

    public Optional<AgentDTO> findById(Long id) { // Recupera un agente per identificativo
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca l'agente, fallendo se l'id è nullo
                .map(AgentMapper::toDto); // Converte l'entità trovata in DTO
    }

    @Transactional // Garantisce che l'operazione di creazione avvenga in una transazione
    public AgentDTO create(AgentDTO dto) { // Crea un nuovo agente
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null"); // Verifica che il DTO non sia nullo
        validate(validatedDto); // Esegue le validazioni di business
        Agent agent = Objects.requireNonNull(AgentMapper.fromDto(validatedDto), "mapped agent must not be null"); // Mappa il DTO in entità
        Agent toSave = Agent.forUser(agent.getUserId(), normalize(agent.getAgentCode()), normalize(agent.getTeamRole())); // Normalizza e costruisce l'entità da salvare
        Agent saved = agentRepository.save(toSave); // Persiste l'agente nel database
        return AgentMapper.toDto(saved); // Restituisce il DTO dell'entità salvata
    }

    @Transactional // Garantisce una transazione anche per l'aggiornamento
    public Optional<AgentDTO> update(Long id, AgentDTO dto) { // Aggiorna un agente esistente
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null"); // Verifica che il DTO non sia nullo
        validate(validatedDto); // Applica le validazioni sui dati in ingresso
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Recupera l'agente esistente verificando l'id
                .map(existing -> new Agent(existing.getId(), // Costruisce una nuova entità preservando l'id esistente
                        validatedDto.getUserId(), // Imposta il riferimento all'utente
                        normalize(validatedDto.getAgentCode()), // Normalizza il codice agente
                        normalize(validatedDto.getTeamRole()))) // Normalizza il ruolo nel team
                .map(agentRepository::save) // Salva l'entità aggiornata
                .map(AgentMapper::toDto); // Converte il risultato in DTO
    }

    @Transactional // La cancellazione avviene in transazione
    public boolean delete(Long id) { // Elimina un agente per id
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca l'agente e gestisce id nullo
                .map(existing -> { // Se presente esegue il blocco di cancellazione
                    agentRepository.deleteById(id); // Cancella l'entità dal database
                    return true; // Indica che la cancellazione è avvenuta
                })
                .orElse(false); // Restituisce false se l'agente non esiste
    }

    public Agent require(Long id) { // Restituisce l'agente o lancia eccezione se non trovato
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Recupera l'agente controllando l'id
                .orElseThrow(() -> new IllegalArgumentException("Agente non trovato")); // Lancia errore se l'agente manca
    }

    private void validate(AgentDTO dto) { // Valida i campi obbligatori del DTO
        if (dto.getUserId() == null) { // Controlla la presenza dell'utente associato
            throw new IllegalArgumentException("L'utente associato è obbligatorio"); // Eccezione se mancante
        }
        if (!StringUtils.hasText(dto.getAgentCode())) { // Verifica che il codice agente non sia vuoto
            throw new IllegalArgumentException("Il codice agente è obbligatorio"); // Eccezione se il codice è assente
        }
        if (!StringUtils.hasText(dto.getTeamRole())) { // Verifica che il ruolo nel team sia presente
            throw new IllegalArgumentException("Il ruolo nel team è obbligatorio"); // Eccezione se manca il ruolo
        }
    }

    private String normalize(String value) { // Rimuove spazi non necessari dalle stringhe
        return value != null ? value.trim() : null; // Trimma la stringa se non è nulla, altrimenti restituisce null
    }
}
