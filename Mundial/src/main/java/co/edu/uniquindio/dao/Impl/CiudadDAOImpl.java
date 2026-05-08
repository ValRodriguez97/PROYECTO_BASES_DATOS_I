package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.ICiudadDAO;
import co.edu.uniquindio.model.Ciudad;
import co.edu.uniquindio.model.Pais;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CiudadDAOImpl implements ICiudadDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private static final String SELECT_BASE =
            "SELECT ci.idCiudad, ci.nombre AS nombreCiudad, " +
            "       p.idPais, p.nombre AS nombrePais, p.continente, p.esAnfitrion " +
            "FROM Ciudad ci " +
            "JOIN Pais p ON ci.idPais = p.idPais ";

    private Ciudad mapear(ResultSet rs) throws SQLException {
        Pais p = new Pais();
        p.setIdPais(rs.getInt("idPais"));
        p.setNombre(rs.getString("nombrePais"));
        p.setContinente(rs.getString("continente"));
        p.setEsAnfitrion(rs.getBoolean("esAnfitrion"));

        Ciudad c = new Ciudad();
        c.setIdCiudad(rs.getInt("idCiudad"));
        c.setNombre(rs.getString("nombreCiudad"));
        c.setPais(p);
        return c;
    }

    @Override
    public void insertar(Ciudad c) throws Exception {
        String sql = "INSERT INTO Ciudad (nombre, idPais) VALUES (?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getPais().getIdPais());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setIdCiudad(keys.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Ciudad c) throws Exception {
        String sql = "UPDATE Ciudad SET nombre=?, idPais=? WHERE idCiudad=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getPais().getIdPais());
            ps.setInt(3, c.getIdCiudad());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM Ciudad WHERE idCiudad=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Ciudad> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                SELECT_BASE + "WHERE ci.idCiudad=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Ciudad> listarTodos() throws Exception {
        List<Ciudad> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY p.nombre, ci.nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public List<Ciudad> listarPorPais(int idPais) throws Exception {
        List<Ciudad> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(
                SELECT_BASE + "WHERE p.idPais=? ORDER BY ci.nombre")) {
            ps.setInt(1, idPais);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
}
