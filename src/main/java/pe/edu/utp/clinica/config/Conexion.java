package pe.edu.utp.clinica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de conexion a MySQL (patron Singleton).
 * Todos los DAO piden su conexion aqui.
 */
public final class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/clinica_citas"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root1234";

    private static Conexion instancia;

    private Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontro el driver de MySQL", e);
        }
    }

    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
