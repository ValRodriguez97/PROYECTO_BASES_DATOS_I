package co.edu.uniquindio.model;

import co.edu.uniquindio.model.Enum.TipoUsuario;

public abstract class  Usuario {

    private int idUsuario;
    private String nombreUsuario;
    private String contraseña;
    private TipoUsuario tipoUsuario;

    public Usuario(){}

    public Usuario(int idUsuario, String nombreUsuario, String contraseña, TipoUsuario tipoUsuario){
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.tipoUsuario = tipoUsuario;
    }

    public abstract boolean puedeCrearUsuarios();
    public abstract boolean puedeEjecutarCRUD();
    public abstract boolean puedeEjecutarConsultas();

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return nombreUsuario + " [" + tipoUsuario + "]";
    }
}
