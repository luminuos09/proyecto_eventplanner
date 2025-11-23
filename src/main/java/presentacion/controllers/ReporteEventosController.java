package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logica.*;
import modelos.*;
import presentacion.NavigationHelper;
import excepciones.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controlador para la pantalla de Reportes de Eventos.
 * Permite generar diferentes tipos de reportes sobre eventos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class ReporteEventosController {
    
    @FXML private TextArea txtResultados;
    
    private GestorEventos gestorEventos;
    private GenerarReportes generarReportes;
    private GestorPagos gestorPagos;
    private Stage stage;
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        gestorPagos = GestorPagos.getInstance();
        generarReportes = new GenerarReportes(gestorEventos, gestorPagos);
        
        txtResultados.setText("Seleccione una opción para generar un reporte...\n\n" +
                             "📋 Reporte de Asistencia - Detalles de un evento específico\n" +
                             "⚖️ Comparar Eventos - Comparación entre dos eventos\n" +
                             "📈 Análisis de Tendencias - Tendencias por tipo de evento\n" +
                             "👤 Reporte por Organizador - Eventos de un organizador");
        
        System.out.println("[ReporteEventos] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Genera un reporte de asistencia para un evento
     */
    @FXML
    private void generarReporteAsistencia() {
        System.out.println("[ReporteEventos] Generando reporte de asistencia...");
        
        Evento evento = seleccionarEvento("Seleccione el evento para ver su reporte de asistencia");
        
        if (evento == null) {
            return;
        }
        
        try {
            String reporte = generarReportes.generarReporteAsistencia(evento.getId());
            txtResultados.setText(reporte);
            
            mostrarInformacion("Reporte Generado", 
                "Reporte de asistencia generado exitosamente para: " + evento.getNombre());
            
        } catch (EventPlannerException e) {
            mostrarError("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Compara dos eventos
     */
    @FXML
    private void compararEventos() {
        System.out.println("[ReporteEventos] Comparando eventos...");
        
        Evento evento1 = seleccionarEvento("Seleccione el PRIMER evento para comparar");
        if (evento1 == null) return;
        
        Evento evento2 = seleccionarEvento("Seleccione el SEGUNDO evento para comparar");
        if (evento2 == null) return;
        
        if (evento1.getId().equals(evento2.getId())) {
            mostrarAdvertencia("Advertencia", "Debe seleccionar dos eventos diferentes");
            return;
        }
        
        try {
            String reporte = generarReportes.compararEventos(evento1.getId(), evento2.getId());
            txtResultados.setText(reporte);
            
            mostrarInformacion("Comparación Generada", 
                "Comparación exitosa entre:\n• " + evento1.getNombre() + "\n• " + evento2.getNombre());
            
        } catch (EventPlannerException e) {
            mostrarError("Error", "No se pudo comparar los eventos: " + e.getMessage());
        }
    }
    
    /**
     * Analiza tendencias de participación
     */
    @FXML
    private void analizarTendencias() {
        System.out.println("[ReporteEventos] Analizando tendencias...");
        
        String reporte = generarReportes.analizarTendencias();
        txtResultados.setText(reporte);
        
        mostrarInformacion("Análisis Completado", 
            "Análisis de tendencias generado exitosamente");
    }
    
    /**
     * Genera reporte por organizador
     */
    @FXML
    private void reportePorOrganizador() {
        System.out.println("[ReporteEventos] Generando reporte por organizador...");
        
        Organizador organizador = seleccionarOrganizador();
        
        if (organizador == null) {
            return;
        }
        
        try {
            String reporte = generarReportes.generarReportePorOrganizador(organizador.getId());
            txtResultados.setText(reporte);
            
            mostrarInformacion("Reporte Generado", 
                "Reporte generado para el organizador: " + organizador.getNombre());
            
        } catch (EventPlannerException e) {
            mostrarError("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Abre el diálogo para seleccionar un evento
     */
    private Evento seleccionarEvento(String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SeleccionarEvento.fxml"));
            Parent root = loader.load();
            
            SeleccionarEventoController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(titulo);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            dialogStage.setScene(new Scene(root));
            
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
     * Permite seleccionar un organizador
     */
    private Organizador seleccionarOrganizador() {
        ArrayList<Organizador> organizadores = gestorEventos.obtenerTodosOrganizadores();
        
        if (organizadores.isEmpty()) {
            mostrarAdvertencia("Sin Organizadores", 
                "No hay organizadores registrados en el sistema");
            return null;
        }
        
        ChoiceDialog<Organizador> dialog = new ChoiceDialog<>(organizadores.get(0), organizadores);
        dialog.setTitle("Seleccionar Organizador");
        dialog.setHeaderText("Seleccione el organizador para ver su reporte");
        dialog.setContentText("Organizador:");
        
        Optional<Organizador> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    /**
     * Guarda el reporte actual en un archivo de texto
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
        fileChooser.setTitle("Guardar Reporte");
        fileChooser.setInitialFileName("reporte_evento.txt");
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
        txtResultados.setText("Seleccione una opción para generar un reporte...\n\n" +
                             "📋 Reporte de Asistencia - Detalles de un evento específico\n" +
                             "⚖️ Comparar Eventos - Comparación entre dos eventos\n" +
                             "📈 Análisis de Tendencias - Tendencias por tipo de evento\n" +
                             "👤 Reporte por Organizador - Eventos de un organizador");
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
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);   
            
            stage.setTitle("Reportes y Estadísticas - Event Planner");
            
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
}