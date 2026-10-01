package pe.edu.utp.clinica.dao;

import java.util.List;

import pe.edu.utp.clinica.model.Medico;

public interface MedicoDAO {
    List<Medico> listarPorEspecialidad(int idEspecialidad);
    Medico buscarPorId(int idMedico);
}
