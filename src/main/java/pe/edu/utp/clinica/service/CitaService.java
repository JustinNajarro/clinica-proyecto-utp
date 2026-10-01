package pe.edu.utp.clinica.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.edu.utp.clinica.config.ApiException;
import pe.edu.utp.clinica.dao.CitaDAO;
import pe.edu.utp.clinica.dao.HorarioDAO;
import pe.edu.utp.clinica.dao.MedicoDAO;
import pe.edu.utp.clinica.model.Cita;
import pe.edu.utp.clinica.model.HorarioMedico;
import pe.edu.utp.clinica.model.dto.CitaRequest;
import pe.edu.utp.clinica.model.dto.Turno;

/** Reglas de negocio de las citas: disponibilidad y reserva. */
@Service
public class CitaService {

    private static final int MINUTOS_POR_TURNO = 30;
    private static final String[] DIAS =
            {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

    private final CitaDAO citaDAO;
    private final HorarioDAO horarioDAO;
    private final MedicoDAO medicoDAO;

    public CitaService(CitaDAO citaDAO, HorarioDAO horarioDAO, MedicoDAO medicoDAO) {
        this.citaDAO = citaDAO;
        this.horarioDAO = horarioDAO;
        this.medicoDAO = medicoDAO;
    }

    /** Turnos libres de un medico en una fecha. */
    public List<Turno> disponibilidad(int idMedico, LocalDate fecha) {
        List<Turno> libres = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        if (fecha.isBefore(hoy)) {
            return libres;
        }

        String dia = DIAS[fecha.getDayOfWeek().getValue() - 1];
        List<HorarioMedico> horarios = horarioDAO.listarPorMedicoYDia(idMedico, dia);
        List<LocalTime> ocupadas = citaDAO.horasOcupadas(idMedico, fecha);
        LocalTime ahora = LocalTime.now();

        for (HorarioMedico h : horarios) {
            LocalTime t = h.horaInicio();
            while (!t.plusMinutes(MINUTOS_POR_TURNO).isAfter(h.horaFin())) {
                boolean yaPaso = fecha.equals(hoy) && !t.isAfter(ahora);
                if (!yaPaso && !ocupadas.contains(t)) {
                    libres.add(new Turno(t, h.idConsultorio()));
                }
                t = t.plusMinutes(MINUTOS_POR_TURNO);
            }
        }
        return libres;
    }

    /** Valida el turno y registra la cita. */
    public Cita reservar(CitaRequest req) {
        if (req.dniPaciente() == null || req.fecha() == null || req.hora() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Faltan datos para registrar la cita.");
        }
        if (medicoDAO.buscarPorId(req.idMedico()) == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El medico no existe.");
        }

        Turno turno = disponibilidad(req.idMedico(), req.fecha()).stream()
                .filter(t -> t.hora().equals(req.hora()))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT,
                        "No hay horario disponible para esa fecha y hora."));

        String codigo = "HJAT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return citaDAO.registrar(req, turno.idConsultorio(), codigo);
    }
}
