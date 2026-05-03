package co.edu.uniquindio.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class Jugador {

    private int idJugador;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private BigDecimal estatura; // m
    private BigDecimal peso; // Kg
    private BigDecimal valorMercado; // millones de euros
    private Equipo equipo;
    private Posicion posicion;

    public Jugador(){}

    public Jugador(int id, String nombre, String apellido, LocalDate fechaNacimiento, BigDecimal estatura,
                   BigDecimal peso, BigDecimal valorMercado, Equipo equipo, Posicion posicion) {
        this.idJugador = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.estatura = estatura;
        this.peso = peso;
        this.valorMercado = valorMercado;
        this.equipo = equipo;
        this.posicion = posicion;
    }

    public int getEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public BigDecimal getEstatura() {
        return estatura;
    }

    public void setEstatura(BigDecimal estatura) {
        this.estatura = estatura;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(BigDecimal valorMercado) {
        this.valorMercado = valorMercado;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " | " + posicion + " | " + equipo;
    }
}
