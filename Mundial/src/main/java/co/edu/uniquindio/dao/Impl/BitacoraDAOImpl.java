package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.*;
import co.edu.uniquindio.dao.IBitacoraDAO;
import co.edu.uniquindio.model.Bitacora;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAOImpl implements IBitacoraDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    @Override
    public int registrarIngreso(int idUsuario, LocalDateTime ingreso) throws Exception {
        String sql = "INSERT INTO Bitacora (idUsuario, fechaHoraIngreso) VALUES (?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUsuario);
            ps.setTimestamp(2, Timestamp.valueOf(ingreso));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public void registrarSalida(int idBitacora, LocalDateTime salida) throws Exception {
        String sql = "UPDATE Bitacora SET fechaHoraSalida=? WHERE idBitacora=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(salida));
            ps.setInt(2, idBitacora);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Bitacora> listarPorRango(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        List<Bitacora> lista = new ArrayList<>();
        String sql = "SELECT * FROM Bitacora WHERE fechaHoraIngreso BETWEEN ? AND ? " +
                "OR (fechaHoraSalida BETWEEN ? AND ?) ORDER BY fechaHoraIngreso";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            Timestamp tDesde = Timestamp.valueOf(desde);
            Timestamp tHasta = Timestamp.valueOf(hasta);
            ps.setTimestamp(1, tDesde);
            ps.setTimestamp(2, tHasta);
            ps.setTimestamp(3, tDesde);
            ps.setTimestamp(4, tHasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bitacora b = new Bitacora();
                    b.setIdBitacora(rs.getInt("idBitacora"));
                    b.setIdUsuario(rs.getInt("idUsuario"));
                    b.setFechaHoraIngreso(rs.getTimestamp("fechaHoraIngreso").toLocalDateTime());
                    Timestamp salida = rs.getTimestamp("fechaHoraSalida");
                    if (salida != null) b.setFechaHoraSalida(salida.toLocalDateTime());
                    lista.add(b);
                }
            }
        }
        return lista;
    }

    @Override
    public List<Bitacora> listarPorUsuario(int idUsuario) throws Exception {
        List<Bitacora> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM Bitacora WHERE idUsuario=? ORDER BY fechaHoraIngreso DESC")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bitacora b = new Bitacora();
                    b.setIdBitacora(rs.getInt("idBitacora"));
                    b.setIdUsuario(rs.getInt("idUsuario"));
                    b.setFechaHoraIngreso(rs.getTimestamp("fechaHoraIngreso").toLocalDateTime());
                    Timestamp salida = rs.getTimestamp("fechaHoraSalida");
                    if (salida != null) b.setFechaHoraSalida(salida.toLocalDateTime());
                    lista.add(b);
                }
            }
        }
        return lista;
    }

}
