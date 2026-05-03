package co.edu.uniquindio.services;

import co.edu.uniquindio.dao.*;
import co.edu.uniquindio.dao.Impl.*;
import co.edu.uniquindio.model.Jugador;
import co.edu.uniquindio.model.Partido;

import java.util.List;

public class MundialService {

    private final IJugadorDAO jugadorDAO = new JugadorDAOImpl();
    private final IPartidoDAO partidoDAO = new PartidoDAOImpl();

    public List<Jugador> jugadorMasCostosoPorConfederacion() throws Exception {
        return jugadorDAO.jugadorMasCostosoPorConfederacion();
    }

    public List<Partido> partidosPorEstadio(int idEstadio) throws Exception {
        return partidoDAO.listarPorEstadio(idEstadio);
    }

    public List<Object[]> equipoMasCostosoPorPaisSede() throws Exception {
        return partidoDAO.equipoMasCostosoPorPaisSede();
    }

    public List<Object[]> cantidadJugadoresSub21PorEquipo() throws Exception {
        return jugadorDAO.cantidadSub21PorEquipo();
    }
}
