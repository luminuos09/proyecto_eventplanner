 /**
 * Excepcion personalizada.
 *  Hereda de la clase Padre EventPlannerException.
 *  Excepcion lanzada cuando se ingresan datos invalidos.
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */ 
package excepciones;

public class DatosInvalidosException extends EventPlannerException {
    
     /**
     * Constructor que crea la excepcion de ParticipanteYaRegistrado.
     * @param campo Campo en el cual se presento el registro Invalido
     * @param razon Razon por la cual el registro es invalido.
     */
    
    public DatosInvalidosException(String campo,String razon){
        super("Datos invalidos en el campo "+ campo +": " + razon,"ERR_VALIDACION:004");
    }
}
