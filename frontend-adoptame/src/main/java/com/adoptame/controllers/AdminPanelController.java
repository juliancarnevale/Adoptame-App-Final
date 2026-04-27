package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.adoptame.models.Solicitud;
import com.adoptame.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AdminPanelController {

    @FXML private VBox panelPerros, panelSolicitudes;

    @FXML private TableView<Perro> tablaPerros;
    @FXML private TableColumn<Perro, Long> colPerroId;
    @FXML private TableColumn<Perro, String> colPerroNombre, colPerroRaza, colPerroTamanio, colPerroSexo, colPerroEstado;
    @FXML private TableColumn<Perro, Integer> colPerroEdad;

    @FXML private TextField txtPerroNombre, txtPerroRaza, txtPerroEdad;
    @FXML private ComboBox<String> cbPerroTamanio, cbPerroSexo, cbPerroEstado;
    @FXML private TextArea txtPerroDesc;
    @FXML private Label lblRutaFoto;

    @FXML private TableView<Solicitud> tablaSolicitudes;
    @FXML private TableColumn<Solicitud, Long> colSolId, colSolPerroId;
    @FXML private TableColumn<Solicitud, String> colSolAdoptante, colSolCorreo, colSolTelefono, colSolEstado;

    @FXML private TextArea txtSolDetalles;

    private Perro perroSeleccionado;
    private Solicitud solicitudSeleccionada;
    private String nombreArchivoImagen;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    
    private static AdminPanelController instance;

    public AdminPanelController() {
        instance = this;
    }

    public static AdminPanelController getInstance() {
        return instance;
    }

    public void showRegistrarMenu() {
        mostrarPanelPerros();
        onLimpiarFormPerro();
    }

    @FXML
    public void initialize() {
        cbPerroTamanio.setItems(FXCollections.observableArrayList("Pequeño", "Mediano", "Grande"));
        cbPerroSexo.setItems(FXCollections.observableArrayList("Macho", "Hembra"));
        cbPerroEstado.setItems(FXCollections.observableArrayList("Disponible", "Adoptado"));

        colPerroId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPerroNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPerroRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colPerroEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colPerroTamanio.setCellValueFactory(new PropertyValueFactory<>("tamanio"));
        colPerroSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colPerroEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colSolId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSolPerroId.setCellValueFactory(new PropertyValueFactory<>("perroId"));
        colSolAdoptante.setCellValueFactory(new PropertyValueFactory<>("nombreAdoptante"));
        colSolCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colSolTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colSolEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaPerros.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                perroSeleccionado = newSelection;
                txtPerroNombre.setText(newSelection.getNombre());
                txtPerroRaza.setText(newSelection.getRaza());
                txtPerroEdad.setText(String.valueOf(newSelection.getEdad()));
                cbPerroTamanio.setValue(newSelection.getTamanio());
                cbPerroSexo.setValue(newSelection.getSexo());
                cbPerroEstado.setValue(newSelection.getEstado());
                txtPerroDesc.setText(newSelection.getDescripcion());
                nombreArchivoImagen = newSelection.getImagenPath();
                lblRutaFoto.setText(nombreArchivoImagen != null ? nombreArchivoImagen : "Sin foto");
            }
        });

        tablaPerros.setRowFactory(tv -> {
            TableRow<Perro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Perro perroDobleClic = row.getItem();
                    abrirDetallesDelPerro(perroDobleClic);
                }
            });
            return row;
        });

        tablaSolicitudes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                solicitudSeleccionada = newSelection;
                txtSolDetalles.setText(
                    "Motivo:\n" + newSelection.getMotivo() + "\n\n" +
                    "Experiencia:\n" + newSelection.getExperiencia() + "\n\n" +
                    "Dirección:\n" + newSelection.getDireccion()
                );
            }
        });

        mostrarPanelPerros();
    }

    private void abrirDetallesDelPerro(Perro perro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adoptame/views/VistaDetallePerroAdmin.fxml"));
            Parent root = loader.load();
            
            DetallePerroAdminController controller = loader.getController();
            controller.setData(perro);
            
            Stage stage = new Stage();
            stage.setTitle("Gestión del Perro - " + perro.getNombre());
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarPanelPerros() {
        panelPerros.setVisible(true);
        panelSolicitudes.setVisible(false);
        cargarPerros();
    }

    @FXML
    private void mostrarPanelSolicitudes() {
        panelPerros.setVisible(false);
        panelSolicitudes.setVisible(true);
        cargarSolicitudes();
    }

    private void cargarPerros() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/perros")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            List<Perro> perros = gson.fromJson(response.body(), new TypeToken<List<Perro>>(){}.getType());
            Platform.runLater(() -> tablaPerros.setItems(FXCollections.observableArrayList(perros)));
        });
    }

    private void cargarSolicitudes() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/solicitudes")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            List<Solicitud> solicitudes = gson.fromJson(response.body(), new TypeToken<List<Solicitud>>(){}.getType());
            Platform.runLater(() -> tablaSolicitudes.setItems(FXCollections.observableArrayList(solicitudes)));
        });
    }

    @FXML
    private void onSeleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(txtPerroNombre.getScene().getWindow());

        if (file != null) {
            try {
                File dirImages = new File("src/main/resources/images/");
                if (!dirImages.exists()) dirImages.mkdirs();
                File dest = new File(dirImages, file.getName());
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                nombreArchivoImagen = file.getName();
                lblRutaFoto.setText(nombreArchivoImagen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onGuardarPerro() {
        if (txtPerroNombre.getText().isEmpty() || txtPerroRaza.getText().isEmpty()) return;

        Perro p = perroSeleccionado != null ? perroSeleccionado : new Perro();
        p.setNombre(txtPerroNombre.getText());
        p.setRaza(txtPerroRaza.getText());
        p.setEdad(Integer.parseInt(txtPerroEdad.getText().isEmpty() ? "0" : txtPerroEdad.getText()));
        p.setTamanio(cbPerroTamanio.getValue());
        p.setSexo(cbPerroSexo.getValue());
        p.setEstado(cbPerroEstado.getValue());
        p.setDescripcion(txtPerroDesc.getText());
        p.setImagenPath(nombreArchivoImagen);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(p)))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
            cargarPerros();
            onLimpiarFormPerro();
        }));
    }

    @FXML
    private void onEliminarPerro() {
        if (perroSeleccionado == null) return;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/perros/" + perroSeleccionado.getId()))
                .DELETE()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(() -> {
            cargarPerros();
            onLimpiarFormPerro();
        }));
    }

    @FXML
    private void onLimpiarFormPerro() {
        perroSeleccionado = null;
        txtPerroNombre.clear();
        txtPerroRaza.clear();
        txtPerroEdad.clear();
        cbPerroTamanio.setValue(null);
        cbPerroSexo.setValue(null);
        cbPerroEstado.setValue(null);
        txtPerroDesc.clear();
        nombreArchivoImagen = null;
        lblRutaFoto.setText("Sin foto");
        tablaPerros.getSelectionModel().clearSelection();
    }

    @FXML
    private void onAprobarSolicitud() {
        actualizarEstadoSolicitud("Aprobada");
    }

    @FXML
    private void onRechazarSolicitud() {
        actualizarEstadoSolicitud("Rechazada");
    }

    private void actualizarEstadoSolicitud(String estado) {
        if (solicitudSeleccionada == null) return;
        solicitudSeleccionada.setEstado(estado);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/solicitudes"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(solicitudSeleccionada)))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> Platform.runLater(this::cargarSolicitudes));
    }

    @FXML
    private void onCerrarSesion() {
        SessionManager.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/adoptame/views/Login.fxml"));
            Stage stage = (Stage) panelPerros.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AdoptaMe - Iniciar Sesión");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}