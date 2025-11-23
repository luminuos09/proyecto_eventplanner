package presentacion;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.WeakHashMap;

/**
 * Clase auxiliar optimizada para manejar navegación y estilos.
 * Implementa caché para mejorar rendimiento.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 2.0 - Optimizado
 */
public class NavigationHelper {
    
    private static final String CSS_PATH = "/fxml/styles.css";
    
    //  CACHÉ: CSS se carga UNA SOLA VEZ
    private static URL cssURL = null;
    private static String cssExternalForm = null;
    
    // CACHÉ: Evitar agregar múltiples listeners F11
    private static final WeakHashMap<Scene, Boolean> scenesConF11 = new WeakHashMap<>();
    
    /**
     * Inicializa el caché de CSS (se ejecuta una sola vez)
     */
    private static void inicializarCSS() {
        if (cssURL == null) {
            cssURL = NavigationHelper.class.getResource(CSS_PATH);
            if (cssURL != null) {
                cssExternalForm = cssURL.toExternalForm();
                System.out.println("[NavHelper] ✅ CSS cargado y cacheado");
            } else {
                System.out.println("[NavHelper] ⚠️  CSS no encontrado en " + CSS_PATH);
            }
        }
    }
    
    /**
     * Aplica el CSS a una escena (OPTIMIZADO con caché)
     * 
     * @param scene Escena a la que aplicar CSS
     */

    
    /**
     * Aplica CSS, maximiza y ajusta al tamaño de pantalla
     * 
     * @param scene Escena a la que aplicar CSS
     * @param stage Stage a maximizar
     */
    public static void aplicarCSS(Scene scene) {
        System.out.println("[NavigationHelper]  Usando estilos inline del FXML");
    }
    
    /**
     * Agrega el atajo F11 para alternar pantalla completa (OPTIMIZADO)
     * Evita agregar múltiples listeners a la misma escena
     * 
     * @param scene Escena
     * @param stage Stage
     */
    public static void agregarAtaljoF11(Scene scene, Stage stage) {
        if (scene == null || stage == null) return;
        
        // Verifica si ya se agregó el listener
        if (!scenesConF11.containsKey(scene)) {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                    event.consume();
                }
            });
            scenesConF11.put(scene, Boolean.TRUE);
        }
    }
    
    /**
     * Configura una escena completa (CSS, maximizado, F11)
     * 
     * @param stage Stage donde aplicar la configuración
     */
    public static void configurarEscena(Stage stage) {
        if (stage == null) return;
        
        Scene scene = stage.getScene();
        if (scene != null) {
            aplicarCSS(scene);
            agregarAtaljoF11(scene, stage);
        }
        stage.setMaximized(true);
    }
    
    /**
     * Limpia el caché (útil para liberar memoria si es necesario)
     */
    public static void limpiarCache() {
        scenesConF11.clear();
        System.out.println("[NavHelper] 🧹 Caché limpiado");
    }
    
    /**
     * Obtiene información de diagnóstico
     */
    public static void mostrarEstadisticas() {
        System.out.println("[NavHelper] 📊 Estadísticas:");
        System.out.println("  - CSS cacheado: " + (cssURL != null ? "Sí" : "No"));
        System.out.println("  - Escenas con F11: " + scenesConF11.size());
    }
}