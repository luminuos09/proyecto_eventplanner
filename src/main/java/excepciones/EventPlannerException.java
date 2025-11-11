/**
 * Excepción base del sistema Event Planner.
 * Todas las excepciones personalizadas heredan de esta clase.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package excepciones;


public class EventPlannerException extends Exception {
   private String codigoError;
    
   
     /**
     * Constructor con solo mensaje de error.
     * 
     * @param mensaje Descripcion del error
     */
    public EventPlannerException(String mensaje) {
         super(mensaje);
    }

     /**
     * Constructor con solo mensaje de error.
     * 
     * @param mensaje
     * @param codigoError Código único del error
     */
    
    public EventPlannerException(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }
    public EventPlannerException(String message, Throwable cause) {
    super(message, cause);
    }

     /**
     * Constructor con mensaje, código de error y causa raíz.
     * Útil para encadenar excepciones.
     * 
     * @param mensaje  
     * @param codigoError 
     * @param causa Excepción que causo este error
     */
    public EventPlannerException(String codigoError, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError=codigoError;
    }
   
    /**
     * Obtiene el codigo de error asociado a la esta exception.
     * @return El codigo o null.
     */
    public String getCodigoError() {
        return codigoError;
    } 
}
