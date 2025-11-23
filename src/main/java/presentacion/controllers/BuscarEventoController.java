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
import excepciones.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Controlador para buscar eventos por diferentes criterios
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class BuscarEventoController {
    
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblTotal;
    @FXML private TableView<Evento> tablaResultados;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colCapacidad;
    @FXML private TableColumn<Evento, Void> colAcciones;
    
    private Stage stage;
    private GestorEventos gestor;
    private ObservableList<Evento> resultados;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        gestor = GestorEventos.getInstance();
        resultados = FXCollections.observableArrayList();
        
        configurarComboBox();
        configurarTabla();
        
        System.out.println("[BuscarEvento] Controlador inicializado");
    }
    
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Configura el ComboBox de estados
     */
    private void configurarComboBox() {
        ObservableList<String> estados = FXCollections.observableArrayList();
        for (EstadoEvento estado : EstadoEvento.values()) {
            estados.add(estado.getDescripcion());
        }
        cmbEstado.setItems(estados);
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipo().toString()));
        
        colFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFechaInicio().format(formatter)));
        
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().getDescripcion()));
        
        colCapacidad.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%d/%d",
                cellData.getValue().getParticipantesRegistrados().size(),
                cellData.getValue().getCapacidadMaxima())));
        
        // Botón para ver detalle
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️ Ver");
            
            {
                btnVer.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; " +
                              "-fx-font-size: 12px; -fx-padding: 5 15; -fx-background-radius: 5; " +
                              "-fx-cursor: hand;");
                btnVer.setOnAction(event -> {
                    Evento evento = getTableView().getItems().get(getIndex());
                    mostrarDetalle(evento);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(btnVer);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
        
        tablaResultados.setItems(resultados);
    }
    
    /**
     * Busca evento por ID
     */
    @FXML
    private void buscarPorId() {
        String id = txtId.getText().trim();
        
        if (id.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Por favor ingrese un ID");
            return;
        }
        
        try {
            Evento evento = gestor.buscarEvento(id);
            mostrarResultados(new ArrayList<>() {{ add(evento); }});
            System.out.println("[BuscarEvento] Evento encontrado: " + evento.getNombre());
            
        } catch (EventoNoEncontradoException e) {
            mostrarAdvertencia("No encontrado", "No existe un evento con ese ID");
            limpiarResultados();
        } catch (Exception e) {
            mostrarError("Error", "Error al buscar: " + e.getMessage());
        }
    }
    
    /**
     * Busca eventos por nombre (búsqueda parcial)
     */
    @FXML
    private void buscarPorNombre() {
        String nombre = txtNombre.getText().trim().toLowerCase();
        
        if (nombre.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Por favor ingrese un nombre");
            return;
        }
        
        ArrayList<Evento> eventosEncontrados = new ArrayList<>();
        
        for (Evento evento : gestor.obtenerTodosEventos()) {
            if (evento.getNombre().toLowerCase().contains(nombre)) {
                eventosEncontrados.add(evento);
            }
        }
        
        if (eventosEncontrados.isEmpty()) {
            mostrarInfo("Sin resultados", "No se encontraron eventos con ese nombre");
            limpiarResultados();
        } else {
            mostrarResultados(eventosEncontrados);
            System.out.println("[BuscarEvento] " + eventosEncontrados.size() + " eventos encontrados");
        }
    }
    
    /**
     * Busca eventos por estado
     */
    @FXML
    private void buscarPorEstado() {
        String estadoSeleccionado = cmbEstado.getValue();
        
        if (estadoSeleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Por favor seleccione un estado");
            return;
        }
        
        // Convertir descripción a EstadoEvento
        EstadoEvento estado = null;
        for (EstadoEvento e : EstadoEvento.values()) {
            if (e.getDescripcion().equals(estadoSeleccionado)) {
                estado = e;
                break;
            }
        }
        
        if (estado == null) return;
        
        ArrayList<Evento> eventosEncontrados = gestor.buscarEventosPorEstado(estado);
        
        if (eventosEncontrados.isEmpty()) {
            mostrarInfo("Sin resultados", "No hay eventos con ese estado");
            limpiarResultados();
        } else {
            mostrarResultados(eventosEncontrados);
            System.out.println("[BuscarEvento] " + eventosEncontrados.size() + " eventos encontrados");
        }
    }
    
    /**
     * Muestra los resultados en la tabla
     */
    private void mostrarResultados(ArrayList<Evento> eventos) {
        resultados.clear();
        resultados.addAll(eventos);
        lblTotal.setText(eventos.size() + " eventos encontrados");
    }
    
    /**
     * Limpia los resultados
     */
    private void limpiarResultados() {
        resultados.clear();
        lblTotal.setText("0 eventos encontrados");
    }
    
    /**
     * Muestra el detalle de un evento
     */
    private void mostrarDetalle(Evento evento) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del Evento");
        alert.setHeaderText(evento.getTipo().getIcono() + " " + evento.getNombre());
        
        String contenido = String.format(
            "ID: %s\n\n" +
            "📋 Descripción:\n%s\n\n" +
            "📅 Fecha Inicio: %s\n" +
            "📅 Fecha Fin: %s\n" +
            "📍 Ubicación: %s\n" +
            "👥 Capacidad: %d/%d\n" +
            "📊 Estado: %s\n\n" +
            "✅ Inscritos: %d\n" +
            "✔️ Asistentes: %d",
            evento.getId(),
            evento.getDescripcion(),
            evento.getFechaInicio().format(formatter),
            evento.getFechaFin().format(formatter),
            evento.getUbicacion(),
            evento.getParticipantesRegistrados().size(),
            evento.getCapacidadMaxima(),
            evento.getEstado().getDescripcion(),
            evento.getParticipantesRegistrados().size(),
            evento.getParticipantesAsistentes().size()
        );
        
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    /**
     * Limpia el formulario
     */
    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        cmbEstado.setValue(null);
        limpiarResultados();
        System.out.println("[BuscarEvento] Formulario limpiado");
    }
    
    /**
     * Vuelve al menú de eventos
     */
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
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ============== MÉTODOS AUXILIARES ==============
    
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}