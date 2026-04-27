package com.adoptame.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/com/adoptame/views/Login.fxml");
        
        if (fxmlLocation == null) {
            throw new RuntimeException("ERROR: No se encontró Login.fxml en la ruta /com/adoptame/views/");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        
        URL cssLocation = getClass().getResource("/com/adoptame/views/login-estilos.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        primaryStage.setTitle("AdoptaMe - Iniciar Sesión");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}