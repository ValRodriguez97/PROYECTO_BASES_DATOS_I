package co.edu.uniquindio.model;

public class Pais {

    private int idPais;
    private String nombre;
    private String continente;

    public Pais(){}

    public Pais(int id, String nombre, String continente) {
        this.idPais = id;
        this.nombre = nombre;
        this.continente = continente;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContinente() {
        return continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
