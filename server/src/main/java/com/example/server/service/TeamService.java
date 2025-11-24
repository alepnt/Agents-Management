package com.example.server.service; // Commento automatico: package com.example.server.service;
// Spazio commentato per leggibilità
import com.example.common.dto.TeamDTO; // Commento automatico: import com.example.common.dto.TeamDTO;
import com.example.server.domain.Team; // Commento automatico: import com.example.server.domain.Team;
import com.example.server.repository.TeamRepository; // Commento automatico: import com.example.server.repository.TeamRepository;
import com.example.server.service.mapper.TeamMapper; // Commento automatico: import com.example.server.service.mapper.TeamMapper;
import org.springframework.stereotype.Service; // Commento automatico: import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Commento automatico: import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Commento automatico: import org.springframework.util.StringUtils;
// Spazio commentato per leggibilità
import java.util.List; // Commento automatico: import java.util.List;
import java.util.Objects; // Commento automatico: import java.util.Objects;
import java.util.Optional; // Commento automatico: import java.util.Optional;
import java.util.stream.StreamSupport; // Commento automatico: import java.util.stream.StreamSupport;
// Spazio commentato per leggibilità
@Service // Commento automatico: @Service
public class TeamService { // Commento automatico: public class TeamService {
// Spazio commentato per leggibilità
    private final TeamRepository teamRepository; // Commento automatico: private final TeamRepository teamRepository;
// Spazio commentato per leggibilità
    public TeamService(TeamRepository teamRepository) { // Commento automatico: public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository; // Commento automatico: this.teamRepository = teamRepository;
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public List<TeamDTO> findAll() { // Commento automatico: public List<TeamDTO> findAll() {
        return StreamSupport.stream(teamRepository.findAll().spliterator(), false) // Commento automatico: return StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                .map(TeamMapper::toDto) // Commento automatico: .map(TeamMapper::toDto)
                .toList(); // Commento automatico: .toList();
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public Optional<TeamDTO> findById(Long id) { // Commento automatico: public Optional<TeamDTO> findById(Long id) {
        return teamRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return teamRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(TeamMapper::toDto); // Commento automatico: .map(TeamMapper::toDto);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public TeamDTO create(TeamDTO dto) { // Commento automatico: public TeamDTO create(TeamDTO dto) {
        TeamDTO validated = Objects.requireNonNull(dto, "team must not be null"); // Commento automatico: TeamDTO validated = Objects.requireNonNull(dto, "team must not be null");
        validate(validated); // Commento automatico: validate(validated);
        String normalizedName = normalize(validated.getName()); // Commento automatico: String normalizedName = normalize(validated.getName());
        ensureUniqueName(normalizedName, null); // Commento automatico: ensureUniqueName(normalizedName, null);
        Team toSave = new Team(null, normalizedName); // Commento automatico: Team toSave = new Team(null, normalizedName);
        Team saved = teamRepository.save(toSave); // Commento automatico: Team saved = teamRepository.save(toSave);
        return TeamMapper.toDto(saved); // Commento automatico: return TeamMapper.toDto(saved);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public Optional<TeamDTO> update(Long id, TeamDTO dto) { // Commento automatico: public Optional<TeamDTO> update(Long id, TeamDTO dto) {
        TeamDTO validated = Objects.requireNonNull(dto, "team must not be null"); // Commento automatico: TeamDTO validated = Objects.requireNonNull(dto, "team must not be null");
        validate(validated); // Commento automatico: validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Commento automatico: Long requiredId = Objects.requireNonNull(id, "id must not be null");
        String normalizedName = normalize(validated.getName()); // Commento automatico: String normalizedName = normalize(validated.getName());
        return teamRepository.findById(requiredId) // Commento automatico: return teamRepository.findById(requiredId)
                .map(existing -> { // Commento automatico: .map(existing -> {
                    ensureUniqueName(normalizedName, requiredId); // Commento automatico: ensureUniqueName(normalizedName, requiredId);
                    Team toSave = new Team(existing.getId(), normalizedName); // Commento automatico: Team toSave = new Team(existing.getId(), normalizedName);
                    return TeamMapper.toDto(teamRepository.save(toSave)); // Commento automatico: return TeamMapper.toDto(teamRepository.save(toSave));
                }); // Commento automatico: });
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public boolean delete(Long id) { // Commento automatico: public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Commento automatico: Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return teamRepository.findById(requiredId) // Commento automatico: return teamRepository.findById(requiredId)
                .map(existing -> { // Commento automatico: .map(existing -> {
                    Team nonNullExisting = Objects.requireNonNull(existing, "team must not be null"); // Commento automatico: Team nonNullExisting = Objects.requireNonNull(existing, "team must not be null");
                    teamRepository.delete(nonNullExisting); // Commento automatico: teamRepository.delete(nonNullExisting);
                    return true; // Commento automatico: return true;
                }) // Commento automatico: })
                .orElse(false); // Commento automatico: .orElse(false);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private void validate(TeamDTO dto) { // Commento automatico: private void validate(TeamDTO dto) {
        if (!StringUtils.hasText(dto.getName())) { // Commento automatico: if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il nome del team è obbligatorio");
        } // Commento automatico: }
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private void ensureUniqueName(String name, Long currentId) { // Commento automatico: private void ensureUniqueName(String name, Long currentId) {
        teamRepository.findByName(name) // Commento automatico: teamRepository.findByName(name)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId)) // Commento automatico: .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> { // Commento automatico: .ifPresent(existing -> {
                    throw new IllegalArgumentException("Esiste già un team con questo nome"); // Commento automatico: throw new IllegalArgumentException("Esiste già un team con questo nome");
                }); // Commento automatico: });
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String normalize(String value) { // Commento automatico: private String normalize(String value) {
        return value != null ? value.trim() : null; // Commento automatico: return value != null ? value.trim() : null;
    } // Commento automatico: }
} // Commento automatico: }
