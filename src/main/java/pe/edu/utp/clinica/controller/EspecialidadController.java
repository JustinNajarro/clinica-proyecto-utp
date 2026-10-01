package pe.edu.utp.clinica.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.clinica.dao.EspecialidadDAO;
import pe.edu.utp.clinica.dao.MedicoDAO;
import pe.edu.utp.clinica.model.Especialidad;
import pe.edu.utp.clinica.model.Medico;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadDAO especialidadDAO;
    private final MedicoDAO medicoDAO;

    public EspecialidadController(EspecialidadDAO especialidadDAO, MedicoDAO medicoDAO) {
        this.especialidadDAO = especialidadDAO;
        this.medicoDAO = medicoDAO;
    }

    @GetMapping
    public List<Especialidad> listar() {
        return especialidadDAO.listar();
    }

    @GetMapping("/{id}/medicos")
    public List<Medico> medicos(@PathVariable("id") int id) {
        return medicoDAO.listarPorEspecialidad(id);
    }
}
