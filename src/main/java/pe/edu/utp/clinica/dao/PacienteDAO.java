package pe.edu.utp.clinica.dao;

import pe.edu.utp.clinica.model.Paciente;
import pe.edu.utp.clinica.model.dto.RegistroPacienteRequest;

public interface PacienteDAO {
    Paciente login(String dni, String password);
    boolean existe(String dni, String correo);
    Paciente registrar(RegistroPacienteRequest datos);
}
