package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Confederacion;
import java.util.List;
import java.util.Optional;

public interface IConfederacionDAO {
    void insertar(Confederacion c) throws Exception;
    void actualizar(Confederacion c) throws Exception;
    void eliminar(int id) throws Exception;
    Optional<Confederacion> buscarPorId(int id) throws Exception;
    List<Confederacion> listarTodos() throws Exception;
}