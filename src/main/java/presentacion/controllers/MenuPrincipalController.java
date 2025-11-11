package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;


/**
 * Controlador del Menú Principal
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class MenuPrincipalController {
    
    private Stage stage;
    
    /**
     * Establece el Stage principal
     * @param stage Stage principal de la aplicación
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println(" Stage recibido en MenuPrincipalController");
    }
    
    /**
     * Abre la gestión de eventos
     */
    @FXML
private void abrirGestionEventos() {
    System.out.println(" Abriendo Gestión de Eventos...");
    
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormularioEvento.fxml"));
        Parent root = loader.load();
        
        // Crear nueva ventana
        Stage nuevaVentana = new Stage();
        nuevaVentana.setTitle("Crear Nuevo Evento - Event Planner");
        
        Scene scene = new Scene(root, 900, 700);
        
        //  APLICAR CSS MORADO
        try {
            java.net.URL cssUrl = getClass().getResource("/fxml/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println(" CSS morado aplicado al formulario");
            } else {
                System.out.println(" No se encontro /fxml/styles.css");
            }
        } catch (Exception e) {
            System.out.println(" Error al cargar CSS: " + e.getMessage());
        }
        
        nuevaVentana.setScene(scene);
        nuevaVentana.show();
        
        System.out.println(" Ventana de Gestión de Eventos abierta");
        
    } catch (Exception e) {
        System.err.println(" Error al abrir Gestión de Eventos:");
        e.printStackTrace();
        mostrarError("Error", "No se pudo abrir el formulario de eventos: " + e.getMessage());
    }
}

    /**
     * Abre el menú de gestión de participantes
     */
    @FXML
    private void abrirGestionParticipantes() {
        System.out.println(" Abriendo Gestión de Participantes...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuParticipantes.fxml"));
            Parent root = loader.load();
            
            // Crear nueva ventana
            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle("Gestión de Participantes - Event Planner");
            
            Scene scene = new Scene(root, 900, 700);
            
            // APLICAR CSS MORADO
            try {
                java.net.URL cssUrl = getClass().getResource("/fxml/styles.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println(" CSS morado aplicado al menú de participantes");
                } else {
                    System.out.println(" No se encontro /fxml/styles.css");
                }
            } catch (Exception e) {
                System.out.println(" Error al cargar CSS: " + e.getMessage());
            }
            
            nuevaVentana.setScene(scene);
            nuevaVentana.show();
            
            System.out.println(" Ventana de Gestión de Participantes abierta");
            
        } catch (Exception e) {
            System.err.println(" Error al abrir Gestión de Participantes:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el menú de participantes: " + e.getMessage());
        }
    }
    
    /**
     * Abre el sistema de pagos (próximamente)
     */
    @FXML
    private void abrirSistemaPagos() {
        mostrarInfo("Sistema de Pagos", "Esta funcionalidad se implementará próximamente");
    }
    
    /**
     * Abre los reportes (próximamente)
     */
    @FXML
    private void abrirReportes() {
        mostrarInfo("Reportes y Estadísticas", "Esta funcionalidad se implementará próximamente");
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
                System.out.println(" Cerrando aplicación...");
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
    
    /**
     * Muestra un mensaje de advertencia
     * @param titulo Título del diálogo
     * @param mensaje Mensaje de advertencia
     */
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 