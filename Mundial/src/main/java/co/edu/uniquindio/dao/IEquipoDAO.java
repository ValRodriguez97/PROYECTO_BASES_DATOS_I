package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Equipo;

import java.util.List;
import java.util.Optional;

public interface  IEquipoDAO {
    void insertar(Equipo equipo) throws Exception;
    void actualizar(Equipo equipo) throws Exception;
    void eliminar(int id) throws Exception;
    Optional<Equipo> buscarPorId(int id) throws Exception;
    List<Equipo> listarTodos() throws Exception;
    List<Equipo> listarPorConfederacion(int idConfederacion) throws Exception;
    List<Equipo> listarPorGrupo(int idGrupo) throws Exception;
}
