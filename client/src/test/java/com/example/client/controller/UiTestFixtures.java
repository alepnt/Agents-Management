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
        Path sessionDir = Files.createTempDirectory("gestore-agenti-session");
        sessionDir.toFile().deleteOnExit();
        return new SessionStore(sessionDir);
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
        private final java.util.function.Function<com.example.client.service.LoginForm, AuthSession> loginResolver;
        private final java.util.function.Supplier<UserSummary> registerSupplier;

        StubAuthApiClient(AuthSession session) {
            this(form -> session, session::user);
        }

        StubAuthApiClient(java.util.function.Function<com.example.client.service.LoginForm, AuthSession> loginResolver) {
            this(loginResolver, () -> null);
        }

        StubAuthApiClient(java.util.function.Function<com.example.client.service.LoginForm, AuthSession> loginResolver,
                          java.util.function.Supplier<UserSummary> registerSupplier) {
            this.loginResolver = loginResolver;
            this.registerSupplier = registerSupplier;
        }

        @Override
        public AuthSession login(com.example.client.service.LoginForm form) {
            AuthSession session = loginResolver.apply(form);
            if (session == null) {
                throw new IllegalStateException("Nessuna sessione di test disponibile per " + form.azureId());
            }
            return session;
        }

        @Override
        public UserSummary register(com.example.client.service.RegisterForm form) {
            UserSummary summary = registerSupplier.get();
            if (summary == null) {
                throw new IllegalStateException("Nessun utente di test disponibile per la registrazione");
            }
            return summary;
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
        @Override
        public MainViewController create(AuthSession session, SessionStore sessionStore) {
            return new StubMainViewController(session, sessionStore);
        }
    }
}
