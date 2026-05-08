package co.edu.uniquindio.dao.Impl;

import co.edu.uniquindio.conexion.ConexionBD;
import co.edu.uniquindio.dao.IPartidoDAO;
import co.edu.uniquindio.model.*;
import co.edu.uniquindio.model.Enum.Condicion;

import java.sql.*;
import java.util.*;

public class PartidoDAOImpl implements IPartidoDAO {

    private Connection getConn() throws Exception {
        return ConexionBD.getInstance().getConexion();
    }

    private Partido mapear(ResultSet rs) throws SQLException {
        Partido p = new Partido();
        p.setIdPartido(rs.getInt("idPartido"));
        p.setHoraFecha(rs.getTimestamp("horaFecha").toLocalDateTime());

        Grupo g = new Grupo();
        g.setIdGrupo(rs.getInt("idGrupo"));
        g.setLetra(rs.getString("letraGrupo").charAt(0));
        p.setGrupo(g);

        Estadio est = new Estadio();
        est.setIdEstadio(rs.getInt("idEstadio"));
        est.setNombre(rs.getString("nombreEstadio"));
        p.setEstadio(est);

        return p;
    }

    private static final String SELECT_BASE =
            "SELECT pa.*, g.letra AS letraGrupo, est.nombre AS nombreEstadio " +
                    "FROM Partido pa " +
                    "JOIN Grupo g ON pa.idGrupo = g.idGrupo " +
                    "JOIN Estadio est ON pa.idEstadio = est.idEstadio ";

    @Override
    public void insertar(Partido p) throws Exception {
        String sql = "INSERT INTO Partido (horaFecha, idGrupo, idEstadio) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(p.getHoraFecha()));
            ps.setInt(2, p.getGrupo().getIdGrupo());
            ps.setInt(3, p.getEstadio().getIdEstadio());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setIdPartido(keys.getInt(1));
            }
        }
    }

    /**
     * Crea el Partido y sus dos DetallePartido (LOCAL y VISITANTE) en una sola
     * transacción. Si cualquier paso falla se hace rollback completo para evitar
     * partidos huérfanos sin equipos.
     */
    @Override
    public void insertarConDetalles(Partido p) throws Exception {
        if (p.getDetalles() == null || p.getDetalles().size() != 2) {
            throw new Exception("Un Partido debe llevar exactamente 2 DetallePartido (LOCAL y VISITANTE).");
        }
        Connection conn = getConn();
        boolean autoCommitOriginal = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            String sqlPartido = "INSERT INTO Partido (horaFecha, idGrupo, idEstadio) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPartido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, Timestamp.valueOf(p.getHoraFecha()));
                ps.setInt(2, p.getGrupo().getIdGrupo());
                ps.setInt(3, p.getEstadio().getIdEstadio());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) p.setIdPartido(keys.getInt(1));
                }
            }

            String sqlDetalle = "INSERT INTO DetallePartido (idPartido, idEquipo, condicion, golesAnotados) " +
                                "VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
                for (DetallePartido d : p.getDetalles()) {
                    d.setIdPartido(p.getIdPartido());
                    ps.setInt(1, d.getIdPartido());
                    ps.setInt(2, d.getEquipo().getIdEquipo());
                    ps.setString(3, d.getCondicion().name());
                    ps.setInt(4, d.getGolesAnotados());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw ex;
        } finally {
            try { conn.setAutoCommit(autoCommitOriginal); } catch (Exception ignored) {}
        }
    }

    @Override
    public void actualizar(Partido p) throws Exception {
        String sql = "UPDATE Partido SET horaFecha=?, idGrupo=?, idEstadio=? WHERE idPartido=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(p.getHoraFecha()));
            ps.setInt(2, p.getGrupo().getIdGrupo());
            ps.setInt(3, p.getEstadio().getIdEstadio());
            ps.setInt(4, p.getIdPartido());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM Partido WHERE idPartido=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Partido> buscarPorId(int id) throws Exception {
        try (PreparedStatement ps = getConn().prepareStatement(SELECT_BASE + "WHERE pa.idPartido=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Partido> listarTodos() throws Exception {
        List<Partido> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY pa.horaFecha")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public List<Partido> listarPorEstadio(int idEstadio) throws Exception {
        List<Partido> lista = new ArrayList<>();
        // SELECT extendido con alias explícitos sin puntos — JDBC no admite "tabla.columna" en getString()
        String sql =
                "SELECT pa.idPartido, pa.horaFecha, pa.idGrupo, pa.idEstadio, " +
                        "       g.letra AS letraGrupo, est.nombre AS nombreEstadio, " +
                        "       e1.idEquipo AS idEquipoLocal,    e1.nombre AS nombreLocal,    dp1.golesAnotados AS golesLocal, " +
                        "       e2.idEquipo AS idEquipoVisitante, e2.nombre AS nombreVisitante, dp2.golesAnotados AS golesVisitante " +
                        "FROM Partido pa " +
                        "JOIN Grupo g   ON pa.idGrupo   = g.idGrupo " +
                        "JOIN Estadio est ON pa.idEstadio = est.idEstadio " +
                        "LEFT JOIN DetallePartido dp1 ON dp1.idPartido = pa.idPartido AND dp1.condicion = 'LOCAL' " +
                        "LEFT JOIN DetallePartido dp2 ON dp2.idPartido = pa.idPartido AND dp2.condicion = 'VISITANTE' " +
                        "LEFT JOIN Equipo e1 ON e1.idEquipo = dp1.idEquipo " +
                        "LEFT JOIN Equipo e2 ON e2.idEquipo = dp2.idEquipo " +
                        "WHERE pa.idEstadio = ? " +
                        "ORDER BY pa.horaFecha";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEstadio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido p = mapear(rs);
                    agregarDetallesDesdeRs(p, rs);
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    /**
     * Construye y agrega los {@link DetallePartido} al partido usando los alias
     * explícitos del SELECT: idEquipoLocal, nombreLocal, golesLocal,
     * idEquipoVisitante, nombreVisitante, golesVisitante.
     */
    private void agregarDetallesDesdeRs(Partido p, ResultSet rs) throws SQLException {
        String nombreLocal = rs.getString("nombreLocal");
        if (nombreLocal != null) {
            Equipo local = new Equipo();
            local.setIdEquipo(rs.getInt("idEquipoLocal"));
            local.setNombre(nombreLocal);
            DetallePartido d1 = new DetallePartido(
                    p.getIdPartido(), local,
                    Condicion.LOCAL,
                    rs.getInt("golesLocal"));
            p.agregarDetalle(d1);
        }
        String nombreVisitante = rs.getString("nombreVisitante");
        if (nombreVisitante != null) {
            Equipo visitante = new Equipo();
            visitante.setIdEquipo(rs.getInt("idEquipoVisitante"));
            visitante.setNombre(nombreVisitante);
            DetallePartido d2 = new DetallePartido(
                    p.getIdPartido(), visitante,
                    Condicion.VISITANTE,
                    rs.getInt("golesVisitante"));
            p.agregarDetalle(d2);
        }
    }

    @Override
    public List<Partido> listarPorGrupo(int idGrupo) throws Exception {
        List<Partido> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SELECT_BASE + "WHERE pa.idGrupo=?")) {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ---- Consulta a3: Equipo más costoso que juega en cada país anfitrión ----
    // Filtra por Pais.esAnfitrion = TRUE para no depender de la grafía del nombre.
    // Suma el valor de mercado por equipo UNA sola vez (en una subconsulta) para
    // evitar el doble conteo que se produce al hacer JOIN con Partido.
    // Retorna Object[]{ String paisSede, String nombreEquipo, BigDecimal valorTotal }
    @Override
    public List<Object[]> equipoMasCostosoPorPaisSede() throws Exception {
        List<Object[]> lista = new ArrayList<>();
        String sql =
                "SELECT pais_sede, equipo, valor_total FROM (" +
                "  SELECT ps.nombre AS pais_sede, e.nombre AS equipo, ev.valor_total, " +
                "         ROW_NUMBER() OVER (PARTITION BY ps.idPais ORDER BY ev.valor_total DESC, e.nombre) AS rn " +
                "  FROM Pais ps " +
                "  JOIN Ciudad ci   ON ci.idPais   = ps.idPais " +
                "  JOIN Estadio est ON est.idCiudad = ci.idCiudad " +
                "  JOIN Partido pa  ON pa.idEstadio = est.idEstadio " +
                "  JOIN EquipoGrupo eg ON eg.idGrupo = pa.idGrupo " +
                "  JOIN Equipo e    ON e.idEquipo = eg.idEquipo " +
                "  JOIN ( " +
                "      SELECT idEquipo, SUM(valorMercado) AS valor_total " +
                "      FROM Jugador " +
                "      GROUP BY idEquipo " +
                "  ) ev ON ev.idEquipo = e.idEquipo " +
                "  WHERE ps.esAnfitrion = TRUE " +
                "  GROUP BY ps.idPais, ps.nombre, e.idEquipo, e.nombre, ev.valor_total " +
                ") ranked " +
                "WHERE rn = 1 " +
                "ORDER BY pais_sede";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getString("pais_sede"),
                        rs.getString("equipo"),
                        rs.getBigDecimal("valor_total")
                });
            }
        }
        return lista;
    }
}