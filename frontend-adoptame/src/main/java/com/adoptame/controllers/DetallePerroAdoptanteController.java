package com.adoptame.controllers;

import com.adoptame.models.Perro;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;

public class DetallePerroAdoptanteController {

    @FXML private ImageView imgPrincipal;
    @FXML private Label lblNombre, lblSubDetalles, lblEstado, lblDescripcion, lblPlaceholder;
    private Perro perro;

    public void setData(Perro perro) {
        this.perro = perro;
        lblNombre.setText(perro.getNombre());
        lblSubDetalles.setText(perro.getRaza() + " • " + perro.getEdad() + " años • " + perro.getSexo() + " • " + perro.getTamanio());
        lblEstado.setText(perro.getEstado());
        lblDescripcion.setText(perro.getDescripcion() != null ? perro.getDescripcion() : "Sin descripción.");

        cargarImagen(perro.getImagenPath());
    }

    private void cargarImagen(String path) {
        if (path == null || path.isEmpty()) {
            lblPlaceholder.setVisible(true);
            return;
        }
        try {
            String rutaDefinitiva = "C:\\Users\\julia\\Desktop\\Proyecto-Adoptame-Final\\frontend-adoptame\\src\\main\\resources\\images\\";
            File file = new File(rutaDefinitiva + path.trim());
            if (file.exists()) {
                imgPrincipal.setImage(new Image(file.toURI().toString()));
                lblPlaceholder.setVisible(false);
            } else {
                lblPlaceholder.setVisible(true);
            }
        } catch (Exception e) {
            lblPlaceholder.setVisible(true);
        }
    }

    @FXML
    private void onVolver() {
        cambiarVista("/com/adoptame/views/VistaCatalogo.fxml", null);
    }

    @FXML
    private void onSolicitarAdopcion() {
        // Cargamos el formulario de adopción pasando los datos del perro seleccionado
        cambiarVista("/com/adoptame/views/VistaFormularioAdopcion.fxml", perro);
    }

    // Método privado para gestionar la navegación de forma limpia y sin errores
    private void cambiarVista(String fxmlPath, Perro datosPerro) {
        InicioAdoptanteController main = InicioAdoptanteController.getInstance();
        if (main != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent view = loader.load();

                // Si vamos al formulario, le pasamos el perro al controlador
                if (datosPerro != null) {
                    FormularioAdopcionController controller = loader.getController();
                    controller.setData(datosPerro);
                }

                main.cargarVista(view);
            } catch (IOException e) {
                System.err.println("Error al cargar la vista: " + fxmlPath);
                e.printStackTrace();
            }
        }
    }
}