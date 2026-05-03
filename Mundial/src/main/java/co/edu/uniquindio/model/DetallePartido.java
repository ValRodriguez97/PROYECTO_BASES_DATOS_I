package co.edu.uniquindio.model;

import co.edu.uniquindio.model.Enum.Condicion;

public class DetallePartido {

    private int idPartido;
    private Condicion condicion;
    private Equipo equipo;
    private int golesAnotados;

    public DetallePartido () {}

    public DetallePartido(int idPartido, Equipo equipo, Condicion condicion, int goles){
        this.idPartido = idPartido;
        this.equipo = equipo;
        this.condicion = condicion;
        this.golesAnotados = goles;
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public Condicion getCondicion() {
        return condicion;
    }

    public void setCondicion(Condicion condicion) {
        this.condicion = condicion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public int getGolesAnotados() {
        return golesAnotados;
    }

    public void setGolesAnotados(int golesAnotados) {
        this.golesAnotados = golesAnotados;
    }

    @Override
    public String toString() {
        return equipo.getNombre() + " [" + condicion + "] " + golesAnotados + " goles";
    }
}
