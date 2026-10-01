package pe.edu.utp.clinica.model.dto;

public record RegistroPacienteRequest(String dni, String nombres, String apellidos, String telefono,
                                      String correo, String password, String sexo, String seguro) {}
