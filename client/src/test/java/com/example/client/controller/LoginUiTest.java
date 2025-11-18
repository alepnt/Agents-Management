package com.example.client.controller;

import com.example.client.service.AuthSession;
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

import static org.testfx.api.FxAssert.verifyThat;

class LoginUiTest extends ApplicationTest {

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
        FxToolkit.registerPrimaryStage();
    }

    @AfterEach
    void tearDown() throws Exception {
        UiTestFixtures.cleanupStages();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/LoginView.fxml"));
        loader.setControllerFactory(type -> {
            if (type == LoginController.class) {
                return LoginController.create(
                        sessionStore,
                        new UiTestFixtures.StubAuthApiClient(session),
                        new UiTestFixtures.StubMainViewFactory(session, sessionStore)
                );
            }
            throw new IllegalStateException("Controller non gestito: " + type.getName());
        });
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void successfulLoginNavigatesToDashboard() {
        clickOn("#emailField").write(session.user().email());
        clickOn("#displayNameField").write(session.user().displayName());
        clickOn("#azureIdField").write(session.user().azureId());
        clickOn("#accessTokenArea").write(session.accessToken());

        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("#mainTabPane", NodeMatchers.isVisible());
    }
}
