/**
 * Participaante - Usuario que se registra en la plataforma y asiste a eventos.
 * Hereda de Persona e implementa comportamientos específicos para
 * la asistencia a eventos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;


import java.util.ArrayList;

public class Participante extends Persona{
    
    private String empresa;
    private String cargo;
    private String intereses;
    private boolean vip;
    private final ArrayList<String>eventosRegistrados;

    

public Participante(String nombre, String email, String telefono, String empresa, String cargo,String intereses,boolean vip) {
    super(nombre, email, telefono);  
    this.empresa = empresa;
    this.cargo = cargo;
    this.intereses = intereses;  //Vacio inicialmente
    this.vip = vip;  // No VIP por defecto
    this.eventosRegistrados = new ArrayList<>(); //Lista vacia
    } 

     // METODOS ABSTRACTOS IMPLEMENTAD
    /**
     * Obtiene el rol del organizador según su experiencia.
     * Clasifica en Participante VIP o Participante Regular
     * 
     * @return String con Rol de participante
     */
    @Override
    public String obtenerRol(){
        if (vip) {
            return "Participante VIP";
        } 
        else 
        {
            return "Participante Regular";
        }
    }

        /**
     * Genera una descripción resumida del participante.
     * 
     * @return String con la cantidad de eventos registrados por el participante.
     */ 

    @Override
    public String obtenerDescripcion(){
        return cargo +" en "+ empresa+ 
        "|Eventos Registrados: "+ eventosRegistrados.size();
    }


   
    // METODOS DE NEGOCIO
    /**
     * Registra un evento cual se registro el participante.Solo almacena el ID del evento, no crea el objeto Evento.
     * 
     * @param eventoID
     * @return true si se registro exitosamente, false si el ID es invalido
     * 
     */

    public boolean registrarseEvento(String eventoID){
        if(eventoID==null|| eventoID.trim().isEmpty()){
            return false;
        }
        else
        
        if(eventosRegistrados.contains(eventoID)){
            return false;
        }
        eventosRegistrados.add(eventoID);
        return true;

    }
    
    
    public boolean cancelarRegistro(String eventoId){
        return eventosRegistrados.remove(eventoId);        
    }

/* 
  Utiliza un StringBuilder para construir de forma eficiente 
  la representación en texto de todos los eventos registrados.
  
  Dentro del bucle for, se agrega para cada evento:
   - Un numero de índice (i + 1) que representa su orden en la lista.
   - Un espacio separador.
   - El ID o nombre del evento obtenido según su posición.
   - Un salto de línea al final para mantener el formato visual.
  
  Finalmente, el contenido del StringBuilder se convierte a texto
  mediante toString() y se devuelve como resultado.
 */

    public String listarEventos(){
        if(eventosRegistrados.isEmpty()){
            return"No tienes eventos registrados";
        }
        
        StringBuilder lista = new StringBuilder();
        lista.append("MIS EVENTOS REGISTRADOS");
        lista.append("════════════════════════════\n");

        for(int i=0; i<eventosRegistrados.size(); i++){
            lista.append(i+1).append(". ").append(eventosRegistrados.get(i)).append("\n");
        }
        return lista.toString();
    }

    
    /*Getters and Setters */
    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public boolean isVip() {
        return this.vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public ArrayList<String> getEventosRegistrados() {
        return new ArrayList<>(eventosRegistrados);
    }

    // METODO SOBREESCRITO
    /**
     * Genera una representación en texto del organizador.
     * Combina información de Persona con datos específicos del Organizador.
     * Utiliza StringBuilder para construcción eficiente.
     * 
     * @return String formateado con toda la informacion del organizador
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n");
        sb.append("┌─ INFORMACION PARTICIPANTE ──────────┐\n");
        sb.append("│ Empresa: ").append(empresa).append("\n");
        sb.append("│ Cargo: ").append(cargo).append("\n");
        sb.append("│ Estado: ").append(vip ? "VIP " : "Regular").append("\n");
        sb.append("│ Eventos registrados: ").append(eventosRegistrados.size()).append("\n");
        if (!intereses.isEmpty()) {
            sb.append("│ Intereses: ").append(intereses).append("\n");
        }
        sb.append("└─────────────────────────────────────┘");
        
        return sb.toString();
    }
}
