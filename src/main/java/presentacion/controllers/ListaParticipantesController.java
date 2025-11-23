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
import presentacion.NavigationHelper;


import java.util.ArrayList;

/**
 * Controlador para la lista de participantes
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class ListaParticipantesController {
    
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;
    @FXML private TableView<Participante> tablaParticipantes;
    @FXML private TableColumn<Participante, String> colId;
    @FXML private TableColumn<Participante, String> colNombre;
    @FXML private TableColumn<Participante, String> colEmail;
    @FXML private TableColumn<Participante, String> colTelefono;
    @FXML private TableColumn<Participante, String> colEmpresa;
    @FXML private TableColumn<Participante, String> colCargo;
    @FXML private TableColumn<Participante, String> colVip;
    
    private GestorEventos gestor;
    private Stage stage;
    private ObservableList<Participante> listaParticipantes;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.listaParticipantes = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarParticipantes();
        aplicarEstilos();
    }
    
    private void aplicarEstilos() {
    javafx.application.Platform.runLater(() -> {
        try {
            Scene scene = tablaParticipantes.getScene();
            if (scene != null) {
                var cssUrl = getClass().getResource("/styles.css");
                if (cssUrl != null) {
                    String cssPath = cssUrl.toExternalForm();
                    if (!scene.getStylesheets().contains(cssPath)) {
                        scene.getStylesheets().add(cssPath);
                        System.out.println("✅ CSS aplicado a ListaParticipantes");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error aplicando CSS: " + e.getMessage());
        }
    });
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
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        
        // Columna VIP con formato especial
        colVip.setCellValueFactory(cellData -> {
            String vipStatus = cellData.getValue().isVip() ? " SÍ" : " NO";
            return new javafx.beans.property.SimpleStringProperty(vipStatus);
        });
        
        tablaParticipantes.setItems(listaParticipantes);
    }
    
    /**
     * Carga todos los participantes
     */
    private void cargarParticipantes() {
        try {
            ArrayList<Participante> participantes = gestor.obtenerTodosParticipantes();
            listaParticipantes.clear();
            listaParticipantes.addAll(participantes);
            
            lblTotal.setText("Total: " + participantes.size() + " participantes");
            
            System.out.println(" " + participantes.size() + " participantes cargados");
            
        } catch (Exception e) {
            System.err.println(" Error al cargar participantes:");
            e.printStackTrace();
            mostrarError("Error", "No se pudieron cargar los participantes");
        }
    }
    
    /**
     * Busca participantes por nombre o email
     */
    @FXML
    private void buscar() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        
        if (textoBusqueda.isEmpty()) {
            cargarParticipantes();
            return;
        }
        
        ArrayList<Participante> todosParticipantes = gestor.obtenerTodosParticipantes();
        listaParticipantes.clear();
        
        for (Participante p : todosParticipantes) {
            if (p.getNombre().toLowerCase().contains(textoBusqueda) ||
                p.getEmail().toLowerCase().contains(textoBusqueda)) {
                listaParticipantes.add(p);
            }
        }
        
        lblTotal.setText("Encontrados: " + listaParticipantes.size() + " participantes");
    }
    
    /**
     * Actualiza la lista
     */
    @FXML
    private void actualizarLista() {
        txtBuscar.clear();
        cargarParticipantes();
        mostrarInfo("Actualizado", "Lista de participantes actualizada");
    }
    
    /**
     * Abre el formulario para crear nuevo participante
     */
    @FXML
    private void abrirFormularioNuevo() {
        System.out.println("Abriendo formulario de participantes...");
        
        if (stage == null) {
            System.err.println("ERROR: stage es null en abrirFormularioParticipante");
            mostrarError("Error", "No se puede abrir el formulario");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormularioParticipante.fxml"));
            Parent root = loader.load();
            
            FormularioParticipanteController controller = loader.getController();
            
            if (controller != null) {
                controller.setStage(stage);  // ⬅️ CRÍTICO
                System.out.println("Stage asignado a FormularioParticipanteController");
            }
            
            Scene scene = new Scene(root);
                NavigationHelper.aplicarCSS(scene);
                this.stage.setScene(scene);
                this.stage.setMaximized(false);
                this.stage.setMaximized(true);
                 
            stage.setTitle("Registrar Participante - Event Planner");
            
            System.out.println("Formulario de participante abierto");
            
        } catch (Exception e) {
            System.err.println("Error al abrir formulario:");
            e.printStackTrace();
            mostrarError("Error al abrir formulario", "No se pudo abrir el formulario de participantes: " + e.getMessage());
        }
    }
    
    /**
     * Muestra el detalle del participante seleccionado
     */
    @FXML
    private void verDetalle() {
        Participante seleccionado = tablaParticipantes.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Por favor seleccione un participante de la tabla");
            return;
        }
        
        String detalle = String.format(
            " INFORMACIÓN DEL PARTICIPANTE\n\n" +
            "ID: %s\n" +
            "Nombre: %s\n" +
            "Email: %s\n" +
            "Teléfono: %s\n" +
            "Empresa: %s\n" +
            "Cargo: %s\n" +
            "Intereses: %s\n" +
            "VIP: %s\n" +
            "Eventos registrados: %d\n" +
            "Fecha de registro: %s",
            seleccionado.getId(),
            seleccionado.getNombre(),
            seleccionado.getEmail(),
            seleccionado.getTelefono(),
            seleccionado.getEmpresa(),
            seleccionado.getCargo(),
            seleccionado.getIntereses(),
            seleccionado.isVip() ? " SÍ" : " NO",
            seleccionado.getEventosRegistrados().size(),
            seleccionado.getFechaRegistro()
        );
        
        mostrarInfo("Detalle del Participante", detalle);
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
            
            //  APLICAR CSS
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);  
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Gestión de Participantes - Event Planner");
            
            System.out.println("Vuelto a Menú de Participantes");
            
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
}