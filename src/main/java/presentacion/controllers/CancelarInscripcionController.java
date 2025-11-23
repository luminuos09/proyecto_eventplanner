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
import modelos.Participante;
import modelos.Evento;

import java.io.IOException;

import excepciones.EventPlannerException;
import presentacion.NavigationHelper;

/**
 * Controlador para la funcionalidad de cancelar inscripción de un participante a un evento.
 * Permite buscar un participante, visualizar sus eventos activos y cancelar su inscripción.
 * 
 * <p><b>Flujo de uso:</b></p>
 * <ol>
 *   <li>Usuario ingresa ID o email del participante</li>
 *   <li>Sistema busca y muestra los eventos donde está inscrito</li>
 *   <li>Usuario selecciona el evento a cancelar</li>
 *   <li>Sistema valida y procesa la cancelación</li>
 * </ol>
 * 
 * @author  Ayner Jose Castro Benavides
 * @version 1.0
 * @since 2025-01-11
 */
public class CancelarInscripcionController {
    
    // ==================== COMPONENTES FXML ====================
    
    /**
     * Campo de texto para ingresar el ID del participante
     */
    @FXML
    private TextField txtIdParticipante;
    
    /**
     * Campo de texto para ingresar el email del participante (búsqueda alternativa)
     */
    @FXML
    private TextField txtEmailParticipante;
    
    /**
     * Etiqueta que muestra el nombre del participante encontrado
     */
    @FXML
    private Label lblNombreParticipante;
    
    /**
     * Tabla que muestra los eventos donde está inscrito el participante
     */
    @FXML
    private TableView<Evento> tablaEventos;
    
    /**
     * Columna que muestra el ID del evento
     */
    @FXML
    private TableColumn<Evento, String> colId;
    
    /**
     * Columna que muestra el nombre del evento
     */
    @FXML
    private TableColumn<Evento, String> colNombre;
    
    /**
     * Columna que muestra la fecha del evento
     */
    @FXML
    private TableColumn<Evento, String> colFecha;
    
    /**
     * Columna que muestra el tipo de evento
     */
    @FXML
    private TableColumn<Evento, String> colTipo;
    
    /**
     * Columna que muestra el estado del evento
     */
    @FXML
    private TableColumn<Evento, String> colEstado;
    
    /**
     * Botón para cancelar la inscripción
     */
    @FXML
    private Button btnCancelar;
    
    // ==================== ATRIBUTOS DE CLASE ====================
    
    /**
     * Instancia del gestor de eventos (patrón Singleton)
     */
    private GestorEventos gestor;
    
    /**
     * Participante actualmente seleccionado para cancelar inscripción
     */
    private Participante participanteActual;
    
    /**
     * Stage principal de la aplicación
     */
    private Stage stage;
    
    /**
     * Lista observable de eventos para la tabla
     */
    private ObservableList<Evento> listaEventos;
    
    // ==================== INICIALIZACIÓN ====================
    
    /**
     * Inicializa el controlador después de que se cargue el FXML.
     * Configura las columnas de la tabla y prepara los componentes.
     * 
     * <p><b>Nota:</b> Este método se ejecuta automáticamente por JavaFX</p>
     */
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.listaEventos = FXCollections.observableArrayList();
        
        configurarTabla();
        deshabilitarBotonCancelar();
        
        System.out.println("[CancelarInscripcion] Controlador inicializado");
    }
    
    /**
     * Configura las columnas de la tabla de eventos.
     * Define qué propiedad del objeto Evento se mostrará en cada columna.
     * 
     * <p><b>Enseñanza:</b> Usamos setCellValueFactory para mapear los datos
     * del objeto Evento a las celdas de la tabla.</p>
     */
    private void configurarTabla() {
        // Configurar cada columna para mostrar la propiedad correspondiente
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        
        colNombre.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        
        colFecha.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaInicio().toLocalDate().toString()));
        
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTipo().getDescripcion()));
        
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstado().getDescripcion()));
        
        // Asociar la lista observable con la tabla
        tablaEventos.setItems(listaEventos);
        
        System.out.println("[CancelarInscripcion] Tabla configurada");
    }
    
    /**
     * Establece el Stage principal de la aplicación
     * 
     * @param stage El Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    /**
     * Busca un participante por su ID.
     * Si lo encuentra, carga sus eventos activos en la tabla.
     * 
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>El campo ID no debe estar vacío</li>
     *   <li>El participante debe existir en el sistema</li>
     *   <li>El participante debe tener eventos registrados</li>
     * </ul>
     */
    @FXML
    private void buscarPorId() {
        String id = txtIdParticipante.getText().trim();
        
        // Validación: campo no vacío
        if (id.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un ID de participante");
            return;
        }
        
        try {
            // Buscar el participante en el sistema
            Participante participante = gestor.buscarParticipante(id);
            
            if (participante == null) {
                mostrarAdvertencia("No encontrado", 
                    "No existe un participante con el ID: " + id);
                limpiarFormulario();
                return;
            }
            
            // Participante encontrado, cargar sus datos
            cargarDatosParticipante(participante);
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            limpiarFormulario();
        }
    }
    
    /**
     * Busca un participante por su email.
     * Si lo encuentra, carga sus eventos activos en la tabla.
     * 
     * <p><b>Nota:</b> El email debe ser único en el sistema</p>
     */
    @FXML
    private void buscarPorEmail() {
        String email = txtEmailParticipante.getText().trim();
        
        // Validación: campo no vacío
        if (email.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un email");
            return;
        }
        
        try {
            // Buscar por email usando el repositorio
            Participante participante = gestor.buscarParticipantePorEmail(email);
            
            if (participante == null) {
                mostrarAdvertencia("No encontrado", 
                    "No existe un participante con el email: " + email);
                limpiarFormulario();
                return;
            }
            
            // Participante encontrado, cargar sus datos
            cargarDatosParticipante(participante);
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            limpiarFormulario();
        }
    }
    
    /**
     * Carga los datos del participante encontrado y sus eventos activos.
     * 
     * @param participante El participante a cargar
     * 
     * <p><b>Enseñanza:</b> Este método centraliza la lógica de carga de datos
     * para evitar duplicación de código entre buscarPorId() y buscarPorEmail()</p>
     */
    private void cargarDatosParticipante(Participante participante) {
        this.participanteActual = participante;
        
        // Mostrar nombre del participante
        lblNombreParticipante.setText(participante.getNombre());
        
        // Obtener los IDs de eventos donde está inscrito
        var eventosIds = participante.getEventosRegistrados();
        
        if (eventosIds.isEmpty()) {
            mostrarInformacion("Sin eventos", 
                "Este participante no está inscrito en ningún evento");
            listaEventos.clear();
            deshabilitarBotonCancelar();
            return;
        }
        
        // Cargar los eventos completos en la tabla
        cargarEventosEnTabla(eventosIds);
        habilitarBotonCancelar();
        
        System.out.println("[CancelarInscripcion] Participante cargado: " + participante.getNombre());
        System.out.println("[CancelarInscripcion] Eventos registrados: " + eventosIds.size());
    }
    
    /**
     * Carga los eventos en la tabla a partir de sus IDs.
     * 
     * @param eventosIds Lista de IDs de eventos
     * 
     * <p><b>Enseñanza:</b> Iteramos sobre los IDs, buscamos cada evento
     * completo y lo agregamos a la lista observable de la tabla</p>
     */
    private void cargarEventosEnTabla(java.util.ArrayList<String> eventosIds) {
        listaEventos.clear();
        
        for (String eventoId : eventosIds) {
            try {
                Evento evento = gestor.buscarEvento(eventoId);
                if (evento != null) {
                    listaEventos.add(evento);
                }
            } catch (EventPlannerException e) {
                System.err.println("[CancelarInscripcion] Error al cargar evento " + eventoId);
            }
        }
        
        System.out.println("[CancelarInscripcion] Eventos cargados en tabla: " + listaEventos.size());
    }
    
    // ==================== CANCELACIÓN DE INSCRIPCIÓN ====================
    
    /**
     * Cancela la inscripción del participante al evento seleccionado en la tabla.
     * 
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>Debe haber un participante seleccionado</li>
     *   <li>Debe haber un evento seleccionado en la tabla</li>
     *   <li>Usuario debe confirmar la acción</li>
     * </ul>
     * 
     * <p><b>Flujo:</b></p>
     * <ol>
     *   <li>Validar selección</li>
     *   <li>Solicitar confirmación al usuario</li>
     *   <li>Procesar cancelación en el sistema</li>
     *   <li>Actualizar la tabla y mostrar resultado</li>
     * </ol>
     * @throws IOException 
     */
    @FXML
    private void cancelarInscripcion() throws IOException {
        // Validación: debe haber un participante cargado
        if (participanteActual == null) {
            mostrarAdvertencia("Participante no seleccionado", 
                "Primero debe buscar un participante");
            return;
        }
        
        // Validación: debe haber un evento seleccionado en la tabla
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        
        if (eventoSeleccionado == null) {
            mostrarAdvertencia("Evento no seleccionado", 
                "Debe seleccionar un evento de la tabla");
            return;
        }
        
        // Solicitar confirmación al usuario
        boolean confirmado = mostrarConfirmacion(
            "Confirmar cancelación",
            "¿Está seguro de cancelar la inscripción de:\n\n" +
            "Participante: " + participanteActual.getNombre() + "\n" +
            "Evento: " + eventoSeleccionado.getNombre() + "\n\n" +
            "Esta acción no se puede deshacer."
        );
        
        if (!confirmado) {
            System.out.println("[CancelarInscripcion] Cancelación abortada por el usuario");
            return;
        }
        
        // Procesar la cancelación
        try {
            gestor.cancelarInscripcion(participanteActual.getId(), eventoSeleccionado.getId());
            
            mostrarExito("Inscripción cancelada", 
                "La inscripción ha sido cancelada exitosamente");
            
            // Recargar los eventos del participante
            cargarDatosParticipante(participanteActual);
            
            System.out.println("[CancelarInscripcion] Inscripción cancelada: " + 
                eventoSeleccionado.getNombre());
            
        } catch (EventPlannerException e) {
            mostrarError("Error al cancelar", e.getMessage());
            System.err.println("[CancelarInscripcion] Error: " + e.getMessage());
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Limpia todos los campos del formulario y la tabla
     */
    @FXML
    private void limpiarFormulario() {
        txtIdParticipante.clear();
        txtEmailParticipante.clear();
        lblNombreParticipante.setText("");
        listaEventos.clear();
        participanteActual = null;
        deshabilitarBotonCancelar();
        
        System.out.println("[CancelarInscripcion] Formulario limpiado");
    }
    
    
    /**
     * Habilita el botón de cancelar inscripción
     */
    private void habilitarBotonCancelar() {
        btnCancelar.setDisable(false);
    }
    
    /**
     * Deshabilita el botón de cancelar inscripción
     */
    private void deshabilitarBotonCancelar() {
        btnCancelar.setDisable(true);
    }
    
    // ==================== NAVEGACIÓN ====================
    
    /**
     * Vuelve al menú de participantes
     */
    @FXML
    private void volverMenuParticipantes() {
        if (stage == null) {
            mostrarError("Error", "No se puede volver al menú");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MenuParticipantes.fxml"));
            Parent root = loader.load();
            
            MenuParticipantesController controller = loader.getController();
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);  
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Menú Participantes - Event Planner");
            
            System.out.println("[CancelarInscripcion] Volviendo al menú de participantes");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú");
        }
    }
    
    // ==================== MENSAJES DE USUARIO ====================
    
    /**
     * Muestra un mensaje de advertencia al usuario
     * 
     * @param titulo Título del diálogo
     * @param mensaje Contenido del mensaje
     */
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de error al usuario
     * 
     * @param titulo Título del diálogo
     * @param mensaje Contenido del mensaje
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje informativo al usuario
     * 
     * @param titulo Título del diálogo
     * @param mensaje Contenido del mensaje
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de éxito al usuario
     * 
     * @param titulo Título del diálogo
     * @param mensaje Contenido del mensaje
     */
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("✓ Operación exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de confirmación y retorna la respuesta del usuario
     * 
     * @param titulo Título del diálogo
     * @param mensaje Contenido del mensaje
     * @return true si el usuario confirmó, false si canceló
     */
    private boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        
        var resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}