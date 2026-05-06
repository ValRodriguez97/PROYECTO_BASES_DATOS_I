package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Partido;

import java.util.List;
import java.util.Optional;

public interface IPartidoDAO {

    void              insertar(Partido partido)   throws Exception;
    /** Inserta el Partido + sus DetallePartido (LOCAL y VISITANTE) en una sola transacción. */
    void              insertarConDetalles(Partido partido) throws Exception;
    void              actualizar(Partido partido) throws Exception;
    void              eliminar(int id)       throws Exception;
    Optional<Partido> buscarPorId(int id)    throws Exception;
    List<Partido>     listarTodos()          throws Exception;
    List<Partido>     listarPorEstadio(int idEstadio) throws Exception;
    List<Partido> listarPorGrupo(int idGrupo)     throws Exception;
    List<Object[]> equipoMasCostosoPorPaisSede() throws Exception;

}
