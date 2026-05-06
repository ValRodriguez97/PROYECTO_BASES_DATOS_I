package co.edu.uniquindio;

import co.edu.uniquindio.model.Administrador;
import co.edu.uniquindio.model.UsuarioEsporadico;
import co.edu.uniquindio.model.UsuarioTradicional;
import co.edu.uniquindio.services.SistemaSeguridadService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del backend que NO requieren conexión a la base de datos.
 * Validan reglas de seguridad y permisos por tipo de usuario.
 */
class AppTest {

    @Test
    void hashContrasenaEsDeterministicoYHexadecimal() {
        SistemaSeguridadService s = new SistemaSeguridadService();
        String h1 = s.hashContrasena("Admin2026!");
        String h2 = s.hashContrasena("Admin2026!");
        assertEquals(h1, h2, "El mismo input debe producir el mismo hash");
        assertEquals(64, h1.length(), "SHA-256 hex debe tener 64 caracteres");
        assertTrue(h1.matches("[0-9a-f]{64}"));
    }

    @Test
    void permisosPorTipoDeUsuario() {
        Administrador admin    = new Administrador(1, "admin",  "x");
        UsuarioTradicional trad = new UsuarioTradicional(2, "user",  "x");
        UsuarioEsporadico esp   = new UsuarioEsporadico(3, "guest", "x");

        // Solo el admin crea usuarios
        assertTrue (admin.puedeCrearUsuarios());
        assertFalse(trad.puedeCrearUsuarios());
        assertFalse(esp.puedeCrearUsuarios());

        // Admin y tradicional pueden CRUD; esporádico no
        assertTrue (admin.puedeEjecutarCRUD());
        assertTrue (trad.puedeEjecutarCRUD());
        assertFalse(esp.puedeEjecutarCRUD());

        // Todos pueden ejecutar consultas
        assertTrue(admin.puedeEjecutarConsultas());
        assertTrue(trad.puedeEjecutarConsultas());
        assertTrue(esp.puedeEjecutarConsultas());
    }
}
