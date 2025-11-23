package excepciones;

/**
 * Excepción lanzada cuando un usuario intenta hacer login
 * pero su cuenta está pendiente de aprobación.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class CuentaPendienteException extends EventPlannerException {
    
    public CuentaPendienteException(String mensaje) {
        super(mensaje, "CUENTA_PENDIENTE");
    }
}