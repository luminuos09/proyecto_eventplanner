package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logica.GestorEventos;
import modelos.Participante;
import modelos.Evento;
import modelos.EstadoEvento;
import excepciones.*;
import presentacion.NavigationHelper;

import java.util.ArrayList;

/**
 * Controlador para inscribir participantes a eventos
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class InscripcionEventoController {
    
    @FXML private TextField txtEmailParticipante;
    @FXML private Label lblParticipante;
    @FXML private ComboBox<String> cmbEventos;
    @FXML private Label lblInfoEvento;
    @FXML private Label lblMensaje;
    
    private GestorEventos gestor;
    private Stage stage;
    private Participante participanteSeleccionado;
    private ArrayList<Evento> eventosDisponibles;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.eventosDisponibles = new ArrayList<>();
         
        cargarEventosDisponibles();
        configurarComboBox();
        aplicarEstilos();
    }

    private void aplicarEstilos() {
    javafx.application.Platform.runLater(() -> {
        try {
            Scene scene = txtEmailParticipante.getScene();
            if (scene != null) {
                var cssUrl = getClass().getResource("/styles.css");
                if (cssUrl != null) {
                    String cssPath = cssUrl.toExternalForm();
                    if (!scene.getStylesheets().contains(cssPath)) {
                        scene.getStylesheets().add(cssPath);
                        System.out.println("CSS aplicado a InscripcionEvento");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error aplicando CSS: " + e.getMessage());
        }
    });
}
    
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Carga los eventos disponibles para inscripción
     */
    private void cargarEventosDisponibles() {
        try {
            ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
            eventosDisponibles.clear();
            
            // Solo eventos PUBLICADOS con cupos disponibles
            for (Evento evento : todosEventos) {
                if (evento.getEstado() == EstadoEvento.PUBLICADO && evento.tieneCupoDisponible()) {
                    eventosDisponibles.add(evento);
                }
            }
            
            System.out.println("" + eventosDisponibles.size() + " eventos disponibles para inscripción");
            
        } catch (Exception e) {
            System.err.println("Error al cargar eventos:");
            e.printStackTrace();
        }
    }
    
    /**
     * Configura el ComboBox de eventos
     */
    private void configurarComboBox() {
        ObservableList<String> nombresEventos = FXCollections.observableArrayList();
        
        for (Evento evento : eventosDisponibles) {
            String item = String.format("%s - %s (%d/%d cupos)",
                evento.getTipo().getIcono(),
                evento.getNombre(),
                evento.getParticipantesRegistrados().size(),
                evento.getCapacidadMaxima()
            );
            nombresEventos.add(item);
        }
        
        cmbEventos.setItems(nombresEventos);
        
        // Listener para mostrar info del evento seleccionado
        cmbEventos.setOnAction(e -> mostrarInfoEvento());
    }
    
    /**
     * Busca el participante por email
     */
    @FXML
    private void buscarParticipante() {
        String email = txtEmailParticipante.getText().trim();
        
        if (email.isEmpty()) {
            mostrarAdvertencia("Email requerido", "Por favor ingrese un email");
            return;
        }
        
        try {
            participanteSeleccionado = gestor.buscarParticipantePorEmail(email);
            
            lblParticipante.setText(String.format(
                "%s (%s) - %s",
                participanteSeleccionado.getNombre(),
                participanteSeleccionado.isVip() ? "VIP" : "Regular",
                participanteSeleccionado.getEmail()
            ));
            lblParticipante.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            
            System.out.println("✅Participante encontrado: " + participanteSeleccionado.getNombre());
            
        } catch (Exception e) {
            mostrarError("Error", "Error al buscar participante: " + e.getMessage());
        }
    }
    
    /**
     * Muestra información del evento seleccionado
     */
    private void mostrarInfoEvento() {
        int index = cmbEventos.getSelectionModel().getSelectedIndex();
        
        if (index < 0 || index >= eventosDisponibles.size()) {
            return;
        }
        
        Evento evento = eventosDisponibles.get(index);
        
        String info = String.format(
            "Fecha: %s\n" +
            "Ubicación: %s\n" +
            "Cupos: %d/%d disponibles\n" +
            "Entrada: %s",
            evento.getFechaInicio().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            evento.getUbicacion(),
            evento.getCuposDisponibles(),
            evento.getCapacidadMaxima(),
            "Gratuita" // Ajusta según tu lógica de tickets
        );
        
        lblInfoEvento.setText(info);
        lblInfoEvento.setVisible(true);
    }
    
    /**
     * Inscribe el participante al evento
     */
    @FXML
    private void inscribir() {
        // Validar participante
        if (participanteSeleccionado == null) {
            mostrarAdvertencia("Participante requerido", "Primero busque y seleccione un participante");
            return;
        }
        
        // Validar evento
        int indexEvento = cmbEventos.getSelectionModel().getSelectedIndex();
        if (indexEvento < 0) {
            mostrarAdvertencia("Evento requerido", "Por favor seleccione un evento");
            return;
        }
        
        Evento eventoSeleccionado = eventosDisponibles.get(indexEvento);
        
        try {
            // Inscribir
            gestor.inscribirParticipante(participanteSeleccionado.getId(), eventoSeleccionado.getId());
            
            mostrarExito(String.format(
                "¡Inscripción exitosa!\n\n" +
                "Participante: %s\n" +
                "Evento: %s\n" +
                "Fecha: %s\n\n" +
                "El participante ha sido inscrito correctamente.",
                participanteSeleccionado.getNombre(),
                eventoSeleccionado.getNombre(),
                eventoSeleccionado.getFechaInicio().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
            ));
            
            limpiar();
            cargarEventosDisponibles();
            configurarComboBox();
            
        } catch (CapacidadExcedidaException e) {
            mostrarError("Sin cupos", "El evento ya no tiene cupos disponibles");
        } catch (ParticipanteYaRegistradoException e) {
            mostrarAdvertencia("Ya inscrito", "El participante ya está inscrito en este evento");
        } catch (EventPlannerException e) {
            mostrarError("Error", "Error al inscribir: " + e.getMessage());
        }
    }
    
    /**
     * Limpia el formulario
     */
    @FXML
    private void limpiar() {
        txtEmailParticipante.clear();
        lblParticipante.setText("Ningún participante seleccionado");
        lblParticipante.setStyle("-fx-text-fill: #9d4edd; -fx-font-style: italic;");
        cmbEventos.getSelectionModel().clearSelection();
        lblInfoEvento.setText("");
        lblInfoEvento.setVisible(false);
        participanteSeleccionado = null;
    }
    
    /**
     * Vuelve al menú de participantes
     */
    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuParticipantes.fxml"));
            Parent root = loader.load();
            
            MenuParticipantesController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root);
            
            // APLICAR CSS
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);  
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            stage.setTitle("Gestión de Participantes - Event Planner");
            
            System.out.println("Vuelto a Menú de Participantes");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }


    
    // ============== MÉTODOS AUXILIARES ==============
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación exitosa");
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
}