package presentacion;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import presentacion.controllers.LoginController;
import java.net.URL;

/**
 * Clase principal de la interfaz gráfica JavaFX
 * Inicia con pantalla de Login
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("\n==============================================");
            System.out.println(" EVENT PLANNER - INICIANDO...");
            System.out.println("==============================================");
            
            // Buscar el FXML de Login
            URL fxmlLocation = getClass().getResource("/fxml/Login.fxml");
            
            // Verificar que existe
            if (fxmlLocation == null) {
                System.err.println(" ERROR: No se encuentra Login.fxml");
                System.err.println("Verifica que el archivo esté en: src/main/resources/fxml/");
                return;
            }
            
            System.out.println(" FXML encontrado: " + fxmlLocation);
            
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            
            System.out.println(" FXML cargado exitosamente");
            
            // Obtener el controlador y pasarle el stage
            LoginController controller = loader.getController();
            
            if (controller != null) {
                controller.setStage(primaryStage);
                System.out.println(" Stage asignado al LoginController");
            } else {
                System.err.println(" ERROR: El controlador es null");
                return;
            }
            
            //  ORDEN CORRECTO: 1. Crear escena, 2. Aplicar CSS, 3. Configurar stage, 4. Maximizar
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            primaryStage.setMaximized(true);
            
            // Atajo F11
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F11) {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            });
            
            // Configurar ventana
            primaryStage.setTitle("Event Planner - Login");
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(76666668);
            
            // ✅ ORDEN CORRECTO
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setMaximized(true); // ✅ SOLO AQUÍ
            primaryStage.setTitle("Event Planner - Login");
            
            // Agregar icono
            try {
                InputStream iconStream = getClass().getResourceAsStream("/fxml/images/icono.png");
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                    System.out.println(" Icono de ventana cargado");
                } else {
                    System.out.println("  Icono no encontrado");
                }
            } catch (Exception e) {
                System.out.println("  No se pudo cargar el icono");
            }
            
            // Configuración de ventana
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(768);
            primaryStage.setFullScreenExitHint("Presiona ESC o F11 para salir de pantalla completa");
            
            // ✅ MAXIMIZAR AL FINAL
            primaryStage.setMaximized(true);
            
            primaryStage.show();
            
            System.out.println("==============================================");
            System.out.println(" APLICACIÓN INICIADA CORRECTAMENTE");
            System.out.println(" PANTALLA DE LOGIN CARGADA");
            System.out.println("==============================================");
            System.out.println("");
            System.out.println(" CREDENCIALES POR DEFECTO:");
            System.out.println("   Usuario: admin");
            System.out.println("   Contraseña: admin123");
            System.out.println("");
            System.out.println("TIP: Presiona F11 para pantalla completa");
            System.out.println("==============================================\n");
            
        } catch (IOException e) {  
            System.err.println("==============================================");
            System.err.println(" ERROR FATAL AL INICIAR:");
            System.err.println("==============================================");
            e.printStackTrace();
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