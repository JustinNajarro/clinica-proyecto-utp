package pe.edu.utp.clinica.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import pe.edu.utp.clinica.model.Cita;
import pe.edu.utp.clinica.model.dto.CitaRequest;

public interface CitaDAO {
    List<LocalTime> horasOcupadas(int idMedico, LocalDate fecha);
    Cita registrar(CitaRequest datos, int idConsultorio, String codigo);
    Cita buscarPorCodigo(String codigo);
    List<Cita> listarPorPaciente(String dni);
    List<Cita> listarTodas();
    boolean cancelar(int idCita, String dniPaciente);
    boolean actualizarEstado(int idCita, String estado);
}
