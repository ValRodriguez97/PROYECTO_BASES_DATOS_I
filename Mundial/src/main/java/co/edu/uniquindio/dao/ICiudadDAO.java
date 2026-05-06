package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Ciudad;

import java.util.List;
import java.util.Optional;

public interface ICiudadDAO {
    void              insertar(Ciudad c)      throws Exception;
    void              actualizar(Ciudad c)    throws Exception;
    void              eliminar(int id)        throws Exception;
    Optional<Ciudad>  buscarPorId(int id)     throws Exception;
    List<Ciudad>      listarTodos()           throws Exception;
    List<Ciudad>      listarPorPais(int idPais) throws Exception;
}
