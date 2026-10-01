package pe.edu.utp.clinica.model;

public record Administrador(int idAdmin, String dni, String nombres, String apellidos,
                            String cargo, String usuario) {}
