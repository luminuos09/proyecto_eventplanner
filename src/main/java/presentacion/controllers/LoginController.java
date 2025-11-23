package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.GestorAutenticacion;
import excepciones.*;
import presentacion.NavigationHelper;

/**
 * Controlador para la pantalla de Login.
 * Maneja la autenticación de usuarios.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class LoginController {
    
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    
    private Stage stage;
    private GestorAutenticacion gestorAuth;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        gestorAuth = GestorAutenticacion.getInstance();
        
        // Limpiar error al escribir
        txtUsuario.textProperty().addListener((obs, old, nuevo) -> {
            if (lblError.isVisible()) {
                lblError.setVisible(false);
            }
        });
        
        txtContrasena.textProperty().addListener((obs, old, nuevo) -> {
            if (lblError.isVisible()) {
                lblError.setVisible(false);
            }
        });
        
        System.out.println("[Login] Controlador inicializado");
        System.out.println("[Login] Usuario por defecto: admin | Contraseña: admin123");
    }
    
    /**
     * Establece el stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Maneja el evento de login
     */
    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();
        
        // Validar campos vacíos
        if (usuario == null || usuario.trim().isEmpty()) {
            mostrarError("Por favor ingrese su usuario");
            txtUsuario.requestFocus();
            return;
        }
        
        if (contrasena == null || contrasena.trim().isEmpty()) {
            mostrarError("Por favor ingrese su contraseña");
            txtContrasena.requestFocus();
            return;
        }
        
        // Intentar login
        System.out.println("[Login] Intentando login con: " + usuario);
        
        try {
            if (gestorAuth.login(usuario, contrasena)) {
                System.out.println("[Login] Login exitoso");
                abrirMenuPrincipal();
            } else {
                System.out.println("[Login] Login fallido");
                mostrarError("Usuario o contraseña incorrectos");
                txtContrasena.clear();
                txtUsuario.requestFocus();
            }
        } catch (CuentaPendienteException e) {
            mostrarAdvertencia("Cuenta Pendiente", e.getMessage());
        } catch (CuentaInactivaException e) {
            mostrarError("Cuenta Inactiva", e.getMessage());
        } catch (CuentaRechazadaException e) {
            mostrarError("Cuenta Rechazada", e.getMessage());
        }
    }
    
    /**
     * Abre el menú principal
     */
    private void abrirMenuPrincipal() {
        if (stage == null) {
            System.err.println("[Login] Error: Stage es null");
            return;
        }
        
        try {        
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            
            MenuPrincipalController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Event Planner - Menú Principal");
            
            System.out.println("[Login] Navegando a menú principal");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir menú principal: " + e.getMessage());
        }
    }
    
    /**
     * Abre la pantalla de registro
     */
    @FXML
    private void abrirRegistro() {
        if (stage == null) {
            System.err.println("[Login] Error: Stage es null");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Registro.fxml"));
            Parent root = loader.load();
            
            RegistroController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Event Planner - Registro");
            
            System.out.println("[Login] Abriendo registro");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir registro: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error en label
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        System.out.println("[Login] Error: " + mensaje);
    }
    
    /**
     * Muestra un mensaje de error en diálogo
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de advertencia
     */
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}