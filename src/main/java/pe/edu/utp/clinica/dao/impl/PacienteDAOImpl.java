package pe.edu.utp.clinica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import pe.edu.utp.clinica.config.Conexion;
import pe.edu.utp.clinica.dao.PacienteDAO;
import pe.edu.utp.clinica.model.Paciente;
import pe.edu.utp.clinica.model.dto.RegistroPacienteRequest;

@Repository
public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public Paciente login(String dni, String password) {
        String sql = "SELECT dni, nombres, apellidos, telefono, correo, sexo, seguro "
                + "FROM paciente WHERE dni = ? AND password = SHA2(?, 256)";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en login de paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existe(String dni, String correo) {
        String sql = "SELECT 1 FROM paciente WHERE dni = ? OR correo = ?";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar paciente: " + e.getMessage(), e);
        }
    }

    /** Registra al paciente y le crea su historial clinico (transaccion). */
    @Override
    public Paciente registrar(RegistroPacienteRequest d) {
        String sqlPaciente = "INSERT INTO paciente (dni, nombres, apellidos, telefono, correo, password, sexo, seguro) "
                + "VALUES (?, ?, ?, ?, ?, SHA2(?, 256), ?, ?)";
        String sqlHistorial = "INSERT INTO historial_clinico (dni_paciente, fecha_creacion) VALUES (?, CURDATE())";

        try (Connection cn = Conexion.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps1 = cn.prepareStatement(sqlPaciente);
                 PreparedStatement ps2 = cn.prepareStatement(sqlHistorial)) {
                ps1.setString(1, d.dni());
                ps1.setString(2, d.nombres());
                ps1.setString(3, d.apellidos());
                ps1.setString(4, d.telefono());
                ps1.setString(5, d.correo());
                ps1.setString(6, d.password());
                ps1.setString(7, d.sexo());
                ps1.setString(8, d.seguro());
                ps1.executeUpdate();

                ps2.setString(1, d.dni());
                ps2.executeUpdate();

                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar paciente: " + e.getMessage(), e);
        }
        return new Paciente(d.dni(), d.nombres(), d.apellidos(), d.telefono(), d.correo(), d.sexo(), d.seguro());
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        return new Paciente(
                rs.getString("dni"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("sexo"),
                rs.getString("seguro"));
    }
}
