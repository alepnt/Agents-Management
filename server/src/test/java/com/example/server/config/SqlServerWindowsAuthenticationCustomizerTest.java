package com.example.server.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlServerWindowsAuthenticationCustomizerTest {

    private final String originalLibraryPath = System.getProperty("java.library.path", "");

    @AfterEach
    void restoreLibraryPath() {
        System.setProperty("java.library.path", originalLibraryPath);
    }

    @Test
    void doesNothingWhenWindowsAuthenticationDisabled() {
        DatabaseAuthenticationProperties properties = new DatabaseAuthenticationProperties();
        SqlServerWindowsAuthenticationCustomizer customizer = new SqlServerWindowsAuthenticationCustomizer(properties);

        try (HikariDataSource dataSource = new HikariDataSource()) {
            dataSource.setJdbcUrl("jdbc:sqlserver://localhost");
            dataSource.setUsername("sa");
            dataSource.setPassword("password");

            customizer.postProcessAfterInitialization(dataSource, "dataSource");

            assertEquals("sa", dataSource.getUsername());
            assertEquals("password", dataSource.getPassword());
            assertNull(dataSource.getDataSourceProperties().getProperty("integratedSecurity"));
        }
    }

    @Test
    void skipsConfigurationForNonSqlServerUrls() {
        DatabaseAuthenticationProperties properties = new DatabaseAuthenticationProperties();
        properties.setAuthenticationMode(DatabaseAuthenticationProperties.AuthenticationMode.WINDOWS);
        SqlServerWindowsAuthenticationCustomizer customizer = new SqlServerWindowsAuthenticationCustomizer(properties);

        try (HikariDataSource dataSource = new HikariDataSource()) {
            dataSource.setJdbcUrl("jdbc:h2:mem:testdb");
            dataSource.setUsername("user");
            dataSource.setPassword("secret");

            customizer.postProcessAfterInitialization(dataSource, "dataSource");

            assertEquals("user", dataSource.getUsername());
            assertEquals("secret", dataSource.getPassword());
            assertNull(dataSource.getDataSourceProperties().getProperty("integratedSecurity"));
        }
    }

    @Test
    void configuresSqlServerDataSourceWithWindowsAuthentication() throws Exception {
        DatabaseAuthenticationProperties properties = new DatabaseAuthenticationProperties();
        properties.setAuthenticationMode(DatabaseAuthenticationProperties.AuthenticationMode.WINDOWS);
        properties.setAuthenticationScheme("Kerberos");
        var dllDirectory = java.nio.file.Files.createTempDirectory("sqljdbc");
        properties.setNativeLibraryPath(dllDirectory.toString());

        SqlServerWindowsAuthenticationCustomizer customizer = new SqlServerWindowsAuthenticationCustomizer(properties);

        try (HikariDataSource dataSource = new HikariDataSource()) {
            dataSource.setPoolName("testPool");
            dataSource.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=testdb");
            dataSource.setUsername("sa");
            dataSource.setPassword("password");

            customizer.postProcessAfterInitialization(dataSource, "dataSource");

            assertNull(dataSource.getUsername());
            assertNull(dataSource.getPassword());

            assertTrue(dataSource.getJdbcUrl().toLowerCase().contains("integratedsecurity=true"));
            assertEquals("true", dataSource.getDataSourceProperties().getProperty("integratedSecurity"));
            assertEquals("Kerberos", dataSource.getDataSourceProperties().getProperty("authenticationScheme"));

            String updatedLibraryPath = System.getProperty("java.library.path");
            assertNotNull(updatedLibraryPath);
            assertTrue(updatedLibraryPath.startsWith(dllDirectory.toAbsolutePath().toString()));
        }
    }
}
