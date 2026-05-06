package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Grupo;
import java.util.List;
import java.util.Optional;

public interface IGrupoDAO {
    void               insertar(Grupo g)      throws Exception;
    void               actualizar(Grupo g)    throws Exception;
    void               eliminar(int id)       throws Exception;
    Optional<Grupo>    buscarPorId(int id)    throws Exception;
    List<Grupo>        listarTodos()          throws Exception;

    // Reportes
    List<Object[]>     valorTotalJugadoresPorEquipoYConfederacion(int idConfederacion) throws Exception;
    List<Object[]>     equiposPorPaisAnfitrion() throws Exception;
}
