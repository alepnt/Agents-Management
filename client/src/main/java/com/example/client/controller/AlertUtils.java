package com.example.client.controller;

import javafx.scene.control.Alert;

public final class AlertUtils {

    private AlertUtils() {
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un problema");
        alert.setContentText(message);
        alert.show();
    }
}
