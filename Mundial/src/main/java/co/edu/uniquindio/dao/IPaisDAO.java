package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Pais;
import java.util.List;
import java.util.Optional;

public interface IPaisDAO {
    void insertar(Pais pais) throws Exception;
    void actualizar(Pais pais) throws Exception;
    void eliminar(int id) throws Exception;
    Optional<Pais> buscarPorId(int id) throws Exception;
    List<Pais> listarTodos() throws Exception;
    List<Pais> listarSedes() throws Exception; // solo México, USA, Canadá
}