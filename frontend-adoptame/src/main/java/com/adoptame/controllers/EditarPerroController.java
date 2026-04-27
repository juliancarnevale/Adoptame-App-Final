package com.adoptame.controllers;

import com.adoptame.models.Perro;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.InputStream;

public class EditarPerroController {

    @FXML private ImageView imgPrincipal;
    @FXML private Label lblNombre, lblSubDetalles, lblDescripcion, lblPlaceholder;
    @FXML private ComboBox<String> comboEstado;

    private Perro perro;

    // Este método se llama desde el Panel de Administración al seleccionar un perro
    public void setPerro(Perro perro) {
        this.perro = perro;
        lblNombre.setText(perro.getNombre());
        lblSubDetalles.setText(perro.getRaza() + " • " + perro.getEdad() + " años • " + perro.getSexo() + " • " + perro.getTamanio());
        lblDescripcion.setText(perro.getDescripcion());
        
        if (comboEstado != null) {
            // Asegúrate de que el combo tenga opciones si no las tiene en el FXML
            if (comboEstado.getItems().isEmpty()) {
                comboEstado.getItems().addAll("Disponible", "Adoptado", "En proceso");
            }
            comboEstado.setValue(perro.getEstado());
        }

        cargarImagen(perro.getImagenPath());
    }

    private void cargarImagen(String path) {
        if (path == null || path.isEmpty()) {
            lblPlaceholder.setVisible(true);
            return;
        }

        try {
            // 1. Obtenemos la carpeta donde está abierto el proyecto (Ej: .../frontend-adoptame)
            String userDir = System.getProperty("user.dir");
            
            // 2. Construimos la ruta hacia la carpeta de recursos de forma dinámica
            // File.separator pone automáticamente \ en Windows o / en Mac/Linux
            String rutaRelativa = "src" + File.separator + "main" + File.separator + 
                                 "resources" + File.separator + "images" + File.separator + path;
            
            File file = new File(userDir, rutaRelativa);

            if (file.exists()) {
                imgPrincipal.setImage(new Image(file.toURI().toString()));
                lblPlaceholder.setVisible(false);
            } else {
                // Plan B: Intentar cargarlo como recurso interno si el archivo está empaquetado
                java.io.InputStream stream = getClass().getResourceAsStream("/images/" + path);
                if (stream != null) {
                    imgPrincipal.setImage(new Image(stream));
                    lblPlaceholder.setVisible(false);
                } else {
                    System.err.println("No se encontró el archivo en: " + file.getAbsolutePath());
                    lblPlaceholder.setVisible(true);
                }
            }
        } catch (Exception e) {
            lblPlaceholder.setVisible(true);
        }
    }

    @FXML
    private void onActualizarEstado() {
        // Aquí va tu lógica para guardar el cambio de estado en la base de datos
        System.out.println("Actualizando estado de " + perro.getNombre() + " a: " + comboEstado.getValue());
    }

    @FXML
    private void onCerrarVentana() {
        Stage stage = (Stage) lblNombre.getScene().getWindow();
        stage.close();
    }
}