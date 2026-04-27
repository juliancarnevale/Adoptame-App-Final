package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert; // <-- NUEVO IMPORT
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class VentanaPrincipalController {

    @FXML
    private ListView<Perro> listaPerros; 

    // ESTO ES LO QUE TIENES QUE PEGAR:
    @FXML
    public void initialize() {
        // Añadimos un "escuchador" a la lista para saber cuándo seleccionas un perro
        listaPerros.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Si el usuario hace clic en un perro, mostramos sus datos
                mostrarAlertaDetalle(newValue);
            }
        });
    }

    // Un pequeño método auxiliar para mostrar la ventana de detalle
    private void mostrarAlertaDetalle(Perro perro) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ficha de Adopción");
        alert.setHeaderText("Detalles de " + perro.getNombre());
        alert.setContentText("Raza: " + perro.getRaza() + 
                           "\nTamaño: " + perro.getTamanio() +
                           "\n\n¡Este perrito está esperando un hogar!");
        alert.showAndWait();
    }

    @FXML
    private void onBtnCargarClick() {
        System.out.println("¡Botón pulsado! Intentando conectar...");
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Gson gson = new Gson();
                    List<Perro> perrosRecibidos = gson.fromJson(json, new TypeToken<List<Perro>>(){}.getType());

                    javafx.application.Platform.runLater(() -> {
                        listaPerros.getItems().clear();
                        listaPerros.getItems().addAll(perrosRecibidos);
                    });
                })
                .exceptionally(e -> {
                    System.err.println("Error al conectar con el backend: " + e.getMessage());
                    return null;
                });
    }
}