package com.adoptame.controllers;

import com.adoptame.models.User;
import com.adoptame.utils.SessionManager;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ToggleGroup tipoUsuario;

    @FXML
    private void onLoginClick(ActionEvent event) {
        String email = txtUsuario.getText();
        String pass = txtPassword.getText();
        
        if (email.isEmpty() || pass.isEmpty()) {
            mostrarError("Campos incompletos", "Rellena usuario y contraseña.");
            return;
        }

        if (!email.contains("@")) email += "@gmail.com";

        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", email);
        credenciales.put("password", pass);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/usuarios/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(credenciales)))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        // INICIO DE SESIÓN REAL
                        User usuarioReal = new Gson().fromJson(response.body(), User.class);
                        SessionManager.setUser(usuarioReal);

                        String rutaFxml = "Administrador".equals(usuarioReal.getTipo()) ? "/com/adoptame/views/AdminPanel.fxml" : "/com/adoptame/views/VistaInicioAdoptante.fxml";
                        String tituloVentana = "Administrador".equals(usuarioReal.getTipo()) ? "Panel de Administración" : "Catálogo de Adopción";
                        
                        navegarAVentana(event, rutaFxml, tituloVentana);
                    } else {
                        mostrarError("Error de Login", "Credenciales incorrectas o usuario no existe.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarError("Error", "No hay conexión con el servidor."));
                    return null;
                });
    }

    @FXML
    private void onCrearCuentaClick(ActionEvent event) {
        navegarAVentana(event, "/com/adoptame/views/VistaRegistro.fxml", "AdoptaMe - Crear Cuenta");
    }

    private void navegarAVentana(ActionEvent event, String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            java.net.URL cssUrl = getClass().getResource("/com/adoptame/views/estilos-app.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}