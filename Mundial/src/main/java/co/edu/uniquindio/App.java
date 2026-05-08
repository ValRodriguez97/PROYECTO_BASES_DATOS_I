package co.edu.uniquindio;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.model.*;
import co.edu.uniquindio.model.Enum.TipoUsuario;
import co.edu.uniquindio.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada principal.
 *
 * Menú de consola para verificar que todo el backend funciona.
 * La UI Swing llama a los mismos Services (MundialService, GestionDatosService,
 * SistemaSeguridadService) sin pasar por aquí.
 */
public class App {

    private static final SistemaSeguridadService seguridad = new SistemaSeguridadService();
    private static final MundialService           mundial   = new MundialService();
    private static final GestionDatosService      gestion   = new GestionDatosService(seguridad);
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Sistema Mundial 2026 - Backend ===");
        System.out.println("Verificando conexión a la base de datos...");
        try {
            ConexionBD bd = ConexionBD.getInstance();
            bd.verificarVersionMySQL();
            System.out.println("Conexión exitosa.\n");
        } catch (Exception e) {
            System.err.println("ERROR de conexión: " + e.getMessage());
            System.err.println("Verifica que MySQL esté activo y las variables DB_* estén configuradas.");
            return;
        }

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = sc.nextLine().trim();
            try {
                switch (opcion) {
                    case "1"  -> login();
                    case "2"  -> logout();
                    case "3"  -> consultaA1();
                    case "4"  -> consultaA2();
                    case "5"  -> consultaA3();
                    case "6"  -> consultaA4();
                    case "7"  -> reporteB1();
                    case "8"  -> reporteB2();
                    case "9"  -> reporteB3();
                    case "10" -> reporteB4();
                    case "11" -> crearUsuario();
                    case "0"  -> { salir = true; logout(); }
                    default   -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        try { ConexionBD.getInstance().cerrar(); } catch (Exception ignored) {}
        System.out.println("Hasta luego.");
    }

    // ────────────────────────── MENÚ ──────────────────────────

    private static void mostrarMenu() {
        String sesion = seguridad.haySesionActiva()
                ? "[" + seguridad.getUsuarioActual().getNombreUsuario() + "]"
                : "[Sin sesión]";
        System.out.println("\n" + sesion);
        System.out.println(" 1) Login");
        System.out.println(" 2) Logout");
        System.out.println("--- Consultas ---");
        System.out.println(" 3) a1 - Jugador más costoso por confederación");
        System.out.println(" 4) a2 - Partidos por estadio");
        System.out.println(" 5) a3 - Equipo más costoso por país sede");
        System.out.println(" 6) a4 - Jugadores sub-21 por equipo");
        System.out.println("--- Reportes PDF ---");
        System.out.println(" 7) b1 - Bitácora por rango de fecha");
        System.out.println(" 8) b2 - Jugadores por peso/estatura/equipo");
        System.out.println(" 9) b3 - Valor total por confederación");
        System.out.println("10) b4 - Equipos por país anfitrión");
        System.out.println("--- Admin ---");
        System.out.println("11) Crear usuario");
        System.out.println(" 0) Salir");
        System.out.print("Opción: ");
    }

    // ────────────────────────── ACCIONES ──────────────────────

    private static void login() throws Exception {
        System.out.print("Usuario: ");
        String u = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String p = sc.nextLine().trim();
        Usuario usuario = seguridad.login(u, p);
        System.out.println("Login exitoso. Bienvenido, " + usuario.getNombreUsuario()
                + " [" + usuario.getTipoUsuario() + "]");
    }

    private static void logout() {
        if (seguridad.haySesionActiva()) {
            seguridad.logout();
            System.out.println("Sesión cerrada.");
        }
    }

    private static void consultaA1() throws Exception {
        List<Jugador> lista = mundial.jugadorMasCostosoPorConfederacion();
        System.out.println("\n=== Jugador más costoso por confederación ===");
        for (Jugador j : lista) {
            System.out.printf("  %-15s | %-30s | %s %s | €%,.2f M%n",
                    j.getEquipo().getConfederacion().getNombre(),
                    j.getEquipo().getNombre(),
                    j.getNombre(), j.getApellido(),
                    j.getValorMercado());
        }
    }

    private static void consultaA2() throws Exception {
        List<co.edu.uniquindio.model.Estadio> estadios = gestion.listarEstadios();
        System.out.println("\nEstadios disponibles:");
        estadios.forEach(e -> System.out.println("  " + e.getIdEstadio() + " - " + e.getNombre()));
        System.out.print("ID del estadio: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        List<Partido> partidos = mundial.partidosPorEstadio(id);
        System.out.println("=== Partidos en el estadio ===");
        partidos.forEach(p -> System.out.println("  " + p));
    }

    private static void consultaA3() throws Exception {
        List<Object[]> lista = mundial.equipoMasCostosoPorPaisSede();
        System.out.println("\n=== Equipo más costoso por país sede ===");
        for (Object[] fila : lista) {
            System.out.printf("  %-20s | %-30s | €%,.2f M%n", fila[0], fila[1], fila[2]);
        }
    }

    private static void consultaA4() throws Exception {
        List<Object[]> lista = mundial.cantidadJugadoresSub21PorEquipo();
        System.out.println("\n=== Jugadores sub-21 por equipo ===");
        for (Object[] fila : lista) {
            System.out.printf("  %-30s | %d jugadores%n", fila[0], fila[1]);
        }
    }

    private static void reporteB1() throws Exception {
        System.out.print("Fecha-hora inicio (yyyy-MM-ddTHH:mm): ");
        LocalDateTime desde = LocalDateTime.parse(sc.nextLine().trim());
        System.out.print("Fecha-hora fin   (yyyy-MM-ddTHH:mm): ");
        LocalDateTime hasta = LocalDateTime.parse(sc.nextLine().trim());
        String ruta = mundial.generarReporteBitacora(desde, hasta);
        System.out.println("PDF generado: " + ruta);
    }

    private static void reporteB2() throws Exception {
        System.out.print("Peso mínimo (kg): ");
        BigDecimal pesoMin = new BigDecimal(sc.nextLine().trim());
        System.out.print("Peso máximo (kg): ");
        BigDecimal pesoMax = new BigDecimal(sc.nextLine().trim());
        System.out.print("Estatura mínima (m): ");
        BigDecimal estMin = new BigDecimal(sc.nextLine().trim());
        System.out.print("Estatura máxima (m): ");
        BigDecimal estMax = new BigDecimal(sc.nextLine().trim());
        System.out.print("ID equipo (0 = todos): ");
        int idEq = Integer.parseInt(sc.nextLine().trim());
        String ruta = mundial.generarReporteJugadoresFiltrados(pesoMin, pesoMax, estMin, estMax, idEq);
        System.out.println("PDF generado: " + ruta);
    }

    private static void reporteB3() throws Exception {
        List<co.edu.uniquindio.model.Confederacion> confs = gestion.listarConfederaciones();
        confs.forEach(c -> System.out.println("  " + c.getIdConfederacion() + " - " + c.getNombre()));
        System.out.print("ID confederación: ");
        int idConf = Integer.parseInt(sc.nextLine().trim());
        String nombre = confs.stream()
                .filter(c -> c.getIdConfederacion() == idConf)
                .map(co.edu.uniquindio.model.Confederacion::getNombre)
                .findFirst().orElse("Confederación " + idConf);
        String ruta = mundial.generarReporteValorPorConfederacion(idConf, nombre);
        System.out.println("PDF generado: " + ruta);
    }

    private static void reporteB4() throws Exception {
        String ruta = mundial.generarReporteEquiposPorAnfitrion();
        System.out.println("PDF generado: " + ruta);
    }

    private static void crearUsuario() throws Exception {
        if (!seguridad.haySesionActiva() || !seguridad.getUsuarioActual().puedeCrearUsuarios()) {
            System.out.println("Solo el Administrador puede crear usuarios.");
            return;
        }
        System.out.print("Nombre de usuario: ");
        String u = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String p = sc.nextLine().trim();
        System.out.print("Tipo (TRADICIONAL / ESPORADICO): ");
        TipoUsuario tipo = TipoUsuario.valueOf(sc.nextLine().trim().toUpperCase());
        seguridad.crearUsuario(u, p, tipo);
        System.out.println("Usuario creado correctamente.");
    }
}