package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IGrupoDAO;
import co.edu.uniquindio.model.Grupo;

import java.sql.*;
import java.util.*;

public class GrupoDAOImpl implements IGrupoDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    @Override
    public Optional<Grupo> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM Grupo WHERE idGrupo=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Grupo(rs.getInt("idGrupo"),
                        rs.getString("letra").charAt(0)));
            }
            return Optional.empty();
        }
    }

    @Override
    public List<Grupo> listarTodos() throws Exception {
        List<Grupo> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Grupo ORDER BY letra")) {
            while (rs.next())
                lista.add(new Grupo(rs.getInt("idGrupo"), rs.getString("letra").charAt(0)));
        }
        return lista;
    }

    /**
     * Reporte b3: valor total de jugadores por equipo, filtrado por confederación.
     * Retorna Object[]{ String equipoNombre, String confNombre, BigDecimal valorTotal }
     */
    @Override
    public List<Object[]> valorTotalJugadoresPorEquipoYConfederacion(int idConfederacion) throws Exception {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT e.nombre AS equipo, c.nombre AS confederacion, " +
            "       SUM(j.valorMercado) AS valorTotal " +
            "FROM Equipo e " +
            "JOIN Confederacion c ON e.idConfederacion = c.idConfederacion " +
            "JOIN Jugador j ON j.idEquipo = e.idEquipo " +
            "WHERE e.idConfederacion = ? " +
            "GROUP BY e.idEquipo, e.nombre, c.nombre " +
            "ORDER BY valorTotal DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idConfederacion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("equipo"),
                    rs.getString("confederacion"),
                    rs.getBigDecimal("valorTotal")
                });
            }
        }
        return lista;
    }

    /**
     * Reporte b4: equipos (con su país de origen) que jugarán en cada país anfitrión.
     * Retorna Object[]{ String paisSede, String equipoNombre, String paisEquipo }
     */
    @Override
    public List<Object[]> equiposPorPaisAnfitrion() throws Exception {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT DISTINCT ps.nombre AS paisSede, e.nombre AS equipo, pe.nombre AS paisEquipo " +
            "FROM Pais ps " +
            "JOIN Ciudad ci ON ci.idPais = ps.idPais " +
            "JOIN Estadio est ON est.idCiudad = ci.idCiudad " +
            "JOIN Partido pa ON pa.idEstadio = est.idEstadio " +
            "JOIN DetallePartido dp ON dp.idPartido = pa.idPartido " +
            "JOIN Equipo e ON e.idEquipo = dp.idEquipo " +
            "JOIN Pais pe ON pe.idPais = e.idPais " +
            "WHERE ps.esAnfitrion = TRUE " +
            "ORDER BY ps.nombre, e.nombre";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("paisSede"),
                    rs.getString("equipo"),
                    rs.getString("paisEquipo")
                });
            }
        }
        return lista;
    }
}