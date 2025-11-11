/**
 * SistemaEventos - Clase principal con el método main para iniciar la aplicación
 * Esta es la clase de entrada del sistema Event Planner.
 * Inicializa el menú principal y gestiona el ciclo de vida de la aplicación.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion;

import java.io.IOException;
 
public class SistemaEventos {
      
    /**
     * Método principal que inicia la aplicación
     * @param args
     */
    public static void main(String[] args) {
        
        // Mostrar pantalla de carga
        mostrarPantallaCarga();
        
        try {
            // Inicializar y mostrar el menú principal
            MenuPrincipalConsola menuPrincipal = new MenuPrincipalConsola();
            menuPrincipal.mostrar();
            
        } catch (Exception e) {
            // Manejo de errores críticos
            System.err.println("\n====================================================================");
            System.err.println("\n                    ERROR CRÍTICO DEL SISTEMA                        ");
            System.err.println("\n====================================================================");
            System.err.println();
            System.err.println(" Ha ocurrido un error crítico que impide continuar:");
            System.err.println("   " + e.getMessage());
            System.err.println();
            System.err.println(" Detalles técnicos:");
            System.err.println();
            System.err.println(" Sugerencias:");
            System.err.println("    Verifique que todos los archivos del sistema estén presentes");
            System.err.println("    Asegúrese de tener permisos de lectura/escritura");
            System.err.println("    Contacte al administrador del sistema");
            System.err.println();
        }
    }
    
        /**
         * Muestra la pantalla de carga inicial del sistema
         */
        private static void mostrarPantallaCarga() {
            limpiarPantalla();
            System.out.println();
            System.out.println();
            System.out.println("====================================================================");
            System.out.println("                                                                    ");
            System.out.println("                        EVENT PLANNER                               ");
            System.out.println("                Sistema de Gestión de Eventos                       ");
            System.out.println("                                                                    ");
            System.out.println("                           Versión 1.0                              ");
            System.out.println("                                                                    ");
            System.out.println("====================================================================");
            System.out.println();
            System.out.println("              Universidad Popular del Cesar");
            System.out.println("              Programación de Computadores II");
            System.out.println();
            System.out.println("====================================================================");
            System.out.println();
            // Simulación de carga
            System.out.print("   Inicializando sistema");
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(200);
                    System.out.print(".");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(" OK");
            
            System.out.print("   Cargando modulos");
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(200);
                    System.out.print(".");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(" OK");
            
            System.out.print("   Verificando base de datos");
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(200);
                    System.out.print(".");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(" OK");            
            System.out.println();
            System.out.println("   Sistema listo!");
            System.out.println();
            
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
            /**
             * Limpia la pantalla de la consola
             */
            private static void limpiarPantalla() {
                // Intentar limpiar la pantalla (funciona en algunas consolas)
                try {
                    final String os = System.getProperty("os.name");
                    
                    if (os.contains("Windows")) {
                        // Windows
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    } else {
                        // Unix/Linux/Mac
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                    }
                } catch (IOException | InterruptedException e) {
                    // Si falla, usar método alternativo
                    for (int i = 0; i < 50; i++) {
                        System.out.println();
                    }
                }
            }
                /**
                 * Información del sistema (metadata)
                 */
                private static final String VERSION = "1.0.0";
                private static final String FECHA_VERSION = "2025";
                private static final String AUTOR = "Ayner Jose Castro Benavides y Sebastian Ramos";
                private static final String INSTITUCION = "Universidad Popular del Cesar";
                private static final String ASIGNATURA = "Programación de Computadores II";
                    /**
                     * Obtiene la versión del sistema
                     * @return Versión del sistema
                     */
                    public static String getVersion() {
                        return VERSION;
                    }
                    
                        /**
                         * Obtiene información del sistema
                         * @return String con información del sistema
                         */
                        public static String getInformacionSistema() {
                            StringBuilder info = new StringBuilder();
                            info.append("====================================================================\n");
                            info.append("                   INFORMACIÓN DEL SISTEMA                           \n");
                            info.append("====================================================================\n");
                            info.append("\n");
                            info.append("Nombre: Event Planner\n");
                            info.append("Versión: ").append(VERSION).append("\n");
                            info.append("Año: ").append(FECHA_VERSION).append("\n");
                            info.append("Autor: ").append(AUTOR).append("\n");
                            info.append("Institución: ").append(INSTITUCION).append("\n");
                            info.append("Asignatura: ").append(ASIGNATURA).append("\n");
                            info.append("\n");
                            info.append("Características:\n");
                            info.append("  • Gestión completa de eventos\n");
                            info.append("  • Control de participantes y organizadores\n");
                            info.append("  • Sistema de inscripciones y check-in\n");
                            info.append("  • Reportes y análisis avanzados\n");
                            info.append("  • Persistencia de datos\n");
                            info.append("\n");
                            info.append("Arquitectura:\n");
                            info.append("  • Programación Orientada a Objetos\n");
                            info.append("  • Arquitectura en 3 capas (Presentación-Lógica-Datos)\n");
                            info.append("  • Manejo de excepciones personalizado\n");
                            info.append("  • Validación de datos robusta\n");
                            return info.toString();
                        }
}