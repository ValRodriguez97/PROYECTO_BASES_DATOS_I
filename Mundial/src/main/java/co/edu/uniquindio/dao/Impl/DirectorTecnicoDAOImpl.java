package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IDirectorTecnicoDAO;
import co.edu.uniquindio.model.DirectorTecnico;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DirectorTecnicoDAOImpl implements IDirectorTecnicoDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private DirectorTecnico mapear(ResultSet rs) throws SQLException {
        DirectorTecnico dt = new DirectorTecnico();
        dt.setIdDT(rs.getInt("idDt"));
        dt.setNombre(rs.getString("nombre"));
        dt.setApellido(rs.getString("apellido"));
        dt.setNacionalidad(rs.getString("nacionalidad"));
        dt.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        return dt;
    }

    @Override
    public void insertar(DirectorTecnico dt) throws Exception {
        String sql = "INSERT INTO DirectorTecnico (nombre, apellido, nacionalidad, fechaNacimiento) " +
                     "VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dt.getNombre());
            ps.setString(2, dt.getApellido());
            ps.setString(3, dt.getNacionalidad());
            ps.setDate(4, Date.valueOf(dt.getFechaNacimiento()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) dt.setIdDT(keys.getInt(1));
        }
    }

    @Override
    public void actualizar(DirectorTecnico dt) throws Exception {
        String sql = "UPDATE DirectorTecnico SET nombre=?, apellido=?, nacionalidad=?, " +
                     "fechaNacimiento=? WHERE idDt=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, dt.getNombre());
            ps.setString(2, dt.getApellido());
            ps.setString(3, dt.getNacionalidad());
            ps.setDate(4, Date.valueOf(dt.getFechaNacimiento()));
            ps.setInt(5, dt.getIdDT());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM DirectorTecnico WHERE idDt=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<DirectorTecnico> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM DirectorTecnico WHERE idDt=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
        }
    }

    @Override
    public List<DirectorTecnico> listarTodos() throws Exception {
        List<DirectorTecnico> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM DirectorTecnico ORDER BY apellido, nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
}