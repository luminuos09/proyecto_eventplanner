package excepciones;

/**
 * Excepción lanzada cuando un usuario intenta hacer login
 * pero su cuenta ha sido desactivada.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class CuentaInactivaException extends EventPlannerException {
    
    public CuentaInactivaException(String mensaje) {
        super(mensaje, "CUENTA_INACTIVA");
    }
}