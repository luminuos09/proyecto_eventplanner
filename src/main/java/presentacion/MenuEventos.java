/**
 * MenuReportes - Gestionar el menú de eventos
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion;

import logica.GestorEventos;
import logica.ValidarDatos;
import modelos.*;
import excepciones.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class MenuEventos {
    
    private final GestorEventos gestor;
    private final Scanner scanner;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private MenuParticipantes menuParticipantes;
    
    /**
     * Constructor
     * @param gestor Instancia del gestor de eventos
     * @param scanner 
     */
    public MenuEventos(GestorEventos gestor, Scanner scanner) {
        this.gestor = gestor;
        this.scanner = scanner;
        this.menuParticipantes = menuParticipantes = new MenuParticipantes(gestor, scanner);
    }
    
    /**
     * Muestra el menú de eventos
     */
    public void mostrar() {
        var volver = false;
        
        while (!volver) {
            try {
                limpiarPantalla();
                mostrarOpciones();
                
                System.out.print("Seleccione una opción: ");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
                System.out.println();
                
                switch (opcion) {
                    case 1 -> crearEvento();
                    case 2 -> listarEventos();
                    case 3 -> buscarEvento();
                    case 4 -> editarEvento();
                    case 5 -> cancelarEvento();
                    case 6 -> filtrarEventosPorEstado();
                    case 7 -> filtrarEventosPorTipo();
                    case 8 -> cambiarEstadoEvento();
                    case 0 -> volver = true;
                    default -> MenuPrincipalConsola.mostrarError("Opción inválida");
                }
                
                if (!volver) {
                    pausar();
                }
                
            } catch (Exception e) {
                MenuPrincipalConsola.mostrarError("Entrada inválida: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer
                pausar();
            }
        }
    }
    
        /**
         * Muestra las opciones del menú
         */
        private void mostrarOpciones() {
            System.out.println("====================================================================");
            System.out.println("                     GESTIÓN DE EVENTOS                             ");
            System.out.println("====================================================================");
            System.out.println();
            System.out.println("  1. Crear Evento");
            System.out.println("  2. Listar Todos los Eventos");
            System.out.println("  3. Buscar Evento por ID");
            System.out.println("  4. Editar Evento");
            System.out.println("  5. Cancelar Evento");
            System.out.println("  6. Filtrar por Estado");
            System.out.println("  7. Filtrar por Tipo");
            System.out.println("  8. Cambiar Estado de Evento");
            System.out.println("  0. Volver al Menú Principal");
            System.out.println();
            System.out.println("====================================================================");
        }
    
            /**
             * Crea un nuevo evento
             */
                private void crearEvento() {
                    System.out.println("====================================================================");
                    System.out.println("                    CREAR NUEVO EVENTO");
                    System.out.println("====================================================================");
                    System.out.println();

                    try {
                        // ========== 1. NOMBRE DEL EVENTO ==========
                        String nombre = null;
                        do {
                            try {
                                System.out.print("Nombre del evento: ");
                                nombre = scanner.nextLine().trim();
                                ValidarDatos.validarNombre(nombre);
                                break; // Nombre valido, salir del loop
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);

                        // ========== 2. DESCRIPCION ==========
                        String descripcion = null;
                        do {
                            try {
                                System.out.print("Descripción (mínimo 10 caracteres): ");
                                descripcion = scanner.nextLine().trim();
                                ValidarDatos.validarDescripcion(descripcion);
                                break; // Descripción válida
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);

                        // ========== 4. FECHA DE INICIO ==========
                        LocalDateTime fechaInicio = null;
                        do {
                            try {
                                System.out.println("\nFecha y hora de inicio (formato: dd/MM/yyyy HH:mm)");
                                System.out.print("Ejemplo: 25/12/2024 10:00 -> ");
                                String fechaInicioStr = scanner.nextLine().trim();
                                fechaInicio = LocalDateTime.parse(fechaInicioStr, FORMATO_FECHA);
                                break; // Fecha válida
                            } catch (DateTimeParseException e) {
                                MenuPrincipalConsola.mostrarError("Formato de fecha inválido. Use: dd/MM/yyyy HH:mm");
                            }
                        } while (true);

                        // ========== 5. FECHA DE FIN ==========
                        LocalDateTime fechaFin = null;
                        do {
                            try {
                                System.out.println("\nFecha y hora de fin (formato: dd/MM/yyyy HH:mm)");
                                System.out.print("Ejemplo: 25/12/2024 18:00 -> ");
                                String fechaFinStr = scanner.nextLine().trim();
                                fechaFin = LocalDateTime.parse(fechaFinStr, FORMATO_FECHA);

                                // Validar que fecha fin sea posterior a fecha inicio
                                ValidarDatos.validarFechas(fechaInicio, fechaFin);
                                break; // Fechas válidas
                            } catch (DateTimeParseException e) {
                                MenuPrincipalConsola.mostrarError("Formato de fecha inválido. Use: dd/MM/yyyy HH:mm");
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);

                        // ========== 6. UBICACIÓN ==========
                        String ubicacion = null;
                        do {
                            try {
                                System.out.print("\nUbicación: ");
                                ubicacion = scanner.nextLine().trim();
                                ValidarDatos.validarUbicacion(ubicacion);
                                break; // Ubicación válida
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);

                        // ========== 7. CAPACIDAD MÁXIMA ==========
                        int capacidadMaxima = 0;
                        do {
                            try {
                                System.out.print("Capacidad maxima: ");
                                capacidadMaxima = scanner.nextInt();
                                scanner.nextLine(); 
                                ValidarDatos.validarCapacidad(capacidadMaxima);
                                break; // Capacidad válida
                            } catch (InputMismatchException e) {
                                MenuPrincipalConsola.mostrarError("Debe ingresar un número válido");
                                scanner.nextLine(); 
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);
                        
                       } catch (Exception e) {
                            MenuPrincipalConsola.mostrarError("Error inesperado: " + e.getMessage());
                            e.printStackTrace(); // Para debugging
                        }
          }     
                /**
                 * Lista todos los eventos
                 */
                private void listarEventos() {
                    System.out.println("====================================================================");
                    System.out.println("                      LISTA DE EVENTOS                               ");
                    System.out.println("====================================================================");
                    System.out.println();
                    
                    ArrayList<Evento> eventos = gestor.obtenerTodosEventos();
                    
                    if (eventos.isEmpty()) {
                        MenuPrincipalConsola.mostrarInfo("No hay eventos registrados en el sistema.");
                        return;
                    }
                    
                    System.out.println("Total de eventos: " + eventos.size());
                    
                    for (int i = 0; i < eventos.size(); i++) {
                        Evento evento = eventos.get(i);
                        System.out.println("\n" + (i + 1) + ". " + evento.getNombre());
                        System.out.println("   ID: " + evento.getId());
                        System.out.println("   Tipo: " + evento.getTipo().getDescripcion() +"| Estado: " + evento.getEstado().getDescripcion());
                        System.out.println("   Fecha: " + evento.getFechaInicio().format(FORMATO_FECHA));
                        System.out.println("   Ubicación: " + evento.getUbicacion());
                        System.out.println("   Registrados: " + evento.getParticipantesRegistrados().size() + "/" + evento.getCapacidadMaxima());
                        System.out.println("   ─".repeat(40));
                    }
                }
                    /**
                     * Busca un evento por ID
                     */
                    private void buscarEvento() {
                        System.out.println("====================================================================");
                        System.out.println("                        BUSCAR EVENTO                                ");
                        System.out.println("====================================================================");
                        System.out.println();
                        
                        System.out.print("Ingrese el ID del evento: ");
                        var id = scanner.nextLine();
                        
                        try {
                            var evento = gestor.buscarEvento(id);
                            
                            
                            MenuPrincipalConsola.mostrarExito("Evento encontrado");
                            System.out.println("=".repeat(70));
                            mostrarDetallesEvento(evento);
                            
                        } catch (EventoNoEncontradoException e) {
                            MenuPrincipalConsola.mostrarError("No se encontró ningún evento con el ID: " + id);
                        }
                    }
                        /**
                         * Edita un evento existente
                         */
                        private void editarEvento() {
                            System.out.println("====================================================================");
                            System.out.println("                        EDITAR EVENTO                                ");
                            System.out.println("====================================================================");
                            System.out.println();
                            
                            System.out.print("Ingrese el ID del evento a editar: ");
                            var id = scanner.nextLine();
                            
                            try {
                                Evento evento = gestor.buscarEvento(id);
                                
                                System.out.println("\nDatos actuales del evento:");
                                mostrarDetallesEvento(evento);
                                
                                System.out.println("\nDeje en blanco para mantener el valor actual");
                                
                                
                                System.out.print("\nNuevo nombre [" + evento.getNombre() + "]: ");
                                String nombre = scanner.nextLine();
                                
                                System.out.print("Nueva descripción [" + evento.getDescripcion() + "]: ");
                                String descripcion = scanner.nextLine();
                                
                                System.out.print("Nueva ubicación [" + evento.getUbicacion() + "]: ");
                                String ubicacion = scanner.nextLine();
                                
                                gestor.actualizarEvento(id, nombre.isEmpty() ? null : nombre, descripcion.isEmpty() ? null : descripcion,ubicacion.isEmpty() ? null : ubicacion);
                                
                                MenuPrincipalConsola.mostrarExito("Evento actualizado exitosamente!");
                                
                            } catch (EventoNoEncontradoException e) {
                                MenuPrincipalConsola.mostrarError("No se encontró el evento con ID: " + id);
                            } catch (EventPlannerException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        }
                        
                            /**
                             * Cancela un evento
                             */
                            private void cancelarEvento() {
                                System.out.println("====================================================================");
                                System.out.println("                       CANCELAR EVENTO                                ");
                                System.out.println("====================================================================");
                                System.out.println();
                                
                                System.out.print("Ingrese el ID del evento a cancelar: ");
                                var eventoId = scanner.nextLine();
                                
                                try {
                                    Evento evento = gestor.buscarEvento(eventoId);
                                    
                                    System.out.println("\nEvento a cancelar:");
                                    
                                    mostrarDetallesEvento(evento);
                                    
                                    System.out.print("\nEmail del organizador (para verificación): ");
                                    var organizadorEmail = scanner.nextLine();
                                    
                                    Organizador organizador = gestor.buscarOrganizadorPorEmail(organizadorEmail);
                                    
                                    System.out.print("\n¿Está seguro que desea cancelar este evento? (S/N): ");
                                    var confirmacion = scanner.nextLine().trim().toUpperCase();
                                    
                                    if (confirmacion.equals("S") || confirmacion.equals("SI") || confirmacion.equals("SÍ")) {
                                        gestor.cancelarEvento(eventoId, organizador.getId());
                                        MenuPrincipalConsola.mostrarExito("Evento cancelado exitosamente");
                                    } else 
                                    {
                                        MenuPrincipalConsola.mostrarInfo("Cancelación abortada.");
                                    }
                                    
                                } catch (EventoNoEncontradoException e) {
                                    MenuPrincipalConsola.mostrarError("No se encontró el evento.");
                                } catch (DatosInvalidosException e) {
                                    MenuPrincipalConsola.mostrarError("Organizador no encontrado.");
                                } catch (EventPlannerException e) {
                                    MenuPrincipalConsola.mostrarError(e.getMessage());
                                }
                            }
                            
                                /**
                                 * Filtra eventos por estado
                                 */
                                private void filtrarEventosPorEstado() {
                                    System.out.println("====================================================================");
                                    System.out.println("                   FILTRAR POR ESTADO                               ");
                                    System.out.println("====================================================================");
                                    System.out.println();
                                    
                                    EstadoEvento estado = seleccionarEstadoEvento();
                                    
                                    ArrayList<Evento> eventos = gestor.buscarEventosPorEstado(estado);
                                    
                                    System.out.println("\nEventos con estado: " + estado.getDescripcion());
                                    
                                    if (eventos.isEmpty()) {
                                        MenuPrincipalConsola.mostrarInfo("No hay eventos con este estado.");
                                        return;
                                    }
                                    
                                    System.out.println("Total encontrados: " + eventos.size());
                                    System.out.println();
                                    
                                    for (int i = 0; i < eventos.size(); i++) {
                                        Evento evento = eventos.get(i);
                                        System.out.println((i + 1) + ". " + evento.getNombre() +" [" + evento.getTipo().getDescripcion() + "]");
                                        System.out.println("   ID: " + evento.getId());
                                        System.out.println("   Fecha: " + evento.getFechaInicio().format(FORMATO_FECHA));
                                        System.out.println("   Registrados: " + evento.getParticipantesRegistrados().size());
                                        System.out.println();
                                    }
                                }
                                    /**
                                     * Filtra eventos por tipo
                                     */
                                    private void filtrarEventosPorTipo() {
                                        System.out.println("====================================================================");
                                        System.out.println("                    FILTRAR POR TIPO                                ");
                                        System.out.println("====================================================================");
                                        System.out.println();
                                        
                                        TipoEvento tipo = seleccionarTipoEvento();
                                        
                                        ArrayList<Evento> todosEventos = gestor.obtenerTodosEventos();
                                        ArrayList<Evento> eventosFiltrados = new ArrayList<>();
                                        
                                        for (Evento evento : todosEventos) {
                                            if (evento.getTipo() == tipo) {
                                                eventosFiltrados.add(evento);
                                            }
                                        }
                                        
                                        System.out.println("\nEventos de tipo: " + tipo.getDescripcion());
                                        
                                        if (eventosFiltrados.isEmpty()) {
                                            MenuPrincipalConsola.mostrarInfo("No hay eventos de este tipo.");
                                            return;
                                        }
                                        
                                        System.out.println("Total encontrados: " + eventosFiltrados.size());
                                        System.out.println();
                                        
                                        for (int i = 0; i < eventosFiltrados.size(); i++) {
                                            Evento evento = eventosFiltrados.get(i);
                                            System.out.println((i + 1) + ". " + evento.getNombre());
                                            System.out.println("   ID: " + evento.getId()+" | Estado: " + evento.getEstado().getDescripcion());
                                            System.out.println("   Fecha: " + evento.getFechaInicio().format(FORMATO_FECHA));
                                            System.out.println();
                                        }
                                    }
                                        /**
                                         * Cambia el estado de un evento
                                         */
                                        private void cambiarEstadoEvento() {
                                            System.out.println("====================================================================");
                                            System.out.println("                   CAMBIAR ESTADO DE EVENTO                          ");
                                            System.out.println("====================================================================");
                                            System.out.println();
                                            listarEventos();
                                            System.out.print("Ingrese el ID del evento: ");
                                            String id = scanner.nextLine();
                                            
                                            try {
                                                Evento evento = gestor.buscarEvento(id);
                                                
                                                System.out.println("\nEvento seleccionado: " + evento.getNombre());
                                                System.out.println("   Estado actual: " + evento.getEstado().getDescripcion());
                                                System.out.println();
                                                
                                                EstadoEvento nuevoEstado = seleccionarEstadoEvento();
                                                
                                                evento.setEstado(nuevoEstado);
                                                
                                                MenuPrincipalConsola.mostrarExito("Estado cambiado a: " + nuevoEstado.getDescripcion());
                                                
                                            } catch (EventoNoEncontradoException e) {
                                                MenuPrincipalConsola.mostrarError("No se encontró el evento con ID: " + id);
                                            }
                                        }
                                            
                                            /**
                                             * Permite seleccionar un tipo de evento
                                             */
                                            private TipoEvento seleccionarTipoEvento() {
                                                System.out.println("\nTIPOS DE EVENTO:");
                                                
                                                
                                                TipoEvento[] tipos = TipoEvento.values();
                                                for (int i = 0; i < tipos.length; i++) {
                                                    System.out.println((i + 1) + ". " + tipos[i].getDescripcion() +" (Capacidad sugerida: " + tipos[i].getCapacidadSugerida() + ")");
                                                }
                                                
                                                System.out.print("\nSeleccione el tipo (1-" + tipos.length + "): ");
                                                var opcion = scanner.nextInt();
                                                scanner.nextLine();
                                                
                                                if (opcion < 1 || opcion > tipos.length) {
                                                    MenuPrincipalConsola.mostrarAdvertencia("Opcion invalida, seleccionando CONFERENCIA por defecto");
                                                    return TipoEvento.CONFERENCIA;
                                                }
                                                
                                                return tipos[opcion - 1];
                                            }
                                            
                                            /**
                                             * Permite seleccionar un estado de evento
                                             */
                                            private EstadoEvento seleccionarEstadoEvento() {
                                                System.out.println("\nESTADOS DE EVENTO:");
                                                
                                                
                                                EstadoEvento[] estados = EstadoEvento.values();
                                                for (int i = 0; i < estados.length; i++) {
                                                    System.out.println((i + 1) + ". " + estados[i].getDescripcion());
                                                }
                                                
                                                System.out.print("\nSeleccione el estado (1-" + estados.length + "): ");
                                                var opcion = scanner.nextInt();
                                                scanner.nextLine();
                                                
                                                if (opcion < 1 || opcion > estados.length) {
                                                    MenuPrincipalConsola.mostrarAdvertencia("Opcion invalida, seleccionando BORRADOR por defecto");
                                                    return EstadoEvento.BORRADOR;
                                                }
                                                
                                                return estados[opcion - 1];
                                            }
                                                /**
                                                 * Muestra los detalles completos de un evento
                                                 */
                                                private void mostrarDetallesEvento(Evento evento) {
                                                    System.out.println("ID: " + evento.getId());
                                                    System.out.println("Nombre: " + evento.getNombre());
                                                    System.out.println("Descripción: " + evento.getDescripcion());
                                                    System.out.println("Tipo: " + evento.getTipo().getDescripcion());
                                                    System.out.println("Estado: " + evento.getEstado().getDescripcion());
                                                    System.out.println("Fecha Inicio: " + evento.getFechaInicio().format(FORMATO_FECHA));
                                                    System.out.println("Fecha Fin: " + evento.getFechaFin().format(FORMATO_FECHA));
                                                    System.out.println("Ubicación: " + evento.getUbicacion());
                                                    System.out.println("Capacidad: " + evento.getCapacidadMaxima() + " personas");
                                                    System.out.println("Registrados: " + evento.getParticipantesRegistrados().size());
                                                    System.out.println("Asistentes: " + evento.getParticipantesAsistentes().size());
                                                    System.out.println("Cupos Disponibles: " + evento.getCuposDisponibles());
                                                    
                                                    if (evento.getParticipantesAsistentes().isEmpty()) {
                                                    } else {
                                                        System.out.println("Porcentaje Asistencia: " +String.format("%.2f%%", evento.calcularPorcentajeAsistencia()));
                                                    }
                                                }
                                                    /**
                                                     * Limpia la pantalla
                                                     */
                                                    private void limpiarPantalla() {
                                                        for (int i = 0; i < 50; i++) {
                                                            System.out.println();
                                                        }
                                                    }
                                                    
                                                    /**
                                                    * Pausa la ejecución
                                                    */
                                                    private void pausar() {
                                                        System.out.print("Presione ENTER para continuar...");
                                                        scanner.nextLine();
                                                    }
}