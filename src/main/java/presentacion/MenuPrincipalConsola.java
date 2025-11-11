/**
 * MenuPrincipal - Mostrar el menú principal y coordinar los submenús
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion;

import logica.GestorEventos;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MenuPrincipalConsola {
    
    private final Scanner scanner;
    private final GestorEventos gestor;
    
    // Submenús
    private final MenuEventos menuEventos;
    private final MenuParticipantes menuParticipantes;
    private final MenuReportes menuReportes;
    private final MenuPagos menuPagos;
    
    /**
     * Constructor
     */
    public MenuPrincipalConsola() {
        this.scanner = new Scanner(System.in);
        this.gestor = new GestorEventos();
        
        // Inicializar submenús
        this.menuEventos = new MenuEventos(gestor, scanner);
        this.menuParticipantes = new MenuParticipantes(gestor, scanner);
        this.menuReportes = new MenuReportes(gestor, scanner);
        this.menuPagos = new MenuPagos(gestor, scanner);
    }
    
    public GestorEventos getGestor() {
        return gestor;
    }
    
    /**
     * Muestra el menú principal y gestiona la navegación
     */
    public void mostrar() {
        try (scanner) {
            var salir = false;
            
            mostrarBienvenida();
            
            while (!salir) {
                try {
                    mostrarOpciones();
                    var opcion = leerOpcion();
                    System.out.println(); // Línea en blanco para separar
                    salir = procesarOpcion(opcion);
                    
                    if (!salir) {
                        pausar();
                    }
                    
                } catch (InputMismatchException e) {
                    System.out.println("\n Error: Debe ingresar un número válido.");
                    scanner.nextLine(); // Limpiar el buffer
                    pausar();
                } catch (Exception e) {
                    System.out.println("\n Error inesperado: " + e.getMessage());
                    pausar();
                }
            }
            mostrarDespedida();
        }
    }
    
    /**
     * Muestra el mensaje de bienvenida
     */
    private void mostrarBienvenida() {
        limpiarPantalla();
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("                      EVENT PLANNER v1.0");
        System.out.println("                Sistema de Gestion de Eventos");
        System.out.println();
        System.out.println("         Universidad Popular del Cesar - Programacion II");
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("      Bienvenido al Sistema de Gestion de Eventos");
        System.out.println("   Organiza, gestiona y controla tus eventos facilmente");
        System.out.println();
        pausar();
    }
    
    /**
     * Muestra las opciones del menú principal
     */
    private void mostrarOpciones() {
        limpiarPantalla();
        System.out.println("====================================================================");
        System.out.println("                          MENÚ PRINCIPAL                             ");
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("    1.  Gestión de Eventos");
        System.out.println("       Crear, editar, cancelar y consultar eventos");
        System.out.println();
        System.out.println("    2.  Gestión de Participantes");
        System.out.println("       Registrar participantes e inscribirlos a eventos");
        System.out.println();
        System.out.println("    3.  Gestión de Organizadores");
        System.out.println("       Registrar y consultar organizadores");
        System.out.println();
        System.out.println("    4.  Pagos y Finanzas");
        System.out.println("       Comprar tickets, ver reportes financieros");
        System.out.println();
        System.out.println("    5.  Reportes y Dashboard");
        System.out.println("       Ver estadísticas, reportes y análisis");
        System.out.println();
        System.out.println("    6.  Ayuda");
        System.out.println("       Información sobre el sistema");
        System.out.println();
        System.out.println("    7.  Salir");
        System.out.println();
        System.out.println("====================================================================");
    }
    
    /**
     * Lee la opción del usuario
     * 
     * @return Opción seleccionada
     */
    private int leerOpcion() {
        System.out.print("Seleccione una opción (1-7): ");
        var opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer
        return opcion;
    }
    
    /**
     * Procesa la opción seleccionada
     * 
     * @param opcion Opción a procesar
     * @return true si se debe salir del programa, false en caso contrario
     */
    private boolean procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> {
                menuEventos.mostrar();
                return false;
            }
            
            case 2 -> {
                menuParticipantes.mostrar();
                return false;
            }
            
            case 3 -> {
                mostrarMenuOrganizadores();
                return false;
            }
            
            case 4 -> {
                menuPagos.mostrar();
                return false;
            }
            
            case 5 -> {
                menuReportes.mostrar();
                return false;
            }
            
            case 6 -> {
                mostrarAyuda();
                return false;
            }
            
            case 7 -> {
                return confirmarSalida();
            }
            
            default -> {
                System.out.println("\n Opción inválida. Por favor, seleccione una opción del 1 al 7.");
                return false;
            }
        }
    }
    
    /**
     * Muestra el menú de gestión de organizadores
     */
    private void mostrarMenuOrganizadores() {
        var volver = false;
        
        while (!volver) {
            try {
                limpiarPantalla();
                System.out.println("====================================================================");
                System.out.println("                    GESTIÓN DE ORGANIZADORES                         ");
                System.out.println("====================================================================");
                System.out.println();
                System.out.println("  1. Registrar Organizador");
                System.out.println("  2. Buscar Organizador por Email");
                System.out.println("  3. Listar Todos los Organizadores");
                System.out.println("  4. Ver Eventos de un Organizador");
                System.out.println("  0. Volver al Menú Principal");
                System.out.println();
                System.out.println("====================================================================");
                System.out.print("Seleccione una opción: ");
                
                var opcion = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                
                switch (opcion) {
                    case 1 -> menuParticipantes.registrarOrganizador();
                    case 2 -> menuParticipantes.buscarOrganizadorPorEmail();
                    case 3 -> menuParticipantes.listarOrganizadores();
                    case 4 -> menuParticipantes.verEventosDeOrganizador();
                    case 0 -> volver = true;
                    default -> System.out.println("❌ Opción inválida.");
                }
                
                if (!volver) {
                    pausar();
                }
                
            } catch (InputMismatchException e) {
                System.out.println("\n Error: Debe ingresar un número válido.");
                scanner.nextLine();
                pausar();
            }
        }
    }

    /**
     * Muestra la pantalla de ayuda
     */
    private void mostrarAyuda() {
        limpiarPantalla();
        System.out.println("====================================================================");
        System.out.println("                            AYUDA                                    ");
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("ACERCA DEL SISTEMA");
        System.out.println("====================================================================");
        System.out.println("Event Planner es un sistema completo para la gestión de eventos que");
        System.out.println("permite organizar conferencias, talleres, ferias y más.");
        System.out.println();
        System.out.println("FUNCIONALIDADES PRINCIPALES");
        System.out.println("====================================================================");
        System.out.println("1. Crear y gestionar eventos de diferentes tipos");
        System.out.println("2. Registrar participantes y organizadores");
        System.out.println("3. Inscribir participantes a eventos");
        System.out.println("4. Sistema de compra de tickets con múltiples opciones");
        System.out.println("5. Procesar pagos con diferentes métodos");
        System.out.println("6. Controlar asistencia con check-in");
        System.out.println("7. Generar reportes financieros y de asistencia");
        System.out.println("8. Visualizar dashboard con métricas en tiempo real");
        System.out.println();
        System.out.println("TIPOS DE EVENTOS SOPORTADOS");
        System.out.println("====================================================================");
        System.out.println("1. CONFERENCIA - Eventos educativos y presentaciones");
        System.out.println("2. TALLER - Sesiones prácticas y capacitaciones");
        System.out.println("3. NETWORKING - Eventos de conexión profesional");
        System.out.println("4. FERIA - Exposiciones y muestras");
        System.out.println("5. WEBINAR - Eventos virtuales en línea");
        System.out.println();
        System.out.println("TIPOS DE TICKETS DISPONIBLES");
        System.out.println("====================================================================");
        System.out.println("1. GRATUITO - Sin costo, acceso básico");
        System.out.println("2. ESTÁNDAR - $50,000 COP - Acceso completo + Material digital");
        System.out.println("3. PREMIUM - $100,000 COP - Material físico + Certificado");
        System.out.println("4. VIP - $200,000 COP - Networking exclusivo + Todos los beneficios");
        System.out.println();
        System.out.println("MEMBRESÍA VIP");
        System.out.println("====================================================================");
        System.out.println("• Descuento del 20% en todos los tickets de pago");
        System.out.println("• Acceso prioritario a eventos");
        System.out.println("• Beneficios exclusivos en networking");
        System.out.println();
        System.out.println("MÉTODOS DE PAGO");
        System.out.println("====================================================================");
        System.out.println("• Efectivo (sin comisión)");
        System.out.println("• Tarjeta de Crédito (3% comisión)");
        System.out.println("• Tarjeta de Débito (1.5% comisión)");
        System.out.println("• Transferencia Bancaria (1% comisión)");
        System.out.println("• PSE (2% comisión)");
        System.out.println("• Nequi (1.5% comisión)");
        System.out.println("• Daviplata (1.5% comisión)");
        System.out.println();
        System.out.println("ESTADOS DE EVENTOS");
        System.out.println("====================================================================");
        System.out.println("1. BORRADOR - Evento en preparación");
        System.out.println("2. PUBLICADO - Evento disponible para inscripciones");
        System.out.println("3. EN_CURSO - Evento actualmente en desarrollo");
        System.out.println("4. FINALIZADO - Evento completado");
        System.out.println("5. CANCELADO - Evento cancelado");
        System.out.println();
        System.out.println("CONSEJOS DE USO");
        System.out.println("====================================================================");
        System.out.println("1. Primero registre organizadores antes de crear eventos");
        System.out.println("2. Registre participantes antes de inscribirlos a eventos");
        System.out.println("3. Use el dashboard para monitorear el estado del sistema");
        System.out.println("4. Genere reportes regularmente para análisis de desempeño");
        System.out.println("5. Revise las alertas para eventos próximos a llenarse");
        System.out.println("6. Los participantes VIP obtienen 20% de descuento automático");
        System.out.println();
        System.out.println("MODELO DE NEGOCIO");
        System.out.println("====================================================================");
        System.out.println("• Comisión de plataforma: 5% por cada ticket vendido");
        System.out.println("• Organizadores reciben: 95% del precio del ticket");
        System.out.println("• Comisiones adicionales según método de pago");
        System.out.println();
        System.out.println("SOPORTE");
        System.out.println("====================================================================");
        System.out.println("Para soporte técnico o consultas:");
        System.out.println("Email: soporte@eventplanner.com");
        System.out.println("Versión: 1.0.0");
        System.out.println("Desarrollado por: Universidad Popular del Cesar");
        System.out.println();
    }
    
    /**
     * Confirma si el usuario desea salir
     * 
     * @return true si confirma la salida, false en caso contrario
     */
    private boolean confirmarSalida() {
        System.out.println("====================================================================");
        System.out.print("¿Está seguro que desea salir? (S/N): ");
        var respuesta = scanner.nextLine().trim().toUpperCase();
        
        if (respuesta.equals("S") || respuesta.equals("SI") || respuesta.equals("SÍ")) {
            return true;
        } else {
            System.out.println("\n Continuando en el sistema...");
            return false;
        }
    }
    
    /**
     * Muestra el mensaje de despedida
     */
    private void mostrarDespedida() {
        limpiarPantalla();
        System.out.println("====================================================================");
        System.out.println("                                                                      ");
        System.out.println("                    ¡HASTA PRONTO!                                    ");
        System.out.println("                                                                      ");
        System.out.println("              Gracias por usar Event Planner                          ");
        System.out.println("                                                                      ");
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("              Sistema cerrado exitosamente");
        System.out.println();
    }
    
    /**
     * Limpia la pantalla (simulado con líneas en blanco)
     */
    private void limpiarPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    private void pausar() {
        System.out.println();
        System.out.println("====================================================================");
        System.out.print("Presione ENTER para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Muestra un mensaje de éxito
     * 
     * @param mensaje Mensaje a mostrar
     */
    public static void mostrarExito(String mensaje) {
        System.out.println(" Exito: " + mensaje);
    }
    
    /**
     * Muestra un mensaje de error
     * 
     * @param mensaje Mensaje a mostrar
     */
    public static void mostrarError(String mensaje) {
        System.out.println(" Error: " + mensaje);
    }
    
    /**
     * Muestra un mensaje de advertencia
     * 
     * @param mensaje Mensaje a mostrar
     */
    public static void mostrarAdvertencia(String mensaje) {
        System.out.println("️ Advertencia: " + mensaje);
    }
    
    /**
     * Muestra un mensaje informativo
     * 
     * @param mensaje Mensaje a mostrar
     */
    public static void mostrarInfo(String mensaje) {
        System.out.println("ℹ️ " + mensaje);
    }
}