package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import logica.GestorEventos;
import presentacion.NavigationHelper;

/**
 * Controlador para el menú de gestión de participantes
 * Permite acceder a las diferentes funcionalidades relacionadas con participantes
 * 
 * @author  Ayner Jose Castro Benavides
 * @version 1.0
 */
public class MenuParticipantesController {
    
    private Stage stage;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        GestorEventos.getInstance();
    }
    
    /**
     * Establece el Stage principal
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("Stage recibido en MenuParticipantesController: " + (stage!=null));
        System.out.println("   Stage guardado: " + (this.stage != null));
    }
        
    /**
     * Abre el formulario para registrar un nuevo participante
     */
    @FXML
    private void abrirFormularioParticipante() {
           System.out.println("Abriendo formulario de participantes...");
    System.out.println("   Stage disponible: " + (this.stage != null));
    
    if (this.stage == null) {
        System.err.println("ERROR: stage es null en MenuParticipantesController");
        mostrarError("Error", "No se puede abrir el formulario");
        return;
    }
    
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormularioParticipante.fxml"));
        Parent root = loader.load();
        
        System.out.println("FormularioParticipante.fxml cargado");
        
        // CRÍTICO: Obtener el controlador y pasarle el stage
        FormularioParticipanteController controller = loader.getController();
        
        if (controller != null) {
            controller.setStage(this.stage);  // ESTA LÍNEA ES CRÍTICA
            System.out.println("Stage asignado a FormularioParticipanteController");
        } else {
            System.err.println("Error: FormularioParticipanteController es null");
            mostrarError("Error", "No se pudo inicializar el formulario");
            return;
        }
        
        Scene scene = new Scene(root);
        
        // Aplicar CSS
        NavigationHelper.aplicarCSS(scene);
        this.stage.setScene(scene);
        this.stage.setMaximized(false);
        this.stage.setMaximized(true);                 
        this.stage.setTitle("Registrar Participante - Event Planner");
        
        System.out.println("Formulario abierto correctamente");
        
    } catch (Exception e) {
        System.err.println("Error al abrir formulario:");
        e.printStackTrace();
        mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
    }
    }
    
    /**
     * Muestra la lista de todos los participantes registrados
     */
    @FXML
    private void listarParticipantes() {
        System.out.println("INTENTANDO abrir lista de participantes...");
        System.out.println("   Stage actual: " + (this.stage != null));
        
        if (this.stage == null) {
            System.err.println("ERROR: stage es null en listarParticipantes");
            mostrarError("Error", "No se puede cambiar de ventana. Stage no inicializado.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaParticipantes.fxml"));
            Parent root = loader.load();
            
            ListaParticipantesController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
                System.out.println("Stage asignado a ListaParticipantesController");
            }

            Scene scene = new Scene(root);
             // APLICAR CSS MORADO
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
        
            this.stage.setTitle("Lista de Participantes - Event Planner");
            
            System.out.println("Lista de participantes mostrada");
            
        } catch (Exception e) {
            System.err.println("Error al abrir lista:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir la lista: " + e.getMessage());
        }
    }
    // /**
    //   Abre el formulario para inscribir un participante a un evento
    //  /
        @FXML
        private void abrirInscripcionEvento() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InscripcionEvento.fxml"));
                Parent root = loader.load();
                
                InscripcionEventoController controller = loader.getController();
                controller.setStage(stage);
                
                Scene scene = new Scene(root);
                // APLICAR CSS MORADO
                NavigationHelper.aplicarCSS(scene);
                this.stage.setScene(scene);
                this.stage.setMaximized(false);
                this.stage.setMaximized(true);
                this.stage.setTitle("Inscribir a Evento - Event Planner");
                
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error", "No se pudo abrir inscripción: " + e.getMessage());
            }
        }
    
        /**
         * Permite cancelar la inscripción de un participante a un evento
         */
        @FXML
        private void cancelarInscripcion() {
            System.out.println("Abriendo Cancelar Inscripción...");
            
            if (this.stage == null) {
                mostrarError("Error", "No se puede abrir la ventana");
                return;
            }
            
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CancelarInscripcion.fxml"));
                Parent root = loader.load();
                
                // CRÍTICO: Obtener el controlador y pasarle el stage
                CancelarInscripcionController controller = loader.getController();
                if (controller != null) {
                    controller.setStage(this.stage);  // ← ESTA LÍNEA ES ESENCIAL
                    System.out.println("Stage asignado a CancelarInscripcionController");
                }
                
                Scene scene = new Scene(root);
                NavigationHelper.aplicarCSS(scene);
                this.stage.setScene(scene);  
                this.stage.setMaximized(false);
                this.stage.setMaximized(true);
                this.stage.setTitle("Cancelar Inscripción - Event Planner");
                
                System.out.println("Ventana Cancelar Inscripción abierta");
                
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al cancelar inscripción", 
                    "No se pudo abrir el formulario: " + e.getMessage());
            }
        }
    
        // /**
        //   Muestra los eventos de un participante específico
        //  /
        @FXML
        private void verMisEventos() {
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir la vista");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MisEventos.fxml"));
            Parent root = loader.load();
            
            MisEventosController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);  
            this.stage.setTitle("Mis Eventos - Event Planner");
            
            System.out.println("Vista Mis Eventos abierta");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir la vista: " + e.getMessage());
        }
        }
    
    // /**
    //   Permite buscar un participante específico
    //  /
        @FXML
        private void buscarParticipante() {
            if (this.stage == null) {
                mostrarError("Error", "No se puede abrir la ventana");
                return;
            }
            
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/BuscarParticipante.fxml"));
                Parent root = loader.load();
                
                // ESTA PARTE ES CRÍTICA:
                BuscarParticipanteController controller = loader.getController();
                if (controller != null) {
                    controller.setStage(this.stage);  // ← AGREGAR ESTA LÍNEA
                }
                
                Scene scene = new Scene(root);
                NavigationHelper.aplicarCSS(scene);
                this.stage.setScene(scene);
                this.stage.setMaximized(false);
                this.stage.setMaximized(true);      
                
                this.stage.setTitle("Buscar Participante - Event Planner");
                
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error", "No se pudo abrir: " + e.getMessage());
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
            
            // Aplicar CSS
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Event Planner - Menú Principal");
            
            System.out.println("Vuelto al Menú Principal");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú principal");
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
}