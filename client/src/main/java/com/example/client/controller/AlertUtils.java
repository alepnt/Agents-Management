package com.example.client.controller; // Package dedicato ai controller e utilità lato client (JavaFX).

import javafx.scene.control.Alert; // Classe JavaFX per la visualizzazione di finestre di dialogo.

/**
 * Utility class per la visualizzazione di messaggi di errore tramite JavaFX.
 * La classe è final e ha costruttore privato per impedirne l’istanza.
 */
public final class AlertUtils { // Classe di utilità non estendibile.

    private AlertUtils() { // Costruttore privato → impedisce la creazione dell'istanza.
    }

    /**
     * Mostra una finestra di dialogo di errore standardizzata.
     *
     * @param message testo dell’errore da visualizzare
     */
    public static void showError(String message) { // Metodo statico per mostrare un errore in UI.
        Alert alert = new Alert(Alert.AlertType.ERROR); // Crea un nuovo popup di tipo ERRORE.
        alert.setTitle("Errore"); // Titolo della finestra.
        alert.setHeaderText("Si è verificato un problema"); // Intestazione generica dell’errore.
        alert.setContentText(message); // Corpo del messaggio contenente il dettaglio passato.
        alert.show(); // Mostra la finestra in modo non bloccante.
    }
} // Fine classe AlertUtils.
