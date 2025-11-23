/**
 * Clase GeneradorReportes
 * Responsabilidad: Generar reportes y análisis estadísticos del sistema
 * Capa: Lógica de Negocio
 * 
 * @author Ayner Jose Castro Benavides
 * @version 2.0
 */
package logica;

import excepciones.*;
import modelos.*;
import java.time.LocalDateTime;
import java.time.format.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenerarReportes {

    private final GestorEventos gestor;
    private GestorPagos gestorPagos;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Constructor con solo GestorEventos (para compatibilidad)
     * @param gestor Instancia del GestorEventos para acceder a los datos de eventos
     */
    public GenerarReportes(GestorEventos gestor) {
        this.gestor = gestor;
        this.gestorPagos = null;
    }
    
    /**
     * Constructor completo con GestorEventos y GestorPagos
     * @param gestor Instancia del GestorEventos
     * @param gestorPagos Instancia del GestorPagos
     */
    public GenerarReportes(GestorEventos gestor, GestorPagos gestorPagos) {
        this.gestor = gestor;
        this.gestorPagos = gestorPagos;
    }
    
    // ==================== MÉTRICAS GENERALES PARA DASHBOARD ====================
    
    /**
     * Obtiene el total de eventos en el sistema
     * @return Número total de eventos
     */
    public int obtenerTotalEventos() {
        return gestor.obtenerTodosEventos().size();
    }
    
    /**
     * Obtiene el total de participantes registrados
     * @return Número total de participantes
     */
    public int obtenerTotalParticipantes() {
        return gestor.obtenerTodosParticipantes().size();
    }
    
    /**
     * Obtiene el total de organizadores registrados
     * @return Número total de organizadores
     */
    public int obtenerTotalOrganizadores() {
        return gestor.obtenerTodosOrganizadores().size();
    }
    
    /**
     * Calcula los ingresos totales del sistema
     * @return Ingresos totales en pesos
     */
    public double calcularIngresosTotales() {
        double total = 0.0;
        
        if (gestorPagos != null) {
            ArrayList<Pago> pagos = gestorPagos.obtenerTodosPagos();
            
            for (Pago pago : pagos) {
                if (pago.getEstado() == EstadoPago.APROBADO) {
                    total += pago.getMontoBase();
                }
            }
        }
        
        return total;
    }
    
    /**
     * Obtiene el total de tickets vendidos
     * @return Número total de tickets
     */
    public int obtenerTotalTicketsVendidos() {
        if (gestorPagos == null) return 0;
        
        int total = 0;
        ArrayList<Pago> pagos = gestorPagos.obtenerTodosPagos();
        
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                total++;
            }
        }
        
        return total;
    }
    
    /**
     * Encuentra el evento con mayor número de participantes registrados
     * @return Evento más popular o null si no hay eventos
     */
    public Evento obtenerEventoMasPopular() {
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        
        if (eventos.isEmpty()) return null;
        
        Evento masPopular = eventos.get(0);
        
        for (Evento evento : eventos) {
            if (evento.getParticipantesRegistrados().size() > 
                masPopular.getParticipantesRegistrados().size()) {
                masPopular = evento;
            }
        }
        
        return masPopular;
    }
    
    /**
     * Encuentra el evento que ha generado más ingresos
     * @return Evento más rentable o null si no hay eventos
     */
    public Evento obtenerEventoMasRentable() {
        if (gestorPagos == null) return null;
        
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        if (eventos.isEmpty()) return null;
        
        Map<String, Double> ingresosPorEvento = new HashMap<>();
        
        // Calcular ingresos por evento
        ArrayList<Pago> pagos = gestorPagos.obtenerTodosPagos();
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                String eventoId = pago.getEventoId();
                double ingresoActual = ingresosPorEvento.getOrDefault(eventoId, 0.0);
                ingresosPorEvento.put(eventoId, ingresoActual + pago.getMontoBase());
            }
        }
        
        // Encontrar el evento con mayores ingresos
        String eventoIdMasRentable = null;
        double mayorIngreso = 0.0;
        
        for (Map.Entry<String, Double> entry : ingresosPorEvento.entrySet()) {
            if (entry.getValue() > mayorIngreso) {
                mayorIngreso = entry.getValue();
                eventoIdMasRentable = entry.getKey();
            }
        }
        
        if (eventoIdMasRentable != null) {
            try {
                return gestor.buscarEvento(eventoIdMasRentable);
            } catch (EventoNoEncontradoException e) {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Obtiene los próximos eventos (eventos publicados con fecha futura)
     * @param limite Número máximo de eventos a retornar
     * @return Lista de próximos eventos
     */
    public ArrayList<Evento> obtenerProximosEventos(int limite) {
        ArrayList<Evento> proximos = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        
        for (Evento evento : eventos) {
            if (evento.getEstado() == EstadoEvento.PUBLICADO && 
                evento.getFechaInicio().isAfter(ahora)) {
                proximos.add(evento);
            }
        }
        
        // Ordenar por fecha más cercana
        proximos.sort((e1, e2) -> e1.getFechaInicio().compareTo(e2.getFechaInicio()));
        
        // Limitar resultados
        if (proximos.size() > limite) {
            return new ArrayList<>(proximos.subList(0, limite));
        }
        
        return proximos;
    }
    
    /**
     * Cuenta eventos por cada estado
     * @return Mapa con estado y cantidad de eventos
     */
    public Map<EstadoEvento, Integer> contarEventosPorEstado() {
        Map<EstadoEvento, Integer> conteo = new HashMap<>();
        
        // Inicializar todos los estados en 0
        for (EstadoEvento estado : EstadoEvento.values()) {
            conteo.put(estado, 0);
        }
        
        // Contar eventos
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        for (Evento evento : eventos) {
            EstadoEvento estado = evento.getEstado();
            conteo.put(estado, conteo.get(estado) + 1);
        }
        
        return conteo;
    }
    
    /**
     * Cuenta pagos por cada método de pago
     * @return Mapa con método y cantidad de pagos
     */
    public Map<MetodoPago, Integer> contarPagosPorMetodo() {
        Map<MetodoPago, Integer> conteo = new HashMap<>();
        
        if (gestorPagos == null) return conteo;
        
        // Inicializar todos los métodos en 0
        for (MetodoPago metodo : MetodoPago.values()) {
            conteo.put(metodo, 0);
        }
        
        // Contar pagos
        ArrayList<Pago> pagos = gestorPagos.obtenerTodosPagos();
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                MetodoPago metodo = pago.getMetodoPago();
                conteo.put(metodo, conteo.get(metodo) + 1);
            }
        }
        
        return conteo;
    }
    
    /**
     * Calcula el promedio de participantes por evento
     * @return Promedio de participantes
     */
    public double calcularPromedioParticipantesPorEvento() {
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        
        if (eventos.isEmpty()) return 0.0;
        
        int totalParticipantes = 0;
        for (Evento evento : eventos) {
            totalParticipantes += evento.getParticipantesRegistrados().size();
        }
        
        return (double) totalParticipantes / eventos.size();
    }
    
    /**
     * Calcula la tasa de ocupación promedio de los eventos
     * @return Porcentaje de ocupación promedio
     */
    public double calcularTasaOcupacionPromedio() {
        ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
        
        if (eventos.isEmpty()) return 0.0;
        
        double sumaOcupacion = 0.0;
        int eventosContados = 0;
        
        for (Evento evento : eventos) {
            if (evento.getCapacidadMaxima() > 0) {
                double ocupacion = (double) evento.getParticipantesRegistrados().size() 
                                  / evento.getCapacidadMaxima() * 100;
                sumaOcupacion += ocupacion;
                eventosContados++;
            }
        }
        
        if (eventosContados == 0) return 0.0;
        
        return sumaOcupacion / eventosContados;
    }
    
    // ==================== REPORTES EXISTENTES (MANTENIDOS) ====================
    
    /**
     * Genera un reporte de asistencia para un evento específico
     * @param eventoId ID del evento para el cual se genera el reporte
     * @return String con el reporte formateado
     * @throws EventPlannerException si el evento no se encuentra o hay un error al generar el reporte
     */
    public String generarReporteAsistencia(String eventoId) throws EventPlannerException {
        try {
            Evento evento = gestor.buscarEvento(eventoId);
            
            int totalRegistrados = evento.getParticipantesRegistrados().size();
            int totalAsistentes = evento.getParticipantesAsistentes().size();
            double porcentajeAsistencia = evento.calcularPorcentajeAsistencia();
            int cuposDisponibles = evento.getCuposDisponibles();
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("=======================================================\n");
            reporte.append("           REPORTE DE ASISTENCIA - EVENTO               \n");
            reporte.append("=======================================================\n\n");
            reporte.append(" INFORMACION DEL EVENTO\n");
            reporte.append("=======================================================\n");
            reporte.append(String.format("Nombre: %s \n", evento.getNombre()));
            reporte.append(String.format("Tipo: %s \n", evento.getTipo().getDescripcion()));
            reporte.append(String.format("Estado: %s \n", evento.getEstado().getDescripcion()));
            reporte.append(String.format("Ubicacion: %s \n", evento.getUbicacion()));
            reporte.append(String.format("Fecha Inicio: %s \n", evento.getFechaInicio().format(FORMATO_FECHA)));
            reporte.append(String.format("Fecha Fin: %s \n\n", evento.getFechaFin().format(FORMATO_FECHA)));

            reporte.append("  ESTADISTICAS DE ASISTENCIA\n");
            reporte.append("=======================================================\n");
            reporte.append(String.format("Capacidad Maxima: %d personas \n", evento.getCapacidadMaxima()));
            reporte.append(String.format("Registrados: %d personas \n", totalRegistrados));
            reporte.append(String.format("Asistieron: %d personas \n", totalAsistentes));
            reporte.append(String.format("Cupos Disponibles: %d \n", cuposDisponibles));
            reporte.append(String.format("Porcentaje de Asistencia: %.2f%% \n\n", porcentajeAsistencia));
            
            // Analisis de ocupacion
            var porcentajeOcupacion = (totalRegistrados * 100.0) / evento.getCapacidadMaxima();
            reporte.append("  ANALISIS \n");
            reporte.append("=======================================================\n");
            reporte.append(String.format("Ocupacion: %.2f%% \n", porcentajeOcupacion));
            
            if (porcentajeAsistencia >= 80) {
                reporte.append(" Excelente asistencia \n");
            } else if (porcentajeAsistencia >= 60) {
                reporte.append(" Buena asistencia \n");
            } else if (porcentajeAsistencia >= 40) {
                reporte.append(" Asistencia regular \n");
            } else {
                reporte.append(" Asistencia baja \n");
            }
            
            if (cuposDisponibles == 0) {
                reporte.append(" Evento lleno - Maxima capacidad alcanzada\n");
            } else if (cuposDisponibles < 10) {
                reporte.append(" Pocos cupos disponibles\n");
            }
            reporte.append("\n");
            return reporte.toString();
            
        } catch (EventoNoEncontradoException e) {
            throw new EventPlannerException("No se pudo generar el reporte: " + e.getMessage(), e);
        }
    }
    
    /**
     * Genera estadísticas generales del sistema de eventos
     * @return String con las estadísticas formateadas
     * @throws EventPlannerException si hay un error al obtener los datos
     */
    public String generarEstadisticasGenerales() throws EventPlannerException {
        ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
        ArrayList<Organizador> todosOrganizadores = gestor.obtenerTodosOrganizadores();
        ArrayList<Participante> todosParticipantes = gestor.obtenerTodosParticipantes();

        var eventosPublicados = 0;
        var eventosEnCurso = 0;
        var eventosFinalizados = 0;
        var eventosCancelados = 0;
        var totalRegistrados = 0;
        var totalAsistentes = 0;
        Map<TipoEvento, Integer> eventosPorTipo = new HashMap<>();
        
        for (TipoEvento tipo : TipoEvento.values()) {
            eventosPorTipo.put(tipo, 0);
        }
        
        for (Evento evento : todosEventos) {
            switch (evento.getEstado()) {
                case PUBLICADO -> eventosPublicados++;
                case EN_CURSO -> eventosEnCurso++;
                case FINALIZADO -> eventosFinalizados++;
                case CANCELADO -> eventosCancelados++;
                default -> {
                }
            }
            eventosPorTipo.put(evento.getTipo(), eventosPorTipo.get(evento.getTipo()) + 1);
            totalRegistrados += evento.getParticipantesRegistrados().size();
            totalAsistentes += evento.getParticipantesAsistentes().size();
        }
        
        var promedioAsistencia = calcularPromedioAsistencia();
        
        StringBuilder estadisticas = new StringBuilder();
        estadisticas.append("=======================================================\n");
        estadisticas.append("         ESTADÍSTICAS GENERALES DEL SISTEMA             \n");
        estadisticas.append("=======================================================\n\n");
        
        estadisticas.append(" RESUMEN GENERAL\n");
        estadisticas.append("=======================================================\n");
        estadisticas.append(String.format("Total de Eventos: %d\n", todosEventos.size()));
        estadisticas.append(String.format("Total de Participantes: %d\n", todosParticipantes.size()));
        estadisticas.append(String.format("Total de Organizadores: %d\n\n", todosOrganizadores.size()));
        
        estadisticas.append(" EVENTOS POR ESTADO\n");
        estadisticas.append("=======================================================\n");
        estadisticas.append(String.format("  Publicados: %d\n", eventosPublicados));
        estadisticas.append(String.format("  En Curso: %d\n", eventosEnCurso));
        estadisticas.append(String.format("  Finalizados: %d\n", eventosFinalizados));
        estadisticas.append(String.format("  Cancelados: %d\n\n", eventosCancelados));
        
        estadisticas.append(" EVENTOS POR TIPO\n");
        estadisticas.append("=======================================================\n");
        for (Map.Entry<TipoEvento, Integer> entry : eventosPorTipo.entrySet()) {
            estadisticas.append(String.format("  %s: %d\n", entry.getKey().getDescripcion(), entry.getValue()));
        }
        estadisticas.append("\n");
        
        estadisticas.append(" ESTADÍSTICAS DE PARTICIPACION\n");
        estadisticas.append("=======================================================\n");
        estadisticas.append(String.format("Total Registrados: %d\n", totalRegistrados));
        estadisticas.append(String.format("Total Asistentes: %d\n", totalAsistentes));
        estadisticas.append(String.format("Promedio de Asistencia: %.2f%%\n\n", promedioAsistencia));
        
        return estadisticas.toString();
    }
    
    /**
     * Compara dos eventos y genera un reporte comparativo
     * @param eventoId1 ID del primer evento
     * @param eventoId2 ID del segundo evento
     * @return String con el reporte comparativo formateado
     * @throws EventPlannerException si alguno de los eventos no se encuentra
     */
    public String compararEventos(String eventoId1, String eventoId2) throws EventPlannerException {
        try {
            Evento evento1 = gestor.buscarEvento(eventoId1);
            Evento evento2 = gestor.buscarEvento(eventoId2);
            
            StringBuilder comparacion = new StringBuilder();
            comparacion.append("=======================================================\n");
            comparacion.append("               COMPARACIÓN DE EVENTOS                  \n");
            comparacion.append("=======================================================\n\n");
            
            comparacion.append(String.format(" Evento 1: %s\n", evento1.getNombre()));
            comparacion.append(String.format("   Tipo: %s\n", evento1.getTipo().getDescripcion()));
            comparacion.append(String.format("   Registrados: %d\n", evento1.getParticipantesRegistrados().size()));
            comparacion.append(String.format("   Asistentes: %d\n", evento1.getParticipantesAsistentes().size()));
            comparacion.append(String.format("   Asistencia: %.2f%%\n\n", evento1.calcularPorcentajeAsistencia()));
            
            comparacion.append(String.format(" Evento 2: %s\n", evento2.getNombre()));
            comparacion.append(String.format("   Tipo: %s\n", evento2.getTipo().getDescripcion()));
            comparacion.append(String.format("   Registrados: %d\n", evento2.getParticipantesRegistrados().size()));
            comparacion.append(String.format("   Asistentes: %d\n", evento2.getParticipantesAsistentes().size()));
            comparacion.append(String.format("   Asistencia: %.2f%%\n\n", evento2.calcularPorcentajeAsistencia()));
            
            comparacion.append(" GANADOR\n");
            comparacion.append("=======================================================\n");
            
            double asistencia1 = evento1.calcularPorcentajeAsistencia();
            double asistencia2 = evento2.calcularPorcentajeAsistencia();
            
            if (asistencia1 > asistencia2) {
                comparacion.append(String.format(" %s tuvo mejor asistencia\n", evento1.getNombre()));
                comparacion.append(String.format("   Diferencia: %.2f%%\n", asistencia1 - asistencia2));
            } else if (asistencia2 > asistencia1) {
                comparacion.append(String.format(" %s tuvo mejor asistencia\n", evento2.getNombre()));
                comparacion.append(String.format("   Diferencia: %.2f%%\n", asistencia2 - asistencia1));
            } else {
                comparacion.append(" Ambos eventos tuvieron la misma asistencia\n");
            }
            
            return comparacion.toString();
            
        } catch (EventoNoEncontradoException e) {
            throw new EventPlannerException("Error al comparar eventos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analiza tendencias en la participación de eventos
     * @return String con el análisis de tendencias formateado
     */
    public String analizarTendencias() {
        ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
        
        Map<TipoEvento, Integer> registradosPorTipo = new HashMap<>();
        Map<TipoEvento, Integer> eventosPorTipo = new HashMap<>();
        
        for (TipoEvento tipo : TipoEvento.values()) {
            registradosPorTipo.put(tipo, 0);
            eventosPorTipo.put(tipo, 0);
        }
        
        for (Evento evento : todosEventos) {
            TipoEvento tipo = evento.getTipo();
            eventosPorTipo.put(tipo, eventosPorTipo.get(tipo) + 1);
            registradosPorTipo.put(tipo, registradosPorTipo.get(tipo) + 
                                evento.getParticipantesRegistrados().size());
        }
        
        TipoEvento tipoMasPopular = null;
        var maxRegistrados = 0;
        
        for (Map.Entry<TipoEvento, Integer> entry : registradosPorTipo.entrySet()) {
            if (entry.getValue() > maxRegistrados) {
                maxRegistrados = entry.getValue();
                tipoMasPopular = entry.getKey();
            }
        }
        
        StringBuilder tendencias = new StringBuilder();
        tendencias.append("============================================================\n");
        tendencias.append("              ANÁLISIS DE TENDENCIAS                      \n");
        tendencias.append("============================================================\n\n");
        
        tendencias.append(" PARTICIPACIÓN POR TIPO DE EVENTO\n");
        tendencias.append("============================================================\n");
        
        for (TipoEvento tipo : TipoEvento.values()) {
            int eventos = eventosPorTipo.get(tipo);
            int registrados = registradosPorTipo.get(tipo);
            double promedio = eventos > 0 ? (double) registrados / eventos : 0;
            
            tendencias.append(String.format("%s:\n", tipo.getDescripcion()));
            tendencias.append(String.format("   Eventos: %d\n", eventos));
            tendencias.append(String.format("   Registrados: %d\n", registrados));
            tendencias.append(String.format("   Promedio: %.1f participantes/evento\n\n", promedio));
        }
        
        if (tipoMasPopular != null) {
            tendencias.append(" TIPO MAS POPULAR\n");
            tendencias.append("============================================================\n");
            tendencias.append(String.format("%s con %d registrados totales\n\n", 
                tipoMasPopular.getDescripcion(), maxRegistrados));
        }
        
        return tendencias.toString();
    }
    
    /**
     * Genera un reporte por organizador
     * @param organizadorId ID del organizador
     * @return String con el reporte formateado
     * @throws EventPlannerException si hay error
     */
    public String generarReportePorOrganizador(String organizadorId) throws EventPlannerException {
        ArrayList<Evento> eventosOrganizador = gestor.obtenerEventosDeOrganizador(organizadorId);
        
        var totalRegistrados = 0;
        var totalAsistentes = 0;
        var eventosActivos = 0;
        var eventosFinalizados = 0;
        
        for (Evento evento : eventosOrganizador) {
            totalRegistrados += evento.getParticipantesRegistrados().size();
            totalAsistentes += evento.getParticipantesAsistentes().size();
            
            if (evento.getEstado() == EstadoEvento.PUBLICADO || 
                evento.getEstado() == EstadoEvento.EN_CURSO) {
                eventosActivos++;
            } else if (evento.getEstado() == EstadoEvento.FINALIZADO) {
                eventosFinalizados++;
            }
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("============================================================\n");
        reporte.append("         REPORTE DE ORGANIZADOR                              \n");
        reporte.append("============================================================\n\n");
        
        reporte.append(" RESUMEN DE ACTIVIDAD\n");
        reporte.append("============================================================\n");
        reporte.append(String.format("Total de Eventos Creados: %d\n", eventosOrganizador.size()));
        reporte.append(String.format("Eventos Activos: %d\n", eventosActivos));
        reporte.append(String.format("Eventos Finalizados: %d\n", eventosFinalizados));
        reporte.append(String.format("Total de Registrados: %d\n", totalRegistrados));
        reporte.append(String.format("Total de Asistentes: %d\n\n", totalAsistentes));
        
        if (!eventosOrganizador.isEmpty()) {
            reporte.append(" EVENTOS\n");
            reporte.append("============================================================\n");
            
            for (Evento evento : eventosOrganizador) {
                reporte.append(String.format("• %s (%s)\n", 
                    evento.getNombre(), evento.getEstado().getDescripcion()));
                reporte.append(String.format("  Registrados: %d | Asistentes: %d | Asistencia: %.2f%%\n\n",
                    evento.getParticipantesRegistrados().size(),
                    evento.getParticipantesAsistentes().size(),
                    evento.calcularPorcentajeAsistencia()));
            }
        }
        
        return reporte.toString();
    }
    
    /**
     * Calcula el promedio de asistencia de todos los eventos
     * @return double con el promedio de asistencia
     */
    public double calcularPromedioAsistencia() {
        ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
        double sumaAsistencias = 0;
        var eventosConAsistencia = 0;
        
        for (Evento evento : todosEventos) {
            if (!evento.getParticipantesAsistentes().isEmpty()) {
                sumaAsistencias += evento.calcularPorcentajeAsistencia();
                eventosConAsistencia++;
            }
        }
        
        return eventosConAsistencia > 0 ? sumaAsistencias / eventosConAsistencia : 0;
    }
    
    /**
     * Obtiene el evento con mejor rating (más exitoso)
     * @return Evento con mejor porcentaje de asistencia
     */
    public Evento obtenerEventoMasExitoso() {
        ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
        Evento eventoMasExitoso = null;
        double mejorAsistencia = 0;
        
        for (Evento evento : todosEventos) {
            double asistencia = evento.calcularPorcentajeAsistencia();
            if (asistencia > mejorAsistencia) {
                mejorAsistencia = asistencia;
                eventoMasExitoso = evento;
            }
        }
        
        return eventoMasExitoso;
    }
    
    /**
     * Genera un resumen ejecutivo con indicadores clave
     * @return String con el resumen ejecutivo formateado
     * @throws EventPlannerException si hay un error al obtener los datos
     */
    public String generarResumenEjecutivo() throws EventPlannerException {
        Evento eventoMasExitoso = obtenerEventoMasExitoso();
        double promedioAsistencia = calcularPromedioAsistencia();
        
        StringBuilder resumen = new StringBuilder();
        resumen.append("============================================================\n");
        resumen.append("              RESUMEN EJECUTIVO                             \n");
        resumen.append("============================================================\n\n");
        
        resumen.append(generarEstadisticasGenerales());
        resumen.append("\n");
        
        if (eventoMasExitoso != null) {
            resumen.append(" EVENTO MÁS EXITOSO\n");
            resumen.append("============================================================\n");
            resumen.append(String.format("Nombre: %s\n", eventoMasExitoso.getNombre()));
            resumen.append(String.format("Tipo: %s\n", eventoMasExitoso.getTipo().getDescripcion()));
            resumen.append(String.format("Asistencia: %.2f%%\n\n", 
                eventoMasExitoso.calcularPorcentajeAsistencia()));
        }
        
        resumen.append(" INDICADORES CLAVE\n");
        resumen.append("============================================================\n");
        resumen.append(String.format("Promedio General de Asistencia: %.2f%%\n", promedioAsistencia));
        
        if (promedioAsistencia >= 75) {
            resumen.append(" Rendimiento: EXCELENTE\n");
        } else if (promedioAsistencia >= 60) {
            resumen.append(" Rendimiento: BUENO\n");
        } else if (promedioAsistencia >= 40) {
            resumen.append(" Rendimiento: REGULAR\n");
        } else {
            resumen.append(" Rendimiento: NECESITA MEJORAR\n");
        }
        
        return resumen.toString();
    }
}