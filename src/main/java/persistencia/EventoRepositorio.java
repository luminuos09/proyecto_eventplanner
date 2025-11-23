/**
 * Clase EventoRepositorio
 * EventoRepositorio - Gestiona la persistencia de eventos en archivo.
 * Implementa operaciones CRUD (Create, Read, Update, Delete) para eventos.
 * Utiliza serializacion de objetos para guardar/cargar datos.
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 * @param
 */
package persistencia;

import java.io.*;

import excepciones.*;
import modelos.Evento;
import java.util.ArrayList;
import modelos.EstadoEvento;

public final class EventoRepositorio extends RepositorioBase<Evento>{

     /**
     * Constructor del repositorio de eventos.
     * Inicializa con archivo "eventos.dat" e intenta cargar datos existentes.
     */

    public EventoRepositorio() {
        super("eventos.dat");
        try {
            cargarDesdeArchivo();
        } catch (IOException | ClassNotFoundException e) {
            // Si el archivo no existe o hay error, iniciar con lista vacía
            datos = new ArrayList<>();
        }
    }
    
        
    /**
     * Agrega un nuevo evento al repositorio y lo guarda en archivo.
     * @param evento Evento a agregar
     * @throws IOException Si hay error al guardar en archivo
     */
    public void agregar(Evento evento)throws IOException{
        datos.add(evento);
        guardarEnArchivo();
    }
    
   /**
     * Busca un evento por su ID.
     * 
     * @param id ID del evento a buscar
     * @return Evento encontrado
     * @throws EventoNoEncontradoException Si no existe el evento
     */
    public Evento buscarPorId (String id)throws EventoNoEncontradoException{
        for(Evento evento: datos){
            if(evento.getId().equals(id)){
                return evento;
            }
        }
        throw new EventoNoEncontradoException(id);
    }
    
    /**
     * Busca eventos por su estado.
     * 
     * @param estado Estado a filtrar
     * @return Lista de eventos con ese estado
     */

    public ArrayList<Evento> buscarPorEstado(EstadoEvento estado){
        ArrayList<Evento> resultados = new ArrayList<>();
        for(Evento evento: datos){
            if(evento.getEstado()== estado){
               resultados.add(evento);
            }
        }
        return resultados;
    }
    
    
    /**
     * Actualiza un evento existente en el repositorio.
     * 
     * @param evento
     * @throws EventoNoEncontradoException 
     * @throws IOException Si hay error al guardar
     */
    public void actualizar(Evento evento)throws EventoNoEncontradoException,IOException{
        var encontrado=false;
        for(int i=0; i<datos.size(); i++){
            if(datos.get(i).getId().equals(evento.getId())){
                datos.set(i, evento);
                encontrado=true;
                guardarEnArchivo();
                break;
            }
        }
        if(!encontrado){
            throw new EventoNoEncontradoException("No se pudo actualizar: evento con ID " + evento.getId() + " no encontrado.");
        }
    }
    
     /**
     * Elimina un evento del repositorio.
     * 
     * @param id ID del evento a eliminar
     * @throws EventoNoEncontradoException 
     * @throws IOException 
     */
    public void eliminar(String id)throws EventoNoEncontradoException,IOException{
        var eliminado=false;
        for(int i=0; i<datos.size(); i++){
            if(datos.get(i).getId().equals(id)){
                datos.remove(i);
                eliminado=true;
                guardarEnArchivo();
                break;
            }   
        }
        if(!eliminado){
            throw new EventoNoEncontradoException(id);
        }
    }

     /**
     * Guarda la lista de eventos en el archivo usando serialización.
     * 
     * @throws IOException Si hay error al escribir en el archivo
     */
    
    @Override
    protected void guardarEnArchivo() throws IOException {
        try (ObjectOutputStream escritura = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            escritura.writeObject(datos);
        }
    }
    

    /**
     * Carga la lista de eventos desde el archivo usando deserialización.
     * 
     * @throws IOException Si hay error al leer el archivo
     * @throws ClassNotFoundException Si la clase serializada no existe
     */

    @Override
    @SuppressWarnings("unchecked")
    protected void cargarDesdeArchivo()throws IOException,ClassNotFoundException{
        try (ObjectInputStream lectura= new ObjectInputStream(new FileInputStream(nombreArchivo))){
            datos = (ArrayList<Evento>) lectura.readObject();
        }
    }
    
    
}
