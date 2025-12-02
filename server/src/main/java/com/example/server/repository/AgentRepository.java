package com.example.server.repository; // Definisce il package in cui si trova l'interfaccia del repository degli agenti.

import com.example.server.domain.Agent; // Importa l'entità Agent utilizzata dal repository.
import org.springframework.data.repository.CrudRepository; // Fornisce le operazioni CRUD di base.
import org.springframework.stereotype.Repository; // Indica a Spring che questa interfaccia è un componente repository.

import java.util.List; // Permette di restituire liste di agenti.
import java.util.Optional; // Gestisce risultati opzionali per le ricerche.

@Repository // Marca il repository per l'individuazione automatica da parte di Spring.
public interface AgentRepository extends CrudRepository<Agent, Long> { // Estende CrudRepository per operazioni su Agent con ID Long.

    List<Agent> findAllByOrderByAgentCodeAsc(); // Recupera tutti gli agenti ordinati per codice in ordine crescente.

    Optional<Agent> findByUserId(Long userId); // Trova un agente associato all'ID utente specificato.

    Optional<Agent> findByAgentCode(String agentCode); // Trova un agente a partire dal suo codice identificativo.

    Optional<Agent> findTopByAgentCodeNotNullOrderByAgentCodeDesc(); // Recupera l'ultimo codice agente assegnato.
}
