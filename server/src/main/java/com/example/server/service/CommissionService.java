package com.example.server.service; // Definisce il package del servizio commissioni

import com.example.common.dto.CommissionDTO; // Importa il DTO per esporre le commissioni
import com.example.server.domain.Agent; // Importa l'entità di dominio Agent
import com.example.server.domain.Commission; // Importa l'entità di dominio Commission
import com.example.server.domain.Contract; // Importa l'entità di dominio Contract
import com.example.server.domain.User; // Importa l'entità di dominio User
import com.example.server.repository.AgentRepository; // Importa il repository degli agenti
import com.example.server.repository.CommissionRepository; // Importa il repository delle commissioni
import com.example.server.repository.ContractRepository; // Importa il repository dei contratti
import com.example.server.repository.UserRepository; // Importa il repository degli utenti
import com.example.server.service.mapper.CommissionMapper; // Importa il mapper tra Commission e CommissionDTO
import org.springframework.stereotype.Service; // Importa l'annotazione di servizio Spring

import java.math.BigDecimal; // Importa BigDecimal per i calcoli monetari
import java.math.MathContext; // Importa MathContext per definire la precisione
import java.math.RoundingMode; // Importa RoundingMode per l'arrotondamento
import java.time.Clock; // Importa Clock per recuperare l'orario
import java.time.Instant; // Importa Instant per gestire i timestamp
import java.util.ArrayList; // Importa ArrayList per collezioni mutabili
import java.util.Comparator; // Importa Comparator per ordinare elementi
import java.util.LinkedHashMap; // Importa LinkedHashMap per mantenere l'ordine di inserimento
import java.util.List; // Importa List per collezionare risultati
import java.util.Locale; // Importa Locale per normalizzare stringhe
import java.util.Map; // Importa Map per le allocazioni
import java.util.Objects; // Importa utilità per verifiche di nullità
import java.util.Optional; // Importa Optional per valori facoltativi
import java.util.stream.StreamSupport; // Importa StreamSupport per iterare sulle collezioni

@Service // Indica che la classe è un servizio Spring
public class CommissionService { // Gestisce la logica di calcolo e persistenza delle commissioni

    private static final BigDecimal MIN_TEAM_RATE = new BigDecimal("0.10"); // Aliquota minima di team
    private static final BigDecimal MAX_TEAM_RATE = new BigDecimal("0.12"); // Aliquota massima di team
    private static final BigDecimal SENIOR_RATE = new BigDecimal("0.03"); // Aliquota per ruoli senior
    private static final BigDecimal JUNIOR_RATE = new BigDecimal("0.02"); // Aliquota per ruoli junior
    private static final BigDecimal INTERN_RATE = new BigDecimal("0.015"); // Aliquota per stagisti
    private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.01"); // Aliquota di default
    private static final MathContext MATH_CONTEXT = new MathContext(8, RoundingMode.HALF_UP); // Precisione per i calcoli di scala

    private final CommissionRepository commissionRepository; // Repository per persistere le commissioni
    private final ContractRepository contractRepository; // Repository per i contratti
    private final AgentRepository agentRepository; // Repository per gli agenti
    private final UserRepository userRepository; // Repository per gli utenti
    private final Clock clock; // Orologio per determinare i timestamp

    public CommissionService(CommissionRepository commissionRepository, // Costruttore con dependency injection del repository commissioni
                             ContractRepository contractRepository, // Repository dei contratti iniettato
                             AgentRepository agentRepository, // Repository degli agenti iniettato
                             UserRepository userRepository, // Repository degli utenti iniettato
                             Clock clock) { // Clock iniettato per controllare il tempo
        this.commissionRepository = commissionRepository; // Assegna il repository delle commissioni
        this.contractRepository = contractRepository; // Assegna il repository dei contratti
        this.agentRepository = agentRepository; // Assegna il repository degli agenti
        this.userRepository = userRepository; // Assegna il repository degli utenti
        this.clock = clock; // Assegna l'orologio
    }

    public List<CommissionDTO> findAll() { // Restituisce tutte le commissioni presenti
        return StreamSupport.stream(commissionRepository.findAll().spliterator(), false) // Converte gli elementi iterabili in stream
                .map(CommissionMapper::toDto) // Converte ogni commissione in DTO
                .toList(); // Colleziona i DTO in una lista
    }

    public Optional<CommissionDTO> findById(Long id) { // Recupera una commissione per id
        return commissionRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica che l'id non sia nullo e cerca la commissione
                .map(CommissionMapper::toDto); // Converte l'entità trovata in DTO
    }

    public CommissionDTO create(CommissionDTO commissionDTO) { // Crea una nuova commissione
        Commission source = Objects.requireNonNull(CommissionMapper.fromDto( // Mappa il DTO in entità
                Objects.requireNonNull(commissionDTO, "commission must not be null")), // Verifica che il DTO non sia nullo
                "mapped commission must not be null"); // Messaggio se la mappatura restituisce null
        Commission toSave = new Commission( // Costruisce l'entità da salvare
                null, // Imposta l'id a null per generarlo
                Objects.requireNonNull(source.getAgentId(), "agentId must not be null"), // Verifica che l'agente sia presente
                Objects.requireNonNull(source.getContractId(), "contractId must not be null"), // Verifica che il contratto sia presente
                defaultAmount(source.getTotalCommission(), BigDecimal.ZERO), // Imposta la commissione totale con valore di fallback
                defaultAmount(source.getPaidCommission(), BigDecimal.ZERO), // Imposta la commissione pagata con valore di fallback
                defaultAmount(source.getPendingCommission(), source.getTotalCommission()), // Imposta la commissione pendente con fallback
                Instant.now(clock) // Timestamp di creazione
        );
        return CommissionMapper.toDto(commissionRepository.save(toSave)); // Salva l'entità e restituisce il DTO
    }

    public Optional<CommissionDTO> update(Long id, CommissionDTO commissionDTO) { // Aggiorna una commissione esistente
        Commission source = Objects.requireNonNull(CommissionMapper.fromDto( // Mappa il DTO in entità
                Objects.requireNonNull(commissionDTO, "commission must not be null")), // Verifica che il DTO non sia nullo
                "mapped commission must not be null"); // Messaggio se la mappatura restituisce null
        return commissionRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Recupera l'entità esistente
                .map(existing -> new Commission( // Costruisce una nuova istanza con i dati aggiornati
                        existing.getId(), // Mantiene l'id originale
                        defaultValue(source.getAgentId(), existing.getAgentId()), // Usa l'agente fornito o quello esistente
                        defaultValue(source.getContractId(), existing.getContractId()), // Usa il contratto fornito o quello esistente
                        defaultAmount(source.getTotalCommission(), existing.getTotalCommission()), // Aggiorna la commissione totale
                        defaultAmount(source.getPaidCommission(), existing.getPaidCommission()), // Aggiorna la commissione pagata
                        defaultAmount(source.getPendingCommission(), existing.getPendingCommission()), // Aggiorna la commissione pendente
                        Instant.now(clock) // Aggiorna il timestamp
                ))
                .map(commissionRepository::save) // Salva l'entità aggiornata
                .map(CommissionMapper::toDto); // Converte l'entità salvata in DTO
    }

    public boolean delete(Long id) { // Elimina una commissione
        return commissionRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Verifica l'id e cerca la commissione
                .map(existing -> { // Se presente
                    commissionRepository.deleteById(Objects.requireNonNull(existing.getId(), "commission id must not be null")); // Cancella la commissione
                    return true; // Indica che la cancellazione è avvenuta
                })
                .orElse(false); // Restituisce false se non trovata
    }

    public List<Commission> updateAfterPayment(Long contractId, BigDecimal invoiceAmount, BigDecimal amountPaid) { // Aggiorna le commissioni dopo un pagamento
        if (contractId == null) { // Se il contratto non è indicato
            return List.of(); // Non ci sono commissioni da aggiornare
        }

        Optional<Contract> contract = contractRepository.findById(contractId); // Recupera il contratto associato
        if (contract.isEmpty()) { // Se il contratto non esiste
            return List.of(); // Nessun aggiornamento viene eseguito
        }

        Long contractAgentId = contract.get().getAgentId(); // Recupera l'agente principale del contratto
        TeamCommissionRule rule = resolveRuleForAgent(contractAgentId); // Determina le regole di ripartizione
        Map<Long, BigDecimal> totalAllocations = allocationForAmount(invoiceAmount, rule); // Calcola la distribuzione sul totale
        Map<Long, BigDecimal> paidAllocations = allocationForAmount(amountPaid, rule); // Calcola la distribuzione sull'importo pagato

        List<Commission> updated = new ArrayList<>(); // Collezione per le commissioni aggiornate
        for (AgentCommissionShare share : orderedShares(rule)) { // Itera le quote in ordine
            Long agentId = share.agentId(); // Identificativo dell'agente beneficiario
            Commission base = commissionRepository // Recupera la commissione esistente per agente e contratto
                    .findByAgentIdAndContractId(agentId, contractId)
                    .orElseGet(() -> Commission.create(agentId, contractId, BigDecimal.ZERO)); // Crea una commissione vuota se assente

            BigDecimal totalCommission = base.getTotalCommission().add(totalAllocations.getOrDefault(agentId, BigDecimal.ZERO)); // Calcola la nuova commissione totale
            BigDecimal paidCommission = base.getPaidCommission().add(paidAllocations.getOrDefault(agentId, BigDecimal.ZERO)); // Calcola la nuova commissione pagata
            BigDecimal pendingCommission = totalCommission.subtract(paidCommission); // Calcola la commissione pendente
            if (pendingCommission.signum() < 0) { // Evita valori negativi
                pendingCommission = BigDecimal.ZERO; // Imposta a zero se sotto zero
            }

            Commission updatedCommission = base.update(totalCommission, paidCommission, pendingCommission, Instant.now(clock)); // Aggiorna l'entità con i nuovi valori
            updated.add(commissionRepository.save(updatedCommission)); // Salva e aggiunge alla lista dei risultati
        }
        return updated; // Restituisce le commissioni aggiornate
    }

    public BigDecimal computeCommission(BigDecimal amount) { // Calcola la commissione base per un importo
        return applyDefaultCommissionRate(amount); // Applica l'aliquota predefinita
    }

    public BigDecimal contractAgentCommission(Long contractId, BigDecimal amount) { // Calcola la commissione per l'agente del contratto
        if (contractId == null) { // Se il contratto non è specificato
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Restituisce zero formattato
        }
        Optional<Contract> contract = contractRepository.findById(contractId); // Cerca il contratto
        if (contract.isEmpty()) { // Se non trovato
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Restituisce zero
        }
        Long agentId = contract.get().getAgentId(); // Recupera l'agente del contratto
        TeamCommissionRule rule = resolveRuleForAgent(agentId); // Determina la regola di ripartizione
        Map<Long, BigDecimal> allocations = allocationForAmount(amount, rule); // Calcola le allocazioni
        return allocations.getOrDefault(agentId, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP); // Restituisce la quota dell'agente formattata
    }

    public BigDecimal calculateAgentCommission(Long teamId, Long agentId, BigDecimal amount) { // Calcola la quota di commissione per un agente
        if (agentId == null) { // Se l'agente non è indicato
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Restituisce zero
        }
        TeamCommissionRule rule = resolveRuleForTeam(teamId); // Recupera la regola per il team
        Map<Long, BigDecimal> allocations = allocationForAmount(amount, rule); // Calcola le allocazioni per l'importo
        return allocations.getOrDefault(agentId, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP); // Restituisce la quota per l'agente
    }

    public BigDecimal calculateTeamCommission(Long teamId, BigDecimal amount) { // Calcola la commissione complessiva di un team
        TeamCommissionRule rule = resolveRuleForTeam(teamId); // Recupera la regola per il team specificato
        if (amount == null) { // Se non viene fornito un importo
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Restituisce zero
        }
        BigDecimal rate = rule.teamCommissionRate(); // Determina l'aliquota di team
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP); // Calcola e restituisce l'importo commissionale
    }

    public BigDecimal applyDefaultCommissionRate(BigDecimal amount) { // Applica l'aliquota predefinita a un importo
        if (amount == null) { // Se l'importo è nullo
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Restituisce zero
        }
        return amount.multiply(MIN_TEAM_RATE).setScale(2, RoundingMode.HALF_UP); // Calcola la commissione minima e arrotonda
    }

    private Map<Long, BigDecimal> allocationForAmount(BigDecimal amount, TeamCommissionRule rule) { // Calcola le allocazioni per un importo dato
        if (amount == null || rule == null || rule.shares().isEmpty()) { // Se mancano dati o non ci sono quote
            return Map.of(); // Restituisce una mappa vuota
        }

        Map<Long, BigDecimal> allocations = new LinkedHashMap<>(); // Mantiene le allocazioni nell'ordine di calcolo
        List<AgentCommissionShare> ordered = orderedShares(rule); // Recupera le quote ordinate
        if (rule.distributionStrategy() == DistributionStrategy.BARRIER) { // Gestisce la strategia a barriera
            BigDecimal remainingRate = rule.teamCommissionRate(); // Aliquota disponibile residua
            for (AgentCommissionShare share : ordered) { // Itera le quote in ordine
                BigDecimal allocatedRate = share.percentage().min(remainingRate); // Assegna il minimo tra quota e residuo
                if (allocatedRate.signum() < 0) { // Evita percentuali negative
                    allocatedRate = BigDecimal.ZERO; // Imposta a zero se negativa
                }
                allocations.put(share.agentId(), amount.multiply(allocatedRate)); // Calcola l'importo allocato per l'agente
                remainingRate = remainingRate.subtract(allocatedRate); // Aggiorna il residuo
            }
        } else { // Strategia di distribuzione proporzionale
            BigDecimal totalShare = ordered.stream() // Somma tutte le percentuali richieste
                    .map(AgentCommissionShare::percentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalShare.signum() == 0) { // Se la somma è zero
                ordered.forEach(share -> allocations.put(share.agentId(), BigDecimal.ZERO)); // Assegna zero a tutti
                return allocations; // Ritorna la mappa
            }
            BigDecimal scaling = rule.teamCommissionRate().divide(totalShare, MATH_CONTEXT); // Calcola il fattore di scala
            for (AgentCommissionShare share : ordered) { // Applica la scala a ogni quota
                BigDecimal allocatedRate = share.percentage().multiply(scaling); // Calcola la quota proporzionale
                allocations.put(share.agentId(), amount.multiply(allocatedRate)); // Memorizza l'importo allocato
            }
        }
        return allocations; // Restituisce le allocazioni calcolate
    }

    private List<AgentCommissionShare> orderedShares(TeamCommissionRule rule) { // Ordina le quote di commissione del team
        if (rule == null) { // Se la regola manca
            return List.of(); // Restituisce una lista vuota
        }
        return rule.shares().stream() // Stream delle quote
                .sorted(Comparator.comparingInt(AgentCommissionShare::ranking).thenComparing(AgentCommissionShare::agentId)) // Ordina per ranking e id agente
                .toList(); // Restituisce la lista ordinata
    }

    private <T> T defaultValue(T value, T fallback) { // Restituisce value se presente altrimenti fallback
        return value != null ? value : fallback; // Operatore ternario per gestire i valori di default
    }

    private BigDecimal defaultAmount(BigDecimal value, BigDecimal fallback) { // Restituisce un importo predefinito se nullo
        if (value != null) { // Se è presente
            return value; // Ritorna il valore
        }
        if (fallback != null) { // Se è definito un fallback
            return fallback; // Ritorna il fallback
        }
        return BigDecimal.ZERO; // Ultima risorsa, restituisce zero
    }

    private TeamCommissionRule resolveRuleForAgent(Long agentId) { // Determina la regola di commissione per un agente specifico
        if (agentId == null) { // Se l'agente non è definito
            return null; // Non esiste alcuna regola
        }
        Optional<Agent> agent = agentRepository.findById(agentId); // Recupera l'agente se presente
        if (agent.isEmpty()) { // Se non trovato
            return TeamCommissionRule.singleAgent(agentId, MIN_TEAM_RATE); // Usa una regola basata su un solo agente
        }
        Long userId = Objects.requireNonNull(agent.get().getUserId(), "userId must not be null"); // Recupera l'utente associato
        Optional<User> user = userRepository.findById(userId); // Cerca l'utente
        if (user.isEmpty() || user.get().getTeamId() == null) { // Se l'utente non ha team
            return TeamCommissionRule.singleAgent(agentId, MIN_TEAM_RATE); // Applica la regola minima
        }
        TeamCommissionRule teamRule = resolveRuleForTeam(user.get().getTeamId()); // Recupera la regola del team
        boolean containsAgent = teamRule.shares().stream() // Controlla se l'agente è già presente nella regola
                .anyMatch(share -> share.agentId().equals(agentId));
        if (!containsAgent) { // Se manca
            List<AgentCommissionShare> enriched = new ArrayList<>(teamRule.shares()); // Copia le quote esistenti
            enriched.add(new AgentCommissionShare(agentId, // Aggiunge la quota per l'agente mancante
                    percentageForRole(agent.get().getTeamRole()), // Determina la percentuale in base al ruolo
                    rankingForRole(agent.get().getTeamRole()))); // Determina il ranking in base al ruolo
            return new TeamCommissionRule(teamRule.teamId(), teamRule.teamCommissionRate(), // Costruisce una nuova regola
                    teamRule.distributionStrategy(), List.copyOf(enriched)); // Mantiene strategia e aggiunge la nuova quota
        }
        return teamRule; // Se già presente restituisce la regola originale
    }

    private TeamCommissionRule resolveRuleForTeam(Long teamId) { // Determina la regola di commissione per un team
        if (teamId == null) { // Se il team non è definito
            return new TeamCommissionRule(null, MIN_TEAM_RATE, DistributionStrategy.PERCENTAGE, List.of()); // Restituisce la regola minima senza membri
        }
        List<User> teamMembers = userRepository.findByTeamId(teamId); // Recupera gli utenti del team
        List<AgentCommissionShare> shares = new ArrayList<>(); // Collezione per le quote di commissione
        for (User member : teamMembers) { // Itera sui membri del team
            agentRepository.findByUserId(member.getId()).ifPresent(agent -> // Se l'utente è anche agente
                    shares.add(new AgentCommissionShare(agent.getId(), // Aggiunge la quota per l'agente
                            percentageForRole(agent.getTeamRole()), // Percentuale basata sul ruolo
                            rankingForRole(agent.getTeamRole())))); // Ranking basato sul ruolo
        }
        BigDecimal totalRequested = shares.stream() // Somma le percentuali richieste
                .map(AgentCommissionShare::percentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal teamRate = normalizeTeamRate(totalRequested); // Normalizza l'aliquota del team
        DistributionStrategy strategy = totalRequested.compareTo(teamRate) > 0 // Decide la strategia in base alla somma richiesta
                ? DistributionStrategy.BARRIER
                : DistributionStrategy.PERCENTAGE;
        return new TeamCommissionRule(teamId, teamRate, strategy, List.copyOf(shares)); // Crea la regola finale per il team
    }

    private BigDecimal normalizeTeamRate(BigDecimal desired) { // Normalizza l'aliquota del team entro i limiti
        if (desired == null) { // Se non è specificata
            return MIN_TEAM_RATE; // Usa l'aliquota minima
        }
        BigDecimal capped = desired.min(MAX_TEAM_RATE); // Limita l'aliquota al massimo consentito
        if (capped.compareTo(MIN_TEAM_RATE) < 0) { // Se inferiore al minimo
            return MIN_TEAM_RATE; // Usa il minimo
        }
        return capped; // Restituisce l'aliquota normalizzata
    }

    private BigDecimal percentageForRole(String role) { // Determina la percentuale di commissione in base al ruolo
        if (role == null) { // Se il ruolo non è specificato
            return DEFAULT_RATE; // Usa l'aliquota di default
        }
        String normalized = role.toLowerCase(Locale.ITALY); // Normalizza il ruolo in minuscolo
        if (normalized.contains("senior")) { // Controlla ruolo senior
            return SENIOR_RATE; // Restituisce aliquota senior
        }
        if (normalized.contains("junior")) { // Controlla ruolo junior
            return JUNIOR_RATE; // Restituisce aliquota junior
        }
        if (normalized.contains("stag")) { // Controlla ruolo stagista
            return INTERN_RATE; // Restituisce aliquota stagista
        }
        return DEFAULT_RATE; // In assenza di corrispondenza usa l'aliquota di default
    }

    private int rankingForRole(String role) { // Determina la priorità di distribuzione in base al ruolo
        if (role == null) { // Se non è specificato
            return 3; // Posiziona alla fine
        }
        String normalized = role.toLowerCase(Locale.ITALY); // Normalizza il ruolo in minuscolo
        if (normalized.contains("senior")) { // Ruolo senior
            return 0; // Priorità più alta
        }
        if (normalized.contains("junior")) { // Ruolo junior
            return 1; // Seconda priorità
        }
        if (normalized.contains("stag")) { // Ruolo stagista
            return 2; // Terza priorità
        }
        return 3; // Default per altri ruoli
    }
}
