/**
 * Excepcion personalizada.
 *  Hereda de la clase Padre EventPlannerException.
 *  Excepcion lanzada cuando no se encuentra un evento con el ID especificado.
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package excepciones;
public class EventoNoEncontradoException extends EventPlannerException {   
     /**
     * Constructor que crea la excepcion con el ID del evento no encontrado.
     * 
     * @param eventoId ID del evento que no se encontró en el sistema
     */
    public EventoNoEncontradoException(String eventoId) {
        super("Evento no encontrado con ID" +eventoId, "ERR_EVENTO_s001 ");
     }
    
}
