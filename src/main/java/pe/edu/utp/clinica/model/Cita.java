package pe.edu.utp.clinica.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** Cita con su detalle (paciente, medico, especialidad, consultorio). */
public record Cita(int idCita, String codigoConfirmacion, LocalDate fechaCita, LocalTime horaCita,
                   String estado, String motivoConsulta, String dniPaciente, String paciente,
                   int idMedico, String medico, String especialidad, String consultorio, int piso) {}
