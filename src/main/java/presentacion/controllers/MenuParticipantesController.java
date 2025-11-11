package presentacion.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import logica.GestorEventos;

/**
 * Controlador para el menú de gestión de participantes
 * Permite acceder a las diferentes funcionalidades relacionadas con participantes
 * 
 * @author Tu Nombre
 * @version 1.0
 */
public class MenuParticipantesController {
    
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
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Abre el formulario para registrar un nuevo participante
     */
    @FXML
    private void abrirFormularioParticipante() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormularioParticipante.fxml"));
                Parent root = loader.load();
                
                // Crear nueva ventana
                Stage nuevaVentana = new Stage();
                nuevaVentana.setTitle("Registrar Nuevo Participante - Event Planner");
                
                Scene scene = new Scene(root, 900, 700);
                
                // APLICAR CSS MORADO
                try {
                    java.net.URL cssUrl = getClass().getResource("/fxml/styles.css");
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                        System.out.println(" CSS morado aplicado al formulario de participante");
                    } else {
                        System.out.println(" No se encontro /fxml/styles.css");
                    }
                } catch (Exception e) {
                    System.out.println(" Error al cargar CSS: " + e.getMessage());
                }
                
                nuevaVentana.setScene(scene);
                nuevaVentana.initModality(Modality.APPLICATION_MODAL);
                nuevaVentana.showAndWait();
            
        } catch (Exception e) {
            mostrarError("Error al abrir formulario", "No se pudo abrir el formulario de participantes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la lista de todos los participantes registrados
     */
    @FXML
    private void listarParticipantes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaParticipantes.fxml"));
            Parent root = loader.load();
            
            ListaParticipantesController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Lista de Participantes - Event Planner");
            
        } catch (Exception e) {
            mostrarError("Error", "No se pudo abrir la lista: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // /**
    //   Abre el formulario para inscribir un participante a un evento
    //  /
        @FXML
        private void abrirInscripcionEvento() {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/InscripcionEvento.fxml")
                );
                Parent root = loader.load();
                
                // SIN obtener el controlador por ahora
                Stage ventana = new Stage();
                ventana.initModality(Modality.APPLICATION_MODAL);
                ventana.setTitle("Inscribir a Evento - Event Planner");
                ventana.setScene(new Scene(root));
                ventana.showAndWait();
                
            } catch (Exception e) {
                mostrarError("Error al abrir inscripción", 
                            "No se pudo abrir el formulario de inscripción: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
    // /**
    //   Permite cancelar la inscripción de un participante a un evento
    //  /
     @FXML
     private void cancelarInscripcion() {
         try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CancelarInscripcion.fxml"));
            Parent root = loader.load();
            //Sin obtener el controlador por ahora

            Stage ventana = new Stage();
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setTitle("Cancelar Inscripcion a Evento - Event Planner");
            ventana.setScene(new Scene(root));
            ventana.showAndWait();
         
        } catch (Exception e) {
             mostrarError("Error al cancelar inscripción", "No se pudo abrir el formulario: " + e.getMessage());
             e.printStackTrace();
         }
     }
    
    // /**
    //   Muestra los eventos de un participante específico
    //  /
     @FXML
    private void verMisEventos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MisEventos.fxml"));
            Parent root = loader.load();
            
            Stage ventana = new Stage();
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setTitle("Mis Eventos - Event Planner");
            ventana.setScene(new Scene(root));
            ventana.showAndWait();

            
        } catch (Exception e) {
            mostrarError("Error al ver eventos", "No se pudo cargar los eventos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // /**
    //  * Permite buscar un participante específico
    //  */
     @FXML
    private void buscarParticipante() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BuscarParticipante.fxml"));
            Parent root = loader.load();
            
            Stage ventana = new Stage();
            ventana.initModality(Modality.APPLICATION_MODAL);           
            ventana.setTitle("Buscar Participante - Event Planner");
            ventana.setScene(new Scene(root));
            ventana.showAndWait();  

            
        } catch (Exception e) {
            mostrarError("Error al buscar", "No se pudo abrir el buscador: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Vuelve al menú principal
     */
    @FXML
    private void volverMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            
            MenuPrincipalController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Event Planner - Menú Principal");
            
        } catch (IOException e) {
            mostrarError("Error al volver", "No se pudo volver al menú principal: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un mensaje de error
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
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}