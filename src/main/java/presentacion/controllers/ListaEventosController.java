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
import modelos.TipoEvento;
import presentacion.NavigationHelper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Controlador para listar todos los eventos del sistema
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class ListaEventosController {
    
    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private Label lblTotal;
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colUbicacion;
    @FXML private TableColumn<Evento, String> colCapacidad;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, Void> colAcciones;
    
    private Stage stage;
    private GestorEventos gestor;
    private ObservableList<Evento> listaEventos;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        gestor = GestorEventos.getInstance();
        listaEventos = FXCollections.observableArrayList();
        
        configurarTabla();
        configurarFiltros();
        cargarEventos();
        
        System.out.println("[ListaEventos] Controlador inicializado");
    }
    
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("[ListaEventos] Stage asignado");
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipo().getIcono() + " " + 
                                    cellData.getValue().getTipo().toString()));
        
        colFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFechaInicio().format(formatter)));
        
        colUbicacion.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUbicacion()));
        
        colCapacidad.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%d/%d",
                cellData.getValue().getParticipantesRegistrados().size(),
                cellData.getValue().getCapacidadMaxima())));
        
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().getDescripcion()));
                                 
        
        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("Ver");
            
            {
                btnVer.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; " +
                              "-fx-font-size: 14px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                              "-fx-cursor: hand;");
                btnVer.setOnAction(event -> {
                    Evento evento = getTableView().getItems().get(getIndex());
                    verDetalleEvento(evento);
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
        
        tablaEventos.setItems(listaEventos);
    }
    
    /**
     * Configura los ComboBox de filtros
     */
    private void configurarFiltros() {
        // Filtro de estado
        ObservableList<String> estados = FXCollections.observableArrayList("Todos");
        for (EstadoEvento estado : EstadoEvento.values()) {
            estados.add(estado.getDescripcion());
        }
        cmbFiltroEstado.setItems(estados);
        cmbFiltroEstado.setValue("Todos");
        cmbFiltroEstado.setOnAction(e -> aplicarFiltros());
        
        // Filtro de tipo
        ObservableList<String> tipos = FXCollections.observableArrayList("Todos");
        for (TipoEvento tipo : TipoEvento.values()) {
            tipos.add(tipo.toString());
        }
        cmbFiltroTipo.setItems(tipos);
        cmbFiltroTipo.setValue("Todos");
        cmbFiltroTipo.setOnAction(e -> aplicarFiltros());
        
        // Búsqueda por texto
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }
    
    /**
     * Carga todos los eventos
     */
    private void cargarEventos() {
        listaEventos.clear();
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        listaEventos.addAll(eventos);
        actualizarTotal();
        
        System.out.println("[ListaEventos] " + eventos.size() + " eventos cargados");
    }
    
    /**
     * Aplica filtros a la lista
     */
    private void aplicarFiltros() {
        String textoBusqueda = txtBusqueda.getText().toLowerCase().trim();
        String estadoFiltro = cmbFiltroEstado.getValue();
        String tipoFiltro = cmbFiltroTipo.getValue();
        
        ArrayList<Evento> eventosFiltrados = new ArrayList<>();
        
        for (Evento evento : gestor.obtenerTodosEventos()) {
            boolean cumpleFiltros = true;
            
            // Filtro por texto
            if (!textoBusqueda.isEmpty()) {
                if (!evento.getNombre().toLowerCase().contains(textoBusqueda)) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por estado
            if (!estadoFiltro.equals("Todos")) {
                if (!evento.getEstado().getDescripcion().equals(estadoFiltro)) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por tipo
            if (!tipoFiltro.equals("Todos")) {
                if (!evento.getTipo().toString().equals(tipoFiltro)) {
                    cumpleFiltros = false;
                }
            }
            
            if (cumpleFiltros) {
                eventosFiltrados.add(evento);
            }
        }
        
        listaEventos.clear();
        listaEventos.addAll(eventosFiltrados);
        actualizarTotal();
    }
    
    /**
     * Actualiza el contador total
     */
    private void actualizarTotal() {
        lblTotal.setText(String.format("Total: %d eventos", listaEventos.size()));
    }
    
    /**
     * Muestra el detalle de un evento
     */
    private void verDetalleEvento(Evento evento) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del Evento");
        alert.setHeaderText(evento.getTipo().getIcono() + " " + evento.getNombre());
        
        String contenido = String.format(
            "📋 Descripción: %s\n\n" +
            "📅 Fecha Inicio: %s\n" +
            "📅 Fecha Fin: %s\n" +
            "📍 Ubicación: %s\n" +
            "👥 Capacidad: %d/%d\n" +
            "📊 Estado: %s\n" +
            "👤 Organizador: %s\n\n" +
            "✅ Inscritos: %d\n" +
            "✔️ Asistentes: %d\n" +
            "📈 Porcentaje Asistencia: %.1f%%",
            evento.getDescripcion(),
            evento.getFechaInicio().format(formatter),
            evento.getFechaFin().format(formatter),
            evento.getUbicacion(),
            evento.getParticipantesRegistrados().size(),
            evento.getCapacidadMaxima(),
            evento.getEstado().getDescripcion(),
            evento.getOrganizadorId(),
            evento.getParticipantesRegistrados().size(),
            evento.getParticipantesAsistentes().size(),
            evento.calcularPorcentajeAsistencia()
        );
        
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    /**
     * Actualiza la lista
     */
    @FXML
    private void actualizar() {
        cargarEventos();
        System.out.println("[ListaEventos] Lista actualizada");
    }
    
    /**
     * Abre formulario de nuevo evento
     */
    @FXML
    private void nuevoEvento() {
        abrirVista("/fxml/FormularioEvento.fxml", "Crear Evento - Event Planner");
    }
    
    /**
     * Ver detalle del evento seleccionado
     */
    @FXML
    private void verDetalle() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", 
                "Por favor seleccione un evento de la tabla");
            return;
        }
        
        verDetalleEvento(seleccionado);
    }
    
    /**
     * Vuelve al menú de eventos
     */
    @FXML
    private void volver() {
        abrirVista("/fxml/MenuEventos.fxml", "Gestión de Eventos - Event Planner");
    }
    
    /**
     * Método auxiliar para abrir vistas
     */
    private void abrirVista(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof FormularioEventoController) {
                ((FormularioEventoController) controller).setStage(stage);
            } else if (controller instanceof MenuEventosController) {
                ((MenuEventosController) controller).setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Menu Gestión de Eventos - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista");
        }
    }
    
    /**
     * Muestra mensaje de advertencia
     */
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}