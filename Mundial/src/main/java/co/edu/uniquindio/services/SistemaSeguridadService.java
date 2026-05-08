package co.edu.uniquindio.services;

import co.edu.uniquindio.dao.*;
import co.edu.uniquindio.dao.Impl.*;
import co.edu.uniquindio.model.Enum.TipoUsuario;
import co.edu.uniquindio.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

public class SistemaSeguridadService {

    private final IUsuarioDAO usuarioDAO  = new UsuarioDAOImpl();
    private final IBitacoraDAO bitacoraDAO = new BitacoraDAOImpl();

    private Usuario usuarioActual;
    private int     idBitacoraActual = -1;

    public String hashContrasena(String contrasena) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public Usuario login(String nombreUsuario, String contrasena) throws Exception {
        Optional<Usuario> opt = usuarioDAO.buscarPorNombre(nombreUsuario);
        if (opt.isEmpty()) {
            throw new Exception("Usuario no encontrado.");
        }
        Usuario u = opt.get();
        String hashIngresado = hashContrasena(contrasena);
        if (!hashIngresado.equals(u.getContraseña())) {
            throw new Exception("Contraseña incorrecta.");
        }
        LocalDateTime ahora = LocalDateTime.now();
        idBitacoraActual = bitacoraDAO.registrarIngreso(u.getIdUsuario(), ahora);
        usuarioActual    = u;
        return u;
    }

    public void logout() {
        if (idBitacoraActual != -1) {
            try {
                bitacoraDAO.registrarSalida(idBitacoraActual, LocalDateTime.now());
            } catch (Exception e) {
                System.err.println("Error al registrar salida en bitácora: " + e.getMessage());
            }
        }
        usuarioActual    = null;
        idBitacoraActual = -1;
    }

    public void crearUsuario(String nombreUsuario, String contrasena,
                             TipoUsuario tipo) throws Exception {
        verificarPermiso("crear usuarios", usuarioActual.puedeCrearUsuarios());

        if (tipo == TipoUsuario.ADMINISTRADOR) {
            throw new Exception("Solo puede existir un Administrador en el sistema.");
        }

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new Exception("El nombre de usuario no puede estar vacío.");
        }

        if (contrasena == null || contrasena.length() < 4) {
            throw new Exception("La contraseña debe tener al menos 4 caracteres.");
        }

        String hash = hashContrasena(contrasena);
        Usuario nuevo = switch (tipo) {
            case TRADICIONAL -> new UsuarioTradicional(0, nombreUsuario, hash);
            default          -> new UsuarioEsporadico(0, nombreUsuario, hash);
        };
        usuarioDAO.insertar(nuevo);
    }

    /**
     * Cambia la contraseña de un usuario. Solo lo permite si:
     *   - hay sesión activa, Y
     *   - el usuario en sesión es Administrador, O
     *   - el usuario en sesión es el dueño de la cuenta (cambio propio).
     */
    public void cambiarContrasena(int idUsuario, String nuevaContrasena) throws Exception {
        if (usuarioActual == null) throw new Exception("No hay sesión activa.");
        boolean esAdmin   = usuarioActual.puedeCrearUsuarios();
        boolean esElMismo = usuarioActual.getIdUsuario() == idUsuario;
        if (!esAdmin && !esElMismo) {
            throw new Exception("Solo el Administrador o el dueño de la cuenta puede cambiar la contraseña.");
        }
        if (nuevaContrasena == null || nuevaContrasena.length() < 4) {
            throw new Exception("La nueva contraseña debe tener al menos 4 caracteres.");
        }
        Optional<Usuario> opt = usuarioDAO.buscarPorId(idUsuario);
        if (opt.isEmpty()) throw new Exception("Usuario no encontrado.");
        Usuario u = opt.get();
        u.setContraseña(hashContrasena(nuevaContrasena));
        usuarioDAO.actualizar(u);
    }

    private void verificarPermiso(String accion, boolean tienePermiso) throws Exception {
        if (usuarioActual == null) throw new Exception("No hay sesión activa.");
        if (!tienePermiso)         throw new Exception("Sin permiso para: " + accion);
    }

    public Usuario getUsuarioActual()    { return usuarioActual; }
    public boolean haySesionActiva()     { return usuarioActual != null; }


}
