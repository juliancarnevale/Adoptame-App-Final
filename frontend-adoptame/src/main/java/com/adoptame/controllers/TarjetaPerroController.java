package com.adoptame.controllers;

import com.adoptame.models.Perro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;

public class TarjetaPerroController {

    @FXML private ImageView imgPerro;
    @FXML private Label lblNombre, lblDetalles, lblEstado, lblPlaceholder;
    private Perro perro;

    public void setPerro(Perro perro) {
        this.perro = perro;
        lblNombre.setText(perro.getNombre());
        lblDetalles.setText(perro.getRaza() + " • " + perro.getEdad() + " años");
        lblEstado.setText(perro.getEstado());

        if (perro.getImagenPath() != null && !perro.getImagenPath().isEmpty()) {
            cargarImagen(perro.getImagenPath());
        }
    }

    private void cargarImagen(String path) {
        try {
            // RUTA ABSOLUTA FORZADA AL ESCRITORIO
            String rutaDefinitiva = "C:\\Users\\julia\\Desktop\\Proyecto-Adoptame-Final\\frontend-adoptame\\src\\main\\resources\\images\\";
            File file = new File(rutaDefinitiva + path.trim());

            if (file.exists()) {
                imgPerro.setImage(new Image(file.toURI().toString()));
                lblPlaceholder.setVisible(false);
            } else {
                // Fallback: Si no coincide la extensión, probamos .jpg y .png
                String nombreSinExt = path.contains(".") ? path.substring(0, path.lastIndexOf('.')) : path;
                File jpg = new File(rutaDefinitiva + nombreSinExt + ".jpg");
                File png = new File(rutaDefinitiva + nombreSinExt + ".png");

                if (jpg.exists()) {
                    imgPerro.setImage(new Image(jpg.toURI().toString()));
                    lblPlaceholder.setVisible(false);
                } else if (png.exists()) {
                    imgPerro.setImage(new Image(png.toURI().toString()));
                    lblPlaceholder.setVisible(false);
                } else {
                    System.err.println("TarjetaPerro: Archivo no hallado en " + file.getAbsolutePath());
                    lblPlaceholder.setVisible(true);
                    imgPerro.setImage(null);
                }
            }
        } catch (Exception e) {
            lblPlaceholder.setVisible(true);
        }
    }

    @FXML
    private void onVerDetalles(ActionEvent event) {
        InicioAdoptanteController main = InicioAdoptanteController.getInstance();
        if (main != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adoptame/views/VistaDetallePerroAdoptante.fxml"));
                Parent view = loader.load();
                DetallePerroAdoptanteController controller = loader.getController();
                controller.setData(perro);
                main.cargarVista(view);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}