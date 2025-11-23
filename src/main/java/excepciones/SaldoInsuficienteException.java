/**
 * SaldoInsuficienteException - Excepción cuando no hay suficiente saldo
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package excepciones;

public class SaldoInsuficienteException extends EventPlannerException {
    
    public SaldoInsuficienteException(double requerido, double disponible) {
        super("Saldo insuficiente. Requerido: $" + String.format("%,.0f", requerido) + 
              ", Disponible: $" + String.format("%,.0f", disponible), 
              "ERR_PAGO_002");
    }
}