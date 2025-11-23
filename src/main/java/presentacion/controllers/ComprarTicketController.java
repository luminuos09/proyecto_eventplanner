package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.GestorEventos;
import logica.GestorPagos;
import modelos.*;
import presentacion.NavigationHelper;
import excepciones.*;
import java.util.ArrayList;

/**
 * Controlador para la compra de tickets.
 * Gestiona la selección de evento, tipo de ticket, método de pago y procesamiento.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class ComprarTicketController {
    
    // ==================== COMPONENTES FXML ====================
    
    @FXML private ComboBox<String> cboParticipante;
    @FXML private ComboBox<String> cboEvento;
    @FXML private ComboBox<TipoTicket> cboTipoTicket;
    @FXML private ComboBox<MetodoPago> cboMetodoPago;
    
    @FXML private javafx.scene.layout.VBox panelInfoEvento;
    @FXML private javafx.scene.layout.VBox panelInfoTicket;
    @FXML private javafx.scene.layout.VBox panelResumen;
    
    @FXML private Label lblEventoNombre;
    @FXML private Label lblEventoFecha;
    @FXML private Label lblEventoUbicacion;
    @FXML private Label lblEventoCupos;
    
    @FXML private Label lblTicketTipo;
    @FXML private Label lblTicketBeneficios;
    @FXML private Label lblTicketPrecio;
    
    @FXML private Label lblResumenPrecio;
    @FXML private Label lblResumenComision;
    @FXML private Label lblResumenTotal;
    
    // ==================== ATRIBUTOS ====================
    
    private GestorEventos gestorEventos;
    private GestorPagos gestorPagos;
    private Stage stage;
    
    private ArrayList<Participante> participantes;
    private ArrayList<Evento> eventos;
    private Evento eventoSeleccionado;
    private Participante participanteSeleccionado;
    
    // ==================== INICIALIZACIÓN ====================
    
    @FXML
    public void initialize() {
        gestorEventos = GestorEventos.getInstance();
        
        cargarParticipantes();
        cargarEventosDisponibles();
        cargarTiposTicket();
        cargarMetodosPago();
        
        configurarListeners();
        
        System.out.println("[ComprarTicket] Controlador inicializado");
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
    }
    
    /**
     * Carga los participantes disponibles
     */
    private void cargarParticipantes() {
        participantes = gestorEventos.obtenerTodosParticipantes();
        
        for (Participante p : participantes) {
            String item = String.format("%s - %s%s",
                p.getNombre(),
                p.getEmail(),
                p.isVip() ? " ⭐ VIP" : ""
            );
            cboParticipante.getItems().add(item);
        }
        
        System.out.println("[ComprarTicket] Participantes cargados: " + participantes.size());
    }
    
    /**
     * Carga solo los eventos publicados con cupos disponibles
     */
    private void cargarEventosDisponibles() {
        eventos = new ArrayList<>();
        
        for (Evento e : gestorEventos.obtenerTodosEventos()) {
            if (e.getEstado() == EstadoEvento.PUBLICADO && e.tieneCupoDisponible()) {
                eventos.add(e);
                
                String item = String.format("%s - %s (%d cupos)",
                    e.getNombre(),
                    e.getFechaInicio().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    e.getCuposDisponibles()
                );
                cboEvento.getItems().add(item);
            }
        }
        
        System.out.println("[ComprarTicket] Eventos disponibles: " + eventos.size());
    }
    
    /**
     * Carga los tipos de ticket disponibles
     */
    private void cargarTiposTicket() {
        cboTipoTicket.getItems().addAll(TipoTicket.values());
        
        cboTipoTicket.setCellFactory(param -> new ListCell<TipoTicket>() {
            @Override
            protected void updateItem(TipoTicket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion() + " - $" + String.format("%,.0f", item.getPrecioBase()));
                }
            }
        });
        
        cboTipoTicket.setButtonCell(new ListCell<TipoTicket>() {
            @Override
            protected void updateItem(TipoTicket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion() + " - $" + String.format("%,.0f", item.getPrecioBase()));
                }
            }
        });
    }
    
    /**
     * Carga los métodos de pago disponibles
     */
    private void cargarMetodosPago() {
        cboMetodoPago.getItems().addAll(MetodoPago.values());
        
        cboMetodoPago.setCellFactory(param -> new ListCell<MetodoPago>() {
            @Override
            protected void updateItem(MetodoPago item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion() + " (+" + String.format("%.1f%%", item.getComision() * 100) + ")");
                }
            }
        });
        
        cboMetodoPago.setButtonCell(new ListCell<MetodoPago>() {
            @Override
            protected void updateItem(MetodoPago item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion() + " (+" + String.format("%.1f%%", item.getComision() * 100) + ")");
                }
            }
        });
    }
    
    /**
     * Configura los listeners para los ComboBox
     */
    private void configurarListeners() {
        cboParticipante.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                participanteSeleccionado = participantes.get(newVal.intValue());
                actualizarResumen();
            }
        });
        
        cboEvento.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                eventoSeleccionado = eventos.get(newVal.intValue());
                mostrarInfoEvento();
                actualizarResumen();
            }
        });
        
        cboTipoTicket.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarInfoTicket(newVal);
                actualizarResumen();
            }
        });
        
        cboMetodoPago.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            actualizarResumen();
        });
    }
    
    /**
     * Muestra la información del evento seleccionado
     */
    private void mostrarInfoEvento() {
        if (eventoSeleccionado == null) return;
        
        lblEventoNombre.setText(eventoSeleccionado.getNombre());
        lblEventoFecha.setText(eventoSeleccionado.getFechaInicio().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblEventoUbicacion.setText(eventoSeleccionado.getUbicacion());
        lblEventoCupos.setText(String.valueOf(eventoSeleccionado.getCuposDisponibles()));
        
        panelInfoEvento.setVisible(true);
        panelInfoEvento.setManaged(true);
    }
    
    /**
     * Muestra la información del tipo de ticket seleccionado
     */
    private void mostrarInfoTicket(TipoTicket tipo) {
        lblTicketTipo.setText(tipo.getDescripcion());
        lblTicketBeneficios.setText(tipo.getBeneficios());
        
        double precio = tipo.getPrecioBase();
        if (participanteSeleccionado != null && participanteSeleccionado.isVip() && tipo != TipoTicket.GRATUITO) {
            precio = tipo.getPrecioConDescuentoVIP();
            lblTicketPrecio.setText("$" + String.format("%,.0f", precio) + " ⭐ (Descuento VIP 20%)");
        } else {
            lblTicketPrecio.setText("$" + String.format("%,.0f", precio));
        }
        
        panelInfoTicket.setVisible(true);
        panelInfoTicket.setManaged(true);
    }
    
    /**
     * Actualiza el resumen del pago
     */
    private void actualizarResumen() {
        TipoTicket tipo = cboTipoTicket.getValue();
        MetodoPago metodo = cboMetodoPago.getValue();
        
        if (tipo == null || metodo == null || participanteSeleccionado == null) {
            panelResumen.setVisible(false);
            panelResumen.setManaged(false);
            return;
        }
        
        double precioBase = tipo.getPrecioBase();
        if (participanteSeleccionado.isVip() && tipo != TipoTicket.GRATUITO) {
            precioBase = tipo.getPrecioConDescuentoVIP();
        }
        
        double comision = metodo.calcularComision(precioBase);
        double total = precioBase + comision;
        
        lblResumenPrecio.setText("$" + String.format("%,.0f", precioBase));
        lblResumenComision.setText("$" + String.format("%,.0f", comision));
        lblResumenTotal.setText("$" + String.format("%,.0f", total));
        
        panelResumen.setVisible(true);
        panelResumen.setManaged(true);
    }
    
    // ==================== ACCIONES ====================
    
    /**
     * Procesa la compra del ticket
     */
    @FXML
    private void procesarCompra() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            Ticket ticket = gestorPagos.comprarTicket(
                eventoSeleccionado.getId(),
                participanteSeleccionado.getId(),
                cboTipoTicket.getValue(),
                cboMetodoPago.getValue()
            );
            
            mostrarExito("¡Compra Exitosa!", 
                "Tu ticket ha sido procesado correctamente.\n\n" +
                "ID Ticket: " + ticket.getId() + "\n" +
                "Código QR: " + ticket.generarCodigoQR() + "\n\n" +
                "Revisa 'Mis Tickets' para más detalles.");
            
            limpiarFormulario();
            
        } catch (EventPlannerException e) {
            mostrarError("Error en la compra", e.getMessage());
        }
    }
    
    /**
     * Valida los campos del formulario
     */
    private boolean validarCampos() {
        if (cboParticipante.getValue() == null) {
            mostrarAdvertencia("Campo vacío", "Debe seleccionar un participante");
            return false;
        }
        
        if (cboEvento.getValue() == null) {
            mostrarAdvertencia("Campo vacío", "Debe seleccionar un evento");
            return false;
        }
        
        if (cboTipoTicket.getValue() == null) {
            mostrarAdvertencia("Campo vacío", "Debe seleccionar un tipo de ticket");
            return false;
        }
        
        if (cboMetodoPago.getValue() == null) {
            mostrarAdvertencia("Campo vacío", "Debe seleccionar un método de pago");
            return false;
        }
        
        return true;
    }
    
    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        cboParticipante.setValue(null);
        cboEvento.setValue(null);
        cboTipoTicket.setValue(null);
        cboMetodoPago.setValue(null);
        
        panelInfoEvento.setVisible(false);
        panelInfoEvento.setManaged(false);
        panelInfoTicket.setVisible(false);
        panelInfoTicket.setManaged(false);
        panelResumen.setVisible(false);
        panelResumen.setManaged(false);
    }
    
    /**
     * Cancela y vuelve al menú de pagos
     */
    @FXML
    private void cancelar() {
        if (stage == null) {
            mostrarError("Error", "No se puede volver al menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuPagos.fxml"));
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
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ==================== MENSAJES ====================
    
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
    
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("✓ Operación exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}