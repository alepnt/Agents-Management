package com.example.server.service; // Commento automatico: package com.example.server.service;
// Spazio commentato per leggibilità
import com.example.common.dto.AgentDTO; // Commento automatico: import com.example.common.dto.AgentDTO;
import com.example.server.domain.Agent; // Commento automatico: import com.example.server.domain.Agent;
import com.example.server.repository.AgentRepository; // Commento automatico: import com.example.server.repository.AgentRepository;
import com.example.server.service.mapper.AgentMapper; // Commento automatico: import com.example.server.service.mapper.AgentMapper;
import org.springframework.stereotype.Service; // Commento automatico: import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Commento automatico: import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Commento automatico: import org.springframework.util.StringUtils;
// Spazio commentato per leggibilità
import java.util.List; // Commento automatico: import java.util.List;
import java.util.Objects; // Commento automatico: import java.util.Objects;
import java.util.Optional; // Commento automatico: import java.util.Optional;
// Spazio commentato per leggibilità
@Service // Commento automatico: @Service
public class AgentService { // Commento automatico: public class AgentService {
// Spazio commentato per leggibilità
    private final AgentRepository agentRepository; // Commento automatico: private final AgentRepository agentRepository;
// Spazio commentato per leggibilità
    public AgentService(AgentRepository agentRepository) { // Commento automatico: public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository; // Commento automatico: this.agentRepository = agentRepository;
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public List<AgentDTO> findAll() { // Commento automatico: public List<AgentDTO> findAll() {
        return agentRepository.findAllByOrderByAgentCodeAsc().stream() // Commento automatico: return agentRepository.findAllByOrderByAgentCodeAsc().stream()
                .map(AgentMapper::toDto) // Commento automatico: .map(AgentMapper::toDto)
                .toList(); // Commento automatico: .toList();
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public Optional<AgentDTO> findById(Long id) { // Commento automatico: public Optional<AgentDTO> findById(Long id) {
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(AgentMapper::toDto); // Commento automatico: .map(AgentMapper::toDto);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public AgentDTO create(AgentDTO dto) { // Commento automatico: public AgentDTO create(AgentDTO dto) {
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null"); // Commento automatico: AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null");
        validate(validatedDto); // Commento automatico: validate(validatedDto);
        Agent agent = Objects.requireNonNull(AgentMapper.fromDto(validatedDto), "mapped agent must not be null"); // Commento automatico: Agent agent = Objects.requireNonNull(AgentMapper.fromDto(validatedDto), "mapped agent must not be null");
        Agent toSave = Agent.forUser(agent.getUserId(), normalize(agent.getAgentCode()), normalize(agent.getTeamRole())); // Commento automatico: Agent toSave = Agent.forUser(agent.getUserId(), normalize(agent.getAgentCode()), normalize(agent.getTeamRole()));
        Agent saved = agentRepository.save(toSave); // Commento automatico: Agent saved = agentRepository.save(toSave);
        return AgentMapper.toDto(saved); // Commento automatico: return AgentMapper.toDto(saved);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public Optional<AgentDTO> update(Long id, AgentDTO dto) { // Commento automatico: public Optional<AgentDTO> update(Long id, AgentDTO dto) {
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null"); // Commento automatico: AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null");
        validate(validatedDto); // Commento automatico: validate(validatedDto);
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> new Agent(existing.getId(), // Commento automatico: .map(existing -> new Agent(existing.getId(),
                        validatedDto.getUserId(), // Commento automatico: validatedDto.getUserId(),
                        normalize(validatedDto.getAgentCode()), // Commento automatico: normalize(validatedDto.getAgentCode()),
                        normalize(validatedDto.getTeamRole()))) // Commento automatico: normalize(validatedDto.getTeamRole())))
                .map(agentRepository::save) // Commento automatico: .map(agentRepository::save)
                .map(AgentMapper::toDto); // Commento automatico: .map(AgentMapper::toDto);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public boolean delete(Long id) { // Commento automatico: public boolean delete(Long id) {
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> { // Commento automatico: .map(existing -> {
                    agentRepository.deleteById(id); // Commento automatico: agentRepository.deleteById(id);
                    return true; // Commento automatico: return true;
                }) // Commento automatico: })
                .orElse(false); // Commento automatico: .orElse(false);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public Agent require(Long id) { // Commento automatico: public Agent require(Long id) {
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Agente non trovato")); // Commento automatico: .orElseThrow(() -> new IllegalArgumentException("Agente non trovato"));
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private void validate(AgentDTO dto) { // Commento automatico: private void validate(AgentDTO dto) {
        if (dto.getUserId() == null) { // Commento automatico: if (dto.getUserId() == null) {
            throw new IllegalArgumentException("L'utente associato è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("L'utente associato è obbligatorio");
        } // Commento automatico: }
        if (!StringUtils.hasText(dto.getAgentCode())) { // Commento automatico: if (!StringUtils.hasText(dto.getAgentCode())) {
            throw new IllegalArgumentException("Il codice agente è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il codice agente è obbligatorio");
        } // Commento automatico: }
        if (!StringUtils.hasText(dto.getTeamRole())) { // Commento automatico: if (!StringUtils.hasText(dto.getTeamRole())) {
            throw new IllegalArgumentException("Il ruolo nel team è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il ruolo nel team è obbligatorio");
        } // Commento automatico: }
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String normalize(String value) { // Commento automatico: private String normalize(String value) {
        return value != null ? value.trim() : null; // Commento automatico: return value != null ? value.trim() : null;
    } // Commento automatico: }
} // Commento automatico: }
