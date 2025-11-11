/**
 * Ticket - Representa un ticket de entrada a un evento
 * Contiene información del tipo, precio, participante y evento
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket implements Serializable {
    
    private final String id;
    private final String eventoId;
    private final String participanteId;
    private final TipoTicket tipo;
    private final double precio;
    private final LocalDateTime fechaCompra;
    private boolean usado;
    private LocalDateTime fechaUso;
    
    /**
     * Constructor para ticket
     */
    public Ticket(String eventoId, String participanteId, TipoTicket tipo, double precio) {
        this.id = generarId();
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.tipo = tipo;
        this.precio = precio;
        this.fechaCompra = LocalDateTime.now();
        this.usado = false;
        this.fechaUso = null;
    }
    
    /**
     * Marca el ticket como usado (check-in)
     */
    public boolean marcarComoUsado() {
        if (usado) {
            return false;
        }
        this.usado = true;
        this.fechaUso = LocalDateTime.now();
        return true;
    }
    
    /**
     * Genera ID único para el ticket
     */
    private String generarId() {
        return "TKT" + System.currentTimeMillis();
    }
    
    /**
     * Genera código QR simulado
     */
    public String generarCodigoQR() {
        return "QR-" + id.substring(3);
    }
    
    // GETTERS
    public String getId() {
        return id;
    }
    
    public String getEventoId() {
        return eventoId;
    }
    
    public String getParticipanteId() {
        return participanteId;
    }
    
    public TipoTicket getTipo() {
        return tipo;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }
    
    public boolean isUsado() {
        return usado;
    }
    
    public LocalDateTime getFechaUso() {
        return fechaUso;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║           TICKET DE EVENTO             ║\n");
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append("║ ID: ").append(id).append("\n");
        sb.append("║ Tipo: ").append(tipo.getDescripcion()).append("\n");
        sb.append("║ Precio: $").append(String.format("%,.0f", precio)).append("\n");
        sb.append("║ Compra: ").append(fechaCompra.format(formatter)).append("\n");
        sb.append("║ Estado: ").append(usado ? "USADO ✓" : "VIGENTE").append("\n");
        if (usado) {
            sb.append("║ Uso: ").append(fechaUso.format(formatter)).append("\n");
        }
        sb.append("║ QR: ").append(generarCodigoQR()).append("\n");
        sb.append("╚════════════════════════════════════════╝");
        return sb.toString();
    }
}