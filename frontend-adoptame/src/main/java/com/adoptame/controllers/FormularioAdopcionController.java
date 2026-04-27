package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.adoptame.models.User;
import com.adoptame.utils.SessionManager;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class FormularioAdopcionController {

    @FXML private Label lblSubtitulo;
    @FXML private TextField txtNombre, txtTelefono, txtCorreo, txtDireccion;
    @FXML private TextArea txtExperiencia, txtMotivo;
    @FXML private CheckBox chkTerminos;

    private Perro perro;

    // MÉTODO CORREGIDO: Ahora se llama setData para sincronizar con DetallePerroAdoptanteController
    public void setData(Perro perro) {
        this.perro = perro;
        if (lblSubtitulo != null && perro != null) {
            lblSubtitulo.setText("Solicitud para adoptar a " + perro.getNombre());
        }
    }

    @FXML
    public void initialize() {
        User user = SessionManager.getUser();
        if (user != null) {
            txtNombre.setText(user.getNombre());
            txtCorreo.setText(user.getEmail());
            txtTelefono.setText(user.getTelefono());
            txtNombre.setEditable(false);
            txtCorreo.setEditable(false);
        }
    }
    
    @FXML
    private void onCancelar() {
        irAVista("/com/adoptame/views/VistaCatalogo.fxml");
    }

    @FXML
    private void onEnviarSolicitud() {
        if (!chkTerminos.isSelected()) {
            mostrarAlerta("Error", "Debes aceptar los términos y condiciones.");
            return;
        }

        if (txtTelefono.getText().isEmpty() || txtDireccion.getText().isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa los campos obligatorios.");
            return;
        }

        if (perro == null) {
            mostrarAlerta("Error", "No se ha seleccionado ningún perro para la solicitud.");
            return;
        }

        Map<String, Object> solicitudData = new HashMap<>();
        solicitudData.put("perroId", perro.getId());
        solicitudData.put("nombreAdoptante", txtNombre.getText());
        solicitudData.put("telefono", txtTelefono.getText());
        solicitudData.put("correo", txtCorreo.getText());
        solicitudData.put("direccion", txtDireccion.getText());
        solicitudData.put("experiencia", txtExperiencia.getText());
        solicitudData.put("motivo", txtMotivo.getText());
        solicitudData.put("estado", "Pendiente");

        String jsonBody = new Gson().toJson(solicitudData);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/solicitudes"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        mostrarAlerta("Éxito", "Tu solicitud ha sido enviada correctamente.");
                        irAVista("/com/adoptame/views/VistaMisSolicitudes.fxml");
                    } else {
                        mostrarAlerta("Error", "No se pudo enviar la solicitud. Código: " + response.statusCode());
                    }
                }));
    }

    // Método de utilidad para cambiar de vista sin repetir código
    private void irAVista(String fxmlPath) {
        InicioAdoptanteController main = InicioAdoptanteController.getInstance();
        if (main != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent view = loader.load();
                main.cargarVista(view);
            } catch (IOException e) {
                System.err.println("Error al cargar la vista: " + fxmlPath);
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}