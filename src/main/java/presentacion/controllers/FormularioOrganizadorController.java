package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.Organizador;
import presentacion.NavigationHelper;
import excepciones.EventPlannerException;

/**
 * Controlador para el formulario de registro de organizadores.
 * Permite crear nuevos organizadores en el sistema.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 * @since 2025-01-12
 */
public class FormularioOrganizadorController {
    
    // ==================== COMPONENTES FXML ====================
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private TextField txtTelefono;
    
    @FXML
    private TextField txtOrganizacion;
    
    @FXML
    private TextField txtDepartamento;
    
    @FXML
    private Spinner<Integer> spnExperiencia;
    
    // ==================== ATRIBUTOS ====================
    
    private GestorEventos gestor;
    private Stage stage;
    
    // ==================== INICIALIZACIÓN ====================
    
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        
        // Configurar el Spinner
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0);
        spnExperiencia.setValueFactory(valueFactory);
        
        System.out.println("[FormularioOrganizador] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // ==================== ACCIONES ====================
    
    /**
     * Guarda el organizador en el sistema
     */
    @FXML
    private void guardarOrganizador() {
        System.out.println("[FormularioOrganizador] Intentando guardar organizador...");
        
        // Validar campos
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Obtener datos del formulario
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String organizacion = txtOrganizacion.getText().trim();
            String departamento = txtDepartamento.getText().trim();
            int experiencia = spnExperiencia.getValue();
            
            // Registrar organizador
            Organizador organizador = gestor.registrarOrganizador(
                nombre, email, telefono, organizacion, departamento, experiencia
            );
            
            System.out.println("[FormularioOrganizador] Organizador guardado: " + organizador.getId());
            
            mostrarExito("Organizador Registrado", 
                "El organizador ha sido registrado exitosamente.\nID: " + organizador.getId());
            
            limpiarFormulario();
            
        } catch (EventPlannerException e) {
            System.err.println("[FormularioOrganizador] Error: " + e.getMessage());
            mostrarError("Error al registrar", e.getMessage());
        }
    }
    
    /**
     * Limpia todos los campos del formulario
     */
    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtOrganizacion.clear();
        txtDepartamento.clear();
        spnExperiencia.getValueFactory().setValue(0);
        
        txtNombre.requestFocus();
        
        System.out.println("[FormularioOrganizador] Formulario limpiado");
    }
    
    /**
     * Cancela y vuelve al menú de organizadores
     */
    @FXML
    private void cancelar() {
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
            stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Gestión de Organizadores - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ==================== VALIDACIONES ====================
    
    /**
     * Valida que todos los campos obligatorios estén completos
     */
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El email es obligatorio");
            txtEmail.requestFocus();
            return false;
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El teléfono es obligatorio");
            txtTelefono.requestFocus();
            return false;
        }
        
        if (txtOrganizacion.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "La organización es obligatoria");
            txtOrganizacion.requestFocus();
            return false;
        }
        
        if (txtDepartamento.getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo vacío", "El departamento es obligatorio");
            txtDepartamento.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // ==================== MENSAJES ====================
    
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
    
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("✓ Operación exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}