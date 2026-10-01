package pe.edu.utp.clinica.dao;

import java.util.List;

import pe.edu.utp.clinica.model.HorarioMedico;

public interface HorarioDAO {
    List<HorarioMedico> listarPorMedicoYDia(int idMedico, String diaSemana);
}
