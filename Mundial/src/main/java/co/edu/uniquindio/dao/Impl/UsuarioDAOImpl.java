package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IUsuarioDAO;
import co.edu.uniquindio.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements IUsuarioDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        int    id     = rs.getInt("idUsuario");
        String nombre = rs.getString("nombreUsuario");
        String pass   = rs.getString("contrasena");
        String tipo   = rs.getString("tipoUsuario");

        return switch (tipo) {
            case "ADMINISTRADOR" -> new Administrador(id, nombre, pass);
            case "TRADICIONAL"   -> new UsuarioTradicional(id, nombre, pass);
            default              -> new UsuarioEsporadico(id, nombre, pass);
        };
    }

    @Override
    public void insertar(Usuario u) throws Exception {
        String sql = "INSERT INTO Usuario (nombreUsuario, contrasena, tipoUsuario) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContraseña());
            ps.setString(3, u.getTipoUsuario().name());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) u.setIdUsuario(keys.getInt(1));
        }
    }

    @Override
    public void actualizar(Usuario u) throws Exception {
        String sql = "UPDATE Usuario SET nombreUsuario=?, contrasena=?, tipoUsuario=? WHERE idUsuario=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContraseña());
            ps.setString(3, u.getTipoUsuario().name());
            ps.setInt(4, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM Usuario WHERE idUsuario=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM Usuario WHERE idUsuario=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> buscarPorNombre(String nombre) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM Usuario WHERE nombreUsuario=?")) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
        }
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs  = st.executeQuery("SELECT * FROM Usuario ORDER BY nombreUsuario")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
}
