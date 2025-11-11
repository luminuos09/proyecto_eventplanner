/**
 * GestorPagos - Gestiona todas las operaciones relacionadas con pagos y tickets
 * Maneja la compra de tickets, procesamiento de pagos y reportes financieros
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package logica;

import modelos.*;
import excepciones.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestorPagos {
    
    private final ArrayList<Ticket> tickets;
    private final ArrayList<Pago> pagos;
    private final GestorEventos gestorEventos;
    
    // Configuración de precios por tipo de evento (pueden ser diferentes)
    private final Map<TipoEvento, Map<TipoTicket, Double>> preciosPersonalizados;
    
    public GestorPagos(GestorEventos gestorEventos) {
        this.gestorEventos = gestorEventos;
        this.tickets = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.preciosPersonalizados = new HashMap<>();
        inicializarPreciosBase();
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
        
        // Si el pago fue exitoso, registrar
        tickets.add(ticket);
        pagos.add(pago);
        
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
    
    /**
     * Busca un ticket por ID
     */
    public Ticket buscarTicket(String ticketId) throws EventPlannerException {
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                return ticket;
            }
        }
        throw new EventPlannerException("Ticket no encontrado con ID: " + ticketId);
    }
    
    /**
     * Obtiene todos los tickets de un participante
     */
    public ArrayList<Ticket> obtenerTicketsDeParticipante(String participanteId) {
        ArrayList<Ticket> ticketsParticipante = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getParticipanteId().equals(participanteId)) {
                ticketsParticipante.add(ticket);
            }
        }
        return ticketsParticipante;
    }
    
    /**
     * Obtiene todos los tickets de un evento
     */
    public ArrayList<Ticket> obtenerTicketsDeEvento(String eventoId) {
        ArrayList<Ticket> ticketsEvento = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getEventoId().equals(eventoId)) {
                ticketsEvento.add(ticket);
            }
        }
        return ticketsEvento;
    }
    
    /**
     * Obtiene todos los pagos de un evento
     */
    public ArrayList<Pago> obtenerPagosDeEvento(String eventoId) {
        ArrayList<Pago> pagosEvento = new ArrayList<>();
        for (Pago pago : pagos) {
            if (pago.getEventoId().equals(eventoId)) {
                pagosEvento.add(pago);
            }
        }
        return pagosEvento;
    }
    
    /**
     * Calcula los ingresos totales de un evento
     */
    public double calcularIngresosEvento(String eventoId) {
        double total = 0.0;
        for (Pago pago : pagos) {
            if (pago.getEventoId().equals(eventoId) && pago.getEstado() == EstadoPago.APROBADO) {
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
        for (Pago pago : pagos) {
            if (pago.getEventoId().equals(eventoId) && pago.getEstado() == EstadoPago.APROBADO) {
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
        for (Pago pago : pagos) {
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
        for (Pago pago : pagos) {
            if (pago.getEventoId().equals(eventoId) && pago.getEstado() == EstadoPago.APROBADO) {
                total += pago.calcularGananciaPlataforma();
            }
        }
        return total;
    }
    
    /**
     * Obtiene estadísticas de ventas por tipo de ticket
     */
    public Map<TipoTicket, Integer> obtenerEstadisticasVentas(String eventoId) {
        Map<TipoTicket, Integer> estadisticas = new HashMap<>();
        
        for (TipoTicket tipo : TipoTicket.values()) {
            estadisticas.put(tipo, 0);
        }
        
        for (Ticket ticket : tickets) {
            if (ticket.getEventoId().equals(eventoId)) {
                TipoTicket tipo = ticket.getTipo();
                estadisticas.put(tipo, estadisticas.get(tipo) + 1);
            }
        }
        
        return estadisticas;
    }
    
    /**
     * Reembolsa un ticket
     */
    public boolean reembolsarTicket(String ticketId) throws EventPlannerException {
        Ticket ticket = buscarTicket(ticketId);
        
        if (ticket.isUsado()) {
            throw new EventPlannerException("No se puede reembolsar un ticket ya usado");
        }
        
        // Buscar el pago asociado
        for (Pago pago : pagos) {
            if (pago.getTicketId().equals(ticketId)) {
                return pago.reembolsar();
            }
        }
        
        return false;
    }
    
    /**
     * Configura un precio personalizado para un tipo de evento y ticket
     */
    public void configurarPrecio(TipoEvento tipoEvento, TipoTicket tipoTicket, double precio) {
        preciosPersonalizados.get(tipoEvento).put(tipoTicket, precio);
    }
    
    public ArrayList<Ticket> obtenerTodosTickets() {
        return new ArrayList<>(tickets);
    }
    
    public ArrayList<Pago> obtenerTodosPagos() {
        return new ArrayList<>(pagos);
    }
}