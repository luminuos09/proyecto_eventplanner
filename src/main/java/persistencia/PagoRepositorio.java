package persistencia;

import modelos.Pago;
import modelos.EstadoPago;
import java.io.*;
import java.util.ArrayList;

/**
 * Repositorio para gestionar la persistencia de pagos
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
public class PagoRepositorio extends RepositorioBase<Pago> {
    
    /**
     * Constructor
     */
    public PagoRepositorio() {
        super("datos/pagos.dat");
        cargarDesdeArchivo();
    }
    
    /**
     * Agrega un pago y guarda en archivo
     * 
     * @param pago Pago a agregar
     * @throws IOException Si hay error al guardar
     */
    public void agregar(Pago pago) throws IOException {
        datos.add(pago);
        guardarEnArchivo();
        System.out.println("[PagoRepo]  Pago guardado: " + pago.getId());
    }
    
    /**
     * Busca un pago por ID
     * 
     * @param id ID del pago
     * @return Pago encontrado
     * @throws Exception Si no se encuentra
     */
    public Pago buscarPorId(String id) throws Exception {
        for (Pago pago : datos) {
            if (pago.getId().equals(id)) {
                return pago;
            }
        }
        throw new Exception("Pago no encontrado: " + id);
    }
    
    /**
     * Busca un pago por ID de ticket
     * 
     * @param ticketId ID del ticket
     * @return Pago asociado al ticket
     * @throws Exception Si no se encuentra
     */
    public Pago buscarPorTicket(String ticketId) throws Exception {
        for (Pago pago : datos) {
            if (pago.getTicketId().equals(ticketId)) {
                return pago;
            }
        }
        throw new Exception("Pago no encontrado para ticket: " + ticketId);
    }
    
    /**
     * Busca pagos por participante
     * 
     * @param participanteId ID del participante
     * @return Lista de pagos del participante
     */
    public ArrayList<Pago> buscarPorParticipante(String participanteId) {
        ArrayList<Pago> resultado = new ArrayList<>();
        for (Pago pago : datos) {
            if (pago.getParticipanteId().equals(participanteId)) {
                resultado.add(pago);
            }
        }
        return resultado;
    }
    
    /**
     * Busca pagos por evento
     * 
     * @param eventoId ID del evento
     * @return Lista de pagos del evento
     */
    public ArrayList<Pago> buscarPorEvento(String eventoId) {
        ArrayList<Pago> resultado = new ArrayList<>();
        for (Pago pago : datos) {
            if (pago.getEventoId().equals(eventoId)) {
                resultado.add(pago);
            }
        }
        return resultado;
    }
    
    /**
     * Busca pagos por estado
     * 
     * @param estado Estado del pago
     * @return Lista de pagos con ese estado
     */
    public ArrayList<Pago> buscarPorEstado(EstadoPago estado) {
        ArrayList<Pago> resultado = new ArrayList<>();
        for (Pago pago : datos) {
            if (pago.getEstado() == estado) {
                resultado.add(pago);
            }
        }
        return resultado;
    }
    
    /**
     * Actualiza un pago existente
     * 
     * @param pagoActualizado Pago con datos actualizados
     * @throws Exception Si no se encuentra el pago
     */
    public void actualizar(Pago pagoActualizado) throws Exception {
        boolean encontrado = false;
        
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(pagoActualizado.getId())) {
                datos.set(i, pagoActualizado);
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            throw new Exception("Pago no encontrado para actualizar: " + pagoActualizado.getId());
        }
        
        guardarEnArchivo();
        System.out.println("[PagoRepo] Pago actualizado: " + pagoActualizado.getId());
    }
    
    /**
     * Elimina un pago
     * 
     * @param id ID del pago a eliminar
     * @throws Exception Si no se encuentra
     */
    public void eliminar(String id) throws Exception {
        Pago pagoAEliminar = null;
        
        for (Pago pago : datos) {
            if (pago.getId().equals(id)) {
                pagoAEliminar = pago;
                break;
            }
        }
        
        if (pagoAEliminar == null) {
            throw new Exception("Pago no encontrado: " + id);
        }
        
        datos.remove(pagoAEliminar);
        guardarEnArchivo();
        System.out.println("[PagoRepo]  Pago eliminado: " + id);
    }
    
    /**
     * Guarda todos los pagos en archivo
     */
    @Override
    protected void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(nombreArchivo))) {
            oos.writeObject(datos);
            System.out.println("[PagoRepo]  " + datos.size() + " pagos guardados");
        } catch (IOException e) {
            System.err.println("[PagoRepo]  Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los pagos desde archivo
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void cargarDesdeArchivo() {
        File archivo = new File(nombreArchivo);
        
        if (!archivo.exists()) {
            System.out.println("[PagoRepo] ⚠️ Archivo no existe, creando nuevo...");
            datos = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            datos = (ArrayList<Pago>) ois.readObject();
            System.out.println("[PagoRepo] " + datos.size() + " pagos cargados");
        } catch (EOFException e) {
            System.out.println("[PagoRepo]  Archivo vacío, inicializando...");
            datos = new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[PagoRepo] Error al cargar: " + e.getMessage());
            datos = new ArrayList<>();
        }
    }
}