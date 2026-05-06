package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.DetallePartido;

import java.util.List;
import java.util.Optional;

public interface IDetallePartidoDAO {
    void insertar(DetallePartido d) throws Exception;
    void actualizar(DetallePartido d) throws Exception;
    void eliminar(int idPartido, int idEquipo) throws Exception;
    Optional<DetallePartido> buscarPorClave(int idPartido, int idEquipo) throws Exception;
    List<DetallePartido> listarPorPartido(int idPartido) throws Exception;
}
