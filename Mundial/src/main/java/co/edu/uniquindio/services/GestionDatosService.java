package co.edu.uniquindio.services;

import co.edu.uniquindio.dao.*;
import co.edu.uniquindio.dao.Impl.*;
import co.edu.uniquindio.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que expone todas las operaciones CRUD y consultas de bitácora del sistema.
 * Antes de cualquier escritura verifica que el usuario en sesión tenga permiso
 * (puedeEjecutarCRUD()). Las operaciones de administración (gestión de usuarios,
 * lectura de bitácora) sólo las puede ejecutar el Administrador.
 */
public class GestionDatosService {

    // --- DAOs ---
    private final IEquipoDAO          equipoDAO     = new EquipoDAOImpl();
    private final IJugadorDAO         jugadorDAO    = new JugadorDAOImpl();
    private final IPartidoDAO         partidoDAO    = new PartidoDAOImpl();
    private final IUsuarioDAO         usuarioDAO    = new UsuarioDAOImpl();
    private final IPaisDAO            paisDAO       = new PaisDAOImpl();
    private final IConfederacionDAO   confDAO       = new ConfederacionDAOImpl();
    private final IDirectorTecnicoDAO dtDAO         = new DirectorTecnicoDAOImpl();
    private final IEstadioDAO         estadioDAO    = new EstadioDAOImpl();
    private final ICiudadDAO          ciudadDAO     = new CiudadDAOImpl();
    private final IGrupoDAO           grupoDAO      = new GrupoDAOImpl();
    private final IPosicionDAO        posicionDAO   = new PosicionDAOImpl();
    private final IDetallePartidoDAO  detalleDAO    = new DetallePartidoDAOImpl();
    private final IEquipoGrupoDAO     equipoGrupoDAO= new EquipoGrupoDAOImpl();
    private final IBitacoraDAO        bitacoraDAO   = new BitacoraDAOImpl();

    private final SistemaSeguridadService seguridad;

    public GestionDatosService(SistemaSeguridadService seguridad) {
        this.seguridad = seguridad;
    }

    // ===================== VALIDACIÓN DE PERMISOS =====================

    private void validarCRUD() throws Exception {
        if (!seguridad.haySesionActiva())
            throw new Exception("No hay sesión activa.");
        if (!seguridad.getUsuarioActual().puedeEjecutarCRUD())
            throw new Exception("El usuario no tiene permisos para ejecutar CRUDs.");
    }

    private void validarAdmin(String accion) throws Exception {
        if (!seguridad.haySesionActiva())
            throw new Exception("No hay sesión activa.");
        if (!seguridad.getUsuarioActual().puedeCrearUsuarios())
            throw new Exception("Solo el Administrador puede " + accion + ".");
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

    /**
     * Crea un Partido. Si la lista de detalles trae los 2 equipos (LOCAL/VISITANTE)
     * los inserta en la misma transacción.
     */
    public void crearPartido(Partido p) throws Exception {
        validarCRUD();
        if (p.getDetalles() != null && p.getDetalles().size() == 2) {
            partidoDAO.insertarConDetalles(p);
        } else {
            partidoDAO.insertar(p);
        }
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

    // ===================== DETALLE PARTIDO =====================

    public void registrarDetallePartido(DetallePartido d) throws Exception {
        validarCRUD();
        detalleDAO.insertar(d);
    }

    public void actualizarDetallePartido(DetallePartido d) throws Exception {
        validarCRUD();
        detalleDAO.actualizar(d);
    }

    public void eliminarDetallePartido(int idPartido, int idEquipo) throws Exception {
        validarCRUD();
        detalleDAO.eliminar(idPartido, idEquipo);
    }

    public List<DetallePartido> listarDetallesPartido(int idPartido) throws Exception {
        return detalleDAO.listarPorPartido(idPartido);
    }

    // ===================== ASIGNACIÓN EQUIPO ↔ GRUPO =====================

    /**
     * Asigna un equipo a un grupo validando:
     *   - El grupo aún no tiene 4 equipos.
     *   - El equipo no está ya asignado a otro grupo.
     */
    public void asignarEquipoAGrupo(int idGrupo, int idEquipo) throws Exception {
        validarCRUD();
        if (equipoGrupoDAO.contarEquiposEnGrupo(idGrupo) >= 4)
            throw new Exception("El grupo ya tiene 4 equipos asignados.");
        if (equipoGrupoDAO.equipoTieneGrupo(idEquipo))
            throw new Exception("El equipo ya está asignado a un grupo.");
        equipoGrupoDAO.asignar(idGrupo, idEquipo);
    }

    public void removerEquipoDeGrupo(int idGrupo, int idEquipo) throws Exception {
        validarCRUD();
        equipoGrupoDAO.remover(idGrupo, idEquipo);
    }

    // ===================== USUARIOS (solo admin) =====================

    public List<Usuario> listarUsuarios() throws Exception {
        validarAdmin("ver todos los usuarios");
        return usuarioDAO.listarTodos();
    }

    public void actualizarUsuario(Usuario u) throws Exception {
        validarAdmin("actualizar usuarios");
        usuarioDAO.actualizar(u);
    }

    public void eliminarUsuario(int id) throws Exception {
        validarAdmin("eliminar usuarios");
        if (seguridad.getUsuarioActual().getIdUsuario() == id)
            throw new Exception("No puedes eliminar tu propio usuario en sesión.");
        usuarioDAO.eliminar(id);
    }

    public Optional<Usuario> buscarUsuario(int id) throws Exception {
        validarAdmin("buscar usuarios");
        return usuarioDAO.buscarPorId(id);
    }

    // ===================== PAÍSES =====================

    public void crearPais(Pais p) throws Exception {
        validarCRUD();
        paisDAO.insertar(p);
    }

    public void actualizarPais(Pais p) throws Exception {
        validarCRUD();
        paisDAO.actualizar(p);
    }

    public void eliminarPais(int id) throws Exception {
        validarCRUD();
        paisDAO.eliminar(id);
    }

    public Optional<Pais> buscarPais(int id) throws Exception {
        return paisDAO.buscarPorId(id);
    }

    public List<Pais> listarPaises()  throws Exception { return paisDAO.listarTodos(); }
    public List<Pais> listarSedes()   throws Exception { return paisDAO.listarSedes();  }

    // ===================== CONFEDERACIONES =====================

    public void crearConfederacion(Confederacion c) throws Exception {
        validarCRUD();
        confDAO.insertar(c);
    }

    public void actualizarConfederacion(Confederacion c) throws Exception {
        validarCRUD();
        confDAO.actualizar(c);
    }

    public void eliminarConfederacion(int id) throws Exception {
        validarCRUD();
        confDAO.eliminar(id);
    }

    public Optional<Confederacion> buscarConfederacion(int id) throws Exception {
        return confDAO.buscarPorId(id);
    }

    public List<Confederacion> listarConfederaciones() throws Exception {
        return confDAO.listarTodos();
    }

    // ===================== DIRECTORES TÉCNICOS =====================

    public void crearDirectorTecnico(DirectorTecnico dt) throws Exception {
        validarCRUD();
        dtDAO.insertar(dt);
    }

    public void actualizarDirectorTecnico(DirectorTecnico dt) throws Exception {
        validarCRUD();
        dtDAO.actualizar(dt);
    }

    public void eliminarDirectorTecnico(int id) throws Exception {
        validarCRUD();
        dtDAO.eliminar(id);
    }

    public Optional<DirectorTecnico> buscarDirectorTecnico(int id) throws Exception {
        return dtDAO.buscarPorId(id);
    }

    public List<DirectorTecnico> listarDirectoresTecnicos() throws Exception {
        return dtDAO.listarTodos();
    }

    // ===================== CIUDADES =====================

    public void crearCiudad(Ciudad c) throws Exception {
        validarCRUD();
        ciudadDAO.insertar(c);
    }

    public void actualizarCiudad(Ciudad c) throws Exception {
        validarCRUD();
        ciudadDAO.actualizar(c);
    }

    public void eliminarCiudad(int id) throws Exception {
        validarCRUD();
        ciudadDAO.eliminar(id);
    }

    public Optional<Ciudad> buscarCiudad(int id) throws Exception {
        return ciudadDAO.buscarPorId(id);
    }

    public List<Ciudad> listarCiudades()                  throws Exception { return ciudadDAO.listarTodos(); }
    public List<Ciudad> listarCiudadesPorPais(int idPais) throws Exception { return ciudadDAO.listarPorPais(idPais); }

    // ===================== ESTADIOS =====================

    public void crearEstadio(Estadio e) throws Exception {
        validarCRUD();
        estadioDAO.insertar(e);
    }

    public void actualizarEstadio(Estadio e) throws Exception {
        validarCRUD();
        estadioDAO.actualizar(e);
    }

    public void eliminarEstadio(int id) throws Exception {
        validarCRUD();
        estadioDAO.eliminar(id);
    }

    public Optional<Estadio> buscarEstadio(int id) throws Exception {
        return estadioDAO.buscarPorId(id);
    }

    public List<Estadio> listarEstadios() throws Exception {
        return estadioDAO.listarTodos();
    }

    // ===================== GRUPOS =====================

    public void crearGrupo(Grupo g) throws Exception {
        validarCRUD();
        grupoDAO.insertar(g);
    }

    public void actualizarGrupo(Grupo g) throws Exception {
        validarCRUD();
        grupoDAO.actualizar(g);
    }

    public void eliminarGrupo(int id) throws Exception {
        validarCRUD();
        grupoDAO.eliminar(id);
    }

    public Optional<Grupo> buscarGrupo(int id) throws Exception {
        return grupoDAO.buscarPorId(id);
    }

    public List<Grupo> listarGrupos() throws Exception {
        return grupoDAO.listarTodos();
    }

    // ===================== POSICIONES =====================

    public void crearPosicion(Posicion p) throws Exception {
        validarCRUD();
        posicionDAO.insertar(p);
    }

    public void actualizarPosicion(Posicion p) throws Exception {
        validarCRUD();
        posicionDAO.actualizar(p);
    }

    public void eliminarPosicion(int id) throws Exception {
        validarCRUD();
        posicionDAO.eliminar(id);
    }

    public Optional<Posicion> buscarPosicion(int id) throws Exception {
        return posicionDAO.buscarPorId(id);
    }

    public List<Posicion> listarPosiciones() throws Exception {
        return posicionDAO.listarTodos();
    }

    // ===================== BITÁCORA (solo admin) =====================

    public List<Bitacora> listarBitacoraPorRango(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        validarAdmin("consultar la bitácora");
        return bitacoraDAO.listarPorRango(desde, hasta);
    }

    public List<Bitacora> listarBitacoraPorUsuario(int idUsuario) throws Exception {
        validarAdmin("consultar la bitácora");
        return bitacoraDAO.listarPorUsuario(idUsuario);
    }

    // ===================== REPORTES (datos crudos) =====================

    public List<Object[]> valorTotalPorEquipoYConfederacion(int idConfederacion) throws Exception {
        return grupoDAO.valorTotalJugadoresPorEquipoYConfederacion(idConfederacion);
    }

    public List<Object[]> equiposPorPaisAnfitrion() throws Exception {
        return grupoDAO.equiposPorPaisAnfitrion();
    }
}
