package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegistrarPerroController {

    @FXML private TextField txtNombre, txtRaza, txtEdad;
    @FXML private ComboBox<String> cbSexo, cbTamanio;
    @FXML private TextArea txtDescripcion;
    @FXML private ImageView imgPreview;
    @FXML private Label lblRutaImagen;

    private File imagenSeleccionada;

    @FXML
    public void initialize() {
        // Llenamos los combos con las opciones típicas
        cbSexo.setItems(FXCollections.observableArrayList("Macho", "Hembra"));
        cbTamanio.setItems(FXCollections.observableArrayList("Pequeño", "Mediano", "Grande"));
    }

    @FXML
    private void onSeleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Perro");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.imagenSeleccionada = file;
            imgPreview.setImage(new Image(file.toURI().toString()));
            lblRutaImagen.setText(file.getName());
        }
    }

    @FXML
    private void onGuardar() {
        if (txtNombre.getText().isEmpty() || cbSexo.getValue() == null) {
            mostrarAlerta("Error", "El nombre y el sexo son obligatorios.");
            return;
        }

        Perro nuevoPerro = new Perro();
        nuevoPerro.setNombre(txtNombre.getText());
        nuevoPerro.setRaza(txtRaza.getText());
        nuevoPerro.setEdad(txtEdad.getText().isEmpty() ? 0 : Integer.parseInt(txtEdad.getText()));
        nuevoPerro.setSexo(cbSexo.getValue());
        nuevoPerro.setTamanio(cbTamanio.getValue());
        nuevoPerro.setDescripcion(txtDescripcion.getText());
        nuevoPerro.setEstado("Disponible");
        nuevoPerro.setImagenPath(imagenSeleccionada != null ? imagenSeleccionada.getName() : "logi.png");

        enviarAlBackend(nuevoPerro);
    }

    private void enviarAlBackend(Perro perro) {
        Gson gson = new Gson();
        String json = gson.toJson(perro);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 201 || response.statusCode() == 200) {
                            mostrarAlerta("Éxito", "¡Perro registrado correctamente!");
                            onLimpiar();
                        } else {
                            mostrarAlerta("Error", "No se pudo guardar. Código: " + response.statusCode());
                        }
                    });
                });
    }

    @FXML
    private void onLimpiar() {
        txtNombre.clear();
        txtRaza.clear();
        txtEdad.clear();
        cbSexo.setValue(null);
        cbTamanio.setValue(null);
        txtDescripcion.clear();
        lblRutaImagen.setText("Ningún archivo seleccionado");
        imgPreview.setImage(new Image(getClass().getResourceAsStream("/images/logi.png")));
    }

    private void mostrarAlerta(String titulo, String msj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}