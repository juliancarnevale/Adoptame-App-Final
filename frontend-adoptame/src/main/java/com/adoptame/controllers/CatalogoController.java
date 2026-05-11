package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CatalogoController {

    @FXML private FlowPane contenedorPerros;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        cargarPerrosDisponibles();
    }

    private void cargarPerrosDisponibles() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            List<Perro> perros = gson.fromJson(response.body(), new TypeToken<List<Perro>>(){}.getType());
            Platform.runLater(() -> {
                contenedorPerros.getChildren().clear();
                for (Perro perro : perros) {
                    if ("Disponible".equalsIgnoreCase(perro.getEstado())) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adoptame/views/TarjetaPerro.fxml"));
                            Parent tarjeta = loader.load();
                            
                            tarjeta.getStyleClass().add("tarjeta");
                            
                            TarjetaPerroController controller = loader.getController();
                            controller.setPerro(perro);
                            
                            contenedorPerros.getChildren().add(tarjeta);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }
}