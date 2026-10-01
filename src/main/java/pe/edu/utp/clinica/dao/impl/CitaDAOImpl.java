package pe.edu.utp.clinica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import pe.edu.utp.clinica.config.ApiException;
import pe.edu.utp.clinica.config.Conexion;
import pe.edu.utp.clinica.dao.CitaDAO;
import pe.edu.utp.clinica.model.Cita;
import pe.edu.utp.clinica.model.dto.CitaRequest;

@Repository
public class CitaDAOImpl implements CitaDAO {

    private static final int ERROR_DUPLICADO = 1062;

    @Override
    public List<LocalTime> horasOcupadas(int idMedico, LocalDate fecha) {
        String sql = "SELECT hora_cita FROM cita_medica "
                + "WHERE id_medico = ? AND fecha_cita = ? AND estado <> 'CANCELADA'";
        List<LocalTime> horas = new ArrayList<>();
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setObject(2, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horas.add(rs.getObject("hora_cita", LocalTime.class));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar horas ocupadas: " + e.getMessage(), e);
        }
        return horas;
    }

    @Override
    public Cita registrar(CitaRequest d, int idConsultorio, String codigo) {
        String sql = "INSERT INTO cita_medica (dni_paciente, id_medico, id_consultorio, fecha_cita, hora_cita, "
                + "codigo_confirmacion, motivo_consulta) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, d.dniPaciente());
            ps.setInt(2, d.idMedico());
            ps.setInt(3, idConsultorio);
            ps.setObject(4, d.fecha());
            ps.setObject(5, d.hora());
            ps.setString(6, codigo);
            ps.setString(7, d.motivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_DUPLICADO) {
                throw new ApiException(HttpStatus.CONFLICT, "Ese horario acaba de ser reservado. Elige otro.");
            }
            throw new RuntimeException("Error al registrar cita: " + e.getMessage(), e);
        }
        return buscarPorCodigo(codigo);
    }

    @Override
    public Cita buscarPorCodigo(String codigo) {
        List<Cita> r = consultar("SELECT * FROM vw_citas_detalle WHERE codigo_confirmacion = ?", codigo);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public List<Cita> listarPorPaciente(String dni) {
        return consultar("SELECT * FROM vw_citas_detalle WHERE dni_paciente = ? "
                + "ORDER BY fecha_cita DESC, hora_cita DESC", dni);
    }

    @Override
    public List<Cita> listarTodas() {
        return consultar("SELECT * FROM vw_citas_detalle ORDER BY fecha_cita DESC, hora_cita");
    }

    @Override
    public boolean cancelar(int idCita, String dniPaciente) {
        String sql = "UPDATE cita_medica SET estado = 'CANCELADA' "
                + "WHERE id_cita = ? AND dni_paciente = ? AND estado = 'PENDIENTE'";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ps.setString(2, dniPaciente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar cita: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizarEstado(int idCita, String estado) {
        String sql = "UPDATE cita_medica SET estado = ? WHERE id_cita = ?";
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_DUPLICADO) {
                throw new ApiException(HttpStatus.CONFLICT, "Ese horario ya esta ocupado por otra cita.");
            }
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage(), e);
        }
    }

    private List<Cita> consultar(String sql, Object... params) {
        List<Cita> lista = new ArrayList<>();
        try (Connection cn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar citas: " + e.getMessage(), e);
        }
        return lista;
    }

    private Cita mapear(ResultSet rs) throws SQLException {
        return new Cita(
                rs.getInt("id_cita"),
                rs.getString("codigo_confirmacion"),
                rs.getObject("fecha_cita", LocalDate.class),
                rs.getObject("hora_cita", LocalTime.class),
                rs.getString("estado"),
                rs.getString("motivo_consulta"),
                rs.getString("dni_paciente"),
                rs.getString("paciente"),
                rs.getInt("id_medico"),
                rs.getString("medico"),
                rs.getString("especialidad"),
                rs.getString("consultorio"),
                rs.getInt("piso"));
    }
}
