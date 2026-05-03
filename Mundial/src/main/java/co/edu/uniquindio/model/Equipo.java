package co.edu.uniquindio.model;

public class Equipo {

    private int idEquipo;
    private String nombre;
    private int rankingFifa;
    private Pais pais;
    private Confederacion confederacion;
    private DirectorTecnico directorTecnico;

    public Equipo() {}

    public Equipo(int id, String nombre, int rankingFifa, Pais pais, Confederacion confederacion, DirectorTecnico dt){
        this. idEquipo = id;
        this.nombre = nombre;
        this.rankingFifa = rankingFifa;
        this.pais = pais;
        this.confederacion = confederacion;
        this.directorTecnico = dt;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getRankingFifa() {
        return rankingFifa;
    }

    public void setRankingFifa(int rankingFifa) {
        this.rankingFifa = rankingFifa;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public Confederacion getConfederacion() {
        return confederacion;
    }

    public void setConfederacion(Confederacion confederacion) {
        this.confederacion = confederacion;
    }

    public DirectorTecnico getDirectorTecnico() {
        return directorTecnico;
    }

    public void setDirectorTecnico(DirectorTecnico directorTecnico) {
        this.directorTecnico = directorTecnico;
    }

    @Override
    public String toString() {
        return nombre + " (FIFA #" + rankingFifa + ")";
    }
}
