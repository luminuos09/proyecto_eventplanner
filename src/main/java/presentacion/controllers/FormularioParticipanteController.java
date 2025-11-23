package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.GestorEventos;
import logica.ValidarDatos;
import modelos.Participante;
import presentacion.NavigationHelper;
import excepciones.DatosInvalidosException;
import excepciones.EventPlannerException;

/**
 * Controlador para el formulario de registro de participantes
 * Permite crear y registrar nuevos participantes en el sistema
 * 
 * @author  Ayner Jose Castro Benavides
 * @version 1.0
 */
public class FormularioParticipanteController {
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmpresa;
    @FXML private TextField txtCargo;
    @FXML private TextArea txtIntereses;
    @FXML private CheckBox chkVip;
    @FXML private Label lblMensaje;
    
    private GestorEventos gestor;
    private Stage stage;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
    }
    
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Guarda un nuevo participante en el sistema
     */
    @FXML
    private void guardarParticipante() {
        try {
            // Validar campos obligatorios
            validarCamposObligatorios();
            
            // Obtener datos del formulario
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String empresa = txtEmpresa.getText().trim();
            String cargo = txtCargo.getText().trim();
            String intereses = txtIntereses.getText().trim();
            boolean esVip = chkVip.isSelected();
            
            // Validar formato de datos
            ValidarDatos.validarNombre(nombre);
            ValidarDatos.validarEmail(email);
            ValidarDatos.validarTelefono(telefono);
            
            // Registrar participante
            Participante participante = gestor.registrarParticipante(
                nombre, 
                email, 
                telefono, 
                empresa, 
                cargo, 
                intereses,
                esVip
            );
            
            // Mostrar mensaje de éxito
            mostrarExito("Participante registrado exitosamente!\n\n" +
                        "ID: " + participante.getId() + "\n" +
                        "Nombre: " + participante.getNombre() + "\n" +
                        "Email: " + participante.getEmail() +
                        (esVip ? "\nMarcado como VIP" : ""));
            
            // Limpiar formulario
            limpiarFormulario();
            
        } catch (DatosInvalidosException e) {
            mostrarError("Datos inválidos", e.getMessage());
        } catch (EventPlannerException e) {
            mostrarError("Error al registrar", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado", "Ocurrió un error al guardar el participante: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Valida que los campos obligatorios estén llenos
     */
    private void validarCamposObligatorios() throws DatosInvalidosException {
        if (txtNombre.getText().trim().isEmpty()) {
            txtNombre.requestFocus();
            throw new DatosInvalidosException("nombre", "El nombre es obligatorio");
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            txtEmail.requestFocus();
            throw new DatosInvalidosException("email", "El email es obligatorio");
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            txtTelefono.requestFocus();
            throw new DatosInvalidosException("telefono", "El teléfono es obligatorio");
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
        txtEmpresa.clear();
        txtCargo.clear();
        txtIntereses.clear();
        chkVip.setSelected(false);
        lblMensaje.setVisible(false);
        
        // Enfocar el primer campo
        txtNombre.requestFocus();
    }
    
    /**
 * Vuelve al menú de participantes
 */
@FXML
private void volver() {
    System.out.println("Intentando volver al menú de participantes...");
    System.out.println("   Stage actual: " + (this.stage != null));
    
    if (this.stage == null) {
        System.err.println("ERROR: stage es null en FormularioParticipanteController");
        mostrarError("Error", "No se puede volver al menú. Stage no inicializado.");
        return;
    }
    
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuParticipantes.fxml"));
        Parent root = loader.load();
        
        MenuParticipantesController controller = loader.getController();
        if (controller != null) {
            controller.setStage(this.stage);
            System.out.println("Stage asignado a MenuParticipantesController");
        }
        
        Scene scene = new Scene(root);
        
        // Aplicar CSS
        NavigationHelper.aplicarCSS(scene);
        this.stage.setScene(scene);
        this.stage.setMaximized(false);
        this.stage.setMaximized(true);  
        this.stage.setTitle("Gestión de Participantes - Event Planner");
        
        System.out.println("Vuelto al menú de participantes");
        
    } catch (Exception e) {
        System.err.println("Error al volver:");
        e.printStackTrace();
        mostrarError("Error", "No se pudo volver al menú: " + e.getMessage());
    }
}
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación exitosa");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}