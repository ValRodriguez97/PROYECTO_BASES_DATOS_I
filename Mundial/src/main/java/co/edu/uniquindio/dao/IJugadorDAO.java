package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Jugador;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IJugadorDAO {

    void              insertar(Jugador jugador)      throws Exception;
    void              actualizar(Jugador jugador)    throws Exception;
    void              eliminar(int id)          throws Exception;
    Optional<Jugador> buscarPorId(int id)       throws Exception;
    List<Jugador>     listarTodos()             throws Exception;
    List<Jugador> listarPorEquipo(int idEquipo) throws Exception;
    List<Jugador> jugadorMasCostosoPorConfederacion() throws Exception;
    List<Object[]> cantidadSub21PorEquipo() throws Exception;
    List<Jugador> filtrarPorPesoEstaturaEquipo(
            BigDecimal pesoMin, BigDecimal pesoMax,
            BigDecimal estaturaMin, BigDecimal estaturaMax,
            int idEquipo) throws Exception;
}
