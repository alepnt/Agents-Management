package com.example.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

/**
 * Utility per centralizzare il caricamento delle viste FXML.
 */
public final class ViewLoader {

    private ViewLoader() {
    }

    public static Scene loadMainView() {
        try {
            URL resource = ViewLoader.class.getResource("/com/example/client/view/MainView.fxml");
            if (resource == null) {
                throw new IllegalStateException("Risorsa FXML non trovata: /com/example/client/view/MainView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            return new Scene(root);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile caricare la vista principale", e);
        }
    }
}
