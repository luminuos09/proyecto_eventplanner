/**
 * Controlador para el formulario de creación de eventos.
 * Gestiona la entrada de datos, validaciones y comunicación con la lógica de negocio.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */package presentacion.controllers;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.Organizador;
import modelos.TipoEvento;
import excepciones.EventPlannerException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class FormularioEventoController {
    
    // ========== COMPONENTES FXML ==========
    
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<TipoEvento> cboTipoEvento;
    @FXML private DatePicker dpFechaInicio;
    @FXML private TextField txtHoraInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtHoraFin;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtPrecioGeneral;
    @FXML private TextField txtPrecioVIP;
    @FXML private TextField txtEmailOrganizador;
    @FXML private Label lblMensaje;
    
    // ========== ATRIBUTOS ==========
    
    /** Gestor de eventos para operaciones de negocio */
    private GestorEventos gestor;
    
    /** Formateador para horas (HH:mm) */
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    
    // ========== INICIALIZACIÓN ==========
    
    /**
     * Inicializa el controlador después de que todos los componentes FXML
     * han sido cargados. Configura el ComboBox de tipos de evento.
     */
    @FXML
    private void initialize() {
        gestor = new GestorEventos();
        
        // Cargar tipos de evento en el ComboBox
        cboTipoEvento.getItems().addAll(TipoEvento.values());
        
        // Configurar formato de visualización
        cboTipoEvento.setCellFactory(param -> {
            return new ListCell<TipoEvento>() {
                @Override
                protected void updateItem(TipoEvento item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getIcono() + " " + item.getDescripcion());
                    }
                }
            };
        });
        
        cboTipoEvento.setButtonCell(new ListCell<TipoEvento>() {
            @Override
            protected void updateItem(TipoEvento item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIcono() + " " + item.getDescripcion());
                }
            }
        });
    }
    
    // ========== MÉTODOS DE ACCIÓN ==========
    
    /**
     * Maneja el evento de crear un nuevo evento.
     * Valida todos los campos y crea el evento en el sistema.
     */
    @FXML
    private void crearEvento() {
        try {
            // Limpiar mensaje anterior
            ocultarMensaje();
            
            // Validar campos vacíos
            if (!validarCamposObligatorios()) {
                return;
            }
            
            // Obtener y validar datos
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            TipoEvento tipo = cboTipoEvento.getValue();
            LocalDateTime fechaInicio = construirFechaHora(dpFechaInicio.getValue(), txtHoraInicio.getText());
            LocalDateTime fechaFin = construirFechaHora(dpFechaFin.getValue(), txtHoraFin.getText());
            String ubicacion = txtUbicacion.getText().trim();
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            double precioGeneral = parsearPrecio(txtPrecioGeneral.getText());
            double precioVIP = parsearPrecioVIP(txtPrecioVIP.getText(), precioGeneral);
            String emailOrganizador = txtEmailOrganizador.getText().trim();
            
            // Buscar organizador
            Organizador organizador = gestor.buscarOrganizadorPorEmail(emailOrganizador);
            
            // Crear evento
            var evento = gestor.crearEvento(nombre, descripcion, tipo,
                fechaInicio, fechaFin, ubicacion,
                capacidad, organizador);
            
            // Mostrar éxito
            mostrarMensajeExito("Evento creado exitosamente: " + evento.getNombre());
            
            // Limpiar formulario
            limpiarFormulario();
            
        } catch (NumberFormatException e) {
            mostrarMensajeError("Error: Verifique que la capacidad y precios sean números válidos");
        } catch (DateTimeParseException e) {
            mostrarMensajeError("Error: El formato de hora debe ser HH:mm (Ej: 10:00)");
        } catch (EventPlannerException e) {
            mostrarMensajeError("Error: " + e.getMessage());
        } catch (Exception e) {
            mostrarMensajeError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cancela la operación y cierra la ventana.
     */
    @FXML
    private void cancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar");
        alert.setHeaderText("¿Desea cancelar la creación del evento?");
        alert.setContentText("Los datos ingresados se perderán");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cerrarVentana();
            }
        });
    }
    
    // ========== MÉTODOS DE VALIDACIÓN ==========
    
    /**
     * Valida que todos los campos obligatorios estén completos.
     * 
     * @return true si todos los campos obligatorios están llenos, false en caso contrario
     */
    private boolean validarCamposObligatorios() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensajeError("El nombre del evento es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtDescripcion.getText().trim().isEmpty()) {
            mostrarMensajeError("La descripción es obligatoria");
            txtDescripcion.requestFocus();
            return false;
        }
        
        if (cboTipoEvento.getValue() == null) {
            mostrarMensajeError("Debe seleccionar un tipo de evento");
            cboTipoEvento.requestFocus();
            return false;
        }
        
        if (dpFechaInicio.getValue() == null || txtHoraInicio.getText().trim().isEmpty()) {
            mostrarMensajeError("La fecha y hora de inicio son obligatorias");
            dpFechaInicio.requestFocus();
            return false;
        }
        
        if (dpFechaFin.getValue() == null || txtHoraFin.getText().trim().isEmpty()) {
            mostrarMensajeError("La fecha y hora de fin son obligatorias");
            dpFechaFin.requestFocus();
            return false;
        }
        
        if (txtUbicacion.getText().trim().isEmpty()) {
            mostrarMensajeError("La ubicación es obligatoria");
            txtUbicacion.requestFocus();
            return false;
        }
        
        if (txtCapacidad.getText().trim().isEmpty()) {
            mostrarMensajeError("La capacidad es obligatoria");
            txtCapacidad.requestFocus();
            return false;
        }
        
        if (txtPrecioGeneral.getText().trim().isEmpty()) {
            mostrarMensajeError("El precio general es obligatorio (use 0 para eventos gratuitos)");
            txtPrecioGeneral.requestFocus();
            return false;
        }
        
        if (txtEmailOrganizador.getText().trim().isEmpty()) {
            mostrarMensajeError("El email del organizador es obligatorio");
            txtEmailOrganizador.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Construye un LocalDateTime a partir de una fecha y una hora en formato String.
     * 
     * @param fecha LocalDate con la fecha
     * @param horaStr String con la hora en formato HH:mm
     * @return LocalDateTime combinando fecha y hora
     * @throws DateTimeParseException si el formato de hora es inválido
     */
    private LocalDateTime construirFechaHora(LocalDate fecha, String horaStr) throws DateTimeParseException {
        LocalTime hora = LocalTime.parse(horaStr.trim(), FORMATO_HORA);
        return LocalDateTime.of(fecha, hora);
    }
    
    /**
     * Parsea un String a double para el precio.
     * 
     * @param precioStr String con el precio
     * @return double con el precio parseado
     * @throws NumberFormatException si el formato no es válido
     */
    private double parsearPrecio(String precioStr) throws NumberFormatException {
        String precio = precioStr.trim().replace(",", "").replace("$", "");
        return Double.parseDouble(precio);
    }
    
    /**
     * Parsea el precio VIP, usando el precio general si está vacío.
     * 
     * @param precioVIPStr String con el precio VIP
     * @param precioGeneral Precio general como fallback
     * @return double con el precio VIP
     */
    private double parsearPrecioVIP(String precioVIPStr, double precioGeneral) {
        if (precioVIPStr == null || precioVIPStr.trim().isEmpty()) {
            return precioGeneral;
        }
        return parsearPrecio(precioVIPStr);
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
        cboTipoEvento.setValue(null);
        dpFechaInicio.setValue(null);
        txtHoraInicio.clear();
        dpFechaFin.setValue(null);
        txtHoraFin.clear();
        txtUbicacion.clear();
        txtCapacidad.clear();
        txtPrecioGeneral.clear();
        txtPrecioVIP.clear();
        txtEmailOrganizador.clear();
        txtNombre.requestFocus();
    }
    
    /**
     * Muestra un mensaje de éxito al usuario.
     * 
     * @param mensaje Texto del mensaje a mostrar
     */
    private void mostrarMensajeExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        lblMensaje.setVisible(true);
    }
    
    /**
     * Muestra un mensaje de error al usuario.
     * 
     * @param mensaje Texto del mensaje a mostrar
     */
    private void mostrarMensajeError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        lblMensaje.setVisible(true);
    }
    
    /**
     * Oculta el label de mensajes.
     */
    private void ocultarMensaje() {
        lblMensaje.setVisible(false);
    }
    
    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
    /**
 * Muestra un mensaje de error
 * @param titulo Título del diálogo
 * @param mensaje Mensaje de error
 */
private void mostrarError(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}

/**
 * Muestra un mensaje informativo
 * @param titulo Título del diálogo
 * @param mensaje Mensaje informativo
 */
private void mostrarInfo(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}

/**
 * Muestra un mensaje de éxito
 * @param mensaje Mensaje de éxito
 */
private void mostrarExito(String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("✅ Operación exitosa");
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}
}