package co.edu.uniquindio.model;

import java.time.LocalDateTime;

public class Bitacora {
    private int idBitacora;
    private int idUsuario;
    private LocalDateTime fechaHoraIngreso;
    private LocalDateTime fechaHoraSalida;

    public Bitacora() {}

    public Bitacora(int idBitacora, int idUsuario, LocalDateTime ingreso, LocalDateTime salida) {
        this.idBitacora = idBitacora;
        this.idUsuario = idUsuario;
        this.fechaHoraIngreso = ingreso;
        this.fechaHoraSalida = salida;
    }

    public int getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(int id) {
        this.idBitacora = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int id) {
        this.idUsuario = id;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime d) {
        this.fechaHoraIngreso = d;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime d) {
        this.fechaHoraSalida = d;
    }

    @Override
    public String toString() {
        return "Bitacora{idUsuario=" + idUsuario + ", ingreso=" + fechaHoraIngreso + ", salida="  + fechaHoraSalida + "}";
    }
}

