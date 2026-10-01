package pe.edu.utp.clinica.model;

public record Medico(int idMedico, String dni, String nombres, String apellidos,
                     String telefono, String correo, String codigoDoctor, int idEspecialidad) {}
