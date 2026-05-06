package co.edu.uniquindio.services;

import co.edu.uniquindio.dao.*;
import co.edu.uniquindio.dao.Impl.*;
import co.edu.uniquindio.model.Jugador;
import co.edu.uniquindio.model.Partido;
import co.edu.uniquindio.Utils.GeneradorReportes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

/** 
 * Servicio de negocio para las consultas y reportes.
 *
 * CONSULTAS (a):
 *   a1 - jugadorMasCostosoPorConfederacion()
 *   a2 - partidosPorEstadio(idEstadio)
 *   a3 - equipoMasCostosoPorPaisSede()
 *   a4 - cantidadJugadoresSub21PorEquipo()
 *
 * REPORTES PDF (b):
 *   b1 - generarReporteBitacora(desde, hasta)
 *   b2 - generarReporteJugadoresFiltrados(...)
 *   b3 - generarReporteValorPorConfederacion(idConf, nombre)
 *   b4 - generarReporteEquiposPorAnfitrion()
 */
public class MundialService {

    private final IJugadorDAO jugadorDAO = new JugadorDAOImpl();
    private final IPartidoDAO partidoDAO = new PartidoDAOImpl();

    // ===================== CONSULTAS =====================

    public List<Jugador> jugadorMasCostosoPorConfederacion() throws Exception {
        return jugadorDAO.jugadorMasCostosoPorConfederacion();
    }

    public List<Partido> partidosPorEstadio(int idEstadio) throws Exception {
        return partidoDAO.listarPorEstadio(idEstadio);
    }

    public List<Object[]> equipoMasCostosoPorPaisSede() throws Exception {
        return partidoDAO.equipoMasCostosoPorPaisSede();
    }

    public List<Object[]> cantidadJugadoresSub21PorEquipo() throws Exception {
        return jugadorDAO.cantidadSub21PorEquipo();
    }

    // ===================== REPORTES PDF =====================

     /**
     * b1: usuarios con ingreso/salida en rango de fecha-hora.
     * @return ruta del PDF generado
     */
    public String generarReporteBitacora(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        return GeneradorReportes.reporteBitacoraPorRango(desde, hasta);
    }

    /**
     * b2: jugadores según peso, estatura y equipo.
     * @param idEquipo  0 = todos los equipos
     * @return ruta del PDF generado
     */
    public String generarReporteJugadoresFiltrados(
            BigDecimal pesoMin, BigDecimal pesoMax,
            BigDecimal estaturaMin, BigDecimal estaturaMax,
            int idEquipo) throws Exception {
        return GeneradorReportes.reporteJugadoresPorFiltros(
                pesoMin, pesoMax, estaturaMin, estaturaMax, idEquipo);
    }

    /**
     * b3: valor total de jugadores por equipo, filtrado por confederación.
     * @return ruta del PDF generado
     */
    public String generarReporteValorPorConfederacion(
            int idConfederacion, String nombreConfederacion) throws Exception {
        return GeneradorReportes.reporteValorTotalPorEquipoYConfederacion(
                idConfederacion, nombreConfederacion);
    }

    /**
     * b4: equipos que jugarán en cada país anfitrión.
     * @return ruta del PDF generado
     */
    public String generarReporteEquiposPorAnfitrion() throws Exception {
        return GeneradorReportes.reporteEquiposPorPaisAnfitrion();
    }
}
