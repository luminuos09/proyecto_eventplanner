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
import excepciones.EventPlannerException;
import java.util.ArrayList;

/**
 * Controlador para buscar y visualizar información de organizadores.
 * Permite búsquedas por ID, email o nombre.
 * 
 * @author  Ayner Jose Castro Benavides
 * @version 1.0
 * @since 2025-01-12
 */
public class BuscarOrganizadorController {
    
    // ==================== COMPONENTES FXML ====================
    
    @FXML
    private TextField txtId;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private javafx.scene.layout.VBox panelResultados;
    
    @FXML
    private javafx.scene.layout.VBox panelResultadosMultiples;
    
    @FXML
    private TableView<Organizador> tablaResultados;
    
    @FXML
    private TableColumn<Organizador, String> colResId;
    
    @FXML
    private TableColumn<Organizador, String> colResNombre;
    
    @FXML
    private TableColumn<Organizador, String> colResEmail;
    
    @FXML
    private TableColumn<Organizador, String> colResOrganizacion;
    
    @FXML
    private Label lblNombre;
    
    @FXML
    private Label lblId;
    
    @FXML
    private Label lblEmail;
    
    @FXML
    private Label lblTelefono;
    
    @FXML
    private Label lblOrganizacion;
    
    @FXML
    private Label lblDepartamento;
    
    @FXML
    private Label lblExperiencia;
    
    @FXML
    private Label lblEventosCreados;
    
    // ==================== ATRIBUTOS ====================
    
    private GestorEventos gestor;
    private Stage stage;
    private Organizador organizadorActual;
    private ObservableList<Organizador> listaOrganizadores;
    
    // ==================== INICIALIZACIÓN ====================
    
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.listaOrganizadores = FXCollections.observableArrayList();
        
        configurarTabla();
        ocultarPaneles();
        
        System.out.println("[BuscarOrganizador] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Configura la tabla de resultados múltiples
     */
    private void configurarTabla() {
        colResId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        
        colResNombre.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        
        colResEmail.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        
        colResOrganizacion.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOrganizacion()));
        
        tablaResultados.setItems(listaOrganizadores);
        
        // Listener para selección
        tablaResultados.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetalles(newSelection);
                }
            });
    }
    
    /**
     * Oculta los paneles de resultados
     */
    private void ocultarPaneles() {
        panelResultados.setVisible(false);
        panelResultados.setManaged(false);
        panelResultadosMultiples.setVisible(false);
        panelResultadosMultiples.setManaged(false);
    }
    
    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    /**
     * Busca un organizador por ID
     */
    @FXML
    private void buscarPorId() {
        String id = txtId.getText().trim();
        
        if (id.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un ID");
            return;
        }
        
        try {
            Organizador organizador = gestor.buscarOrganizador(id);
            
            if (organizador == null) {
                mostrarAdvertencia("No encontrado", 
                    "No existe un organizador con el ID: " + id);
                ocultarPaneles();
                return;
            }
            
            mostrarDetalles(organizador);
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            ocultarPaneles();
        }
    }
    
    /**
     * Busca un organizador por email
     */
    @FXML
    private void buscarPorEmail() {
        String email = txtEmail.getText().trim();
        
        if (email.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un email");
            return;
        }
        
        try {
            Organizador organizador = gestor.buscarOrganizadorPorEmail(email);
            
            if (organizador == null) {
                mostrarAdvertencia("No encontrado", 
                    "No existe un organizador con el email: " + email);
                ocultarPaneles();
                return;
            }
            
            mostrarDetalles(organizador);
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            ocultarPaneles();
        }
    }
    
    /**
     * Busca organizadores por nombre (coincidencia parcial)
     */
    @FXML
    private void buscarPorNombre() {
        String nombre = txtNombre.getText().trim();
        
        if (nombre.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un nombre");
            return;
        }
        
        try {
            ArrayList<Organizador> resultados = gestor.buscarOrganizadoresPorNombre(nombre);
            
            if (resultados.isEmpty()) {
                mostrarAdvertencia("Sin resultados", 
                    "No se encontraron organizadores con el nombre: " + nombre);
                ocultarPaneles();
                return;
            }
            
            if (resultados.size() == 1) {
                mostrarDetalles(resultados.get(0));
            } else {
                mostrarResultadosMultiples(resultados);
            }
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            ocultarPaneles();
        }
    }
    
    // ==================== VISUALIZACIÓN ====================
    
    /**
     * Muestra los detalles de un organizador
     */
    private void mostrarDetalles(Organizador organizador) {
        this.organizadorActual = organizador;
        
        lblNombre.setText(organizador.getNombre());
        lblId.setText(organizador.getId());
        lblEmail.setText(organizador.getEmail());
        lblTelefono.setText(organizador.getTelefono());
        lblOrganizacion.setText(organizador.getOrganizacion());
        lblDepartamento.setText(organizador.getDepartamento());
        lblExperiencia.setText(organizador.getExperienciaAnios() + " años");
        lblEventosCreados.setText(String.valueOf(organizador.getEventosCreados().size()));
        
        panelResultados.setVisible(true);
        panelResultados.setManaged(true);
        panelResultadosMultiples.setVisible(false);
        panelResultadosMultiples.setManaged(false);
        
        System.out.println("[BuscarOrganizador] Detalles mostrados: " + organizador.getNombre());
    }
    
    /**
     * Muestra la tabla de resultados múltiples
     */
    private void mostrarResultadosMultiples(ArrayList<Organizador> organizadores) {
        listaOrganizadores.clear();
        listaOrganizadores.addAll(organizadores);
        
        panelResultadosMultiples.setVisible(true);
        panelResultadosMultiples.setManaged(true);
        panelResultados.setVisible(false);
        panelResultados.setManaged(false);
        
        mostrarInformacion("Múltiples resultados", 
            "Se encontraron " + organizadores.size() + 
            " organizadores. Seleccione uno de la tabla.");
    }
    
    // ==================== ACCIONES ====================
    
    /**
     * Limpia la búsqueda y oculta resultados
     */
    @FXML
    private void limpiar() {
        txtId.clear();
        txtEmail.clear();
        txtNombre.clear();
        listaOrganizadores.clear();
        organizadorActual = null;
        ocultarPaneles();
        
        System.out.println("[BuscarOrganizador] Vista limpiada");
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
    
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}