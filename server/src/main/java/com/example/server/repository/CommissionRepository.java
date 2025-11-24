package com.example.server.repository; // Package che contiene i repository Spring della logica server.

import com.example.server.domain.Commission; // Importa l'entità Commission gestita da questo repository.
import org.springframework.data.repository.CrudRepository; // Interfaccia Spring Data per operazioni CRUD generiche.
import org.springframework.stereotype.Repository; // Stereotipo che registra il componente come repository.

import java.util.List; // Fornisce il tipo di ritorno per elenchi di commissioni.
import java.util.Optional; // Permette di rappresentare risultati non obbligatori.

@Repository // Consente a Spring di individuare automaticamente l'interfaccia come repository.
public interface CommissionRepository extends CrudRepository<Commission, Long> { // Estende CrudRepository per gestire Commission con ID Long.

    Optional<Commission> findByAgentIdAndContractId(Long agentId, Long contractId); // Recupera la commissione per coppia agente-contratto.

    List<Commission> findByAgentId(Long agentId); // Elenca tutte le commissioni associate a uno specifico agente.
}
