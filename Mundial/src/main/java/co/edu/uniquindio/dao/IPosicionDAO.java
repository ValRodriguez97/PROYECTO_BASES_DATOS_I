package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Posicion;
import java.util.List;
import java.util.Optional;

public interface IPosicionDAO {
    void               insertar(Posicion p)   throws Exception;
    void               actualizar(Posicion p) throws Exception;
    void               eliminar(int id)       throws Exception;
    Optional<Posicion> buscarPorId(int id)    throws Exception;
    List<Posicion>     listarTodos()          throws Exception;
}
