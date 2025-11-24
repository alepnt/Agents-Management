package com.example.server.e2e;

import com.example.common.dto.AgentDTO;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.MonthlyCommissionDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.enums.ContractStatus;
import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Contract;
import com.example.server.domain.Customer;
import com.example.server.domain.User;
import com.example.server.dto.AuthResponse;
import com.example.server.dto.LoginRequest;
import com.example.server.repository.AgentRepository;
import com.example.server.repository.ContractRepository;
import com.example.server.repository.CustomerRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.MsalClientProvider;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class ApiE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private MsalClientProvider msalClientProvider;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeEach
    void mockAzure() throws Exception {
        ConfidentialClientApplication client = mock(ConfidentialClientApplication.class);
        IAuthenticationResult result = mock(IAuthenticationResult.class);
        when(result.accessToken()).thenReturn("delegated-token");
        when(client.acquireToken(any(OnBehalfOfParameters.class)))
                .thenReturn(CompletableFuture.completedFuture(result));
        when(msalClientProvider.createClient()).thenReturn(client);
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.batchUpdate(
                "DELETE FROM invoice_lines",
                "DELETE FROM document_history",
                "DELETE FROM invoices",
                "DELETE FROM commissions",
                "DELETE FROM contracts",
                "DELETE FROM agents",
                "DELETE FROM users",
                "DELETE FROM customers",
                "DELETE FROM articles"
        );
    }

    @Test
    @DisplayName("/auth/login registra l'utente e restituisce il token MSAL")
    void loginEndpointRegistersUser() {
        LoginRequest request = new LoginRequest("user-access-token", "login@example.com", "Login User", "az-100");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(url("/api/auth/login"), request, AuthResponse.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isEqualTo("delegated-token");
        assertThat(response.getBody().user().email()).isEqualTo("login@example.com");
        assertThat(userRepository.findByAzureId("az-100")).isPresent();
    }

    @Test
    @DisplayName("/agents espone le operazioni CRUD end-to-end")
    void agentEndpointsExposeCrudLifecycle() {
        User owner = createUser("az-agent-1", "agent1@example.com", "Agente Uno");
        AgentDTO payload = new AgentDTO(null, owner.getId(), "AG-001", "Lead");

        ResponseEntity<AgentDTO> creation = restTemplate.postForEntity(url("/api/agents"), payload, AgentDTO.class);
        assertThat(creation.getStatusCode().is2xxSuccessful()).isTrue();
        AgentDTO created = creation.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();

        ResponseEntity<List<AgentDTO>> listResponse = restTemplate.exchange(
                url("/api/agents"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(listResponse.getBody()).extracting(AgentDTO::getAgentCode).contains("AG-001");

        AgentDTO updatePayload = new AgentDTO(created.getId(), owner.getId(), "AG-001", "Coach");
        ResponseEntity<AgentDTO> updatedResponse = restTemplate.exchange(
                url("/api/agents/" + created.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatePayload),
                AgentDTO.class
        );

        assertThat(updatedResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updatedResponse.getBody()).isNotNull();
        assertThat(updatedResponse.getBody().getTeamRole()).isEqualTo("Coach");
        assertThat(agentRepository.findById(created.getId())).isPresent();
    }

    @Test
    @DisplayName("/invoices supporta creazione, lettura e aggiornamento")
    void invoiceEndpointsSupportLifecycle() {
        SeedData seed = seedContractAndCustomer();

        InvoiceDTO payload = new InvoiceDTO();
        payload.setContractId(seed.contractId);
        payload.setCustomerId(seed.customerId);
        payload.setAmount(new BigDecimal("1200.00"));
        payload.setIssueDate(LocalDate.of(2024, 1, 15));
        payload.setDueDate(LocalDate.of(2024, 2, 15));
        payload.setStatus(InvoiceStatus.DRAFT);
        payload.setNotes("Prima bozza");

        ResponseEntity<InvoiceDTO> creation = restTemplate.postForEntity(url("/api/invoices"), payload, InvoiceDTO.class);
        assertThat(creation.getStatusCode().is2xxSuccessful()).isTrue();
        InvoiceDTO created = creation.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getNumber()).isNotBlank();

        ResponseEntity<InvoiceDTO> retrieved = restTemplate.getForEntity(url("/api/invoices/" + created.getId()), InvoiceDTO.class);
        assertThat(retrieved.getBody()).isNotNull();
        assertThat(retrieved.getBody().getAmount()).isEqualByComparingTo(new BigDecimal("1200.00"));

        InvoiceDTO updatePayload = new InvoiceDTO(
                created.getId(),
                created.getNumber(),
                seed.contractId,
                seed.customerId,
                created.getCustomerName(),
                created.getAmount(),
                created.getIssueDate(),
                created.getDueDate(),
                InvoiceStatus.SENT,
                created.getPaymentDate(),
                "Aggiornata",
                created.getLines()
        );

        ResponseEntity<InvoiceDTO> updated = restTemplate.exchange(
                url("/api/invoices/" + created.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatePayload),
                InvoiceDTO.class
        );

        assertThat(updated.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updated.getBody()).isNotNull();
        assertThat(updated.getBody().getStatus()).isEqualTo(InvoiceStatus.SENT);
        assertThat(updated.getBody().getNotes()).isEqualTo("Aggiornata");
    }

    @Test
    @DisplayName("/stats restituisce aggregati coerenti dopo il pagamento di una fattura")
    void statisticsEndpointsReturnAggregations() {
        SeedData seed = seedContractAndCustomer();

        InvoiceDTO payload = new InvoiceDTO();
        payload.setContractId(seed.contractId);
        payload.setCustomerId(seed.customerId);
        payload.setAmount(new BigDecimal("1500.00"));
        payload.setIssueDate(LocalDate.of(2024, 3, 10));
        payload.setDueDate(LocalDate.of(2024, 4, 10));
        payload.setStatus(InvoiceStatus.DRAFT);

        InvoiceDTO created = restTemplate.postForObject(url("/api/invoices"), payload, InvoiceDTO.class);
        assertThat(created).isNotNull();

        InvoicePaymentRequest payment = new InvoicePaymentRequest(new BigDecimal("1500.00"), LocalDate.of(2024, 3, 20));
        InvoiceDTO paid = restTemplate.postForObject(
                url("/api/invoices/" + created.getId() + "/payments"),
                payment,
                InvoiceDTO.class
        );
        assertThat(paid).isNotNull();
        assertThat(paid.getStatus()).isEqualTo(InvoiceStatus.PAID);

        AgentStatisticsDTO agentStats = restTemplate.getForObject(url("/api/stats/agent?year=2024"), AgentStatisticsDTO.class);
        assertThat(agentStats).isNotNull();
        assertThat(agentStats.agentTotals()).isNotEmpty();

        TeamStatisticsDTO teamStats = restTemplate.getForObject(url("/api/stats/team?year=2024"), TeamStatisticsDTO.class);
        assertThat(teamStats).isNotNull();
        assertThat(teamStats.teamTotals()).isNotEmpty();

        Map<Integer, MonthlyCommissionDTO> monthlyByMonth = agentStats.monthlyTotals().stream()
                .collect(java.util.stream.Collectors.toMap(MonthlyCommissionDTO::month, m -> m));
        assertThat(monthlyByMonth).containsKey(3);
        assertThat(monthlyByMonth.get(3).commission()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(teamStats.teamTotals().getFirst().teamName()).isEqualTo(seed.teamName);
    }

    private SeedData seedContractAndCustomer() {
        User owner = createUser("az-owner-" + System.nanoTime(), "owner@example.com", "Owner");
        Long agentId = agentRepository.save(com.example.server.domain.Agent.forUser(owner.getId(), "AG-SEED", "Lead")).getId();
        Contract contract = contractRepository.save(Contract.create(
                agentId,
                "Cliente Seed",
                "Contratto di prova",
                LocalDate.of(2024, 1, 1),
                null,
                new BigDecimal("5000.00"),
                ContractStatus.ACTIVE
        ));

        Customer customer = customerRepository.save(Customer.create(
                "Seed Customer",
                "IT1234567890",
                "TAX123",
                "customer@example.com",
                "+390123456789",
                "Via Roma 1"
        ));

        return new SeedData(owner.getTeamId(), contract.getId(), customer.getId(), owner.getDisplayName(), "Vendite");
    }

    private User createUser(String azureId, String email, String displayName) {
        Long roleId = roleRepository.findByName("Agent")
                .orElseThrow(() -> new IllegalStateException("Default role missing"))
                .getId();
        Long teamId = teamRepository.findByName("Vendite")
                .orElseThrow(() -> new IllegalStateException("Default team missing"))
                .getId();
        return userRepository.save(new User(null, azureId, email, displayName, null, roleId, teamId, true, LocalDateTime.now()));
    }

    private record SeedData(Long teamId, Long contractId, Long customerId, String agentName, String teamName) {
    }
}
