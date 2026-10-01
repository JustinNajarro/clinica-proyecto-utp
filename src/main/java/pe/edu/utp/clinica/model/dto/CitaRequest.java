package pe.edu.utp.clinica.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaRequest(String dniPaciente, int idMedico, LocalDate fecha, LocalTime hora, String motivo) {}
