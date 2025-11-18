package com.example.client.controller;

import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
import com.example.client.service.UserSummary;
import com.example.client.session.SessionStore;
import org.testfx.api.FxToolkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

final class UiTestFixtures {

    static void enableHeadlessMode() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("java.awt.headless", "true");
    }

    static SessionStore newTemporarySessionStore() throws IOException {
        Path sessionFile = Files.createTempFile("gestore-agenti-session", ".json");
        sessionFile.toFile().deleteOnExit();
        return new SessionStore(sessionFile);
    }

    static AuthSession demoSession() {
        return new AuthSession(
                "dummy-token",
                "Bearer",
                Instant.now().plusSeconds(3600),
                new UserSummary(1L, "demo@example.com", "Demo User", "azure-1", 1L, 1L)
        );
    }

    static void cleanupStages() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    static class StubAuthApiClient extends AuthApiClient {
        private final AuthSession session;

        StubAuthApiClient(AuthSession session) {
            this.session = session;
        }

        @Override
        public AuthSession login(com.example.client.service.LoginForm form) {
            return session;
        }

        @Override
        public UserSummary register(com.example.client.service.RegisterForm form) {
            return session.user();
        }
    }

    static class StubMainViewController extends MainViewController {
        StubMainViewController(AuthSession session, SessionStore sessionStore) {
            super(session, sessionStore);
        }

        @Override
        public void refreshData() {
            // Evita chiamate al backend durante i test UI.
        }
    }

    static class StubMainViewFactory implements LoginController.MainViewFactory {
        private final AuthSession session;
        private final SessionStore sessionStore;

        StubMainViewFactory(AuthSession session, SessionStore sessionStore) {
            this.session = session;
            this.sessionStore = sessionStore;
        }

        @Override
        public MainViewController create(AuthSession session, SessionStore sessionStore) {
            return new StubMainViewController(session, sessionStore);
        }
    }
}
