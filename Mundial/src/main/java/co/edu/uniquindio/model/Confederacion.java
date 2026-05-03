package co.edu.uniquindio.model;

public class Confederacion {

    private int idConfederacion;
    private String nombre;
    private String sigla;

    public Confederacion(){}

    public Confederacion(int id, String nombre, String sigla) {
        this.idConfederacion = id;
        this.nombre = nombre;
        this.sigla = sigla;
    }

    public int getIdConfederacion() {
        return idConfederacion;
    }

    public void setIdConfederacion(int idConfederacion) {
        this.idConfederacion = idConfederacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String toString() {
        return nombre + " (" + sigla + ")";
    }
}
