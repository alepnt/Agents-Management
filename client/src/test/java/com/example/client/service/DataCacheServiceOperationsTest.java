package com.example.client.service;

import com.example.client.command.ClientCommand;
import com.example.client.command.CommandExecutor;
import com.example.client.command.CommandHistoryCaretaker;
import com.example.client.command.CommandResult;
import com.example.client.command.CreateArticleCommand;
import com.example.client.command.CreateContractCommand;
import com.example.client.command.CreateCustomerCommand;
import com.example.client.command.CreateInvoiceCommand;
import com.example.client.command.DeleteArticleCommand;
import com.example.client.command.DeleteContractCommand;
import com.example.client.command.DeleteCustomerCommand;
import com.example.client.command.DeleteInvoiceCommand;
import com.example.client.command.LoadArticlesCommand;
import com.example.client.command.LoadContractsCommand;
import com.example.client.command.LoadCustomersCommand;
import com.example.client.command.LoadInvoicesCommand;
import com.example.client.command.RegisterInvoicePaymentCommand;
import com.example.client.command.UpdateArticleCommand;
import com.example.client.command.UpdateContractCommand;
import com.example.client.command.UpdateCustomerCommand;
import com.example.client.command.UpdateInvoiceCommand;
import com.example.client.model.DataChangeType;
import com.example.client.session.SessionStore;
import com.example.common.dto.AgentDTO;
import com.example.common.dto.ArticleDTO;
import com.example.common.dto.CommissionDTO;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.CustomerDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.MessageDTO;
import com.example.common.dto.RoleDTO;
import com.example.common.dto.TeamDTO;
import com.example.common.dto.UserDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataCacheServiceOperationsTest {

    @TempDir
    Path tempDir;

    private AuthSession activeSession;

    @BeforeEach
    void setUp() {
        activeSession = new AuthSession(
                "token-test",
                "Bearer",
                Instant.now().plusSeconds(3600),
                new UserSummary(1L, "user@example.com", "User", "azure-1", 1L, 1L)
        );
    }

    @Test
    void commandDrivenOperationsInvalidateCachesAndNotifyObservers() throws Exception {
        SessionStore sessionStore = new SessionStore(tempDir);
        sessionStore.save(activeSession);

        TrackingGateway gateway = new TrackingGateway();
        Map<Class<?>, CommandResult<?>> responses = new HashMap<>();

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setId(99L);
        responses.put(LoadInvoicesCommand.class, CommandResult.withoutHistory(List.of(invoice)));
        responses.put(CreateInvoiceCommand.class, CommandResult.withHistory(invoice, invoice.getId(), DocumentType.INVOICE, List.of(new DocumentHistoryDTO())));
        responses.put(UpdateInvoiceCommand.class, CommandResult.withHistory(invoice, invoice.getId(), DocumentType.INVOICE, List.of(new DocumentHistoryDTO())));
        responses.put(DeleteInvoiceCommand.class, null);
        responses.put(RegisterInvoicePaymentCommand.class, CommandResult.withHistory(invoice, invoice.getId(), DocumentType.INVOICE, List.of(new DocumentHistoryDTO())));

        ArticleDTO article = new ArticleDTO();
        article.setId(10L);
        responses.put(LoadArticlesCommand.class, CommandResult.withoutHistory(List.of(article)));
        responses.put(CreateArticleCommand.class, CommandResult.withoutHistory(article));
        responses.put(UpdateArticleCommand.class, CommandResult.withoutHistory(article));
        responses.put(DeleteArticleCommand.class, null);

        ContractDTO contract = new ContractDTO();
        contract.setId(7L);
        responses.put(LoadContractsCommand.class, CommandResult.withoutHistory(List.of(contract)));
        responses.put(CreateContractCommand.class, CommandResult.withoutHistory(contract));
        responses.put(UpdateContractCommand.class, CommandResult.withoutHistory(contract));
        responses.put(DeleteContractCommand.class, null);

        CustomerDTO customer = new CustomerDTO();
        customer.setId(4L);
        responses.put(LoadCustomersCommand.class, CommandResult.withoutHistory(List.of(customer)));
        responses.put(CreateCustomerCommand.class, CommandResult.withoutHistory(customer));
        responses.put(UpdateCustomerCommand.class, CommandResult.withoutHistory(customer));
        responses.put(DeleteCustomerCommand.class, null);

        DataCacheService service = DataCacheService.create(sessionStore);
        injectField(service, "backendGateway", gateway);
        injectField(service, "executor", new PreparedResultExecutor(responses));

        Map<Integer, ?> agentCache = extractMap(service, "agentStatsCache");
        Map<Integer, ?> teamCache = extractMap(service, "teamStatsCache");
        Map<String, ?> historyCache = extractMap(service, "historyCache");

        agentCache.put(2024, gateway.agentStats);
        teamCache.put(2024, gateway.teamStats);
        historyCache.put("history", gateway.historyPage);

        List<DataChangeType> events = new ArrayList<>();
        service.subscribeDataChanges(evt -> events.add(evt.type()));

        assertSame(invoice, service.createInvoice(invoice));
        assertSame(invoice, service.updateInvoice(invoice.getId(), invoice));
        service.deleteInvoice(invoice.getId());
        InvoicePaymentRequest paymentRequest = new InvoicePaymentRequest(LocalDate.now(), BigDecimal.TEN);
        assertSame(invoice, service.registerPayment(invoice.getId(), paymentRequest));

        assertEquals(List.of(invoice), service.getInvoices());
        assertEquals(List.of(article), service.getArticles());
        assertSame(article, service.createArticle(article));
        assertSame(article, service.updateArticle(article.getId(), article));
        service.deleteArticle(article.getId());

        assertEquals(List.of(contract), service.getContracts());
        assertSame(contract, service.createContract(contract));
        assertSame(contract, service.updateContract(contract.getId(), contract));
        service.deleteContract(contract.getId());

        assertEquals(List.of(customer), service.getCustomers());
        assertSame(customer, service.createCustomer(customer));
        assertSame(customer, service.updateCustomer(customer.getId(), customer));
        service.deleteCustomer(customer.getId());

        assertTrue(agentCache.isEmpty(), "La cache statistiche agenti deve essere invalidata");
        assertTrue(teamCache.isEmpty(), "La cache statistiche team deve essere invalidata");
        assertTrue(historyCache.isEmpty(), "La cache dello storico deve essere invalidata");

        Map<DataChangeType, Long> occurrences = events.stream()
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
        assertEquals(4L, occurrences.getOrDefault(DataChangeType.INVOICE, 0L), "Ogni modifica fattura deve notificare gli observer");
        assertEquals(2L, occurrences.getOrDefault(DataChangeType.ARTICLE, 0L), "Le variazioni articoli devono notificare");
        assertEquals(2L, occurrences.getOrDefault(DataChangeType.CONTRACT, 0L), "Le variazioni contratti devono notificare");
        assertEquals(2L, occurrences.getOrDefault(DataChangeType.CUSTOMER, 0L), "Le variazioni clienti devono notificare");

    }

    @Test
    void directGatewayOperationsDelegateCallsAndEmitEvents() throws Exception {
        SessionStore sessionStore = new SessionStore(tempDir);
        sessionStore.save(activeSession);

        TrackingGateway gateway = new TrackingGateway();
        DataCacheService service = DataCacheService.create(sessionStore);
        injectField(service, "backendGateway", gateway);

        List<DataChangeType> events = new ArrayList<>();
        service.subscribeDataChanges(e -> events.add(e.type()));

        List<AgentDTO> agents = service.getAgents();
        assertEquals(gateway.agents, agents);
        assertSame(gateway.sampleAgent, service.createAgent(gateway.sampleAgent));
        assertSame(gateway.sampleAgent, service.updateAgent(1L, gateway.sampleAgent));
        service.deleteAgent(1L);

        List<TeamDTO> teams = service.getTeams();
        assertEquals(gateway.teams, teams);
        assertSame(gateway.sampleTeam, service.createTeam(gateway.sampleTeam));
        assertSame(gateway.sampleTeam, service.updateTeam(2L, gateway.sampleTeam));
        service.deleteTeam(2L);

        List<RoleDTO> roles = service.getRoles();
        assertEquals(gateway.roles, roles);
        assertSame(gateway.sampleRole, service.createRole(gateway.sampleRole));
        assertSame(gateway.sampleRole, service.updateRole(3L, gateway.sampleRole));
        service.deleteRole(3L);

        List<UserDTO> users = service.getUsers();
        assertEquals(gateway.users, users);
        assertSame(gateway.sampleUser, service.createUser(gateway.sampleUser));
        assertSame(gateway.sampleUser, service.updateUser(4L, gateway.sampleUser));
        service.deleteUser(4L);

        List<MessageDTO> messages = service.getMessages();
        assertEquals(gateway.messages, messages);
        assertSame(gateway.sampleMessage, service.createMessage(gateway.sampleMessage));
        assertSame(gateway.sampleMessage, service.updateMessage(5L, gateway.sampleMessage));
        service.deleteMessage(5L);

        List<CommissionDTO> commissions = service.getCommissions();
        assertEquals(gateway.commissions, commissions);
        assertSame(gateway.sampleCommission, service.createCommission(gateway.sampleCommission));
        assertSame(gateway.sampleCommission, service.updateCommission(6L, gateway.sampleCommission));
        service.deleteCommission(6L);

        List<InvoiceLineDTO> invoiceLines = service.getInvoiceLines(10L);
        assertEquals(gateway.invoiceLines, invoiceLines);
        assertSame(gateway.sampleInvoiceLine, service.createInvoiceLine(gateway.sampleInvoiceLine));
        assertSame(gateway.sampleInvoiceLine, service.updateInvoiceLine(7L, gateway.sampleInvoiceLine));
        service.deleteInvoiceLine(7L);

        assertEquals(gateway.invoiceHistory, service.getInvoiceHistory(1L));
        assertEquals(gateway.contractHistory, service.getContractHistory(2L));

        byte[] report = service.downloadClosedInvoiceReport(LocalDate.MIN, LocalDate.MAX, 1L);
        assertArrayEquals(gateway.reportBytes, report, "Il report deve essere scaricato via gateway");

        assertEquals(24, gateway.totalCalls(), "Ogni metodo diretto deve delegare al gateway");

        Map<DataChangeType, Long> occurrences = events.stream()
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.AGENT, 0L));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.TEAM, 0L));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.ROLE, 0L));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.USER, 0L));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.MESSAGE, 0L));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.COMMISSION, 0L));
        assertEquals(3L, occurrences.getOrDefault(DataChangeType.INVOICE, 0L));
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private <K, V> Map<K, V> extractMap(DataCacheService service, String fieldName) throws Exception {
        Field field = DataCacheService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Map<K, V>) field.get(service);
    }

    private static class PreparedResultExecutor extends CommandExecutor {
        private final Map<Class<?>, CommandResult<?>> responses;

        PreparedResultExecutor(Map<Class<?>, CommandResult<?>> responses) {
            super(new BackendGateway(), new CommandHistoryCaretaker());
            this.responses = responses;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> CommandResult<T> execute(ClientCommand<T> command) {
            return (CommandResult<T>) responses.get(command.getClass());
        }
    }

    private static class TrackingGateway extends BackendGateway {
        private final AtomicInteger invoiceCalls = new AtomicInteger();
        private final byte[] reportBytes = {1, 2, 3};
        private final List<DocumentHistoryDTO> invoiceHistory = List.of(new DocumentHistoryDTO(1L, DocumentType.INVOICE, 1L, DocumentAction.CREATED, "Created", Instant.now()));
        private final List<DocumentHistoryDTO> contractHistory = List.of(new DocumentHistoryDTO(2L, DocumentType.CONTRACT, 2L, DocumentAction.CREATED, "Created", Instant.now()));

        private final AgentDTO sampleAgent = new AgentDTO(1L, 1L, "A1", "Lead");
        private final TeamDTO sampleTeam = new TeamDTO(2L, "Team");
        private final RoleDTO sampleRole = new RoleDTO(3L, "Admin", "desc");
        private final UserDTO sampleUser = new UserDTO(4L, "User", "user@example.com", true, 1L, 2L, 3L);
        private final MessageDTO sampleMessage = new MessageDTO(5L, "Oggetto", "Body", Instant.now(), 1L, 2L);
        private final CommissionDTO sampleCommission = new CommissionDTO(6L, "Code", BigDecimal.ONE, BigDecimal.TEN, 1L, 2L, 3L);
        private final InvoiceLineDTO sampleInvoiceLine = new InvoiceLineDTO(7L, 10L, 1L, "Line", 1, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO);

        private final List<AgentDTO> agents = List.of(sampleAgent);
        private final List<TeamDTO> teams = List.of(sampleTeam);
        private final List<RoleDTO> roles = List.of(sampleRole);
        private final List<UserDTO> users = List.of(sampleUser);
        private final List<MessageDTO> messages = List.of(sampleMessage);
        private final List<CommissionDTO> commissions = List.of(sampleCommission);
        private final List<InvoiceLineDTO> invoiceLines = List.of(sampleInvoiceLine);

        private final com.example.common.dto.AgentStatisticsDTO agentStats = new com.example.common.dto.AgentStatisticsDTO(2024, List.of(2024), List.of(), List.of());
        private final com.example.common.dto.TeamStatisticsDTO teamStats = new com.example.common.dto.TeamStatisticsDTO(2024, List.of(2024), List.of());
        private final com.example.common.dto.DocumentHistoryPageDTO historyPage = new com.example.common.dto.DocumentHistoryPageDTO(List.of(), 0, 0, 0);

        @Override
        public List<AgentDTO> listAgents() {
            return agents;
        }

        @Override
        public AgentDTO createAgent(AgentDTO agentDTO) {
            invoiceCalls.incrementAndGet();
            return agentDTO;
        }

        @Override
        public AgentDTO updateAgent(Long id, AgentDTO agentDTO) {
            invoiceCalls.incrementAndGet();
            return agentDTO;
        }

        @Override
        public void deleteAgent(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<TeamDTO> listTeams() {
            return teams;
        }

        @Override
        public TeamDTO createTeam(TeamDTO teamDTO) {
            invoiceCalls.incrementAndGet();
            return teamDTO;
        }

        @Override
        public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
            invoiceCalls.incrementAndGet();
            return teamDTO;
        }

        @Override
        public void deleteTeam(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<RoleDTO> listRoles() {
            return roles;
        }

        @Override
        public RoleDTO createRole(RoleDTO roleDTO) {
            invoiceCalls.incrementAndGet();
            return roleDTO;
        }

        @Override
        public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
            invoiceCalls.incrementAndGet();
            return roleDTO;
        }

        @Override
        public void deleteRole(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<UserDTO> listUsers() {
            return users;
        }

        @Override
        public UserDTO createUser(UserDTO userDTO) {
            invoiceCalls.incrementAndGet();
            return userDTO;
        }

        @Override
        public UserDTO updateUser(Long id, UserDTO userDTO) {
            invoiceCalls.incrementAndGet();
            return userDTO;
        }

        @Override
        public void deleteUser(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<MessageDTO> listMessages() {
            return messages;
        }

        @Override
        public MessageDTO createMessage(MessageDTO messageDTO) {
            invoiceCalls.incrementAndGet();
            return messageDTO;
        }

        @Override
        public MessageDTO updateMessage(Long id, MessageDTO messageDTO) {
            invoiceCalls.incrementAndGet();
            return messageDTO;
        }

        @Override
        public void deleteMessage(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<CommissionDTO> listCommissions() {
            return commissions;
        }

        @Override
        public CommissionDTO createCommission(CommissionDTO commissionDTO) {
            invoiceCalls.incrementAndGet();
            return commissionDTO;
        }

        @Override
        public CommissionDTO updateCommission(Long id, CommissionDTO commissionDTO) {
            invoiceCalls.incrementAndGet();
            return commissionDTO;
        }

        @Override
        public void deleteCommission(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<InvoiceLineDTO> listInvoiceLines(Long invoiceId) {
            return invoiceLines;
        }

        @Override
        public InvoiceLineDTO createInvoiceLine(InvoiceLineDTO invoiceLineDTO) {
            invoiceCalls.incrementAndGet();
            return invoiceLineDTO;
        }

        @Override
        public InvoiceLineDTO updateInvoiceLine(Long id, InvoiceLineDTO invoiceLineDTO) {
            invoiceCalls.incrementAndGet();
            return invoiceLineDTO;
        }

        @Override
        public void deleteInvoiceLine(Long id) {
            invoiceCalls.incrementAndGet();
        }

        @Override
        public List<DocumentHistoryDTO> invoiceHistory(Long id) {
            invoiceCalls.incrementAndGet();
            return invoiceHistory;
        }

        @Override
        public List<DocumentHistoryDTO> contractHistory(Long id) {
            invoiceCalls.incrementAndGet();
            return contractHistory;
        }

        @Override
        public byte[] downloadClosedInvoicesReport(LocalDate from, LocalDate to, Long agentId) {
            invoiceCalls.incrementAndGet();
            return reportBytes;
        }

        int totalCalls() {
            return invoiceCalls.get();
        }
    }
}
