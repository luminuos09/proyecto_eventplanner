package presentacion.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Controlador para el diálogo de selección de eventos.
 * Permite filtrar y seleccionar eventos de la lista.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class SeleccionarEventoController {
    
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private Label lblInfoEvento;
    
    private GestorEventos gestorEventos;
    private ObservableList<Evento> todosEventos;
    private Evento eventoSeleccionado;
    private Stage dialogStage;
    private boolean confirmado = false;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        
        configurarTabla();
        cargarFiltros();
        cargarEventos();
        
        // Listener para mostrar info del evento seleccionado
        tablaEventos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarInfoEvento(newSelection);
                }
            }
        );
        
        System.out.println("[SeleccionarEvento] Controlador inicializado");
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTipo().getDescripcion()
            )
        );
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstado().getDescripcion()
            )
        );
        colFecha.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaInicio().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            )
        );
    }
    
    /**
     * Carga los filtros de estado y tipo
     */
    private void cargarFiltros() {
        // Estados
        ObservableList<String> estados = FXCollections.observableArrayList("Todos");
        for (EstadoEvento estado : EstadoEvento.values()) {
            estados.add(estado.getDescripcion());
        }
        cmbEstado.setItems(estados);
        cmbEstado.setValue("Todos");
        
        // Tipos
        ObservableList<String> tipos = FXCollections.observableArrayList("Todos");
        for (TipoEvento tipo : TipoEvento.values()) {
            tipos.add(tipo.getDescripcion());
        }
        cmbTipo.setItems(tipos);
        cmbTipo.setValue("Todos");
    }
    
    /**
     * Carga todos los eventos en la tabla
     */
    private void cargarEventos() {
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        todosEventos = FXCollections.observableArrayList(eventos);
        tablaEventos.setItems(todosEventos);
        
        System.out.println("[SeleccionarEvento] Cargados " + eventos.size() + " eventos");
    }
    
    /**
     * Filtra los eventos según los criterios de búsqueda
     */
    @FXML
    private void filtrarEventos() {
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        String estadoFiltro = cmbEstado.getValue();
        String tipoFiltro = cmbTipo.getValue();
        
        ObservableList<Evento> eventosFiltrados = FXCollections.observableArrayList();
        
        for (Evento evento : todosEventos) {
            boolean cumpleTexto = textoBusqueda.isEmpty() || 
                                 evento.getNombre().toLowerCase().contains(textoBusqueda);
            
            boolean cumpleEstado = estadoFiltro == null || 
                                  estadoFiltro.equals("Todos") || 
                                  evento.getEstado().getDescripcion().equals(estadoFiltro);
            
            boolean cumpleTipo = tipoFiltro == null || 
                                tipoFiltro.equals("Todos") || 
                                evento.getTipo().getDescripcion().equals(tipoFiltro);
            
            if (cumpleTexto && cumpleEstado && cumpleTipo) {
                eventosFiltrados.add(evento);
            }
        }
        
        tablaEventos.setItems(eventosFiltrados);
    }
    
    /**
     * Muestra la información del evento seleccionado
     */
    private void mostrarInfoEvento(Evento evento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        String info = String.format(
            "📍 %s | 📅 %s | 👥 %d/%d participantes | 📊 %.1f%% asistencia",
            evento.getUbicacion(),
            evento.getFechaInicio().format(formatter),
            evento.getParticipantesRegistrados().size(),
            evento.getCapacidadMaxima(),
            evento.calcularPorcentajeAsistencia()
        );
        
        lblInfoEvento.setText(info);
    }
    
    /**
     * Confirma la selección y cierra el diálogo
     */
    @FXML
    private void confirmarSeleccion() {
        eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        
        if (eventoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("No hay evento seleccionado");
            alert.setContentText("Por favor, seleccione un evento de la tabla.");
            alert.showAndWait();
            return;
        }
        
        confirmado = true;
        cerrarDialogo();
    }
    
    /**
     * Cancela la selección y cierra el diálogo
     */
    @FXML
    private void cancelar() {
        confirmado = false;
        cerrarDialogo();
    }
    
    /**
     * Cierra el diálogo
     */
    private void cerrarDialogo() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    
    // ==================== GETTERS Y SETTERS ====================
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public Evento getEventoSeleccionado() {
        return eventoSeleccionado;
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
}