package co.edu.uniquindio.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "mundial2026";
    private static final String USER = "root";
    private static final String PASSWORD = ""; //Bajo cambio

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";

    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    public static ConexionBD getInstance() throws SQLException {
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
}
