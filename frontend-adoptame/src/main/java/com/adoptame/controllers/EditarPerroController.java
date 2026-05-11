package com.adoptame.controllers;

import com.adoptame.models.Perro;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.File;

public class EditarPerroController {

    @FXML private ImageView imgPrincipal;
    @FXML private Label lblNombre, lblSubDetalles, lblDescripcion, lblPlaceholder;
    @FXML private ComboBox<String> comboEstado;

    private Perro perro;

    public void setPerro(Perro perro) {
        this.perro = perro;
        lblNombre.setText(perro.getNombre());
        lblSubDetalles.setText(perro.getRaza() + " • " + perro.getEdad() + " años • " + perro.getSexo() + " • " + perro.getTamanio());
        lblDescripcion.setText(perro.getDescripcion());
        
        if (comboEstado != null) {
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
            String userDir = System.getProperty("user.dir");
            
            String rutaRelativa = "src" + File.separator + "main" + File.separator + 
                                 "resources" + File.separator + "images" + File.separator + path;
            
            File file = new File(userDir, rutaRelativa);

            if (file.exists()) {
                imgPrincipal.setImage(new Image(file.toURI().toString()));
                lblPlaceholder.setVisible(false);
            } else {
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
        System.out.println("Actualizando estado de " + perro.getNombre() + " a: " + comboEstado.getValue());
    }

    @FXML
    private void onCerrarVentana() {
        Stage stage = (Stage) lblNombre.getScene().getWindow();
        stage.close();
    }
}