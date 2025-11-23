package modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Clase que representa un usuario del sistema Event Planner.
 * Almacena credenciales y roles para control de acceso.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String nombreCompleto;
    private String email;
    private String nombreUsuario;
    private String contrasena;
    private RolUsuario rol;
    private EstadoUsuario estado; 
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
    private boolean activo;
    
    /**
     * Constructor completo de Usuario
     * 
     * @param nombreCompleto Nombre completo del usuario
     * @param email Correo electrónico
     * @param nombreUsuario Usuario para login
     * @param contrasena Contraseña (se guardará encriptada)
     * @param rol Rol del usuario (ORGANIZADOR o ADMIN)
     */
    public Usuario(String nombreCompleto, String email, String nombreUsuario, 
                   String contrasena, RolUsuario rol) {
        this.id = generarId();
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = encriptarContrasena(contrasena);
        this.rol = rol;
        
        this.estado = (rol == RolUsuario.ADMINISTRADOR) 
            ? EstadoUsuario.ACTIVO 
            : EstadoUsuario.PENDIENTE;
        
        this.fechaCreacion = LocalDateTime.now();
        this.ultimoAcceso = null;
        this.activo = true;
    }
    
    /**
     * Constructor para crear usuario con ID existente (al cargar de archivo)
     */
    public Usuario(String id, String nombreCompleto, String email, String nombreUsuario,
                   String contrasena, RolUsuario rol, EstadoUsuario estado,
                   LocalDateTime fechaCreacion, LocalDateTime ultimoAcceso, boolean activo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado; 
        this.fechaCreacion = fechaCreacion;
        this.ultimoAcceso = ultimoAcceso;
        this.activo = activo;
    }
    
    /**
     * Genera un ID único para el usuario
     * 
     * @return ID único basado en timestamp y nombre de usuario
     */
    private String generarId() {
        return "USR" + System.currentTimeMillis();
    }
    
    /**
     * Encripta la contraseña usando un hash simple
     * NOTA: En producción usar BCrypt o similar
     * 
     * @param contrasena Contraseña en texto plano
     * @return Contraseña encriptada
     */
    private String encriptarContrasena(String contrasena) {
        return Integer.toString(contrasena.hashCode());
    }
    
    /**
     * Valida si una contraseña coincide con la almacenada
     * 
     * @param contrasena Contraseña a validar
     * @return true si coincide, false en caso contrario
     */
    public boolean validarContrasena(String contrasena) {
        String contrasenaEncriptada = encriptarContrasena(contrasena);
        return this.contrasena.equals(contrasenaEncriptada);
    }
    
    /**
     * Registra el acceso del usuario
     */
    public void registrarAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }
    
    /**
     * Obtiene información resumida del usuario
     * 
     * @return String con información del usuario
     */
    public String getInfoResumida() {
        return String.format("%s (%s) - %s - %s", 
            nombreCompleto, 
            nombreUsuario, 
            rol.getNombre(),
            estado.getDescripcion());
    }
    
    /**
     * Obtiene la fecha de último acceso formateada
     * 
     * @return Fecha formateada o "Nunca" si no ha accedido
     */
    public String getUltimoAccesoFormateado() {
        if (ultimoAcceso == null) {
            return "Nunca";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return ultimoAcceso.format(formatter);
    }
    
    // ==================== GETTERS Y SETTERS ====================
    
    public String getId() {
        return id;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = encriptarContrasena(contrasena);
    }
    
    public RolUsuario getRol() {
        return rol;
    }
    
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
    
    // ✅ NUEVOS GETTERS/SETTERS PARA ESTADO
    public EstadoUsuario getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return String.format("Usuario[%s - %s - %s - %s]", 
            nombreUsuario, nombreCompleto, rol.getNombre(), estado.getDescripcion());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(id, usuario.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}