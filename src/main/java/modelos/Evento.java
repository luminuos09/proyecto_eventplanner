/**
 * Evento - Representa un evento del sistema Event Planner.
 * Gestiona registros, asistencia, capacidad y agenda.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.1
 */
package modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Evento implements Serializable {
    
    /** ID único del evento (inmutable) */
    private final String id;
    
    /** Nombre del evento */
    private String nombre;
    
    /** Descripción detallada */
    private String descripcion;
    
    /** Tipo de evento - determina capacidad sugerida (inmutable) */
    private final TipoEvento tipo;
    
    /** Fecha de inicio del evento */
    private LocalDateTime fechaInicio;
    
    /** Fecha de finalización del evento */
    private LocalDateTime fechaFin;
    
    /** Ubicación donde se realizará */
    private String ubicacion;
    
    /** Capacidad máxima de participantes (inmutable) */
    private final int capacidadMaxima;
    
    /** Estado actual del evento (mutable) */
    private EstadoEvento estado;
    
    /** ID del organizador responsable (inmutable) */
    private final String organizadorId;
    
    /** Lista de IDs de participantes registrados */
    private final ArrayList<String> participantesRegistrados;
    
    /** Lista de IDs de participantes que hicieron check-in */
    private final ArrayList<String> participantesAsistentes;
    
    /** Items de la agenda del evento */
    private final ArrayList<String> agenda;

    /**
     * Constructor del Evento.
     * Inicializa un evento con estado BORRADOR y listas vacías.
     * 
     * @param nombre  
     * @param descripcion  
     * @param tipo  
     * @param fechaInicio  
     * @param fechaFin  
     * @param ubicacion
     * @param capacidadMaxima Capacidad personalizada (ignora la sugerida por tipo)
     * @param organizadorId 
     */
    public Evento(String nombre, String descripcion, TipoEvento tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin, String ubicacion, int capacidadMaxima, Organizador organizadorId) {
        this.id = generarId();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ubicacion = ubicacion;
        this.capacidadMaxima = capacidadMaxima;  // Usa el parámetro, no tipo.getCapacidadSugerida()
        this.estado = EstadoEvento.BORRADOR;
        this.organizadorId = organizadorId.getId();
        this.participantesRegistrados = new ArrayList<>();
        this.participantesAsistentes = new ArrayList<>();
        this.agenda = new ArrayList<>();
    }

    /**
     * Registra un participante en el evento.
     * Valida: ID no nulo, no duplicado, capacidad disponible, estado válido.
     * 
     * @param participanteId ID del participante a registrar
     * @return true si se registró exitosamente, false si falló alguna validación
     */
    public boolean registrarParticipante(String participanteId) {
        if (participanteId == null || participanteId.trim().isEmpty()) {
            return false;
        }
        
        if (participantesRegistrados.contains(participanteId)) {
            return false;
        }
        
        if (participantesRegistrados.size() >= capacidadMaxima) {
            return false;
        }

        if (estado == EstadoEvento.CANCELADO || estado == EstadoEvento.FINALIZADO) {
            return false;
        }

        participantesRegistrados.add(participanteId);
        return true;
    }

    /**
     * Realiza el check-in de un participante en el evento.
     * Valida: debe estar registrado, no debe haber hecho check-in ya, 
     * el evento debe estar en curso o publicado.
     * 
     * @param participanteId ID del participante
     * @return true si hizo check-in exitosamente, false si falló
     */
    public boolean realizarCheckIn(String participanteId) {
        if (!participantesRegistrados.contains(participanteId)) {
            return false;
        }

        if (participantesAsistentes.contains(participanteId)) {
            return false;
        }

        if (estado != EstadoEvento.EN_CURSO && estado != EstadoEvento.PUBLICADO) {
            return false;
        }

        participantesAsistentes.add(participanteId);
        return true;
    }

    /**
     * Agrega un ítem a la agenda del evento.
     * 
     * @param item Descripción del ítem de agenda
     */
    public void agregarAgenda(String item) {
        if (item != null && !item.trim().isEmpty()) {
            agenda.add(item);
        }
    }

    /**
     * Calcula el porcentaje de asistencia real vs registros.
     * 
     * @return Porcentaje de asistencia (0-100)
     */
    public double calcularPorcentajeAsistencia() {
        if (participantesRegistrados.isEmpty()) {
            return 0.0;
        }
        
        return (double) participantesAsistentes.size() / participantesRegistrados.size() * 100;
    }
    
    /**
     * Verifica si el evento tiene cupo disponible.
     * 
     * @return true si hay espacio, false si está lleno
     */
    public boolean tieneCupoDisponible() {
        return participantesRegistrados.size() < capacidadMaxima;
    }

    /**
     * Obtiene la cantidad de cupos disponibles.
     * 
     * @return Número de espacios libres
     */
    public int getCuposDisponibles() {
        return capacidadMaxima - participantesRegistrados.size();
    }

    /**
     * Genera un ID único para el evento usando timestamp.
     * 
     * @return ID en formato "EVT" + timestamp
     */
    private String generarId() {
        return "EVT" + System.currentTimeMillis();
    }
    
    // ============== GETTERS Y SETTERS ==============
    
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Retorna la fecha de inicio formateada.
     * 
     * @return String con formato dd/MM/yyyy HH:mm o "No asignada"
     */
    public String getFechaInicioFormateada() {
        if (fechaInicio != null) {
            return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "No asignada";
    }
    
    /**
     * Retorna la fecha de fin formateada.
     * 
     * @return String con formato dd/MM/yyyy HH:mm o "No asignada"
     */
    public String getFechaFinFormateada() {
        if (fechaFin != null) {
            return fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "No asignada";
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public String getOrganizadorId() {
        return organizadorId;
    }

    /**
     * Retorna copia de la lista para proteger encapsulación.
     * 
     * @return ArrayList con copia de los IDs de participantes registrados
     */
    public ArrayList<String> getParticipantesRegistrados() {
        return new ArrayList<>(participantesRegistrados);
    }

    public ArrayList<String> getParticipantesAsistentes() {
        return new ArrayList<>(participantesAsistentes);
    }

    public ArrayList<String> getAgenda() {
        return new ArrayList<>(agenda);
    }

    /**
     * Compara dos eventos por su ID único.
     * 
     * @param obj Objeto a comparar
     * @return true si tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evento evento = (Evento) obj;
        return Objects.equals(id, evento.id);
    }

    /**
     * Genera código hash basado únicamente en el ID.
     * Consistente con equals() para representar el objeto con un
     * número entero (hash) para ubicar rápidamente al objeto.
     * 
     * @return código hash del evento
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Genera resumen visual completo del evento con diseño ASCII.
     * Utiliza StringBuilder para construcción eficiente.
     * PROTEGIDO CONTRA NULL - No lanzará NullPointerException
     * 
     * @return String formateado con toda la información del evento
     */
    public String generarResumen() {
        StringBuilder resumen = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        resumen.append("====================================================================\n");
        resumen.append("                       RESUMEN DEL EVENTO                          \n");
        resumen.append("====================================================================\n");
        resumen.append(" ID: ").append(String.format("%-50s", id)).append("    \n");
        resumen.append(" Nombre: ").append(String.format("%-46s", nombre != null ? nombre : "Sin nombre")).append(" \n");
        resumen.append(" Tipo: ").append(String.format("%-48s", tipo != null ? tipo.toString() : "Sin tipo")).append("\n");
        resumen.append(" Estado: ").append(String.format("%-46s", estado != null ? estado.toString() : "Sin estado")).append(" \n");
        resumen.append(" Ubicación: ").append(String.format("%-43s", ubicacion != null ? ubicacion : "Sin ubicación")).append(" \n");
        
        // VALIDACIÓN PARA EVITAR NullPointerException
        String fechaInicioStr = (fechaInicio != null) ? fechaInicio.format(formatter) : "No asignada";
        String fechaFinStr = (fechaFin != null) ? fechaFin.format(formatter) : "No asignada";
        
        resumen.append(" Inicio: ").append(String.format("%-46s", fechaInicioStr)).append(" \n");
        resumen.append(" Fin: ").append(String.format("%-49s", fechaFinStr)).append(" \n");
        resumen.append("====================================================================\n");
        resumen.append("                        ESTADÍSTICAS                       \n");
        resumen.append("====================================================================\n");
        resumen.append(" Capacidad: ").append(String.format("%-43s", capacidadMaxima + " personas")).append(" \n");
        resumen.append(" Registrados: ").append(String.format("%-41s", participantesRegistrados.size() + " personas")).append(" \n");
        resumen.append(" Asistentes: ").append(String.format("%-42s", participantesAsistentes.size() + " personas")).append(" \n");
        resumen.append(" Cupos libres: ").append(String.format("%-40s", getCuposDisponibles() + " espacios")).append(" \n");
        resumen.append(" % Asistencia: ").append(String.format("%-40s", String.format("%.1f%%", calcularPorcentajeAsistencia()))).append(" \n");
        resumen.append("====================================================================\n");
        
        return resumen.toString();
    }

    @Override
    public String toString() {
        return generarResumen();
    }
}