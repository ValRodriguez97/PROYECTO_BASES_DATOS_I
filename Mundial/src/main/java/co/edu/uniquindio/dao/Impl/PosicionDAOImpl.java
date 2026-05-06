package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IPosicionDAO;
import co.edu.uniquindio.model.Posicion;

import java.sql.*;
import java.util.*;

public class PosicionDAOImpl implements IPosicionDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    @Override
    public void insertar(Posicion p) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "INSERT INTO Posicion (nombre) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) p.setIdPosicion(keys.getInt(1));
        }
    }

    @Override
    public Optional<Posicion> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM Posicion WHERE idPosicion=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Posicion(rs.getInt("idPosicion"), rs.getString("nombre")));
            }
            return Optional.empty();
        }
    }

    @Override
    public List<Posicion> listarTodos() throws Exception {
        List<Posicion> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Posicion ORDER BY nombre")) {
            while (rs.next())
                lista.add(new Posicion(rs.getInt("idPosicion"), rs.getString("nombre")));
        }
        return lista;
    }

    @Override
    public void actualizar(Posicion p) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "UPDATE Posicion SET nombre=? WHERE idPosicion=?")) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getIdPosicion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM Posicion WHERE idPosicion=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}