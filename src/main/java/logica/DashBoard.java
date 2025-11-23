/**
 * DashBoard - Clase para gestionar y mostrar el panel de control del sistema de gestión de eventos.
 * Proporciona una vista general de métricas, eventos activos, alertas y reportes. 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package logica;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import modelos.EstadoEvento;
import modelos.Evento;
import modelos.Organizador;
import modelos.Participante;

public class DashBoard {
    
    private final GestorEventos gestor;
    private GenerarReportes generadorReportes;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /*Constructor */
    public DashBoard(GestorEventos gestorEventos) {
        this.gestor = new GestorEventos();
        this.generadorReportes = new GenerarReportes(gestorEventos);
    }
        /**
         * Muestra el panel principal del dashboard con métricas, eventos activos y alertas.
         * @return String con la representación del panel principal
         */
        public String mostrarPanelPrincipal(){
            StringBuilder panel = new StringBuilder();
            
            panel.append("====================================================================\n");
            panel.append("                      DASHBOARD - EVENT PLANNER                      \n");
            panel.append("                      Sistema de Gestión de Eventos                  \n");
            panel.append("====================================================================\n\n");
            
            // Fecha y hora actual
            LocalDateTime ahora = LocalDateTime.now();
            panel.append(String.format(" Fecha: %s\n", ahora.format(FORMATO_FECHA)));
            panel.append("============================================================\n");
            
            // Métricas principales
            panel.append(mostrarMetricasGenerales());
            panel.append("\n");
            
            // Eventos activos
            panel.append(mostrarEventosActivos());
            panel.append("\n");
            
            // Alertas
            panel.append(mostrarAlertas());
            panel.append("\n");
            
            return panel.toString();
        }
            /**
             * Muestra las métricas generales del sistema
             * @return String con las métricas generales
             */
            public String mostrarMetricasGenerales(){
                ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
                ArrayList<Participante> todosParticipantes = gestor.obtenerTodosParticipantes();
                ArrayList<Organizador> todosOrganizadores = gestor.obtenerTodosOrganizadores();
                
                var eventosPublicados = 0;
                var eventosEnCurso = 0;
                var eventosProximos = 0;
                var totalRegistrados = 0;
                
                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime proximaSemana = ahora.plusDays(7);
                
                for (Evento evento : todosEventos) {
                    if (evento.getEstado() == EstadoEvento.PUBLICADO) {
                        eventosPublicados++;
                        // Verificar si es próximo (en los próximos 7 días)
                        if (evento.getFechaInicio().isAfter(ahora) && 
                            evento.getFechaInicio().isBefore(proximaSemana)) {
                            eventosProximos++;
                        }
                    } else if (evento.getEstado() == EstadoEvento.EN_CURSO) {
                        eventosEnCurso++;
                    }
                    totalRegistrados += evento.getParticipantesRegistrados().size();
                }
                
                var promedioAsistencia = generadorReportes.calcularPromedioAsistencia();
                
                StringBuilder metricas = new StringBuilder();
                metricas.append(" METRICAS PRINCIPALES\n");
                metricas.append("============================================================\n");
                
                // Primera fila de métricas
                metricas.append(String.format("========================================================================\n"));
                metricas.append(String.format("   Total Eventos     Publicados       En Curso        Proximos          \n"));
                metricas.append(String.format("       %6d              %6d              %6d               %6d          \n", todosEventos.size(), eventosPublicados, eventosEnCurso, eventosProximos));
                metricas.append(String.format("========================================================================\n"));
                
                // Segunda fila de métricas
                metricas.append(String.format("========================================================================\n"));
                metricas.append(String.format("   Participantes       Organizadores         Promedio Asistencia        \n"));
                metricas.append(String.format("       %6d                  %6d                   %.2f%%                \n", todosParticipantes.size(), todosOrganizadores.size(), promedioAsistencia));
                metricas.append(String.format("========================================================================\n"));
                
                // Mostrar total de registrados en eventos para usar la variable y evitar el warning
                metricas.append(String.format("\nTotal Registrados en eventos: %d\n", totalRegistrados));
                
                return metricas.toString();
            }
                /**
                 * Muestra los eventos activos (en curso y próximos)
                 * @return String con los eventos activos
                 */
                public String mostrarEventosActivos() {
                    ArrayList<Evento> eventosPublicados = gestor.buscarEventosPorEstado(EstadoEvento.PUBLICADO);
                    ArrayList<Evento> eventosEnCurso = gestor.buscarEventosPorEstado(EstadoEvento.EN_CURSO);
                    
                    StringBuilder eventos = new StringBuilder();
                    eventos.append(" EVENTOS ACTIVOS\n");
                    eventos.append("============================================================\n");
                    
                    if (eventosEnCurso.isEmpty() && eventosPublicados.isEmpty()) {
                        eventos.append("   No hay eventos activos en este momento.\n");
                        return eventos.toString();
                    }
                    
                    // Eventos en curso
                    if (!eventosEnCurso.isEmpty()) {
                        eventos.append("\n EN CURSO AHORA:\n");
                        for (Evento evento : eventosEnCurso) {
                            var registrados = evento.getParticipantesRegistrados().size();
                            var asistentes = evento.getParticipantesAsistentes().size();
                            eventos.append(String.format("   • %s [%s]\n", evento.getNombre(), evento.getTipo().getDescripcion()));
                            eventos.append(String.format("     Registrados: %d | Asistentes: %d | Cupos: %d\n",registrados, asistentes, evento.getCuposDisponibles()));
                        }
                    }
                    
                    // Eventos próximos (en los próximos 7 días)
                    if (!eventosPublicados.isEmpty()) {
                        eventos.append("\n PRÓXIMOS EVENTOS:\n");
                        var ahora = LocalDateTime.now();
                        var proximaSemana = ahora.plusDays(7);
                        var contador = 0;
                        
                        for (Evento evento : eventosPublicados) {
                            if (evento.getFechaInicio().isAfter(ahora) && evento.getFechaInicio().isBefore(proximaSemana)) {
                                eventos.append(String.format("   • %s\n", evento.getNombre()));
                                eventos.append(String.format("     Fecha: %s | Tipo: %s\n",
                                evento.getFechaInicio().format(FORMATO_FECHA),
                                evento.getTipo().getDescripcion()));
                                eventos.append(String.format("     Registrados: %d/%d | Ubicación: %s\n",evento.getParticipantesRegistrados().size(),evento.getCapacidadMaxima(),evento.getUbicacion()));
                                contador++;
                                if (contador >= 5) break; // Mostrar máximo 5
                            }
                        }
                        
                        if (contador == 0) {
                            eventos.append("   No hay eventos programados para los próximos 7 días.\n");
                        }
                    }
                    
                    return eventos.toString();
                }
                    /**
                     * Muestra alertas y notificaciones importantes
                     * @return String con las alertas
                     */
                    public String mostrarAlertas() {
                        StringBuilder alertas = new StringBuilder();
                        alertas.append(" ALERTAS Y NOTIFICACIONES\n");
                        alertas.append("============================================================\n");
                        
                        ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
                        var hayAlertas = false;
                        
                        // Alertas de eventos próximos a llenarse
                        for (Evento evento : todosEventos) {
                            if (evento.getEstado() == EstadoEvento.PUBLICADO) {
                                int cuposDisponibles = evento.getCuposDisponibles();
                                double porcentajeOcupacion = (evento.getParticipantesRegistrados().size() * 100.0) / evento.getCapacidadMaxima();
                                
                                if (cuposDisponibles == 0) {
                                    alertas.append(String.format("    LLENO: \"%s\" ha alcanzado su capacidad máxima\n", 
                                        evento.getNombre()));
                                    hayAlertas = true;
                                } else 
                                if (cuposDisponibles <= 5) {
                                    alertas.append(String.format("    CASI LLENO: \"%s\" solo tiene %d cupos disponibles\n", 
                                        evento.getNombre(), cuposDisponibles));
                                    hayAlertas = true;
                                } else 
                                if (porcentajeOcupacion >= 80) {
                                    alertas.append(String.format("    ALTA DEMANDA: \"%s\" tiene %.0f%% de ocupación\n", 
                                        evento.getNombre(), porcentajeOcupacion));
                                    hayAlertas = true;
                                }
                            }
                        }
                        
                        // Alertas de eventos próximos a iniciar
                        LocalDateTime ahora = LocalDateTime.now();
                        LocalDateTime proximasHoras = ahora.plusHours(24);
                        
                        for (Evento evento : todosEventos) {
                            if (evento.getEstado() == EstadoEvento.PUBLICADO &&evento.getFechaInicio().isAfter(ahora) &&evento.getFechaInicio().isBefore(proximasHoras)) {
                                alertas.append(String.format("      PROXIMO: \"%s\" inicia en menos de 24 horas\n", evento.getNombre()));
                                alertas.append(String.format("      Hora: %s\n", 
                                evento.getFechaInicio().format(FORMATO_FECHA)));
                                hayAlertas = true;
                            }
                        }
                        
                        // Alertas de baja asistencia
                        for (Evento evento : todosEventos) {
                            if (evento.getEstado() == EstadoEvento.PUBLICADO) {
                                var registrados = evento.getParticipantesRegistrados().size();
                                if (registrados < 5 && 
                                    evento.getFechaInicio().isBefore(ahora.plusDays(3))) {
                                    alertas.append(String.format("    BAJA INSCRIPCIÓN: \"%s\" solo tiene %d registrados\n", evento.getNombre(), registrados));
                                    hayAlertas = true;
                                }
                            }
                        }
                        
                        if (!hayAlertas) {
                            alertas.append("    No hay alertas en este momento. Todo en orden.\n");
                        }
                        
                        return alertas.toString();
                    }
                        /**
                         * Muestra el top N eventos más exitosos según la asistencia
                         * 
                         * @param limite Número de eventos a mostrar en el top
                         * @return String con el top de eventos más exitosos
                         */
                        public String mostrarTopEventos(int limite) {
                            ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
                            
                            // Ordenar por porcentaje de asistencia
                            todosEventos.sort((e1, e2) ->Double.compare(e2.calcularPorcentajeAsistencia(), e1.calcularPorcentajeAsistencia()));
                            
                            StringBuilder top = new StringBuilder();
                            top.append(" TOP EVENTOS MAS EXITOSOS\n");
                            top.append("============================================================\n");
                            
                            var contador = 0;
                            for (int i = 0; i < todosEventos.size() && contador < limite; i++) {
                                Evento evento = todosEventos.get(i);
                                
                                // Solo mostrar eventos que han tenido asistentes
                                if (!evento.getParticipantesAsistentes().isEmpty()) {
                                    contador++;
                                    var medalla = contador == 1 ? "Oro" : contador == 2 ? "Plata" : contador == 3 ? "Bronce" : "  ";
                                    
                                    top.append(String.format("\n%s %d. %s\n", medalla, contador, evento.getNombre()));
                                    top.append(String.format("      Tipo: %s | Estado: %s\n",evento.getTipo().getDescripcion(),evento.getEstado().getDescripcion()));
                                    top.append(String.format("      Registrados: %d | Asistentes: %d\n",evento.getParticipantesRegistrados().size(),evento.getParticipantesAsistentes().size()));
                                    top.append(String.format("      Asistencia: %.2f%%\n",evento.calcularPorcentajeAsistencia()));
                                }
                            }
                            
                            if (contador == 0) {
                                top.append("   No hay eventos con asistencia registrada aún.\n");
                            }
                            
                            return top.toString();
                        }
                            /**
                             * Genera una vista rápida con las métricas más importantes
                             * 
                             * @return String con la vista rápida
                             */
                            public String generarVistaRapida() {
                                StringBuilder vista = new StringBuilder();
                                
                                vista.append("╔══════════════════════════════════════════════════════════╗\n");
                                vista.append("║                    VISTA RAPIDA                          ║\n");
                                vista.append("╚══════════════════════════════════════════════════════════╝\n\n");
                                
                                // Contadores rápidos
                                var totalEventos = gestor.obtenerTodosEventos().size();
                                var totalParticipantes = gestor.obtenerTodosParticipantes().size();
                                var eventosActivos = gestor.buscarEventosPorEstado(EstadoEvento.PUBLICADO).size()+gestor.buscarEventosPorEstado(EstadoEvento.EN_CURSO).size();
                                
                                vista.append(String.format(" Eventos Totales: %d\n", totalEventos));
                                vista.append(String.format(" Eventos Activos: %d\n", eventosActivos));
                                vista.append(String.format(" Participantes: %d\n", totalParticipantes));
                                
                                // Evento más exitoso
                                Evento eventoExitoso = generadorReportes.obtenerEventoMasExitoso();
                                if (eventoExitoso != null) {
                                    vista.append(String.format("\n Evento Más Exitoso:\n"));
                                    vista.append(String.format("   %s (%.2f%% asistencia)\n", 
                                        eventoExitoso.getNombre(),
                                        eventoExitoso.calcularPorcentajeAsistencia()));
                                }
                                
                                return vista.toString();
                            }
                            
                                /**
                                 * Muestra un reporte integrado con todas las vistas del dashboard
                                 * 
                                 * @param incluirTopEventos Si se debe incluir el top de eventos
                                 * @return String con el reporte completo
                                 */
                                public String mostrarReporteIntegrado(boolean incluirTopEventos) {
                                    StringBuilder reporte = new StringBuilder();
                                    
                                    reporte.append(mostrarPanelPrincipal());
                                    reporte.append("\n");
                                    
                                    if (incluirTopEventos) {
                                        reporte.append(mostrarTopEventos(5));
                                        reporte.append("\n");
                                    }
                                    
                                    // Análisis de tendencias simplificado
                                    reporte.append(" ANÁLISIS RÁPIDO\n");
                                    reporte.append("============================================================\n");
                                    
                                    double promedioAsistencia = generadorReportes.calcularPromedioAsistencia();
                                    reporte.append(String.format("Promedio de Asistencia: %.2f%%\n", promedioAsistencia));
                                    
                                    if (promedioAsistencia >= 80) {
                                        reporte.append("Estado:  EXCELENTE - Los eventos tienen muy buena convocatoria\n");
                                    } else if (promedioAsistencia >= 60) {
                                        reporte.append("Estado:  BUENO - Los eventos mantienen buena asistencia\n");
                                    } else if (promedioAsistencia >= 40) {
                                        reporte.append("Estado:  REGULAR - Se puede mejorar la asistencia\n");
                                    } else {
                                    }
                                    
                                    return reporte.toString();
                                }
}


