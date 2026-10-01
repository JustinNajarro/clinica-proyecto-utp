package pe.edu.utp.clinica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import pe.edu.utp.clinica.config.Conexion;
import pe.edu.utp.clinica.dao.MedicoDAO;
import pe.edu.utp.clinica.model.Medico;

@Repository
public class MedicoDAOImpl implements MedicoDAO {

    private static final String COLUMNAS =
            "id_medico, dni, nombres, apellidos, telefono, correo, codigo_doctor, id_especialidad";

    @Override
    public List<Medico> listarPorEspecialidad(int idEspecialidad) {
        String sql = "SELECT " + COLUMNAS + " FROM medico WHERE id_especialidad = ? ORDER BY apellidos";
        List<Medico> lista = new ArrayList<>();
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar medicos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Medico buscarPorId(int idMedico) {
        String sql = "SELECT " + COLUMNAS + " FROM medico WHERE id_medico = ?";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar medico: " + e.getMessage(), e);
        }
    }

    private Medico mapear(ResultSet rs) throws SQLException {
        return new Medico(
                rs.getInt("id_medico"),
                rs.getString("dni"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("codigo_doctor"),
                rs.getInt("id_especialidad"));
    }
}
