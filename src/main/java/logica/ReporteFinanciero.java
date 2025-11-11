/**
 * ReporteFinanciero - Genera reportes financieros del sistema
 * Incluye análisis de ingresos, comisiones y estadísticas de ventas
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package logica;

import modelos.*;
import excepciones.*;
import java.util.ArrayList;
import java.util.Map;

public class ReporteFinanciero {
    
    private final GestorPagos gestorPagos;
    private final GestorEventos gestorEventos;
    
    public ReporteFinanciero(GestorPagos gestorPagos, GestorEventos gestorEventos) {
        this.gestorPagos = gestorPagos;
        this.gestorEventos = gestorEventos;
    }
    
    /**
     * Genera reporte financiero de un evento específico
     */
    public String generarReporteEvento(String eventoId) throws EventPlannerException {
        Evento evento = gestorEventos.buscarEvento(eventoId);
        ArrayList<Ticket> tickets = gestorPagos.obtenerTicketsDeEvento(eventoId);
        ArrayList<Pago> pagos = gestorPagos.obtenerPagosDeEvento(eventoId);
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("╔══════════════════════════════════════════════════════════╗\n");
        reporte.append("║          REPORTE FINANCIERO - EVENTO                     ║\n");
        reporte.append("╚══════════════════════════════════════════════════════════╝\n\n");
        
        reporte.append(" INFORMACIÓN DEL EVENTO\n");
        reporte.append("============================================================\n");
        reporte.append(String.format("Nombre: %s\n", evento.getNombre()));
        reporte.append(String.format("Tipo: %s\n", evento.getTipo().getDescripcion()));
        reporte.append(String.format("Estado: %s\n\n", evento.getEstado().getDescripcion()));
        
        reporte.append(" ESTADÍSTICAS DE VENTAS\n");
        reporte.append("============================================================\n");
        
        Map<TipoTicket, Integer> estadisticas = gestorPagos.obtenerEstadisticasVentas(eventoId);
        int totalTickets = 0;
        
        for (Map.Entry<TipoTicket, Integer> entry : estadisticas.entrySet()) {
            int cantidad = entry.getValue();
            totalTickets += cantidad;
            reporte.append(String.format("%-15s: %3d tickets\n", 
                entry.getKey().getDescripcion(), cantidad));
        }
        
        reporte.append(String.format("\nTotal Tickets Vendidos: %d\n\n", totalTickets));
        
        reporte.append(" RESUMEN FINANCIERO\n");
        reporte.append("============================================================\n");
        
        double ingresosBrutos = gestorPagos.calcularIngresosEvento(eventoId);
        double comisionPlataforma = gestorPagos.calcularGananciasPlataformaEvento(eventoId);
        double ingresosNetos = gestorPagos.calcularIngresosNetosOrganizador(eventoId);
        
        reporte.append(String.format("Ingresos Brutos:        $%,15.0f\n", ingresosBrutos));
        reporte.append(String.format("Comisión Plataforma:    $%,15.0f (5%%)\n", comisionPlataforma));
        reporte.append(String.format("Ingresos Netos:         $%,15.0f\n\n", ingresosNetos));
        
        reporte.append(" ANÁLISIS DE MÉTODOS DE PAGO\n");
        reporte.append("============================================================\n");
        
        Map<MetodoPago, Integer> metodosPago = new java.util.HashMap<>();
        Map<MetodoPago, Double> montoPorMetodo = new java.util.HashMap<>();
        
        for (MetodoPago metodo : MetodoPago.values()) {
            metodosPago.put(metodo, 0);
            montoPorMetodo.put(metodo, 0.0);
        }
        
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                MetodoPago metodo = pago.getMetodoPago();
                metodosPago.put(metodo, metodosPago.get(metodo) + 1);
                montoPorMetodo.put(metodo, montoPorMetodo.get(metodo) + pago.getMontoBase());
            }
        }
        
        for (Map.Entry<MetodoPago, Integer> entry : metodosPago.entrySet()) {
            if (entry.getValue() > 0) {
                reporte.append(String.format("%-20s: %2d transacciones - $%,.0f\n",
                    entry.getKey().getDescripcion(),
                    entry.getValue(),
                    montoPorMetodo.get(entry.getKey())));
            }
        }
        
        reporte.append("\n");
        
        return reporte.toString();
    }
    
    /**
     * Genera reporte financiero general del sistema
     */
    public String generarReporteGeneral() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("============================================================\n");
        reporte.append("        REPORTE FINANCIERO GENERAL DEL SISTEMA              \n");
        reporte.append("============================================================\n\n");
        
        ArrayList<Ticket> todosTickets = gestorPagos.obtenerTodosTickets();
        ArrayList<Pago> todosPagos = gestorPagos.obtenerTodosPagos();
        
        reporte.append(" RESUMEN DE TRANSACCIONES\n");
        reporte.append("============================================================\n");
        reporte.append(String.format("Total Tickets Emitidos: %d\n", todosTickets.size()));
        reporte.append(String.format("Total Pagos Procesados: %d\n\n", todosPagos.size()));
        
        int aprobados = 0;
        int rechazados = 0;
        int reembolsados = 0;
        
        for (Pago pago : todosPagos) {
            switch (pago.getEstado()) {
                case APROBADO -> aprobados++;
                case RECHAZADO -> rechazados++;
                case REEMBOLSADO -> reembolsados++;
                default -> throw new IllegalArgumentException("Unexpected value: " + pago.getEstado());
            }
        }
        
        reporte.append("Estado de Pagos:\n");
        reporte.append(String.format("  Aprobados:    %d (%.1f%%)\n", aprobados, 
            (aprobados * 100.0 / Math.max(1, todosPagos.size()))));
        reporte.append(String.format("  Rechazados:   %d (%.1f%%)\n", rechazados,
            (rechazados * 100.0 / Math.max(1, todosPagos.size()))));
        reporte.append(String.format("  Reembolsados: %d (%.1f%%)\n\n", reembolsados,
            (reembolsados * 100.0 / Math.max(1, todosPagos.size()))));
        
        reporte.append(" INGRESOS TOTALES\n");
        reporte.append("============================================================\n");
        
        double ingresosTotales = 0.0;
        double gananciaPlataforma = gestorPagos.calcularGananciasPlataforma();
        
        for (Pago pago : todosPagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                ingresosTotales += pago.getMontoBase();
            }
        }
        
        double ingresosOrganizadores = ingresosTotales - gananciaPlataforma;
        
        reporte.append(String.format("Ingresos Brutos Totales:   $%,15.0f\n", ingresosTotales));
        reporte.append(String.format("Ganancia Plataforma (5%%):  $%,15.0f\n", gananciaPlataforma));
        reporte.append(String.format("Ingresos Organizadores:    $%,15.0f\n\n", ingresosOrganizadores));
        
        reporte.append(" VENTAS POR TIPO DE TICKET\n");
        reporte.append("============================================================\n");
        
        Map<TipoTicket, Integer> ventasPorTipo = new java.util.HashMap<>();
        Map<TipoTicket, Double> ingresosPorTipo = new java.util.HashMap<>();
        
        for (TipoTicket tipo : TipoTicket.values()) {
            ventasPorTipo.put(tipo, 0);
            ingresosPorTipo.put(tipo, 0.0);
        }
        
        for (Ticket ticket : todosTickets) {
            TipoTicket tipo = ticket.getTipo();
            ventasPorTipo.put(tipo, ventasPorTipo.get(tipo) + 1);
            ingresosPorTipo.put(tipo, ingresosPorTipo.get(tipo) + ticket.getPrecio());
        }
        
        for (Map.Entry<TipoTicket, Integer> entry : ventasPorTipo.entrySet()) {
            reporte.append(String.format("%-15s: %3d tickets - $%,.0f\n",
                entry.getKey().getDescripcion(),
                entry.getValue(),
                ingresosPorTipo.get(entry.getKey())));
        }
        
        reporte.append("\n");
        
        return reporte.toString();
    }
    
    /**
     * Genera reporte de top eventos más rentables
     */
    public String generarTopEventosRentables(int limite) {
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        // Ordenar por ingresos
        eventos.sort((e1, e2) -> {
            double ing1 = gestorPagos.calcularIngresosEvento(e1.getId());
            double ing2 = gestorPagos.calcularIngresosEvento(e2.getId());
            return Double.compare(ing2, ing1);
        });
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("╔══════════════════════════════════════════════════════════╗\n");
        reporte.append("║          TOP EVENTOS MÁS RENTABLES                       ║\n");
        reporte.append("╚══════════════════════════════════════════════════════════╝\n\n");
        
        int contador = 0;
        for (Evento evento : eventos) {
            if (contador >= limite) break;
            
            double ingresos = gestorPagos.calcularIngresosEvento(evento.getId());
            if (ingresos > 0) {
                contador++;
                
                String medalla = contador == 1 ? "🥇" : contador == 2 ? "🥈" : contador == 3 ? "🥉" : "  ";
                
                reporte.append(String.format("%s %d. %s\n", medalla, contador, evento.getNombre()));
                reporte.append(String.format("      Tipo: %s\n", evento.getTipo().getDescripcion()));
                reporte.append(String.format("      Ingresos Brutos: $%,.0f\n", ingresos));
                reporte.append(String.format("      Tickets Vendidos: %d\n", 
                    gestorPagos.obtenerTicketsDeEvento(evento.getId()).size()));
                reporte.append(String.format("      Ingreso Promedio/Ticket: $%,.0f\n\n",
                    ingresos / Math.max(1, gestorPagos.obtenerTicketsDeEvento(evento.getId()).size())));
            }
        }
        
        if (contador == 0) {
            reporte.append("   No hay eventos con ventas registradas aún.\n");
        }
        
        return reporte.toString();
    }
}