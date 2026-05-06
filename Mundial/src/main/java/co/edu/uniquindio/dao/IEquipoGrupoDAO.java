package co.edu.uniquindio.dao;

public interface IEquipoGrupoDAO {
    void asignar(int idGrupo, int idEquipo) throws Exception;
    void remover(int idGrupo, int idEquipo) throws Exception;
    int  contarEquiposEnGrupo(int idGrupo) throws Exception;
    boolean equipoTieneGrupo(int idEquipo) throws Exception;
}
