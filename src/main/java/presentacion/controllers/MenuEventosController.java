
package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import presentacion.NavigationHelper;

/**
 * Controlador del menú principal de Gestión de Eventos
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class MenuEventosController {
    
    private Stage stage;
    
    /**
     * Establece el Stage principal
     * 
     * @param stage Ventana principal de la aplicación
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("[MenuEventos] Stage asignado");
    }
    
    /**
     * Abre el formulario para crear un nuevo evento
     */
    @FXML
    private void crearEvento() {
        System.out.println("[MenuEventos] Abriendo formulario de creación...");
        abrirVista("/fxml/FormularioEvento.fxml", "Crear Evento - Event Planner");
    }
    
    /**
     * Abre la lista de todos los eventos
     */
    @FXML
    private void listarEventos() {
        System.out.println("[MenuEventos] Abriendo lista de eventos...");
        abrirVista("/fxml/ListaEventos.fxml", "Lista de Eventos - Event Planner");
    }
    
    /**
     * Abre la vista para publicar eventos (cambiar estado)
     */
    @FXML
    private void publicarEventos() {
        System.out.println("[MenuEventos] Abriendo publicador de eventos...");
        abrirVista("/fxml/PublicarEventos.fxml", "Publicar Eventos - Event Planner");
    }
    
    /**
     * Abre la búsqueda de eventos
     */
    @FXML
    private void buscarEventos() {
        System.out.println("[MenuEventos] Abriendo búsqueda de eventos...");
        abrirVista("/fxml/BuscarEvento.fxml", "Buscar Eventos - Event Planner");
    }
    
    /**
     * Vuelve al menú principal
     */
    @FXML
    private void volver() {
        System.out.println("[MenuEventos] Volviendo al menú principal...");
        abrirVista("/fxml/MenuPrincipal.fxml", "Event Planner - Sistema de Gestión");
    }
    
    /**
     * Método auxiliar para abrir vistas
     * 
     * @param rutaFXML Ruta del archivo FXML
     * @param titulo Título de la ventana
     */
    private void abrirVista(String rutaFXML, String titulo) {
        if (stage == null) {
            System.err.println("[MenuEventos] Error: Stage no inicializado");
            mostrarError("Error", "No se puede abrir la vista");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            
            // Pasar el stage al controlador hijo
            Object controller = loader.getController();
            if (controller instanceof FormularioEventoController) {
                ((FormularioEventoController) controller).setStage(stage);
            } else if (controller instanceof ListaEventosController) {
                ((ListaEventosController) controller).setStage(stage);
            } else if (controller instanceof PublicarEventosController) {
                ((PublicarEventosController) controller).setStage(stage);
            } else if (controller instanceof BuscarEventoController) {
                ((BuscarEventoController) controller).setStage(stage);
            } else if (controller instanceof MenuPrincipalController) {
                ((MenuPrincipalController) controller).setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle(titulo);
            
            System.out.println("[MenuEventos] ✅ Vista cargada: " + titulo);
            
        } catch (Exception e) {
            System.err.println("[MenuEventos] ❌ Error al cargar vista:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista: " + e.getMessage());
        }
    }
    
    /**
     * Muestra mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}