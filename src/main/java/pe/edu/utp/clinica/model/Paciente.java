package pe.edu.utp.clinica.model;

public record Paciente(String dni, String nombres, String apellidos, String telefono,
                       String correo, String sexo, String seguro) {}
