package modelos;

/**
 * Enumeración de estados de usuario en el sistema Event Planner.
 * Define el ciclo de vida de las cuentas de usuario.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public enum EstadoUsuario {
    
    /**
     * Pendiente: Usuario registrado pero pendiente de aprobación por admin
     */
    PENDIENTE("Pendiente de aprobación", "⏳"),
    
    /**
     * Activo: Usuario aprobado y con acceso al sistema
     */
    ACTIVO("Activo", "✅"),
    
    /**
     * Inactivo: Usuario temporalmente desactivado
     */
    INACTIVO("Inactivo", "❌"),
    
    /**
     * Rechazado: Solicitud de registro rechazada por admin
     */
    RECHAZADO("Rechazado", "🚫");
    
    private final String descripcion;
    private final String icono;
    
    /**
     * Constructor del enum EstadoUsuario
     * 
     * @param descripcion Descripción del estado
     * @param icono Icono representativo
     */
    EstadoUsuario(String descripcion, String icono) {
        this.descripcion = descripcion;
        this.icono = icono;
    }
    
    /**
     * Obtiene la descripción del estado
     * 
     * @return Descripción del estado
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Obtiene el icono del estado
     * 
     * @return Icono representativo
     */
    public String getIcono() {
        return icono;
    }
    
    @Override
    public String toString() {
        return icono + " " + descripcion;
    }
}