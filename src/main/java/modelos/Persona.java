
/**
 * Clase abstracta base para todos los usuarios del sistema Event Planner.
 * Define los atributos y comportamientos comunes a Organizadores y Participantes.
 * 
 * @author [Ayner Jose Castro Benavides]
 * @version 1.0
 */

package modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


/*Creacion de Clase Padre Abstracta */
public abstract class Persona implements Serializable {
    
   protected String id;
   protected String nombre;
   protected String email;
   protected String telefono;
   protected LocalDateTime fechaRegistro;

   

    public Persona(String nombre, String email, String telefono) {
        this.id = generarId();  // Se genera automáticamente
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = LocalDateTime.now();  // Se asigna automáticamente
    }
    
    
    /*Creacion de metodos abstractos aplicando Polimorfismo al mismo tiempo */
    public abstract String obtenerRol();
    public abstract String obtenerDescripcion();
        
    /*Genera un codigo USR mas la hora actual en milisegundos*/
    private String generarId() {
        return "USR" + System.currentTimeMillis();
    }
    
    
    /*GETTERS AND SETTERS */
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

   /*toString con Stringbilder con metodo append para agregar datos al final de un metodo e
    * existente, de ahi el Override.
    */
   @Override
   public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─────────────────────────────────────┐\n");
        sb.append("│ ").append(obtenerRol().toUpperCase()).append("\n");
        sb.append("├─────────────────────────────────────┤\n");
        sb.append("│ ID: ").append(id).append("\n");
        sb.append("│ Nombre: ").append(nombre).append("\n");
        sb.append("│ Email: ").append(email).append("\n");
        sb.append("│ Telefono: ").append(telefono).append("\n");
        sb.append("│ Registro: ").append(fechaRegistro.format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        sb.append("└─────────────────────────────────────┘");
        return sb.toString();
    }

    /**
     *
     * @return
     */

     /*hashcode unicamente para id con el fin de  representar el objeto con un 
      * numero entero(hash) para ubicar rapidamente al objeto.
      */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

     @Override
    public boolean equals(Object obj) {
        /*Si el objeto comparado es el mismo (misma referencia), devuelve true */
        if (this == obj) return true;
        /*Si el objeto comparado es null o no es de la misma clase, devuelve false */
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Persona persona = (Persona) obj;
        /*Se convierte el objeto a Persona */
        return Objects.equals(id, persona.id);
    }
   
}
