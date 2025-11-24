package com.example.server.service; // Commento automatico: package com.example.server.service;
// Spazio commentato per leggibilità
import com.example.common.dto.ArticleDTO; // Commento automatico: import com.example.common.dto.ArticleDTO;
import com.example.server.domain.Article; // Commento automatico: import com.example.server.domain.Article;
import com.example.server.repository.ArticleRepository; // Commento automatico: import com.example.server.repository.ArticleRepository;
import com.example.server.service.mapper.ArticleMapper; // Commento automatico: import com.example.server.service.mapper.ArticleMapper;
import org.springframework.stereotype.Service; // Commento automatico: import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Commento automatico: import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Commento automatico: import org.springframework.util.StringUtils;
// Spazio commentato per leggibilità
import java.math.BigDecimal; // Commento automatico: import java.math.BigDecimal;
import java.util.List; // Commento automatico: import java.util.List;
import java.util.Objects; // Commento automatico: import java.util.Objects;
import java.util.Optional; // Commento automatico: import java.util.Optional;
// Spazio commentato per leggibilità
@Service // Commento automatico: @Service
public class ArticleService { // Commento automatico: public class ArticleService {
// Spazio commentato per leggibilità
    private final ArticleRepository articleRepository; // Commento automatico: private final ArticleRepository articleRepository;
// Spazio commentato per leggibilità
    public ArticleService(ArticleRepository articleRepository) { // Commento automatico: public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository; // Commento automatico: this.articleRepository = articleRepository;
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public List<ArticleDTO> findAll() { // Commento automatico: public List<ArticleDTO> findAll() {
        return articleRepository.findAllByOrderByNameAsc().stream() // Commento automatico: return articleRepository.findAllByOrderByNameAsc().stream()
                .map(ArticleMapper::toDto) // Commento automatico: .map(ArticleMapper::toDto)
                .toList(); // Commento automatico: .toList();
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public Optional<ArticleDTO> findById(Long id) { // Commento automatico: public Optional<ArticleDTO> findById(Long id) {
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(ArticleMapper::toDto); // Commento automatico: .map(ArticleMapper::toDto);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public ArticleDTO create(ArticleDTO dto) { // Commento automatico: public ArticleDTO create(ArticleDTO dto) {
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null"); // Commento automatico: ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null");
        validate(validatedDto); // Commento automatico: validate(validatedDto);
        Article source = Objects.requireNonNull(ArticleMapper.fromDto(validatedDto), // Commento automatico: Article source = Objects.requireNonNull(ArticleMapper.fromDto(validatedDto),
                "mapped article must not be null"); // Commento automatico: "mapped article must not be null");
        Article toSave = Objects.requireNonNull(Article.create( // Commento automatico: Article toSave = Objects.requireNonNull(Article.create(
                normalize(source.getCode()), // Commento automatico: normalize(source.getCode()),
                normalize(source.getName()), // Commento automatico: normalize(source.getName()),
                normalize(source.getDescription()), // Commento automatico: normalize(source.getDescription()),
                normalizePrice(source.getUnitPrice()), // Commento automatico: normalizePrice(source.getUnitPrice()),
                source.getVatRate(), // Commento automatico: source.getVatRate(),
                normalize(source.getUnitOfMeasure()) // Commento automatico: normalize(source.getUnitOfMeasure())
        ), "created article must not be null"); // Commento automatico: ), "created article must not be null");
        Article saved = articleRepository.save(toSave); // Commento automatico: Article saved = articleRepository.save(toSave);
        return ArticleMapper.toDto(saved); // Commento automatico: return ArticleMapper.toDto(saved);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public Optional<ArticleDTO> update(Long id, ArticleDTO dto) { // Commento automatico: public Optional<ArticleDTO> update(Long id, ArticleDTO dto) {
        ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null"); // Commento automatico: ArticleDTO validatedDto = Objects.requireNonNull(dto, "article must not be null");
        validate(validatedDto); // Commento automatico: validate(validatedDto);
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> { // Commento automatico: .map(existing -> {
                    Article updateSource = Objects.requireNonNull(Article.create( // Commento automatico: Article updateSource = Objects.requireNonNull(Article.create(
                            normalize(validatedDto.getCode()), // Commento automatico: normalize(validatedDto.getCode()),
                            normalize(validatedDto.getName()), // Commento automatico: normalize(validatedDto.getName()),
                            normalize(validatedDto.getDescription()), // Commento automatico: normalize(validatedDto.getDescription()),
                            normalizePrice(validatedDto.getUnitPrice()), // Commento automatico: normalizePrice(validatedDto.getUnitPrice()),
                            validatedDto.getVatRate(), // Commento automatico: validatedDto.getVatRate(),
                            normalize(validatedDto.getUnitOfMeasure()) // Commento automatico: normalize(validatedDto.getUnitOfMeasure())
                    ), "created article must not be null"); // Commento automatico: ), "created article must not be null");
                    Article updated = Objects.requireNonNull(existing.updateFrom(updateSource), // Commento automatico: Article updated = Objects.requireNonNull(existing.updateFrom(updateSource),
                            "updated article must not be null"); // Commento automatico: "updated article must not be null");
                    Article saved = articleRepository.save(updated); // Commento automatico: Article saved = articleRepository.save(updated);
                    return ArticleMapper.toDto(saved); // Commento automatico: return ArticleMapper.toDto(saved);
                }); // Commento automatico: });
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public boolean delete(Long id) { // Commento automatico: public boolean delete(Long id) {
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> { // Commento automatico: .map(existing -> {
                    articleRepository.deleteById(id); // Commento automatico: articleRepository.deleteById(id);
                    return true; // Commento automatico: return true;
                }) // Commento automatico: })
                .orElse(false); // Commento automatico: .orElse(false);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public Article require(Long id) { // Commento automatico: public Article require(Long id) {
        return articleRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return articleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato")); // Commento automatico: .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato"));
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private void validate(ArticleDTO dto) { // Commento automatico: private void validate(ArticleDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getName())) { // Commento automatico: if (dto == null || !StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Il nome dell'articolo è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il nome dell'articolo è obbligatorio");
        } // Commento automatico: }
        BigDecimal price = dto.getUnitPrice(); // Commento automatico: BigDecimal price = dto.getUnitPrice();
        if (price != null && price.signum() < 0) { // Commento automatico: if (price != null && price.signum() < 0) {
            throw new IllegalArgumentException("Il prezzo unitario non può essere negativo"); // Commento automatico: throw new IllegalArgumentException("Il prezzo unitario non può essere negativo");
        } // Commento automatico: }
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String normalize(String value) { // Commento automatico: private String normalize(String value) {
        return value != null ? value.trim() : null; // Commento automatico: return value != null ? value.trim() : null;
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private BigDecimal normalizePrice(BigDecimal price) { // Commento automatico: private BigDecimal normalizePrice(BigDecimal price) {
        return price != null ? price : BigDecimal.ZERO; // Commento automatico: return price != null ? price : BigDecimal.ZERO;
    } // Commento automatico: }
} // Commento automatico: }
