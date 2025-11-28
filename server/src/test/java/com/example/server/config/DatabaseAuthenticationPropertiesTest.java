package com.example.server.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAuthenticationPropertiesTest {

    private DatabaseAuthenticationProperties properties;

    @BeforeEach
    void setUp() {
        properties = new DatabaseAuthenticationProperties();
    }

    @Test
    void defaultsAreSqlWithNativeAuthentication() {
        assertEquals(DatabaseAuthenticationProperties.AuthenticationMode.SQL, properties.getAuthenticationMode());
        assertEquals("NativeAuthentication", properties.getAuthenticationScheme());
        assertNull(properties.getNativeLibraryPath());
        assertFalse(properties.isWindowsAuthentication());
    }

    @Test
    void blankValuesFallbackToDefaults() {
        properties.setAuthenticationMode(null);
        properties.setAuthenticationScheme("   ");
        properties.setNativeLibraryPath("   ");

        assertEquals(DatabaseAuthenticationProperties.AuthenticationMode.SQL, properties.getAuthenticationMode());
        assertEquals("NativeAuthentication", properties.getAuthenticationScheme());
        assertNull(properties.getNativeLibraryPath());
    }

    @Test
    void nonBlankValuesAreApplied() {
        properties.setAuthenticationMode(DatabaseAuthenticationProperties.AuthenticationMode.WINDOWS);
        properties.setAuthenticationScheme("Kerberos");
        properties.setNativeLibraryPath("/opt/sqljdbc");

        assertEquals(DatabaseAuthenticationProperties.AuthenticationMode.WINDOWS, properties.getAuthenticationMode());
        assertTrue(properties.isWindowsAuthentication());
        assertEquals("Kerberos", properties.getAuthenticationScheme());
        assertEquals("/opt/sqljdbc", properties.getNativeLibraryPath());
    }
}
