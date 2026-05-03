package co.edu.uniquindio.model;

public class Ciudad {

    private int idCiudad;
    private String nombre;
    private Pais pais;

    public Ciudad() {}

    public Ciudad(int id, String nombre, Pais pais){
        this.idCiudad = id;
        this.nombre = nombre;
        this.pais = pais;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    @Override

    public String toString() {
        return nombre;
    }
}
