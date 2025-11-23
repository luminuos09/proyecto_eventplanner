/**
 * MenuPagos - Gestiona el menú de pagos, tickets y finanzas
 * @author Ayner Jose Castro Benavides
 * @version 1.0
 */
package presentacion;

import logica.*;
import modelos.*;
import excepciones.*;
import java.util.Scanner;
import java.util.ArrayList;

public class MenuPagos {
    
    private final GestorEventos gestorEventos;
    private final GestorPagos gestorPagos;
    private final ReporteFinanciero reporteFinanciero;
    private final Scanner scanner;
    
    public MenuPagos(GestorEventos gestorEventos, Scanner scanner) {
        this.gestorEventos = gestorEventos;
        this.gestorPagos = GestorPagos.getInstance();
        this.reporteFinanciero = new ReporteFinanciero(gestorPagos, gestorEventos);
        this.scanner = scanner;
    }
    
    /**
     * Muestra el menú principal de pagos
     */
    public void mostrar() {
        boolean volver = false;
        
        while (!volver) {
            try {
                limpiarPantalla();
                mostrarOpciones();
                
                System.out.print("Seleccione una opción: ");
                int opcion = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                
                switch (opcion) {
                    case 1 -> comprarTicket();
                    case 2 -> verMisTickets();
                    case 3 -> verTicketsEvento();
                    case 4 -> reembolsarTicket();
                    case 5 -> reporteFinancieroEvento();
                    case 6 -> reporteFinancieroGeneral();
                    case 7 -> topEventosRentables();
                    case 8 -> configurarPrecios();
                    case 9 -> verGananciasPlataforma();
                    case 0 -> volver = true;
                    default -> MenuPrincipalConsola.mostrarError("Opción inválida");
                }
                
                if (!volver) {
                    pausar();
                }
                
            } catch (Exception e) {
                MenuPrincipalConsola.mostrarError("Error: " + e.getMessage());
                scanner.nextLine();
                pausar();
            }
        }
    }
    
    private void mostrarOpciones() {
        System.out.println("====================================================================");
        System.out.println("                      PAGOS Y FINANZAS                              ");
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("  COMPRA DE TICKETS");
        System.out.println("  ====================================================================");
        System.out.println("  1. Comprar Ticket para Evento");
        System.out.println("  2. Ver Mis Tickets");
        System.out.println("  3. Ver Tickets de un Evento");
        System.out.println("  4. Solicitar Reembolso");
        System.out.println();
        System.out.println("  REPORTES FINANCIEROS");
        System.out.println("  ====================================================================");
        System.out.println("  5. Reporte Financiero de Evento");
        System.out.println("  6. Reporte Financiero General");
        System.out.println("  7. Top Eventos Más Rentables");
        System.out.println("  9. Ver Ganancias de la Plataforma");
        System.out.println();
        System.out.println("  CONFIGURACIÓN");
        System.out.println("  ====================================================================");
        System.out.println("  8. Configurar Precios");
        System.out.println();
        System.out.println("  0. Volver al Menú Principal");
        System.out.println();
        System.out.println("====================================================================");
    }
    
    private void comprarTicket() {
        System.out.println("====================================================================");
        System.out.println("                      COMPRAR TICKET                               ");
        System.out.println("====================================================================");
        System.out.println();
        
        try {
            // Mostrar eventos disponibles
            ArrayList<Evento> eventosPublicados = gestorEventos.buscarEventosPorEstado(EstadoEvento.PUBLICADO);
            
            if (eventosPublicados.isEmpty()) {
                MenuPrincipalConsola.mostrarInfo("No hay eventos disponibles para comprar tickets.");
                return;
            }
            
            System.out.println("EVENTOS DISPONIBLES:\n");
            for (int i = 0; i < eventosPublicados.size(); i++) {
                Evento e = eventosPublicados.get(i);
                System.out.println((i + 1) + ". " + e.getNombre() + " [" + e.getTipo().getDescripcion() + "]");
                System.out.println("   ID: " + e.getId());
                System.out.println("   Cupos disponibles: " + e.getCuposDisponibles());
                System.out.println();
            }
            
            // Solicitar email del participante
            System.out.print("Email del participante: ");
            String email = scanner.nextLine();
            
            Participante participante = gestorEventos.buscarParticipantePorEmail(email);
            
            // Solicitar ID del evento
            System.out.print("\nID del evento: ");
            String eventoId = scanner.nextLine();
            
            Evento evento = gestorEventos.buscarEvento(eventoId);
            
            // Mostrar tipos de tickets disponibles
            System.out.println("====================================================================");
            System.out.println("                   TIPOS DE TICKETS DISPONIBLES                      ");
            System.out.println("====================================================================");
            System.out.println();
            
            TipoTicket[] tipos = TipoTicket.values();
            for (int i = 0; i < tipos.length; i++) {
                TipoTicket tipo = tipos[i];
                double precio = tipo.getPrecioBase();
                
                if (participante.isVip() && tipo != TipoTicket.GRATUITO) {
                    double precioDescuento = tipo.getPrecioConDescuentoVIP();
                    System.out.println((i + 1) + ". " + tipo.getDescripcion());
                    System.out.println("   Precio regular: $" + String.format("%,.0f", precio));
                    System.out.println("   Precio VIP: $" + String.format("%,.0f", precioDescuento) + " (20% OFF) ⭐");
                    System.out.println("   Beneficios: " + tipo.getBeneficios());
                } else {
                    System.out.println((i + 1) + ". " + tipo.getDescripcion() + " - $" + String.format("%,.0f", precio));
                    System.out.println("   Beneficios: " + tipo.getBeneficios());
                }
                System.out.println();
            }
            
            System.out.print("Seleccione tipo de ticket (1-" + tipos.length + "): ");
            int tipoSeleccion = scanner.nextInt();
            scanner.nextLine();
            
            if (tipoSeleccion < 1 || tipoSeleccion > tipos.length) {
                MenuPrincipalConsola.mostrarError("Opción inválida");
                return;
            }
            
            TipoTicket tipoTicket = tipos[tipoSeleccion - 1];
            
            // Mostrar métodos de pago
            System.out.println("====================================================================");
            System.out.println("                 MÉTODOS DE PAGO DISPONIBLES                         ");
            System.out.println("====================================================================");
            System.out.println();
            
            MetodoPago[] metodos = MetodoPago.values();
            for (int i = 0; i < metodos.length; i++) {
                System.out.println((i + 1) + ". " + metodos[i].toString());
            }
            
            System.out.print("\nSeleccione método de pago (1-" + metodos.length + "): ");
            int metodoSeleccion = scanner.nextInt();
            scanner.nextLine();
            
            if (metodoSeleccion < 1 || metodoSeleccion > metodos.length) {
                MenuPrincipalConsola.mostrarError("Opción inválida");
                return;
            }
            
            MetodoPago metodoPago = metodos[metodoSeleccion - 1];
            
            // Calcular total
            double precioBase = tipoTicket.getPrecioBase();
            if (participante.isVip() && tipoTicket != TipoTicket.GRATUITO) {
                precioBase = tipoTicket.getPrecioConDescuentoVIP();
            }
            
            double comision = metodoPago.calcularComision(precioBase);
            double total = precioBase + comision;
            
            // Mostrar resumen
            System.out.println("====================================================================");
            System.out.println("                     RESUMEN DE COMPRA                              ");
            System.out.println("====================================================================");
            System.out.println();
            System.out.println("Participante: " + participante.getNombre());
            System.out.println("Evento: " + evento.getNombre());
            System.out.println("Tipo de ticket: " + tipoTicket.getDescripcion());
            System.out.println("Método de pago: " + metodoPago.getDescripcion());
            System.out.println();
            System.out.println("Precio ticket: $" + String.format("%,.0f", precioBase));
            System.out.println("Comisión pago: $" + String.format("%,.0f", comision));
            System.out.println("=".repeat(40));
            System.out.println("TOTAL A PAGAR: $" + String.format("%,.0f", total));
            System.out.println();
            
            System.out.print("¿Confirmar compra? (S/N): ");
            String confirmacion = scanner.nextLine().toUpperCase();
            
            if (!confirmacion.equals("S")) {
                MenuPrincipalConsola.mostrarInfo("Compra cancelada");
                return;
            }
            
            // Procesar compra
            System.out.println("\nProcesando pago...");
            Thread.sleep(1500);
            
            Ticket ticket = gestorPagos.comprarTicket(eventoId, participante.getId(),tipoTicket, metodoPago);
            
            System.out.println();
            MenuPrincipalConsola.mostrarExito("¡Compra realizada exitosamente!");
            System.out.println();
            System.out.println(ticket.toString());
            System.out.println();
            System.out.println("Se ha enviado el ticket a: " + participante.getEmail());
            
        } catch (DatosInvalidosException e) {
            MenuPrincipalConsola.mostrarError("Participante no encontrado");
        } catch (EventoNoEncontradoException e) {
            MenuPrincipalConsola.mostrarError("Evento no encontrado");
        } catch (EventPlannerException e) {
            MenuPrincipalConsola.mostrarError(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void verMisTickets() {
        System.out.println("====================================================================");
        System.out.println("                         MIS TICKETS                                ");
        System.out.println("====================================================================");
        System.out.println();
        
        try {
            System.out.print("Email del participante: ");
            String email = scanner.nextLine();
            
            Participante participante = gestorEventos.buscarParticipantePorEmail(email);
            ArrayList<Ticket> tickets = gestorPagos.obtenerTicketsDeParticipante(participante.getId());
            
            if (tickets.isEmpty()) {
                MenuPrincipalConsola.mostrarInfo("No tiene tickets comprados");
                return;
            }
            
            System.out.println("\nTICKETS COMPRADOS (" + tickets.size() + "):\n");
            
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                Evento evento = gestorEventos.buscarEvento(ticket.getEventoId());
                
                System.out.println((i + 1) + ". " + evento.getNombre());
                System.out.println("   Tipo: " + ticket.getTipo().getDescripcion());
                System.out.println("   Precio: $" + String.format("%,.0f", ticket.getPrecio()));
                System.out.println("   Estado: " + (ticket.isUsado() ? "USADO " : "VIGENTE"));
                System.out.println("   Código QR: " + ticket.generarCodigoQR());
                System.out.println();
            }
            
        } catch (DatosInvalidosException e) {
            MenuPrincipalConsola.mostrarError("Participante no encontrado");
        } catch (EventoNoEncontradoException e) {
            MenuPrincipalConsola.mostrarError("Error al cargar evento");
        }
    }
    
    private void verTicketsEvento() {
        System.out.println("====================================================================");
        System.out.println("              TICKETS VENDIDOS DE EVENTO                            ");
        System.out.println("====================================================================");
        System.out.println();
        
        try {
            System.out.print("ID del evento: ");
            String eventoId = scanner.nextLine();
            
            Evento evento = gestorEventos.buscarEvento(eventoId);
            ArrayList<Ticket> tickets = gestorPagos.obtenerTicketsDeEvento(eventoId);
            
            System.out.println("\nEVENTO: " + evento.getNombre());
            System.out.println("Total tickets vendidos: " + tickets.size());
            System.out.println();
            
            if (tickets.isEmpty()) {
                MenuPrincipalConsola.mostrarInfo("No hay tickets vendidos para este evento");
                return;
            }
            
            // Estadísticas por tipo
            java.util.Map<TipoTicket, Integer> estadisticas = gestorPagos.obtenerEstadisticasVentas(eventoId);
            
            System.out.println("VENTAS POR TIPO:");
            for (java.util.Map.Entry<TipoTicket, Integer> entry : estadisticas.entrySet()) {
                if (entry.getValue() > 0) {
                    System.out.println("  " + entry.getKey().getDescripcion() + ": " + entry.getValue());
                }
            }
            
            System.out.println("\nIngresos totales: $" + 
                String.format("%,.0f", gestorPagos.calcularIngresosEvento(eventoId)));
            
        } catch (EventoNoEncontradoException e) {
            MenuPrincipalConsola.mostrarError("Evento no encontrado");
        }
    }
    
    private void reembolsarTicket() {
        System.out.println("====================================================================");
        System.out.println("                    SOLICITAR REEMBOLSO                             ");
        System.out.println("====================================================================");
        System.out.println();
        
        try {
            System.out.print("ID del ticket: ");
            String ticketId = scanner.nextLine();
            
            Ticket ticket = gestorPagos.buscarTicket(ticketId);
            Evento evento = gestorEventos.buscarEvento(ticket.getEventoId());
            
            System.out.println("\nTICKET A REEMBOLSAR:");
            System.out.println("Evento: " + evento.getNombre());
            System.out.println("Tipo: " + ticket.getTipo().getDescripcion());
            System.out.println("Precio: $" + String.format("%,.0f", ticket.getPrecio()));
            System.out.println();
            
            System.out.print("¿Confirmar reembolso? (S/N): ");
            String confirmacion = scanner.nextLine().toUpperCase();
            
            if (!confirmacion.equals("S")) {
                MenuPrincipalConsola.mostrarInfo("Reembolso cancelado");
                return;
            }
            
            boolean exito = gestorPagos.reembolsarTicket(ticketId);
            
            if (exito) {
                MenuPrincipalConsola.mostrarExito("Reembolso procesado exitosamente");
                System.out.println("El dinero será devuelto en 3-5 días hábiles");
            } else {
                MenuPrincipalConsola.mostrarError("No se pudo procesar el reembolso");
            }
            
        } catch (EventPlannerException e) {
            MenuPrincipalConsola.mostrarError(e.getMessage());
        }
    }
    
    private void reporteFinancieroEvento() {
        System.out.println("====================================================================");
        System.out.println("          REPORTE FINANCIERO DE EVENTO                              ");
        System.out.println("====================================================================");
        System.out.println();
        
        try {
            System.out.print("ID del evento: ");
            String eventoId = scanner.nextLine();
            
            limpiarPantalla();
            String reporte = reporteFinanciero.generarReporteEvento(eventoId);
            System.out.println(reporte);
            
        } catch (EventPlannerException e) {
            MenuPrincipalConsola.mostrarError(e.getMessage());
        }
    }
    
    private void reporteFinancieroGeneral() {
        limpiarPantalla();
        System.out.println(reporteFinanciero.generarReporteGeneral());
    }
    
    private void topEventosRentables() {
        System.out.print("¿Cuántos eventos desea ver? (1-10): ");
        int limite = scanner.nextInt();
        scanner.nextLine();
        
        if (limite < 1) limite = 5;
        if (limite > 10) limite = 10;
        
        limpiarPantalla();
        System.out.println(reporteFinanciero.generarTopEventosRentables(limite));
    }
    
    private void configurarPrecios() {
        System.out.println("====================================================================");
        System.out.println("                     CONFIGURAR PRECIOS                             ");
        System.out.println("====================================================================");
        System.out.println();
        
        MenuPrincipalConsola.mostrarInfo("Funcionalidad de configuración de precios personalizada");
        MenuPrincipalConsola.mostrarInfo("(En desarrollo - Próximamente)");
    }
    
    private void verGananciasPlataforma() {
        limpiarPantalla();
        System.out.println("====================================================================");
        System.out.println("                  GANANCIAS DE LA PLATAFORMA                        ");
        System.out.println("====================================================================");
        System.out.println();
        
        double ganancias = gestorPagos.calcularGananciasPlataforma();
        
        System.out.println(" RESUMEN DE GANANCIAS");
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("Comisión aplicada: 5% por transacción");
        System.out.println();
        System.out.println("Ganancias totales de la plataforma:");
        System.out.println("   $" + String.format("%,.0f COP", ganancias));
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();
        System.out.println("Estas ganancias provienen de:");
        System.out.println("    Comisión del 5% sobre cada ticket vendido");
        System.out.println("    Aplicable a todos los tipos de ticket (excepto gratuitos)");
        System.out.println("    Fuente de sostenibilidad del sistema");
        System.out.println();
    }
    
    private void limpiarPantalla() {
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }
    
    private void pausar() {
        System.out.print("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }
    
    public GestorPagos getGestorPagos() {
        return gestorPagos;
    }
}