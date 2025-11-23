/**
 * MenuParticipantes - Gestionar el menú de participantes y organizadores
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion;

import logica.*;
import modelos.*;
import excepciones.*;

import java.io.IOException;
import java.util.*;

public class MenuParticipantes {
    
    private final GestorEventos gestor;
    private final Scanner scanner;
    
    /**
     * Constructor
     * @param gestor Instancia del gestor de eventos
     * @param scanner 
     */
    public MenuParticipantes(GestorEventos gestor, Scanner scanner) {
        this.gestor = gestor;
        this.scanner = scanner;
    }
    
    /**
     * Muestra el menú de participantes
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
                    case 1 -> registrarParticipante();
                    case 2 -> inscribirEnEvento();
                    case 3 -> cancelarInscripcion();
                    case 4 -> verMisEventos();
                    case 5 -> realizarCheckIn();
                    case 6 -> buscarParticipantePorEmail();
                    case 7 -> listarParticipantes();
                    case 8 -> actualizarPerfil();
                    case 0 -> volver = true;
                    default -> MenuPrincipalConsola.mostrarError("Opción inválida");
                }
                
                if (!volver) {
                    pausar();
                }
                
            } catch (Exception e) {
                MenuPrincipalConsola.mostrarError("Entrada inválida: " + e.getMessage());
                scanner.nextLine();
                pausar();
            }
        }
    }
        /**
         * Muestra las opciones del menú
         */
        private void mostrarOpciones() {
            System.out.println("====================================================================");
            System.out.println("                   GESTIÓN DE PARTICIPANTES                           ");
            System.out.println("====================================================================");
            System.out.println();
            System.out.println("  1.  Registrar Participante");
            System.out.println("  2.  Inscribirse a un Evento");
            System.out.println("  3.  Cancelar Inscripción");
            System.out.println("  4.  Ver Mis Eventos");
            System.out.println("  5.  Realizar Check-in");
            System.out.println("  6.  Buscar Participante por Email");
            System.out.println("  7.  Listar Todos los Participantes");
            System.out.println("  8.  Actualizar Perfil");
            System.out.println("  0.  Volver al Menú Principal");
            System.out.println();
            System.out.println("====================================================================");
        }
            /**
             * Registra un nuevo participante
             */
                private void registrarParticipante() {
                    System.out.println("====================================================================");
                    System.out.println("                    REGISTRAR PARTICIPANTE");
                    System.out.println("====================================================================");
                    System.out.println();
                    
                    try {
                        // ========== 1. NOMBRE COMPLETO ==========
                        String nombre = null;
                        do {
                            try {
                                System.out.print("Nombre completo: ");
                                String input = scanner.nextLine().trim();
                                ValidarDatos.validarNombre(input);
                                nombre = input;
                                break; // Nombre válido
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);
                        
                        // ========== 2. EMAIL ==========
                        String email = null;
                        do {
                            try {
                                System.out.print("Email: ");
                                String input = scanner.nextLine().trim();
                                
                                // Validar formato
                                ValidarDatos.validarEmail(input);
                                
                                // Verificar que no exista
                                try {
                                    gestor.buscarParticipantePorEmail(input);
                                    // Si llega aquí, el email ya existe
                                    MenuPrincipalConsola.mostrarError("Ya existe un participante registrado con ese email");
                                } catch (EventPlannerException e) {
                                    // Email no existe, podemos usarlo
                                    email = input;
                                    break;
                                }
                                
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);
                        
                        // ========== 3. TELÉFONO ==========
                        String telefono = null;
                        do {
                            try {
                                System.out.print("Teléfono: ");
                                String input = scanner.nextLine().trim();
                                ValidarDatos.validarTelefono(input);
                                telefono = input;
                                break; // Teléfono válido
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError(e.getMessage());
                            }
                        } while (true);
                        
                        // ========== 4. EMPRESA ==========
                        String empresa = null;
                        do {
                            try {
                                System.out.print("Empresa: ");
                                empresa = scanner.nextLine().trim();
                                
                                if (empresa.isEmpty()) {
                                    MenuPrincipalConsola.mostrarError("La empresa no puede estar vacía");
                                    continue;
                                }
                                
                                if (empresa.length() < 2) {
                                    MenuPrincipalConsola.mostrarError("La empresa debe tener al menos 2 caracteres");
                                    continue;
                                }
                                
                                break; // Empresa valida
                            } catch (Exception e) {
                                MenuPrincipalConsola.mostrarError("Error al ingresar empresa");
                            }
                        } while (true);
                        
                        // ========== 5. CARGO ==========
                        String cargo = null;
                        do {
                            try {
                                System.out.print("Cargo: ");
                                cargo = scanner.nextLine().trim();
                                
                                if (cargo.isEmpty()) {
                                    MenuPrincipalConsola.mostrarError("El cargo no puede estar vacío");
                                    continue;
                                }
                                
                                if (cargo.length() < 2) {
                                    MenuPrincipalConsola.mostrarError("El cargo debe tener al menos 2 caracteres");
                                    continue;
                                }
                                
                                break; // Cargo valido
                            } catch (Exception e) {
                                MenuPrincipalConsola.mostrarError("Error al ingresar cargo");
                            }
                        } while (true);
                        
                        // ========== 6. INTERESES ==========
                        String intereses = null;
                        do {
                            try {
                                System.out.print("Intereses (separados por coma): ");
                                intereses = scanner.nextLine().trim();
                                
                                if (intereses.isEmpty()) {
                                    MenuPrincipalConsola.mostrarError("Debe ingresar al menos un interes");
                                    continue;
                                }
                                
                                if (intereses.length() < 3) {
                                    MenuPrincipalConsola.mostrarError("Los intereses deben tener al menos 3 caracteres");
                                    continue;
                                }
                                
                                break; // Intereses válidos
                            } catch (Exception e) {
                                MenuPrincipalConsola.mostrarError("Error al ingresar intereses");
                            }
                        } while (true);
                        
                        // ========== 7. VIP (OPCIONAL) ==========
                        boolean vip = false;
                        do {
                            try {
                                System.out.print("¿Es participante VIP? (S/N): ");
                                String respuestaVip = scanner.nextLine().trim().toUpperCase();
                                
                                if (respuestaVip.equals("S") || respuestaVip.equals("SI")) {
                                    vip = true;
                                    break;
                                } else if (respuestaVip.equals("N") || respuestaVip.equals("NO")) {
                                    vip = false;
                                    break;
                                } else {
                                    MenuPrincipalConsola.mostrarError("Respuesta inválida. Ingrese S o N");
                                }
                            } catch (Exception e) {
                                MenuPrincipalConsola.mostrarError("Error al procesar respuesta");
                            }
                        } while (true);
                        
                        // ========== 8. REGISTRAR PARTICIPANTE ==========
                        Participante participante = gestor.registrarParticipante(nombre,  email,  telefono,  empresa,  cargo,  intereses, vip);
                        
                        // ========== 9. MOSTRAR CONFIRMACIÓN ==========
                        System.out.println();
                        System.out.println("=".repeat(40));
                        MenuPrincipalConsola.mostrarExito("¡Participante registrado exitosamente!");
                        
                        if (vip) {
                            System.out.println("Participante VIP registrado con beneficios especiales");
                        }
                        
                        System.out.println("\nDETALLES DEL PARTICIPANTE:");
                        System.out.println("=".repeat(40));
                        mostrarDetallesParticipante(participante);
                        
                    } catch (EventPlannerException e) {
                        MenuPrincipalConsola.mostrarError("Error al registrar participante: " + e.getMessage());
                    } catch (Exception e) {
                        MenuPrincipalConsola.mostrarError("Error inesperado: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                /**
                 * Inscribe un participante a un evento
                 */
                private void inscribirEnEvento() {
                    System.out.println("====================================================================");
                    System.out.println("                    INSCRIBIRSE A EVENTO                             ");
                    System.out.println("====================================================================");
                    System.out.println();
                    
                    try {
                        // Mostrar eventos disponibles
                        System.out.println(" EVENTOS DISPONIBLES:");
                        
                        ArrayList<Evento> eventosPublicados = gestor.buscarEventosPorEstado(EstadoEvento.PUBLICADO);
                         ArrayList<Organizador> organizadores = gestor.obtenerTodosOrganizadores();
                        if (eventosPublicados.isEmpty()) {
                            MenuPrincipalConsola.mostrarInfo("No hay eventos disponibles para inscripción en este momento.");
                            return;
                        }
                        
                        for (int i = 0; i < eventosPublicados.size(); i++) {
                            Evento evento = eventosPublicados.get(i);
                            Organizador org = organizadores.get(i);
                            System.out.println((i + 1) + ". " + evento.getNombre() + " [" + evento.getTipo().getDescripcion() + "]");
                            System.out.println("   ID: " + evento.getId());
                            System.out.println("   Cupos: " + evento.getCuposDisponibles() + "/" + evento.getCapacidadMaxima());
                            System.out.println("   Ubicación: " + evento.getUbicacion());
                            System.out.println("   Email del Organizador: "+org.getEmail());
                            System.out.println();
                        }
                        
                        System.out.print("Email del participante: ");
                        String email = scanner.nextLine();
                        
                        Participante participante = gestor.buscarParticipantePorEmail(email);
                        
                        System.out.print("\nID del evento al que desea inscribirse: ");
                        String eventoId = scanner.nextLine();
                        
                        Evento evento = gestor.buscarEvento(eventoId);
                        
                        // Verificar si ya está inscrito
                        if (participante.getEventosRegistrados().contains(eventoId)) {
                            MenuPrincipalConsola.mostrarAdvertencia("Ya está inscrito en este evento.");
                            return;
                        }
                        
                        gestor.inscribirParticipante(participante.getId(), eventoId);
                        
                        
                        MenuPrincipalConsola.mostrarExito("¡Inscripción exitosa!");
                        System.out.println("\nDETALLES DE LA INSCRIPCIÓN:");
                        System.out.println("Participante: " + participante.getNombre());
                        System.out.println("Email: " + participante.getEmail());
                        System.out.println("Evento: " + evento.getNombre());
                        System.out.println("Tipo: " + evento.getTipo().getDescripcion());
                        System.out.println("Ubicación: " + evento.getUbicacion());
                        System.out.println("Vip: "+(participante.isVip()?"SI" : "No"));
                        System.out.println("Cupos restantes: " + evento.getCuposDisponibles());
                        
                    } catch (DatosInvalidosException e) {
                        MenuPrincipalConsola.mostrarError("No se encontró: " + e.getMessage());
                    } catch (EventoNoEncontradoException e) {
                        MenuPrincipalConsola.mostrarError("Evento no encontrado.");
                    } catch (EventPlannerException e) {
                        MenuPrincipalConsola.mostrarError(e.getMessage());
                    }
                }
                    /**
                     * Cancela la inscripción de un participante
                     */
                    private void cancelarInscripcion() {
                        System.out.println("====================================================================");
                        System.out.println("                    CANCELAR INSCRIPCIÓN                             ");
                        System.out.println("====================================================================");
                        System.out.println();
                        
                        try {
                            System.out.print("Email del participante: ");
                            var email = scanner.nextLine();
                            
                            Participante participante = gestor.buscarParticipantePorEmail(email);
                            
                            if (participante.getEventosRegistrados().isEmpty()) {
                                MenuPrincipalConsola.mostrarInfo("No tiene inscripciones activas.");
                                return;
                            }
                            
                            System.out.println("\nSUS EVENTOS REGISTRADOS:");
                            
                            
                            ArrayList<String> eventosIds = participante.getEventosRegistrados();
                            for (int i = 0; i < eventosIds.size(); i++) {
                                try {
                                    Evento evento = gestor.buscarEvento(eventosIds.get(i));
                                    System.out.println((i + 1) + ". " + evento.getNombre());
                                    System.out.println("   ID: " + evento.getId());
                                    System.out.println("   Estado: " + evento.getEstado().getDescripcion());
                                    System.out.println();
                                } catch (EventoNoEncontradoException e) {
                                    // Evento no encontrado, continuar
                                }
                            }
                            
                            System.out.print("ID del evento a cancelar: ");
                            String eventoId = scanner.nextLine();
                            
                            System.out.print("\n ¿Está seguro que desea cancelar la inscripción? (S/N): ");
                            String confirmacion = scanner.nextLine().trim().toUpperCase();
                            
                            if (confirmacion.equals("S") || confirmacion.equals("SI") || confirmacion.equals("SÍ")) {
                                gestor.cancelarInscripcion(participante.getId(),eventoId);
                                MenuPrincipalConsola.mostrarExito("Inscripción cancelada exitosamente.");
                            } else 
                            {
                                MenuPrincipalConsola.mostrarInfo("Cancelación abortada.");
                            }
                            
                    } catch (DatosInvalidosException | IOException e) {
                            MenuPrincipalConsola.mostrarError("Participante no encontrado.");
                        } catch (EventPlannerException e) {
                            MenuPrincipalConsola.mostrarError(e.getMessage());
                        }
                    }
                        /**
                         * Muestra los eventos de un participante
                         */
                        private void verMisEventos() {
                            System.out.println("====================================================================");
                            System.out.println("                        MIS EVENTOS                                  ");
                            System.out.println("====================================================================");
                            System.out.println();
                            
                            try {
                                System.out.print("Email del participante: ");
                                var email = scanner.nextLine();
                                
                                Participante participante = gestor.buscarParticipantePorEmail(email);
                                
                                ArrayList<String> eventosIds = participante.getEventosRegistrados();
                                
                                if (eventosIds.isEmpty()) {
                                    MenuPrincipalConsola.mostrarInfo("No está inscrito en ningún evento.");
                                    return;
                                }
                                
                                System.out.println("\nEVENTOS REGISTRADOS (" + eventosIds.size() + "):");
                                
                                
                                for (int i = 0; i < eventosIds.size(); i++) {
                                    try {
                                        Evento evento = gestor.buscarEvento(eventosIds.get(i));
                                        
                                        System.out.println("\n" + (i + 1) + ". " + evento.getNombre());
                                        System.out.println("   ID: " + evento.getId());
                                        System.out.println("   Tipo: " + evento.getTipo().getDescripcion());
                                        System.out.println("   Estado: " + evento.getEstado().getDescripcion());
                                        System.out.println("   Ubicación: " + evento.getUbicacion());
                                        
                                        // Verificar si hizo check-in
                                        boolean hizoCheckIn = evento.getParticipantesAsistentes().contains(participante.getId());
                                        if (hizoCheckIn) {
                                            System.out.println("   Check-in realizado");
                                        } else {
                                            System.out.println("   Pendiente de asistencia");
                                        }
                                        
                                        
                                        
                                    } catch (EventoNoEncontradoException e) {
                                        System.out.println("\n" + (i + 1) + ". [Evento no encontrado]");
                                    }
                                }
                                
                            } catch (DatosInvalidosException e) {
                                MenuPrincipalConsola.mostrarError("Participante no encontrado.");
                            }
                        }
                        
                            /**
                             * Realiza el check-in de un participante en un evento
                             */
                            private void realizarCheckIn() {
                                System.out.println("====================================================================");
                                System.out.println("                        REALIZAR CHECK-IN                            ");
                                System.out.println("====================================================================");
                                System.out.println();
                                
                                try {
                                    System.out.print("Email del participante: ");
                                    var email = scanner.nextLine();
                                    
                                    Participante participante = gestor.buscarParticipantePorEmail(email);
                                    
                                    System.out.print("ID del evento: ");
                                    var eventoId = scanner.nextLine();
                                    
                                    Evento evento = gestor.buscarEvento(eventoId);
                                    
                                    // Verificar que está inscrito
                                    if (!participante.getEventosRegistrados().contains(eventoId)) {
                                        MenuPrincipalConsola.mostrarError("No está inscrito en este evento.");
                                        return;
                                    }
                                    
                                    // Verificar si ya hizo check-in
                                    if (evento.getParticipantesAsistentes().contains(participante.getId())) {
                                        MenuPrincipalConsola.mostrarAdvertencia("Ya realizó check-in en este evento.");
                                        return;
                                    }
                                    
                                    gestor.realizarCheckIn(participante.getId(), eventoId);
                                    
                                    
                                    MenuPrincipalConsola.mostrarExito("¡Check-in realizado exitosamente!");
                                    System.out.println("\nCONFIRMACIÓN:");
                                    System.out.println("─".repeat(70));
                                    System.out.println("Participante: " + participante.getNombre());
                                    System.out.println("Evento: " + evento.getNombre());
                                    System.out.println("Hora: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                                    System.out.println("\n¡Bienvenido al evento!");
                                    
                                } catch (DatosInvalidosException e) {
                                    MenuPrincipalConsola.mostrarError("Participante no encontrado.");
                                } catch (EventoNoEncontradoException e) {
                                    MenuPrincipalConsola.mostrarError("Evento no encontrado.");
                                } catch (EventPlannerException e) {
                                    MenuPrincipalConsola.mostrarError(e.getMessage());
                                }
                            }
                                /**
                                 * Busca un participante por email
                                 */
                                private void buscarParticipantePorEmail() {
                                    System.out.println("====================================================================");
                                    System.out.println("                    BUSCAR PARTICIPANTE                              ");
                                    System.out.println("====================================================================");
                                    System.out.println();
                                    
                                    try {
                                        System.out.print("Email del participante: ");
                                        String email = scanner.nextLine();
                                        
                                        Participante participante = gestor.buscarParticipantePorEmail(email);
                                        
                                        MenuPrincipalConsola.mostrarExito("Participante encontrado!");
                                        mostrarDetallesParticipante(participante);
                                        
                                    } catch (DatosInvalidosException e) {
                                        MenuPrincipalConsola.mostrarError("No se encontró ningún participante con ese email.");
                                    }
                                }
                                
                                /**
                                 * Lista todos los participantes
                                 */
                                    private void listarParticipantes() {
                                        System.out.println("====================================================================");
                                        System.out.println("                    LISTA DE PARTICIPANTES                           ");
                                        System.out.println("====================================================================");
                                        System.out.println();
                                        
                                        ArrayList<Participante> participantes = gestor.obtenerTodosParticipantes();
                                        
                                        if (participantes.isEmpty()) {
                                            MenuPrincipalConsola.mostrarInfo("No hay participantes registrados.");
                                            return;
                                        }
                                        
                                        System.out.println("Total de participantes: " + participantes.size());
                                        
                                        for (int i = 0; i < participantes.size(); i++) {
                                            Participante p = participantes.get(i);
                                            System.out.println("\n" + (i + 1) + ". " + p.getNombre());
                                            System.out.println("   ID: " + p.getId());
                                            System.out.println("   Email: " + p.getEmail());
                                            System.out.println("   Empresa: " + p.getEmpresa() + " | Cargo: " + p.getCargo());
                                            System.out.println("   Eventos registrados: " + p.getEventosRegistrados().size());
                                        }
                                    }
                                    
                                        /**
                                         * Actualiza el perfil de un participante
                                         */
                                        private void actualizarPerfil() {
                                            System.out.println("====================================================================");
                                            System.out.println("                    ACTUALIZAR PERFIL                                 ");
                                            System.out.println("====================================================================");
                                            System.out.println();
                                            
                                            try {
                                                System.out.print("Email del participante: ");
                                                var email = scanner.nextLine();
                                                
                                                Participante participante = gestor.buscarParticipantePorEmail(email);
                                                
                                                System.out.println("\n Datos actuales:");
                                                mostrarDetallesParticipante(participante);
                                                
                                                System.out.println("\n Deje en blanco para mantener el valor actual");
                                                
                                                
                                                System.out.print("\nNuevo nombre [" + participante.getNombre() + "]: ");
                                                var nombre = scanner.nextLine();
                                                if (!nombre.isEmpty()){
                                                    participante.setNombre(nombre);
                                                }
                                                
                                                System.out.print("Nuevo teléfono [" + participante.getTelefono() + "]: ");
                                                var telefono = scanner.nextLine();
                                                if (!telefono.isEmpty()) {
                                                    participante.setTelefono(telefono);
                                                }
                                                
                                                System.out.print("Nueva empresa [" + participante.getEmpresa() + "]: ");
                                                var empresa = scanner.nextLine();
                                                if (!empresa.isEmpty()) {
                                                    participante.setEmpresa(empresa);
                                                }
                                                
                                                System.out.print("Nuevo cargo [" + participante.getCargo() + "]: ");
                                                var cargo = scanner.nextLine();
                                                if (!cargo.isEmpty()) {
                                                    participante.setCargo(cargo);
                                                }
                                                MenuPrincipalConsola.mostrarExito("Perfil actualizado exitosamente!");
                                                
                                            } catch (DatosInvalidosException e) {
                                                MenuPrincipalConsola.mostrarError("Participante no encontrado.");
                                            }
                                        }
                                            
                                            /**
                                             * Registra un nuevo organizador
                                             */
                                            public void registrarOrganizador() {
                                                System.out.println("====================================================================");
                                                System.out.println("                    REGISTRAR ORGANIZADOR");
                                                System.out.println("====================================================================");
                                                System.out.println();
                                                
                                                try {
                                                    // ========== 1. NOMBRE COMPLETO ==========
                                                    String nombre = null;
                                                    do {
                                                        try {
                                                            System.out.print("Nombre completo: ");
                                                            String input = scanner.nextLine().trim();
                                                            ValidarDatos.validarNombre(input);
                                                            nombre = input;
                                                            break; // Nombre válido
                                                        } catch (DatosInvalidosException e) {
                                                            MenuPrincipalConsola.mostrarError(e.getMessage());
                                                        }
                                                    } while (true);
                                                    
                                                    // ========== 2. EMAIL ==========
                                                    String email = null;
                                                    do {
                                                        try {
                                                            System.out.print("Email: ");
                                                            String input = scanner.nextLine().trim();
                                                            
                                                            // Validar formato
                                                            ValidarDatos.validarEmail(input);
                                                            
                                                            // Verificar que no exista
                                                            try {
                                                                gestor.buscarOrganizadorPorEmail(input);
                                                                // Si llega aquí, el email ya existe
                                                                MenuPrincipalConsola.mostrarError("Ya existe un organizador registrado con ese email");
                                                            } catch (EventPlannerException e) {
                                                                // Email no existe, podemos usarlo
                                                                email = input;
                                                                break;
                                                            }
                                                            
                                                        } catch (DatosInvalidosException e) {
                                                            MenuPrincipalConsola.mostrarError(e.getMessage());
                                                        }
                                                    } while (true);
                                                    
                                                    // ========== 3. TELEFONO ==========
                                                    String telefono = null;
                                                    do {
                                                        try {
                                                            System.out.print("Teléfono: ");
                                                            String input = scanner.nextLine().trim();
                                                            ValidarDatos.validarTelefono(input);
                                                            telefono = input;
                                                            break; // Teléfono válido
                                                        } catch (DatosInvalidosException e) {
                                                            MenuPrincipalConsola.mostrarError(e.getMessage());
                                                        }
                                                    } while (true);
                                                    
                                                    // ========== 4. ORGANIZACIÓN ==========
                                                    String organizacion = null;
                                                    do {
                                                        try {
                                                            System.out.print("Organización: ");
                                                            organizacion = scanner.nextLine().trim();
                                                            
                                                            if (organizacion.isEmpty()) {
                                                                MenuPrincipalConsola.mostrarError("La organización no puede estar vacía");
                                                                continue;
                                                            }
                                                            
                                                            if (organizacion.length() < 2) {
                                                                MenuPrincipalConsola.mostrarError("La organización debe tener al menos 2 caracteres");
                                                                continue;
                                                            }
                                                            
                                                            if (organizacion.length() > 100) {
                                                                MenuPrincipalConsola.mostrarError("La organización no puede exceder 100 caracteres");
                                                                continue;
                                                            }
                                                            
                                                            break; // Organización válida
                                                        } catch (Exception e) {
                                                            MenuPrincipalConsola.mostrarError("Error al ingresar organización");
                                                        }
                                                    } while (true);
                                                    
                                                    // ========== 5. DEPARTAMENTO ==========
                                                    String departamento = null;
                                                    do {
                                                        try {
                                                            System.out.print("Departamento: ");
                                                            departamento = scanner.nextLine().trim();
                                                            
                                                            if (departamento.isEmpty()) {
                                                                MenuPrincipalConsola.mostrarError("El departamento no puede estar vacío");
                                                                continue;
                                                            }
                                                            
                                                            if (departamento.length() < 2) {
                                                                MenuPrincipalConsola.mostrarError("El departamento debe tener al menos 2 caracteres");
                                                                continue;
                                                            }
                                                            
                                                            if (departamento.length() > 50) {
                                                                MenuPrincipalConsola.mostrarError("El departamento no puede exceder 50 caracteres");
                                                                continue;
                                                            }
                                                            
                                                            break; // Departamento válido
                                                        } catch (Exception e) {
                                                            MenuPrincipalConsola.mostrarError("Error al ingresar departamento");
                                                        }
                                                    } while (true);
                                                    
                                                    // ========== 6. AÑOS DE EXPERIENCIA ==========
                                                    int experiencia = 0;
                                                    do {
                                                        try {
                                                            System.out.print("Años de experiencia (0-50): ");
                                                            experiencia = scanner.nextInt();
                                                            scanner.nextLine(); // Limpiar buffer
                                                            
                                                            // Validar rango
                                                            if (experiencia < 0) {
                                                                MenuPrincipalConsola.mostrarError("La experiencia no puede ser negativa");
                                                                continue;
                                                            }
                                                            
                                                            if (experiencia > 50) {
                                                                MenuPrincipalConsola.mostrarError("La experiencia no puede exceder 50 años");
                                                                continue;
                                                            }
                                                            
                                                            break; // Experiencia válida
                                                            
                                                        } catch (InputMismatchException e) {
                                                            scanner.nextLine(); // Limpiar buffer
                                                            MenuPrincipalConsola.mostrarError("Debe ingresar un número válido");
                                                        }
                                                    } while (true);
                                                    
                                                    // ========== 7. REGISTRAR ORGANIZADOR ==========
                                                    Organizador organizador = gestor.registrarOrganizador(
                                                        nombre, 
                                                        email, 
                                                        telefono, 
                                                        organizacion, 
                                                        departamento, 
                                                        experiencia
                                                    );
                                                    
                                                    // ========== 8. MOSTRAR CONFIRMACIÓN ==========
                                                    System.out.println();
                                                    System.out.println("=".repeat(70));
                                                    MenuPrincipalConsola.mostrarExito("¡Organizador registrado exitosamente!");
                                                    
                                                    // Mostrar mensaje adicional según experiencia
                                                    if (experiencia >= 10) {
                                                        System.out.println("👔 Organizador experimentado con " + experiencia + " años en el área");
                                                    } else if (experiencia >= 5) {
                                                        System.out.println("📋 Organizador con experiencia intermedia");
                                                    } else if (experiencia > 0) {
                                                        System.out.println("🌱 Organizador con experiencia inicial");
                                                    } else {
                                                        System.out.println("🆕 Nuevo organizador sin experiencia previa");
                                                    }
                                                    
                                                    System.out.println("\nDETALLES DEL ORGANIZADOR:");
                                                    System.out.println("=".repeat(70));
                                                    mostrarDetallesOrganizador(organizador);
                                                    
                                                } catch (EventPlannerException e) {
                                                    MenuPrincipalConsola.mostrarError("Error al registrar organizador: " + e.getMessage());
                                                } catch (Exception e) {
                                                    MenuPrincipalConsola.mostrarError("Error inesperado: " + e.getMessage());
                                                    e.printStackTrace();
                                                }
                                            }
                                                /**
                                                 * Busca un organizador por email
                                                 */
                                                public void buscarOrganizadorPorEmail() {
                                                    System.out.println("====================================================================");
                                                    System.out.println("                    BUSCAR ORGANIZADOR                               ");
                                                    System.out.println("====================================================================");
                                                    System.out.println();
                                                    
                                                    try {
                                                        System.out.print("Email del organizador: ");
                                                        var email = scanner.nextLine();
                                                        
                                                        Organizador organizador = gestor.buscarOrganizadorPorEmail(email);
                                                        
                                                        MenuPrincipalConsola.mostrarExito("Organizador encontrado!");
                                                        mostrarDetallesOrganizador(organizador);
                                                        
                                                    } catch (DatosInvalidosException e) {
                                                        MenuPrincipalConsola.mostrarError("No se encontró ningún organizador con ese email.");
                                                    }
                                                }
                                            
                                                    /**
                                                     * Lista todos los organizadores
                                                     */
                                                    public void listarOrganizadores() {
                                                        System.out.println("====================================================================");
                                                        System.out.println("                    LISTA DE ORGANIZADORES                           ");
                                                        System.out.println("====================================================================");
                                                        System.out.println();
                                                        
                                                        ArrayList<Organizador> organizadores = gestor.obtenerTodosOrganizadores();
                                                        
                                                        if (organizadores.isEmpty()) {
                                                            MenuPrincipalConsola.mostrarInfo("No hay organizadores registrados.");
                                                            return;
                                                        }
                                                        
                                                        System.out.println("Total de organizadores: " + organizadores.size());
                                                        
                                                        for (int i = 0; i < organizadores.size(); i++) {
                                                            Organizador org = organizadores.get(i);
                                                            System.out.println("\n" + (i + 1) + ". " + org.getNombre());
                                                            System.out.println("   ID: " + org.getId());
                                                            System.out.println("   Email: " + org.getEmail());
                                                            System.out.println("   Organización: " + org.getOrganizacion());
                                                            System.out.println("   Experiencia: " + org.getExperienciaAnios() + " años");
                                                            System.out.println("   Eventos creados: " + org.getEventosCreados().size());
                                                            System.out.println("   ─".repeat(40));
                                                        }
                                                    }
                                                    
                                                        /**
                                                         * Ver eventos de un organizador específico
                                                         */
                                                        public void verEventosDeOrganizador() {
                                                            System.out.println("====================================================================");
                                                            System.out.println("                EVENTOS DEL ORGANIZADOR                              ");
                                                            System.out.println("====================================================================");
                                                            System.out.println();
                                                            
                                                            try {
                                                                System.out.print("Email del organizador: ");
                                                                var email = scanner.nextLine();
                                                                
                                                                Organizador organizador = gestor.buscarOrganizadorPorEmail(email);
                                                                
                                                                ArrayList<Evento> eventos = gestor.obtenerEventosDeOrganizador(organizador.getId());
                                                                
                                                                if (eventos.isEmpty()) {
                                                                    MenuPrincipalConsola.mostrarInfo("Este organizador no ha creado eventos aún.");
                                                                    return;
                                                                }
                                                                
                                                                System.out.println("\nEVENTOS DE " + organizador.getNombre().toUpperCase());
                                                                System.out.println("Total: " + eventos.size() + " eventos\n");
                                                                
                                                                for (int i = 0; i < eventos.size(); i++) {
                                                                    Evento evento = eventos.get(i);
                                                                    System.out.println((i + 1) + ". " + evento.getNombre());
                                                                    System.out.println("   Tipo: " + evento.getTipo().getDescripcion() + "|Estado: " + evento.getEstado().getDescripcion());
                                                                    System.out.println("   Registrados: " + evento.getParticipantesRegistrados().size());
                                                                    System.out.println();
                                                                }
                                                                
                                                            } catch (DatosInvalidosException e) {
                                                                MenuPrincipalConsola.mostrarError("Organizador no encontrado.");
                                                            } catch (EventPlannerException e) {
                                                                MenuPrincipalConsola.mostrarError(e.getMessage());
                                                            }
                                                        }
                                                            /**
                                                             * Muestra los detalles de un participante
                                                             */
                                                            private void mostrarDetallesParticipante(Participante p) {
                                                                System.out.println("ID: " + p.getId());
                                                                System.out.println("Nombre: " + p.getNombre());
                                                                System.out.println("Email: " + p.getEmail());
                                                                System.out.println("Teléfono: " + p.getTelefono());
                                                                System.out.println("Empresa: " + p.getEmpresa());
                                                                System.out.println("Cargo: " + p.getCargo());
                                                                System.out.println("Intereses: " + p.getIntereses());
                                                                System.out.println("VIP: " + (p.isVip() ? "Sí" : "No"));
                                                                System.out.println("Eventos registrados: " + p.getEventosRegistrados().size());
                                                                System.out.println("Fecha de registro: " + p.getFechaRegistro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                                                            }
                                                            
                                                            /**
                                                             * Muestra los detalles de un organizador
                                                             */
                                                            private void mostrarDetallesOrganizador(Organizador org) {
                                                                System.out.println("ID: " + org.getId());
                                                                System.out.println("Nombre: " + org.getNombre());
                                                                System.out.println("Email: " + org.getEmail());
                                                                System.out.println("Teléfono: " + org.getTelefono());
                                                                System.out.println("Organización: " + org.getOrganizacion());
                                                                System.out.println("Departamento: " + org.getDepartamento());
                                                                System.out.println("Experiencia: " + org.getExperienciaAnios() + " años");
                                                                System.out.println("Eventos creados: " + org.getEventosCreados().size());
                                                                System.out.println("Fecha de registro: " + org.getFechaRegistro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                                                            }
                                                            
                                                            /**
                                                             * Limpia la pantalla
                                                             */
                                                            private void limpiarPantalla() {
                                                                for (int i = 0; i < 3; i++) {
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