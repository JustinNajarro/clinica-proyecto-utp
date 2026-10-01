package pe.edu.utp.clinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.utp.clinica.config.ApiException;
import pe.edu.utp.clinica.dao.AdministradorDAO;
import pe.edu.utp.clinica.dao.PacienteDAO;
import pe.edu.utp.clinica.model.Administrador;
import pe.edu.utp.clinica.model.Paciente;
import pe.edu.utp.clinica.model.dto.LoginAdminRequest;
import pe.edu.utp.clinica.model.dto.LoginPacienteRequest;
import pe.edu.utp.clinica.model.dto.RegistroPacienteRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PacienteDAO pacienteDAO;
    private final AdministradorDAO administradorDAO;

    public AuthController(PacienteDAO pacienteDAO, AdministradorDAO administradorDAO) {
        this.pacienteDAO = pacienteDAO;
        this.administradorDAO = administradorDAO;
    }

    @PostMapping("/paciente/login")
    public Paciente loginPaciente(@RequestBody LoginPacienteRequest req) {
        Paciente p = pacienteDAO.login(req.dni(), req.password());
        if (p == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "DNI o contrasena incorrectos.");
        }
        return p;
    }

    @PostMapping("/paciente/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente registrar(@RequestBody RegistroPacienteRequest req) {
        if (vacio(req.dni()) || vacio(req.nombres()) || vacio(req.apellidos())
                || vacio(req.correo()) || vacio(req.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Completa DNI, nombres, apellidos, correo y contrasena.");
        }
        if (!req.dni().matches("\\d{8}")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El DNI debe tener 8 digitos.");
        }
        if (pacienteDAO.existe(req.dni(), req.correo())) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un paciente con ese DNI o correo.");
        }
        return pacienteDAO.registrar(req);
    }

    @PostMapping("/admin/login")
    public Administrador loginAdmin(@RequestBody LoginAdminRequest req) {
        Administrador a = administradorDAO.login(req.usuario(), req.password());
        if (a == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos.");
        }
        return a;
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
