package com.adoptame.controllers;

import com.adoptame.models.User;
import com.adoptame.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class InicioAdoptanteController {

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentArea;
    @FXML private ImageView imgUserAvatar;
    
    private static InicioAdoptanteController instance;

    public static InicioAdoptanteController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        setupUserAvatar();
        cargarVista("/com/adoptame/views/VistaCatalogo.fxml"); 
    }

    public void setupUserAvatar() {
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(30, 30, 30);
        imgUserAvatar.setClip(clip);

        User user = SessionManager.getUser();
        if (user != null && user.getFotoPerfil() != null && !user.getFotoPerfil().isEmpty()) {
            try { imgUserAvatar.setImage(new Image(user.getFotoPerfil())); } catch (Exception e) {}
        }
    }

    public void cargarVista(Parent vista) {
        contentArea.getChildren().setAll(vista);
    }
    
    public void cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent vista = loader.load();
            contentArea.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMisSolicitudes() {
        cargarVista("/com/adoptame/views/VistaMisSolicitudes.fxml");
    }

    @FXML
    private void onPerfil() {
        cargarVista("/com/adoptame/views/VistaPerfilAdoptante.fxml");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        SessionManager.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/adoptame/views/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AdoptaMe - Iniciar Sesión");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}