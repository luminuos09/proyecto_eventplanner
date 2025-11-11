package presentacion;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import presentacion.controllers.MenuPrincipalController;
import java.net.URL;

/**
 * Clase principal de la interfaz gráfica JavaFX
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("==============================================");
            System.out.println("      EVENT PLANNER - INICIANDO...");
            System.out.println("==============================================");
            
            // Buscar el FXML
            URL fxmlLocation = getClass().getResource("/fxml/MenuPrincipal.fxml");
            
            // Verificar que existe
            if (fxmlLocation == null) {
                System.err.println(" ERROR: No se encuentra MenuPrincipal.fxml");
                System.err.println("Verifica que el archivo esté en: src/main/resources/fxml/");
                return;
            }
            
            System.out.println(" FXML encontrado: " + fxmlLocation);
            
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            
            System.out.println(" FXML cargado exitosamente");
            
            // Obtener el controlador y pasarle el stage
            MenuPrincipalController controller = loader.getController();
            
            if (controller != null) {
                controller.setStage(primaryStage);
                System.out.println(" Stage asignado al MenuPrincipalController");
            } else {
                System.err.println(" ERROR: El controlador es null");
                System.err.println("Verifica que MenuPrincipal.fxml tenga:");
                System.err.println("fx:controller=\"presentacion.controllers.MenuPrincipalController\"");
                return;
            }
            
            // Crear la escena
            Scene scene = new Scene(root, 900, 700);
            
            // Buscar CSS
            URL cssLocation = getClass().getResource("/fxml/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
                System.out.println(" CSS cargado: " + cssLocation);
            } else {
                System.out.println(" CSS no encontrado en /styles.css");
                // Intentar buscar en /fxml/styles.css
                cssLocation = getClass().getResource("/fxml/styles.css");
                if (cssLocation != null) {
                    scene.getStylesheets().add(cssLocation.toExternalForm());
                    System.out.println(" CSS cargado desde: " + cssLocation);
                } else {
                    System.out.println(" CSS tampoco encontrado en /fxml/styles.css");
                    System.out.println(" La aplicación funcionará sin estilos CSS");
                }
            }
            
            // Configurar ventana
            primaryStage.setTitle("Event Planner - Sistema de Gestión");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.show();
            
            System.out.println("==============================================");
            System.out.println(" APLICACIÓN INICIADA CORRECTAMENTE");
            System.out.println("==============================================\n");
            
        } catch (IOException e) {  
            System.err.println("==============================================");
            System.err.println(" ERROR FATAL AL INICIAR:");
            System.err.println("==============================================");
            System.err.println("==============================================");
        }
    }
    
    public static void main(String[] args) {
        System.out.println("\n EVENT PLANNER");
        System.out.println(" Universidad Popular del Cesar");
        System.out.println(" Programación de Computadores II - 2025\n");
        launch(args);
    }
}