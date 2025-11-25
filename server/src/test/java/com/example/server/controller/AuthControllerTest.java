package com.example.server.controller;

import com.example.server.dto.AuthResponse;
import com.example.server.dto.LoginRequest;
import com.example.server.dto.RegisterRequest;
import com.example.server.dto.UserSummary;
import com.example.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    // Prevents Spring Data JDBC auditing infrastructure from being initialized during this
    // MVC slice test. The application enables JDBC auditing globally, which would otherwise
    // attempt to construct a real auditing handler (and its dependencies) when the context
    // starts. Providing a mock bean with the expected name short-circuits that setup so the
    // controller tests can focus solely on the web layer.
    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("Login returns token payload when credentials are valid")
    void loginReturnsToken() throws Exception {
        AuthResponse response = new AuthResponse(
                "jwt-token",
                "Bearer",
                Instant.parse("2024-01-01T00:00:00Z"),
                new UserSummary(1L, "user@example.com", "User", "azure-1", 2L, 3L)
        );
        when(userService.loginWithMicrosoft(any(LoginRequest.class))).thenReturn(response);

        LoginRequest request = new LoginRequest("token", "user@example.com", "User", "azure-1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").value("2024-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.user.roleId").value(2));
    }

    @Test
    @DisplayName("Login rejects missing fields with validation error")
    void loginValidationError() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login maps service exception to HTTP status")
    void loginServiceError() throws Exception {
        when(userService.loginWithMicrosoft(any(LoginRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));

        LoginRequest request = new LoginRequest("token", "user@example.com", "User", "azure-1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Register returns created user summary")
    void registerReturnsCreated() throws Exception {
        UserSummary summary = new UserSummary(10L, "new@example.com", "New User", "az-10", 5L, 6L);
        when(userService.register(any(RegisterRequest.class))).thenReturn(summary);

        RegisterRequest request = new RegisterRequest("az-10", "new@example.com", "New User", "ABC123", "password-123", "Team", "Role");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.azureId").value("az-10"));
    }

    @Test
    @DisplayName("Register enforces bean validation constraints")
    void registerValidationError() throws Exception {
        RegisterRequest request = new RegisterRequest("", "not-an-email", "", "A", "short", null, null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register propagates service errors to HTTP layer")
    void registerServiceError() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "User exists"));

        RegisterRequest request = new RegisterRequest("az-10", "new@example.com", "New User", "ABC123", "password-123", "Team", "Role");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
