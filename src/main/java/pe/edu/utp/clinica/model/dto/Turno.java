package pe.edu.utp.clinica.model.dto;

import java.time.LocalTime;

/** Un turno libre de 30 minutos. */
public record Turno(LocalTime hora, int idConsultorio) {}
