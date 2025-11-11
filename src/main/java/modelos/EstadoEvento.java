
package modelos;

public enum EstadoEvento {
    BORRADOR("BORRADOR"),
    PUBLICADO("PUBLICADO"),
    EN_CURSO("EN CURSO"),
    FINALIZADO("FINALIZADO"),
    CANCELADO("CANCELADO");
    
    private String descripcion;


    EstadoEvento (String descripcion){
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return this.descripcion;
    }


    @Override
    public String toString() {
        return "EstadoEvento{" + 
                "Descripcion = " + descripcion + 
                '}';
    }
    
    
}
