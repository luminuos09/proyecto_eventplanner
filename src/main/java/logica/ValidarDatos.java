/**
 * ValidarDatos - Valida todos los datos de entradad del sistema.
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package logica;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import excepciones.*;

public class ValidarDatos {

    /*Expresiones regulares para validar(Constantes para validar) */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String TELEFONO_REGEX = "^[0-9]{7,15}$";
/* Patrones compilados(mejor compilacion) */
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(TELEFONO_REGEX);
/*Constantes para la validacion de los datos */
    private static final int UBICACION_MIN_LENGTH = 5;
    private static final int UBICACION_MAX_LENGTH = 200;
    private static final int CAPACIDAD_MIN = 1;
    private static final int CAPACIDAD_MAX = 10000;

    public ValidarDatos(){

    }
    /* Valida que los nombres sean correctos */
    public static void validarNombre(String nombre) throws DatosInvalidosException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatosInvalidosException("nombre", "El nombre no puede estar vacío");
        }
        
        if (nombre.trim().length() < 2) {
            throw new DatosInvalidosException("nombre", 
                "El nombre debe tener al menos 2 caracteres");
        }
        
        if (nombre.length() > 100) {
            throw new DatosInvalidosException("nombre", 
                "El nombre no puede exceder 100 caracteres");
        }
        
        // Regex mas permisiva que acepta letras, espacios, tildes, y apóstrofes
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s'-]+$";
        
        if (!nombre.matches(regex)) {
            throw new DatosInvalidosException("nombre", 
                "El nombre solo puede contener letras, espacios, tildes y guiones");
        }
    }
        /*Valida que el email tenga el formato correcto */
        public static void validarEmail(String email) throws DatosInvalidosException {
            if (email == null || email.trim().isEmpty()) {
                throw new DatosInvalidosException("email", "El email no puede estar vacío");
            }
            
            // Usar la constante EMAIL_REGEX
            if (!email.matches(EMAIL_REGEX)) {
                throw new DatosInvalidosException("email", 
                    "El formato del email es invalido. Ejemplo: usuario@dominio.com");
            }
            
            // Validar longitud razonable
            if (email.length() > 100) {
                throw new DatosInvalidosException("email", 
                    "El email no puede exceder 100 caracteres");
            }
        }
            /*Valida que las fechas sean coherentes */
            public static void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatosInvalidosException{
                if(fechaInicio == null){
                    throw new DatosInvalidosException("fechaInicio", "No puede ser vacia");
                }
                if(fechaFin == null){
                    throw new DatosInvalidosException("fecha Final", "No puede ser vacia");
                }
                LocalDateTime ahora = LocalDateTime.now();
                /*La fecha de inicio no puede ser anterior a la actual */
                if(fechaInicio.isBefore(ahora)){
                    throw new DatosInvalidosException("fechaInicio","La fecha de Inicio no puede ser antes de la actual");
                }
                /*La fecha de fin debe ser posterior a la de inicio */
                if(fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)){
                    throw new DatosInvalidosException("fechaFinal", "La fecha de fin debe ser posterior a la de inicio ");
                }
                long duracion=java.time.Duration.between(fechaFin, ahora).toDays();
                if( duracion>30){
                    throw new DatosInvalidosException("fecha", "Un evento no puede durar mas de 30 dias");
                }
            }
                /* Valida que la ubicaciun sea correcta */
                public static void validarUbicacion(String ubicacion)throws DatosInvalidosException{
                    if(ubicacion==null || ubicacion.trim().isEmpty()){
                        throw new DatosInvalidosException("ubicacion", "No puede ser vacia");
                    }
                    var ubicacionLimpia = ubicacion.trim();
                    if(ubicacionLimpia.length()<UBICACION_MIN_LENGTH){
                        throw new DatosInvalidosException("ubicacion", "La ubicacion no puede ser menos"+UBICACION_MIN_LENGTH+" caracteres");
                    }
                    if(ubicacionLimpia.length()>UBICACION_MAX_LENGTH){
                        throw new DatosInvalidosException("ubicacion", "La ubicacion no debe exceder "+UBICACION_MAX_LENGTH+" caracteres");
                    }
                }
                    /*Valida que el telefono tenga el formato correcto */
                    public static void validarTelefono(String telefono) throws DatosInvalidosException{
                        if(telefono==null || telefono.trim().isEmpty()){
                        throw new DatosInvalidosException("telefono", "No puede ser vacio");
                        }   
                        var telefonoLimpio = telefono.replaceAll("[\\s\\-()]", "");
                        if (telefono.startsWith("+")) {
                            telefonoLimpio= telefonoLimpio.substring(1);
                        }
                        if(!TELEFONO_PATTERN.matcher(telefonoLimpio).matches()){
                            throw new DatosInvalidosException("telefono", "El telefono debe contener entre 7 y 15 digitos");
                        }
                    }
                        /*Valida que la capacidad sea correcta */
                        public static void validarCapacidad(int capacidad) throws DatosInvalidosException {
                            if (capacidad < CAPACIDAD_MIN) {
                                throw new DatosInvalidosException("capacidad", 
                                    "La capacidad mínima es " + CAPACIDAD_MIN);
                            }
                            
                            if (capacidad > CAPACIDAD_MAX) {
                                throw new DatosInvalidosException("capacidad", 
                                    "La capacidad máxima es " + CAPACIDAD_MAX);
                            }
                        }
                                /* Vaklida que la descripcion sea correcta, que no este vacia */
                                public static void validarDescripcion(String descripcion) throws DatosInvalidosException {
                                    if (descripcion == null || descripcion.trim().isEmpty()) {
                                        throw new DatosInvalidosException("descripcion", 
                                            "La descripción no puede estar vacía");
                                    }
                                    
                                    if (descripcion.trim().length() < 5) {
                                        throw new DatosInvalidosException("descripcion", 
                                            "La descripción debe tener al menos 10 caracteres");
                                    }
                                    
                                    if (descripcion.length() > 1000) {
                                        throw new DatosInvalidosException("descripcion", 
                                            "La descripción no puede exceder 1000 caracteres");
                                    }
                                }
                                    /*Valida que el ID no este vacio */
                                    public static void validarId(String id, String nombreCampo) throws DatosInvalidosException {
                                        if (id == null || id.trim().isEmpty()) {
                                            throw new DatosInvalidosException(nombreCampo, 
                                                "El " + nombreCampo + " no puede estar vacío");
                                        }
                                    }
                                        /*Valida años de experiencia */
                                        public static void validarExperiencia(int experiencia) throws DatosInvalidosException {
                                            if (experiencia < 0) {
                                                throw new DatosInvalidosException("experiencia", 
                                                    "Los años de experiencia no pueden ser negativos");
                                            }
                                            
                                            if (experiencia > 50) {
                                                throw new DatosInvalidosException("experiencia", 
                                                    "Los años de experiencia parecen excesivos (máximo 50)");
                                            }

                                        }  /*METODOS UTILIZADOS PARA CAPITALIZAR Y ELIMINAR ESPACIOS , OSEA PARA NORMALIZAR*/
                                            public static String normalizarTexto(String texto) {
                                                if (texto == null) {
                                                    return "";
                                                }
                                                return texto.trim().replaceAll("\\s+", " ");
                                            }

                                                public static String normalizarEmail(String email) {
                                                    if (email == null) {
                                                        return "";
                                                    }
                                                    return email.trim().toLowerCase();
                                                }
    }







