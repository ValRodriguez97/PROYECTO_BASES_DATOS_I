package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IConfederacionDAO;
import co.edu.uniquindio.model.Confederacion;

import java.sql.*;
import java.util.*;

public class ConfederacionDAOImpl implements IConfederacionDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private Confederacion mapear(ResultSet rs) throws SQLException {
        return new Confederacion(
            rs.getInt("idConfederacion"),
            rs.getString("nombre"),
            rs.getString("sigla")
        );
    }

    @Override
    public void insertar(Confederacion c) throws Exception {
        String sql = "INSERT INTO Confederacion (nombre, sigla) VALUES (?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getSigla());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setIdConfederacion(keys.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Confederacion c) throws Exception {
        String sql = "UPDATE Confederacion SET nombre=?, sigla=? WHERE idConfederacion=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getSigla());
            ps.setInt(3, c.getIdConfederacion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM Confederacion WHERE idConfederacion=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Confederacion> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM Confederacion WHERE idConfederacion=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Confederacion> listarTodos() throws Exception {
        List<Confederacion> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Confederacion ORDER BY nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
}