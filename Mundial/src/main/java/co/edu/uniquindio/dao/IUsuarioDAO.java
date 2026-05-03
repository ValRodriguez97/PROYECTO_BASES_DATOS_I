package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioDAO {
    void insertar(Usuario usuario) throws Exception;
    void actualizar(Usuario usuario) throws Exception;
    void eliminar(int idUsuario) throws  Exception;
    Optional<Usuario> buscarPorId(int idUsuario) throws  Exception;
    Optional<Usuario> buscarPorNombre(String nombre) throws Exception;
    List<Usuario> listarTodos() throws Exception;
}
