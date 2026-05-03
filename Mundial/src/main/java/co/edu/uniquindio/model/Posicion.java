package co.edu.uniquindio.model;

public class Posicion {

    private int idPosicion;
    private String nombre;

    public Posicion() {}

    public Posicion(int id, String nombre){
        this.idPosicion = id;
        this.nombre = nombre;
    }

    public int getIdPosicion() {
        return idPosicion;
    }

    public void setIdPosicion(int idPosicion) {
        this.idPosicion = idPosicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
