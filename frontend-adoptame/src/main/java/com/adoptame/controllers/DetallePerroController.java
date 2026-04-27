package com.adoptame.controllers;

import com.adoptame.models.Perro;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DetallePerroController {

    @FXML private Label lblNombre, lblRaza, lblEdad, lblSexo, lblTamanio, lblEstado, lblDescripcion;
    @FXML private ImageView imgPerro;

    private Perro perroActual;

    public void initData(Perro perro) {
        this.perroActual = perro;
        lblNombre.setText(perro.getNombre());
        lblRaza.setText(perro.getRaza());
        lblEdad.setText(String.valueOf(perro.getEdad()));
        lblSexo.setText(perro.getSexo());
        lblTamanio.setText(perro.getTamanio());
        lblDescripcion.setText(perro.getDescripcion());
        lblEstado.setText(perro.getEstado());

        if (perro.getImagenPath() != null) {
            String ruta = "/images/" + perro.getImagenPath();
            try {
                imgPerro.setImage(new Image(getClass().getResourceAsStream(ruta)));
            } catch (Exception e) {
                imgPerro.setImage(new Image(getClass().getResourceAsStream("/images/logi.png")));
            }
        }
    }

    @FXML
    private void onEditarClick() {
        // Aquí puedes llamar a la lógica de edición si fuera necesario
        System.out.println("Click en editar perro: " + perroActual.getNombre());
    }

    @FXML
    private void onBorrarClick() {
        // Aquí puedes llamar a la lógica de borrado
        System.out.println("Click en borrar perro: " + perroActual.getNombre());
        ((Stage) lblNombre.getScene().getWindow()).close();
    }
}