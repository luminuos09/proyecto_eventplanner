/**
 * TipoTicket - Enum que define los diferentes tipos de tickets disponibles
 * Cada tipo tiene un precio base y beneficios específicos
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;

public enum TipoTicket {
    GRATUITO("Gratuito", 0.0, "Acceso básico al evento"),
    ESTANDAR("Estándar", 50000.0, "Acceso completo + Material digital"),
    PREMIUM("Premium", 100000.0, "Acceso completo + Material físico + Certificado"),
    VIP("VIP", 200000.0, "Acceso total + Material premium + Networking exclusivo + Certificado");
    
    private final String descripcion;
    private final double precioBase;
    private final String beneficios;
    
    TipoTicket(String descripcion, double precioBase, String beneficios) {
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.beneficios = beneficios;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public double getPrecioBase() {
        return precioBase;
    }
    
    public String getBeneficios() {
        return beneficios;
    }
    
    /**
     * Calcula el precio con descuento VIP (20% off)
     */
    public double getPrecioConDescuentoVIP() {
        return precioBase * 0.8; // 20% descuento
    }
    
    @Override
    public String toString() {
        return descripcion + " - $" + String.format("%,.0f", precioBase);
    }
}