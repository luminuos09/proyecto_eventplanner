package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logica.GestorEventos;
import modelos.Participante;
import modelos.Evento;
import excepciones.*;
import presentacion.NavigationHelper;

import java.util.ArrayList;

/**
 * Controlador para mostrar los eventos de un participante
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class MisEventosController {
    
    @FXML private TextField txtEmailParticipante;
    @FXML private Label lblParticipante;
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colUbicacion;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colAsistio;
    
    private GestorEventos gestor;
    private Stage stage;
    private Participante participanteSeleccionado;
    private ObservableList<Evento> listaEventos;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.listaEventos = FXCollections.observableArrayList();
        
        configurarTabla();
    }
    
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        
        // Columna Tipo con icono
        colTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipo().getIcono() + " " + 
                        cellData.getValue().getTipo().toString();
            return new javafx.beans.property.SimpleStringProperty(tipo);
        });
        
        // Columna Fecha formateada
        colFecha.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaInicio()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });
        
        // Columna Estado
        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado().toString();
            return new javafx.beans.property.SimpleStringProperty(estado);
        });
        
        // Columna Asistió (check-in)
        colAsistio.setCellValueFactory(cellData -> {
            Evento evento = cellData.getValue();
            boolean asistio = participanteSeleccionado != null && 
                            evento.getParticipantesAsistentes().contains(participanteSeleccionado.getId());
            String texto = asistio ? "SÍ" : "NO";
            return new javafx.beans.property.SimpleStringProperty(texto);
        });
        
        tablaEventos.setItems(listaEventos);
    }
    
    /**
     * Busca el participante por email
     */
    @FXML
    private void buscarParticipante() {
        String email = txtEmailParticipante.getText().trim();
        
        if (email.isEmpty()) {
            mostrarAdvertencia("Email requerido", "Por favor ingrese un email");
            return;
        }
        
        try {
            participanteSeleccionado = gestor.buscarParticipantePorEmail(email);
            
            lblParticipante.setText(String.format(
                "%s - %d eventos registrados",
                participanteSeleccionado.getNombre(),
                participanteSeleccionado.getEventosRegistrados().size()
            ));
            lblParticipante.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            
            cargarEventos();
            
            System.out.println("Participante encontrado: " + participanteSeleccionado.getNombre());
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "Error al buscar participante: " + e.getMessage());
        }
    }
    
    /**
     * Carga los eventos del participante
     */
    private void cargarEventos() {
        listaEventos.clear();
        
        if (participanteSeleccionado == null) {
            return;
        }
        
        try {
            ArrayList<String> idsEventos = participanteSeleccionado.getEventosRegistrados();
            
            for (String idEvento : idsEventos) {
                try {
                    Evento evento = gestor.buscarEvento(idEvento);
                    listaEventos.add(evento);
                } catch (EventoNoEncontradoException e) {
                    System.err.println("⚠️ Evento no encontrado: " + idEvento);
                }
            }
            
            System.out.println(" " + listaEventos.size() + " eventos cargados");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "Error al cargar eventos: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la lista de eventos
     */
    @FXML
    private void actualizar() {
        if (participanteSeleccionado != null) {
            buscarParticipante();
        }
    }
    
    /**
     * Vuelve al menú de participantes
     */
    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuParticipantes.fxml"));
            Parent root = loader.load();
            
            MenuParticipantesController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);  
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            
            stage.setTitle("Gestión de Participantes - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ============== MÉTODOS AUXILIARES ==============
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
}