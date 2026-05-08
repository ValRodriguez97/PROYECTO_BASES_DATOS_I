package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IEquipoDAO;
import co.edu.uniquindio.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipoDAOImpl implements IEquipoDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private Equipo mapear(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("idEquipo"));
        e.setNombre(rs.getString("nombreEquipo"));
        e.setRankingFifa(rs.getInt("rankingFifa"));

        Pais p = new Pais();
        p.setIdPais(rs.getInt("idPais"));
        p.setNombre(rs.getString("nombrePais"));
        e.setPais(p);

        Confederacion c = new Confederacion();
        c.setIdConfederacion(rs.getInt("idConfederacion"));
        c.setNombre(rs.getString("nombreConf"));
        c.setSigla(rs.getString("siglaConf"));
        e.setConfederacion(c);

        DirectorTecnico dt = new DirectorTecnico();
        dt.setIdDT(rs.getInt("idDt"));
        dt.setNombre(rs.getString("nombreDt"));
        dt.setApellido(rs.getString("apellidoDt"));
        e.setDirectorTecnico(dt);

        return e;
    }

    private static final String SELECT_BASE =
            "SELECT e.idEquipo, e.nombre AS nombreEquipo, e.rankingFifa, " +
                    "p.idPais, p.nombre AS nombrePais, " +
                    "c.idConfederacion, c.nombre AS nombreConf, c.sigla AS siglaConf, " +
                    "dt.idDt, dt.nombre AS nombreDt, dt.apellido AS apellidoDt " +
                    "FROM Equipo e " +
                    "JOIN Pais p ON e.idPais=p.idPais " +
                    "JOIN Confederacion c ON e.idConfederacion=c.idConfederacion " +
                    "JOIN DirectorTecnico dt ON e.idDt=dt.idDt ";

    @Override
    public void insertar(Equipo e) throws Exception {
        String sql = "INSERT INTO Equipo (nombre,rankingFifa,idPais,idConfederacion,idDt) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getRankingFifa());
            ps.setInt(3, e.getPais().getIdPais());
            ps.setInt(4, e.getConfederacion().getIdConfederacion());
            ps.setInt(5, e.getDirectorTecnico().getIdDT());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setIdEquipo(keys.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Equipo e) throws Exception {
        String sql = "UPDATE Equipo SET nombre=?,rankingFifa=?,idPais=?,idConfederacion=?,idDt=? WHERE idEquipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getRankingFifa());
            ps.setInt(3, e.getPais().getIdPais());
            ps.setInt(4, e.getConfederacion().getIdConfederacion());
            ps.setInt(5, e.getDirectorTecnico().getIdDT());
            ps.setInt(6, e.getIdEquipo());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM Equipo WHERE idEquipo=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Equipo> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(SELECT_BASE + "WHERE e.idEquipo=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Equipo> listarTodos() throws Exception {
        List<Equipo> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY e.nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public List<Equipo> listarPorConfederacion(int idConf) throws Exception {
        List<Equipo> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(
                SELECT_BASE + "WHERE e.idConfederacion=? ORDER BY e.rankingFifa")) {
            ps.setInt(1, idConf);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Equipo> listarPorGrupo(int idGrupo) throws Exception {
        List<Equipo> lista = new ArrayList<>();
        String sql = SELECT_BASE +
                "JOIN EquipoGrupo eg ON e.idEquipo=eg.idEquipo WHERE eg.idGrupo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
}
