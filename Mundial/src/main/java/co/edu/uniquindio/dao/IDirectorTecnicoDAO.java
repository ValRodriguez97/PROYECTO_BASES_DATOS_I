package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.DirectorTecnico;
import java.util.List;
import java.util.Optional;

public interface IDirectorTecnicoDAO {
    void insertar(DirectorTecnico dt) throws Exception;
    void actualizar(DirectorTecnico dt) throws Exception;
    void eliminar(int id) throws Exception;
    Optional<DirectorTecnico> buscarPorId(int id) throws Exception;
    List<DirectorTecnico> listarTodos() throws Exception;
}