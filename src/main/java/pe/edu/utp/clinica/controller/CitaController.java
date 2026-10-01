package pe.edu.utp.clinica.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.clinica.config.ApiException;
import pe.edu.utp.clinica.dao.CitaDAO;
import pe.edu.utp.clinica.model.Cita;
import pe.edu.utp.clinica.model.dto.CitaRequest;
import pe.edu.utp.clinica.model.dto.EstadoRequest;
import pe.edu.utp.clinica.model.dto.Turno;
import pe.edu.utp.clinica.service.CitaService;

@RestController
@RequestMapping("/api")
public class CitaController {

    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "ATENDIDA", "CANCELADA");

    private final CitaService citaService;
    private final CitaDAO citaDAO;

    public CitaController(CitaService citaService, CitaDAO citaDAO) {
        this.citaService = citaService;
        this.citaDAO = citaDAO;
    }

    /** Ej: GET /api/medicos/1/disponibilidad?fecha=2026-10-05 */
    @GetMapping("/medicos/{id}/disponibilidad")
    public List<Turno> disponibilidad(@PathVariable("id") int idMedico,
                                      @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.disponibilidad(idMedico, fecha);
    }

    @PostMapping("/citas")
    @ResponseStatus(HttpStatus.CREATED)
    public Cita reservar(@RequestBody CitaRequest req) {
        return citaService.reservar(req);
    }

    @GetMapping("/citas/paciente/{dni}")
    public List<Cita> citasDelPaciente(@PathVariable("dni") String dni) {
        return citaDAO.listarPorPaciente(dni);
    }

    @PutMapping("/citas/{id}/cancelar")
    public Cita cancelar(@PathVariable("id") int idCita, @RequestParam("dni") String dni) {
        if (!citaDAO.cancelar(idCita, dni)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La cita no existe o ya no esta pendiente.");
        }
        return citaDAO.listarPorPaciente(dni).stream()
                .filter(c -> c.idCita() == idCita)
                .findFirst()
                .orElse(null);
    }

    /** Panel del administrador */
    @GetMapping("/citas")
    public List<Cita> listarTodas() {
        return citaDAO.listarTodas();
    }

    @PutMapping("/citas/{id}/estado")
    public void cambiarEstado(@PathVariable("id") int idCita, @RequestBody EstadoRequest req) {
        String estado = req.estado() == null ? "" : req.estado().toUpperCase();
        if (!ESTADOS.contains(estado)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Estado invalido. Usa PENDIENTE, ATENDIDA o CANCELADA.");
        }
        if (!citaDAO.actualizarEstado(idCita, estado)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "La cita no existe.");
        }
    }
}
