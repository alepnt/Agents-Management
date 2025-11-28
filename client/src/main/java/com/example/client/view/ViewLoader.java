package com.example.client.view;
// Package che contiene componenti e utility per le viste JavaFX.

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
// Librerie JavaFX per il caricamento dinamico delle viste FXML.

import java.io.IOException;
import java.net.URL;
// URL per localizzare risorse nel classpath e gestione eccezioni.

/**
 * Utility per centralizzare il caricamento delle viste FXML.
 *
 * Scopo:
 * - fornire un unico punto di accesso per caricare la MainView
 * - garantire gestione coerente di errori e percorsi
 * - evitare ripetizione di codice boilerplate in più controller
 *
 * È "final" e con costruttore privato perché contiene solo metodi statici.
 */
public final class ViewLoader {

    /**
     * Costruttore privato.
     * Impedisce istanziazione di questa utility class.
     */
    private ViewLoader() {
    }

    /**
     * Carica e restituisce la scena principale dell’applicazione.
     *
     * @return Scene già pronta da impostare su uno Stage.
     * @throws IllegalStateException se il file FXML non è trovato o non può essere
     *                               caricato.
     */
    public static Scene loadMainView() {
        try {
            // Percorso della risorsa FXML principale
            URL resource = ViewLoader.class.getResource("/com/example/client/view/MainView.fxml");

            // Verifica che il file esista nel classpath
            if (resource == null) {
                throw new IllegalStateException(
                        "Risorsa FXML non trovata: /com/example/client/view/MainView.fxml");
            }

            // Loader FXML che costruisce l'albero dei nodi JavaFX
            FXMLLoader loader = new FXMLLoader(resource);

            // Caricamento effettivo della UI
            Parent root = loader.load();

            // Restituisce la scena appena creata
            return new Scene(root);

        } catch (IOException e) {
            // Wrappa l’errore in un IllegalStateException
            throw new IllegalStateException("Impossibile caricare la vista principale", e);
        }
    }
}
