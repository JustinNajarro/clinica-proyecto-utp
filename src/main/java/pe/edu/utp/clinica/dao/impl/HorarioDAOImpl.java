package pe.edu.utp.clinica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import pe.edu.utp.clinica.config.Conexion;
import pe.edu.utp.clinica.dao.HorarioDAO;
import pe.edu.utp.clinica.model.HorarioMedico;

@Repository
public class HorarioDAOImpl implements HorarioDAO {

    @Override
    public List<HorarioMedico> listarPorMedicoYDia(int idMedico, String diaSemana) {
        String sql = "SELECT id_horario, id_medico, id_consultorio, dia_semana, hora_inicio, hora_fin "
                + "FROM horario_medico WHERE id_medico = ? AND dia_semana = ? ORDER BY hora_inicio";
        List<HorarioMedico> lista = new ArrayList<>();
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setString(2, diaSemana);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new HorarioMedico(
                            rs.getInt("id_horario"),
                            rs.getInt("id_medico"),
                            rs.getInt("id_consultorio"),
                            rs.getString("dia_semana"),
                            rs.getObject("hora_inicio", LocalTime.class),
                            rs.getObject("hora_fin", LocalTime.class)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar horarios: " + e.getMessage(), e);
        }
        return lista;
    }
}
