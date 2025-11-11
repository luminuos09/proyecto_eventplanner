/**
 * MenuReportes - Gestiona el menu de reportes y dashboard
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion;

import logica.GestorEventos;
import logica.DashBoard;
import logica.GenerarReportes;
import modelos.*;
import excepciones.*;

import java.time.LocalDateTime;
import java.util.Scanner;

public class MenuReportes {
    
    private final GestorEventos gestor;
    private final Scanner scanner;
    private final DashBoard dashboard;
    private final GenerarReportes generarReportes;
    
    /**
     * Constructor
     * @param gestor Instancia del gestor de eventos
     * @param scanner 
     */
    public MenuReportes(GestorEventos gestor, Scanner scanner) {
        this.gestor = gestor;
        this.scanner = scanner;
        this.dashboard = new DashBoard(gestor);
        this.generarReportes = new GenerarReportes(gestor);
    }
    
    /**
     * Muestra el menú de reportes
     */
    public void mostrar() {
        var volver = false;
        
        while (!volver) {
            try {
                limpiarPantalla(); 
                mostrarOpciones();
                
                System.out.print("Seleccione una opción: ");
                var opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
                System.out.println();
                
                switch (opcion) {
                    case 1 -> mostrarDashboard();
                    case 2 -> generarReporteAsistencia();
                    case 3 -> mostrarEstadisticasGenerales();
                    case 4 -> compararEventos();
                    case 5 -> analizarTendencias();
                    case 6 -> mostrarTopEventos();
                    case 7 -> generarReportePorOrganizador();
                    case 8 -> generarResumenEjecutivo();
                    case 9 -> vistaRapida();
                    case 0 -> volver = true;
                    default -> MenuPrincipalConsola.mostrarError("Opción inválida");
                }
                
                if (!volver) {
                    pausar();
                }
                
            } catch (EventPlannerException e) {
                MenuPrincipalConsola.mostrarError("Entrada inválida: " + e.getMessage());
                scanner.nextLine();
                pausar();
            }
        }
    }
    
        /**
         * Muestra las opciones del menú
         */
        private void mostrarOpciones() {
            System.out.println("====================================================================");
            System.out.println("                    REPORTES Y DASHBOARD                             ");
            System.out.println("====================================================================");
            System.out.println();
            System.out.println("  DASHBOARD");
            System.out.println("  ====================================================================");
            System.out.println("  1.  Ver Dashboard Principal");
            System.out.println("  9.  Vista Rápida");
            System.out.println();
            System.out.println("  REPORTES DETALLADOS");
            System.out.println("  ====================================================================");
            System.out.println("  2.  Reporte de Asistencia de Evento");
            System.out.println("  3.  Estadísticas Generales del Sistema");
            System.out.println("  7.  Reporte por Organizador");
            System.out.println();
            System.out.println("  ANÁLISIS");
            System.out.println("  ====================================================================");
            System.out.println("  4.  Comparar Dos Eventos");
            System.out.println("  5.  Análisis de Tendencias");
            System.out.println("  6.  Top Eventos Más Exitosos");
            System.out.println("  8.  Resumen Ejecutivo Completo");
            System.out.println();
            System.out.println("  0.  Volver al Menú Principal");
            System.out.println();
            System.out.println("====================================================================");
        }
        
            /**
             * Muestra el dashboard principal
             */
            private void mostrarDashboard() {
                limpiarPantalla();
                System.out.println(dashboard.mostrarPanelPrincipal());
                
                System.out.println("\n¿Desea ver más detalles?");
                System.out.println("1. Ver eventos activos");
                System.out.println("2. Ver alertas");
                System.out.println("3. Ver métricas");
                System.out.println("0. Volver");
                
                System.out.print("\nOpción: ");
                var opcion = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcion) {
                    case 1 -> {
                        System.out.println("\n" + dashboard.mostrarEventosActivos());
                        pausar();
            }
                    case 2 -> {
                        System.out.println("\n" + dashboard.mostrarAlertas());
                        pausar();
            }
                    case 3 -> {
                        System.out.println("\n" + dashboard.mostrarMetricasGenerales());
                        pausar();
            }
                }
            }
            
            /**
             * Genera un reporte de asistencia para un evento específico
             */
                private void generarReporteAsistencia() {
                    System.out.println("====================================================================");
                    System.out.println("                    REPORTE DE ASISTENCIA                            ");
                    System.out.println("====================================================================");
                    System.out.println();
                    
                    try {
                        // Listar eventos disponibles
                        System.out.println("EVENTOS DISPONIBLES:");
                        var eventos = gestor.obtenerTodosEventos();
                        
                        if (eventos.isEmpty()) {
                            MenuPrincipalConsola.mostrarInfo("No hay eventos registrados.");
                            return;
                        }
                        
                        for (int i = 0; i < eventos.size(); i++) {
                            Evento e = eventos.get(i);
                            System.out.println((i + 1) + ". " + e.getNombre() + " [" + e.getEstado().getDescripcion() + "]");
                            System.out.println("   ID: " + e.getId());
                        }
                        
                        System.out.print("\nIngrese el ID del evento: ");
                        var eventoId = scanner.nextLine();
                        
                        limpiarPantalla();
                        var reporte = generarReportes.generarReporteAsistencia(eventoId);
                        System.out.println(reporte);
                        
                        // Opción para exportar (simulado)
                        System.out.print("\n¿Desea guardar este reporte? (S/N): ");
                        var respuesta = scanner.nextLine().trim().toUpperCase();
                        
                        if (respuesta.equals("S") || respuesta.equals("SI") || respuesta.equals("SÍ")) {
                            MenuPrincipalConsola.mostrarExito("Reporte guardado como: reporte_" + eventoId + ".txt");
                            MenuPrincipalConsola.mostrarInfo("(Funcionalidad de guardado simulada)");
                        }
                        
                    } catch (EventPlannerException e) {
                        MenuPrincipalConsola.mostrarError(e.getMessage());
                    }
                }
                
                    /**
                     * Muestra las estadísticas generales del sistema
                     */
                    private void mostrarEstadisticasGenerales()throws EventPlannerException {
                        try{
                        
                        limpiarPantalla();
                        System.out.println("====================================================================");
                        System.out.println("                     ESTADÍSTICAS GENERALES                          ");
                        System.out.println("====================================================================");
                        System.out.println();
                        
                        var estadisticas = generarReportes.generarEstadisticasGenerales();
                        System.out.println(estadisticas);
                        
                        // Mostrar evento más exitoso
                        Evento eventoExitoso = generarReportes.obtenerEventoMasExitoso();
                        if (eventoExitoso != null) {
                            System.out.println("\nEVENTO MÁS EXITOSO");
                            System.out.println("Nombre: " + eventoExitoso.getNombre());
                            System.out.println("Tipo: " + eventoExitoso.getTipo().getDescripcion());
                            System.out.println("Asistencia: " + String.format("%.2f%%", 
                                eventoExitoso.calcularPorcentajeAsistencia()));
                            System.out.println("Registrados: " + eventoExitoso.getParticipantesRegistrados().size());
                            System.out.println("Asistentes: " + eventoExitoso.getParticipantesAsistentes().size());
                        }
                        } catch (EventPlannerException e) {
                            throw new EventPlannerException("Error al generar estadísticas generales: " + e.getMessage(), e);
                        }
                    }
                        /**
                         * Compara dos eventos
                         */
                        private void compararEventos() {
                            System.out.println("====================================================================");
                            System.out.println("                       COMPARAR EVENTOS                              ");
                            System.out.println("====================================================================");
                            System.out.println();
                            
                            try {
                                // Mostrar eventos con asistencia
                                var eventos = gestor.obtenerTodosEventos();
                                var eventosConAsistencia = new java.util.ArrayList<Evento>();
                                
                                for (Evento e : eventos) {
                                    if (!e.getParticipantesAsistentes().isEmpty()) {
                                        eventosConAsistencia.add(e);
                                    }
                                }
                                
                                if (eventosConAsistencia.size() < 2) {
                                    MenuPrincipalConsola.mostrarInfo("Se necesitan al menos 2 eventos con asistencia registrada.");
                                    return;
                                }
                                
                                System.out.println("EVENTOS CON ASISTENCIA:");
                                
                                
                                for (int i = 0; i < eventosConAsistencia.size(); i++) {
                                    Evento e = eventosConAsistencia.get(i);
                                    System.out.println((i + 1) + ". " + e.getNombre() + " - Asistencia: " + String.format("%.2f%%", e.calcularPorcentajeAsistencia()));
                                    System.out.println("   ID: " + e.getId());
                                }
                                
                                System.out.print("\nID del primer evento: ");
                                var id1 = scanner.nextLine();
                                
                                System.out.print("ID del segundo evento: ");
                                var id2 = scanner.nextLine();
                                
                                limpiarPantalla();
                                var comparacion = generarReportes.compararEventos(id1, id2);
                                System.out.println(comparacion);
                                
                            } catch (EventPlannerException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        }
                        
                            /**
                             * Muestra el análisis de tendencias
                             */
                            private void analizarTendencias() {
                                limpiarPantalla();
                                System.out.println("====================================================================");
                                System.out.println("                    ANÁLISIS DE TENDENCIAS                            ");
                                System.out.println("====================================================================");
                                System.out.println();
                                
                                var tendencias = generarReportes.analizarTendencias();
                                System.out.println(tendencias);
                                
                                // Análisis adicional
                                var promedioAsistencia = generarReportes.calcularPromedioAsistencia();
                                
                                System.out.println("RECOMENDACIONES");
                                
                                if (promedioAsistencia >= 80) {
                                    System.out.println("Excelente desempeño general del sistema");
                                    System.out.println("    Continuar con las estrategias actuales");
                                    System.out.println("    Considerar aumentar capacidad de eventos populares");
                                } else 
                                if (promedioAsistencia >= 60) {
                                    System.out.println("Buen desempeño, con margen de mejora");
                                    System.out.println("    Analizar eventos con baja asistencia");
                                    System.out.println("    Mejorar comunicación previa al evento");
                                } else 
                                if (promedioAsistencia >= 40) {
                                    System.out.println("Desempeño regular, se requieren mejoras");
                                    System.out.println("    Revisar estrategias de promoción");
                                    System.out.println("    Encuestar participantes sobre preferencias");
                                    System.out.println("    Optimizar horarios y ubicaciones");
                                } else {
                                    System.out.println("Desempeño bajo, acción inmediata requerida");
                                    System.out.println("    Reevaluar tipos de eventos ofrecidos");
                                    System.out.println("    Mejorar experiencia del participante");
                                    System.out.println("    Aumentar seguimiento post-inscripción");
                                }
                            }
                            
                                /**
                                 * Muestra el top de eventos más exitosos
                                 */
                                private void mostrarTopEventos() {
                                    System.out.println("====================================================================");
                                    System.out.println("                    TOP EVENTOS MÁS EXITOSOS                         ");
                                    System.out.println("====================================================================");
                                    System.out.println();
                                    
                                    System.out.print("¿Cuántos eventos desea ver? (1-10): ");
                                    var limite = scanner.nextInt();
                                    scanner.nextLine();
                                    
                                    if (limite < 1) limite = 5;
                                    if (limite > 10) limite = 10;
                                    
                                    limpiarPantalla();
                                    var top = dashboard.mostrarTopEventos(limite);
                                    System.out.println(top);
                                }
                                
                                    /**
                                     * Genera reporte por organizador
                                     */
                                    private void generarReportePorOrganizador() {
                                        System.out.println("====================================================================");
                                        System.out.println("                    REPORTE POR ORGANIZADOR                          ");
                                        System.out.println("====================================================================");
                                        System.out.println();
                                        
                                        try {
                                            // Listar organizadores
                                            var organizadores = gestor.obtenerTodosOrganizadores();
                                            
                                            if (organizadores.isEmpty()) {
                                                MenuPrincipalConsola.mostrarInfo("No hay organizadores registrados.");
                                                return;
                                            }
                                            
                                            System.out.println("ORGANIZADORES DISPONIBLES:");
                                            
                                            
                                            for (int i = 0; i < organizadores.size(); i++) {
                                                Organizador org = organizadores.get(i);
                                                System.out.println((i + 1) + ". " + org.getNombre() + " - " + org.getOrganizacion());
                                                System.out.println("   ID: " + org.getId() + " | Email: " + org.getEmail());
                                                System.out.println("   Eventos: " + org.getEventosCreados().size());
                                                System.out.println();
                                            }
                                            
                                            System.out.print("Ingrese el ID del organizador: ");
                                            String organizadorId = scanner.nextLine();
                                            
                                            limpiarPantalla();
                                            String reporte = generarReportes.generarReportePorOrganizador(organizadorId);
                                            System.out.println(reporte);
                                            
                                        } catch (EventPlannerException e) {
                                            MenuPrincipalConsola.mostrarError(e.getMessage());
                                        }
                                    }
                                    
                                    /**
                                     * Genera el resumen ejecutivo completo
                                     */
                                        private void generarResumenEjecutivo() {
                                            limpiarPantalla();
                                            System.out.println("====================================================================");
                                            System.out.println("                    RESUMEN EJECUTIVO                                ");
                                            System.out.println("====================================================================");
                                            System.out.println();
                                            
                                            MenuPrincipalConsola.mostrarInfo("Generando resumen ejecutivo completo...");
                                            System.out.println();
                                            
                                            try {
                                                String resumen = generarReportes.generarResumenEjecutivo();
                                                System.out.println(resumen);
                                                
                                                // Opciones adicionales
                                                System.out.println("\nOPCIONES ADICIONALES");
                                                
                                                System.out.println("1. Ver reporte integrado del dashboard");
                                                System.out.println("2. Exportar resumen (simulado)");
                                                System.out.println("0. Volver");
                                                
                                                System.out.print("\nOpción: ");
                                                var opcion = scanner.nextInt();
                                                scanner.nextLine();
                                                
                                                switch (opcion) {
                                                    case 1 -> {
                                                        limpiarPantalla();
                                                        System.out.println(dashboard.mostrarReporteIntegrado(true));
                                                        pausar();
                                                    }
                                                    case 2 -> {
                                                        MenuPrincipalConsola.mostrarExito("Resumen exportado como: resumen_ejecutivo.pdf");
                                                        MenuPrincipalConsola.mostrarInfo("(Funcionalidad de exportación simulada)");
                                                        pausar();
                                                    }
                                                }
                                                
                                            } catch (EventPlannerException e) {
                                                MenuPrincipalConsola.mostrarError(e.getMessage());
                                            }
                                        }
                                        
                                            /**
                                             * Muestra una vista rápida del sistema
                                             */
                                            private void vistaRapida() {
                                                limpiarPantalla();
                                                System.out.println(dashboard.generarVistaRapida());
                                                
                                                
                                                System.out.println("MÉTRICAS RÁPIDAS ADICIONALES");
                                                
                                                
                                                // Calcular algunas métricas rápidas
                                                var eventos = gestor.obtenerTodosEventos();
                                                var eventosHoy = 0;
                                                var eventosProximaSemana = 0;
                                                
                                                LocalDateTime hoy = LocalDateTime.now();
                                                LocalDateTime finSemana = hoy.plusDays(7);
                                                LocalDateTime finDia = hoy.plusDays(1);
                                                
                                                for (Evento e : eventos) {
                                                    if (e.getFechaInicio().isAfter(hoy) && e.getFechaInicio().isBefore(finDia)) {
                                                        eventosHoy++;
                                                    }
                                                    if (e.getFechaInicio().isAfter(hoy) && e.getFechaInicio().isBefore(finSemana)) {
                                                        eventosProximaSemana++;
                                                    }
                                                }
                                                
                                                System.out.println("Eventos hoy: " + eventosHoy);
                                                System.out.println("Eventos próxima semana: " + eventosProximaSemana);
                                                
                                                double promedioAsistencia = generarReportes.calcularPromedioAsistencia();
                                                System.out.println("Promedio de asistencia: " + String.format("%.2f%%", promedioAsistencia));
                                                
                                                // Indicador de salud del sistema
                                                System.out.println("\nSALUD DEL SISTEMA");
                                                System.out.println("─".repeat(70));
                                                
                                                String estado;
                                                if (promedioAsistencia >= 80) {
                                                    estado = "EXCELENTE";
                                                } else
                                                if (promedioAsistencia >= 60) {
                                                    estado = "BUENO";
                                                } else 
                                                if (promedioAsistencia >= 40) {
                                                    estado = "REGULAR";
                                                } else {
                                                    estado = "NECESITA ATENCIÓN";
                                                }
                                                
                                                System.out.println("Estado: " + estado);
                                                System.out.println("Nivel de actividad: " + (eventos.size() > 10 ? "Alto" : eventos.size() > 5 ? "Medio" : "Bajo"));
                                            }
                                                /**
                                                * Limpia la pantalla
                                                */
                                                private void limpiarPantalla() {
                                                    for (int i = 0; i < 3; i++) {
                                                        System.out.println();
                                                    }
                                                }
                                                
                                                /**
                                                * Pausa la ejecución
                                                */
                                                private void pausar() {
                                                    System.out.print("Presione ENTER para continuar...");
                                                    scanner.nextLine();
                                                }
}