/**
 * ParticipanteRepositorio - Gestiona la persistencia de participantes en archivos.
 * Implementa operaciones CRUD (Create, Read, Update, Delete) para participantes.
 * Utiliza serialización de objetos para guardar/cargar datos.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package persistencia;

import excepciones.*;
import java.io.*;
import modelos.Participante;
import java.util.ArrayList;

public class ParticipanteRepositorio extends RepositorioBase<Participante> {
     /**
     * Constructor del repositorio de participantes.
     * Inicializa con archivo "participantes.dat" e intenta cargar datos existentes.
     */
    public ParticipanteRepositorio(){
        super("participantes.dat");
        try {
            cargarDesdeArchivo();
        } catch (IOException | ClassNotFoundException e) {
         // Si el archivo no existe o hay error, iniciar con lista vacía
            datos = new ArrayList<>();
        }
    }
    

    // OPERACIONES CRUD
    // 
    /**
     * Agrega un nuevo participante al repositorio y lo guarda en archivo.
     * 
     * @param participante Participante a agregar
     * @throws IOException Si hay error al guardar en archivo
     */
    public void agregar(Participante participante) throws IOException {
        datos.add(participante);
        guardarEnArchivo();
    }
     
    /**
     * Busca un participante por su ID.
     * 
     * @param id ID del participante a buscar
     * @return Participante encontrado
     * @throws ParticipanteNoEncontradoException Si no existe el organizador
     */
    public Participante buscarPorId(String id) throws ParticipanteNoEncontradoException {
        for (Participante participante : datos) {
            if(participante.getId().equals(id)){
                return participante;
            }
        }
        throw new ParticipanteNoEncontradoException(id);
    }
    /**
     * Busca un participante por su email.
     * Útil para login o validación de duplicados.
     * 
     * @param email Email del participante a buscar
     * @return Participante encontrado o null si no existe
     */
    public Participante buscarPorEmail(String email){
       for (Participante participante : datos) {
        if(participante.getEmail().equalsIgnoreCase(email)){
            return participante;
        }
       }
       return null;
    }
    /**
     * Actualiza un participante existente en el repositorio.
     * 
     * @param Participante Participante con datos actualizados
     * @throws ParticipanteNoEncontradoException Si el organizador no existe
     * @throws IOException Si hay error al guardar
     */
    public void actualizar(Participante participante) throws DatosInvalidosException, IOException {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(participante.getId())) {
                datos.set(i, participante);
                guardarEnArchivo();
                return;
            }
        }
        throw new DatosInvalidosException(participante.getId(), "Participante no encontrado para actualizar");
    }
     
    /**
     * Elimina un participante del repositorio.
     * 
     * @param id ID del participante a eliminar
     * @throws Participante Si el Participante no existe
     * @throws IOException Si hay error al guardar
     */
    public void eliminar(String id) throws DatosInvalidosException, IOException {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(id)) {
                datos.remove(i);
                guardarEnArchivo();
                return;
            }
        }
        throw new DatosInvalidosException(id, "Participante no encontrado para eliminar");
    }
    
     //METODOS ABSTRACTOS
    /**
     * Guarda la lista de participantes en el archivo usando serialización.
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
     * Carga la lista de participantes desde el archivo usando deserialización.
     * 
     * @throws IOException Si hay error al leer el archivo
     * @throws ClassNotFoundException Si la clase serializada no existe
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void cargarDesdeArchivo()throws IOException,ClassNotFoundException{
        try (ObjectInputStream lectura= new ObjectInputStream(new FileInputStream(nombreArchivo))){
            datos = (ArrayList<Participante>) lectura.readObject();
        }
    }
}