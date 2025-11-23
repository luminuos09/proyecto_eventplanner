package presentacion.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import logica.GestorEventos;
import modelos.Participante;
import presentacion.NavigationHelper;
import modelos.Evento;
import excepciones.EventPlannerException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Controlador para buscar y visualizar información completa de participantes.
 * Permite búsquedas por ID, email o nombre, y muestra datos personales y eventos.
 * 
 * <p><b>Funcionalidades principales:</b></p>
 * <ul>
 *   <li>Búsqueda por ID (exacta)</li>
 *   <li>Búsqueda por email (exacta)</li>
 *   <li>Búsqueda por nombre (coincidencia parcial)</li>
 *   <li>Visualización de datos personales completos</li>
 *   <li>Lista de eventos donde está inscrito</li>
 *   <li>Indicador de estado VIP</li>
 * </ul>
 * 
 * <p><b>Casos de uso:</b></p>
 * <ul>
 *   <li>Verificar si una persona ya está registrada antes de crear cuenta</li>
 *   <li>Consultar datos de contacto de un participante</li>
 *   <li>Ver el historial de participación en eventos</li>
 *   <li>Validar información antes de procesos administrativos</li>
 * </ul>
 * 
 * @author  Ayner Jose Castro Benavides
 * @version 1.0
 * @since 2025-01-11
 */
public class BuscarParticipanteController {
    
    // ==================== COMPONENTES FXML - BÚSQUEDA ====================
    
    /**
     * Campo de texto para ingresar el ID del participante a buscar
     */
    @FXML
    private TextField txtId;
    
    /**
     * Campo de texto para ingresar el email del participante a buscar
     */
    @FXML
    private TextField txtEmail;
    
    /**
     * Campo de texto para ingresar el nombre (o parte del nombre) a buscar
     */
    @FXML
    private TextField txtNombre;
    
    /**
     * Botón para buscar por ID
     */
    @FXML
    private Button btnBuscarId;
    
    /**
     * Botón para buscar por email
     */
    @FXML
    private Button btnBuscarEmail;
    
    /**
     * Botón para buscar por nombre
     */
    @FXML
    private Button btnBuscarNombre;
    
    // ==================== COMPONENTES FXML - RESULTADOS ====================
    
    /**
     * Panel que contiene los resultados de la búsqueda
     * Se oculta hasta que se realice una búsqueda exitosa
     */
    @FXML
    private javafx.scene.layout.VBox panelResultados;
    
    /**
     * Etiqueta que muestra el nombre completo del participante encontrado
     */
    @FXML
    private Label lblNombre;
    
    /**
     * Etiqueta que muestra el ID del participante
     */
    @FXML
    private Label lblId;
    
    /**
     * Etiqueta que muestra el email del participante
     */
    @FXML
    private Label lblEmail;
    
    /**
     * Etiqueta que muestra el teléfono del participante
     */
    @FXML
    private Label lblTelefono;
    
    /**
     * Etiqueta que muestra la empresa donde trabaja
     */
    @FXML
    private Label lblEmpresa;
    
    /**
     * Etiqueta que muestra el cargo que desempeña
     */
    @FXML
    private Label lblCargo;
    
    /**
     * Etiqueta que muestra los intereses del participante
     */
    @FXML
    private Label lblIntereses;
    
    /**
     * Etiqueta que muestra si es VIP o no
     */
    @FXML
    private Label lblEstadoVip;
    
    /**
     * Etiqueta que muestra la fecha de registro en el sistema
     */
    @FXML
    private Label lblFechaRegistro;
    
    /**
     * Etiqueta que muestra el total de eventos donde está inscrito
     */
    @FXML
    private Label lblTotalEventos;
    
    // ==================== COMPONENTES FXML - TABLA ====================
    
    /**
     * Tabla que muestra los eventos donde está inscrito el participante
     */
    @FXML
    private TableView<Evento> tablaEventos;
    
    /**
     * Columna que muestra el nombre del evento
     */
    @FXML
    private TableColumn<Evento, String> colNombre;
    
    /**
     * Columna que muestra el tipo de evento
     */
    @FXML
    private TableColumn<Evento, String> colTipo;
    
    /**
     * Columna que muestra la fecha del evento
     */
    @FXML
    private TableColumn<Evento, String> colFecha;
    
    /**
     * Columna que muestra el estado del evento
     */
    @FXML
    private TableColumn<Evento, String> colEstado;
    
    /**
     * Columna que muestra si asistió al evento
     */
    @FXML
    private TableColumn<Evento, String> colAsistencia;
    
    // ==================== COMPONENTES FXML - BÚSQUEDA MÚLTIPLE ====================
    
    /**
     * Tabla que muestra múltiples resultados cuando la búsqueda por nombre
     * encuentra varios participantes
     */
    @FXML
    private TableView<Participante> tablaResultados;
    
    /**
     * Columna de ID en la tabla de resultados múltiples
     */
    @FXML
    private TableColumn<Participante, String> colResId;
    
    /**
     * Columna de nombre en la tabla de resultados múltiples
     */
    @FXML
    private TableColumn<Participante, String> colResNombre;
    
    /**
     * Columna de email en la tabla de resultados múltiples
     */
    @FXML
    private TableColumn<Participante, String> colResEmail;
    
    /**
     * Columna de empresa en la tabla de resultados múltiples
     */
    @FXML
    private TableColumn<Participante, String> colResEmpresa;
    
    /**
     * Panel que contiene la tabla de resultados múltiples
     */
    @FXML
    private javafx.scene.layout.VBox panelResultadosMultiples;
    
    // ==================== ATRIBUTOS DE CLASE ====================
    
    /**
     * Instancia del gestor de eventos (patrón Singleton)
     */
    private GestorEventos gestor;
    
    /**
     * Participante actualmente mostrado en la vista
     */
    private Participante participanteActual;
    
    /**
     * Stage de la ventana modal (esta vista se abre como modal)
     */
    private Stage stage;
    
    /**
     * Lista observable de eventos del participante para la tabla
     */
    private ObservableList<Evento> listaEventos;
    
    /**
     * Lista observable de participantes encontrados (búsqueda múltiple)
     */
    private ObservableList<Participante> listaParticipantes;
    
    /**
     * Formateador de fechas para mostrar en formato legible
     */
    private final DateTimeFormatter formatoFecha = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Formateador de fechas corto (solo día/mes/año)
     */
    private final DateTimeFormatter formatoFechaCorto = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // ==================== INICIALIZACIÓN ====================
    
    /**
     * Inicializa el controlador después de que se cargue el FXML.
     * Configura las tablas y prepara los componentes.
     * 
     * <p><b>Enseñanza:</b> Inicializamos con los paneles de resultados ocultos.
     * Solo se mostrarán cuando haya una búsqueda exitosa.</p>
     */
    @FXML
    public void initialize() {
        this.gestor = GestorEventos.getInstance();
        this.listaEventos = FXCollections.observableArrayList();
        this.listaParticipantes = FXCollections.observableArrayList();
        
        configurarTablaEventos();
        configurarTablaResultados();
        ocultarPanelesResultados();
        
        System.out.println("[BuscarParticipante] Controlador inicializado");
    }
    
    /**
     * Configura la tabla de eventos del participante.
     * Define cómo se mostrarán los datos de cada evento.
     */
    private void configurarTablaEventos() {
        colNombre.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNombre()));
        
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTipo().getDescripcion()));
        
        colFecha.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaInicio().format(formatoFechaCorto)));
        
        colEstado.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEstado().getDescripcion()));
        
        colAsistencia.setCellValueFactory(cellData -> {
            Evento evento = cellData.getValue();
            String participanteId = (participanteActual != null) 
                ? participanteActual.getId() 
                : "";
            
            boolean asistio = evento.getParticipantesAsistentes()
                .contains(participanteId);
            
            return new javafx.beans.property.SimpleStringProperty(
                asistio ? "✓ Sí" : "No");
        });
        
        tablaEventos.setItems(listaEventos);
        
        System.out.println("[BuscarParticipante] Tabla de eventos configurada");
    }
    
    /**
     * Configura la tabla de resultados múltiples (búsqueda por nombre).
     * Esta tabla se usa cuando se encuentran varios participantes.
     * 
     * <p><b>Enseñanza:</b> Cuando buscas por nombre, pueden haber varias
     * coincidencias (ej: "Juan García" y "Juan López"). Esta tabla muestra
     * todos los resultados y el usuario puede seleccionar cuál ver.</p>
     */
    private void configurarTablaResultados() {
        colResId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getId()));
        
        colResNombre.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNombre()));
        
        colResEmail.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEmail()));
        
        colResEmpresa.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEmpresa() != null 
                    ? cellData.getValue().getEmpresa() 
                    : "N/A"));
        
        tablaResultados.setItems(listaParticipantes);
        
        // Listener para cuando el usuario seleccione un participante de la lista
        tablaResultados.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarDetallesParticipante(newSelection);
                }
            });
        
        System.out.println("[BuscarParticipante] Tabla de resultados múltiples configurada");
    }
    
    /**
     * Oculta los paneles de resultados al iniciar la vista.
     * Se mostrarán solo cuando haya datos que mostrar.
     */
    private void ocultarPanelesResultados() {
        if (panelResultados != null) {
            panelResultados.setVisible(false);
            panelResultados.setManaged(false);
        }
        if (panelResultadosMultiples != null) {
            panelResultadosMultiples.setVisible(false);
            panelResultadosMultiples.setManaged(false);
        }
    }
    
    /**
     * Establece el Stage de la ventana modal
     * 
     * @param stage El Stage de la ventana
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    /**
     * Busca un participante por su ID (búsqueda exacta).
     * 
     * <p><b>Validaciones:</b></p>
     * <ul>
     *   <li>El campo no debe estar vacío</li>
     *   <li>El ID debe existir en el sistema</li>
     * </ul>
     * 
     * <p><b>Enseñanza:</b> La búsqueda por ID es EXACTA, debe coincidir
     * completamente. Si escribes "PART001" no encontrará "PART002".</p>
     */
    @FXML
    private void buscarPorId() {
        String id = txtId.getText().trim();
        
        // Validación: campo no vacío
        if (id.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un ID");
            return;
        }
        
        try {
            Participante participante = gestor.buscarParticipante(id);
            
            if (participante == null) {
                mostrarAdvertencia("No encontrado", 
                    "No existe un participante con el ID: " + id);
                ocultarPanelesResultados();
                return;
            }
            
            // Participante encontrado, mostrar sus datos
            mostrarDetallesParticipante(participante);
            
            System.out.println("[BuscarParticipante] Encontrado por ID: " + 
                participante.getNombre());
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            ocultarPanelesResultados();
        }
    }
    
    /**
     * Busca un participante por su email (búsqueda exacta).
     * 
     * <p><b>Nota:</b> El email debe ser único en el sistema, por lo que
     * solo puede haber un resultado.</p>
     */
    @FXML
    private void buscarPorEmail() {
        String email = txtEmail.getText().trim();
        
        // Validación: campo no vacío
        if (email.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un email");
            return;
        }
        
        try {
            Participante participante = gestor.buscarParticipantePorEmail(email);
            
            if (participante == null) {
                mostrarAdvertencia("No encontrado", 
                    "No existe un participante con el email: " + email);
                ocultarPanelesResultados();
                return;
            }
            
            // Participante encontrado, mostrar sus datos
            mostrarDetallesParticipante(participante);
            
            System.out.println("[BuscarParticipante] Encontrado por email: " + 
                participante.getNombre());
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            ocultarPanelesResultados();
        }
    }
    
    /**
     * Busca participantes por nombre (búsqueda parcial).
     * Puede encontrar múltiples resultados si varios nombres coinciden.
     * 
     * <p><b>Enseñanza:</b> Esta búsqueda es FLEXIBLE:</p>
     * <ul>
     *   <li>Insensible a mayúsculas/minúsculas</li>
     *   <li>Encuentra coincidencias parciales (ej: "Juan" encuentra "Juan García")</li>
     *   <li>Puede devolver múltiples resultados</li>
     * </ul>
     * 
     * <p><b>Flujo:</b></p>
     * <ol>
     *   <li>Si encuentra 0 resultados → Mostrar advertencia</li>
     *   <li>Si encuentra 1 resultado → Mostrar detalles directamente</li>
     *   <li>Si encuentra múltiples → Mostrar tabla de resultados</li>
     * </ol>
     */
    @FXML
    private void buscarPorNombre() {
        String nombre = txtNombre.getText().trim();
        
        // Validación: campo no vacío
        if (nombre.isEmpty()) {
            mostrarAdvertencia("Campo vacío", "Debe ingresar un nombre");
            return;
        }
        
        try {
            // Buscar todos los participantes que coincidan
            ArrayList<Participante> resultados = 
                gestor.buscarParticipantesPorNombre(nombre);
            
            if (resultados.isEmpty()) {
                mostrarAdvertencia("Sin resultados", 
                    "No se encontraron participantes con el nombre: " + nombre);
                ocultarPanelesResultados();
                return;
            }
            
            // Análisis del número de resultados
            if (resultados.size() == 1) {
                // Solo un resultado, mostrar directamente
                mostrarDetallesParticipante(resultados.get(0));
                System.out.println("[BuscarParticipante] 1 resultado encontrado");
            } else {
                // Múltiples resultados, mostrar tabla de selección
                mostrarResultadosMultiples(resultados);
                System.out.println("[BuscarParticipante] " + resultados.size() + 
                    " resultados encontrados");
            }
            
        } catch (EventPlannerException e) {
            mostrarError("Error de búsqueda", e.getMessage());
            ocultarPanelesResultados();
        }
    }
    
    // ==================== VISUALIZACIÓN DE DATOS ====================
    
    /**
     * Muestra los detalles completos de un participante.
     * Carga su información personal y sus eventos.
     * 
     * @param participante El participante a mostrar
     * 
     * <p><b>Datos mostrados:</b></p>
     * <ul>
     *   <li>Información personal (nombre, email, teléfono)</li>
     *   <li>Información laboral (empresa, cargo)</li>
     *   <li>Intereses personales</li>
     *   <li>Estado VIP</li>
     *   <li>Fecha de registro</li>
     *   <li>Lista de eventos inscritos</li>
     * </ul>
     */
    private void mostrarDetallesParticipante(Participante participante) {
        this.participanteActual = participante;
        
        // Mostrar información personal
        lblNombre.setText(participante.getNombre());
        lblId.setText(participante.getId());
        lblEmail.setText(participante.getEmail());
        lblTelefono.setText(participante.getTelefono() != null 
            ? participante.getTelefono() 
            : "No especificado");
        
        // Mostrar información laboral
        lblEmpresa.setText(participante.getEmpresa() != null 
            ? participante.getEmpresa() 
            : "No especificada");
        
        lblCargo.setText(participante.getCargo() != null 
            ? participante.getCargo() 
            : "No especificado");
        
        // Mostrar intereses
        lblIntereses.setText(participante.getIntereses() != null 
            ? participante.getIntereses() 
            : "No especificados");
        
        // Mostrar estado VIP con estilo visual
        if (participante.isVip()) {
            lblEstadoVip.setText("⭐ VIP");
            lblEstadoVip.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");
        } else {
            lblEstadoVip.setText("Regular");
            lblEstadoVip.setStyle("-fx-text-fill: #666666;");
        }
        
        // Mostrar fecha de registro
        lblFechaRegistro.setText(
            participante.getFechaRegistro().format(formatoFecha));
        
        // Cargar eventos del participante
        cargarEventosParticipante(participante);
        
        // Mostrar panel de detalles, ocultar resultados múltiples
        panelResultados.setVisible(true);
        panelResultados.setManaged(true);
        
        if (panelResultadosMultiples != null) {
            panelResultadosMultiples.setVisible(false);
            panelResultadosMultiples.setManaged(false);
        }
        
        System.out.println("[BuscarParticipante] Detalles mostrados: " + 
            participante.getNombre());
    }
    
    /**
     * Muestra la tabla de resultados múltiples cuando la búsqueda
     * encuentra varios participantes.
     * 
     * @param participantes Lista de participantes encontrados
     * 
     * <p><b>Enseñanza:</b> Cuando hay múltiples resultados, mostramos
     * una tabla para que el usuario seleccione cuál quiere ver en detalle.
     * Es como hacer una "pre-selección" antes de ver el perfil completo.</p>
     */
    private void mostrarResultadosMultiples(ArrayList<Participante> participantes) {
        listaParticipantes.clear();
        listaParticipantes.addAll(participantes);
        
        // Mostrar panel de resultados múltiples, ocultar detalles
        panelResultadosMultiples.setVisible(true);
        panelResultadosMultiples.setManaged(true);
        
        panelResultados.setVisible(false);
        panelResultados.setManaged(false);
        
        mostrarInformacion("Múltiples resultados", 
            "Se encontraron " + participantes.size() + 
            " participantes. Seleccione uno de la tabla para ver sus detalles.");
    }
    
    /**
     * Carga los eventos donde está inscrito el participante.
     * 
     * @param participante El participante cuyos eventos se van a cargar
     * 
     * <p><b>Proceso:</b></p>
     * <ol>
     *   <li>Obtener los IDs de eventos del participante</li>
     *   <li>Por cada ID, buscar el evento completo</li>
     *   <li>Agregar cada evento a la tabla</li>
     *   <li>Actualizar contador de eventos</li>
     * </ol>
     */
    private void cargarEventosParticipante(Participante participante) {
        listaEventos.clear();
        
        var eventosIds = participante.getEventosRegistrados();
        
        for (String eventoId : eventosIds) {
            try {
                Evento evento = gestor.buscarEvento(eventoId);
                if (evento != null) {
                    listaEventos.add(evento);
                }
            } catch (EventPlannerException e) {
                System.err.println("[BuscarParticipante] Error al cargar evento: " + 
                    eventoId);
            }
        }
        
        // Actualizar contador
        lblTotalEventos.setText(String.valueOf(listaEventos.size()));
        
        System.out.println("[BuscarParticipante] Eventos cargados: " + 
            listaEventos.size());
    }
    
    // ==================== LIMPIEZA Y NAVEGACIÓN ====================
    
    /**
     * Limpia todos los campos de búsqueda y oculta los resultados.
     * Reinicia la vista al estado inicial.
     */
    @FXML
    private void limpiar() {
        // Limpiar campos de búsqueda
        txtId.clear();
        txtEmail.clear();
        txtNombre.clear();
        
        // Limpiar listas
        listaEventos.clear();
        listaParticipantes.clear();
        
        // Limpiar referencia
        participanteActual = null;
        
        // Ocultar paneles de resultados
        ocultarPanelesResultados();
        
        System.out.println("[BuscarParticipante] Vista limpiada");
    }
    
    /**
     * Cierra la ventana de búsqueda y vuelve a la ventana anterior.
     * 
     * <p><b>Nota:</b> Como esta vista se abre como modal, solo necesitamos
     * cerrar el Stage actual.</p>
     * @throws IOException 
     */
    @FXML
    private void cerrar() throws IOException {
    
       Platform.runLater(() -> {
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
            stage.setTitle("Menú Participantes - Event Planner");
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo volver al menú: " + e.getMessage());
        }
    });
    }
    
    // ==================== MENSAJES AL USUARIO ====================
    
    /**
     * Muestra un mensaje de advertencia
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
     * Muestra un mensaje de error
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
     * Muestra un mensaje informativo
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
}