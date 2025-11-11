/**
 * Clase base abstracta para todos los repositorios del sistema.
 * Proporciona funcionalidad comun para persistencia de datos en archivos.
 * Utiliza generics para funcionar con cualquier tipo de entidad.
 * 
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 * @param <T> Tipo de entidad que maneja el repositorio
 */
package persistencia;

import java.util.ArrayList;
import java.io.*;


public abstract class RepositorioBase<T> {
    protected String nombreArchivo;
    protected ArrayList<T> datos;
    
     /**
     * Constructor del repositorio base.
     * Inicializa el nombre del archivo y la lista de datos vacía.
     * 
     * @param nombreArchivo Nombre del archivo de persistencia
     */
    public RepositorioBase(String nombreArchivo){
        this.nombreArchivo= nombreArchivo;
        this.datos =new ArrayList<>();
        
    }
   
     /**
     * Guarda los datos en el archivo.
     * Cada repositorio específico implementa cómo serializar sus datos.
     * 
     * @throws IOException Si hay error al escribir en el archivo
     */
    protected abstract void guardarEnArchivo()throws IOException;
      /**
     * Carga los datos desde el archivo.
     * Cada repositorio específico implementa cómo deserializar sus datos.
     * 
     * @throws IOException Si hay error al leer el archivo
     * @throws ClassNotFoundException Si la clase serializada no existe
     */
    protected abstract void cargarDesdeArchivo()throws IOException, ClassNotFoundException;
    
     /**
     * Obtiene todos los elementos del repositorio.
     * Retorna una copia para proteger la encapsulación.
     * 
     * @return ArrayList con copia de todos los elementos
     */
    public ArrayList<T> obtenerTodos(){
        return new ArrayList<>(datos);
    }
    
    /**
     * Cuenta la cantidad de registros en el repositorio.
     * 
     * @return Numero de elementos almacenados
     */
    public int contarRegistros(){
        return datos.size();
    }
    
     /**
     * Limpia todos los datos del repositorio en memoria.
     * No afecta el archivo hasta que se llame a guardarEnArchivo().
     */
    public void limpiar (){
        datos.clear();
    }
    
    /**
 * Limpia todos los registros del repositorio
 */
public void limpiarTodosLosDatos() {
    datos.clear();
    try {
        guardarEnArchivo();
    } catch (IOException e) {
        System.err.println("Error al limpiar repositorio: " + e.getMessage());
    }
}
}
