package persistencia;

import modelos.Usuario;
import modelos.RolUsuario;
import modelos.EstadoUsuario;
import com.google.gson.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Repositorio para gestionar la persistencia de usuarios.
 * Guarda y carga usuarios desde archivo JSON.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class UsuarioRepositorio {
    
    private static final String ARCHIVO_USUARIOS = "datos/usuarios.json";
    private ArrayList<Usuario> usuarios;
    private Gson gson;
    
    /**
     * Constructor del repositorio de usuarios
     */
    public UsuarioRepositorio() {
        this.usuarios = new ArrayList<>();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();
        
        crearDirectorioSiNoExiste();
        cargarDesdeArchivo();
        
        // Crear usuario admin por defecto si no existe
        if (usuarios.isEmpty()) {
            crearUsuarioAdminPorDefecto();
        }
    }
    
    /**
     * Crea el directorio de datos si no existe
     */
    private void crearDirectorioSiNoExiste() {
        File directorio = new File("datos");
        if (!directorio.exists()) {
            directorio.mkdirs();
            System.out.println("[UsuarioRepo] Directorio 'datos' creado");
        }
    }
    
    /**
     * Crea un usuario administrador por defecto
     */
    private void crearUsuarioAdminPorDefecto() {
        Usuario admin = new Usuario(
            "Administrador del Sistema",
            "admin@eventplanner.com",
            "admin",
            "admin123",
            RolUsuario.ADMINISTRADOR
        );
        
        usuarios.add(admin);
        guardarEnArchivo();
        
        System.out.println("[UsuarioRepo] Usuario administrador creado");
        System.out.println("Usuario: admin | Contraseña: admin123");
    }
    
    /**
     * Agrega un nuevo usuario al sistema
     * 
     * @param usuario Usuario a agregar
     * @throws IOException Si hay error al guardar
     */
    public void agregar(Usuario usuario) throws IOException {
        if (existeNombreUsuario(usuario.getNombreUsuario())) {
            throw new IOException("El nombre de usuario ya existe");
        }
        
        if (existeEmail(usuario.getEmail())) {
            throw new IOException("El email ya está registrado");
        }
        
        usuarios.add(usuario);
        guardarEnArchivo();
        
        System.out.println("[UsuarioRepo] Usuario agregado: " + usuario.getNombreUsuario());
    }
    
    /**
     * Busca un usuario por nombre de usuario
     * 
     * @param nombreUsuario Nombre de usuario a buscar
     * @return Usuario encontrado o null
     */
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return usuario;
            }
        }
        return null;
    }
    
    /**
     * Busca un usuario por email
     * 
     * @param email Email a buscar
     * @return Usuario encontrado o null
     */
    public Usuario buscarPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }
        return null;
    }
    
    /**
     * Busca un usuario por ID
     * 
     * @param id ID del usuario
     * @return Usuario encontrado o null
     */
    public Usuario buscarPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        return null;
    }
    
    /**
     * Valida las credenciales de un usuario
     * 
     * @param nombreUsuario Nombre de usuario
     * @param contrasena Contraseña
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    public Usuario validarCredenciales(String nombreUsuario, String contrasena) {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);
        
        if (usuario == null) {
            System.out.println("[UsuarioRepo] Usuario no encontrado: " + nombreUsuario);
            return null;
        }
        
        if (!usuario.isActivo()) {
            System.out.println("[UsuarioRepo] Usuario inactivo: " + nombreUsuario);
            return null;
        }
        
        if (!usuario.validarContrasena(contrasena)) {
            System.out.println("[UsuarioRepo] Contraseña incorrecta para: " + nombreUsuario);
            return null;
        }
        
        // Registrar acceso
        usuario.registrarAcceso();
        guardarEnArchivo();
        
        System.out.println("[UsuarioRepo] Login exitoso: " + nombreUsuario);
        return usuario;
    }
    
    /**
     * Verifica si existe un nombre de usuario
     * 
     * @param nombreUsuario Nombre a verificar
     * @return true si existe
     */
    public boolean existeNombreUsuario(String nombreUsuario) {
        return buscarPorNombreUsuario(nombreUsuario) != null;
    }
    
    /**
     * Verifica si existe un email
     * 
     * @param email Email a verificar
     * @return true si existe
     */
    public boolean existeEmail(String email) {
        return buscarPorEmail(email) != null;
    }
    
    /**
     * Obtiene todos los usuarios
     * 
     * @return Lista de usuarios
     */
    public ArrayList<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }
    
    /**
     * Obtiene usuarios por rol
     * 
     * @param rol Rol a filtrar
     * @return Lista de usuarios con ese rol
     */
    public ArrayList<Usuario> obtenerPorRol(RolUsuario rol) {
        ArrayList<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.getRol() == rol) {
                resultado.add(usuario);
            }
        }
        return resultado;
    }
    
    /**
     *       Obtiene usuarios por estado
     * 
     * @param estado Estado a filtrar
     * @return Lista de usuarios con ese estado
     */
    public ArrayList<Usuario> obtenerPorEstado(EstadoUsuario estado) {
        ArrayList<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.getEstado() == estado) {
                resultado.add(usuario);
            }
        }
        return resultado;
    }
    
    /**
     *   Obtiene organizadores pendientes de aprobación
     * 
     * @return Lista de organizadores pendientes
     */
    public ArrayList<Usuario> obtenerOrganizadoresPendientes() {
        ArrayList<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.getRol() == RolUsuario.ORGANIZADOR && 
                usuario.getEstado() == EstadoUsuario.PENDIENTE) {
                resultado.add(usuario);
            }
        }
        return resultado;
    }
    
    /**
     * Actualiza un usuario existente
     * 
     * @param usuario Usuario a actualizar
     */
    public void actualizar(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                guardarEnArchivo();
                System.out.println("[UsuarioRepo] Usuario actualizado: " + usuario.getNombreUsuario());
                return;
            }
        }
    }
    
    /**
     * Aprueba un organizador   
     * 
     * @param usuarioId ID del usuario a aprobar
     * @throws IOException Si el usuario no existe
     */
    public void aprobarOrganizador(String usuarioId) throws IOException {
        Usuario usuario = buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IOException("Usuario no encontrado");
        }
        
        usuario.setEstado(EstadoUsuario.ACTIVO);
        actualizar(usuario);
        
        System.out.println("[UsuarioRepo] Organizador aprobado: " + usuario.getNombreUsuario());
    }
    
    /**
     * 
     * 
     * @param usuarioId ID del usuario a rechazar
     * @throws IOException Si el usuario no existe
     */
    public void rechazarOrganizador(String usuarioId) throws IOException {
        Usuario usuario = buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IOException("Usuario no encontrado");
        }
        
        usuario.setEstado(EstadoUsuario.RECHAZADO);
        actualizar(usuario);
        
        System.out.println("[UsuarioRepo] Organizador rechazado: " + usuario.getNombreUsuario());
    }
    
    /**
     * Cambia la contraseña de un usuario
     * 
     * @param nombreUsuario Usuario
     * @param nuevaContrasena Nueva contraseña
     * @throws IOException Si el usuario no existe
     */
    public void cambiarContrasena(String nombreUsuario, String nuevaContrasena) throws IOException {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);
        if (usuario == null) {
            throw new IOException("Usuario no encontrado");
        }
        
        usuario.setContrasena(nuevaContrasena);
        actualizar(usuario);
    }
    
    /**
     * Guarda los usuarios en archivo JSON
     */
    private void guardarEnArchivo() {
        try (FileWriter writer = new FileWriter(ARCHIVO_USUARIOS)) {
            gson.toJson(usuarios, writer);
            System.out.println("[UsuarioRepo] Usuarios guardados en archivo");
        } catch (IOException e) {
            System.err.println("[UsuarioRepo] Error al guardar: " + e.getMessage());
        }
    }
    
/**
 * Carga los usuarios desde archivo JSON
 */
private void cargarDesdeArchivo() {
    File archivo = new File(ARCHIVO_USUARIOS);
    
    if (!archivo.exists()) {
        System.out.println("[UsuarioRepo] Archivo no existe, se creará uno nuevo");
        return;
    }
    
    try (FileReader reader = new FileReader(archivo)) {
        Usuario[] usuariosArray = gson.fromJson(reader, Usuario[].class);
        
        if (usuariosArray != null) {
            usuarios.clear();
            for (Usuario usuario : usuariosArray) {
                //  MIGRACIÓN: Si el usuario no tiene estado, asignarlo
                if (usuario.getEstado() == null) {
                    if (usuario.getRol() == RolUsuario.ADMINISTRADOR) {
                        usuario.setEstado(EstadoUsuario.ACTIVO);
                    } else {
                        usuario.setEstado(EstadoUsuario.ACTIVO); // Los existentes se activan automáticamente
                    }
                    System.out.println("[UsuarioRepo]  Migrado usuario: " + usuario.getNombreUsuario() + " -> " + usuario.getEstado());
                }
                usuarios.add(usuario);
            }
            
            // Guardar con el nuevo formato
            guardarEnArchivo();
            
            System.out.println("[UsuarioRepo] " + usuarios.size() + " usuarios cargados");
        }
    } catch (IOException e) {
        System.err.println("[UsuarioRepo] Error al cargar: " + e.getMessage());
    }
}
    
    /**
     * Obtiene la cantidad de usuarios registrados
     * 
     * @return Cantidad de usuarios
     */
    public int contarUsuarios() {
        return usuarios.size();
    }
    
    // ==================== ADAPTADOR PARA LocalDateTime ====================
    
    /**
     * Adaptador personalizado para serializar/deserializar LocalDateTime con Gson
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, 
                                    JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }
        
        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, 
                                        JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}