/**
 * Controlador para el formulario de creación de eventos.
 * Gestiona la entrada de datos, validaciones y comunicación con la lógica de negocio.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.Organizador;
import modelos.TipoEvento;
import excepciones.EventPlannerException;
import javafx.scene.Scene;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import presentacion.NavigationHelper;


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
    @FXML private ComboBox<String> cboOrganizador;
    @FXML private Label lblMensaje;
    
    // ========== ATRIBUTOS ==========
    
    /** Gestor de eventos para operaciones de negocio */
    private GestorEventos gestor;
    private Stage stage;
    private ArrayList<Organizador> organizadoresDisponibles;
    
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
        
        // Cargar organizadores disponibles
        cargarOrganizadores();
    }

    /**
     * Carga los organizadores disponibles en el ComboBox,
     * ordenados por experiencia (de mayor a menor)
     */
    private void cargarOrganizadores() {
        try {
            // Obtener todos los organizadores
            organizadoresDisponibles = gestor.obtenerTodosOrganizadores();
            
            if (organizadoresDisponibles.isEmpty()) {
                cboOrganizador.setPromptText("¡No hay organizadores registrados!");
                cboOrganizador.setDisable(true);
                mostrarMensajeError("*No hay organizadores registrados*");
                return;
            }
            
            // Ordenar por experiencia (de mayor a menor)
            organizadoresDisponibles.sort((o1, o2) -> 
                Integer.compare(o2.getExperienciaAnios(), o1.getExperienciaAnios())
            );
            
            // Agregar al ComboBox con formato legible
            for (Organizador org : organizadoresDisponibles) {
                String item = String.format("%s (%d años) - %s",
                    org.getNombre(),
                    org.getExperienciaAnios(),
                    org.getEmail()
                );
                cboOrganizador.getItems().add(item);
            }
            
            System.out.println("[FormularioEvento] Organizadores cargados: " + 
                organizadoresDisponibles.size());
            
        } catch (Exception e) {
            System.err.println("[FormularioEvento] Error al cargar organizadores: " + 
                e.getMessage());
            cboOrganizador.setPromptText("⚠️ Error al cargar organizadores");
            cboOrganizador.setDisable(true);
        }
    }
       
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
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
            
            // Obtener organizador seleccionado
            String seleccion = cboOrganizador.getValue();
            if (seleccion == null || seleccion.isEmpty()) {
                mostrarMensajeError("Debe seleccionar un organizador para el evento");
                cboOrganizador.requestFocus();
                return;
            }
            
            // Extraer el email del organizador (está después del " - ")
            String emailOrganizador = seleccion.substring(seleccion.lastIndexOf(" - ") + 3).trim();
            
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
     * Cancela la operación y vuelve al menú principal
     */
    @FXML
    private void cancelar() {
        if (stage == null) {
            mostrarError("Error", "No se puede volver al menú");
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
            stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Event Planner - Sistema de Gestión de Eventos");
            
            System.out.println("[FormularioEvento] Cancelado - Volviendo al menú principal");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
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
        
        if (cboOrganizador.getValue() == null) {
            mostrarMensajeError("Debe seleccionar un organizador");
            cboOrganizador.requestFocus();
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
        cboOrganizador.setValue(null);
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
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}