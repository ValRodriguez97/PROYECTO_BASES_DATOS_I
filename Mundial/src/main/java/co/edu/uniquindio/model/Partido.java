package co.edu.uniquindio.model;

import co.edu.uniquindio.model.Enum.Condicion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Partido {

    private int idPartido;
    private LocalDateTime horaFecha;
    private Grupo  grupo;
    private Estadio estadio;
    private List<DetallePartido> detalles = new ArrayList<>();

    public Partido() {}

    public Partido(int id, LocalDateTime horaFecha, Grupo grupo, Estadio estadio){
        this.idPartido = id;
        this.horaFecha = horaFecha;
        this.grupo = grupo;
        this.estadio = estadio;
    }

    public void agregarDetalle(DetallePartido detalle) {
        if (detalle == null) return;

        boolean yaExiste = detalles.stream().anyMatch(d -> d.getCondicion() == detalle.getCondicion());

        if (yaExiste) {
            throw new IllegalArgumentException("Ya existe un equipo con condición " + detalle.getCondicion());
        }

        this.detalles.add(detalle);
    }

    public Equipo getEquipoLocal() {
        return detalles.stream().filter(d -> d.getCondicion() == Condicion.LOCAL)
                .map(DetallePartido::getEquipo).findFirst().orElse(null);
    }

    public Equipo getEquipoVisitante(){
        return detalles.stream().filter(d -> d.getCondicion() == Condicion.VISITANTE)
                .map(DetallePartido::getEquipo).findFirst().orElse(null);
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public LocalDateTime getHoraFecha() {
        return horaFecha;
    }

    public void setHoraFecha(LocalDateTime horaFecha) {
        this.horaFecha = horaFecha;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Estadio getEstadio() {
        return estadio;
    }

    public void setEstadio(Estadio estadio) {
        this.estadio = estadio;
    }

    public List<DetallePartido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePartido> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        Equipo local = getEquipoLocal();
        Equipo visitante = getEquipoVisitante();
        return (local != null ? local.getNombre() : "?")
                + " vs " + (visitante != null ? visitante.getNombre() : "?")
                + " | " + horaFecha + " | " + estadio.getNombre();
    }
}
