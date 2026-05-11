package com.adoptame.controllers;

import com.adoptame.models.Perro;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.File;


public class DetallePerroAdminController {


    @FXML private ImageView imgPrincipal, imgExtra1, imgExtra2, imgExtra3;
    @FXML private Label lblNombre, lblSubDetalles, lblDescripcion, lblPlaceholder;
    @FXML private ComboBox<String> cbEstado;

    private Perro perro;


    public void setData(Perro perro) {
        this.perro = perro;
        lblNombre.setText(perro.getNombre());
        lblSubDetalles.setText(perro.getRaza() + " • " + perro.getEdad() + " años • " + perro.getSexo());
        lblDescripcion.setText(perro.getDescripcion() != null ? perro.getDescripcion() : "Sin descripción.");

        // Configurar ComboBox
        if (cbEstado != null) {
            cbEstado.getItems().setAll("Disponible", "Adoptado", "En proceso");
            cbEstado.setValue(perro.getEstado());
        }

        cargarImagen(perro.getImagenPath());
    }

    private void cargarImagen(String path) {
        if (path == null || path.isEmpty()) {
            lblPlaceholder.setVisible(true);
            return;
        }

        try {

            String rutaEscritorio = "C:\\Users\\julia\\Desktop\\Proyecto-Adoptame-Final\\frontend-adoptame\\src\\main\\resources\\images\\";
            
            File file = new File(rutaEscritorio + path.trim());


            System.out.println("Intentando cargar: " + file.getAbsolutePath());
            System.out.println("¿El archivo existe físicamente?: " + file.exists());

            if (file.exists()) {
                imgPrincipal.setImage(new Image(file.toURI().toString()));
                lblPlaceholder.setVisible(false);
            } else {

                String nombreSinExt = path.substring(0, path.lastIndexOf('.'));
                File fileJpg = new File(rutaEscritorio + nombreSinExt + ".jpg");
                File filePng = new File(rutaEscritorio + nombreSinExt + ".png");

                if (fileJpg.exists()) {
                    imgPrincipal.setImage(new Image(fileJpg.toURI().toString()));
                    lblPlaceholder.setVisible(false);
                } else if (filePng.exists()) {
                    imgPrincipal.setImage(new Image(filePng.toURI().toString()));
                    lblPlaceholder.setVisible(false);
                } else {
                    System.err.println("ERROR CRÍTICO: La foto no está en la carpeta del escritorio.");
                    lblPlaceholder.setVisible(true);
                    imgPrincipal.setImage(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblPlaceholder.setVisible(true);
        }
    }


    @FXML
    private void onAddExtraPhoto1() {
        System.out.println("Añadiendo foto extra 1 para " + perro.getNombre());
    }

    @FXML
    private void onAddExtraPhoto2() {
        System.out.println("Añadiendo foto extra 2 para " + perro.getNombre());
    }

    @FXML
    private void onAddExtraPhoto3() {
        System.out.println("Añadiendo foto extra 3 para " + perro.getNombre());
    }

    @FXML
    private void onGuardarEstado() {
        System.out.println("Guardando nuevo estado: " + cbEstado.getValue());
    }

    @FXML
    private void onCerrar() {
        if (lblNombre.getScene() != null) {
            Stage stage = (Stage) lblNombre.getScene().getWindow();
            stage.close();
        }
    }
}