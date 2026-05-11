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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class RegistroController {

    @FXML private RadioButton rbAdoptante, rbAsociacion;
    @FXML private ToggleGroup tipoCuentaGroup;
    @FXML private VBox boxProtectora;
    
    @FXML private TextField txtProtectora, txtNombre, txtCorreo, txtTelefono;
    @FXML private TextField txtCalle, txtCP, txtLocalidad, txtProvincia;
    @FXML private PasswordField txtPassword;

    @FXML
    public void initialize() {
        tipoCuentaGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (rbAsociacion.isSelected()) {
                boxProtectora.setVisible(true);
                boxProtectora.setManaged(true);
            } else {
                boxProtectora.setVisible(false);
                boxProtectora.setManaged(false);
            }
        });
    }

    @FXML
    private void onRegistrarClick(ActionEvent event) {
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            mostrarAlerta("Error", "Los campos Nombre, Correo y Contraseña son obligatorios.");
            return;
        }

        if (rbAsociacion.isSelected() && txtProtectora.getText().isEmpty()) {
            mostrarAlerta("Error", "Debe indicar el nombre de la protectora o asociación.");
            return;
        }

        String nombreFinal = rbAsociacion.isSelected() ? txtProtectora.getText() + " - " + txtNombre.getText() : txtNombre.getText();
        String tipoUsuario = rbAsociacion.isSelected() ? "Administrador" : "Adoptante";

        Map<String, String> data = new HashMap<>();
        data.put("nombre", nombreFinal);
        data.put("email", txtCorreo.getText());
        data.put("telefono", txtTelefono.getText());
        data.put("passwordHash", txtPassword.getText());
        data.put("tipo", tipoUsuario);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(data);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/usuarios/registro"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        
                        User usuarioReal = new Gson().fromJson(response.body(), User.class);
                        SessionManager.setUser(usuarioReal);

                        String fxmlDestino = rbAsociacion.isSelected() ? "/com/adoptame/views/AdminPanel.fxml" : "/com/adoptame/views/VistaInicioAdoptante.fxml";
                        String tituloDestino = rbAsociacion.isSelected() ? "Panel de Administración - AdoptaMe" : "Catálogo de Adopción - AdoptaMe";
                        
                        navegarAVentana(event, fxmlDestino, tituloDestino);
                        
                    } else if (response.statusCode() == 409) {
                        mostrarAlerta("Usuario ya existe", "El correo electrónico ingresado ya está registrado. Por favor, utiliza otro o inicia sesión.");
                    } else {
                        mostrarAlerta("Error al registrar", "Ha ocurrido un problema. Código: " + response.statusCode());
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarAlerta("Error de conexión", "No se pudo conectar con el servidor backend."));
                    ex.printStackTrace();
                    return null;
                });
    }

    @FXML
    private void onVolverClick(ActionEvent event) {
        navegarAVentana(event, "/com/adoptame/views/Login.fxml", "AdoptaMe - Iniciar Sesión");
    }

    private void navegarAVentana(ActionEvent event, String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            java.net.URL cssUrl = getClass().getResource("/com/adoptame/views/estilos-app.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            if (fxml.contains("AdminPanel")) {
                java.net.URL adminCssUrl = getClass().getResource("/com/adoptame/views/estilos-admin.css");
                if (adminCssUrl != null) {
                    scene.getStylesheets().add(adminCssUrl.toExternalForm());
                }
            }

            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            
            try {
                com.adoptame.utils.Animaciones.animarVista(root);
            } catch (Exception e) {}

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}