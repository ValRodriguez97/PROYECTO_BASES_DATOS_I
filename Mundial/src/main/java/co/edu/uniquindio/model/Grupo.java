package co.edu.uniquindio.model;

import java.util.ArrayList;
import java.util.List;

public class Grupo {

    private int idGrupo;
    private char letra;
    private List<Equipo> equipos = new ArrayList<>();

    public Grupo(){}

    public Grupo(int id, char letra) {
        this.idGrupo = id;
        this.letra = letra;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public char getLetra() {
        return letra;
    }

    public void setLetra(char letra) {
        this.letra = letra;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    @Override
    public String toString() {
        return "Grupo " + letra;
    }
}
