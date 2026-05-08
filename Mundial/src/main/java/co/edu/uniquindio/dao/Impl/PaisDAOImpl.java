package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IPaisDAO;
import co.edu.uniquindio.model.Pais;

import java.sql.*;
import java.util.*;

public class PaisDAOImpl implements IPaisDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private Pais mapear(ResultSet rs) throws SQLException {
        Pais p = new Pais();
        p.setIdPais(rs.getInt("idPais"));
        p.setNombre(rs.getString("nombre"));
        p.setContinente(rs.getString("continente"));
        p.setEsAnfitrion(rs.getBoolean("esAnfitrion"));
        return p;
    }

    @Override
    public void insertar(Pais p) throws Exception {
        String sql = "INSERT INTO Pais (nombre, continente, esAnfitrion) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getContinente());
            ps.setBoolean(3, p.isEsAnfitrion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setIdPais(keys.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Pais p) throws Exception {
        String sql = "UPDATE Pais SET nombre=?, continente=?, esAnfitrion=? WHERE idPais=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getContinente());
            ps.setBoolean(3, p.isEsAnfitrion());
            ps.setInt(4, p.getIdPais());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM Pais WHERE idPais=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Pais> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM Pais WHERE idPais=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Pais> listarTodos() throws Exception {
        List<Pais> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Pais ORDER BY nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public List<Pais> listarSedes() throws Exception {
        List<Pais> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Pais WHERE esAnfitrion = TRUE ORDER BY nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
}
