package co.edu.uniquindio.services;

import co.edu.uniquindio.dao.*;
import co.edu.uniquindio.dao.Impl.*;
import co.edu.uniquindio.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que expone todas las operaciones CRUD del sistema.
 * Antes de cualquier escritura verifica que el usuario en sesión
 * tenga permiso (puedeEjecutarCRUD()).
 */
public class GestionDatosService {

    // --- DAOs ---
    private final IEquipoDAO          equipoDAO   = new EquipoDAOImpl();
    private final IJugadorDAO         jugadorDAO  = new JugadorDAOImpl();
    private final IPartidoDAO         partidoDAO  = new PartidoDAOImpl();
    private final IUsuarioDAO         usuarioDAO  = new UsuarioDAOImpl();
    private final IPaisDAO            paisDAO     = new PaisDAOImpl();
    private final IConfederacionDAO   confDAO     = new ConfederacionDAOImpl();
    private final IDirectorTecnicoDAO dtDAO       = new DirectorTecnicoDAOImpl();
    private final IEstadioDAO         estadioDAO  = new EstadioDAOImpl();
    private final IGrupoDAO           grupoDAO    = new GrupoDAOImpl();
    private final IPosicionDAO        posicionDAO = new PosicionDAOImpl();

    private final SistemaSeguridadService seguridad;

    public GestionDatosService(SistemaSeguridadService seguridad) {
        this.seguridad = seguridad;
    }

    // ===================== VALIDACIÓN =====================

    private void validarCRUD() throws Exception {
        if (!seguridad.haySesionActiva())
            throw new Exception("No hay sesión activa.");
        if (!seguridad.getUsuarioActual().puedeEjecutarCRUD())
            throw new Exception("El usuario no tiene permisos para ejecutar CRUDs.");
    }

    // ===================== EQUIPOS =====================

    public void crearEquipo(Equipo e) throws Exception {
        validarCRUD();
        equipoDAO.insertar(e);
    }

    public void actualizarEquipo(Equipo e) throws Exception {
        validarCRUD();
        equipoDAO.actualizar(e);
    }

    public void eliminarEquipo(int id) throws Exception {
        validarCRUD();
        equipoDAO.eliminar(id);
    }

    public Optional<Equipo> buscarEquipo(int id) throws Exception {
        return equipoDAO.buscarPorId(id);
    }

    public List<Equipo> listarEquipos() throws Exception {
        return equipoDAO.listarTodos();
    }

    public List<Equipo> listarEquiposPorConfederacion(int idConf) throws Exception {
        return equipoDAO.listarPorConfederacion(idConf);
    }

    public List<Equipo> listarEquiposPorGrupo(int idGrupo) throws Exception {
        return equipoDAO.listarPorGrupo(idGrupo);
    }

    // ===================== JUGADORES =====================

    public void crearJugador(Jugador j) throws Exception {
        validarCRUD();
        jugadorDAO.insertar(j);
    }

    public void actualizarJugador(Jugador j) throws Exception {
        validarCRUD();
        jugadorDAO.actualizar(j);
    }

    public void eliminarJugador(int id) throws Exception {
        validarCRUD();
        jugadorDAO.eliminar(id);
    }

    public Optional<Jugador> buscarJugador(int id) throws Exception {
        return jugadorDAO.buscarPorId(id);
    }

    public List<Jugador> listarJugadores() throws Exception {
        return jugadorDAO.listarTodos();
    }

    public List<Jugador> listarJugadoresPorEquipo(int idEquipo) throws Exception {
        return jugadorDAO.listarPorEquipo(idEquipo);
    }

    // ===================== PARTIDOS =====================

    public void crearPartido(Partido p) throws Exception {
        validarCRUD();
        partidoDAO.insertar(p);
    }

    public void actualizarPartido(Partido p) throws Exception {
        validarCRUD();
        partidoDAO.actualizar(p);
    }

    public void eliminarPartido(int id) throws Exception {
        validarCRUD();
        partidoDAO.eliminar(id);
    }

    public Optional<Partido> buscarPartido(int id) throws Exception {
        return partidoDAO.buscarPorId(id);
    }

    public List<Partido> listarPartidos() throws Exception {
        return partidoDAO.listarTodos();
    }

    // ===================== USUARIOS (solo admin) =====================

    public List<Usuario> listarUsuarios() throws Exception {
        if (!seguridad.haySesionActiva())
            throw new Exception("No hay sesión activa.");
        if (!seguridad.getUsuarioActual().puedeCrearUsuarios())
            throw new Exception("Solo el Administrador puede ver todos los usuarios.");
        return usuarioDAO.listarTodos();
    }

    public void eliminarUsuario(int id) throws Exception {
        if (!seguridad.haySesionActiva())
            throw new Exception("No hay sesión activa.");
        if (!seguridad.getUsuarioActual().puedeCrearUsuarios())
            throw new Exception("Solo el Administrador puede eliminar usuarios.");
        usuarioDAO.eliminar(id);
    }

    // ===================== CATÁLOGOS (solo lectura) =====================

    public List<Pais>           listarPaises()         throws Exception { return paisDAO.listarTodos(); }
    public List<Pais>           listarSedes()          throws Exception { return paisDAO.listarSedes(); }
    public List<Confederacion>  listarConfederaciones()throws Exception { return confDAO.listarTodos(); }
    public List<DirectorTecnico>listarDirectoresTecnicos() throws Exception { return dtDAO.listarTodos(); }
    public List<Estadio>        listarEstadios()       throws Exception { return estadioDAO.listarTodos(); }
    public List<Grupo>          listarGrupos()         throws Exception { return grupoDAO.listarTodos(); }
    public List<Posicion>       listarPosiciones()     throws Exception { return posicionDAO.listarTodos(); }

    // ===================== REPORTES (datos) =====================

    public List<Object[]> valorTotalPorEquipoYConfederacion(int idConfederacion) throws Exception {
        return grupoDAO.valorTotalJugadoresPorEquipoYConfederacion(idConfederacion);
    }

    public List<Object[]> equiposPorPaisAnfitrion() throws Exception {
        return grupoDAO.equiposPorPaisAnfitrion();
    }
}