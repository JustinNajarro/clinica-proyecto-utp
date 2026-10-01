package pe.edu.utp.clinica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import pe.edu.utp.clinica.config.Conexion;
import pe.edu.utp.clinica.dao.AdministradorDAO;
import pe.edu.utp.clinica.model.Administrador;

@Repository
public class AdministradorDAOImpl implements AdministradorDAO {

    @Override
    public Administrador login(String usuario, String password) {
        String sql = "SELECT id_admin, dni, nombres, apellidos, cargo, usuario "
                + "FROM personal_administrativo WHERE usuario = ? AND password = SHA2(?, 256)";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Administrador(
                        rs.getInt("id_admin"),
                        rs.getString("dni"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("cargo"),
                        rs.getString("usuario"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en login de administrador: " + e.getMessage(), e);
        }
    }
}
