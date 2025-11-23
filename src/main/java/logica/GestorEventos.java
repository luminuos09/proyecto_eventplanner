/**
 * GestorEventos - Coordinar todas las operaciones del sistema.
 * Esta es la clase principal que coordina los repositorios y aplica
 * las reglas de negocio del sistema Event Planner
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package logica;

import persistencia.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import excepciones.*;
import modelos.*;

public class GestorEventos {
    
     // ============== PATRÓN SINGLETON ==============
     private static GestorEventos instance;
    
    /**
     * Obtiene la única instancia de GestorEventos
     * @return Instancia única de GestorEventos
     */
    public static GestorEventos getInstance() {
        if (instance == null) {
            instance = new GestorEventos();
        }
        return instance;
    }
    
     // Repositorios (capa de persistencia)
    private final EventoRepositorio eventoRepo;
    private final OrganizadorRepositorio organizadorRepo;
    private final ParticipanteRepositorio participanteRepo;
// Validador de datos


    /**
     * Constructor por defecto
     * Inicializa todos los repositorios y el validador
     */
    public GestorEventos(){
        this.eventoRepo= new EventoRepositorio();
        this.organizadorRepo= new OrganizadorRepositorio();
        this.participanteRepo = new ParticipanteRepositorio();
    }

    /**
     * Crea un nuevo evento en el sistema
     * @param nombre Nombre del evento
     * @param descripcion Descripcion del evento
     * @param tipo Tipo de evento (enum)
     * @param fechaInicio Fecha y hora de inicio
     * @param fechaFin Fecha y hora de finalización
     * @param ubicacion Ubicación del evento
     * @param capacidadMaxima Capacidad maxima de participantes
     * @param organizadorId ID del organizador que crea el evento
     * @return El evento creado
     * @throws EventPlannerException si hay algún error en la creación
     */
        public Evento crearEvento(String nombre, String descripcion, TipoEvento tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin, String ubicacion, int capacidadMaxima, Organizador organizadorId) throws EventPlannerException {
            try {
            ValidarDatos.validarNombre(nombre);
            ValidarDatos.validarDescripcion(descripcion);
            ValidarDatos.validarFechas(fechaInicio, fechaFin);
            ValidarDatos.validarUbicacion(ubicacion);
            ValidarDatos.validarCapacidad(capacidadMaxima);
            ValidarDatos.validarId("organizadorId", "organizadorId");
            Organizador organizador = organizadorRepo.buscarPorId(organizadorId.getId());
            if(organizador == null){
                throw new DatosInvalidosException("organizadorId","No puede ser vacio");
            }
            Evento nuevoEvento= new Evento(nombre, descripcion, tipo, fechaInicio, fechaFin, ubicacion,capacidadMaxima, organizadorId);
            // Crear el evento
            eventoRepo.agregar(nuevoEvento);
            organizador.crearEvento(nuevoEvento.getId());
            organizadorRepo.actualizar(organizador);
            return nuevoEvento;
            } catch (IOException e) {
            throw new EventPlannerException("Error al guardar el evento: " + e.getMessage(), e);
            }catch (DatosInvalidosException e){
                throw e;
            }
        }

            /**
             * Busca un evento por su ID
             * 
             * @param eventoId ID del evento
             * @return El evento encontrado
             * @throws EventoNoEncontradoException si el evento no existe
             */
            public Evento buscarEvento(String eventoId)throws EventoNoEncontradoException{
                try {
                    ValidarDatos.validarId(eventoId, "eventoId");
                    return eventoRepo.buscarPorId(eventoId);
                } catch (DatosInvalidosException e) {
                    throw new EventoNoEncontradoException(eventoId);
                }
            }
                /**
                 * Obtiene todos los eventos del sistema
                 * 
                 * @return Lista de todos los eventos
                 */
                public ArrayList<Evento> obtenerTodosEventos(){
                    return eventoRepo.obtenerTodos();
                }

                /**
                 * Busca eventos por estado
                 * 
                 * @param estado Estado del evento (PUBLICADO, EN_CURSO, etc.)
                 * @return Lista de eventos con ese estado
                 */
                public ArrayList<Evento> buscarEventosPorEstado(EstadoEvento estado) {
                return eventoRepo.buscarPorEstado(estado);
                }
                /**
                 * Actualiza la informacion de un evento
                 * 
                 * @param eventoId ID del evento a actualizar
                 * @param nombre Nuevo nombre
                 * @param descripcion Nueva descripción
                 * @param ubicacion Nueva ubicación
                 * @throws EventPlannerException si hay error en la actualización
                 */
                    public void actualizarEvento(String eventoId, String nombre, String descripcion,String ubicacion)throws EventPlannerException{
                        try {
                            Evento evento = buscarEvento(eventoId);
                            if(evento.getEstado() == EstadoEvento.FINALIZADO || evento.getEstado() == EstadoEvento.CANCELADO){
                                throw new EventPlannerException("No se puede editar un evento "+evento.getEstado().getDescripcion());
                            }
                            if(nombre!= null && !nombre.isEmpty()){
                                ValidarDatos.validarNombre(nombre);
                                evento.setNombre(nombre);
                            }
                            if(descripcion!= null && !descripcion.isEmpty()){
                                ValidarDatos.validarDescripcion(descripcion);
                                evento.setDescripcion(descripcion);
                            }
                            if(ubicacion!= null && !ubicacion.isEmpty()){
                                ValidarDatos.validarUbicacion(ubicacion);
                                evento.setUbicacion(ubicacion);
                            }
                            eventoRepo.actualizar(evento);
                        } catch (IOException e) {
                            throw new EventPlannerException("Error al actualizar el evento: "+e.getMessage(),e);
                        }
                    }
                    /**
                     * Cancela un evento
                     * 
                     * @param eventoId ID del evento a cancelar
                     * @param organizadorId ID del organizador (para verificar permisos)
                     * @throws EventPlannerException si hay error en la cancelación
                     */
                    public void cancelarEvento(String eventoId, String organizadorId) throws EventPlannerException {
                        try {
                            Evento evento = buscarEvento(eventoId);
                            
                            // Verificar que el organizador sea el dueño del evento
                            if (!evento.getOrganizadorId().equals(organizadorId)) {
                                throw new EventPlannerException("Solo el organizador del evento puede cancelarlo");
                            }
                            
                            // Verificar que no esté ya cancelado o finalizado
                            if (evento.getEstado() == EstadoEvento.CANCELADO) {
                                throw new EventPlannerException("El evento ya está cancelado");
                            }
                            
                            if (evento.getEstado() == EstadoEvento.FINALIZADO) {
                                throw new EventPlannerException("No se puede cancelar un evento finalizado");
                            }
                            
                            // Cambiar estado a cancelado
                            evento.setEstado(EstadoEvento.CANCELADO);
                            eventoRepo.actualizar(evento);
                            
                        } catch (IOException e) {
                            throw new EventPlannerException("Error al cancelar el evento: " + e.getMessage(), e);
                        }
                    }
                        /**
                         * Registra un nuevo participante en el sistema
                         * 
                         * @param nombre Nombre del participante
                         * @param email Email del participante
                         * @param telefono Teléfono del participante
                         * @param empresa Empresa donde trabaja
                         * @param cargo Cargo que ocupa
                         * @param intereses Intereses del participante
                         * @param vip
                         * @return El participante creado
                         * @throws EventPlannerException si hay error en el registro
                         */
                        public Participante registrarParticipante(String nombre, String email, String telefono, String empresa, String cargo, String intereses,boolean vip)throws EventPlannerException {
                            try {
                                // Validaciones de formato
                                ValidarDatos.validarNombre(nombre);
                                ValidarDatos.validarEmail(email);
                                ValidarDatos.validarTelefono(telefono);
                                
                                // Verificar que NO exista el email
                                Participante existente = participanteRepo.buscarPorEmail(email);
                                
                                if (existente != null) {
                                    // SÍ existe - ERROR
                                    throw new EventPlannerException("Ya existe un participante registrado con este email");
                                }
                                
                                // NO existe - Crear participante
                                Participante nuevoParticipante = new Participante(nombre, email, telefono, empresa, cargo,intereses,vip);
                                participanteRepo.agregar(nuevoParticipante);
                                
                                return nuevoParticipante;
                                
                            } catch (IOException e) {
                                throw new EventPlannerException("No se pudo registrar un nuevo participante");
                            }
                        }
                        /**
                         * Inscribe un participante a un evento
                         * 
                         * @param participanteId ID del participante
                         * @param eventoId ID del evento
                         * @throws EventPlannerException si hay error en la inscripción
                         */
                            public void inscribirParticipante(String participanteId, String eventoId)throws EventPlannerException{
                                try {
                                    Participante participante = participanteRepo.buscarPorId(participanteId);
                                    Evento evento = buscarEvento(eventoId);
                                    if(evento.getEstado() != EstadoEvento.PUBLICADO && evento.getEstado()!= EstadoEvento.EN_CURSO){
                                        throw new EventPlannerException("Solo se puede inscribir a eventos publicados en curso");
                                    }
                                    if(!evento.tieneCupoDisponible()){
                                            throw new CapacidadExcedidaException(evento.getNombre(),evento.getCapacidadMaxima());
                                    }
                                    if(participante.getEventosRegistrados().contains(eventoId)){
                                        throw new ParticipanteYaRegistradoException(participante.getNombre(),"Ya esta registrado en este evento");
                                    }
                                    var registrado = evento.registrarParticipante(participanteId);
                                    if(!registrado){
                                        throw new EventPlannerException("No se pudo registrar el participante al evento");
                                    }
                                    participante.registrarseEvento(eventoId);
                                    eventoRepo.actualizar(evento);
                                    participanteRepo.actualizar(participante);
                                } catch (IOException e) {
                                    throw new EventPlannerException("Error al inscribir participante: "+e.getMessage(),e);
                                }
                            }
                            /**
                             * Cancela la inscripción de un participante a un evento
                             * 
                             * @param participanteId 
                             * @param eventoId 
                             * @throws EventPlannerException si hay error en la cancelación
                            * @throws java.io.IOException
                             */
                                public void cancelarInscripcion(String participanteId, String eventoId)throws EventPlannerException, IOException{
                                    try{
                                        Participante participante = participanteRepo.buscarPorId(participanteId);
                                        Evento evento = buscarEvento(eventoId);
                                        if(!participante.getEventosRegistrados().contains(eventoId)){
                                            throw new EventPlannerException("El participante no esta inscrito en este evento");
                                        }
                                        if(evento.getEstado() == EstadoEvento.EN_CURSO || evento.getEstado() == EstadoEvento.FINALIZADO){
                                            throw new EventPlannerException("No se pudo cancelar la inscripcion a este evento ",evento.getEstado().getDescripcion());
                                        }
                                        // Cancelar inscripcion
                                        participante.cancelarRegistro(eventoId);
                                        evento.getParticipantesRegistrados().remove(participanteId);
                                          // Actualizar repositorios
                                        participanteRepo.actualizar(participante);
                                        eventoRepo.actualizar(evento);
                                    }catch(IOException e){
                                        throw new EventPlannerException("Error al cancelar la inscripcion: "+e.getMessage(),e);
                                    }
                                }
                                    /**
                                     * Realiza el check-in de un participante en un evento
                                     * 
                                     * @param participanteId 
                                     * @param eventoId 
                                     * @throws EventPlannerException si hay error en el check-in
                                     */
                                    public void realizarCheckIn(String participanteId, String eventoId)throws EventPlannerException{
                                        try {
                                            Participante participante = participanteRepo.buscarPorId(participanteId);
                                            Evento evento = buscarEvento(eventoId);
                                            if(!participante.getEventosRegistrados().contains(eventoId)){
                                                throw new EventPlannerException("El participante no esta inscrito en este evento");
                                            }
                                            if(evento.getEstado() != EstadoEvento.EN_CURSO){
                                                throw new EventPlannerException("Solo puede hacer Check-In en eventos en curso");
                                            }
                                            var checkInExitoso = evento.realizarCheckIn(participanteId);
                                            if(!checkInExitoso){
                                                throw new EventPlannerException("Ya se realizo check-In anteriormente");
                                            }
                                            eventoRepo.actualizar(evento);
                                        } catch (IOException e) {
                                            throw new EventPlannerException("No se pudo realizar el Check-In de forma exitosa");
                                        }     
                                    }
                                        /**
                                         * Obtiene todos los participantes del sistema
                                         * 
                                         * @return Lista de todos los participantes
                                         */
                                        public ArrayList<Participante> obtenerTodosParticipantes(){
                                            return participanteRepo.obtenerTodos();
                                        }
                                            /**
                                             * Busca un participante por email
                                             * 
                                             * @param email Email del participante
                                             * @return El participante encontrado
                                             * @throws DatosInvalidosException si no se encuentra
                                             */
                                            public Participante buscarParticipantePorEmail(String email)throws DatosInvalidosException{
                                                    Participante participante = participanteRepo.buscarPorEmail(email);
                                                    
                                                    if (participante == null) {
                                                        throw new DatosInvalidosException("No se encontró participante con ese email", email);
                                                    }
                                                return participante;
                                            }
                                            /**
                                             * Busca participantes por nombre (coincidencia parcial)
                                             * 
                                             * @param nombre Texto a buscar en los nombres
                                             * @return Lista de participantes que coinciden
                                             * @throws EventPlannerException Si hay error en la búsqueda
                                             */
                                            public ArrayList<Participante> buscarParticipantesPorNombre(String nombre) 
                                                throws EventPlannerException {
                                                
                                                ArrayList<Participante> todosParticipantes = 
                                                    participanteRepo.obtenerTodos();
                                                
                                                ArrayList<Participante> resultados = new ArrayList<>();
                                                String nombreBuscado = nombre.toLowerCase();
                                                
                                                for (Participante p : todosParticipantes) {
                                                    if (p.getNombre().toLowerCase().contains(nombreBuscado)) {
                                                        resultados.add(p);
                                                    }
                                                }
                                                
                                                return resultados;
                                            }

                                            /**
                                                     * Busca un participante por ID
                                                     */
                                                    public Participante buscarParticipante(String id) throws EventPlannerException {
                                                        return participanteRepo.buscarPorId(id);
                                                    }
                                                /**
                                                 * Registra un nuevo organizador en el sistema
                                                 * 
                                                 * @param nombre Nombre del organizador
                                                 * @param email Email del organizador
                                                 * @param telefono Teléfono del organizador
                                                 * @param organizacion Organización a la que pertenece
                                                 * @param departamento Departamento
                                                 * @param experienciaAnios Años de experiencia
                                                 * @return El organizador creado
                                                 * @throws EventPlannerException si hay error en el registro
                                                 */
                                            public Organizador registrarOrganizador(String nombre, String email, String telefono, String organizacion,String departamento, int experienciaAnios)throws EventPlannerException{
                                                try {
                                                    ValidarDatos.validarNombre(nombre);
                                                    ValidarDatos.validarEmail(email);
                                                    ValidarDatos.validarTelefono(telefono);
                                                    ValidarDatos.validarExperiencia(experienciaAnios);
                                                    Organizador existente = organizadorRepo.buscarPorEmail(email);
                                                    if(existente != null){
                                                        throw new EventPlannerException("Ya existe un organizador registrado con ese email");
                                                    }
                                                    
                                                    Organizador nuevoOrganizador = new Organizador(nombre, email, telefono, organizacion, departamento,      experienciaAnios);
                                                    organizadorRepo.agregar(nuevoOrganizador);
                                                    return  nuevoOrganizador;
                                                } catch (IOException e) {
                                                    throw new EventPlannerException("Error al registrar un organizador"+e.getMessage(),e);
                                                }
                                            }  
                                                /**
                                                 * Busca un organizador por email
                                                 * 
                                                 * @param email Email del organizador
                                                 * @return El organizador encontrado
                                                 * @throws DatosInvalidosException si no se encuentra
                                                 */
                                            public Organizador buscarOrganizadorPorEmail(String email)throws DatosInvalidosException{
                                                Organizador organizador = organizadorRepo.buscarPorEmail(email);
                                                if(organizador == null){
                                                    throw new DatosInvalidosException("No se encontro organizador con email ", email);
                                                }
                                                return organizador;
                                            }
                                            
                                                public ArrayList<Organizador> buscarOrganizadoresPorNombre(String nombre) 
                                                    throws EventPlannerException {
                                                    
                                                    try {
                                                        ArrayList<Organizador> todos = organizadorRepo.obtenerTodos();
                                                        ArrayList<Organizador> resultados = new ArrayList<>();
                                                        String nombreBuscado = nombre.toLowerCase();
                                                        
                                                        for (Organizador org : todos) {
                                                            if (org.getNombre().toLowerCase().contains(nombreBuscado)) {
                                                                resultados.add(org);
                                                            }
                                                        }
                                                        
                                                        return resultados;
                                                        
                                                    } catch (Exception e) {
                                                        throw new EventPlannerException("Error al buscar organizadores por nombre: " + e.getMessage());
                                                    }
                                                }
                                                /**
                                                 * Busca un organizador por ID
                                                 */
                                                public Organizador buscarOrganizador(String id) throws EventPlannerException {
                                                    try {
                                                        return organizadorRepo.buscarPorId(id);
                                                    } catch (Exception e) {
                                                        throw new EventPlannerException("Error al buscar organizador: " + e.getMessage());
                                                    }
                                                }
                                                /**
                                                 * Obtiene todos los organizadores del sistema
                                                 * 
                                                 * @return Lista de todos los organizadores
                                                 */
                                            public ArrayList<Organizador>obtenerTodosOrganizadores(){
                                                return organizadorRepo.obtenerTodos();
                                            }
                                                /**
                                                 * Obtiene los eventos creados por un organizador especifico
                                                 * 
                                                 * @param organizadorId
                                                 * @return Lista de eventos del organizador
                                                 * @throws EventPlannerException 
                                                 */
                                            public ArrayList<Evento> obtenerEventosDeOrganizador(String organizadorId)throws EventPlannerException{
                                                    ArrayList<Evento> eventosOrganizador= new ArrayList<>();
                                                    ArrayList<Evento> todosEventos = eventoRepo.obtenerTodos();
                                                    for(Evento evento: todosEventos){
                                                        if(evento.getOrganizadorId().equals(organizadorId)){
                                                            eventosOrganizador.add(evento);
                                                        }
                                                    }
                                                    return eventosOrganizador;
                                            }
                                            /**
                                             * Actualiza un evento existente
                                                                                         * @throws IOException 
                                                                                         * @throws EventoNoEncontradoException 
                                             */
                                            public void actualizarEvento(Evento evento) throws EventoNoEncontradoException, IOException {
                                                eventoRepo.actualizar(evento);
                                                System.out.println("[Gestor] Evento actualizado: " + evento.getNombre());
                                            }
                                            /**
                                             * Obtiene estadísticas generales del sistema
                                             * 
                                             * @return String con las estadísticas
                                             */
                                            public String obtenerEstadisticasGenerales() {
                                                    var totalEventos = eventoRepo.contarRegistros();
                                                    var totalParticipantes = participanteRepo.contarRegistros();
                                                    var totalOrganizadores = organizadorRepo.contarRegistros();
                                                    
                                                    var eventosPublicados = buscarEventosPorEstado(EstadoEvento.PUBLICADO).size();
                                                    var eventosEnCurso = buscarEventosPorEstado(EstadoEvento.EN_CURSO).size();
                                                    var eventosFinalizados = buscarEventosPorEstado(EstadoEvento.FINALIZADO).size();
                                                    
                                                    return String.format("""
                                                                         === ESTADISTICAS DEL SISTEMA ===
                                                                         Total de Eventos: %d
                                                                           - Publicados: %d
                                                                           - En Curso: %d
                                                                           - Finalizados: %d
                                                                         Total de Participantes: %d
                                                                         Total de Organizadores: %d
                                                                         """,
                                                        totalEventos, eventosPublicados, eventosEnCurso, eventosFinalizados,
                                                        totalParticipantes, totalOrganizadores
                                                    );
                                                }
                                                /**
                                                 * Limpia todos los datos del sistema (útil para datos de prueba)
                                                 */
                                                public void limpiarTodosLosDatos() {
                                                    eventoRepo.limpiar();
                                                    participanteRepo.limpiar();
                                                    organizadorRepo.limpiar();
                                                }
}