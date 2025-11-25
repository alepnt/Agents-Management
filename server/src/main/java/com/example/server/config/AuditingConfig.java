package com.example.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.Instant;
import java.util.Optional;

/**
 * Configurazione di supporto per l'auditing di Spring Data JDBC.
 * Fornisce un {@link DateTimeProvider} che restituisce l'istante corrente,
 * consentendo la valorizzazione automatica dei campi annotati con
 * {@code @CreatedDate} e {@code @LastModifiedDate} anche nei profili di test.
 */
@Configuration
public class AuditingConfig {

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }
}
