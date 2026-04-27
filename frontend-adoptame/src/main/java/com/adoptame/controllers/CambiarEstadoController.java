package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CambiarEstadoController {

    @FXML private TextField txtBuscador;
    @FXML private ListView<Perro> lvPerros;
    @FXML private VBox panelAccion;
    @FXML private ImageView imgPerro;
    @FXML private Label lblNombre, lblEstadoActual;

    private ObservableList<Perro> listaOriginal = FXCollections.observableArrayList();
    private Perro perroSeleccionado;

    @FXML
    public void initialize() {
        panelAccion.setOpacity(0.5); // Desactivado visualmente al inicio
        configurarLista();
        cargarPerros();
    }

    private void configurarLista() {
        lvPerros.setCellFactory(param -> new ListCell<Perro>() {
            @Override
            protected void updateItem(Perro item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " (" + item.getRaza() + ")");
                }
            }
        });

        // Evento al seleccionar un perro
        lvPerros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarDetalleMini(newVal);
            }
        });
    }

    private void mostrarDetalleMini(Perro perro) {
        this.perroSeleccionado = perro;
        panelAccion.setOpacity(1.0);
        lblNombre.setText(perro.getNombre());
        lblEstadoActual.setText("Estado actual: " + perro.getEstado());
        
        String ruta = "/images/" + (perro.getImagenPath() != null ? perro.getImagenPath() : "logi.png");
        try {
            imgPerro.setImage(new Image(getClass().getResourceAsStream(ruta)));
        } catch (Exception e) {
            imgPerro.setImage(new Image(getClass().getResourceAsStream("/images/logi.png")));
        }
    }

    @FXML private void onMarcarDisponible() { actualizarEstado("Disponible"); }
    @FXML private void onMarcarAdoptado() { actualizarEstado("Adoptado"); }
    @FXML private void onMarcarRevision() { actualizarEstado("En Revisión"); }

    private void actualizarEstado(String nuevoEstado) {
        if (perroSeleccionado == null) return;
        
        perroSeleccionado.setEstado(nuevoEstado);
        
        HttpClient client = HttpClient.newHttpClient();
        String json = new Gson().toJson(perroSeleccionado);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros/" + perroSeleccionado.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            lblEstadoActual.setText("Estado actual: " + nuevoEstado);
                            lvPerros.refresh(); // Refresca la lista visualmente
                        }
                    });
                });
    }

    private void cargarPerros() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    List<Perro> perros = new Gson().fromJson(response.body(), new TypeToken<List<Perro>>(){}.getType());
                    Platform.runLater(() -> {
                        listaOriginal.setAll(perros);
                        
                        // Configurar el buscador
                        FilteredList<Perro> filteredData = new FilteredList<>(listaOriginal, p -> true);
                        txtBuscador.textProperty().addListener((prop, old, newVal) -> {
                            filteredData.setPredicate(perro -> {
                                if (newVal == null || newVal.isEmpty()) return true;
                                return perro.getNombre().toLowerCase().contains(newVal.toLowerCase());
                            });
                        });
                        lvPerros.setItems(filteredData);
                    });
                });
    }
}