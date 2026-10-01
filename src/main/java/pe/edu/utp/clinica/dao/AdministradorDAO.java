package pe.edu.utp.clinica.dao;

import pe.edu.utp.clinica.model.Administrador;

public interface AdministradorDAO {
    Administrador login(String usuario, String password);
}
