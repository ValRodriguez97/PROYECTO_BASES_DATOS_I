package co.edu.uniquindio.model;

import java.time.LocalDate;

public class DirectorTecnico {

    private int idDT;
    private String nombre;
    private String apellido;
    private String nacionalidad;
    private LocalDate fechaNacimiento;

    public DirectorTecnico() {}

    public DirectorTecnico(int id, String nombre, String apellido, String nacionalidad, LocalDate fechaNacimiento) {
        this.idDT = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getIdDT() {
        return idDT;
    }

    public void setIdDT(int idDT) {
        this.idDT = idDT;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + nacionalidad + ")";
    }
}
