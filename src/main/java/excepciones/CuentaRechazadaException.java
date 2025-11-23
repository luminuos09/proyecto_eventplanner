package excepciones;

/**
 * Excepción lanzada cuando un usuario intenta hacer login
 * pero su solicitud de registro fue rechazada.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class CuentaRechazadaException extends EventPlannerException {
    
    public CuentaRechazadaException(String mensaje) {
        super(mensaje, "CUENTA_RECHAZADA");
    }
}