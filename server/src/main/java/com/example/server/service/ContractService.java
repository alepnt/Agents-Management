package com.example.server.service; // Definisce il package in cui si trova il servizio dei contratti.

import com.example.common.dto.ContractDTO; // Importa il DTO dei contratti scambiato con il client.
import com.example.common.dto.DocumentHistoryDTO; // Importa il DTO dello storico documentale.
import com.example.common.enums.DocumentAction; // Importa l'enum che descrive l'azione effettuata sul documento.
import com.example.common.enums.DocumentType; // Importa l'enum che identifica il tipo di documento gestito.
import com.example.server.domain.Contract; // Importa l'entità di dominio dei contratti.
import com.example.server.repository.ContractRepository; // Importa il repository per la persistenza dei contratti.
import com.example.server.service.mapper.ContractMapper; // Importa il mapper tra entità Contract e ContractDTO.
import com.example.server.service.mapper.DocumentHistoryMapper; // Importa il mapper per convertire lo storico documentale in DTO.
import org.springframework.stereotype.Service; // Importa l'annotazione Service di Spring.

import java.util.List; // Importa la struttura dati List.
import java.util.Objects; // Importa la classe Objects per validazioni di null.
import java.util.Optional; // Importa Optional per gestire risultati opzionali.
import java.util.stream.Collectors; // Importa Collectors per trasformare stream in liste.

@Service // Indica che questa classe è un componente di servizio Spring.
public class ContractService { // Gestisce le operazioni di business sui contratti.

    private final ContractRepository contractRepository; // Repository per accedere ai dati dei contratti.
    private final DocumentHistoryService documentHistoryService; // Servizio per registrare lo storico dei documenti.

    public ContractService(ContractRepository contractRepository, DocumentHistoryService documentHistoryService) { // Costruttore che riceve le dipendenze necessarie.
        this.contractRepository = contractRepository; // Inizializza il repository dei contratti.
        this.documentHistoryService = documentHistoryService; // Inizializza il servizio per lo storico documentale.
    }

    public List<ContractDTO> findAll() { // Restituisce tutti i contratti ordinati per data di inizio decrescente.
        return contractRepository.findAllByOrderByStartDateDesc().stream() // Recupera i contratti e li trasforma in stream.
                .map(ContractMapper::toDto) // Converte ogni entità Contract in ContractDTO.
                .toList(); // Colleziona il risultato in una lista.
    }

    public Optional<ContractDTO> findById(Long id) { // Recupera un contratto per id e lo mappa in DTO se presente.
        return contractRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca per id dopo averlo validato.
                .map(ContractMapper::toDto); // Converte l'entità in DTO se trovata.
    }

    public ContractDTO create(ContractDTO dto) { // Crea un nuovo contratto e registra l'azione nello storico.
        ContractDTO requiredDto = Objects.requireNonNull(dto, "contract must not be null"); // Verifica che il DTO non sia null.
        Contract contract = Objects.requireNonNull(ContractMapper.fromDto(requiredDto), // Converte il DTO in entità Contract.
                "mapped contract must not be null"); // Garantisce che la conversione non restituisca null.
        Contract saved = contractRepository.save(contract); // Salva il contratto nel database.
        documentHistoryService.log(DocumentType.CONTRACT, // Registra l'operazione nello storico.
                Objects.requireNonNull(saved.getId(), "contract id must not be null"), // Usa l'id del contratto salvato, assicurandosi che non sia null.
                DocumentAction.CREATED, // Specifica che l'azione è una creazione.
                "Contratto creato: " + saved.getDescription()); // Aggiunge una descrizione dettagliata.
        return ContractMapper.toDto(saved); // Restituisce il contratto salvato come DTO.
    }

    public Optional<ContractDTO> update(Long id, ContractDTO dto) { // Aggiorna un contratto esistente se trovato.
        ContractDTO requiredDto = Objects.requireNonNull(dto, "contract must not be null"); // Verifica che il DTO passato non sia null.
        return contractRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Recupera il contratto da aggiornare.
                .map(existing -> Objects.requireNonNull(existing.updateFrom(Objects.requireNonNull( // Aggiorna i campi dell'entità esistente.
                        ContractMapper.fromDto(requiredDto), "mapped contract must not be null")), // Converte e valida il DTO.
                        "updated contract must not be null")) // Garantisce che l'oggetto aggiornato non sia null.
                .map(contractRepository::save) // Salva l'entità aggiornata nel database.
                .map(saved -> { // Converte il risultato e registra lo storico.
                    documentHistoryService.log(DocumentType.CONTRACT, // Registra l'aggiornamento nel log.
                            Objects.requireNonNull(saved.getId(), "contract id must not be null"), // Valida l'id del contratto salvato.
                            DocumentAction.UPDATED, // Indica che l'azione è un aggiornamento.
                            "Contratto aggiornato"); // Messaggio descrittivo dell'operazione.
                    return ContractMapper.toDto(saved); // Restituisce il contratto aggiornato come DTO.
                });
    }

    public boolean delete(Long id) { // Elimina un contratto se esiste e registra l'operazione.
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Valida l'id richiesto.
        return contractRepository.findById(requiredId) // Cerca il contratto da eliminare.
                .map(contract -> { // Se presente, procede con la cancellazione.
                    contractRepository.deleteById(requiredId); // Elimina il contratto per id.
                    documentHistoryService.log(DocumentType.CONTRACT, // Registra la cancellazione nello storico.
                            Objects.requireNonNull(contract.getId(), "contract id must not be null"), // Assicura che l'id non sia null.
                            DocumentAction.DELETED, // Specifica che l'azione è una eliminazione.
                            "Contratto eliminato"); // Messaggio descrittivo.
                    return true; // Indica che la cancellazione è avvenuta.
                })
                .orElse(false); // Restituisce false se il contratto non è stato trovato.
    }

    public List<DocumentHistoryDTO> history(Long id) { // Recupera lo storico del contratto indicato.
        return documentHistoryService.list(DocumentType.CONTRACT, Objects.requireNonNull(id, "id must not be null")).stream() // Ottiene le voci di storico per il contratto richiesto.
                .map(DocumentHistoryMapper::toDto) // Converte le entità di storico in DTO.
                .collect(Collectors.toList()); // Colleziona i risultati in una lista.
    }
}
