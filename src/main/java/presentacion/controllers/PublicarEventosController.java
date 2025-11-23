package presentacion.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.Evento;
import modelos.EstadoEvento;
import presentacion.NavigationHelper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import excepciones.EventoNoEncontradoException;

/**
 * Controlador para publicar eventos (cambiar de BORRADOR a PUBLICADO)
 */
public class PublicarEventosController {
    
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, Void> colAcciones;
    
    private Stage stage;
    private GestorEventos gestor;
    private ObservableList<Evento> listaEventos;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @FXML
    public void initialize() {
        gestor = GestorEventos.getInstance();
        listaEventos = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarEventos();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipo().toString()));
        
        colFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFechaInicio().format(formatter)));
        
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().getDescripcion()));
        
        // Columna de acciones con botones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnPublicar = new Button("✅ Publicar");
            private final Button btnCancelar = new Button("❌ Cancelar");
            private final HBox hbox = new HBox(10, btnPublicar, btnCancelar);
            
            {
                btnPublicar.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 15; -fx-background-radius: 5; -fx-cursor: hand;");
                btnCancelar.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 15; -fx-background-radius: 5; -fx-cursor: hand;");
                hbox.setAlignment(Pos.CENTER);
                
                btnPublicar.setOnAction(event -> {
                    Evento evento = getTableView().getItems().get(getIndex());
                    publicarEvento(evento);
                });
                
                btnCancelar.setOnAction(event -> {
                    Evento evento = getTableView().getItems().get(getIndex());
                    cancelarEvento(evento);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Evento evento = getTableView().getItems().get(getIndex());
                    // Solo mostrar botones si está en BORRADOR
                    if (evento.getEstado() == EstadoEvento.BORRADOR) {
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        tablaEventos.setItems(listaEventos);
    }
    
    private void cargarEventos() {
        listaEventos.clear();
        // Cargar solo eventos en BORRADOR
        gestor.obtenerTodosEventos().stream()
            .filter(e -> e.getEstado() == EstadoEvento.BORRADOR)
            .forEach(listaEventos::add);
    }
    
    private void publicarEvento(Evento evento) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Publicar Evento");
        confirmacion.setHeaderText("¿Publicar " + evento.getNombre() + "?");
        confirmacion.setContentText("El evento será visible para los participantes.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                evento.setEstado(EstadoEvento.PUBLICADO);
                try {
                    gestor.actualizarEvento(evento);
                } catch (EventoNoEncontradoException | IOException e) {
                    e.printStackTrace();
                }
                
                mostrarExito("Evento Publicado", evento.getNombre() + " ahora es visible para inscripciones.");
                cargarEventos();
            }
        });
    }
    
    private void cancelarEvento(Evento evento) {
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("Cancelar Evento");
        confirmacion.setHeaderText("¿Cancelar " + evento.getNombre() + "?");
        confirmacion.setContentText("El evento no podrá ser publicado.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                evento.setEstado(EstadoEvento.CANCELADO);
                try {
                    gestor.actualizarEvento(evento);
                } catch (EventoNoEncontradoException | IOException e) {
                    e.printStackTrace();
                }
                
                mostrarInfo("Evento Cancelado", evento.getNombre() + " ha sido cancelado.");
                cargarEventos();
            }
        });
    }
    
    @FXML
    private void actualizar() {
        cargarEventos();
    }
    
    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuEventos.fxml"));
            Parent root = loader.load();
            
            MenuEventosController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Gestión de Eventos - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}