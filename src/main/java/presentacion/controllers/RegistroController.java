package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.GestorAutenticacion;
import modelos.RolUsuario;
import presentacion.NavigationHelper;

/**
 * Controlador para la pantalla de Registro de usuarios.
 * Solo permite registro de Organizadores.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class RegistroController {
    
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Label lblMensaje;
    
    private Stage stage;
    private GestorAutenticacion gestorAuth;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        gestorAuth = GestorAutenticacion.getInstance();
        
        System.out.println("[Registro] Controlador inicializado");
        System.out.println("[Registro] Solo se permite registro de Organizadores");
    }
    
    /**
     * Establece el stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Maneja el evento de registro
     */
    @FXML
    private void handleRegistro() {
        // Limpiar mensaje previo
        lblMensaje.setVisible(false);
        
        // Obtener datos
        String nombreCompleto = txtNombreCompleto.getText();
        String email = txtEmail.getText();
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();
        
        // Validar campos vacíos
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            mostrarError("Por favor ingrese su nombre completo");
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            mostrarError("Por favor ingrese su email");
            return;
        }
        
        if (usuario == null || usuario.trim().isEmpty()) {
            mostrarError("Por favor ingrese un nombre de usuario");
            return;
        }
        
        if (contrasena == null || contrasena.trim().isEmpty()) {
            mostrarError("Por favor ingrese una contraseña");
            return;
        }
        
        if (confirmar == null || confirmar.trim().isEmpty()) {
            mostrarError("Por favor confirme su contraseña");
            return;
        }
        
        // Validar contraseñas coinciden
        if (!contrasena.equals(confirmar)) {
            mostrarError("Las contraseñas no coinciden");
            txtConfirmarContrasena.clear();
            return;
        }
        // Asignar rol de Organizador
        RolUsuario rol = RolUsuario.ORGANIZADOR;
        
        // Intentar registrar
        try {
            gestorAuth.registrarUsuario(
                nombreCompleto.trim(),
                email.trim(),
                usuario.trim(),
                contrasena,
                rol
            );
            
            mostrarExito("¡Registro exitoso!\n\nTu cuenta está pendiente de aprobación.\nUn administrador revisará tu solicitud pronto.\n\nRedirigiendo al login...");
            
            // Esperar 3 segundos y volver al login
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> volverLogin());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            mostrarError("Error al registrar: " + e.getMessage());
            System.err.println("[Registro] Error: " + e.getMessage());
        }
    }
    
    /**
     * Vuelve a la pantalla de login
     */
    @FXML
    private void volverLogin() {
        if (stage == null) {
            System.err.println("[Registro] Error: Stage es null");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            LoginController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);  
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Event Planner - Login");
            
            System.out.println("[Registro] Volviendo a login");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[Registro] Error al volver a login: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        lblMensaje.setText("❌ " + mensaje);
        lblMensaje.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblMensaje.setVisible(true);
    }
    
    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        lblMensaje.setText("✅ " + mensaje);
        lblMensaje.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblMensaje.setVisible(true);
    }
}