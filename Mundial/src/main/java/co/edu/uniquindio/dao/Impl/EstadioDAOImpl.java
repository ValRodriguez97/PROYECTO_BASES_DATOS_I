package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IEstadioDAO;
import co.edu.uniquindio.model.*;

import java.sql.*;
import java.util.*;

public class EstadioDAOImpl implements IEstadioDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private static final String SELECT_BASE =
        "SELECT est.idEstadio, est.nombre AS nombreEstadio, est.capacidad, " +
        "       ci.idCiudad, ci.nombre AS nombreCiudad, " +
        "       p.idPais, p.nombre AS nombrePais, p.continente " +
        "FROM Estadio est " +
        "JOIN Ciudad ci ON est.idCiudad = ci.idCiudad " +
        "JOIN Pais p ON ci.idPais = p.idPais ";

    private Estadio mapear(ResultSet rs) throws SQLException {
        Pais pais = new Pais();
        pais.setIdPais(rs.getInt("idPais"));
        pais.setNombre(rs.getString("nombrePais"));
        pais.setContinente(rs.getString("continente"));

        Ciudad ciudad = new Ciudad();
        ciudad.setIdCiudad(rs.getInt("idCiudad"));
        ciudad.setNombre(rs.getString("nombreCiudad"));
        ciudad.setPais(pais);

        Estadio e = new Estadio();
        e.setIdEstadio(rs.getInt("idEstadio"));
        e.setNombre(rs.getString("nombreEstadio"));
        e.setCapacidad(rs.getInt("capacidad"));
        e.setCiudad(ciudad);
        return e;
    }

    @Override
    public void insertar(Estadio e) throws Exception {
        String sql = "INSERT INTO Estadio (nombre, capacidad, idCiudad) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getCapacidad());
            ps.setInt(3, e.getCiudad().getIdCiudad());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) e.setIdEstadio(keys.getInt(1));
        }
    }

    @Override
    public void actualizar(Estadio e) throws Exception {
        String sql = "UPDATE Estadio SET nombre=?, capacidad=?, idCiudad=? WHERE idEstadio=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getCapacidad());
            ps.setInt(3, e.getCiudad().getIdCiudad());
            ps.setInt(4, e.getIdEstadio());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM Estadio WHERE idEstadio=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Estadio> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                SELECT_BASE + "WHERE est.idEstadio=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
        }
    }

    @Override
    public List<Estadio> listarTodos() throws Exception {
        List<Estadio> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY est.nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
}
