package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.adoptame.models.Solicitud;
import com.adoptame.models.User;
import com.adoptame.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MisSolicitudesController {

    @FXML private VBox contenedorSolicitudes;

    @FXML
    public void initialize() {
        contenedorSolicitudes.getChildren().clear();
        cargarSolicitudesPersonales();
    }

    private void cargarSolicitudesPersonales() {
        User currentUser = SessionManager.getUser();
        if (currentUser == null) return;

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // 1. Obtener perros para mapear nombres
        HttpRequest reqPerros = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros"))
                .build();

        client.sendAsync(reqPerros, HttpResponse.BodyHandlers.ofString())
                .thenAccept(respPerros -> {
                    List<Perro> perros = gson.fromJson(respPerros.body(), new TypeToken<List<Perro>>(){}.getType());
                    Map<Long, Perro> mapaPerros = perros.stream().collect(Collectors.toMap(Perro::getId, p -> p));

                    // 2. Obtener solicitudes
                    HttpRequest reqSols = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/solicitudes"))
                            .build();

                    client.sendAsync(reqSols, HttpResponse.BodyHandlers.ofString())
                            .thenAccept(respSols -> {
                                List<Solicitud> todas = gson.fromJson(respSols.body(), new TypeToken<List<Solicitud>>(){}.getType());
                                
                                List<Solicitud> misSols = todas.stream()
                                        .filter(s -> s.getCorreo() != null && s.getCorreo().equalsIgnoreCase(currentUser.getEmail()))
                                        .collect(Collectors.toList());

                                Platform.runLater(() -> {
                                    if (misSols.isEmpty()) {
                                        Label empty = new Label("No has realizado ninguna solicitud de adopción.");
                                        empty.setFont(new Font(16));
                                        contenedorSolicitudes.getChildren().add(empty);
                                    } else {
                                        for (Solicitud sol : misSols) {
                                            Perro p = mapaPerros.get(sol.getPerroId());
                                            crearTarjetaSolicitud(p != null ? p.getNombre() : "Perro desconocido", p != null ? p.getRaza() : "-", sol.getEstado());
                                        }
                                    }
                                });
                            });
                });
    }

    private void crearTarjetaSolicitud(String nombre, String raza, String estado) {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-padding: 20;");
        
        VBox info = new VBox(5);
        Label lblP = new Label(nombre + " (" + raza + ")");
        lblP.setFont(new Font("System Bold", 18));
        info.getChildren().addAll(lblP, new Label("Estado de la solicitud:"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblE = new Label(estado != null ? estado : "Pendiente");
        String color = "#fff3cd"; // Amarillo (Pendiente)
        String textCol = "#856404";
        
        if ("Aprobada".equalsIgnoreCase(estado)) {
            color = "#d4edda"; // Verde
            textCol = "#155724";
        } else if ("Rechazada".equalsIgnoreCase(estado)) {
            color = "#f8d7da"; // Rojo
            textCol = "#721c24";
        }
        
        lblE.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + textCol + "; -fx-padding: 5 15; -fx-background-radius: 15; -fx-font-weight: bold;");
        
        box.getChildren().addAll(info, spacer, lblE);
        contenedorSolicitudes.getChildren().add(box);
    }

    @FXML
    private void onVolverAlCatalogo() {
        InicioAdoptanteController main = InicioAdoptanteController.getInstance();
        if (main != null) {
            main.cargarVista("/com/adoptame/views/VistaCatalogo.fxml");
        }
    }
}