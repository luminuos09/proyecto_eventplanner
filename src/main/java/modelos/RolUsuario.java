 package modelos;

/**
 * Enumeración de roles de usuario en el sistema Event Planner.
 * Define los permisos y accesos de cada tipo de usuario.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public enum RolUsuario {
    
    /**
     * Organizador: Puede crear eventos, gestionar participantes y ver reportes
     */
    ORGANIZADOR("Organizador", "Crea y gestiona eventos", "👤"),
    
    /**
     * Administrador: Acceso total al sistema incluyendo gestión de usuarios
     */
    ADMINISTRADOR("Administrador", "Control total del sistema", "👑");
    
    private final String nombre;
    private final String descripcion;
    private final String icono;
    
    /**
     * Constructor del enum RolUsuario
     * 
     * @param nombre Nombre del rol
     * @param descripcion Descripción de permisos
     * @param icono Icono representativo
     */
    RolUsuario(String nombre, String descripcion, String icono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
    }
    
    /**
     * Obtiene el nombre del rol
     * 
     * @return Nombre del rol
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Obtiene la descripción del rol
     * 
     * @return Descripción de permisos
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Obtiene el icono del rol
     * 
     * @return Icono representativo
     */
    public String getIcono() {
        return icono;
    }
    
    /**
     * Verifica si el rol es administrador
     * 
     * @return true si es ADMINISTRADOR
     */
    public boolean esAdministrador() {
        return this == ADMINISTRADOR;
    }
    
    /**
     * Verifica si el rol es organizador
     * 
     * @return true si es ORGANIZADOR
     */
    public boolean esOrganizador() {
        return this == ORGANIZADOR;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return icono + " " + nombre;
    }
}