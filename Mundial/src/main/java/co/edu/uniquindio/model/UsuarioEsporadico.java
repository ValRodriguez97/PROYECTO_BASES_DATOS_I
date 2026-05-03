package co.edu.uniquindio.model;

import co.edu.uniquindio.model.Enum.TipoUsuario;

public class UsuarioEsporadico extends Usuario{

    public UsuarioEsporadico(){
        super();
        setTipoUsuario(TipoUsuario.ESPORADICO);
    }

    public UsuarioEsporadico(int id, String nombreUsuario, String contraseña){
        super( id, nombreUsuario, contraseña, TipoUsuario.ESPORADICO);
    }

    @Override
    public boolean puedeCrearUsuarios(){
        return false;
    }

    @Override
    public boolean puedeEjecutarCRUD() {
        return false;
    }

    @Override
    public boolean puedeEjecutarConsultas(){
        return true;
    }
}
