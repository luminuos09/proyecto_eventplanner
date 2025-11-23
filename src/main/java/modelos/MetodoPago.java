/**
 * MetodoPago - Enum que define los métodos de pago disponibles
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package modelos;

public enum MetodoPago {
    EFECTIVO("Efectivo", 0.0),
    TARJETA_CREDITO("Tarjeta de Crédito", 0.03), // 3% comisión
    TARJETA_DEBITO("Tarjeta de Débito", 0.015), // 1.5% comisión
    TRANSFERENCIA("Transferencia Bancaria", 0.01), // 1% comisión
    PSE("PSE", 0.02), // 2% comisión
    NEQUI("Nequi", 0.015), // 1.5% comisión
    DAVIPLATA("Daviplata", 0.015); // 1.5% comisión
    
    private final String descripcion;
    private final double comision; // Porcentaje de comisión
    
    MetodoPago(String descripcion, double comision) {
        this.descripcion = descripcion;
        this.comision = comision;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public double getComision() {
        return comision;
    }
    
    /**
     * Calcula el monto de comisión
     */
    public double calcularComision(double monto) {
        return monto * comision;
    }
    
    /**
     * Calcula el monto total con comisión
     */
    public double calcularTotal(double monto) {
        return monto + calcularComision(monto);
    }
    
    @Override
    public String toString() {
        return descripcion + " (Comisión: " + String.format("%.1f%%", comision * 100) + ")";
    }
}