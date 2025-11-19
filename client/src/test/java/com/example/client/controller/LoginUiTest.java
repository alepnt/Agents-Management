package com.example.client.controller;

import com.example.client.service.AuthSession;
import com.example.client.service.UserSummary;
import com.example.client.session.SessionStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testfx.api.FxAssert.verifyThat;

class LoginUiTest extends ApplicationTest {

    private SessionStore sessionStore;
    private AuthSession session;
    private Map<String, AuthSession> availableSessions;
    private UiTestFixtures.StubAuthApiClient authApiClient;
    private UiTestFixtures.StubMainViewFactory mainViewFactory;
    private Stage primaryStage;

    @BeforeAll
    static void configureHeadless() {
        UiTestFixtures.enableHeadlessMode();
    }

    @BeforeEach
    void setUp() throws Exception {
        sessionStore = UiTestFixtures.newTemporarySessionStore();
        session = UiTestFixtures.demoSession();
        availableSessions = new HashMap<>();
        availableSessions.put(session.user().azureId(), session);
        authApiClient = new UiTestFixtures.StubAuthApiClient(form -> availableSessions.get(form.azureId()));
        mainViewFactory = new UiTestFixtures.StubMainViewFactory();
        FxToolkit.registerPrimaryStage();
    }

    @AfterEach
    void tearDown() throws Exception {
        UiTestFixtures.cleanupStages();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        loadLoginScene();
    }

    @Test
    void successfulLoginNavigatesToDashboard() {
        performLogin(session);
        verifyThat("#mainTabPane", NodeMatchers.isVisible());
    }

    @Test
    void loginsForDifferentAccountsDoNotOverrideSessions() throws Exception {
        AuthSession secondSession = new AuthSession(
                "second-token",
                "Bearer",
                session.expiresAt().plusSeconds(600),
                new UserSummary(2L, "second@example.com", "Second User", "azure-2", 2L, 2L)
        );
        availableSessions.put(secondSession.user().azureId(), secondSession);

        performLogin(session);
        WaitForAsyncUtils.waitForFxEvents();

        relaunchLoginView();

        performLogin(secondSession);
        WaitForAsyncUtils.waitForFxEvents();

        var storedFirst = sessionStore.loadForUser(session.user().azureId());
        var storedSecond = sessionStore.loadForUser(secondSession.user().azureId());

        org.junit.jupiter.api.Assertions.assertTrue(storedFirst.isPresent(), "La sessione del primo utente deve esistere");
        org.junit.jupiter.api.Assertions.assertEquals(session.accessToken(), storedFirst.get().accessToken());
        org.junit.jupiter.api.Assertions.assertTrue(storedSecond.isPresent(), "La sessione del secondo utente deve esistere");
        org.junit.jupiter.api.Assertions.assertEquals(secondSession.accessToken(), storedSecond.get().accessToken());
    }

    private void loadLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/LoginView.fxml"));
        loader.setControllerFactory(type -> {
            if (type == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient, mainViewFactory);
            }
            throw new IllegalStateException("Controller non gestito: " + type.getName());
        });
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void relaunchLoginView() throws Exception {
        FxToolkit.setupFixture(() -> {
            try {
                loadLoginScene();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void performLogin(AuthSession sessionToUse) {
        clickOn("#emailField").write(sessionToUse.user().email());
        clickOn("#displayNameField").write(sessionToUse.user().displayName());
        clickOn("#azureIdField").write(sessionToUse.user().azureId());
        clickOn("#accessTokenArea").write(sessionToUse.accessToken());

        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
    }
}
