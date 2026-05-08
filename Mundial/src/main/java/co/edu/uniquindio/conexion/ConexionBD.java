package co.edu.uniquindio.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String HOST     = System.getenv().getOrDefault("DB_HOST",     "localhost");
    private static final String PORT     = System.getenv().getOrDefault("DB_PORT",     "3306");
    private static final String DATABASE = System.getenv().getOrDefault("DB_NAME",     "mundial2026");
    private static final String USER     = System.getenv().getOrDefault("DB_USER",     "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", ""); //bajo cambio

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false"
            + "&serverTimezone=America/Bogota"
            + "&allowPublicKeyRetrieval=true"
            + "&characterEncoding=UTF-8";

    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            this.conexion.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    public static synchronized ConexionBD getInstance() throws SQLException {
        if (instancia == null || instancia.conexion.isClosed()) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        } finally {
            instancia = null;
        }
    }

    public void verificarVersionMySQL() throws SQLException {
        try (Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery("SELECT VERSION()")){
            if (rs.next()) {
                String version = rs.getString(1);
                int mayorVersion = Integer.parseInt(version.split("\\.")[0]);
                if (mayorVersion < 8) {
                    throw new SQLException("Se requiere MySQL 8 o superior. Versión detectada: " + version);
                }
            }
            
        }
    }
}
