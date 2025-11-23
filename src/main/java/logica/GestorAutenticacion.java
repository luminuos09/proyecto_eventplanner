package logica;

import modelos.Usuario;
import modelos.RolUsuario;
import modelos.EstadoUsuario;
import persistencia.UsuarioRepositorio;
import excepciones.*;
import java.io.IOException;

/**
 * Gestor de autenticación y sesiones del sistema.
 * Maneja el login, logout y sesión activa.
 * Implementa patrón Singleton.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class GestorAutenticacion {
    
    private static GestorAutenticacion instancia;
    private UsuarioRepositorio usuarioRepo;
    private Usuario usuarioActual;
    
    /**
     * Constructor privado (Singleton)
     */
    private GestorAutenticacion() {
        this.usuarioRepo = new UsuarioRepositorio();
        this.usuarioActual = null;
    }
    
    /**
     * Obtiene la instancia única del gestor (Singleton)
     * 
     * @return Instancia del GestorAutenticacion
     */
    public static GestorAutenticacion getInstance() {
        if (instancia == null) {
            instancia = new GestorAutenticacion();
        }
        return instancia;
    }
    
    /**
     * Intenta realizar login con las credenciales proporcionadas
     * 
     * @param nombreUsuario Nombre de usuario
     * @param contrasena Contraseña
     * @return true si el login fue exitoso
     * @throws CuentaPendienteException Si la cuenta está pendiente
     * @throws CuentaInactivaException Si la cuenta está inactiva
     * @throws CuentaRechazadaException Si la cuenta fue rechazada
     */
    public boolean login(String nombreUsuario, String contrasena) 
            throws CuentaPendienteException, CuentaInactivaException, CuentaRechazadaException {
        
        // Validar que no haya campos vacíos
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            System.out.println("[Auth] Nombre de usuario vacío");
            return false;
        }
        
        if (contrasena == null || contrasena.trim().isEmpty()) {
            System.out.println("[Auth] Contraseña vacía");
            return false;
        }
        
        // Buscar usuario
        Usuario usuario = usuarioRepo.buscarPorNombreUsuario(nombreUsuario.trim());
        
        if (usuario == null) {
            System.out.println("[Auth] Usuario no encontrado: " + nombreUsuario);
            return false;
        }
        
        // Verificar estado de la cuenta
        if (usuario.getEstado() == EstadoUsuario.PENDIENTE) {
            throw new CuentaPendienteException(
                "Tu cuenta está pendiente de aprobación.\n\n" +
                "Un administrador debe aprobar tu registro antes de que puedas acceder al sistema.\n\n" +
                "Por favor, espera la confirmación."
            );
        }
        
        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new CuentaInactivaException(
                "Tu cuenta ha sido desactivada.\n\n" +
                "Contacta con un administrador para más información."
            );
        }
        
        if (usuario.getEstado() == EstadoUsuario.RECHAZADO) {
            throw new CuentaRechazadaException(
                "Tu solicitud de registro fue rechazada.\n\n" +
                "Contacta con un administrador si crees que esto es un error."
            );
        }
        
        // Validar contraseña
        if (!usuario.validarContrasena(contrasena)) {
            System.out.println("[Auth] Contraseña incorrecta para: " + nombreUsuario);
            return false;
        }
        
        // Login exitoso
        this.usuarioActual = usuario;
        usuario.registrarAcceso();
        usuarioRepo.actualizar(usuario);
        
        System.out.println("[Auth] Login exitoso: " + usuario.getNombreCompleto());
        System.out.println("[Auth] Rol: " + usuario.getRol().getNombre());
        System.out.println("[Auth] Estado: " + usuario.getEstado().getDescripcion());
        
        return true;
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param nombreCompleto Nombre completo
     * @param email Email
     * @param nombreUsuario Nombre de usuario
     * @param contrasena Contraseña
     * @param rol Rol del usuario
     * @throws IOException Si hay error al guardar o usuario duplicado
     */
    public void registrarUsuario(String nombreCompleto, String email, String nombreUsuario,
                                String contrasena, RolUsuario rol) throws IOException {
        
        // Validaciones
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IOException("El nombre completo es obligatorio");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IOException("El email es obligatorio");
        }
        
        if (!email.contains("@")) {
            throw new IOException("Email inválido");
        }
        
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IOException("El nombre de usuario es obligatorio");
        }
        
        if (nombreUsuario.length() < 4) {
            throw new IOException("El nombre de usuario debe tener al menos 4 caracteres");
        }
        
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IOException("La contraseña es obligatoria");
        }
        
        if (contrasena.length() < 6) {
            throw new IOException("La contraseña debe tener al menos 6 caracteres");
        }
        
        // Crear y guardar usuario
        Usuario nuevoUsuario = new Usuario(
            nombreCompleto.trim(),
            email.trim().toLowerCase(),
            nombreUsuario.trim().toLowerCase(),
            contrasena,
            rol
        );
        
        usuarioRepo.agregar(nuevoUsuario);
        
        System.out.println("[Auth] Usuario registrado: " + nombreUsuario);
        System.out.println("[Auth] Estado inicial: " + nuevoUsuario.getEstado().getDescripcion());
    }
    
    /**
     * Cierra la sesión actual
     */
    public void logout() {
        if (usuarioActual != null) {
            System.out.println("[Auth] Logout: " + usuarioActual.getNombreUsuario());
            this.usuarioActual = null;
        }
    }
    
    /**
     * Verifica si hay un usuario logueado
     * 
     * @return true si hay sesión activa
     */
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
    
    /**
     * Obtiene el usuario actualmente logueado
     * 
     * @return Usuario actual o null si no hay sesión
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Verifica si el usuario actual es administrador
     * 
     * @return true si es administrador
     */
    public boolean esAdministrador() {
        return usuarioActual != null && 
               usuarioActual.getRol() == RolUsuario.ADMINISTRADOR;
    }
    
    /**
     * Verifica si el usuario actual es organizador
     * 
     * @return true si es organizador
     */
    public boolean esOrganizador() {
        return usuarioActual != null && 
               usuarioActual.getRol() == RolUsuario.ORGANIZADOR;
    }
    
    /**
     * Obtiene el nombre del usuario actual
     * 
     * @return Nombre del usuario o "Invitado" si no hay sesión
     */
    public String getNombreUsuarioActual() {
        if (usuarioActual != null) {
            return usuarioActual.getNombreCompleto();
        }
        return "Invitado";
    }
    
    /**
     * Obtiene el rol del usuario actual
     * 
     * @return Rol del usuario o null si no hay sesión
     */
    public RolUsuario getRolUsuarioActual() {
        if (usuarioActual != null) {
            return usuarioActual.getRol();
        }
        return null;
    }
    
    /**
     * Cambia la contraseña del usuario actual
     * 
     * @param contrasenaActual Contraseña actual
     * @param nuevaContrasena Nueva contraseña
     * @throws IOException Si hay error o contraseña actual incorrecta
     */
    public void cambiarContrasenaActual(String contrasenaActual, String nuevaContrasena) 
            throws IOException {
        
        if (usuarioActual == null) {
            throw new IOException("No hay sesión activa");
        }
        
        if (!usuarioActual.validarContrasena(contrasenaActual)) {
            throw new IOException("La contraseña actual es incorrecta");
        }
        
        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            throw new IOException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        usuarioRepo.cambiarContrasena(usuarioActual.getNombreUsuario(), nuevaContrasena);
        usuarioActual.setContrasena(nuevaContrasena);
        
        System.out.println("[Auth] Contraseña cambiada para: " + usuarioActual.getNombreUsuario());
    }
    
    /**
     * Obtiene el repositorio de usuarios
     * SOLO para uso administrativo
     * 
     * @return UsuarioRepositorio
     */
    public UsuarioRepositorio getUsuarioRepositorio() {
        return usuarioRepo;
    }
    
    /**
     * Obtiene información de la sesión actual
     * 
     * @return String con información de la sesión
     */
    public String getInfoSesion() {
        if (usuarioActual == null) {
            return "Sin sesión activa";
        }
        
        return String.format("Usuario: %s | Rol: %s | Estado: %s | Último acceso: %s",
            usuarioActual.getNombreCompleto(),
            usuarioActual.getRol().getNombre(),
            usuarioActual.getEstado().getDescripcion(),
            usuarioActual.getUltimoAccesoFormateado()
        );
    }
    
    /**
     * Verifica si el usuario actual tiene permisos de administrador
     * Lanza excepción si no los tiene
     * 
     * @throws SecurityException Si no tiene permisos
     */
    public void verificarPermisosAdmin() throws SecurityException {
        if (!esAdministrador()) {
            throw new SecurityException("Se requieren permisos de administrador");
        }
    }
}