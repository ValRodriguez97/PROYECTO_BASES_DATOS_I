package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.*;
import co.edu.uniquindio.dao.IJugadorDAO;
import co.edu.uniquindio.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JugadorDAOImpl implements IJugadorDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private Jugador mapearSimple(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();
        j.setIdJugador(rs.getInt("idJugador"));
        j.setNombre(rs.getString("nombre"));
        j.setApellido(rs.getString("apellido"));
        j.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        j.setEstatura(rs.getBigDecimal("estatura"));
        j.setPeso(rs.getBigDecimal("peso"));
        j.setValorMercado(rs.getBigDecimal("valorMercado"));

        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("idEquipo"));
        e.setNombre(rs.getString("nombreEquipo"));
        j.setEquipo(e);

        Posicion p = new Posicion();
        p.setIdPosicion(rs.getInt("idPosicion"));
        p.setNombre(rs.getString("nombrePosicion"));
        j.setPosicion(p);

        return j;
    }

    private static final String SELECT_BASE =
            "SELECT j.*, e.nombre AS nombreEquipo, pos.nombre AS nombrePosicion " +
                    "FROM Jugador j " +
                    "JOIN Equipo e ON j.idEquipo = e.idEquipo " +
                    "JOIN Posicion pos ON j.idPosicion = pos.idPosicion ";

    @Override
    public void insertar(Jugador j) throws Exception {
        String sql = "INSERT INTO Jugador (nombre,apellido,fechaNacimiento,estatura,peso," +
                "valorMercado,idEquipo,idPosicion) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getApellido());
            ps.setDate(3, Date.valueOf(j.getFechaNacimiento()));
            ps.setBigDecimal(4, j.getEstatura());
            ps.setBigDecimal(5, j.getPeso());
            ps.setBigDecimal(6, j.getValorMercado());
            ps.setInt(7, j.getEquipo().getIdEquipo());
            ps.setInt(8, j.getPosicion().getIdPosicion());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) j.setIdJugador(keys.getInt(1));
        }
    }

    @Override
    public void actualizar(Jugador j) throws Exception {
        String sql = "UPDATE Jugador SET nombre=?,apellido=?,fechaNacimiento=?," +
                "estatura=?,peso=?,valorMercado=?,idEquipo=?,idPosicion=? WHERE idJugador=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getApellido());
            ps.setDate(3, Date.valueOf(j.getFechaNacimiento()));
            ps.setBigDecimal(4, j.getEstatura());
            ps.setBigDecimal(5, j.getPeso());
            ps.setBigDecimal(6, j.getValorMercado());
            ps.setInt(7, j.getEquipo().getIdEquipo());
            ps.setInt(8, j.getPosicion().getIdPosicion());
            ps.setInt(9, j.getIdJugador());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM Jugador WHERE idJugador=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Jugador> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(SELECT_BASE + "WHERE j.idJugador=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapearSimple(rs)) : Optional.empty();
        }
    }

    @Override
    public List<Jugador> listarTodos() throws Exception {
        List<Jugador> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs  = st.executeQuery(SELECT_BASE + "ORDER BY j.apellido, j.nombre")) {
            while (rs.next()) lista.add(mapearSimple(rs));
        }
        return lista;
    }

    @Override
    public List<Jugador> listarPorEquipo(int idEquipo) throws Exception {
        List<Jugador> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SELECT_BASE + "WHERE j.idEquipo=?")) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearSimple(rs));
        }
        return lista;
    }

    @Override
    public List<Jugador> jugadorMasCostosoPorConfederacion() throws Exception {
        List<Jugador> lista = new ArrayList<>();
        String sql =
                "SELECT j.*, e.nombre AS nombreEquipo, pos.nombre AS nombrePosicion, " +
                        "       c.nombre AS nombreConfederacion " +
                        "FROM Jugador j " +
                        "JOIN Equipo e ON j.idEquipo = e.idEquipo " +
                        "JOIN Posicion pos ON j.idPosicion = pos.idPosicion " +
                        "JOIN Confederacion c ON e.idConfederacion = c.idConfederacion " +
                        "WHERE (e.idConfederacion, j.valorMercado) IN (" +
                        "   SELECT e2.idConfederacion, MAX(j2.valorMercado) " +
                        "   FROM Jugador j2 JOIN Equipo e2 ON j2.idEquipo = e2.idEquipo " +
                        "   GROUP BY e2.idConfederacion" +
                        ") ORDER BY c.nombre";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Jugador j = mapearSimple(rs);
                // Agregar info de confederación al equipo
                Confederacion c = new Confederacion();
                c.setNombre(rs.getString("nombreConfederacion"));
                j.getEquipo().setConfederacion(c);
                lista.add(j);
            }
        }
        return lista;
    }

    @Override
    public List<Object[]> cantidadSub21PorEquipo() throws Exception {
        List<Object[]> lista = new ArrayList<>();
        String sql =
                "SELECT e.nombre AS equipo, COUNT(j.idJugador) AS cantidad " +
                        "FROM Equipo e " +
                        "LEFT JOIN Jugador j ON j.idEquipo = e.idEquipo " +
                        "   AND TIMESTAMPDIFF(YEAR, j.fechaNacimiento, CURDATE()) < 21 " +
                        "GROUP BY e.idEquipo, e.nombre " +
                        "ORDER BY cantidad DESC, e.nombre";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{ rs.getString("equipo"), rs.getLong("cantidad") });
            }
        }
        return lista;
    }

    @Override
    public List<Jugador> filtrarPorPesoEstaturaEquipo(
            BigDecimal pesoMin, BigDecimal pesoMax,
            BigDecimal estaturaMin, BigDecimal estaturaMax,
            int idEquipo) throws Exception {
        List<Jugador> lista = new ArrayList<>();
        String sql = SELECT_BASE +
                "WHERE j.peso BETWEEN ? AND ? AND j.estatura BETWEEN ? AND ? " +
                (idEquipo > 0 ? "AND j.idEquipo=? " : "") +
                "ORDER BY e.nombre, j.apellido";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBigDecimal(1, pesoMin);
            ps.setBigDecimal(2, pesoMax);
            ps.setBigDecimal(3, estaturaMin);
            ps.setBigDecimal(4, estaturaMax);
            if (idEquipo > 0) ps.setInt(5, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearSimple(rs));
        }
        return lista;
    }


}
