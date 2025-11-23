package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import logica.GestorAutenticacion;
import modelos.RolUsuario;
import presentacion.NavigationHelper;

/**
 * Controlador del Menú Principal
 * Maneja permisos según el rol del usuario
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class MenuPrincipalController {
    
    @FXML private Button btnGestionOrganizadores;
    @FXML private Button btnReportes;
    
    private Stage stage;
    private GestorAutenticacion gestorAuth;
    
    /**
     * Inicializa el controlador y configura permisos
     */
    @FXML
    public void initialize() {
        gestorAuth = GestorAutenticacion.getInstance();
        
        // CONFIGURAR PERMISOS SEGÚN ROL
        configurarPermisos();
        
        System.out.println("[MenuPrincipal] Controlador inicializado");
    }
    
    /**
     * Configura qué botones puede ver cada rol
     */
    private void configurarPermisos() {
        RolUsuario rolActual = gestorAuth.getRolUsuarioActual();
        
        if (rolActual == RolUsuario.ORGANIZADOR) {

            if (btnGestionOrganizadores != null) {
                btnGestionOrganizadores.setVisible(false);
                btnGestionOrganizadores.setManaged(false);
            }
            
            if (btnReportes != null) {
                btnReportes.setVisible(false);
                btnReportes.setManaged(false);
            }
            
            System.out.println("[MenuPrincipal] Vista de ORGANIZADOR cargada");
            System.out.println("[MenuPrincipal] Módulos ocultos: Gestión de Organizadores, Reportes");
            
        } else if (rolActual == RolUsuario.ADMINISTRADOR) {
            System.out.println("[MenuPrincipal] Vista de ADMINISTRADOR cargada");
            System.out.println("[MenuPrincipal] Acceso total al sistema");
        }
    }
    
    /**
     * Establece el Stage principal
     * @param stage Stage principal de la aplicación
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("[MenuPrincipal] Stage recibido");
    }
    
    /**
     * Abre la gestión de eventos
     */
    @FXML
    private void abrirGestionEventos() {
        System.out.println("[MenuPrincipal] Abriendo Gestión de Eventos...");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuEventos.fxml"));
            Parent root = loader.load();
            
            MenuEventosController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Gestión de Eventos - Event Planner");
            
            System.out.println("[MenuPrincipal] Gestión de Eventos abierta");
             
        } catch (Exception e) {
            System.err.println("[MenuPrincipal] Error al abrir Gestión de Eventos:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el formulario de eventos: " + e.getMessage());
        }
    }

    /**
     * Abre el menú de gestión de participantes
     */
    @FXML
    private void abrirGestionParticipantes() {
        System.out.println("[MenuPrincipal] Abriendo Gestión de Participantes...");
    
        if (stage == null) {
            System.err.println("[MenuPrincipal] ERROR: stage es null");
            mostrarError("Error", "Stage no inicializado");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuParticipantes.fxml"));
            Parent root = loader.load();
            
            MenuParticipantesController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Gestión de Participantes - Event Planner");
            
            System.out.println("[MenuPrincipal] Gestión de Participantes abierta");

        } catch (Exception e) {
            System.err.println("[MenuPrincipal] Error al abrir Gestión de Participantes:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el menú de participantes: " + e.getMessage());
        }
    }

    /**
     * Abre el menú de gestión de organizadores (SOLO ADMIN)
     */
    @FXML
    private void abrirGestionOrganizadores() {
        System.out.println("[MenuPrincipal] Abriendo Gestión de Organizadores...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir el menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuOrganizadores.fxml"));
            Parent root = loader.load();
            
            MenuOrganizadoresController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);     
            NavigationHelper.aplicarCSS(scene);       
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Gestión de Organizadores - Event Planner");
            
            System.out.println("[MenuPrincipal] Gestión de Organizadores abierta");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el menú: " + e.getMessage());
        }
    }
            
    /**
     * Abre el menú del sistema de pagos
     */
    @FXML
    private void abrirSistemaPagos() {
        System.out.println("[MenuPrincipal] Abriendo Sistema de Pagos...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir el menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuPagos.fxml"));
            Parent root = loader.load();
            
            MenuPagosController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Sistema de Pagos - Event Planner");
            
            System.out.println("[MenuPrincipal] Sistema de Pagos abierto");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el sistema de pagos: " + e.getMessage());
        }
    }
    
    /**
     * Abre los reportes (SOLO ADMIN)
     */
    @FXML
    private void abrirReportes() {
        System.out.println("[MenuPrincipal] Abriendo Reportes y Estadísticas...");
    
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir el menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuReportes.fxml"));
            Parent root = loader.load();
            
            MenuReportesController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            this.stage.setTitle("Reportes y Estadísticas - Event Planner");
            
            System.out.println("[MenuPrincipal] Reportes abiertos");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir reportes: " + e.getMessage());
        }
    }
    
    /**
     * Sale de la aplicación con confirmación
     */
    @FXML
    private void salir() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Salir");
        alert.setHeaderText("¿Está seguro que desea salir?");
        alert.setContentText("Se cerrará la aplicación Event Planner");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("[MenuPrincipal] Cerrando aplicación...");
                System.exit(0);
            }
        });
    }
    
    // ============== MÉTODOS AUXILIARES ==============
    
    /**
     * Muestra un mensaje de error
     * @param titulo Título del diálogo
     * @param mensaje Mensaje del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje informativo
     * @param titulo Título del diálogo
     * @param mensaje Mensaje informativo
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}