package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import logica.GestorEventos;
import logica.GestorPagos;
import modelos.*;
import presentacion.NavigationHelper;

import java.util.ArrayList;

public class HistorialPagosController {
    
    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, String> colId;
    @FXML private TableColumn<Pago, String> colFecha;
    @FXML private TableColumn<Pago, String> colParticipante;
    @FXML private TableColumn<Pago, String> colEvento;
    @FXML private TableColumn<Pago, String> colMonto;
    @FXML private TableColumn<Pago, String> colMetodo;
    @FXML private TableColumn<Pago, String> colEstado;
    @FXML private TableColumn<Pago, String> colReferencia;
    
    @FXML private Label lblTotalPagos;
    @FXML private Label lblAprobados;
    @FXML private Label lblIngresos;
    
    private GestorEventos gestorEventos;
    private GestorPagos gestorPagos;
    private Stage stage;
    private ObservableList<Pago> listaPagos;
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        listaPagos = FXCollections.observableArrayList();
        
        configurarTabla();
        
        System.out.println("[HistorialPagos] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
        cargarPagos();
    }
    
    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        
        colFecha.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaCreacion().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        
        colParticipante.setCellValueFactory(cellData -> {
            try {
                Participante p = gestorEventos.buscarParticipante(cellData.getValue().getParticipanteId());
                return new javafx.beans.property.SimpleStringProperty(p.getNombre());
            } catch (Exception ex) {
                return new javafx.beans.property.SimpleStringProperty("Desconocido");
            }
        });
        
        colEvento.setCellValueFactory(cellData -> {
            try {
                Evento e = gestorEventos.buscarEvento(cellData.getValue().getEventoId());
                return new javafx.beans.property.SimpleStringProperty(e.getNombre());
            } catch (Exception ex) {
                return new javafx.beans.property.SimpleStringProperty("Evento eliminado");
            }
        });
        
        colMonto.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                "$" + String.format("%,.0f", cellData.getValue().getMontoTotal())));
        
        colMetodo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMetodoPago().getDescripcion()));
        
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstado().getNombre()));
        
        colReferencia.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNumeroReferencia()));
        
        tablaPagos.setItems(listaPagos);
    }
    
    private void cargarPagos() {
        listaPagos.clear();
        
        if (gestorPagos != null) {
            ArrayList<Pago> pagos = gestorPagos.obtenerTodosPagos();
            listaPagos.addAll(pagos);
            
            actualizarEstadisticas(pagos);
            
            System.out.println("[HistorialPagos] Pagos cargados: " + pagos.size());
        }
    }
    
    private void actualizarEstadisticas(ArrayList<Pago> pagos) {
        int total = pagos.size();
        int aprobados = 0;
        double ingresos = 0.0;
        
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                aprobados++;
                ingresos += pago.getMontoBase();
            }
        }
        
        lblTotalPagos.setText(String.valueOf(total));
        lblAprobados.setText(String.valueOf(aprobados));
        lblIngresos.setText("$" + String.format("%,.0f", ingresos));
    }
    
    @FXML
    private void actualizarLista() {
        cargarPagos();
    }
    
    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPagos.fxml"));
            Parent root = loader.load();
            
            MenuPagosController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
                controller.setGestorPagos(gestorPagos);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Sistema de Pagos - Event Planner");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}