package co.edu.uniquindio.model;

import co.edu.uniquindio.model.Enum.TipoUsuario;

public class UsuarioTradicional extends Usuario {

    public UsuarioTradicional() {
        super();
        setTipoUsuario(TipoUsuario.TRADICIONAL);
    }

    public UsuarioTradicional(int id, String nombreUsuario, String contraseña){
        super(id, nombreUsuario, contraseña, TipoUsuario.TRADICIONAL);
    }

    @Override
    public boolean puedeCrearUsuarios(){
        return false;
    }

    @Override
    public boolean puedeEjecutarCRUD(){
        return true;
    }

    @Override
    public boolean puedeEjecutarConsultas() {
        return true;
    }


}
