package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.Organizador;
import presentacion.NavigationHelper;

import java.util.ArrayList;

/**
 * Controlador para listar y visualizar organizadores registrados.
 * Muestra una tabla con todos los organizadores y permite buscar/filtrar.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 * @since 2025-01-12
 */
public class ListaOrganizadoresController {
    
    // ==================== COMPONENTES FXML ====================
    
    @FXML
    private TableView<Organizador> tablaOrganizadores;
    
    @FXML
    private TableColumn<Organizador, String> colId;
    
    @FXML
    private TableColumn<Organizador, String> colNombre;
    
    @FXML
    private TableColumn<Organizador, String> colEmail;
    
    @FXML
    private TableColumn<Organizador, String> colTelefono;
    
    @FXML
    private TableColumn<Organizador, String> colOrganizacion;
    
    @FXML
    private TableColumn<Organizador, String> colDepartamento;
    
    @FXML
    private TableColumn<Organizador, String> colExperiencia;
    
    @FXML
    private TableColumn<Organizador, String> colEventos;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private Label lblTotalOrganizadores;
    
    @FXML
    private Label lblTotalEventos;
    
    @FXML
    private javafx.scene.layout.VBox panelDetalles;
    
    @FXML
    private TextArea txtDetalles;
    
    // ==================== ATRIBUTOS ====================
    
    private GestorEventos gestor;
    private Stage stage;
    private ObservableList<Organizador> listaOrganizadores;
    private ObservableList<Organizador> listaFiltrada;
    
    // ==================== INICIALIZACIÓN ====================
    
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.listaOrganizadores = FXCollections.observableArrayList();
        this.listaFiltrada = FXCollections.observableArrayList();
        
        configurarTabla();
        configurarBusqueda();
        cargarOrganizadores();
        
        System.out.println("[ListaOrganizadores] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        
        colNombre.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        
        colEmail.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        
        colTelefono.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));
        
        colOrganizacion.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOrganizacion()));
        
        colDepartamento.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartamento()));
        
        colExperiencia.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getExperienciaAnios() + " años"));
        
        colEventos.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getEventosCreados().size())));
        
        tablaOrganizadores.setItems(listaFiltrada);
        
        // Listener para mostrar detalles al seleccionar
        tablaOrganizadores.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetalles(newSelection);
                }
            });
        
        System.out.println("[ListaOrganizadores] Tabla configurada");
    }
    
    /**
     * Configura el campo de búsqueda en tiempo real
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarOrganizadores(newVal);
        });
    }
    
    /**
     * Carga todos los organizadores desde el sistema
     */
    private void cargarOrganizadores() {
        listaOrganizadores.clear();
        
        ArrayList<Organizador> organizadores = gestor.obtenerTodosOrganizadores();
        listaOrganizadores.addAll(organizadores);
        
        filtrarOrganizadores("");
        actualizarEstadisticas();
        
        System.out.println("[ListaOrganizadores] Cargados: " + organizadores.size());
    }
    
    /**
     * Filtra los organizadores según el texto de búsqueda
     */
    private void filtrarOrganizadores(String textoBusqueda) {
        listaFiltrada.clear();
        
        if (textoBusqueda == null || textoBusqueda.isEmpty()) {
            listaFiltrada.addAll(listaOrganizadores);
        } else {
            String busqueda = textoBusqueda.toLowerCase();
            
            for (Organizador org : listaOrganizadores) {
                if (org.getNombre().toLowerCase().contains(busqueda) ||
                    org.getEmail().toLowerCase().contains(busqueda) ||
                    org.getOrganizacion().toLowerCase().contains(busqueda) ||
                    org.getDepartamento().toLowerCase().contains(busqueda)) {
                    
                    listaFiltrada.add(org);
                }
            }
        }
        
        System.out.println("[ListaOrganizadores] Filtrados: " + listaFiltrada.size());
    }
    
    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas() {
        int totalOrganizadores = listaOrganizadores.size();
        int totalEventos = 0;
        
        for (Organizador org : listaOrganizadores) {
            totalEventos += org.getEventosCreados().size();
        }
        
        lblTotalOrganizadores.setText(String.valueOf(totalOrganizadores));
        lblTotalEventos.setText(String.valueOf(totalEventos));
    }
    
    /**
     * Muestra los detalles del organizador seleccionado
     */
    private void mostrarDetalles(Organizador organizador) {
        StringBuilder detalles = new StringBuilder();
        
        detalles.append("═══════════════════════════════════════\n");
        detalles.append("   INFORMACIÓN COMPLETA\n");
        detalles.append("═══════════════════════════════════════\n\n");
        
        detalles.append("📋 ID: ").append(organizador.getId()).append("\n");
        detalles.append("👤 Nombre: ").append(organizador.getNombre()).append("\n");
        detalles.append("📧 Email: ").append(organizador.getEmail()).append("\n");
        detalles.append("📱 Teléfono: ").append(organizador.getTelefono()).append("\n\n");
        
        detalles.append("🏢 Organización: ").append(organizador.getOrganizacion()).append("\n");
        detalles.append("🏛️ Departamento: ").append(organizador.getDepartamento()).append("\n");
        detalles.append("⏳ Experiencia: ").append(organizador.getExperienciaAnios()).append(" años\n\n");
        
        detalles.append("📅 Eventos Creados: ").append(organizador.getEventosCreados().size()).append("\n");
        detalles.append("📆 Registrado: ").append(
            organizador.getFechaRegistro().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
        ).append("\n");
        
        txtDetalles.setText(detalles.toString());
        panelDetalles.setVisible(true);
        panelDetalles.setManaged(true);
    }
    
    // ==================== ACCIONES ====================
    
    /**
     * Actualiza la lista de organizadores
     */
    @FXML
    private void actualizarLista() {
        cargarOrganizadores();
        txtBuscar.clear();
        panelDetalles.setVisible(false);
        panelDetalles.setManaged(false);
        
        System.out.println("[ListaOrganizadores] Lista actualizada");
    }
    
    /**
     * Vuelve al menú de organizadores
     */
    @FXML
    private void volverMenu() {
        if (stage == null) {
            mostrarError("Error", "No se puede volver al menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuOrganizadores.fxml"));
            Parent root = loader.load();
            
            MenuOrganizadoresController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Gestión de Organizadores - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ==================== MENSAJES ====================
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}