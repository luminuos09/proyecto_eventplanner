package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logica.GestorPagos;
import presentacion.NavigationHelper;

public class MenuReportesController {
    
    private Stage stage;
    private GestorPagos gestorPagos;
    
    @FXML
    public void initialize() {
        System.out.println("[MenuReportes] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
    }
    
    @FXML
    private void abrirDashboard() {
        System.out.println("[MenuReportes] Abriendo dashboard...");
        
        if (stage == null) {
            System.err.println("[MenuReportes] Error: Stage es null");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
                controller.setGestorPagos(gestorPagos);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Dashboard - Event Planner");
            
            System.out.println("[MenuReportes] Dashboard abierto");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[MenuReportes] Error al abrir dashboard: " + e.getMessage());
        }
    }
    
     @FXML
    private void abrirReportesEventos() {
        System.out.println("[MenuReportes] Abriendo reportes de eventos...");
        
        if (stage == null) {
            System.err.println("[MenuReportes] Error: Stage es null");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/ReporteEventos.fxml"));
            Parent root = loader.load();
            
            ReporteEventosController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Reportes de Eventos - Event Planner");
            
            System.out.println("[MenuReportes] Reportes de eventos abiertos");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[MenuReportes] Error al abrir reportes de eventos: " + e.getMessage());
        }
    }

        @FXML
    private void abrirReportesFinancieros() {
        System.out.println("[MenuReportes] Abriendo reportes financieros...");
        
        if (stage == null) {
            System.err.println("[MenuReportes] Error: Stage es null");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/ReporteFinanciero.fxml"));
            Parent root = loader.load();
            
            ReporteFinancieroController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Reporte financiero - Event Planner");

            System.out.println("[MenuReportes] Reportes financieros abiertos");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[MenuReportes] Error al abrir reportes financieros: " + e.getMessage());
        }
    }
    @FXML
private void abrirExportarReportes() {
    System.out.println("[MenuReportes] Abriendo exportar reportes...");
    
    if (stage == null) {
        System.err.println("[MenuReportes] Error: Stage es null");
        return;
    }
    
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/ExportarReportes.fxml"));
        Parent root = loader.load();
        
        ExportarReportesController controller = loader.getController();
        if (controller != null) {
            controller.setStage(stage);
        }
        
        Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
        stage.setTitle("Exportar Reportes - Event Planner");
        stage.setMaximized(true);
        
        System.out.println("[MenuReportes] Exportar reportes abierto");
        
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("[MenuReportes] Error al abrir exportar reportes: " + e.getMessage());
    }
}

    @FXML
    private void volverMenuPrincipal() {
        System.out.println("[MenuReportes] Volviendo al menú principal...");
        
        if (stage == null) {
            System.err.println("[MenuReportes] Error: Stage es null");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            
            MenuPrincipalController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Event Planner - Sistema de Gestión de Eventos");
            
            System.out.println("[MenuReportes] Vuelto al menú principal");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[MenuReportes] Error al volver: " + e.getMessage());
        }
    }
}