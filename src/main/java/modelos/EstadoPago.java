/**
 * EstadoPago - Enum que define los estados de un pago
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;

public enum EstadoPago {
    PENDIENTE("Pendiente", "Pago en proceso"),
    APROBADO("Aprobado", "Pago exitoso"),
    RECHAZADO("Rechazado", "Pago no autorizado"),
    REEMBOLSADO("Reembolsado", "Dinero devuelto"),
    CANCELADO("Cancelado", "Pago cancelado");
    
    private final String nombre;
    private final String descripcion;
    
    EstadoPago(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return nombre + " - " + descripcion;
    }
}