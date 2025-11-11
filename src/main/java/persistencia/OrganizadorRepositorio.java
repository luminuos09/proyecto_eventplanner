/**
 * OrganizadorRepositorio - Gestiona la persistencia de organizadores en archivo.
 * Implementa operaciones CRUD (Create, Read, Update, Delete) para organizadores.
 * Utiliza serialización de objetos para guardar/cargar datos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package persistencia;

import excepciones.*;
import modelos.Organizador;
import java.io.*;
import java.util.ArrayList;

public class OrganizadorRepositorio extends RepositorioBase<Organizador> {
    /**
     * Constructor del repositorio de organizadores.
     * Inicializa con archivo "organizadores.dat" e intenta cargar datos existentes.
     */
    public OrganizadorRepositorio() {
        super("organizadores.dat");
        try {
            cargarDesdeArchivo();
        } catch (IOException | ClassNotFoundException e) {
            // Si el archivo no existe o hay error, iniciar con lista vacía
            datos = new ArrayList<>();
        }
    }
    // 
    // OPERACIONES CRUD
    // 
    /**
     * Agrega un nuevo organizador al repositorio y lo guarda en archivo.
     * 
     * @param organizador Organizador a agregar
     * @throws IOException Si hay error al guardar en archivo
     */
    public void agregar(Organizador organizador) throws IOException {
        datos.add(organizador);
        guardarEnArchivo();
    }
    
    /**
     * Busca un organizador por su ID.
     * 
     * @param id ID del organizador a buscar
     * @return Organizador encontrado
     * @throws OrganizadorNoEncontradoException Si no existe el organizador
     */
    public Organizador buscarPorId(String id) throws OrganizadorNoEncontradoException {
        for (Organizador organizador : datos) {
            if (organizador.getId().equals(id)) {
                return organizador;
            }
        }
        throw new OrganizadorNoEncontradoException(id);
    }
    
    /**
     * Busca un organizador por su email.
     * Útil para login o validación de duplicados.
     * 
     * @param email Email del organizador a buscar
     * @return Organizador encontrado o null si no existe
     */
    public Organizador buscarPorEmail(String email) {
        for (Organizador organizador : datos) {
            if (organizador.getEmail().equalsIgnoreCase(email)) {
                return organizador;
            }
        }
        return null;
    }
    /**
     * Actualiza un organizador existente en el repositorio.
     * 
     * @param organizador Organizador con datos actualizados
     * @throws OrganizadorNoEncontradoException Si el organizador no existe
     * @throws IOException Si hay error al guardar
     */
    public void actualizar(Organizador organizador) throws OrganizadorNoEncontradoException, IOException {
        var encontrado = false;
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(organizador.getId())) {
                datos.set(i, organizador);
                encontrado = true;
                guardarEnArchivo();
                break;
            }
        }
        if (!encontrado) {
            throw new OrganizadorNoEncontradoException(organizador.getId());
        }
    }
    
    /**
     * Elimina un organizador del repositorio.
     * 
     * @param id ID del organizador a eliminar
     * @throws OrganizadorNoEncontradoException Si el organizador no existe
     * @throws IOException Si hay error al guardar
     */
    public void eliminar(String id) throws OrganizadorNoEncontradoException, IOException {
        var eliminado = false;
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(id)) {
                datos.remove(i);
                eliminado = true;
                guardarEnArchivo();
                break;
            }
        }
        if (!eliminado) {
            throw new OrganizadorNoEncontradoException(id);
        }
    }
    
    
   //METODOS ABSTRACTOS
    /**
     * Guarda la lista de organizadores en el archivo usando serialización.
     * 
     * @throws IOException Si hay error al escribir en el archivo
     */
    @Override
    protected void guardarEnArchivo() throws IOException {
        try (ObjectOutputStream escritura = new ObjectOutputStream(
                new FileOutputStream(nombreArchivo))) {
            escritura.writeObject(datos);
        }
    }
    
    /**
     * Carga la lista de organizadores desde el archivo usando deserialización.
     * 
     * @throws IOException Si hay error al leer el archivo
     * @throws ClassNotFoundException Si la clase serializada no existe
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void cargarDesdeArchivo() throws IOException, ClassNotFoundException {
        try (ObjectInputStream lectura = new ObjectInputStream(
                new FileInputStream(nombreArchivo))) {
            datos = (ArrayList<Organizador>) lectura.readObject();
        }
    }
}