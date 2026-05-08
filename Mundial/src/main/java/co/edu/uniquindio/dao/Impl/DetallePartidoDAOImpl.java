package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IDetallePartidoDAO;
import co.edu.uniquindio.model.DetallePartido;
import co.edu.uniquindio.model.Equipo;
import co.edu.uniquindio.model.Enum.Condicion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetallePartidoDAOImpl implements IDetallePartidoDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private static final String SELECT_BASE =
            "SELECT dp.idPartido, dp.idEquipo, dp.condicion, dp.golesAnotados, " +
            "       e.nombre AS nombreEquipo " +
            "FROM DetallePartido dp " +
            "JOIN Equipo e ON e.idEquipo = dp.idEquipo ";

    private DetallePartido mapear(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("idEquipo"));
        e.setNombre(rs.getString("nombreEquipo"));

        return new DetallePartido(
                rs.getInt("idPartido"),
                e,
                Condicion.valueOf(rs.getString("condicion")),
                rs.getInt("golesAnotados"));
    }

    @Override
    public void insertar(DetallePartido d) throws Exception {
        String sql = "INSERT INTO DetallePartido (idPartido, idEquipo, condicion, golesAnotados) " +
                     "VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, d.getIdPartido());
            ps.setInt(2, d.getEquipo().getIdEquipo());
            ps.setString(3, d.getCondicion().name());
            ps.setInt(4, d.getGolesAnotados());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(DetallePartido d) throws Exception {
        String sql = "UPDATE DetallePartido SET condicion=?, golesAnotados=? " +
                     "WHERE idPartido=? AND idEquipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, d.getCondicion().name());
            ps.setInt(2, d.getGolesAnotados());
            ps.setInt(3, d.getIdPartido());
            ps.setInt(4, d.getEquipo().getIdEquipo());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idPartido, int idEquipo) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM DetallePartido WHERE idPartido=? AND idEquipo=?")) {
            ps.setInt(1, idPartido);
            ps.setInt(2, idEquipo);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<DetallePartido> buscarPorClave(int idPartido, int idEquipo) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                SELECT_BASE + "WHERE dp.idPartido=? AND dp.idEquipo=?")) {
            ps.setInt(1, idPartido);
            ps.setInt(2, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<DetallePartido> listarPorPartido(int idPartido) throws Exception {
        List<DetallePartido> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(
                SELECT_BASE + "WHERE dp.idPartido=? ORDER BY dp.condicion")) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
}
