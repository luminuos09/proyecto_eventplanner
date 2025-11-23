package persistencia;

import modelos.Ticket;
import java.io.*;
import java.util.ArrayList;

/**
 * Repositorio para gestionar la persistencia de tickets
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class TicketRepositorio extends RepositorioBase<Ticket> {
    
    /**
     * Constructor
     */
    public TicketRepositorio() {
        super("datos/tickets.dat");
        cargarDesdeArchivo();
    }
    
    /**
     * Agrega un ticket y guarda en archivo
     */
    public void agregar(Ticket ticket) throws IOException {
        datos.add(ticket);
        guardarEnArchivo();
        System.out.println("[TicketRepo]  Ticket guardado: " + ticket.getId());
    }
    
    /**
     * Busca un ticket por ID
     */
    public Ticket buscarPorId(String id) throws Exception {
        for (Ticket ticket : datos) {
            if (ticket.getId().equals(id)) {
                return ticket;
            }
        }
        throw new Exception("Ticket no encontrado: " + id);
    }
    
    /**
     * Busca tickets por participante
     */
    public ArrayList<Ticket> buscarPorParticipante(String participanteId) {
        ArrayList<Ticket> resultado = new ArrayList<>();
        for (Ticket ticket : datos) {
            if (ticket.getParticipanteId().equals(participanteId)) {
                resultado.add(ticket);
            }
        }
        return resultado;
    }
    
    /**
     * Busca tickets por evento
     */
    public ArrayList<Ticket> buscarPorEvento(String eventoId) {
        ArrayList<Ticket> resultado = new ArrayList<>();
        for (Ticket ticket : datos) {
            if (ticket.getEventoId().equals(eventoId)) {
                resultado.add(ticket);
            }
        }
        return resultado;
    }
    
    /**
     * Guarda todos los tickets en archivo
     */
    @Override
    protected void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(nombreArchivo))) {
            oos.writeObject(datos);
            System.out.println("[TicketRepo] " + datos.size() + " tickets guardados");
        } catch (IOException e) {
            System.err.println("[TicketRepo] Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los tickets desde archivo
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void cargarDesdeArchivo() {
        File archivo = new File(nombreArchivo);
        
        if (!archivo.exists()) {
            System.out.println("[TicketRepo] Archivo no existe, creando nuevo...");
            datos = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            datos = (ArrayList<Ticket>) ois.readObject();
            System.out.println("[TicketRepo] " + datos.size() + " tickets cargados");
        } catch (EOFException e) {
            System.out.println("[TicketRepo]  Archivo vacío, inicializando...");
            datos = new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[TicketRepo] Error al cargar: " + e.getMessage());
            datos = new ArrayList<>();
        }
    }
}