package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.*;
import modelos.*;
import presentacion.NavigationHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Controlador para Reportes Financieros.
 * Maneja análisis de ingresos, comisiones y estadísticas de pagos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class ReporteFinancieroController {
    
    @FXML private Label lblIngresosTotales;
    @FXML private Label lblComisionPlataforma;
    @FXML private Label lblIngresosOrganizadores;
    @FXML private Label lblTotalTransacciones;
    @FXML private TextArea txtResultados;
    
    private GestorEventos gestorEventos;
    private GestorPagos gestorPagos;
    private Stage stage;
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        gestorPagos = GestorPagos.getInstance();
        
        cargarResumenGeneral();
        
        txtResultados.setText("Seleccione una opción para generar un reporte financiero...\n\n" +
                             "💵 Ingresos por Evento - Detalles financieros de un evento\n" +
                             "💳 Ingresos por Método de Pago - Distribución de pagos\n" +
                             "🏆 Ranking de Eventos - Top eventos por ingresos\n" +
                             "📊 Estadísticas de Comisiones - Análisis de comisiones");
        
        System.out.println("[ReporteFinanciero] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Carga el resumen general de finanzas
     */
    private void cargarResumenGeneral() {
        // Ingresos totales
        double ingresosTotales = gestorPagos.calcularIngresosTotales();
        lblIngresosTotales.setText(String.format("$%,.0f", ingresosTotales));
        
        // Comisión plataforma
        double comisionPlataforma = gestorPagos.calcularGananciasPlataforma();
        lblComisionPlataforma.setText(String.format("$%,.0f", comisionPlataforma));
        
        // Ingresos organizadores (total - comisión)
        double ingresosOrganizadores = ingresosTotales - comisionPlataforma;
        lblIngresosOrganizadores.setText(String.format("$%,.0f", ingresosOrganizadores));
        
        // Total transacciones
        ArrayList<Pago> pagos = gestorPagos.obtenerTodosPagos();
        int totalAprobados = 0;
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                totalAprobados++;
            }
        }
        lblTotalTransacciones.setText(String.valueOf(totalAprobados));
    }
    
    /**
     * Genera reporte de ingresos por evento
     */
    @FXML
    private void reporteIngresosPorEvento() {
        System.out.println("[ReporteFinanciero] Generando reporte de ingresos por evento...");
        
        Evento evento = seleccionarEvento("Seleccione el evento para ver sus ingresos");
        if (evento == null) return;
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=======================================================\n");
        reporte.append("        REPORTE DE INGRESOS POR EVENTO                  \n");
        reporte.append("=======================================================\n\n");
        
        reporte.append(" INFORMACIÓN DEL EVENTO\n");
        reporte.append("=======================================================\n");
        reporte.append(String.format("Nombre: %s\n", evento.getNombre()));
        reporte.append(String.format("Tipo: %s\n", evento.getTipo().getDescripcion()));
        reporte.append(String.format("Estado: %s\n\n", evento.getEstado().getDescripcion()));
        
        // Obtener pagos del evento
        ArrayList<Pago> pagosEvento = gestorPagos.obtenerPagosDeEvento(evento.getId());
        
        int totalPagos = pagosEvento.size();
        int pagosAprobados = 0;
        int pagosRechazados = 0;
        int pagosPendientes = 0;
        int pagosCancelados =0;
        int pagosReembolsados =0;
        double totalIngresos = 0;
        double totalComision = 0;
        
        Map<TipoTicket, Integer> ticketsPorTipo = new HashMap<>();
        for (TipoTicket tipo : TipoTicket.values()) {
            ticketsPorTipo.put(tipo, 0);
        }
        
        for (Pago pago : pagosEvento) {
            switch (pago.getEstado()) {
                case APROBADO -> {
                    pagosAprobados++;
                    totalIngresos += pago.getMontoBase();
                    totalComision += pago.calcularGananciaPlataforma();
                }
                case RECHAZADO -> pagosRechazados++;
                case PENDIENTE -> pagosPendientes++;
                case CANCELADO -> pagosCancelados++;
                case REEMBOLSADO -> pagosReembolsados++;
            }
        }
        
        // Obtener tickets para contar por tipo
        ArrayList<Ticket> ticketsEvento = gestorPagos.obtenerTicketsDeEvento(evento.getId());
        for (Ticket ticket : ticketsEvento) {
            TipoTicket tipo = ticket.getTipo();
            ticketsPorTipo.put(tipo, ticketsPorTipo.get(tipo) + 1);
        }
        
        double ingresosOrganizador = totalIngresos - totalComision;
        
        reporte.append(" RESUMEN FINANCIERO\n");
        reporte.append("=======================================================\n");
        reporte.append(String.format("Ingresos Brutos: $%,.0f\n", totalIngresos));
        reporte.append(String.format("Comisión Plataforma (5%%): $%,.0f\n", totalComision));
        reporte.append(String.format("Ingresos Netos Organizador: $%,.0f\n\n", ingresosOrganizador));
        
        reporte.append(" ESTADÍSTICAS DE PAGOS\n");
        reporte.append("=======================================================\n");
        reporte.append(String.format("Total de Transacciones: %d\n", totalPagos));
        reporte.append(String.format("  Aprobadas: %d\n", pagosAprobados));
        reporte.append(String.format("  Rechazadas: %d\n", pagosRechazados));
        reporte.append(String.format("  Pendientes: %d\n\n", pagosPendientes));
        
        if (pagosAprobados > 0) {
            double promedioTicket = totalIngresos / pagosAprobados;
            reporte.append(String.format("Precio Promedio por Ticket: $%,.0f\n\n", promedioTicket));
        }
        
        reporte.append(" TICKETS VENDIDOS POR TIPO\n");
        reporte.append("=======================================================\n");
        for (Map.Entry<TipoTicket, Integer> entry : ticketsPorTipo.entrySet()) {
            if (entry.getValue() > 0) {
                reporte.append(String.format("%s: %d tickets\n", 
                    entry.getKey().getDescripcion(), entry.getValue()));
            }
        }
        
        txtResultados.setText(reporte.toString());
        
        mostrarInformacion("Reporte Generado", 
            String.format("Reporte financiero generado para: %s\nIngresos: $%,.0f", 
            evento.getNombre(), totalIngresos));
    }
    
    /**
     * Genera reporte de ingresos por método de pago
     */
    @FXML
    private void reporteIngresosPorMetodoPago() {
        System.out.println("[ReporteFinanciero] Generando reporte por método de pago...");
        
        ArrayList<Pago> todosPagos = gestorPagos.obtenerTodosPagos();
        
        Map<MetodoPago, Double> ingresosPorMetodo = new HashMap<>();
        Map<MetodoPago, Integer> cantidadPorMetodo = new HashMap<>();
        
        // Inicializar
        for (MetodoPago metodo : MetodoPago.values()) {
            ingresosPorMetodo.put(metodo, 0.0);
            cantidadPorMetodo.put(metodo, 0);
        }
        
        double totalIngresos = 0;
        int totalTransacciones = 0;
        
        // Procesar pagos
        for (Pago pago : todosPagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                MetodoPago metodo = pago.getMetodoPago();
                double montoActual = ingresosPorMetodo.get(metodo);
                int cantidadActual = cantidadPorMetodo.get(metodo);
                
                ingresosPorMetodo.put(metodo, montoActual + pago.getMontoBase());
                cantidadPorMetodo.put(metodo, cantidadActual + 1);
                
                totalIngresos += pago.getMontoBase();
                totalTransacciones++;
            }
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=======================================================\n");
        reporte.append("     REPORTE DE INGRESOS POR MÉTODO DE PAGO           \n");
        reporte.append("=======================================================\n\n");
        
        reporte.append(" RESUMEN GENERAL\n");
        reporte.append("=======================================================\n");
        reporte.append(String.format("Total de Ingresos: $%,.0f\n", totalIngresos));
        reporte.append(String.format("Total de Transacciones: %d\n\n", totalTransacciones));
        
        reporte.append(" DISTRIBUCIÓN POR MÉTODO DE PAGO\n");
        reporte.append("=======================================================\n");
        
        for (MetodoPago metodo : MetodoPago.values()) {
            double ingresos = ingresosPorMetodo.get(metodo);
            int cantidad = cantidadPorMetodo.get(metodo);
            
            if (cantidad > 0) {
                double porcentaje = (ingresos / totalIngresos) * 100;
                double promedio = ingresos / cantidad;
                
                reporte.append(String.format("\n%s:\n", metodo.getDescripcion()));
                reporte.append(String.format("  Ingresos: $%,.0f (%.1f%%)\n", ingresos, porcentaje));
                reporte.append(String.format("  Transacciones: %d\n", cantidad));
                reporte.append(String.format("  Promedio: $%,.0f\n", promedio));
            }
        }
        
        txtResultados.setText(reporte.toString());
        
        mostrarInformacion("Reporte Generado", 
            String.format("Distribución de $%,.0f en %d transacciones", 
            totalIngresos, totalTransacciones));
    }
    
    /**
     * Genera ranking de eventos por ingresos
     */
    @FXML
    private void reporteRankingEventos() {
        System.out.println("[ReporteFinanciero] Generando ranking de eventos...");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        // Crear lista de eventos con sus ingresos
        List<EventoIngresos> ranking = new ArrayList<>();
        
        for (Evento evento : eventos) {
            double ingresos = gestorPagos.calcularIngresosEvento(evento.getId());
            if (ingresos > 0) {
                ranking.add(new EventoIngresos(evento, ingresos));
            }
        }
        
        // Ordenar por ingresos descendente
        ranking.sort((e1, e2) -> Double.compare(e2.ingresos, e1.ingresos));
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=======================================================\n");
        reporte.append("         RANKING DE EVENTOS POR INGRESOS               \n");
        reporte.append("=======================================================\n\n");
        
        if (ranking.isEmpty()) {
            reporte.append("No hay eventos con ventas registradas.\n");
        } else {
            double totalIngresos = 0;
            for (EventoIngresos ei : ranking) {
                totalIngresos += ei.ingresos;
            }
            
            reporte.append(String.format(" TOTAL DE INGRESOS: $%,.0f\n", totalIngresos));
            reporte.append(String.format(" EVENTOS CON VENTAS: %d\n\n", ranking.size()));
            
            reporte.append(" TOP EVENTOS\n");
            reporte.append("=======================================================\n\n");
            
            int posicion = 1;
            for (EventoIngresos ei : ranking) {
                double porcentaje = (ei.ingresos / totalIngresos) * 100;
                int tickets = gestorPagos.obtenerTicketsDeEvento(ei.evento.getId()).size();
                
                String medalla = "";
                if (posicion == 1) medalla = "🥇";
                else if (posicion == 2) medalla = "🥈";
                else if (posicion == 3) medalla = "🥉";
                else medalla = String.format("#%d", posicion);
                
                reporte.append(String.format("%s %s\n", medalla, ei.evento.getNombre()));
                reporte.append(String.format("   Tipo: %s\n", ei.evento.getTipo().getDescripcion()));
                reporte.append(String.format("   Ingresos: $%,.0f (%.1f%%)\n", ei.ingresos, porcentaje));
                reporte.append(String.format("   Tickets vendidos: %d\n", tickets));
                reporte.append(String.format("   Precio promedio: $%,.0f\n\n", tickets > 0 ? ei.ingresos / tickets : 0));
                
                posicion++;
            }
        }
        
        txtResultados.setText(reporte.toString());
        
        mostrarInformacion("Ranking Generado", 
            String.format("Se encontraron %d eventos con ventas", ranking.size()));
    }
    
    /**
     * Genera estadísticas de comisiones
     */
    @FXML
    private void reporteEstadisticasComisiones() {
        System.out.println("[ReporteFinanciero] Generando estadísticas de comisiones...");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        double totalIngresos = 0;
        double totalComisiones = 0;
        double totalOrganizadores = 0;
        int eventosConVentas = 0;
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=======================================================\n");
        reporte.append("        ESTADÍSTICAS DE COMISIONES                     \n");
        reporte.append("=======================================================\n\n");
        
        reporte.append(" DETALLE POR EVENTO\n");
        reporte.append("=======================================================\n\n");
        
        for (Evento evento : eventos) {
            double ingresos = gestorPagos.calcularIngresosEvento(evento.getId());
            
            if (ingresos > 0) {
                eventosConVentas++;
                double comision = gestorPagos.calcularGananciasPlataformaEvento(evento.getId());
                double ingresosOrg = ingresos - comision;
                
                totalIngresos += ingresos;
                totalComisiones += comision;
                totalOrganizadores += ingresosOrg;
                
                reporte.append(String.format("%s\n", evento.getNombre()));
                reporte.append(String.format("  Ingresos Brutos: $%,.0f\n", ingresos));
                reporte.append(String.format("  Comisión (5%%): $%,.0f\n", comision));
                reporte.append(String.format("  Ingresos Netos: $%,.0f\n\n", ingresosOrg));
            }
        }
        
        reporte.append(" RESUMEN GENERAL\n");
        reporte.append("=======================================================\n");
        reporte.append(String.format("Eventos con ventas: %d\n\n", eventosConVentas));
        reporte.append(String.format("Total Ingresos Brutos: $%,.0f\n", totalIngresos));
        reporte.append(String.format("Total Comisiones Plataforma: $%,.0f (%.1f%%)\n", 
            totalComisiones, (totalComisiones / totalIngresos) * 100));
        reporte.append(String.format("Total Ingresos Organizadores: $%,.0f (%.1f%%)\n\n", 
            totalOrganizadores, (totalOrganizadores / totalIngresos) * 100));
        
        if (eventosConVentas > 0) {
            reporte.append(String.format("Comisión promedio por evento: $%,.0f\n", 
                totalComisiones / eventosConVentas));
            reporte.append(String.format("Ingresos promedio por evento: $%,.0f\n", 
                totalIngresos / eventosConVentas));
        }
        
        txtResultados.setText(reporte.toString());
        
        mostrarInformacion("Estadísticas Generadas", 
            String.format("Comisiones totales: $%,.0f", totalComisiones));
    }
    
    /**
     * Actualiza los datos del resumen general
     */
    @FXML
    private void actualizarDatos() {
        cargarResumenGeneral();
        mostrarInformacion("Datos Actualizados", "El resumen financiero se ha actualizado");
    }
    
    /**
     * Guarda el reporte en un archivo
     */
    @FXML
    private void guardarReporte() {
        String contenido = txtResultados.getText();
        
        if (contenido.trim().isEmpty() || contenido.contains("Seleccione una opción")) {
            mostrarAdvertencia("Sin Contenido", 
                "No hay ningún reporte para guardar. Genere un reporte primero.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Financiero");
        fileChooser.setInitialFileName("reporte_financiero.txt");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo de Texto", "*.txt")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contenido);
                mostrarInformacion("Guardado Exitoso", 
                    "El reporte se ha guardado en:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                mostrarError("Error al Guardar", 
                    "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }
    
    /**
     * Limpia el área de resultados
     */
    @FXML
    private void limpiarResultados() {
        txtResultados.clear();
        txtResultados.setText("Seleccione una opción para generar un reporte financiero...\n\n" +
                             "💵 Ingresos por Evento - Detalles financieros de un evento\n" +
                             "💳 Ingresos por Método de Pago - Distribución de pagos\n" +
                             "🏆 Ranking de Eventos - Top eventos por ingresos\n" +
                             "📊 Estadísticas de Comisiones - Análisis de comisiones");
    }
    
    /**
     * Selecciona un evento
     */
    private Evento seleccionarEvento(String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SeleccionarEvento.fxml"));
            javafx.scene.Parent root = loader.load();
            
            SeleccionarEventoController controller = loader.getController();
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(titulo);
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            dialogStage.setScene(new javafx.scene.Scene(root));
            
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isConfirmado()) {
                return controller.getEventoSeleccionado();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el selector de eventos");
        }
        
        return null;
    }
    
    /**
     * Vuelve al menú de reportes
     */
    @FXML
    private void volver() {
        if (stage == null) {
            mostrarError("Error", "No se puede volver al menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuReportes.fxml"));
            Parent root = loader.load();
            
            Scene scene = new javafx.scene.Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);

            stage.setTitle("Reportes y Estadísticas - Event Planner");
            
            
            MenuReportesController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // ==================== CLASE AUXILIAR ====================
    
    /**
     * Clase auxiliar para almacenar evento con sus ingresos
     */
    private static class EventoIngresos {
        Evento evento;
        double ingresos;
        
        EventoIngresos(Evento evento, double ingresos) {
            this.evento = evento;
            this.ingresos = ingresos;
        }
    }
}