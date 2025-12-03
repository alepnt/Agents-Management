package com.example.server.config; // Package di configurazione condiviso dal backend.

import com.example.common.enums.ContractStatus; // Enum che rappresenta gli stati di un contratto.
import java.util.List; // Collezione immutabile di converter registrati.
import org.springframework.context.annotation.Bean; // Consente di esporre i metodi come bean Spring.
import org.springframework.context.annotation.Configuration; // Indica che la classe contiene definizioni di bean.
import org.springframework.core.convert.converter.Converter; // Interfaccia per implementare la logica di conversione custom.
import org.springframework.data.convert.ReadingConverter; // Annota il converter utilizzato in lettura dal database.
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions; // Espone la registrazione dei converter personalizzati.

/**
 * Configura i converter personalizzati usati da Spring Data JDBC.
 */
@Configuration // Rende la classe rilevabile come configurazione Spring.
public class JdbcEnumConfiguration { // Definisce la configurazione per i converter JDBC.

    @Bean // Esporta il bean che registra i converter custom.
    public JdbcCustomConversions jdbcCustomConversions() { // Restituisce le conversioni personalizzate da applicare.
        return new JdbcCustomConversions(List.of(new ContractStatusReadConverter())); // Registra il converter per ContractStatus.
    }

    @ReadingConverter // Indica che il converter viene usato in fase di lettura dal database.
    static class ContractStatusReadConverter implements Converter<String, ContractStatus> { // Converte le stringhe dello stato contratto.

        @Override
        public ContractStatus convert(String source) { // Converte una stringa in ContractStatus normalizzando il valore.
            if (source == null) { // Gestisce valori null provenienti dal database.
                return null; // Restituisce null senza sollevare eccezioni.
            }
            String normalized = source.trim().toUpperCase(); // Rimuove spazi e normalizza il case per evitare errori.
            return ContractStatus.valueOf(normalized); // Converte nel corrispondente valore enum.
        }
    }
}
