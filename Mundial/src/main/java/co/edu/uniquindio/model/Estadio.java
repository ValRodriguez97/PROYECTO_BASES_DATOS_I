package co.edu.uniquindio.model;

public class Estadio {

    private int idEstadio;
    private String nombre;
    private int capacidad;
    private Ciudad ciudad;

    public Estadio() {}

    public Estadio(int id, String nombre, int capacidad, Ciudad ciudad){
        this.idEstadio = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ciudad = ciudad;
    }

    public int getIdEstadio() {
        return idEstadio;
    }

    public void setIdEstadio(int idEstadio) {
        this.idEstadio = idEstadio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return nombre + " (cap: " + capacidad + ")";
    }
}
