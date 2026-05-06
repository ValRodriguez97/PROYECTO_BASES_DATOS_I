package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Posicion;
import java.util.List;
import java.util.Optional;

public interface IPosicionDAO {
    void insertar(Posicion p) throws Exception;
    Optional<Posicion> buscarPorId(int id) throws Exception;
    List<Posicion> listarTodos() throws Exception;
}