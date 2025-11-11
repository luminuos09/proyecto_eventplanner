/**
 * Organizador - Usuario que crea y gestiona eventos.
 * Hereda de Persona e implementa comportamientos específicos para
 * la gestión de eventos corporativos y académicos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;

import java.util.ArrayList;

public class Organizador extends Persona {
    
    // ATRIBUTOS
    /** Empresa u organización a la que pertenece el organizador */
    private String organizacion;
    
    /** Departamento o área dentro de la organización */
    private String departamento;
    
    /** Lista de IDs de eventos creados por este organizador */
    private ArrayList<String> eventosCreados;
    
    /** Años de experiencia como organizador de eventos */
    private int experienciaAnios;

    // CONSTRUCTOR
    /**
     * Constructor del Organizador.Inicializa un organizador con experiencia en 0 años y sin eventos creados.
     * 
     * @param nombre Nombre completo del organizador
     * @param email Correo electrónico institucional
     * @param telefono Numero de contacto
     * @param organizacion Empresa u organización
     * @param departamento Area o departamento
     * @param experienciaAnios
     */
    public Organizador(String nombre, String email, String telefono,String organizacion, String departamento,int experienciaAnios) {
        super(nombre, email, telefono);
        this.organizacion = organizacion;
        this.departamento = departamento;
        this.eventosCreados = new ArrayList<>();//Lista vacia
        this.experienciaAnios = experienciaAnios;
    }
    
    // METODOS ABSTRACTOS IMPLEMENTAD
    /**
     * Obtiene el rol del organizador segun su experiencia.
     * Clasifica en Junior (0-1 años),Semi-Senior (2-4 años) o Senior (5+ años).
     * 
     * @return String con el nivel del organizador
     */
    
    @Override
    public String obtenerRol() {
        if (experienciaAnios >= 5) {
            return "Organizador Senior";
        } else if (experienciaAnios >= 2) {
            return "Organizador Semi-Senior";
        } else {
            return "Organizador Junior";
        }
    }
    
    /**
     * Genera una descripción resumida del organizador.
     * 
     * @return String con departamento, organizacion y total de eventos
     */
    @Override
    public String obtenerDescripcion() {
        return departamento + " | " + organizacion + 
               " | " + eventosCreados.size() + " eventos creados";
    }
    
    
    // METODOS DE NEGOCIO
    
    /**
     * Registra un evento creado por este organizador.
     * Solo almacena el ID del evento, no crea el objeto Evento.
     * 
     * @param eventoId ID unico del evento a registrar
     * @return true si se registró exitosamente, false si el ID es inválido
     */
    public boolean crearEvento(String eventoId) {
        if (eventoId == null || eventoId.trim().isEmpty()) {
            return false;
        }
        eventosCreados.add(eventoId);
        return true;
    }
    
    /**
     * Genera un reporte con las estadísticas del organizador.
     * Utiliza StringBuilder para construccion eficiente del reporte.
     * 
     * @return String formateado con estadisticas completas
     */
    
    public String obtenerEstadisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("ESTADISTICAS DEL ORGANIZADOR\n");
        stats.append("════════════════════════════════\n");
        stats.append("Eventos creados: ").append(eventosCreados.size()).append("\n");
        stats.append("Experiencia: ").append(experienciaAnios).append(" años\n");
        stats.append("Nivel: ").append(obtenerRol()).append("\n");
        stats.append("Organizacion: ").append(organizacion).append("\n");
        stats.append("Departamento: ").append(departamento).append("\n");
        return stats.toString();
    }

    
    // GETTERS Y SETTERS
    
    public String getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(String organizacion) {
        this.organizacion = organizacion;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Retorna una copia de la lista de eventos para proteger la encapsulación.
     * 
     * @return ArrayList con copia de los IDs de eventos creados
     */
    public ArrayList<String> getEventosCreados() {
        return new ArrayList<>(eventosCreados);
    }

    public void setEventosCreados(ArrayList<String> eventosCreados) {
        this.eventosCreados = eventosCreados;
    }

    public int getExperienciaAnios() {
        return experienciaAnios;
    }
    
    public void setExperienciaAnios(int experienciaAnios) {
        this.experienciaAnios = experienciaAnios;
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
        sb.append("┌─ INFORMACION ORGANIZADOR ───────────┐\n");
        sb.append("│ Organizacion: ").append(organizacion).append("\n");
        sb.append("│ Departamento: ").append(departamento).append("\n");
        sb.append("│ Experiencia: ").append(experienciaAnios).append(" años\n");
        sb.append("│ Eventos creados: ").append(eventosCreados.size()).append("\n");
        sb.append("│ Nivel: ").append(obtenerRol()).append("\n");
        sb.append("└─────────────────────────────────────┘");
        return sb.toString();
    }
}