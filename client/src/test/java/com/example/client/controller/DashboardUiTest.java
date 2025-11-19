package com.example.client.controller;

import com.example.client.service.AuthSession;
import com.example.client.session.SessionStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.TabPane;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

class DashboardUiTest extends ApplicationTest {

    private SessionStore sessionStore;
    private AuthSession session;

    @BeforeAll
    static void configureHeadless() {
        UiTestFixtures.enableHeadlessMode();
    }

    @BeforeEach
    void setUp() throws Exception {
        sessionStore = UiTestFixtures.newTemporarySessionStore();
        session = UiTestFixtures.demoSession();
        sessionStore.save(session);
        FxToolkit.registerPrimaryStage();
    }

    @AfterEach
    void tearDown() throws Exception {
        UiTestFixtures.cleanupStages();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/MainView.fxml"));
        loader.setControllerFactory(type -> {
            if (type == MainViewController.class) {
                return new UiTestFixtures.StubMainViewController(session, sessionStore);
            }
            throw new IllegalStateException("Controller non gestito: " + type.getName());
        });

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void dashboardLoadsWithTabsVisible() {
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#mainTabPane", NodeMatchers.isVisible());
        TabPane tabPane = lookup("#mainTabPane").query();
        assertEquals("Fatture", tabPane.getTabs().get(0).getText());
    }

    @Test
    void logoutRequiresNewAuthentication() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Esci");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#loginButton", NodeMatchers.isVisible());
        Labeled statusLabel = lookup("#statusLabel").queryLabeled();
        assertEquals(MainViewController.LOGOUT_STATUS_MESSAGE, statusLabel.getText());
        assertTrue(sessionStore.load().isEmpty());
    }
}
