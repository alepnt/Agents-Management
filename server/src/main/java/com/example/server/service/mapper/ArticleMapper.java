package com.example.server.service.mapper; // Defines the package for article mapping utilities

import com.example.common.dto.ArticleDTO; // Imports the DTO representation of an Article
import com.example.server.domain.Article; // Imports the entity representation of an Article

public final class ArticleMapper { // Utility class to convert between Article entity and DTO

    private ArticleMapper() { // Private constructor to prevent instantiation
    }

    public static ArticleDTO toDto(Article article) { // Converts an Article entity to its DTO form
        if (article == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException when mapping
        }
        return new ArticleDTO( // Builds the DTO using entity values
                article.getId(), // Maps the article identifier
                article.getCode(), // Maps the article code
                article.getName(), // Maps the article name
                article.getDescription(), // Maps the article description
                article.getUnitPrice(), // Maps the unit price
                article.getVatRate(), // Maps the VAT rate
                article.getUnitOfMeasure(), // Maps the unit of measure
                article.getCreatedAt(), // Maps the creation timestamp
                article.getUpdatedAt() // Maps the last update timestamp
        );
    }

    public static Article fromDto(ArticleDTO dto) { // Converts an ArticleDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Article( // Builds the entity using DTO values
                dto.getId(), // Sets the article identifier
                dto.getCode(), // Sets the article code
                dto.getName(), // Sets the article name
                dto.getDescription(), // Sets the article description
                dto.getUnitPrice(), // Sets the unit price
                dto.getVatRate(), // Sets the VAT rate
                dto.getUnitOfMeasure(), // Sets the unit of measure
                dto.getCreatedAt(), // Sets the creation timestamp
                dto.getUpdatedAt() // Sets the last update timestamp
        );
    }
}
