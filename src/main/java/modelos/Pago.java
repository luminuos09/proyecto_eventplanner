/**
 * Pago - Representa una transacción de pago en el sistema
 * Incluye información del ticket, método de pago y comisiones
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Pago implements Serializable {
    
    private static final double COMISION_PLATAFORMA = 0.05; // 5% comisión del sistema
    
    private final String id;
    private final String ticketId;
    private final String participanteId;
    private final String eventoId;
    private final double montoBase;
    private final MetodoPago metodoPago;
    private final double comisionMetodo;
    private final double comisionPlataforma;
    private final double montoTotal;
    private EstadoPago estado;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaAprobacion;
    private String numeroReferencia;
    private String numeroAutorizacion;
    
    /**
     * Constructor del Pago
     */
    public Pago(String ticketId, String participanteId, String eventoId, 
                double montoBase, MetodoPago metodoPago) {
        this.id = generarId();
        this.ticketId = ticketId;
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.montoBase = montoBase;
        this.metodoPago = metodoPago;
        this.comisionMetodo = metodoPago.calcularComision(montoBase);
        this.comisionPlataforma = montoBase * COMISION_PLATAFORMA;
        this.montoTotal = montoBase + comisionMetodo;
        this.estado = EstadoPago.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.numeroReferencia = generarNumeroReferencia();
    }
    
    /**
     * Procesa el pago (simulación)
     */
    public boolean procesarPago() {
        // Simulación: 95% de probabilidad de éxito
        Random random = new Random();
        boolean exito = random.nextDouble() < 0.95;
        
        if (exito) {
            this.estado = EstadoPago.APROBADO;
            this.fechaAprobacion = LocalDateTime.now();
            this.numeroAutorizacion = generarNumeroAutorizacion();
            return true;
        } else {
            this.estado = EstadoPago.RECHAZADO;
            return false;
        }
    }
    
    /**
     * Reembolsa el pago
     */
    public boolean reembolsar() {
        if (estado != EstadoPago.APROBADO) {
            return false;
        }
        this.estado = EstadoPago.REEMBOLSADO;
        return true;
    }
    
    /**
     * Cancela el pago
     */
    public boolean cancelar() {
        if (estado != EstadoPago.PENDIENTE) {
            return false;
        }
        this.estado = EstadoPago.CANCELADO;
        return true;
    }
    
    /**
     * Calcula la ganancia neta del organizador
     */
    public double calcularGananciaOrganizador() {
        if (estado != EstadoPago.APROBADO) {
            return 0.0;
        }
        return montoBase - comisionPlataforma;
    }
    
    /**
     * Calcula la ganancia de la plataforma
     */
    public double calcularGananciaPlataforma() {
        if (estado != EstadoPago.APROBADO) {
            return 0.0;
        }
        return comisionPlataforma;
    }
    
    private String generarId() {
        return "PAY" + System.currentTimeMillis();
    }
    
    private String generarNumeroReferencia() {
        return "REF" + System.currentTimeMillis();
    }
    
    private String generarNumeroAutorizacion() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    // GETTERS
    public String getId() {
        return id;
    }
    
    public String getTicketId() {
        return ticketId;
    }
    
    public String getParticipanteId() {
        return participanteId;
    }
    
    public String getEventoId() {
        return eventoId;
    }
    
    public double getMontoBase() {
        return montoBase;
    }
    
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public double getComisionMetodo() {
        return comisionMetodo;
    }
    
    public double getComisionPlataforma() {
        return comisionPlataforma;
    }
    
    public double getMontoTotal() {
        return montoTotal;
    }
    
    public EstadoPago getEstado() {
        return estado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }
    
    public String getNumeroReferencia() {
        return numeroReferencia;
    }
    
    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║            COMPROBANTE DE PAGO               ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║ ID Transacción: ").append(id).append("\n");
        sb.append("║ Referencia: ").append(numeroReferencia).append("\n");
        if (numeroAutorizacion != null) {
            sb.append("║ Autorización: ").append(numeroAutorizacion).append("\n");
        }
        sb.append("║ Estado: ").append(estado.getNombre()).append("\n");
        sb.append("║ Método: ").append(metodoPago.getDescripcion()).append("\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║ Monto Base: $").append(String.format("%,. 0f", montoBase)).append("\n");
        sb.append("║ Comisión Método: $").append(String.format("%,.0f", comisionMetodo)).append("\n");
        sb.append("║ TOTAL A PAGAR: $").append(String.format("%,.0f", montoTotal)).append("\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║ Fecha: ").append(fechaCreacion.format(formatter)).append("\n");
        if (fechaAprobacion != null) {
            sb.append("║ Aprobado: ").append(fechaAprobacion.format(formatter)).append("\n");
        }
        sb.append("╚══════════════════════════════════════════════╝");
        return sb.toString();
    }
}