/**
 * Excepcion personalizada.
 *  Hereda de la clase Padre EventPlannerException.
 *  Excepcion lanzada cuando el participante se quiere por segunda vez al mismo evento
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */ 
package excepciones;

public class ParticipanteYaRegistradoException extends EventPlannerException{
     /**
     * Constructor que crea la excepcion de ParticipanteYaRegistrado.
     * @param participanteNombre Nombre del participante registrado por error.
     * @param eventoNombre Nombre del evento al que se quiere realizar una inscripcion invalida.
     */
    
    public ParticipanteYaRegistradoException(String participanteNombre, String eventoNombre){
     super("El participante "+participanteNombre+ "ya esta registrado en el evento" +eventoNombre+ ",","ERR_REGISTRO_OO3");
    }
}
