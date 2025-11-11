/**
 * PagoRechazadoException - Excepción lanzada cuando un pago es rechazado
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package excepciones;

public class PagoRechazadoException extends EventPlannerException {
    
    public PagoRechazadoException(String razon) {
        super("Pago rechazado: " + razon, "ERR_PAGO_001");
    }
    
    public PagoRechazadoException(String metodoPago, String razon) {
        super("Pago rechazado con " + metodoPago + ": " + razon, "ERR_PAGO_001");
    }
}