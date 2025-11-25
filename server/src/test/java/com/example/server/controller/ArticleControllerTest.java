package com.example.server.controller;

import com.example.common.dto.ArticleDTO;
import com.example.server.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@Import(ArticleControllerTest.Config.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @TestConfiguration
    static class Config {
        @Bean
        JdbcMappingContext jdbcMappingContext() {
            return new JdbcMappingContext();
        }
    }

    @Test
    @DisplayName("List articles returns all resources")
    void listArticles() throws Exception {
        List<ArticleDTO> articles = List.of(
                new ArticleDTO(1L, "A1", "Article 1", "Description", BigDecimal.TEN, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH),
                new ArticleDTO(2L, "A2", "Article 2", "Description", BigDecimal.ONE, BigDecimal.ZERO, "pz", Instant.EPOCH, Instant.EPOCH)
        );
        when(articleService.findAll()).thenReturn(articles);

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("A1"))
                .andExpect(jsonPath("$[1].vatRate").value(0));
    }

    @Test
    @DisplayName("Create article returns persisted payload")
    void createArticle() throws Exception {
        ArticleDTO request = new ArticleDTO(null, "A1", "Article", "Description", BigDecimal.TEN, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH);
        ArticleDTO saved = new ArticleDTO(9L, "A1", "Article", "Description", BigDecimal.TEN, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH);
        when(articleService.create(any(ArticleDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/articles")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.name").value("Article"));
    }

    @Test
    @DisplayName("Create article propagates service failures")
    void createArticleError() throws Exception {
        when(articleService.create(any(ArticleDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid"));

        ArticleDTO request = new ArticleDTO(null, "A1", "Article", "Description", BigDecimal.TEN, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH);

        mockMvc.perform(post("/api/articles")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update article returns updated payload")
    void updateArticle() throws Exception {
        ArticleDTO request = new ArticleDTO(null, "A1", "Updated", "Description", BigDecimal.ONE, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH);
        ArticleDTO updated = new ArticleDTO(3L, "A1", "Updated", "Description", BigDecimal.ONE, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH);
        when(articleService.update(eq(3L), any(ArticleDTO.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/articles/3")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("Update article returns 404 when missing")
    void updateArticleNotFound() throws Exception {
        when(articleService.update(eq(8L), any(ArticleDTO.class))).thenReturn(Optional.empty());

        ArticleDTO request = new ArticleDTO(null, "A1", "Updated", "Description", BigDecimal.ONE, BigDecimal.ONE, "pz", Instant.EPOCH, Instant.EPOCH);

        mockMvc.perform(put("/api/articles/8")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete article returns 404 when nothing is deleted")
    void deleteArticleNotFound() throws Exception {
        when(articleService.delete(44L)).thenReturn(false);

        mockMvc.perform(delete("/api/articles/44"))
                .andExpect(status().isNotFound());
    }
}
