package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.*;
import modelos.*;
import presentacion.NavigationHelper;

import java.util.Map;

import excepciones.EventPlannerException;

/**
 * Controlador del Dashboard de Reportes.
 * Muestra estadísticas y métricas principales del sistema.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class DashboardController {
    
    @FXML private Label lblTotalEventos;
    @FXML private Label lblTotalParticipantes;
    @FXML private Label lblIngresosTotales;
    @FXML private Label lblTicketsVendidos;
    
    @FXML private Label lblEventoPopularNombre;
    @FXML private Label lblEventoPopularInfo;
    @FXML private Label lblEventoRentableNombre;
    @FXML private Label lblEventoRentableInfo;
    
    @FXML private TextArea txtEventosPorEstado;
    @FXML private Label lblPromedioParticipantes;
    @FXML private Label lblTasaOcupacion;
    @FXML private Label lblTotalOrganizadores;
    
    private GenerarReportes generadorReportes;
    private GestorEventos gestorEventos;
    private GestorPagos gestorPagos;
    private Stage stage;
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        gestorPagos = GestorPagos.getInstance();
        generadorReportes = new GenerarReportes(gestorEventos, gestorPagos);
        
        cargarDatos();
        
        System.out.println("[Dashboard] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
        this.generadorReportes = new GenerarReportes(gestorEventos, gestorPagos);
    }
    
    /**
     * Carga todos los datos del dashboard
     */
    private void cargarDatos() {
        cargarMetricasPrincipales();
        cargarEventosDestacados();
        cargarEstadisticasAdicionales();
    }
    
    /**
     * Carga las métricas principales (cards superiores)
     */
    private void cargarMetricasPrincipales() {
        lblTotalEventos.setText(String.valueOf(generadorReportes.obtenerTotalEventos()));
        lblTotalParticipantes.setText(String.valueOf(generadorReportes.obtenerTotalParticipantes()));
        lblIngresosTotales.setText("$" + String.format("%,.0f", generadorReportes.calcularIngresosTotales()));
        lblTicketsVendidos.setText(String.valueOf(generadorReportes.obtenerTotalTicketsVendidos()));
    }
    
    /**
     * Carga información de eventos destacados
     */
    private void cargarEventosDestacados() {
        // Evento más popular
        Evento masPopular = generadorReportes.obtenerEventoMasPopular();
        if (masPopular != null) {
            lblEventoPopularNombre.setText(masPopular.getNombre());
            lblEventoPopularInfo.setText(
                String.format("📍 %s\n👥 %d participantes\n📅 %s",
                    masPopular.getUbicacion(),
                    masPopular.getParticipantesRegistrados().size(),
                    masPopular.getFechaInicio().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
            );
        } else {
            lblEventoPopularNombre.setText("Sin eventos registrados");
            lblEventoPopularInfo.setText("");
        }
        
        // Evento más rentable
        Evento masRentable = generadorReportes.obtenerEventoMasRentable();
        if (masRentable != null) {
            lblEventoRentableNombre.setText(masRentable.getNombre());
            lblEventoRentableInfo.setText(
                String.format("📍 %s\n🎫 %d tickets vendidos\n📅 %s",
                    masRentable.getUbicacion(),
                    masRentable.getParticipantesRegistrados().size(),
                    masRentable.getFechaInicio().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
            );
        } else {
            lblEventoRentableNombre.setText("Sin eventos con ventas");
            lblEventoRentableInfo.setText("");
        }
    }
    
    /**
     * Carga estadísticas adicionales
     */
    private void cargarEstadisticasAdicionales() {
        // Eventos por estado
        Map<EstadoEvento, Integer> eventosPorEstado = generadorReportes.contarEventosPorEstado();
        StringBuilder sb = new StringBuilder();
        
        for (Map.Entry<EstadoEvento, Integer> entry : eventosPorEstado.entrySet()) {
            sb.append(String.format("%s: %d eventos\n", 
                entry.getKey().getDescripcion(), 
                entry.getValue()));
        }
        
        txtEventosPorEstado.setText(sb.toString());
        
        // Métricas adicionales
        lblPromedioParticipantes.setText(
            String.format("%.1f", generadorReportes.calcularPromedioParticipantesPorEvento())
        );
        
        lblTasaOcupacion.setText(
            String.format("%.1f%%", generadorReportes.calcularTasaOcupacionPromedio())
        );
        
        lblTotalOrganizadores.setText(
            String.valueOf(generadorReportes.obtenerTotalOrganizadores())
        );
    }
    
    /**
     * Actualiza todos los datos del dashboard
     */
    @FXML
    private void actualizarDashboard() {
        System.out.println("[Dashboard] Actualizando datos...");
        cargarDatos();
        mostrarInformacion("Dashboard actualizado", "Los datos se han actualizado correctamente.");
    }
    
    /**
     * Muestra el resumen ejecutivo en un diálogo
     * @throws EventPlannerException 
     */
    @FXML
    private void verResumenEjecutivo() throws EventPlannerException {
        String resumen = generadorReportes.generarResumenEjecutivo();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resumen Ejecutivo");
        alert.setHeaderText("📊 Resumen Ejecutivo del Sistema");
        
        TextArea textArea = new TextArea(resumen);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
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
            
            MenuReportesController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
                controller.setGestorPagos(gestorPagos);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Reportes y Estadísticas - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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