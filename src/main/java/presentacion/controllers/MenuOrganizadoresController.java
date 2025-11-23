package presentacion.controllers;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logica.GestorAutenticacion;
import modelos.EstadoUsuario;
import modelos.RolUsuario;
import modelos.Usuario;
import persistencia.UsuarioRepositorio;
import presentacion.NavigationHelper;

/**
 * Controlador para el menú de gestión de organizadores.
 * Permite aprobar solicitudes, gestionar organizadores activos y ver historial.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 2.0
 * @since 2025-01-12
 */
public class MenuOrganizadoresController {
    
    // ==================== COMPONENTES FXML ====================
    
    @FXML private TabPane tabPane;
    @FXML private Tab tabPendientes;
    @FXML private Tab tabActivos;
    @FXML private Tab tabTodos;
    
    // Tab Pendientes
    @FXML private TableView<Usuario> tablaPendientes;
    @FXML private TableColumn<Usuario, String> colPendNombre;
    @FXML private TableColumn<Usuario, String> colPendUsuario;
    @FXML private TableColumn<Usuario, String> colPendEmail;
    @FXML private TableColumn<Usuario, String> colPendFecha;
    @FXML private TableColumn<Usuario, Void> colPendAcciones;
    @FXML private Label lblCantidadPendientes;
    @FXML private VBox vboxSinPendientes;
    
    // Tab Activos
    @FXML private TableView<Usuario> tablaActivos;
    @FXML private TableColumn<Usuario, String> colActNombre;
    @FXML private TableColumn<Usuario, String> colActUsuario;
    @FXML private TableColumn<Usuario, String> colActEmail;
    @FXML private TableColumn<Usuario, String> colActUltimoAcceso;
    @FXML private TableColumn<Usuario, Void> colActAcciones;
    @FXML private Label lblCantidadActivos;
    
    // Tab Todos
    @FXML private TableView<Usuario> tablaTodos;
    @FXML private TableColumn<Usuario, String> colTodosNombre;
    @FXML private TableColumn<Usuario, String> colTodosUsuario;
    @FXML private TableColumn<Usuario, String> colTodosEmail;
    @FXML private TableColumn<Usuario, String> colTodosEstado;
    @FXML private TableColumn<Usuario, String> colTodosFecha;
    @FXML private TableColumn<Usuario, Void> colTodosAcciones;
    @FXML private ComboBox<String> comboFiltroEstado;
    @FXML private TextField txtBuscar;
    
    // ==================== ATRIBUTOS ====================
    
    private Stage stage;
    private UsuarioRepositorio usuarioRepo;
    private ObservableList<Usuario> listaPendientes;
    private ObservableList<Usuario> listaActivos;
    private ObservableList<Usuario> listaTodos;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioRepo = GestorAutenticacion.getInstance().getUsuarioRepositorio();
        
        // Inicializar listas observables
        listaPendientes = FXCollections.observableArrayList();
        listaActivos = FXCollections.observableArrayList();
        listaTodos = FXCollections.observableArrayList();
        
        // Configurar tablas
        configurarTablaPendientes();
        configurarTablaActivos();
        configurarTablaTodos();
        
        // Configurar combo de filtros (solo si existe)
        if (comboFiltroEstado != null) {
            comboFiltroEstado.getItems().addAll(
                "Todos",
                "✅ Activos",
                "⏳ Pendientes",
                "❌ Inactivos",
                "🚫 Rechazados"
            );
            comboFiltroEstado.getSelectionModel().selectFirst();
        }
        
        // Cargar datos
        actualizarListas();
        
        System.out.println("[MenuOrganizadores] Controlador inicializado");
    }
    
    /**
     * Establece el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("[MenuOrganizadores] Stage asignado");
    }
    
    // ==================== CONFIGURACIÓN DE TABLAS ====================
    
    /**
     * Configura la tabla de solicitudes pendientes
     */
    private void configurarTablaPendientes() {
        colPendNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colPendUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colPendEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPendFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFechaCreacion().format(dateFormatter))
        );
        
        // Columna de acciones con botones
        colPendAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnAprobar = new Button("✅ Aprobar");
            private final Button btnRechazar = new Button("❌ Rechazar");
            private final Button btnDetalles = new Button("👁️");
            private final HBox hbox = new HBox(8, btnAprobar, btnRechazar, btnDetalles);
            
            {
                btnAprobar.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                btnRechazar.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                btnDetalles.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                hbox.setAlignment(Pos.CENTER);
                
                btnAprobar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    aprobarOrganizador(usuario);
                });
                
                btnRechazar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    rechazarOrganizador(usuario);
                });
                
                btnDetalles.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    mostrarDetallesUsuario(usuario);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        tablaPendientes.setItems(listaPendientes);
    }
    
    /**
     * Configura la tabla de organizadores activos
     */
    private void configurarTablaActivos() {
        colActNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colActUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colActEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colActUltimoAcceso.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUltimoAccesoFormateado())
        );
        
        // Columna de acciones
        colActAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnDesactivar = new Button("❌ Desactivar");
            private final HBox hbox = new HBox(8, btnDesactivar);
            
            {
                btnDesactivar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 15; -fx-background-radius: 5; -fx-cursor: hand;");
                hbox.setAlignment(Pos.CENTER);
                
                btnDesactivar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    desactivarOrganizador(usuario);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        tablaActivos.setItems(listaActivos);
    }
    
    /**
     * Configura la tabla de todos los organizadores
     */
    private void configurarTablaTodos() {
        // Solo configurar si existe el tab
        if (tablaTodos == null) return;
        
        colTodosNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colTodosUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colTodosEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTodosEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().toString())
        );
        colTodosFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFechaCreacion().format(dateFormatter))
        );
        
        // Columna de detalles
        colTodosAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️");
            
            {
                btnVer.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 15; -fx-background-radius: 5; -fx-cursor: hand;");
                
                btnVer.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    mostrarDetallesUsuario(usuario);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnVer);
                setAlignment(Pos.CENTER);
            }
        });
        
        tablaTodos.setItems(listaTodos);
    }
    
    // ==================== ACCIONES PRINCIPALES ====================
    
    /**
     * Aprueba un organizador pendiente
     */
    private void aprobarOrganizador(Usuario usuario) {
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Aprobar Organizador");
        confirmacion.setHeaderText("¿Aprobar a " + usuario.getNombreCompleto() + "?");
        confirmacion.setContentText("El usuario podrá acceder al sistema como organizador.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    usuarioRepo.aprobarOrganizador(usuario.getId());
                    mostrarExito("Organizador Aprobado", 
                        usuario.getNombreCompleto() + " ha sido aprobado exitosamente.");
                    actualizarListas();
                } catch (IOException e) {
                    mostrarError("Error", "No se pudo aprobar: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Rechaza un organizador pendiente
     */
    private void rechazarOrganizador(Usuario usuario) {
        Alert confirmacion = new Alert(AlertType.WARNING);
        confirmacion.setTitle("Rechazar Solicitud");
        confirmacion.setHeaderText("¿Rechazar a " + usuario.getNombreCompleto() + "?");
        confirmacion.setContentText("El usuario no podrá acceder al sistema.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    usuarioRepo.rechazarOrganizador(usuario.getId());
                    mostrarInfo("Solicitud Rechazada", 
                        "La solicitud de " + usuario.getNombreCompleto() + " ha sido rechazada.");
                    actualizarListas();
                } catch (IOException e) {
                    mostrarError("Error", "No se pudo rechazar: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Desactiva un organizador activo
     */
    private void desactivarOrganizador(Usuario usuario) {
        Alert confirmacion = new Alert(AlertType.WARNING);
        confirmacion.setTitle("Desactivar Organizador");
        confirmacion.setHeaderText("¿Desactivar a " + usuario.getNombreCompleto() + "?");
        confirmacion.setContentText("El usuario no podrá acceder al sistema temporalmente.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                usuario.setEstado(EstadoUsuario.INACTIVO);
                usuarioRepo.actualizar(usuario);
                mostrarInfo("Organizador Desactivado", 
                    usuario.getNombreCompleto() + " ha sido desactivado.");
                actualizarListas();
            }
        });
    }
    
    /**
     * Muestra detalles completos de un usuario
     */
    private void mostrarDetallesUsuario(Usuario usuario) {
        Alert detalles = new Alert(AlertType.INFORMATION);
        detalles.setTitle("Detalles del Organizador");
        detalles.setHeaderText(usuario.getNombreCompleto());
        
        String contenido = String.format(
            "Usuario: %s\n" +
            "Email: %s\n" +
            "Estado: %s\n" +
            "Fecha de Registro: %s\n" +
            "Último Acceso: %s\n" +
            "ID: %s",
            usuario.getNombreUsuario(),
            usuario.getEmail(),
            usuario.getEstado().getDescripcion(),
            usuario.getFechaCreacion().format(dateFormatter),
            usuario.getUltimoAccesoFormateado(),
            usuario.getId()
        );
        
        detalles.setContentText(contenido);
        detalles.showAndWait();
    }
    
    // ==================== ACTUALIZACIÓN DE DATOS ====================
    
    /**
     * Actualiza todas las listas con datos frescos
     */
    @FXML
    private void actualizarListas() {
        // Pendientes
        listaPendientes.clear();
        listaPendientes.addAll(usuarioRepo.obtenerOrganizadoresPendientes());
        if (lblCantidadPendientes != null) {
            lblCantidadPendientes.setText("(" + listaPendientes.size() + ")");
        }
        
        // Mostrar/ocultar mensaje de sin pendientes
        if (vboxSinPendientes != null) {
            if (listaPendientes.isEmpty()) {
                tablaPendientes.setVisible(false);
                tablaPendientes.setManaged(false);
                vboxSinPendientes.setVisible(true);
                vboxSinPendientes.setManaged(true);
            } else {
                tablaPendientes.setVisible(true);
                tablaPendientes.setManaged(true);
                vboxSinPendientes.setVisible(false);
                vboxSinPendientes.setManaged(false);
            }
        }
        
        // Activos
        listaActivos.clear();
        listaActivos.addAll(usuarioRepo.obtenerPorEstado(EstadoUsuario.ACTIVO)
            .stream()
            .filter(u -> u.getRol() == RolUsuario.ORGANIZADOR)
            .toList());
        if (lblCantidadActivos != null) {
            lblCantidadActivos.setText("(" + listaActivos.size() + ")");
        }
        
        // Todos (solo si existe)
        if (tablaTodos != null) {
            listaTodos.clear();
            listaTodos.addAll(usuarioRepo.obtenerPorRol(RolUsuario.ORGANIZADOR));
        }
        
        System.out.println("[MenuOrganizadores] Listas actualizadas - Pendientes: " + 
            listaPendientes.size() + ", Activos: " + listaActivos.size());
    }
    
    /**
     * Aplica filtro de estado en la tabla de todos
     */
    @FXML
    private void aplicarFiltro() {
        if (comboFiltroEstado == null || tablaTodos == null) return;
        
        String filtro = comboFiltroEstado.getValue();
        
        if (filtro == null || filtro.equals("Todos")) {
            listaTodos.setAll(usuarioRepo.obtenerPorRol(RolUsuario.ORGANIZADOR));
        } else {
            EstadoUsuario estado = null;
            if (filtro.contains("Activos")) estado = EstadoUsuario.ACTIVO;
            else if (filtro.contains("Pendientes")) estado = EstadoUsuario.PENDIENTE;
            else if (filtro.contains("Inactivos")) estado = EstadoUsuario.INACTIVO;
            else if (filtro.contains("Rechazados")) estado = EstadoUsuario.RECHAZADO;
            
            if (estado != null) {
                EstadoUsuario estadoFinal = estado;
                listaTodos.setAll(usuarioRepo.obtenerPorRol(RolUsuario.ORGANIZADOR)
                    .stream()
                    .filter(u -> u.getEstado() == estadoFinal)
                    .toList());
            }
        }
    }
    
    /**
     * Busca en la tabla por nombre
     */
    @FXML
    private void buscarEnTabla() {
        if (txtBuscar == null || tablaTodos == null) return;
        
        String busqueda = txtBuscar.getText().toLowerCase();
        
        if (busqueda.isEmpty()) {
            aplicarFiltro();
        } else {
            listaTodos.setAll(usuarioRepo.obtenerPorRol(RolUsuario.ORGANIZADOR)
                .stream()
                .filter(u -> u.getNombreCompleto().toLowerCase().contains(busqueda) ||
                           u.getNombreUsuario().toLowerCase().contains(busqueda) ||
                           u.getEmail().toLowerCase().contains(busqueda))
                .toList());
        }
    }
    
    // ==================== NAVEGACIÓN A FUNCIONALIDADES ADICIONALES ====================
    
    /**
     * Abre formulario para registrar organizador manualmente
     */
    @FXML
    private void abrirFormularioOrganizador() {
        System.out.println("[MenuOrganizadores] Abriendo formulario de organizador...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir el formulario");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/FormularioOrganizador.fxml"));
            Parent root = loader.load();
            
            FormularioOrganizadorController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Registrar Organizador - Event Planner");
            
            System.out.println("[MenuOrganizadores] Formulario abierto");
            
        } catch (IOException e) {
            System.err.println("[MenuOrganizadores] Error al abrir formulario:");
            e.printStackTrace();
            mostrarInfo("Registro de Organizadores", 
                "Los organizadores se registran mediante el formulario público.\n\n" +
                "Apruébalos desde la pestaña 'Solicitudes Pendientes'.");
        }
    }
    
    /**
     * Muestra la lista de todos los organizadores registrados
     */
    @FXML
    private void listarOrganizadores() {
        System.out.println("[MenuOrganizadores] Abriendo lista de organizadores...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir la lista");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/ListaOrganizadores.fxml"));
            Parent root = loader.load();
            
            ListaOrganizadoresController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Lista de Organizadores - Event Planner");
            
            System.out.println("[MenuOrganizadores] Lista mostrada");
            
        } catch (Exception e) {
            System.err.println("[MenuOrganizadores] Error al abrir lista:");
            e.printStackTrace();
            mostrarInfo("Lista de Organizadores", 
                "Usa las pestañas principales para ver:\n\n" +
                "• Solicitudes Pendientes\n" +
                "• Organizadores Activos");
        }
    }
    
    /**
     * Abre la ventana de búsqueda de organizadores
     */
    @FXML
    private void buscarOrganizador() {
        System.out.println("[MenuOrganizadores] Abriendo búsqueda de organizador...");
        
        if (this.stage == null) {
            mostrarError("Error", "No se puede abrir la búsqueda");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/BuscarOrganizador.fxml"));
            Parent root = loader.load();
            
            BuscarOrganizadorController controller = loader.getController();
            if (controller != null) {
                controller.setStage(this.stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            this.stage.setScene(scene);
            this.stage.setMaximized(false);
            this.stage.setMaximized(true);
            this.stage.setTitle("Buscar Organizador - Event Planner");
            
            System.out.println("[MenuOrganizadores] Búsqueda abierta");
            
        } catch (Exception e) {
            System.err.println("[MenuOrganizadores] Error al abrir búsqueda:");
            e.printStackTrace();
            mostrarInfo("Búsqueda de Organizadores", 
                "Puedes buscar organizadores desde las pestañas principales usando los filtros.");
        }
    }

    /**
     * Muestra el historial completo con filtros avanzados
     */
    @FXML
    private void verHistorialCompleto() {
        if (tabTodos != null && tabPane != null) {
            tabPane.getSelectionModel().select(tabTodos);
        } else {
            mostrarInfo("Historial", 
                "El historial está disponible en las pestañas principales.");
        }
    }
    
    /**
     * Vuelve al menú principal
     */
    @FXML
    private void volverMenuPrincipal() {
        if (stage == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            
            MenuPrincipalController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            Scene scene = new Scene(root);
            NavigationHelper.aplicarCSS(scene);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.setTitle("Event Planner - Menú Principal");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ==================== MENSAJES AL USUARIO ====================
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}