package co.edu.uniquindio.model;

import co.edu.uniquindio.model.Enum.TipoUsuario;

public class Administrador extends  Usuario{

    public Administrador() {
        super();
        setTipoUsuario(TipoUsuario.ADMINISTRADOR);
    }

    public Administrador(int id, String nombreUsuario, String contraseña){
        super(id, nombreUsuario, contraseña, TipoUsuario.ADMINISTRADOR);
    }

    @Override
    public boolean puedeCrearUsuarios() {
        return true;
    }

    @Override
    public boolean puedeEjecutarCRUD() {
        return true;
    }

    @Override
    public boolean puedeEjecutarConsultas() {
        return true;
    }
}
