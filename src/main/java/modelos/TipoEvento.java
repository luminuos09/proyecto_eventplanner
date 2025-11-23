package modelos;

 
public enum TipoEvento {
    CONFERENCIA("Conferencia Academica o Profesional", 100,""),
    TALLER("Taller Practico Interactivo", 30,""),
    NETWORKING("Evento de Networking", 50," "),
    FERIA("Feria Comercial o exposicion", 200," "),
    WEBINAR("Seminario Web Online", 500,"");
    
    private final String descripcion;
    private final int capacidadSugerida;
    private final String icono;
    

        // Constructor
    TipoEvento(String descripcion, int capacidadSugerida,String icono) {
        this.descripcion = descripcion;
        this.capacidadSugerida = capacidadSugerida;
        this.icono = icono;
        
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCapacidadSugerida() {
        return capacidadSugerida;
    } 
    
        public String getIcono() {
        return icono; 
    }
    

    @Override
    public String toString() {
        return "TipoEvento{" + 
                "Descripcion =" + descripcion + 
                ",Capacidad Sugerida =" + capacidadSugerida +  '}';
    }
}
