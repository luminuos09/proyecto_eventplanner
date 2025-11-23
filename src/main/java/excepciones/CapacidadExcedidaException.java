/**
 * Excepcion personalizada.
 *  Hereda de la clase Padre EventPlannerException.
 *  Excepcion lanzada cuando se excede la capacidad en un evento.
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */ 

package excepciones; 
public class CapacidadExcedidaException extends EventPlannerException {
    public CapacidadExcedidaException(String eventoNombre, int capacidad){
     /**
     * Constructor que crea la excepcion la capacidad excedida.
     * @param eventoNombre nombre del evento que excedio la capacidad.
     */
        super("Capacidad excedida en evento "+eventoNombre+
             "|Capacidad Maxima: "+capacidad,"ERR_CAPACIDAD_OO2");
    }
}
