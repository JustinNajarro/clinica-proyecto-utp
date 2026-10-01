package pe.edu.utp.clinica.model;

import java.time.LocalTime;

public record HorarioMedico(int idHorario, int idMedico, int idConsultorio,
                            String diaSemana, LocalTime horaInicio, LocalTime horaFin) {}
