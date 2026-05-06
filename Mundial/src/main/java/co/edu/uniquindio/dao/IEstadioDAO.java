package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Estadio;
import java.util.List;
import java.util.Optional;

public interface IEstadioDAO {
    void insertar(Estadio estadio) throws Exception;
    void actualizar(Estadio estadio) throws Exception;
    void eliminar(int id) throws Exception;
    Optional<Estadio> buscarPorId(int id) throws Exception;
    List<Estadio> listarTodos() throws Exception;
}