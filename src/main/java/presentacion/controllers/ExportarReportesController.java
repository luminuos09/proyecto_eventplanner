package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.*;
import modelos.*;
import presentacion.NavigationHelper;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controlador para Exportar Reportes.
 * Permite generar y descargar reportes en diferentes formatos (TXT, CSV, PDF).
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class ExportarReportesController {
    
    @FXML private VBox btnReporteEventos;
    @FXML private VBox btnReporteAsistencia;
    @FXML private VBox btnReporteFinanciero;
    @FXML private VBox btnReporteParticipantes;
    
    @FXML private Label lblTipoSeleccionado;
    @FXML private Label lblEstado;
    @FXML private ProgressBar progressBar;
    
    private Stage stage;
    private GestorEventos gestorEventos;
    private GestorPagos gestorPagos;
    
    private String tipoReporteSeleccionado = null;
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        gestorPagos = GestorPagos.getInstance();
        
        System.out.println("[ExportarReportes] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // ==================== SELECCIÓN DE TIPO DE REPORTE ====================
    
    /**
     * Selecciona reporte de eventos
     */
    @FXML
    private void seleccionarReporteEventos() {
        limpiarSeleccionVisual();
        tipoReporteSeleccionado = "EVENTOS";
        btnReporteEventos.setStyle("-fx-background-color: #1976D2; -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;");
        lblTipoSeleccionado.setText("Tipo seleccionado: REPORTE DE EVENTOS");
        lblTipoSeleccionado.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblEstado.setText("Reporte de eventos seleccionado. Elija un formato de exportacion.");
        System.out.println("[ExportarReportes] Tipo seleccionado: EVENTOS");
    }
    
    /**
     * Selecciona reporte de asistencia
     */
    @FXML
    private void seleccionarReporteAsistencia() {
        limpiarSeleccionVisual();
        tipoReporteSeleccionado = "ASISTENCIA";
        btnReporteAsistencia.setStyle("-fx-background-color: #1976D2; -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;");
        lblTipoSeleccionado.setText("Tipo seleccionado: REPORTE DE ASISTENCIA");
        lblTipoSeleccionado.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblEstado.setText("Reporte de asistencia seleccionado. Elija un formato de exportacion.");
        System.out.println("[ExportarReportes] Tipo seleccionado: ASISTENCIA");
    }
    
    /**
     * Selecciona reporte financiero
     */
    @FXML
    private void seleccionarReporteFinanciero() {
        limpiarSeleccionVisual();
        tipoReporteSeleccionado = "FINANCIERO";
        btnReporteFinanciero.setStyle("-fx-background-color: #1976D2; -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;");
        lblTipoSeleccionado.setText("Tipo seleccionado: REPORTE FINANCIERO");
        lblTipoSeleccionado.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblEstado.setText("Reporte financiero seleccionado. Elija un formato de exportacion.");
        System.out.println("[ExportarReportes] Tipo seleccionado: FINANCIERO");
    }
    
    /**
     * Selecciona reporte de participantes
     */
    @FXML
    private void seleccionarReporteParticipantes() {
        limpiarSeleccionVisual();
        tipoReporteSeleccionado = "PARTICIPANTES";
        btnReporteParticipantes.setStyle("-fx-background-color: #1976D2; -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;");
        lblTipoSeleccionado.setText("Tipo seleccionado: REPORTE DE PARTICIPANTES");
        lblTipoSeleccionado.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblEstado.setText("Reporte de participantes seleccionado. Elija un formato de exportacion.");
        System.out.println("[ExportarReportes] Tipo seleccionado: PARTICIPANTES");
    }
    
    /**
     * Limpia la selección visual
     */
    private void limpiarSeleccionVisual() {
        String estiloDefault = "-fx-background-color: #E3F2FD; -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;";
        btnReporteEventos.setStyle(estiloDefault);
        btnReporteAsistencia.setStyle(estiloDefault);
        btnReporteFinanciero.setStyle(estiloDefault);
        btnReporteParticipantes.setStyle(estiloDefault);
    }
    
    // ==================== EXPORTACIÓN ====================
    
    /**
     * Exporta a formato TXT
     */
    @FXML
    private void exportarTXT() {
        if (!validarSeleccion()) return;
        
        String contenido = generarContenidoReporte();
        if (contenido == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte TXT");
        fileChooser.setInitialFileName(generarNombreArchivo() + ".txt");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo de Texto", "*.txt")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                mostrarProgreso(true);
                FileWriter writer = new FileWriter(file);
                writer.write(contenido);
                writer.close();
                
                mostrarProgreso(false);
                lblEstado.setText("Reporte exportado exitosamente: " + file.getName());
                mostrarInformacion("Exportacion Exitosa", 
                    "El reporte se ha guardado en:\n" + file.getAbsolutePath());
                
                System.out.println("[ExportarReportes] Exportado TXT: " + file.getAbsolutePath());
                
            } catch (IOException e) {
                mostrarProgreso(false);
                lblEstado.setText("Error al exportar el archivo");
                mostrarError("Error al Exportar", 
                    "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }
    
    /**
     * Exporta a formato CSV
     */
    @FXML
    private void exportarCSV() {
        if (!validarSeleccion()) return;
        
        String contenido = generarContenidoCSV();
        if (contenido == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte CSV");
        fileChooser.setInitialFileName(generarNombreArchivo() + ".csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo CSV", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                mostrarProgreso(true);
                FileWriter writer = new FileWriter(file);
                writer.write(contenido);
                writer.close();
                
                mostrarProgreso(false);
                lblEstado.setText("Reporte CSV exportado: " + file.getName());
                mostrarInformacion("Exportacion Exitosa", 
                    "El reporte CSV se ha guardado en:\n" + file.getAbsolutePath() + 
                    "\n\nPuede abrirlo con Excel o cualquier hoja de calculo.");
                
                System.out.println("[ExportarReportes] Exportado CSV: " + file.getAbsolutePath());
                
            } catch (IOException e) {
                mostrarProgreso(false);
                lblEstado.setText("Error al exportar CSV");
                mostrarError("Error al Exportar", 
                    "No se pudo guardar el archivo CSV: " + e.getMessage());
            }
        }
    }
    
    /**
     * Exporta a formato PDF
     */
    @FXML
    private void exportarPDF() {
        if (!validarSeleccion()) return;
        
        mostrarAdvertencia("Funcion en Desarrollo", 
            "La exportacion a PDF esta en desarrollo.\n\n" +
            "Por ahora puede usar:\n" +
            "- Formato TXT para texto simple\n" +
            "- Formato CSV para abrir en Excel");
    }
    
    // ==================== GENERACIÓN DE CONTENIDO ====================
    
    /**
     * Genera el contenido del reporte según el tipo seleccionado
     */
    private String generarContenidoReporte() {
        switch (tipoReporteSeleccionado) {
            case "EVENTOS":
                return generarReporteEventos();
            case "ASISTENCIA":
                return generarReporteAsistencia();
            case "FINANCIERO":
                return generarReporteFinanciero();
            case "PARTICIPANTES":
                return generarReporteParticipantes();
            default:
                return null;
        }
    }
    
    /**
     * Genera contenido CSV según el tipo seleccionado
     */
    private String generarContenidoCSV() {
        switch (tipoReporteSeleccionado) {
            case "EVENTOS":
                return generarCSVEventos();
            case "ASISTENCIA":
                return generarCSVAsistencia();
            case "FINANCIERO":
                return generarCSVFinanciero();
            case "PARTICIPANTES":
                return generarCSVParticipantes();
            default:
                return null;
        }
    }
    
    /**
     * Genera reporte de eventos en formato texto
     */
    private String generarReporteEventos() {
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("=======================================================\n");
        reporte.append("           REPORTE COMPLETO DE EVENTOS                 \n");
        reporte.append("=======================================================\n");
        reporte.append("Fecha de generacion: ").append(obtenerFechaHora()).append("\n\n");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        reporte.append("TOTAL DE EVENTOS: ").append(eventos.size()).append("\n\n");
        
        // Agrupar por estado
        Map<EstadoEvento, List<Evento>> eventosPorEstado = new HashMap<>();
        for (EstadoEvento estado : EstadoEvento.values()) {
            eventosPorEstado.put(estado, new ArrayList<>());
        }
        
        for (Evento evento : eventos) {
            eventosPorEstado.get(evento.getEstado()).add(evento);
        }
        
        // Mostrar por estado
        for (EstadoEvento estado : EstadoEvento.values()) {
            List<Evento> eventosEstado = eventosPorEstado.get(estado);
            if (!eventosEstado.isEmpty()) {
                reporte.append("\n").append(estado.getDescripcion().toUpperCase()).append(" (")
                       .append(eventosEstado.size()).append(")\n");
                reporte.append("-------------------------------------------------------\n");
                
                for (Evento evento : eventosEstado) {
                    reporte.append(String.format("- %s\n", evento.getNombre()));
                    reporte.append(String.format("  Tipo: %s | Capacidad: %d | Inscritos: %d\n", 
                        evento.getTipo().getDescripcion(), 
                        evento.getCapacidadMaxima(),
                        evento.getParticipantesRegistrados().size()));
                    reporte.append(String.format("  Ubicacion: %s\n", evento.getUbicacion()));
                    reporte.append(String.format("  Fecha: %s\n\n", 
                        evento.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
                }
            }
        }
        
        return reporte.toString();
    }
    
    /**
     * Genera CSV de eventos
     */
    private String generarCSVEventos() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nombre,Tipo,Estado,Capacidad,Inscritos,Ubicacion,Fecha Inicio,Fecha Fin\n");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        for (Evento evento : eventos) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,\"%s\",\"%s\",\"%s\"\n",
                evento.getId(),
                evento.getNombre(),
                evento.getTipo().getDescripcion(),
                evento.getEstado().getDescripcion(),
                evento.getCapacidadMaxima(),
                evento.getParticipantesRegistrados().size(),
                evento.getUbicacion(),
                evento.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                evento.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));
        }
        
        return csv.toString();
    }
    
    /**
     * Genera reporte de asistencia
     */
    private String generarReporteAsistencia() {
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("=======================================================\n");
        reporte.append("           REPORTE DE ASISTENCIA                       \n");
        reporte.append("=======================================================\n");
        reporte.append("Fecha de generacion: ").append(obtenerFechaHora()).append("\n\n");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        for (Evento evento : eventos) {
            int inscritos = evento.getParticipantesRegistrados().size();
            int asistentes = evento.getParticipantesAsistentes().size();
            
            if (inscritos > 0) {
                double porcentaje = ((double) asistentes / inscritos) * 100;
                
                reporte.append(String.format("%s\n", evento.getNombre()));
                reporte.append(String.format("  Inscritos: %d\n", inscritos));
                reporte.append(String.format("  Asistentes: %d\n", asistentes));
                reporte.append(String.format("  Asistencia: %.1f%%\n\n", porcentaje));
            }
        }
        
        return reporte.toString();
    }
    
    /**
     * Genera CSV de asistencia
     */
    private String generarCSVAsistencia() {
        StringBuilder csv = new StringBuilder();
        csv.append("Evento,Tipo,Fecha,Inscritos,Asistentes,Porcentaje Asistencia\n");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        for (Evento evento : eventos) {
            int inscritos = evento.getParticipantesRegistrados().size();
            int asistentes = evento.getParticipantesAsistentes().size();
            double porcentaje = inscritos > 0 ? ((double) asistentes / inscritos) * 100 : 0;
            
            csv.append(String.format("\"%s\",\"%s\",\"%s\",%d,%d,%.1f%%\n",
                evento.getNombre(),
                evento.getTipo().getDescripcion(),
                evento.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                inscritos,
                asistentes,
                porcentaje
            ));
        }
        
        return csv.toString();
    }
    
    /**
     * Genera reporte financiero
     */
    private String generarReporteFinanciero() {
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("=======================================================\n");
        reporte.append("           REPORTE FINANCIERO COMPLETO                 \n");
        reporte.append("=======================================================\n");
        reporte.append("Fecha de generacion: ").append(obtenerFechaHora()).append("\n\n");
        
        double totalIngresos = gestorPagos.calcularIngresosTotales();
        double totalComisiones = gestorPagos.calcularGananciasPlataforma();
        
        reporte.append(String.format("INGRESOS TOTALES: $%,.0f\n", totalIngresos));
        reporte.append(String.format("COMISIONES PLATAFORMA (5%%): $%,.0f\n", totalComisiones));
        reporte.append(String.format("INGRESOS ORGANIZADORES: $%,.0f\n\n", totalIngresos - totalComisiones));
        
        reporte.append("DETALLE POR EVENTO:\n");
        reporte.append("-------------------------------------------------------\n");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        for (Evento evento : eventos) {
            double ingresos = gestorPagos.calcularIngresosEvento(evento.getId());
            if (ingresos > 0) {
                double comision = gestorPagos.calcularGananciasPlataformaEvento(evento.getId());
                reporte.append(String.format("\n%s\n", evento.getNombre()));
                reporte.append(String.format("  Ingresos Brutos: $%,.0f\n", ingresos));
                reporte.append(String.format("  Comision: $%,.0f\n", comision));
                reporte.append(String.format("  Ingresos Netos: $%,.0f\n", ingresos - comision));
            }
        }
        
        return reporte.toString();
    }
    
    /**
     * Genera CSV financiero
     */
    private String generarCSVFinanciero() {
        StringBuilder csv = new StringBuilder();
        csv.append("Evento,Ingresos Brutos,Comision 5%,Ingresos Netos,Tickets Vendidos\n");
        
        ArrayList<Evento> eventos = gestorEventos.obtenerTodosEventos();
        
        for (Evento evento : eventos) {
            double ingresos = gestorPagos.calcularIngresosEvento(evento.getId());
            if (ingresos > 0) {
                double comision = gestorPagos.calcularGananciasPlataformaEvento(evento.getId());
                int tickets = gestorPagos.obtenerTicketsDeEvento(evento.getId()).size();
                
                csv.append(String.format("\"%s\",%.2f,%.2f,%.2f,%d\n",
                    evento.getNombre(),
                    ingresos,
                    comision,
                    ingresos - comision,
                    tickets
                ));
            }
        }
        
        return csv.toString();
    }
    
    /**
     * Genera reporte de participantes
     */
    private String generarReporteParticipantes() {
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("=======================================================\n");
        reporte.append("           LISTADO DE PARTICIPANTES                    \n");
        reporte.append("=======================================================\n");
        reporte.append("Fecha de generacion: ").append(obtenerFechaHora()).append("\n\n");
        
        ArrayList<Participante> participantes = gestorEventos.obtenerTodosParticipantes();
        
        reporte.append("TOTAL DE PARTICIPANTES REGISTRADOS: ").append(participantes.size()).append("\n\n");
        
        for (Participante p : participantes) {
            reporte.append(String.format("- %s\n", p.getNombre()));
            reporte.append(String.format("  Email: %s\n", p.getEmail()));
            reporte.append(String.format("  Telefono: %s\n", p.getTelefono()));
            reporte.append(String.format("  Empresa: %s\n", p.getEmpresa()));
            reporte.append(String.format("  VIP: %s\n", p.isVip() ? "Si" : "No"));
            reporte.append(String.format("  Eventos registrados: %d\n\n", 
                p.getEventosRegistrados().size()));
        }
        
        return reporte.toString();
    }
    
    /**
     * Genera CSV de participantes
     */
    private String generarCSVParticipantes() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nombre,Email,Telefono,Empresa,Cargo,VIP,Eventos Registrados\n");
        
        ArrayList<Participante> participantes = gestorEventos.obtenerTodosParticipantes();
        
        for (Participante p : participantes) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d\n",
                p.getId(),
                p.getNombre(),
                p.getEmail(),
                p.getTelefono(),
                p.getEmpresa(),
                p.getCargo(),
                p.isVip() ? "Si" : "No",
                p.getEventosRegistrados().size()
            ));
        }
        
        return csv.toString();
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Valida que se haya seleccionado un tipo de reporte
     */
    private boolean validarSeleccion() {
        if (tipoReporteSeleccionado == null) {
            mostrarAdvertencia("Seleccion Requerida", 
                "Por favor seleccione primero un tipo de reporte.");
            return false;
        }
        return true;
    }
    
    /**
     * Genera nombre de archivo
     */
    private String generarNombreArchivo() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "reporte_" + tipoReporteSeleccionado.toLowerCase() + "_" + fecha;
    }
    
    /**
     * Obtiene fecha y hora actual formateada
     */
    private String obtenerFechaHora() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    
    /**
     * Muestra/oculta barra de progreso
     */
    private void mostrarProgreso(boolean mostrar) {
        progressBar.setVisible(mostrar);
        if (mostrar) {
            lblEstado.setText("Generando y exportando reporte...");
        }
    }
    
    /**
     * Limpia la selección
     */
    @FXML
    private void limpiarSeleccion() {
        tipoReporteSeleccionado = null;
        limpiarSeleccionVisual();
        lblTipoSeleccionado.setText("Seleccione un tipo de reporte");
        lblTipoSeleccionado.setStyle("-fx-text-fill: #666666; -fx-font-size: 13px; -fx-font-style: italic;");
        lblEstado.setText("Listo para exportar. Seleccione un tipo de reporte y un formato.");
        progressBar.setVisible(false);
        System.out.println("[ExportarReportes] Seleccion limpiada");
    }
    
    /**
     * Vuelve al menú de reportes
     */
    @FXML
    private void volver() {
        if (stage == null) {
            mostrarError("Error", "No se puede volver al menu");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuReportes.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setTitle("Reportes y Estadisticas - Event Planner");
            NavigationHelper.aplicarCSS(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            
            MenuReportesController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menu");
        }
    }
    
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
}