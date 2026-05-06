package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IEquipoGrupoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipoGrupoDAOImpl implements IEquipoGrupoDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    @Override
    public void asignar(int idGrupo, int idEquipo) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "INSERT INTO EquipoGrupo (idGrupo, idEquipo) VALUES (?,?)")) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idEquipo);
            ps.executeUpdate();
        }
    }

    @Override
    public void remover(int idGrupo, int idEquipo) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM EquipoGrupo WHERE idGrupo=? AND idEquipo=?")) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idEquipo);
            ps.executeUpdate();
        }
    }

    @Override
    public int contarEquiposEnGrupo(int idGrupo) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT COUNT(*) FROM EquipoGrupo WHERE idGrupo=?")) {
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public boolean equipoTieneGrupo(int idEquipo) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT 1 FROM EquipoGrupo WHERE idEquipo=? LIMIT 1")) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
}
