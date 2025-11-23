/**
 * GestorPagos - Gestiona todas las operaciones relacionadas con pagos y tickets
 * Maneja la compra de tickets, procesamiento de pagos y reportes financieros
 * 
 * @author Ayner Jose Castro Benavides
 * @version 2.0 - Con Persistencia
 */
package logica;

import modelos.*;
import excepciones.*;
import persistencia.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestorPagos {
    
    // ==================== REPOSITORIOS ====================
    private final TicketRepositorio ticketRepo;
    private final PagoRepositorio pagoRepo;
    
    private final GestorEventos gestorEventos;
    
    // Configuración de precios por tipo de evento
    private final Map<TipoEvento, Map<TipoTicket, Double>> preciosPersonalizados;
    
    // ==================== SINGLETON ====================
    
    private static GestorPagos instancia;
    
    /**
     * Obtiene la instancia única del GestorPagos
     */
    public static GestorPagos getInstance() {
        if (instancia == null) {
            instancia = new GestorPagos();
        }
        return instancia;
    }
    
    // ==================== CONSTRUCTORES ====================
    
    /**
     * Constructor privado para Singleton
     */
    private GestorPagos() {
        this.gestorEventos = GestorEventos.getInstance();
        this.ticketRepo = new TicketRepositorio();
        this.pagoRepo = new PagoRepositorio();
        this.preciosPersonalizados = new HashMap<>();
        inicializarPreciosBase();
        
        System.out.println("[GestorPagos] ✅ Gestor inicializado con persistencia");
    }
    
    /**
     * Inicializa precios base por defecto
     */
    private void inicializarPreciosBase() {
        for (TipoEvento tipoEvento : TipoEvento.values()) {
            Map<TipoTicket, Double> precios = new HashMap<>();
            for (TipoTicket tipoTicket : TipoTicket.values()) {
                precios.put(tipoTicket, tipoTicket.getPrecioBase());
            }
            preciosPersonalizados.put(tipoEvento, precios);
        }
    }
    
    // ==================== MÉTODOS DE COMPRA ====================
    
    /**
     * Compra un ticket para un evento
     */
    public Ticket comprarTicket(String eventoId, String participanteId, 
                                TipoTicket tipoTicket, MetodoPago metodoPago) 
            throws EventPlannerException {
        
        // Validar que el evento existe
        Evento evento = gestorEventos.buscarEvento(eventoId);
        
        // Validar que el participante existe
        Participante participante = null;
        for (Participante p : gestorEventos.obtenerTodosParticipantes()) {
            if (p.getId().equals(participanteId)) {
                participante = p;
                break;
            }
        }
        
        if (participante == null) {
            throw new ParticipanteNoEncontradoException(participanteId);
        }
        
        // Verificar que el evento tenga cupos
        if (!evento.tieneCupoDisponible()) {
            throw new CapacidadExcedidaException(evento.getNombre(), evento.getCapacidadMaxima());
        }
        
        // Verificar que no esté ya inscrito
        if (participante.getEventosRegistrados().contains(eventoId)) {
            throw new ParticipanteYaRegistradoException(participante.getNombre(), evento.getNombre());
        }
        
        // Calcular precio (con descuento VIP si aplica)
        double precio = calcularPrecio(evento.getTipo(), tipoTicket, participante.isVip());
        
        // Crear ticket
        Ticket ticket = new Ticket(eventoId, participanteId, tipoTicket, precio);
        
        // Crear pago
        Pago pago = new Pago(ticket.getId(), participanteId, eventoId, precio, metodoPago);
        
        // Procesar pago (simulado)
        boolean pagoExitoso = pago.procesarPago();
        
        if (!pagoExitoso) {
            throw new PagoRechazadoException(metodoPago.getDescripcion(), 
                "Transacción no autorizada. Intente con otro método de pago.");
        }
        
    
        try {
            ticketRepo.agregar(ticket);
            pagoRepo.agregar(pago);
            System.out.println("[GestorPagos]  Ticket y pago guardados: " + ticket.getId());
        } catch (Exception e) {
            System.err.println("[GestorPagos] Error al guardar: " + e.getMessage());
            throw new EventPlannerException("Error al guardar ticket: " + e.getMessage());
        }
        
        // Inscribir al participante en el evento
        gestorEventos.inscribirParticipante(participanteId, eventoId);
        
        return ticket;
    }
    
    /**
     * Calcula el precio de un ticket
     */
    private double calcularPrecio(TipoEvento tipoEvento, TipoTicket tipoTicket, boolean esVip) {
        double precioBase = preciosPersonalizados.get(tipoEvento).get(tipoTicket);
        
        if (esVip && tipoTicket != TipoTicket.GRATUITO) {
            return tipoTicket.getPrecioConDescuentoVIP();
        }
        
        return precioBase;
    }
    
    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    /**
     * Busca un ticket por ID
     */
    public Ticket buscarTicket(String ticketId) throws EventPlannerException {
        try {
            return ticketRepo.buscarPorId(ticketId);
        } catch (Exception e) {
            throw new EventPlannerException("Ticket no encontrado con ID: " + ticketId);
        }
    }
    
    /**
     * Obtiene todos los tickets de un participante
     */
    public ArrayList<Ticket> obtenerTicketsDeParticipante(String participanteId) {
        return ticketRepo.buscarPorParticipante(participanteId);
    }
    
    /**
     * Obtiene todos los tickets de un evento
     */
    public ArrayList<Ticket> obtenerTicketsDeEvento(String eventoId) {
        return ticketRepo.buscarPorEvento(eventoId);
    }
    
    /**
     * Obtiene todos los pagos de un evento
     */
    public ArrayList<Pago> obtenerPagosDeEvento(String eventoId) {
        return pagoRepo.buscarPorEvento(eventoId);
    }
    
    /**
     * Obtiene todos los pagos de un participante
     */
    public ArrayList<Pago> obtenerPagosDeParticipante(String participanteId) {
        return pagoRepo.buscarPorParticipante(participanteId);
    }
    
    // ==================== CÁLCULOS FINANCIEROS ====================
    
    /**
     * Calcula los ingresos totales de un evento
     */
    public double calcularIngresosEvento(String eventoId) {
        double total = 0.0;
        for (Pago pago : pagoRepo.buscarPorEvento(eventoId)) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                total += pago.getMontoBase();
            }
        }
        return total;
    }
    
    /**
     * Calcula los ingresos netos del organizador (después de comisión)
     */
    public double calcularIngresosNetosOrganizador(String eventoId) {
        double total = 0.0;
        for (Pago pago : pagoRepo.buscarPorEvento(eventoId)) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                total += pago.calcularGananciaOrganizador();
            }
        }
        return total;
    }
    
    /**
     * Calcula las ganancias de la plataforma
     */
    public double calcularGananciasPlataforma() {
        double total = 0.0;
        for (Pago pago : pagoRepo.obtenerTodos()) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                total += pago.calcularGananciaPlataforma();
            }
        }
        return total;
    }
    
    /**
     * Calcula ganancias de plataforma por evento
     */
    public double calcularGananciasPlataformaEvento(String eventoId) {
        double total = 0.0;
        for (Pago pago : pagoRepo.buscarPorEvento(eventoId)) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                total += pago.calcularGananciaPlataforma();
            }
        }
        return total;
    }
    
    /**
     * Calcula los ingresos totales del sistema (para Dashboard)
     */
    public double calcularIngresosTotales() {
        double total = 0.0;
        for (Pago pago : pagoRepo.obtenerTodos()) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                total += pago.getMontoBase();
            }
        }
        return total;
    }
    
    // ==================== ESTADÍSTICAS ====================
    
    /**
     * Obtiene estadísticas de ventas por tipo de ticket
     */
    public Map<TipoTicket, Integer> obtenerEstadisticasVentas(String eventoId) {
        Map<TipoTicket, Integer> estadisticas = new HashMap<>();
        
        for (TipoTicket tipo : TipoTicket.values()) {
            estadisticas.put(tipo, 0);
        }
        
        for (Ticket ticket : ticketRepo.buscarPorEvento(eventoId)) {
            TipoTicket tipo = ticket.getTipo();
            estadisticas.put(tipo, estadisticas.get(tipo) + 1);
        }
        
        return estadisticas;
    }
    
    /**
     * Cuenta pagos por cada método de pago (para Dashboard)
     */
    public Map<MetodoPago, Integer> contarPagosPorMetodo() {
        Map<MetodoPago, Integer> conteo = new HashMap<>();
        
        // Inicializar todos los métodos en 0
        for (MetodoPago metodo : MetodoPago.values()) {
            conteo.put(metodo, 0);
        }
        
        // Contar pagos
        for (Pago pago : pagoRepo.obtenerTodos()) {
            if (pago.getEstado() == EstadoPago.APROBADO) {
                MetodoPago metodo = pago.getMetodoPago();
                conteo.put(metodo, conteo.get(metodo) + 1);
            }
        }
        
        return conteo;
    }
    
    // ==================== REEMBOLSOS ====================
    
    /**
     * Reembolsa un ticket
     */
    public boolean reembolsarTicket(String ticketId) throws EventPlannerException {
        Ticket ticket = buscarTicket(ticketId);
        
        if (ticket.isUsado()) {
            throw new EventPlannerException("No se puede reembolsar un ticket ya usado");
        }
        
        // Buscar el pago asociado
        try {
            Pago pago = pagoRepo.buscarPorTicket(ticketId);
            boolean reembolsado = pago.reembolsar();
            
            if (reembolsado) {
                pagoRepo.actualizar(pago);
            }
            
            return reembolsado;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ==================== CONFIGURACIÓN ====================
    
    /**
     * Configura un precio personalizado para un tipo de evento y ticket
     */
    public void configurarPrecio(TipoEvento tipoEvento, TipoTicket tipoTicket, double precio) {
        preciosPersonalizados.get(tipoEvento).put(tipoTicket, precio);
    }
    
    // ==================== GETTERS ====================
    
    public ArrayList<Ticket> obtenerTodosTickets() {
        return ticketRepo.obtenerTodos();
    }
    
    public ArrayList<Pago> obtenerTodosPagos() {
        return pagoRepo.obtenerTodos();
    }
}