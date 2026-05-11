package com.adoptame.controllers;

import com.adoptame.models.User;
import com.adoptame.utils.SessionManager;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PerfilAdoptanteController {

    @FXML private ImageView imgPerfil;
    @FXML private TextField txtNombre, txtEmail, txtTelefono;
    private String rutaFotoTemporal;

    @FXML
    public void initialize() {
        User user = SessionManager.getUser();
        if (user != null) {
            txtNombre.setText(user.getNombre());
            txtEmail.setText(user.getEmail());
            txtTelefono.setText(user.getTelefono());
            
            if (user.getFotoPerfil() != null && !user.getFotoPerfil().isEmpty()) {
                try { imgPerfil.setImage(new Image(user.getFotoPerfil())); } catch (Exception e) {}
            }
        }
    }

    @FXML
    private void onCambiarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog((Stage) txtNombre.getScene().getWindow());

        if (file != null) {
            rutaFotoTemporal = file.toURI().toString();
            imgPerfil.setImage(new Image(rutaFotoTemporal));
        }
    }

    @FXML
    private void onGuardarCambios() {
        User user = SessionManager.getUser();
        if (user != null) {
            user.setNombre(txtNombre.getText());
            user.setEmail(txtEmail.getText());
            user.setTelefono(txtTelefono.getText());
            if (rutaFotoTemporal != null) user.setFotoPerfil(rutaFotoTemporal);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/usuarios/" + user.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user)))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    SessionManager.setUser(user); 
                    
                    if (InicioAdoptanteController.getInstance() != null) {
                        InicioAdoptanteController.getInstance().setupUserAvatar();
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText(null);
                    alert.setContentText("Tus datos han sido actualizados en la base de datos.");
                    alert.showAndWait();
                }
            }));
        }
    }

    @FXML
    private void onVolverAlCatalogo() {
        if (InicioAdoptanteController.getInstance() != null) {
            InicioAdoptanteController.getInstance().cargarVista("/com/adoptame/views/VistaCatalogo.fxml");
        }
    }
}