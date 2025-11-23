package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import logica.GestorEventos;
import logica.GestorPagos;
import presentacion.NavigationHelper;

/**
 * Controlador para el menú principal del sistema de pagos.
 * Permite acceder a las funcionalidades de compra de tickets,
 * consulta de tickets y historial de pagos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 * @since 2025-01-13
 */
public class MenuPagosController {
    
    /**
     * Gestor de pagos (Singleton)
     */
    private GestorPagos gestorPagos;
    
    /**
     * Gestor de eventos (Singleton)
     */
    private GestorEventos gestorEventos;
    
    /**
     * Stage principal de la aplicación
     */
    private Stage stage;
    
    /**
     * Inicializa el controlador.
     * Se ejecuta automáticamente después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        this.gestorEventos = GestorEventos.getInstance();
        this.gestorPagos = GestorPagos.getInstance();
        
        System.out.println("[MenuPagos] Controlador inicializado");
    }
    
    /**
     * Establece el Stage principal de la aplicación.
     * 
     * @param stage El Stage compartido de la aplicación
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("[MenuPagos] Stage asignado: " + (stage != null));
    }
    
    /**
     * Establece el gestor de pagos (para compartir instancia)
     * 
     * @param gestorPagos Instancia del gestor de pagos
     */
    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
    }
    
    // ==================== NAVEGACIÓN A FUNCIONALIDADES ====================
    
    /**
     * Abre la ventana para comprar un ticket
     */
    @FXML
    private void comprarTicket() {
        System.out.println("[MenuPagos] Abriendo compra de ticket...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir la compra");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/ComprarTicket.fxml"));
            Parent root = loader.load();
            
            System.out.println("[MenuPagos] FXML cargado correctamente");
            
            // Obtener el controlador y pasarle el stage
            ComprarTicketController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
                controller.setGestorPagos(this.gestorPagos);
                System.out.println("[MenuPagos] Stage y gestor asignados");
            } else {
                System.err.println("[MenuPagos] Controlador es null");
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Comprar Ticket - Event Planner");
            
            System.out.println("[MenuPagos] Ventana de compra abierta");
            
        } catch (Exception e) {
            System.err.println("[MenuPagos] Error al abrir compra:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir la compra de tickets: " + e.getMessage());
        }
    }
    
    /**
     * Muestra los tickets del participante actual
     */
    @FXML
    private void verMisTickets() {
        System.out.println("[MenuPagos] Abriendo mis tickets...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir la lista");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MisTickets.fxml"));
            Parent root = loader.load();
            
            MisTicketsController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
                controller.setGestorPagos(this.gestorPagos);
                System.out.println("[MenuPagos] Stage y gestor asignados");
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Mis Tickets - Event Planner");
            
            System.out.println("[MenuPagos] Mis tickets mostrados");
            
        } catch (Exception e) {
            System.err.println("[MenuPagos] Error al abrir tickets:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir mis tickets: " + e.getMessage());
        }
    }
    
    /**
     * Muestra el historial completo de pagos
     */
    @FXML
    private void verHistorialPagos() {
        System.out.println("[MenuPagos] Abriendo historial de pagos...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir el historial");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/HistorialPagos.fxml"));
            Parent root = loader.load();
            
            HistorialPagosController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
                controller.setGestorPagos(this.gestorPagos);
                System.out.println("[MenuPagos] Stage y gestor asignados");
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Historial de Pagos - Event Planner");
            
            System.out.println("[MenuPagos] Historial mostrado");
            
        } catch (Exception e) {
            System.err.println("[MenuPagos] Error al abrir historial:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el historial: " + e.getMessage());
        }
    }
    
    /**
     * Vuelve al menú principal de la aplicación
     */
    @FXML
    private void volverMenuPrincipal() {
        System.out.println("[MenuPagos] Volviendo al menú principal...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede volver al menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            
            MenuPrincipalController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            //Aplicar CSS
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Event Planner - Sistema de Gestión de Eventos");
            
            System.out.println("[MenuPagos] Vuelto al menú principal");
            
        } catch (Exception e) {
            System.err.println("[MenuPagos] Error al volver:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú principal");
        }
    }
    
    // ==================== MENSAJES AL USUARIO ====================
    
    /**
     * Muestra un mensaje de error al usuario.
     * 
     * @param titulo Título del diálogo de error
     * @param mensaje Mensaje descriptivo del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}