package com.example.client.controller;

import com.example.client.command.CommandMemento;
import com.example.client.model.AgentModel;
import com.example.client.model.ArticleModel;
import com.example.client.model.CommissionModel;
import com.example.client.model.ContractModel;
import com.example.client.model.CustomerModel;
import com.example.client.model.DataChangeEvent;
import com.example.client.model.DocumentHistoryModel;
import com.example.client.model.DocumentHistorySearchCriteria;
import com.example.client.model.MessageModel;
import com.example.client.model.InvoiceModel;
import com.example.client.model.InvoiceLineModel;
import com.example.client.model.RoleModel;
import com.example.client.model.TeamModel;
import com.example.client.model.UserModel;
import com.example.client.service.AuthSession;
import com.example.client.service.BackendCommunicationException;
import com.example.client.service.BackendServiceException;
import com.example.client.service.DataCacheService;
import com.example.client.service.NotificationService;
import com.example.client.service.SessionExpiredException;
import com.example.client.session.SessionStore;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.ArticleDTO;
import com.example.common.dto.CustomerDTO;
import com.example.common.dto.AgentCommissionDTO;
import com.example.common.dto.AgentDTO;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.MessageDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.dto.NotificationMessage;
import com.example.common.dto.CommissionDTO;
import com.example.common.dto.RoleDTO;
import com.example.common.dto.TeamDTO;
import com.example.common.dto.UserDTO;
import com.example.common.enums.ContractStatus;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.common.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller principale della dashboard JavaFX.
 */
public class MainViewController {

    private final AuthSession session;
    private final SessionStore sessionStore;
    private final DataCacheService dataCacheService;
    private final NotificationService notificationService = new NotificationService();
    private final ObservableList<InvoiceModel> invoiceItems = FXCollections.observableArrayList();
    private final ObservableList<ContractModel> contractItems = FXCollections.observableArrayList();
    private final ObservableList<CustomerModel> customerItems = FXCollections.observableArrayList();
    private final ObservableList<ArticleModel> articleItems = FXCollections.observableArrayList();
    private final ObservableList<InvoiceLineModel> invoiceLineItems = FXCollections.observableArrayList();
    private final ObservableList<AgentModel> agentItems = FXCollections.observableArrayList();
    private final ObservableList<TeamModel> teamItems = FXCollections.observableArrayList();
    private final ObservableList<RoleModel> roleItems = FXCollections.observableArrayList();
    private final ObservableList<UserModel> userItems = FXCollections.observableArrayList();
    private final ObservableList<MessageModel> messageItems = FXCollections.observableArrayList();
    private final ObservableList<CommissionModel> commissionItems = FXCollections.observableArrayList();
    private final ObservableList<DocumentHistoryModel> historyItems = FXCollections.observableArrayList();
    private final ObservableList<DocumentHistoryModel> historySearchItems = FXCollections.observableArrayList();
    private final Observer<NotificationMessage> notificationObserver = this::onNotification;
    private final Observer<CommandMemento> historyObserver = this::onCommandExecuted;
    private final Observer<DataChangeEvent> dataChangeObserver = this::onDataChanged;
    private final DateTimeFormatter historyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private DocumentType currentHistoryType;
    private Long currentHistoryId;
    private boolean updatingStatsYear;
    private int historyCurrentPage;
    private long historyTotalPages;
    private DocumentHistorySearchCriteria historyCurrentCriteria = new DocumentHistorySearchCriteria();
    private boolean sessionExpired;

    public MainViewController() {
        this(null, new SessionStore());
    }

    public MainViewController(AuthSession session, SessionStore sessionStore) {
        this.session = session;
        this.sessionStore = sessionStore;
        this.dataCacheService = DataCacheService.create(sessionStore);
        if (session != null) {
            this.dataCacheService.restoreSession();
        }
    }

    public static MainViewController create(AuthSession session, SessionStore sessionStore) {
        return new MainViewController(session, sessionStore);
    }

    @FXML
    private TabPane mainTabPane;

    @FXML
    private TextField invoiceNumberField;
    @FXML
    private ComboBox<CustomerModel> invoiceCustomerCombo;
    @FXML
    private TextField invoiceContractField;
    @FXML
    private TextField invoiceAmountField;
    @FXML
    private DatePicker invoiceIssueDatePicker;
    @FXML
    private DatePicker invoiceDueDatePicker;
    @FXML
    private ComboBox<InvoiceStatus> invoiceStatusCombo;
    @FXML
    private DatePicker invoicePaymentDatePicker;
    @FXML
    private TextField invoicePaymentAmountField;
    @FXML
    private TextArea invoiceNotesArea;
    @FXML
    private Button invoiceSaveButton;
    @FXML
    private Button invoiceUpdateButton;
    @FXML
    private Button invoiceDeleteButton;
    @FXML
    private Button invoicePayButton;

    @FXML
    private TableView<InvoiceLineModel> invoiceLinesTable;
    @FXML
    private TableColumn<InvoiceLineModel, String> invoiceLineArticleColumn;
    @FXML
    private TableColumn<InvoiceLineModel, String> invoiceLineDescriptionColumn;
    @FXML
    private TableColumn<InvoiceLineModel, BigDecimal> invoiceLineQuantityColumn;
    @FXML
    private TableColumn<InvoiceLineModel, BigDecimal> invoiceLinePriceColumn;
    @FXML
    private TableColumn<InvoiceLineModel, BigDecimal> invoiceLineVatColumn;
    @FXML
    private TableColumn<InvoiceLineModel, BigDecimal> invoiceLineTotalColumn;
    @FXML
    private ComboBox<ArticleModel> invoiceLineArticleCombo;
    @FXML
    private TextField invoiceLineDescriptionField;
    @FXML
    private TextField invoiceLineQuantityField;
    @FXML
    private TextField invoiceLinePriceField;
    @FXML
    private TextField invoiceLineVatField;
    @FXML
    private Button invoiceLineAddButton;
    @FXML
    private Button invoiceLineRemoveButton;

    @FXML
    private TableView<InvoiceModel> invoiceTable;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceNumberColumn;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceCustomerColumn;
    @FXML
    private TableColumn<InvoiceModel, BigDecimal> invoiceAmountColumn;
    @FXML
    private TableColumn<InvoiceModel, String> invoiceStatusColumn;
    @FXML
    private TableColumn<InvoiceModel, LocalDate> invoiceDueDateColumn;

    @FXML
    private PieChart invoiceStatusChart;

    @FXML
    private TableView<DocumentHistoryModel> historyTable;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historyActionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historyDescriptionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historyTimestampColumn;

    @FXML
    private ComboBox<DocumentType> historyTypeCombo;
    @FXML
    private ComboBox<DocumentAction> historyActionCombo;
    @FXML
    private TextField historyDocumentIdField;
    @FXML
    private TextField historySearchField;
    @FXML
    private DatePicker historyFromDatePicker;
    @FXML
    private DatePicker historyToDatePicker;
    @FXML
    private ComboBox<Integer> historyPageSizeCombo;
    @FXML
    private TextField historyAgentIdField;
    @FXML
    private TableView<DocumentHistoryModel> historySearchTable;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchTypeColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchDocumentIdColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchActionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchDescriptionColumn;
    @FXML
    private TableColumn<DocumentHistoryModel, String> historySearchTimestampColumn;
    @FXML
    private Button historyPrevButton;
    @FXML
    private Button historyNextButton;
    @FXML
    private Label historyPageInfoLabel;
    @FXML
    private Label historyTotalLabel;

    @FXML
    private TextField contractAgentField;
    @FXML
    private TextField contractCustomerField;
    @FXML
    private TextField contractDescriptionField;
    @FXML
    private TextField contractValueField;
    @FXML
    private ComboBox<ContractStatus> contractStatusCombo;
    @FXML
    private DatePicker contractStartPicker;
    @FXML
    private DatePicker contractEndPicker;
    @FXML
    private Button contractSaveButton;
    @FXML
    private Button contractUpdateButton;
    @FXML
    private Button contractDeleteButton;

    @FXML
    private TableView<ContractModel> contractTable;
    @FXML
    private TableColumn<ContractModel, String> contractCustomerColumn;
    @FXML
    private TableColumn<ContractModel, String> contractDescriptionColumn;
    @FXML
    private TableColumn<ContractModel, BigDecimal> contractValueColumn;
    @FXML
    private TableColumn<ContractModel, String> contractStatusColumn;
    @FXML
    private TableColumn<ContractModel, LocalDate> contractStartColumn;

    @FXML
    private PieChart contractStatusChart;

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerEmailField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private TextField customerVatField;
    @FXML
    private TextField customerTaxCodeField;
    @FXML
    private TextField customerAddressField;
    @FXML
    private Button customerSaveButton;
    @FXML
    private Button customerUpdateButton;
    @FXML
    private Button customerDeleteButton;
    @FXML
    private TableView<CustomerModel> customerTable;
    @FXML
    private TableColumn<CustomerModel, String> customerNameColumn;
    @FXML
    private TableColumn<CustomerModel, String> customerEmailColumn;
    @FXML
    private TableColumn<CustomerModel, String> customerPhoneColumn;
    @FXML
    private TableColumn<CustomerModel, String> customerVatColumn;

    @FXML
    private TextField articleCodeField;
    @FXML
    private TextField articleNameField;
    @FXML
    private TextArea articleDescriptionArea;
    @FXML
    private TextField articlePriceField;
    @FXML
    private TextField articleVatField;
    @FXML
    private TextField articleUomField;
    @FXML
    private Button articleSaveButton;
    @FXML
    private Button articleUpdateButton;
    @FXML
    private Button articleDeleteButton;
    @FXML
    private TableView<ArticleModel> articleTable;
    @FXML
    private TableColumn<ArticleModel, String> articleCodeColumn;
    @FXML
    private TableColumn<ArticleModel, String> articleNameColumn;
    @FXML
    private TableColumn<ArticleModel, BigDecimal> articlePriceColumn;
    @FXML
    private TableColumn<ArticleModel, BigDecimal> articleVatColumn;

    @FXML
    private TableView<AgentModel> agentTable;
    @FXML
    private TableColumn<AgentModel, String> agentCodeColumn;
    @FXML
    private TableColumn<AgentModel, Long> agentUserIdColumn;
    @FXML
    private TableColumn<AgentModel, String> agentTeamRoleColumn;
    @FXML
    private TextField agentUserIdField;
    @FXML
    private TextField agentCodeField;
    @FXML
    private TextField agentTeamRoleField;
    @FXML
    private Button agentSaveButton;
    @FXML
    private Button agentUpdateButton;
    @FXML
    private Button agentDeleteButton;

    @FXML
    private TableView<TeamModel> teamTable;
    @FXML
    private TableColumn<TeamModel, String> teamNameColumn;
    @FXML
    private TextField teamNameField;
    @FXML
    private Button teamSaveButton;
    @FXML
    private Button teamUpdateButton;
    @FXML
    private Button teamDeleteButton;

    @FXML
    private TableView<RoleModel> roleTable;
    @FXML
    private TableColumn<RoleModel, String> roleNameColumn;
    @FXML
    private TextField roleNameField;
    @FXML
    private Button roleSaveButton;
    @FXML
    private Button roleUpdateButton;
    @FXML
    private Button roleDeleteButton;

    @FXML
    private TableView<UserModel> userTable;
    @FXML
    private TableColumn<UserModel, String> userEmailColumn;
    @FXML
    private TableColumn<UserModel, String> userDisplayNameColumn;
    @FXML
    private TableColumn<UserModel, Long> userRoleIdColumn;
    @FXML
    private TableColumn<UserModel, Long> userTeamIdColumn;
    @FXML
    private TableColumn<UserModel, Boolean> userActiveColumn;
    @FXML
    private TextField userAzureIdField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField userDisplayNameField;
    @FXML
    private TextField userPasswordField;
    @FXML
    private TextField userRoleIdField;
    @FXML
    private TextField userTeamIdField;
    @FXML
    private CheckBox userActiveCheck;
    @FXML
    private Button userSaveButton;
    @FXML
    private Button userUpdateButton;
    @FXML
    private Button userDeleteButton;

    @FXML
    private TableView<MessageModel> messageTable;
    @FXML
    private TableColumn<MessageModel, String> messageConversationColumn;
    @FXML
    private TableColumn<MessageModel, Long> messageSenderColumn;
    @FXML
    private TableColumn<MessageModel, Long> messageTeamColumn;
    @FXML
    private TableColumn<MessageModel, String> messageCreatedColumn;
    @FXML
    private TextField messageConversationField;
    @FXML
    private TextField messageSenderField;
    @FXML
    private TextField messageTeamField;
    @FXML
    private TextArea messageBodyArea;
    @FXML
    private Button messageSaveButton;
    @FXML
    private Button messageUpdateButton;
    @FXML
    private Button messageDeleteButton;

    @FXML
    private TableView<CommissionModel> commissionTable;
    @FXML
    private TableColumn<CommissionModel, Long> commissionAgentColumn;
    @FXML
    private TableColumn<CommissionModel, Long> commissionContractColumn;
    @FXML
    private TableColumn<CommissionModel, BigDecimal> commissionTotalColumn;
    @FXML
    private TableColumn<CommissionModel, BigDecimal> commissionPaidColumn;
    @FXML
    private TableColumn<CommissionModel, BigDecimal> commissionPendingColumn;
    @FXML
    private TableColumn<CommissionModel, String> commissionUpdatedColumn;
    @FXML
    private TextField commissionAgentField;
    @FXML
    private TextField commissionContractField;
    @FXML
    private TextField commissionTotalField;
    @FXML
    private TextField commissionPaidField;
    @FXML
    private TextField commissionPendingField;
    @FXML
    private Button commissionSaveButton;
    @FXML
    private Button commissionUpdateButton;
    @FXML
    private Button commissionDeleteButton;

    @FXML
    private Label notificationLabel;

    @FXML
    private ComboBox<Integer> statsYearCombo;

    @FXML
    private LineChart<String, Number> commissionTrendChart;

    @FXML
    private BarChart<String, Number> agentCommissionBarChart;

    @FXML
    private PieChart teamCommissionPieChart;

    @FXML
    public void initialize() {
        invoiceStatusCombo.getItems().setAll(InvoiceStatus.values());
        contractStatusCombo.getItems().setAll(ContractStatus.values());
        invoicePaymentAmountField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        invoiceAmountField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        contractValueField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        invoiceLineQuantityField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        invoiceLinePriceField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        invoiceLineVatField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        articlePriceField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        articleVatField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        if (commissionTotalField != null) {
            commissionTotalField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        }
        if (commissionPaidField != null) {
            commissionPaidField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        }
        if (commissionPendingField != null) {
            commissionPendingField.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter(), null));
        }

        invoiceCustomerCombo.setItems(customerItems);
        invoiceLineArticleCombo.setItems(articleItems);
        invoiceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        invoiceCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        invoiceAmountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        invoiceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        invoiceDueDateColumn.setCellValueFactory(param -> param.getValue().dueDateProperty());

        invoiceLineArticleColumn.setCellValueFactory(cell -> cell.getValue().articleNameProperty());
        invoiceLineDescriptionColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        invoiceLineQuantityColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty());
        invoiceLinePriceColumn.setCellValueFactory(cell -> cell.getValue().unitPriceProperty());
        invoiceLineVatColumn.setCellValueFactory(cell -> cell.getValue().vatRateProperty());
        invoiceLineTotalColumn.setCellValueFactory(cell -> cell.getValue().totalProperty());

        contractCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        contractDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        contractValueColumn.setCellValueFactory(param -> param.getValue().totalValueProperty());
        contractStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractStartColumn.setCellValueFactory(param -> param.getValue().startDateProperty());

        customerNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        customerEmailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        customerPhoneColumn.setCellValueFactory(cell -> cell.getValue().phoneProperty());
        customerVatColumn.setCellValueFactory(cell -> cell.getValue().vatNumberProperty());

        articleCodeColumn.setCellValueFactory(cell -> cell.getValue().codeProperty());
        articleNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        articlePriceColumn.setCellValueFactory(cell -> cell.getValue().unitPriceProperty());
        articleVatColumn.setCellValueFactory(cell -> cell.getValue().vatRateProperty());

        if (agentTable != null) {
            agentTable.setItems(agentItems);
            agentCodeColumn.setCellValueFactory(new PropertyValueFactory<>("agentCode"));
            agentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
            agentTeamRoleColumn.setCellValueFactory(new PropertyValueFactory<>("teamRole"));
        }

        if (teamTable != null) {
            teamTable.setItems(teamItems);
            teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        if (roleTable != null) {
            roleTable.setItems(roleItems);
            roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        if (userTable != null) {
            userTable.setItems(userItems);
            userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            userDisplayNameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));
            userRoleIdColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));
            userTeamIdColumn.setCellValueFactory(new PropertyValueFactory<>("teamId"));
            userActiveColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        }

        if (messageTable != null) {
            messageTable.setItems(messageItems);
            messageConversationColumn.setCellValueFactory(new PropertyValueFactory<>("conversationId"));
            messageSenderColumn.setCellValueFactory(new PropertyValueFactory<>("senderId"));
            messageTeamColumn.setCellValueFactory(new PropertyValueFactory<>("teamId"));
            messageCreatedColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatInstant(cell.getValue().getCreatedAt())));
        }

        if (commissionTable != null) {
            commissionTable.setItems(commissionItems);
            commissionAgentColumn.setCellValueFactory(new PropertyValueFactory<>("agentId"));
            commissionContractColumn.setCellValueFactory(new PropertyValueFactory<>("contractId"));
            commissionTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalCommission"));
            commissionPaidColumn.setCellValueFactory(new PropertyValueFactory<>("paidCommission"));
            commissionPendingColumn.setCellValueFactory(new PropertyValueFactory<>("pendingCommission"));
            commissionUpdatedColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatInstant(cell.getValue().getLastUpdated())));
        }

        historyActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        historyDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        historyTimestampColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatInstant(cell.getValue().getCreatedAt())));

        if (historyTypeCombo != null) {
            historyTypeCombo.getItems().setAll(DocumentType.values());
        }
        if (historyActionCombo != null) {
            historyActionCombo.getItems().setAll(DocumentAction.values());
        }
        if (historyPageSizeCombo != null) {
            historyPageSizeCombo.getItems().setAll(25, 50, 100);
            historyPageSizeCombo.setValue(25);
        }
        if (historySearchTable != null) {
            historySearchTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
            historySearchDocumentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
            historySearchActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
            historySearchDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            historySearchTimestampColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatInstant(cell.getValue().getCreatedAt())));
            historySearchTable.setItems(historySearchItems);
        }

        invoiceTable.setItems(invoiceItems);
        contractTable.setItems(contractItems);
        historyTable.setItems(historyItems);
        invoiceLinesTable.setItems(invoiceLineItems);
        customerTable.setItems(customerItems);
        articleTable.setItems(articleItems);

        updateHistoryPagination(null);

        invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                clearInvoiceLineForm();
                populateInvoiceForm(selected);
                loadHistory(DocumentType.INVOICE, selected.getId());
            } else {
                invoiceLineItems.clear();
            }
        });

        invoiceLinesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldLine, selected) -> {
            if (selected != null) {
                populateInvoiceLineForm(selected);
            }
        });

        contractTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                populateContractForm(selected);
                loadHistory(DocumentType.CONTRACT, selected.getId());
            }
        });

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                populateCustomerForm(selected);
            }
        });

        articleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                populateArticleForm(selected);
            }
        });

        if (agentTable != null) {
            agentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null) {
                    populateAgentForm(selected);
                }
            });
        }

        if (teamTable != null) {
            teamTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null) {
                    populateTeamForm(selected);
                }
            });
        }

        if (roleTable != null) {
            roleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null) {
                    populateRoleForm(selected);
                }
            });
        }

        if (userTable != null) {
            userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null) {
                    populateUserForm(selected);
                }
            });
        }

        if (messageTable != null) {
            messageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null) {
                    populateMessageForm(selected);
                }
            });
        }

        if (commissionTable != null) {
            commissionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null) {
                    populateCommissionForm(selected);
                }
            });
        }

        if (statsYearCombo != null) {
            statsYearCombo.setOnAction(event -> {
                if (!updatingStatsYear) {
                    refreshStatistics();
                }
            });
        }

        invoiceLineItems.addListener((ListChangeListener<InvoiceLineModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(this::registerInvoiceLineListeners);
                }
            }
            updateInvoiceAmountFromLines();
        });

        invoiceLineArticleCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldArticle, article) -> {
            if (article != null) {
                if (article.getUnitPrice() != null) {
                    invoiceLinePriceField.setText(article.getUnitPrice().toPlainString());
                }
                if (article.getVatRate() != null) {
                    invoiceLineVatField.setText(article.getVatRate().toPlainString());
                }
                if ((invoiceLineDescriptionField.getText() == null || invoiceLineDescriptionField.getText().isBlank())
                        && article.getDescription() != null) {
                    invoiceLineDescriptionField.setText(article.getDescription());
                }
            }
        });

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && Objects.equals(newTab.getText(), "Contratti")) {
                ContractModel selected = contractTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadHistory(DocumentType.CONTRACT, selected.getId());
                } else {
                    historyItems.clear();
                }
            } else {
                InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadHistory(DocumentType.INVOICE, selected.getId());
                } else {
                    historyItems.clear();
                }
            }
        });

        dataCacheService.getCaretaker().subscribe(historyObserver);
        dataCacheService.subscribeDataChanges(dataChangeObserver);
        notificationService.subscribe(notificationObserver);

        refreshData();
    }

    @FXML
    public void refreshData() {
        refreshCustomers();
        refreshArticles();
        refreshInvoices();
        refreshContracts();
        refreshAgents();
        refreshTeams();
        refreshRoles();
        refreshUsers();
        refreshMessages();
        refreshCommissions();
        refreshStatistics();
        refreshHistorySearch(true);
        notificationService.publish(new NotificationMessage("refresh", "Dati aggiornati", Instant.now()));
    }

    @FXML
    public void onCreateInvoice() {
        try {
            InvoiceDTO dto = buildInvoiceFromForm(null);
            InvoiceDTO created = withSession(() -> dataCacheService.createInvoice(dto));
            if (created == null) {
                return;
            }
            refreshInvoices();
            selectInvoice(created.getId());
            notificationService.publish(new NotificationMessage("invoice", "Fattura creata", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onUpdateInvoice() {
        InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una fattura", Instant.now()));
            return;
        }
        try {
            InvoiceDTO dto = buildInvoiceFromForm(selected.getId());
            withSession(() -> dataCacheService.updateInvoice(selected.getId(), dto));
            refreshInvoices();
            selectInvoice(selected.getId());
            notificationService.publish(new NotificationMessage("invoice", "Fattura aggiornata", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onDeleteInvoice() {
        InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una fattura da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteInvoice(selected.getId()));
        refreshInvoices();
        historyItems.clear();
        notificationService.publish(new NotificationMessage("invoice", "Fattura eliminata", Instant.now()));
    }

    @FXML
    public void onRegisterPayment() {
        InvoiceModel selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una fattura", Instant.now()));
            return;
        }
        BigDecimal amountPaid = parseBigDecimal(invoicePaymentAmountField.getText()).orElse(selected.getAmount());
        LocalDate paymentDate = invoicePaymentDatePicker.getValue();
        InvoicePaymentRequest request = new InvoicePaymentRequest(paymentDate, amountPaid);
        withSession(() -> dataCacheService.registerPayment(selected.getId(), request));
        refreshInvoices();
        selectInvoice(selected.getId());
        notificationService.publish(new NotificationMessage("invoice", "Pagamento registrato", Instant.now()));
    }

    @FXML
    public void onAddInvoiceLine() {
        ArticleModel article = invoiceLineArticleCombo.getSelectionModel().getSelectedItem();
        if (article == null || article.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un articolo", Instant.now()));
            return;
        }
        BigDecimal quantity = parseBigDecimal(invoiceLineQuantityField.getText()).orElse(BigDecimal.ONE);
        if (quantity == null || quantity.signum() <= 0) {
            notificationService.publish(new NotificationMessage("error", "Quantità non valida", Instant.now()));
            return;
        }
        BigDecimal price = parseBigDecimal(invoiceLinePriceField.getText())
                .orElse(article.getUnitPrice() != null ? article.getUnitPrice() : BigDecimal.ZERO);
        if (price.signum() < 0) {
            notificationService.publish(new NotificationMessage("error", "Prezzo non valido", Instant.now()));
            return;
        }
        BigDecimal vat = parseBigDecimal(invoiceLineVatField.getText())
                .orElse(article.getVatRate() != null ? article.getVatRate() : BigDecimal.ZERO);
        InvoiceLineModel line = new InvoiceLineModel();
        line.setArticleId(article.getId());
        line.setArticleCode(article.getCode());
        line.setArticleName(article.getName());
        String description = invoiceLineDescriptionField.getText();
        if (description == null || description.isBlank()) {
            description = article.getDescription() != null && !article.getDescription().isBlank()
                    ? article.getDescription()
                    : article.getName();
        }
        line.setDescription(description);
        line.setQuantity(quantity);
        line.setUnitPrice(price);
        line.setVatRate(vat);
        line.recalculateTotal();
        invoiceLineItems.add(line);
        registerInvoiceLineListeners(line);
        clearInvoiceLineForm();
    }

    @FXML
    public void onRemoveInvoiceLine() {
        InvoiceLineModel selected = invoiceLinesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una riga", Instant.now()));
            return;
        }
        invoiceLineItems.remove(selected);
        updateInvoiceAmountFromLines();
        clearInvoiceLineForm();
        notificationService.publish(new NotificationMessage("invoice", "Riga fattura rimossa", Instant.now()));
    }

    @FXML
    public void onCreateCustomer() {
        try {
            CustomerDTO dto = buildCustomerFromForm(null);
            CustomerDTO created = withSession(() -> dataCacheService.createCustomer(dto));
            if (created == null) {
                return;
            }
            refreshCustomers();
            selectCustomer(created.getId());
            notificationService.publish(new NotificationMessage("customer", "Cliente creato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onUpdateCustomer() {
        CustomerModel selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un cliente", Instant.now()));
            return;
        }
        try {
            CustomerDTO dto = buildCustomerFromForm(selected.getId());
            withSession(() -> dataCacheService.updateCustomer(selected.getId(), dto));
            refreshCustomers();
            selectCustomer(selected.getId());
            notificationService.publish(new NotificationMessage("customer", "Cliente aggiornato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onDeleteCustomer() {
        CustomerModel selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un cliente", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteCustomer(selected.getId()));
        refreshCustomers();
        clearCustomerForm();
        notificationService.publish(new NotificationMessage("customer", "Cliente eliminato", Instant.now()));
    }

    @FXML
    public void onCreateArticle() {
        try {
            ArticleDTO dto = buildArticleFromForm(null);
            ArticleDTO created = withSession(() -> dataCacheService.createArticle(dto));
            if (created == null) {
                return;
            }
            refreshArticles();
            selectArticle(created.getId());
            notificationService.publish(new NotificationMessage("article", "Articolo creato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onUpdateArticle() {
        ArticleModel selected = articleTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un articolo", Instant.now()));
            return;
        }
        try {
            ArticleDTO dto = buildArticleFromForm(selected.getId());
            withSession(() -> dataCacheService.updateArticle(selected.getId(), dto));
            refreshArticles();
            selectArticle(selected.getId());
            notificationService.publish(new NotificationMessage("article", "Articolo aggiornato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onDeleteArticle() {
        ArticleModel selected = articleTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un articolo", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteArticle(selected.getId()));
        refreshArticles();
        clearArticleForm();
        notificationService.publish(new NotificationMessage("article", "Articolo eliminato", Instant.now()));
    }

    @FXML
    public void onCreateContract() {
        try {
            ContractDTO dto = buildContractFromForm(null);
            ContractDTO created = withSession(() -> dataCacheService.createContract(dto));
            if (created == null) {
                return;
            }
            refreshContracts();
            selectContract(created.getId());
            notificationService.publish(new NotificationMessage("contract", "Contratto creato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onUpdateContract() {
        ContractModel selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un contratto", Instant.now()));
            return;
        }
        try {
            ContractDTO dto = buildContractFromForm(selected.getId());
            withSession(() -> dataCacheService.updateContract(selected.getId(), dto));
            refreshContracts();
            selectContract(selected.getId());
            notificationService.publish(new NotificationMessage("contract", "Contratto aggiornato", Instant.now()));
        } catch (IllegalArgumentException ex) {
            notifyError(ex.getMessage());
        }
    }

    @FXML
    public void onDeleteContract() {
        ContractModel selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un contratto da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteContract(selected.getId()));
        refreshContracts();
        historyItems.clear();
        notificationService.publish(new NotificationMessage("contract", "Contratto eliminato", Instant.now()));
    }

    @FXML
    public void onCreateAgent() {
        AgentDTO dto = buildAgentFromForm(null);
        AgentDTO created = withSession(() -> dataCacheService.createAgent(dto));
        refreshAgents();
        selectAgent(created != null ? created.getId() : null);
        notificationService.publish(new NotificationMessage("agent", "Agente creato", Instant.now()));
    }

    @FXML
    public void onUpdateAgent() {
        AgentModel selected = agentTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un agente", Instant.now()));
            return;
        }
        AgentDTO dto = buildAgentFromForm(selected.getId());
        withSession(() -> dataCacheService.updateAgent(selected.getId(), dto));
        refreshAgents();
        selectAgent(selected.getId());
        notificationService.publish(new NotificationMessage("agent", "Agente aggiornato", Instant.now()));
    }

    @FXML
    public void onDeleteAgent() {
        AgentModel selected = agentTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un agente da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteAgent(selected.getId()));
        refreshAgents();
        clearAgentForm();
        notificationService.publish(new NotificationMessage("agent", "Agente eliminato", Instant.now()));
    }

    @FXML
    public void onCreateTeam() {
        TeamModel model = buildTeamFromForm(null);
        TeamDTO created = withSession(() -> dataCacheService.createTeam(model.toDto()));
        refreshTeams();
        selectTeam(created != null ? created.getId() : null);
        notificationService.publish(new NotificationMessage("team", "Team creato", Instant.now()));
    }

    @FXML
    public void onUpdateTeam() {
        TeamModel selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un team", Instant.now()));
            return;
        }
        TeamModel model = buildTeamFromForm(selected.getId());
        withSession(() -> dataCacheService.updateTeam(selected.getId(), model.toDto()));
        refreshTeams();
        selectTeam(selected.getId());
        notificationService.publish(new NotificationMessage("team", "Team aggiornato", Instant.now()));
    }

    @FXML
    public void onDeleteTeam() {
        TeamModel selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un team da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteTeam(selected.getId()));
        refreshTeams();
        clearTeamForm();
        notificationService.publish(new NotificationMessage("team", "Team eliminato", Instant.now()));
    }

    @FXML
    public void onCreateRole() {
        RoleModel model = buildRoleFromForm(null);
        RoleDTO created = withSession(() -> dataCacheService.createRole(model.toDto()));
        refreshRoles();
        selectRole(created != null ? created.getId() : null);
        notificationService.publish(new NotificationMessage("role", "Ruolo creato", Instant.now()));
    }

    @FXML
    public void onUpdateRole() {
        RoleModel selected = roleTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un ruolo", Instant.now()));
            return;
        }
        RoleModel model = buildRoleFromForm(selected.getId());
        withSession(() -> dataCacheService.updateRole(selected.getId(), model.toDto()));
        refreshRoles();
        selectRole(selected.getId());
        notificationService.publish(new NotificationMessage("role", "Ruolo aggiornato", Instant.now()));
    }

    @FXML
    public void onDeleteRole() {
        RoleModel selected = roleTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un ruolo da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteRole(selected.getId()));
        refreshRoles();
        clearRoleForm();
        notificationService.publish(new NotificationMessage("role", "Ruolo eliminato", Instant.now()));
    }

    @FXML
    public void onCreateUser() {
        UserModel model = buildUserFromForm(null);
        UserDTO created = withSession(() -> dataCacheService.createUser(model.toDto()));
        refreshUsers();
        selectUser(created != null ? created.getId() : null);
        notificationService.publish(new NotificationMessage("user", "Utente creato", Instant.now()));
    }

    @FXML
    public void onUpdateUser() {
        UserModel selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un utente", Instant.now()));
            return;
        }
        UserModel model = buildUserFromForm(selected.getId());
        withSession(() -> dataCacheService.updateUser(selected.getId(), model.toDto()));
        refreshUsers();
        selectUser(selected.getId());
        notificationService.publish(new NotificationMessage("user", "Utente aggiornato", Instant.now()));
    }

    @FXML
    public void onDeleteUser() {
        UserModel selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un utente da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteUser(selected.getId()));
        refreshUsers();
        clearUserForm();
        notificationService.publish(new NotificationMessage("user", "Utente eliminato", Instant.now()));
    }

    @FXML
    public void onCreateMessage() {
        MessageModel model = buildMessageFromForm(null);
        MessageDTO created = withSession(() -> dataCacheService.createMessage(model.toDto()));
        refreshMessages();
        selectMessage(created != null ? created.getId() : null);
        notificationService.publish(new NotificationMessage("message", "Messaggio creato", Instant.now()));
    }

    @FXML
    public void onUpdateMessage() {
        MessageModel selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un messaggio", Instant.now()));
            return;
        }
        MessageModel model = buildMessageFromForm(selected.getId());
        withSession(() -> dataCacheService.updateMessage(selected.getId(), model.toDto()));
        refreshMessages();
        selectMessage(selected.getId());
        notificationService.publish(new NotificationMessage("message", "Messaggio aggiornato", Instant.now()));
    }

    @FXML
    public void onDeleteMessage() {
        MessageModel selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona un messaggio da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteMessage(selected.getId()));
        refreshMessages();
        clearMessageForm();
        notificationService.publish(new NotificationMessage("message", "Messaggio eliminato", Instant.now()));
    }

    @FXML
    public void onCreateCommission() {
        CommissionModel model = buildCommissionFromForm(null);
        CommissionDTO created = withSession(() -> dataCacheService.createCommission(model.toDto()));
        refreshCommissions();
        selectCommission(created != null ? created.getId() : null);
        notificationService.publish(new NotificationMessage("commission", "Commissione creata", Instant.now()));
    }

    @FXML
    public void onUpdateCommission() {
        CommissionModel selected = commissionTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una commissione", Instant.now()));
            return;
        }
        CommissionModel model = buildCommissionFromForm(selected.getId());
        withSession(() -> dataCacheService.updateCommission(selected.getId(), model.toDto()));
        refreshCommissions();
        selectCommission(selected.getId());
        notificationService.publish(new NotificationMessage("commission", "Commissione aggiornata", Instant.now()));
    }

    @FXML
    public void onDeleteCommission() {
        CommissionModel selected = commissionTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            notificationService.publish(new NotificationMessage("warn", "Seleziona una commissione da eliminare", Instant.now()));
            return;
        }
        withSession(() -> dataCacheService.deleteCommission(selected.getId()));
        refreshCommissions();
        clearCommissionForm();
        notificationService.publish(new NotificationMessage("commission", "Commissione eliminata", Instant.now()));
    }

    @FXML
    public void onApplyHistoryFilters() {
        refreshHistorySearch(true);
        notificationService.publish(new NotificationMessage("history", "Filtri applicati", Instant.now()));
    }

    @FXML
    public void onResetHistoryFilters() {
        if (historyTypeCombo != null) {
            historyTypeCombo.setValue(null);
        }
        if (historyActionCombo != null) {
            historyActionCombo.setValue(null);
        }
        if (historyDocumentIdField != null) {
            historyDocumentIdField.clear();
        }
        if (historySearchField != null) {
            historySearchField.clear();
        }
        if (historyFromDatePicker != null) {
            historyFromDatePicker.setValue(null);
        }
        if (historyToDatePicker != null) {
            historyToDatePicker.setValue(null);
        }
        if (historyAgentIdField != null) {
            historyAgentIdField.clear();
        }
        if (historyPageSizeCombo != null) {
            historyPageSizeCombo.setValue(25);
        }
        refreshHistorySearch(true);
        notificationService.publish(new NotificationMessage("history", "Filtri ripristinati", Instant.now()));
    }

    @FXML
    public void onHistoryPrevPage() {
        if (historyCurrentPage > 0) {
            historyCurrentPage--;
            refreshHistorySearch(false);
        }
    }

    @FXML
    public void onHistoryNextPage() {
        if (historyCurrentPage + 1 < historyTotalPages) {
            historyCurrentPage++;
            refreshHistorySearch(false);
        }
    }

    @FXML
    public void onHistoryExportCsv() {
        DocumentHistorySearchCriteria criteria = historyCurrentCriteria != null ? historyCurrentCriteria : buildHistoryCriteria();
        byte[] csv = withSession(() -> dataCacheService.exportDocumentHistory(criteria));
        if (csv == null) {
            return;
        }
        saveToFile(csv, "storico-documenti.csv", "csv", "File CSV");
    }

    @FXML
    public void onHistoryDownloadPdf() {
        Long agentId = parseLong(historyAgentIdField != null ? historyAgentIdField.getText() : null).orElse(null);
        LocalDate from = historyFromDatePicker != null ? historyFromDatePicker.getValue() : null;
        LocalDate to = historyToDatePicker != null ? historyToDatePicker.getValue() : null;
        byte[] pdf = withSession(() -> dataCacheService.downloadClosedInvoiceReport(from, to, agentId));
        if (pdf == null) {
            return;
        }
        saveToFile(pdf, "report-fatture-chiuse.pdf", "pdf", "Documento PDF");
    }

    private void refreshCustomers() {
        Long selectedCustomerId = Optional.ofNullable(invoiceCustomerCombo.getSelectionModel().getSelectedItem())
                .map(CustomerModel::getId)
                .orElse(null);
        List<CustomerDTO> dtos = withSession(dataCacheService::getCustomers);
        if (dtos == null) {
            return;
        }
        customerItems.setAll(dtos.stream().map(CustomerModel::fromDto).toList());
        if (selectedCustomerId != null) {
            customerItems.stream()
                    .filter(item -> selectedCustomerId.equals(item.getId()))
                    .findFirst()
                    .ifPresent(invoiceCustomerCombo.getSelectionModel()::select);
        }
    }

    private void refreshArticles() {
        Long selectedArticleId = Optional.ofNullable(invoiceLineArticleCombo.getSelectionModel().getSelectedItem())
                .map(ArticleModel::getId)
                .orElse(null);
        List<ArticleDTO> dtos = withSession(dataCacheService::getArticles);
        if (dtos == null) {
            return;
        }
        articleItems.setAll(dtos.stream().map(ArticleModel::fromDto).toList());
        if (selectedArticleId != null) {
            articleItems.stream()
                    .filter(item -> selectedArticleId.equals(item.getId()))
                    .findFirst()
                    .ifPresent(invoiceLineArticleCombo.getSelectionModel()::select);
        }
    }

    private void refreshInvoices() {
        List<InvoiceDTO> dtos = withSession(dataCacheService::getInvoices);
        if (dtos == null) {
            return;
        }
        invoiceItems.setAll(dtos.stream().map(InvoiceModel::fromDto).toList());
        updateInvoiceChart();
    }

    private void refreshContracts() {
        List<ContractDTO> dtos = withSession(dataCacheService::getContracts);
        if (dtos == null) {
            return;
        }
        contractItems.setAll(dtos.stream().map(ContractModel::fromDto).toList());
        updateContractChart();
    }

    private void refreshAgents() {
        List<AgentDTO> dtos = withSession(dataCacheService::getAgents);
        if (dtos != null) {
            agentItems.setAll(dtos.stream().map(AgentModel::fromDto).toList());
        }
    }

    private void refreshTeams() {
        List<TeamDTO> dtos = withSession(dataCacheService::getTeams);
        if (dtos != null) {
            teamItems.setAll(dtos.stream().map(TeamModel::fromDto).toList());
        }
    }

    private void refreshRoles() {
        List<RoleDTO> dtos = withSession(dataCacheService::getRoles);
        if (dtos != null) {
            roleItems.setAll(dtos.stream().map(RoleModel::fromDto).toList());
        }
    }

    private void refreshUsers() {
        List<UserDTO> dtos = withSession(dataCacheService::getUsers);
        if (dtos != null) {
            userItems.setAll(dtos.stream().map(UserModel::fromDto).toList());
        }
    }

    private void refreshMessages() {
        List<MessageDTO> dtos = withSession(dataCacheService::getMessages);
        if (dtos != null) {
            messageItems.setAll(dtos.stream().map(MessageModel::fromDto).toList());
        }
    }

    private void refreshCommissions() {
        List<CommissionDTO> dtos = withSession(dataCacheService::getCommissions);
        if (dtos != null) {
            commissionItems.setAll(dtos.stream().map(CommissionModel::fromDto).toList());
        }
    }

    private void refreshHistorySearch(boolean resetPage) {
        if (historySearchTable == null) {
            return;
        }
        if (resetPage) {
            historyCurrentPage = 0;
        }
        DocumentHistorySearchCriteria criteria = buildHistoryCriteria();
        historyCurrentCriteria = criteria;
        int size = historyPageSizeCombo != null && historyPageSizeCombo.getValue() != null ? historyPageSizeCombo.getValue() : 25;
        DocumentHistoryPageDTO page = withSession(() -> dataCacheService.searchDocumentHistory(criteria, historyCurrentPage, size));
        if (page == null || page.getItems() == null) {
            historySearchItems.clear();
            updateHistoryPagination(null);
            return;
        }
        historyCurrentPage = page.getPage();
        historySearchItems.setAll(page.getItems().stream().map(DocumentHistoryModel::fromDto).toList());
        updateHistoryPagination(page);
    }

    private void refreshStatistics() {
        AgentStatisticsDTO agentStats = withSession(() -> dataCacheService.getAgentStatistics(statsYearCombo != null ? statsYearCombo.getValue() : null));
        if (agentStats == null) {
            return;
        }
        if (statsYearCombo != null) {
            updatingStatsYear = true;
            statsYearCombo.getItems().setAll(agentStats.years());
            statsYearCombo.setValue(agentStats.year());
            updatingStatsYear = false;
        }
        updateCommissionTrendChart(agentStats);
        updateAgentBarChart(agentStats);
        TeamStatisticsDTO teamStats = withSession(() -> dataCacheService.getTeamStatistics(agentStats.year()));
        if (teamStats == null) {
            return;
        }
        updateTeamPieChart(teamStats);
    }

    private void updateInvoiceChart() {
        Map<String, Long> counts = invoiceItems.stream()
                .collect(Collectors.groupingBy(item -> Optional.ofNullable(item.getStatus()).orElse("N/D"), Collectors.counting()));
        invoiceStatusChart.setData(FXCollections.observableArrayList(counts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .toList()));
    }

    private void updateContractChart() {
        Map<String, Long> counts = contractItems.stream()
                .collect(Collectors.groupingBy(item -> Optional.ofNullable(item.getStatus()).orElse("N/D"), Collectors.counting()));
        contractStatusChart.setData(FXCollections.observableArrayList(counts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .toList()));
    }

    private void updateCommissionTrendChart(AgentStatisticsDTO statistics) {
        if (commissionTrendChart == null) {
            return;
        }
        commissionTrendChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Provvigioni");
        statistics.monthlyTotals().stream()
                .sorted((a, b) -> Integer.compare(a.month(), b.month()))
                .forEach(entry -> series.getData().add(new XYChart.Data<>(monthLabel(entry.month()), entry.commission().doubleValue())));
        commissionTrendChart.getData().add(series);
    }

    private void updateAgentBarChart(AgentStatisticsDTO statistics) {
        if (agentCommissionBarChart == null) {
            return;
        }
        agentCommissionBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Provvigioni");
        for (AgentCommissionDTO aggregate : statistics.agentTotals()) {
            String label = aggregate.teamName() != null && !aggregate.teamName().isBlank()
                    ? aggregate.agentName() + " (" + aggregate.teamName() + ")"
                    : aggregate.agentName();
            series.getData().add(new XYChart.Data<>(label, aggregate.commission().doubleValue()));
        }
        agentCommissionBarChart.getData().add(series);
    }

    private void updateTeamPieChart(TeamStatisticsDTO statistics) {
        if (teamCommissionPieChart == null) {
            return;
        }
        teamCommissionPieChart.setData(FXCollections.observableArrayList(statistics.teamTotals().stream()
                .map(team -> new PieChart.Data(team.teamName() != null && !team.teamName().isBlank() ? team.teamName() : "Senza team",
                        team.commission().doubleValue()))
                .toList()));
    }

    private InvoiceDTO buildInvoiceFromForm(Long id) {
        CustomerModel customer = invoiceCustomerCombo.getSelectionModel().getSelectedItem();
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Il cliente è obbligatorio");
        }
        LocalDate issueDate = invoiceIssueDatePicker.getValue();
        if (issueDate == null) {
            throw new IllegalArgumentException("La data di emissione è obbligatoria");
        }
        Long contractId = parseLong(invoiceContractField.getText()).orElse(null);
        InvoiceStatus status = invoiceStatusCombo.getValue() != null ? invoiceStatusCombo.getValue() : InvoiceStatus.DRAFT;
        List<InvoiceLineDTO> lines = invoiceLineItems.stream()
                .map(InvoiceLineModel::toDto)
                .toList();
        BigDecimal amount;
        if (!lines.isEmpty()) {
            amount = invoiceLineItems.stream()
                    .map(InvoiceLineModel::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            invoiceAmountField.setText(amount.toPlainString());
        } else {
            amount = parseBigDecimal(invoiceAmountField.getText())
                    .orElseThrow(() -> new IllegalArgumentException("Importo non valido"));
        }
        return new InvoiceDTO(id,
                invoiceNumberField.getText(),
                contractId,
                customer.getId(),
                customer.getName(),
                amount,
                issueDate,
                invoiceDueDatePicker.getValue(),
                status,
                invoicePaymentDatePicker.getValue(),
                invoiceNotesArea.getText(),
                lines);
    }

    private ContractDTO buildContractFromForm(Long id) {
        Long agentId = parseLong(contractAgentField.getText())
                .orElseThrow(() -> new IllegalArgumentException("Agente non valido"));
        String customer = contractCustomerField.getText();
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Il cliente è obbligatorio");
        }
        BigDecimal value = parseBigDecimal(contractValueField.getText())
                .orElseThrow(() -> new IllegalArgumentException("Valore non valido"));
        LocalDate start = contractStartPicker.getValue();
        if (start == null) {
            throw new IllegalArgumentException("La data di inizio è obbligatoria");
        }
        return new ContractDTO(id,
                agentId,
                customer,
                contractDescriptionField.getText(),
                start,
                contractEndPicker.getValue(),
                value,
                contractStatusCombo.getValue() != null ? contractStatusCombo.getValue() : ContractStatus.DRAFT);
    }

    private AgentDTO buildAgentFromForm(Long id) {
        Long userId = parseLong(agentUserIdField.getText()).orElse(null);
        String code = normalize(agentCodeField.getText());
        String role = normalize(agentTeamRoleField.getText());
        return new AgentDTO(id, userId, code, role);
    }

    private TeamModel buildTeamFromForm(Long id) {
        TeamModel model = new TeamModel();
        model.setId(id);
        model.setName(normalize(teamNameField.getText()));
        return model;
    }

    private RoleModel buildRoleFromForm(Long id) {
        RoleModel model = new RoleModel();
        model.setId(id);
        model.setName(normalize(roleNameField.getText()));
        return model;
    }

    private UserModel buildUserFromForm(Long id) {
        UserModel model = new UserModel();
        model.setId(id);
        model.setAzureId(normalize(userAzureIdField.getText()));
        model.setEmail(normalize(userEmailField.getText()));
        model.setDisplayName(normalize(userDisplayNameField.getText()));
        model.setPassword(normalize(userPasswordField.getText()));
        model.setRoleId(parseLong(userRoleIdField.getText()).orElse(null));
        model.setTeamId(parseLong(userTeamIdField.getText()).orElse(null));
        model.setActive(userActiveCheck != null && userActiveCheck.isSelected());
        model.setCreatedAt(java.time.LocalDateTime.now());
        return model;
    }

    private MessageModel buildMessageFromForm(Long id) {
        MessageModel model = new MessageModel();
        model.setId(id);
        model.setConversationId(normalize(messageConversationField.getText()));
        model.setSenderId(parseLong(messageSenderField.getText()).orElse(null));
        model.setTeamId(parseLong(messageTeamField.getText()).orElse(null));
        model.setBody(messageBodyArea.getText());
        model.setCreatedAt(Instant.now());
        return model;
    }

    private CommissionModel buildCommissionFromForm(Long id) {
        CommissionModel model = new CommissionModel();
        model.setId(id);
        model.setAgentId(parseLong(commissionAgentField.getText()).orElse(null));
        model.setContractId(parseLong(commissionContractField.getText()).orElse(null));
        model.setTotalCommission(parseBigDecimal(commissionTotalField.getText()).orElse(BigDecimal.ZERO));
        model.setPaidCommission(parseBigDecimal(commissionPaidField.getText()).orElse(BigDecimal.ZERO));
        model.setPendingCommission(parseBigDecimal(commissionPendingField.getText()).orElse(BigDecimal.ZERO));
        model.setLastUpdated(Instant.now());
        return model;
    }

    private void populateInvoiceForm(InvoiceModel model) {
        invoiceNumberField.setText(model.getNumber());
        if (model.getCustomerId() != null) {
            customerItems.stream()
                    .filter(item -> model.getCustomerId().equals(item.getId()))
                    .findFirst()
                    .ifPresent(invoiceCustomerCombo.getSelectionModel()::select);
        } else {
            invoiceCustomerCombo.getSelectionModel().clearSelection();
        }
        invoiceContractField.setText(model.getContractId() != null ? model.getContractId().toString() : "");
        invoiceAmountField.setText(model.getAmount() != null ? model.getAmount().toPlainString() : "");
        invoiceIssueDatePicker.setValue(model.getIssueDate());
        invoiceDueDatePicker.setValue(model.getDueDate());
        invoiceStatusCombo.setValue(model.getStatus() != null ? InvoiceStatus.valueOf(model.getStatus()) : null);
        invoicePaymentDatePicker.setValue(model.getPaymentDate());
        invoicePaymentAmountField.setText(model.getAmount() != null ? model.getAmount().toPlainString() : "");
        invoiceNotesArea.setText(model.getNotes());
        List<InvoiceLineModel> lines = model.getLines().stream()
                .map(InvoiceLineModel::fromDto)
                .toList();
        lines.forEach(line -> {
            articleItems.stream()
                    .filter(article -> line.getArticleId() != null && line.getArticleId().equals(article.getId()))
                    .findFirst()
                    .ifPresent(article -> {
                        line.setArticleName(article.getName());
                        if (line.getArticleCode() == null || line.getArticleCode().isBlank()) {
                            line.setArticleCode(article.getCode());
                        }
                    });
            registerInvoiceLineListeners(line);
            line.recalculateTotal();
        });
        invoiceLineItems.setAll(lines);
        updateInvoiceAmountFromLines();
    }

    private void populateContractForm(ContractModel model) {
        contractAgentField.setText(model.getAgentId() != null ? model.getAgentId().toString() : "");
        contractCustomerField.setText(model.getCustomerName());
        contractDescriptionField.setText(model.getDescription());
        contractValueField.setText(model.getTotalValue() != null ? model.getTotalValue().toPlainString() : "");
        contractStatusCombo.setValue(model.getStatus() != null ? ContractStatus.valueOf(model.getStatus()) : null);
        contractStartPicker.setValue(model.getStartDate());
        contractEndPicker.setValue(model.getEndDate());
    }

    private void populateCustomerForm(CustomerModel model) {
        customerNameField.setText(model.getName());
        customerEmailField.setText(model.getEmail());
        customerPhoneField.setText(model.getPhone());
        customerVatField.setText(model.getVatNumber());
        customerTaxCodeField.setText(model.getTaxCode());
        customerAddressField.setText(model.getAddress());
    }

    private void clearCustomerForm() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
        customerVatField.clear();
        customerTaxCodeField.clear();
        customerAddressField.clear();
        customerTable.getSelectionModel().clearSelection();
    }

    private void populateArticleForm(ArticleModel model) {
        articleCodeField.setText(model.getCode());
        articleNameField.setText(model.getName());
        articleDescriptionArea.setText(model.getDescription());
        articlePriceField.setText(model.getUnitPrice() != null ? model.getUnitPrice().toPlainString() : "");
        articleVatField.setText(model.getVatRate() != null ? model.getVatRate().toPlainString() : "");
        articleUomField.setText(model.getUnitOfMeasure());
    }

    private void clearArticleForm() {
        articleCodeField.clear();
        articleNameField.clear();
        articleDescriptionArea.clear();
        articlePriceField.clear();
        articleVatField.clear();
        articleUomField.clear();
        articleTable.getSelectionModel().clearSelection();
    }

    private void populateAgentForm(AgentModel model) {
        agentUserIdField.setText(model.getUserId() != null ? model.getUserId().toString() : "");
        agentCodeField.setText(model.getAgentCode());
        agentTeamRoleField.setText(model.getTeamRole());
    }

    private void clearAgentForm() {
        agentUserIdField.clear();
        agentCodeField.clear();
        agentTeamRoleField.clear();
        if (agentTable != null) {
            agentTable.getSelectionModel().clearSelection();
        }
    }

    private void populateTeamForm(TeamModel model) {
        teamNameField.setText(model.getName());
    }

    private void clearTeamForm() {
        teamNameField.clear();
        if (teamTable != null) {
            teamTable.getSelectionModel().clearSelection();
        }
    }

    private void populateRoleForm(RoleModel model) {
        roleNameField.setText(model.getName());
    }

    private void clearRoleForm() {
        roleNameField.clear();
        if (roleTable != null) {
            roleTable.getSelectionModel().clearSelection();
        }
    }

    private void populateUserForm(UserModel model) {
        userAzureIdField.setText(model.getAzureId());
        userEmailField.setText(model.getEmail());
        userDisplayNameField.setText(model.getDisplayName());
        userPasswordField.setText(model.getPassword());
        userRoleIdField.setText(model.getRoleId() != null ? model.getRoleId().toString() : "");
        userTeamIdField.setText(model.getTeamId() != null ? model.getTeamId().toString() : "");
        if (userActiveCheck != null && model.activeProperty() != null) {
            userActiveCheck.setSelected(model.isActive());
        }
    }

    private void clearUserForm() {
        userAzureIdField.clear();
        userEmailField.clear();
        userDisplayNameField.clear();
        userPasswordField.clear();
        userRoleIdField.clear();
        userTeamIdField.clear();
        if (userActiveCheck != null) {
            userActiveCheck.setSelected(false);
        }
        if (userTable != null) {
            userTable.getSelectionModel().clearSelection();
        }
    }

    private void populateMessageForm(MessageModel model) {
        messageConversationField.setText(model.getConversationId());
        messageSenderField.setText(model.getSenderId() != null ? model.getSenderId().toString() : "");
        messageTeamField.setText(model.getTeamId() != null ? model.getTeamId().toString() : "");
        messageBodyArea.setText(model.getBody());
    }

    private void clearMessageForm() {
        messageConversationField.clear();
        messageSenderField.clear();
        messageTeamField.clear();
        messageBodyArea.clear();
        if (messageTable != null) {
            messageTable.getSelectionModel().clearSelection();
        }
    }

    private void populateCommissionForm(CommissionModel model) {
        commissionAgentField.setText(model.getAgentId() != null ? model.getAgentId().toString() : "");
        commissionContractField.setText(model.getContractId() != null ? model.getContractId().toString() : "");
        commissionTotalField.setText(model.getTotalCommission() != null ? model.getTotalCommission().toPlainString() : "");
        commissionPaidField.setText(model.getPaidCommission() != null ? model.getPaidCommission().toPlainString() : "");
        commissionPendingField.setText(model.getPendingCommission() != null ? model.getPendingCommission().toPlainString() : "");
    }

    private void clearCommissionForm() {
        commissionAgentField.clear();
        commissionContractField.clear();
        commissionTotalField.clear();
        commissionPaidField.clear();
        commissionPendingField.clear();
        if (commissionTable != null) {
            commissionTable.getSelectionModel().clearSelection();
        }
    }

    private void loadHistory(DocumentType type, Long documentId) {
        if (documentId == null) {
            historyItems.clear();
            return;
        }
        currentHistoryType = type;
        currentHistoryId = documentId;
        List<DocumentHistoryDTO> entries = withSession(() -> switch (type) {
            case INVOICE -> dataCacheService.getInvoiceHistory(documentId);
            case CONTRACT -> dataCacheService.getContractHistory(documentId);
        });
        if (entries == null) {
            historyItems.clear();
            return;
        }
        historyItems.setAll(entries.stream().map(DocumentHistoryModel::fromDto).toList());
    }

    private void selectInvoice(Long id) {
        if (id == null) {
            return;
        }
        invoiceTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> invoiceTable.getSelectionModel().select(item));
    }

    private void selectContract(Long id) {
        if (id == null) {
            return;
        }
        contractTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> contractTable.getSelectionModel().select(item));
    }

    private void selectCustomer(Long id) {
        if (id == null) {
            return;
        }
        customerTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> customerTable.getSelectionModel().select(item));
    }

    private void selectArticle(Long id) {
        if (id == null) {
            return;
        }
        articleTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> articleTable.getSelectionModel().select(item));
    }

    private void selectAgent(Long id) {
        if (id == null || agentTable == null) {
            return;
        }
        agentTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> agentTable.getSelectionModel().select(item));
    }

    private void selectTeam(Long id) {
        if (id == null || teamTable == null) {
            return;
        }
        teamTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> teamTable.getSelectionModel().select(item));
    }

    private void selectRole(Long id) {
        if (id == null || roleTable == null) {
            return;
        }
        roleTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> roleTable.getSelectionModel().select(item));
    }

    private void selectUser(Long id) {
        if (id == null || userTable == null) {
            return;
        }
        userTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> userTable.getSelectionModel().select(item));
    }

    private void selectMessage(Long id) {
        if (id == null || messageTable == null) {
            return;
        }
        messageTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> messageTable.getSelectionModel().select(item));
    }

    private void selectCommission(Long id) {
        if (id == null || commissionTable == null) {
            return;
        }
        commissionTable.getItems().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> commissionTable.getSelectionModel().select(item));
    }

    private void onNotification(NotificationMessage notification) {
        if (notificationLabel != null) {
            notificationLabel.setText(notification.getPayload());
        }
    }

    private void onDataChanged(DataChangeEvent event) {
        if (event == null) {
            return;
        }
        switch (event.type()) {
            case INVOICE -> refreshStatistics();
            case CUSTOMER -> refreshCustomers();
            case ARTICLE -> refreshArticles();
            case CONTRACT -> {
            }
            case AGENT -> refreshAgents();
            case TEAM -> refreshTeams();
            case ROLE -> refreshRoles();
            case USER -> refreshUsers();
            case MESSAGE -> refreshMessages();
            case COMMISSION -> refreshCommissions();
        }
    }

    private void onCommandExecuted(CommandMemento memento) {
        if (memento == null || memento.getResult() == null || memento.getResult().historySnapshot().isEmpty()) {
            return;
        }
        if (Objects.equals(currentHistoryType, memento.getResult().documentType())
                && Objects.equals(currentHistoryId, memento.getResult().documentId())) {
            historyItems.setAll(memento.getResult().historySnapshot().stream()
                    .map(DocumentHistoryModel::fromDto)
                    .toList());
        }
    }

    public void shutdown() {
        notificationService.unsubscribe(notificationObserver);
        dataCacheService.unsubscribeDataChanges(dataChangeObserver);
        dataCacheService.getCaretaker().unsubscribe(historyObserver);
    }

    private DocumentHistorySearchCriteria buildHistoryCriteria() {
        DocumentHistorySearchCriteria criteria = new DocumentHistorySearchCriteria();
        if (historyTypeCombo != null) {
            criteria.setDocumentType(historyTypeCombo.getValue());
        }
        if (historyDocumentIdField != null) {
            criteria.setDocumentId(parseLong(historyDocumentIdField.getText()).orElse(null));
        }
        if (historyActionCombo != null && historyActionCombo.getValue() != null) {
            criteria.setActions(List.of(historyActionCombo.getValue()));
        } else {
            criteria.setActions(List.of());
        }
        criteria.setFrom(startOfDay(historyFromDatePicker != null ? historyFromDatePicker.getValue() : null));
        criteria.setTo(endOfDay(historyToDatePicker != null ? historyToDatePicker.getValue() : null));
        if (historySearchField != null) {
            String text = historySearchField.getText();
            criteria.setSearchText(text != null && !text.isBlank() ? text.trim() : null);
        }
        return criteria;
    }

    private void updateHistoryPagination(DocumentHistoryPageDTO page) {
        if (historyPageInfoLabel == null || historyTotalLabel == null) {
            return;
        }
        if (page == null) {
            historyTotalPages = 0;
            historyPageInfoLabel.setText("Pagina 0 di 0");
            historyTotalLabel.setText("0 risultati");
            if (historyPrevButton != null) {
                historyPrevButton.setDisable(true);
            }
            if (historyNextButton != null) {
                historyNextButton.setDisable(true);
            }
            return;
        }
        historyTotalPages = Math.max(page.getTotalPages(), 1);
        historyPageInfoLabel.setText(String.format("Pagina %d di %d", page.getPage() + 1, page.getTotalPages()));
        historyTotalLabel.setText(page.getTotalElements() + " risultati");
        if (historyPrevButton != null) {
            historyPrevButton.setDisable(!page.hasPrevious());
        }
        if (historyNextButton != null) {
            historyNextButton.setDisable(!page.hasNext());
        }
    }

    private <T> T withSession(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (SessionExpiredException ex) {
            handleSessionExpired(ex.getMessage());
            return null;
        } catch (BackendServiceException | BackendCommunicationException ex) {
            notifyError(ex.getMessage());
            return null;
        }
    }

    private void withSession(Runnable runnable) {
        withSession(() -> {
            runnable.run();
            return null;
        });
    }

    private void notifyError(String message) {
        notificationService.publish(new NotificationMessage("error", message, Instant.now()));
        AlertUtils.showError(message);
    }

    private void handleSessionExpired(String message) {
        if (sessionExpired) {
            return;
        }
        sessionExpired = true;
        dataCacheService.invalidateHistory();
        dataCacheService.invalidateStatistics();
        notificationService.publish(new NotificationMessage("session", message, Instant.now()));
        showLoginPrompt(message);
    }

    private void showLoginPrompt(String reason) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/LoginView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == LoginController.class) {
                    String prefilledEmail = session != null && session.user() != null ? session.user().email() : null;
                    return LoginController.create(sessionStore, prefilledEmail, reason, "-fx-text-fill: #c62828; -fx-font-weight: bold;");
                }
                throw new IllegalStateException("Controller non supportato: " + type.getName());
            });
            Parent root = loader.load();
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestore Agenti - Login");
        } catch (IOException e) {
            notifyError("Impossibile aprire la schermata di login: " + e.getMessage());
        }
    }

    private Optional<BigDecimal> parseBigDecimal(String value) {
        try {
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(new BigDecimal(value));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Optional<Long> parseLong(String value) {
        try {
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private CustomerDTO buildCustomerFromForm(Long id) {
        String name = customerNameField.getText();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome del cliente è obbligatorio");
        }
        return new CustomerDTO(id,
                name.trim(),
                normalize(customerVatField.getText()),
                normalize(customerTaxCodeField.getText()),
                normalize(customerEmailField.getText()),
                normalize(customerPhoneField.getText()),
                normalize(customerAddressField.getText()),
                null,
                null);
    }

    private ArticleDTO buildArticleFromForm(Long id) {
        String name = articleNameField.getText();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'articolo è obbligatorio");
        }
        BigDecimal price = parseBigDecimal(articlePriceField.getText()).orElse(BigDecimal.ZERO);
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Prezzo non valido");
        }
        BigDecimal vat = parseBigDecimal(articleVatField.getText()).orElse(BigDecimal.ZERO);
        return new ArticleDTO(id,
                normalize(articleCodeField.getText()),
                name.trim(),
                normalize(articleDescriptionArea.getText()),
                price,
                vat,
                normalize(articleUomField.getText()),
                null,
                null);
    }

    private void populateInvoiceLineForm(InvoiceLineModel line) {
        if (line.getArticleId() != null) {
            articleItems.stream()
                    .filter(article -> line.getArticleId().equals(article.getId()))
                    .findFirst()
                    .ifPresent(invoiceLineArticleCombo.getSelectionModel()::select);
        } else {
            invoiceLineArticleCombo.getSelectionModel().clearSelection();
        }
        invoiceLineDescriptionField.setText(line.getDescription());
        invoiceLineQuantityField.setText(line.getQuantity() != null ? line.getQuantity().toPlainString() : "");
        invoiceLinePriceField.setText(line.getUnitPrice() != null ? line.getUnitPrice().toPlainString() : "");
        invoiceLineVatField.setText(line.getVatRate() != null ? line.getVatRate().toPlainString() : "");
    }

    private void clearInvoiceLineForm() {
        invoiceLineArticleCombo.getSelectionModel().clearSelection();
        invoiceLineDescriptionField.clear();
        invoiceLineQuantityField.clear();
        invoiceLinePriceField.clear();
        invoiceLineVatField.clear();
        invoiceLinesTable.getSelectionModel().clearSelection();
    }

    private void registerInvoiceLineListeners(InvoiceLineModel line) {
        line.totalProperty().addListener((obs, oldValue, newValue) -> updateInvoiceAmountFromLines());
    }

    private void updateInvoiceAmountFromLines() {
        if (invoiceLineItems.isEmpty()) {
            invoiceAmountField.clear();
            invoicePaymentAmountField.clear();
            return;
        }
        BigDecimal total = invoiceLineItems.stream()
                .map(InvoiceLineModel::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoiceAmountField.setText(total.toPlainString());
        invoicePaymentAmountField.setText(total.toPlainString());
    }

    private String normalize(String value) {
        return value != null && !value.isBlank() ? value.trim() : null;
    }

    private Instant startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
    }

    private Instant endOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    private void saveToFile(byte[] data, String defaultFileName, String extension, String description) {
        if (data == null || data.length == 0) {
            notificationService.publish(new NotificationMessage("warn", "Nessun dato da esportare", Instant.now()));
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva " + description);
        chooser.setInitialFileName(defaultFileName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, "*." + extension));
        File target = chooser.showSaveDialog(mainTabPane != null && mainTabPane.getScene() != null ? mainTabPane.getScene().getWindow() : null);
        if (target != null) {
            try {
                Files.write(Path.of(target.toURI()), data);
                notificationService.publish(new NotificationMessage("export", "File salvato: " + target.getName(), Instant.now()));
            } catch (IOException ex) {
                notificationService.publish(new NotificationMessage("error", "Errore nel salvataggio del file", Instant.now()));
            }
        }
    }

    private String formatInstant(Instant instant) {
        return instant != null ? historyFormatter.format(instant) : "";
    }

    private String monthLabel(int month) {
        try {
            return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault());
        } catch (Exception ex) {
            return Integer.toString(month);
        }
    }

    private static class BigDecimalStringConverter extends StringConverter<BigDecimal> {

        @Override
        public String toString(BigDecimal object) {
            return object == null ? "" : object.toPlainString();
        }

        @Override
        public BigDecimal fromString(String string) {
            if (string == null || string.isBlank()) {
                return null;
            }
            try {
                return new BigDecimal(string);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
