package com.example.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createShouldInitializeDefaults() {
        Article article = Article.create("ART-1", "Laptop", "Ultrabook", new BigDecimal("999.99"),
                new BigDecimal("22"), "PZ");

        assertThat(article.getId()).isNull();
        assertThat(article.getCreatedAt()).isNull();
        assertThat(article.getUpdatedAt()).isNull();
        assertThat(article.getCode()).isEqualTo("ART-1");
        assertThat(article.getName()).isEqualTo("Laptop");
    }

    @Test
    void shouldValidateMandatoryFields() {
        Article invalid = new Article(1L, " ", " ", "desc", null, null, " ", Instant.now(), Instant.now());

        Set<ConstraintViolation<Article>> violations = validator.validate(invalid);

        assertThat(violations).extracting("message").containsExactlyInAnyOrder(
                "Il codice articolo è obbligatorio",
                "Il nome articolo è obbligatorio",
                "Il prezzo unitario è obbligatorio",
                "L'aliquota IVA è obbligatoria",
                "L'unità di misura è obbligatoria"
        );
    }

    @Test
    void shouldValidatePositiveAmounts() {
        Article invalid = Article.create("ART-2", "Monitor", "4K", new BigDecimal("-1"),
                new BigDecimal("-5"), "PZ");

        Set<ConstraintViolation<Article>> violations = validator.validate(invalid);

        assertThat(violations).extracting("message")
                .containsExactlyInAnyOrder(
                        "Il prezzo unitario deve essere positivo",
                        "L'aliquota IVA deve essere positiva"
                );
    }

    @Test
    void utilityMethodsShouldUseIdentifiersAndExposeReadableToString() {
        Article first = Article.create("ART-10", "Tablet", "", BigDecimal.ONE, BigDecimal.ZERO, "PZ").withId(5L);
        Article second = Article.create("ART-11", "Phone", "", BigDecimal.ONE, BigDecimal.ZERO, "PZ").withId(5L);
        Article different = Article.create("ART-12", "PC", "", BigDecimal.ONE, BigDecimal.ZERO, "PZ").withId(6L);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(different);
        assertThat(first.toString()).contains("Article{", "id=5", "code='ART-10'", "unitOfMeasure='PZ'");
    }
}
