package pe.edu.utp.clinica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import pe.edu.utp.clinica.config.Conexion;
import pe.edu.utp.clinica.dao.EspecialidadDAO;
import pe.edu.utp.clinica.model.Especialidad;

@Repository
public class EspecialidadDAOImpl implements EspecialidadDAO {

    @Override
    public List<Especialidad> listar() {
        String sql = "SELECT id_especialidad, nombre_especialidad, descripcion FROM especialidad ORDER BY nombre_especialidad";
        List<Especialidad> lista = new ArrayList<>();
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Especialidad(
                        rs.getInt("id_especialidad"),
                        rs.getString("nombre_especialidad"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar especialidades: " + e.getMessage(), e);
        }
        return lista;
    }
}
