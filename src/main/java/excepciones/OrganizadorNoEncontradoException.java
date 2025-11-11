/**
 * Excepción lanzada cuando no se encuentra un organizador con el ID especificado.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package excepciones;

public class OrganizadorNoEncontradoException extends EventPlannerException {
    
    /**
     * Constructor que crea la excepción con el ID del organizador no encontrado.
     * 
     * @param organizadorId ID del organizador que no se encontró
     */
    public OrganizadorNoEncontradoException(String organizadorId) {
        super("Organizador no encontrado con ID: " + organizadorId, "ERR_ORGANIZADOR_005");
    }
}