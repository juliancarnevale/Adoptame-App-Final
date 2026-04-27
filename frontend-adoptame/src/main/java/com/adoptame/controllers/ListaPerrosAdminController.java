package com.adoptame.controllers;

import com.adoptame.models.Perro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.io.InputStream;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ListaPerrosAdminController {

    @FXML private TableView<Perro> tablaPerros;
    @FXML private TableColumn<Perro, Long> colId;
    @FXML private TableColumn<Perro, String> colNombre;
    @FXML private TableColumn<Perro, String> colRaza;
    @FXML private TableColumn<Perro, String> colEdad;
    @FXML private TableColumn<Perro, String> colEstado;
    // IMPORTANTE: Esta columna es de tipo Void porque no muestra datos, sino botones
    @FXML private TableColumn<Perro, Void> colAcciones; 
    @FXML private TextField txtBuscador;

    // Listas para el buscador
    private ObservableList<Perro> listaMaestra = FXCollections.observableArrayList();
    private FilteredList<Perro> listaFiltrada;

    @FXML
    public void initialize() {
        // 1. Configurar columnas de datos normales
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // 2. Configurar la columna Nombre (para que se vea azul y detecte doble clic)
        setupNombreColumn();

        // 3. Configurar la columna Acciones (para añadir botones Ver/Estado)
        setupAccionesColumn();

        // 4. Configurar el Buscador en tiempo real
        setupFiltroBusqueda();

        // 5. Configurar el Doble clic en la fila completa
        setupDobleClickFila();

        cargarDatosDesdeBackend();
    }

    @FXML
    private void onBtnRegistrarClick() {
        if (AdminPanelController.getInstance() != null) {
            AdminPanelController.getInstance().showRegistrarMenu();
        }
    }
    
    private void setupNombreColumn() {
        colNombre.setCellFactory(column -> new TableCell<Perro, String>() { // <--- Tipo explícito añadido
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Limpiamos el estilo si está vacío
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                }
            }
        });
    }

    private void setupAccionesColumn() {
        Callback<TableColumn<Perro, Void>, TableCell<Perro, Void>> cellFactory = new Callback<TableColumn<Perro, Void>, TableCell<Perro, Void>>() {
            @Override
            public TableCell<Perro, Void> call(final TableColumn<Perro, Void> param) {
                return new TableCell<Perro, Void>() {
                    private final Button btnVer = new Button("Ver");
                    private final HBox contenedor = new HBox(btnVer);

                    {
                        contenedor.setAlignment(javafx.geometry.Pos.CENTER);
                        btnVer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 15; -fx-border-color: #ccc; -fx-border-radius: 15;");

                        btnVer.setOnAction(event -> {
                            Perro perro = getTableView().getItems().get(getIndex());
                            mostrarImagenPerro(perro);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(contenedor);
                        }
                    }
                };
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void setupFiltroBusqueda() {
        // Enlazamos la lista maestra a la lista filtrada
        listaFiltrada = new FilteredList<>(listaMaestra, p -> true); // Al inicio se ve todo
        
        // Ponemos un "escuchador" en la caja de texto
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(perro -> {
                // Si el buscador está vacío, mostramos todos
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                // Filtramos por nombre o por raza
                if (perro.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                if (perro.getRaza().toLowerCase().contains(lowerCaseFilter)) return true;
                
                return false; // No coincide
            });
        });

        // Enlazamos la tabla a la lista filtrada
        tablaPerros.setItems(listaFiltrada);
    }

    private void setupDobleClickFila() {
        // Configuramos la fábrica de filas para detectar doble clic
        tablaPerros.setRowFactory(tv -> {
            TableRow<Perro> fila = new TableRow<>();
            fila.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!fila.isEmpty())) {
                    Perro perroSeleccionado = fila.getItem();
                    System.out.println("Doble clic en: " + perroSeleccionado.getNombre());
                    abrirVistaDetalle(perroSeleccionado);
                }
            });
            return fila;
        });
    }

    // Método dummy para el botón Ver
    private void mostrarImagenPerro(Perro perro) {
        String nombreArchivo = perro.getImagenPath();
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            mostrarAlerta("Error", "No hay imagen asignada en la DB para " + perro.getNombre());
            return;
        }

        try {
            String ruta = "/images/" + nombreArchivo;
            InputStream res = getClass().getResourceAsStream(ruta);
            
            if (res == null) {
                mostrarAlerta("Error", "No encuentro el archivo: " + nombreArchivo + " en resources/images");
                return;
            }

            Stage stage = new Stage();
            ImageView iv = new ImageView(new Image(res));
            iv.setFitWidth(500);
            iv.setPreserveRatio(true);
            
            stage.setScene(new Scene(new javafx.scene.layout.StackPane(iv)));
            stage.setTitle("Foto de " + perro.getNombre());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Método para abrir la Ficha Detallada (Fase 2)
    private void abrirVistaDetalle(Perro perro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adoptame/views/DetallePerro.fxml"));
            Parent root = loader.load();

            DetallePerroController controller = loader.getController();
            controller.initData(perro);

            Stage stage = new Stage();
            stage.setTitle("Ficha Detallada de " + perro.getNombre());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 850, 600)); 
            
            // 1. Mostramos la ventana y el programa se para aquí hasta que se cierre
            stage.showAndWait(); 
            
            // 2. ¡ESTA ES LA LÍNEA MÁGICA! 
            // Cuando la ventana de detalle se cierra, obligamos a la tabla a recargar los datos
            cargarDatosDesdeBackend(); 
            System.out.println("Lista actualizada automáticamente tras cerrar el detalle.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosDesdeBackend() {
        // ... misma lógica de antes ...
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/perros")).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Gson gson = new Gson();
                    List<Perro> lista = gson.fromJson(json, new TypeToken<List<Perro>>(){}.getType());
                    
                    Platform.runLater(() -> {
                        // IMPORTANTE: Cargamos la información en la listaMaestra
                        listaMaestra.setAll(lista);
                    });
                });
   }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}