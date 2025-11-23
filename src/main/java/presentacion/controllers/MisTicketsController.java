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

public class MisTicketsController {
    
    @FXML private ComboBox<String> cboParticipante;
    @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colId;
    @FXML private TableColumn<Ticket, String> colEvento;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, String> colPrecio;
    @FXML private TableColumn<Ticket, String> colFechaCompra;
    @FXML private TableColumn<Ticket, String> colEstado;
    @FXML private TableColumn<Ticket, String> colQR;
    
    private GestorEventos gestorEventos;
    private GestorPagos gestorPagos;
    private Stage stage;
    private ArrayList<Participante> participantes;
    private ObservableList<Ticket> listaTickets;
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        listaTickets = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarParticipantes();
        
        System.out.println("[MisTickets] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
    }
    
    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        
        colEvento.setCellValueFactory(cellData -> {
            try {
                Evento e = gestorEventos.buscarEvento(cellData.getValue().getEventoId());
                return new javafx.beans.property.SimpleStringProperty(e.getNombre());
            } catch (Exception ex) {
                return new javafx.beans.property.SimpleStringProperty("Evento eliminado");
            }
        });
        
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo().getDescripcion()));
        
        colPrecio.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("$" + String.format("%,.0f", cellData.getValue().getPrecio())));
        
        colFechaCompra.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaCompra().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isUsado() ? "✓ Usado" : "Vigente"));
        
        colQR.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().generarCodigoQR()));
        
        tablaTickets.setItems(listaTickets);
    }
    
    private void cargarParticipantes() {
        participantes = gestorEventos.obtenerTodosParticipantes();
        
        for (Participante p : participantes) {
            cboParticipante.getItems().add(p.getNombre() + " - " + p.getEmail());
        }
        
        cboParticipante.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                cargarTickets(participantes.get(newVal.intValue()).getId());
            }
        });
    }
    
    private void cargarTickets(String participanteId) {
        listaTickets.clear();
        
        if (gestorPagos != null) {
            ArrayList<Ticket> tickets = gestorPagos.obtenerTicketsDeParticipante(participanteId);
            listaTickets.addAll(tickets);
            System.out.println("[MisTickets] Tickets cargados: " + tickets.size());
        }
    }
    
    @FXML
    private void actualizarLista() {
        if (cboParticipante.getSelectionModel().getSelectedIndex() >= 0) {
            String participanteId = participantes.get(
                cboParticipante.getSelectionModel().getSelectedIndex()).getId();
            cargarTickets(participanteId);
        }
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
            this.stage.setTitle("Sistema de Pagos - Event Planner");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}