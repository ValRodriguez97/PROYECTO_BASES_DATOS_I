package co.edu.uniquindio.Utils;

import co.edu.uniquindio.dao.IBitacoraDAO;
import co.edu.uniquindio.dao.Impl.BitacoraDAOImpl;
import co.edu.uniquindio.dao.Impl.GrupoDAOImpl;
import co.edu.uniquindio.dao.Impl.JugadorDAOImpl;
import co.edu.uniquindio.dao.Impl.UsuarioDAOImpl;
import co.edu.uniquindio.model.Bitacora;
import co.edu.uniquindio.model.Jugador;
import co.edu.uniquindio.model.Usuario;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Genera los 4 reportes PDF exigidos por el enunciado usando Apache PDFBox.
 *
 * Todos los PDFs se guardan en la carpeta "reportes/" relativa al directorio
 * desde donde se ejecuta la aplicación.
 */
public class GeneradorReportes {

    private static final String DIR_REPORTES = "reportes";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── FUENTES ──────────────────────────────────────────────────────────────
    private static PDType1Font fuente(boolean bold) {
        return new PDType1Font(bold
                ? Standard14Fonts.FontName.HELVETICA_BOLD
                : Standard14Fonts.FontName.HELVETICA);
    }

    // ── UTILIDADES PDF ───────────────────────────────────────────────────────

    private static File asegurarDirectorio() {
        File dir = new File(DIR_REPORTES);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    /**
     * Escribe texto en la posición dada y devuelve la nueva posición Y.
     * Si no cabe en la página actual, crea una nueva.
     */
    private static float escribirLinea(PDPageContentStream cs,
                                       String texto,
                                       float x, float y,
                                       float fontSize,
                                       boolean bold) throws IOException {
        cs.beginText();
        cs.setFont(fuente(bold), fontSize);
        cs.newLineAtOffset(x, y);
        // PDFBox no admite caracteres fuera del charset estándar sin TTF;
        // reemplazamos tildes para evitar errores en PDType1Font
        cs.showText(normalizarTexto(texto));
        cs.endText();
        return y - (fontSize + 4);
    }

    /** Quita tildes y caracteres no soportados por PDType1Font (Helvetica). */
    private static String normalizarTexto(String s) {
        if (s == null) return "";
        return s
            // tildes y ñ
            .replace('á','a').replace('é','e').replace('í','i')
            .replace('ó','o').replace('ú','u').replace('ü','u')
            .replace('Á','A').replace('É','E').replace('Í','I')
            .replace('Ó','O').replace('Ú','U').replace('ñ','n')
            .replace('Ñ','N')
            // caracteres de tabla/box-drawing (U+2500 a U+257F)
            .replaceAll("[\\u2500-\\u257F]", "-")
            // otros símbolos frecuentes fuera de WinAnsiEncoding
            .replaceAll("[\\u2018\\u2019\\u201C\\u201D]", "'")
            .replaceAll("[\\u2013\\u2014]", "-")
            .replaceAll("[\\u2026]", "...")
            // cualquier carácter fuera del rango imprimible Latin-1 (0x20–0xFF)
            .replaceAll("[^\\x20-\\xFF]", "?");
    }

    private static void escribirTitulo(PDPageContentStream cs,
                                       String titulo,
                                       float pageWidth) throws IOException {
        cs.beginText();
        cs.setFont(fuente(true), 16);
        float w = fuente(true).getStringWidth(normalizarTexto(titulo)) / 1000 * 16;
        cs.newLineAtOffset((pageWidth - w) / 2, 780);
        cs.showText(normalizarTexto(titulo));
        cs.endText();

        cs.beginText();
        cs.setFont(fuente(false), 9);
        cs.newLineAtOffset(50, 760);
        cs.showText("Generado: " + LocalDateTime.now().format(FMT));
        cs.endText();
    }

    private static PDPageContentStream nuevaPagina(PDDocument doc) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);
        return new PDPageContentStream(doc, page);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REPORTE b1 — Usuarios que ingresaron/salieron en un rango de fecha/hora
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @param desde  fecha-hora inicio del rango
     * @param hasta  fecha-hora fin del rango
     * @return ruta absoluta del PDF generado
     */
    public static String reporteBitacoraPorRango(LocalDateTime desde, LocalDateTime hasta)
            throws Exception {

        IBitacoraDAO bitacoraDAO = new BitacoraDAOImpl();
        UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

        List<Bitacora> registros = bitacoraDAO.listarPorRango(desde, hasta);

        File dir = asegurarDirectorio();
        String nombre = "reporte_bitacora_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        File archivo = new File(dir, nombre);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float w = page.getMediaBox().getWidth();

            escribirTitulo(cs, "Reporte de Bitacora de Usuarios", w);

            // Cabecera de columnas
            float y = 730;
            cs.setFont(fuente(true), 10);
            cs.beginText(); cs.newLineAtOffset(50, y);
            cs.showText("Usuario"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(200, y);
            cs.showText("Ingreso"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(340, y);
            cs.showText("Salida"); cs.endText();
            y -= 15;

            // Línea separadora
            cs.moveTo(50, y + 5); cs.lineTo(w - 50, y + 5); cs.stroke();
            y -= 5;

            cs.setFont(fuente(false), 9);
            for (Bitacora b : registros) {
                if (y < 60) {
                    cs.close();
                    cs = nuevaPagina(doc);
                    y = 750;
                }

                Optional<Usuario> uOpt = usuarioDAO.buscarPorId(b.getIdUsuario());
                String nombreUser = uOpt.map(Usuario::getNombreUsuario)
                        .orElse("ID:" + b.getIdUsuario());

                cs.beginText(); cs.newLineAtOffset(50, y);
                cs.showText(normalizarTexto(nombreUser)); cs.endText();

                cs.beginText(); cs.newLineAtOffset(200, y);
                cs.showText(b.getFechaHoraIngreso().format(FMT)); cs.endText();

                cs.beginText(); cs.newLineAtOffset(340, y);
                String salida = b.getFechaHoraSalida() != null
                        ? b.getFechaHoraSalida().format(FMT) : "En sesion";
                cs.showText(salida); cs.endText();

                y -= 14;
            }

            if (registros.isEmpty()) {
                cs.beginText(); cs.setFont(fuente(false), 10);
                cs.newLineAtOffset(50, y);
                cs.showText("Sin registros en el rango indicado."); cs.endText();
            }

            cs.close();
            doc.save(archivo);
        }

        return archivo.getAbsolutePath();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REPORTE b2 — Jugadores filtrados por peso, estatura y equipo
    // ─────────────────────────────────────────────────────────────────────────

    public static String reporteJugadoresPorFiltros(
            BigDecimal pesoMin, BigDecimal pesoMax,
            BigDecimal estaturaMin, BigDecimal estaturaMax,
            int idEquipo) throws Exception {

        JugadorDAOImpl jugadorDAO = new JugadorDAOImpl();
        List<Jugador> jugadores = jugadorDAO.filtrarPorPesoEstaturaEquipo(
                pesoMin, pesoMax, estaturaMin, estaturaMax, idEquipo);

        File dir = asegurarDirectorio();
        String nombre = "reporte_jugadores_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        File archivo = new File(dir, nombre);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float w = page.getMediaBox().getWidth();

            escribirTitulo(cs, "Reporte de Jugadores por Peso, Estatura y Equipo", w);

            // Filtros aplicados
            cs.beginText(); cs.setFont(fuente(false), 8); cs.newLineAtOffset(50, 748);
            cs.showText(String.format("Filtros: Peso [%.1f - %.1f kg]  Estatura [%.2f - %.2f m]",
                    pesoMin, pesoMax, estaturaMin, estaturaMax));
            cs.endText();

            float y = 728;
            // Cabeceras
            cs.setFont(fuente(true), 9);
            String[] cab = {"Jugador", "Equipo", "Posicion", "Peso(kg)", "Estatura(m)", "Edad", "Valor(M€)"};
            float[] posX = {50, 180, 290, 365, 415, 465, 505};
            for (int i = 0; i < cab.length; i++) {
                cs.beginText(); cs.newLineAtOffset(posX[i], y);
                cs.showText(cab[i]); cs.endText();
            }
            y -= 12;
            cs.moveTo(50, y + 4); cs.lineTo(w - 30, y + 4); cs.stroke();
            y -= 5;
            cs.setFont(fuente(false), 8);

            for (Jugador j : jugadores) {
                if (y < 60) {
                    cs.close();
                    cs = nuevaPagina(doc);
                    y = 750;
                    cs.setFont(fuente(false), 8);
                }

                String[] vals = {
                    normalizarTexto(j.getNombre() + " " + j.getApellido()),
                    normalizarTexto(j.getEquipo().getNombre()),
                    normalizarTexto(j.getPosicion().getNombre()),
                    j.getPeso().toPlainString(),
                    j.getEstatura().toPlainString(),
                    String.valueOf(j.getEdad()),
                    j.getValorMercado().toPlainString()
                };
                for (int i = 0; i < vals.length; i++) {
                    cs.beginText(); cs.newLineAtOffset(posX[i], y);
                    cs.showText(vals[i]); cs.endText();
                }
                y -= 12;
            }

            if (jugadores.isEmpty()) {
                cs.beginText(); cs.setFont(fuente(false), 10);
                cs.newLineAtOffset(50, y);
                cs.showText("Sin jugadores con esos filtros."); cs.endText();
            }

            cs.close();
            doc.save(archivo);
        }

        return archivo.getAbsolutePath();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REPORTE b3 — Valor total de jugadores por equipo y confederación
    // ─────────────────────────────────────────────────────────────────────────

    public static String reporteValorTotalPorEquipoYConfederacion(
            int idConfederacion, String nombreConfederacion) throws Exception {

        GrupoDAOImpl grupoDAO = new GrupoDAOImpl();
        List<Object[]> datos = grupoDAO.valorTotalJugadoresPorEquipoYConfederacion(idConfederacion);

        File dir = asegurarDirectorio();
        String nombre = "reporte_valor_confederacion_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        File archivo = new File(dir, nombre);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float w = page.getMediaBox().getWidth();

            escribirTitulo(cs, "Valor Total de Jugadores por Equipo", w);

            cs.beginText(); cs.setFont(fuente(false), 8); cs.newLineAtOffset(50, 748);
            cs.showText("Confederacion: " + normalizarTexto(nombreConfederacion));
            cs.endText();

            float y = 725;
            cs.setFont(fuente(true), 10);
            cs.beginText(); cs.newLineAtOffset(50, y); cs.showText("Equipo"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(320, y); cs.showText("Valor Total (M€)"); cs.endText();
            y -= 12;
            cs.moveTo(50, y + 4); cs.lineTo(w - 50, y + 4); cs.stroke();
            y -= 5;

            BigDecimal grandTotal = BigDecimal.ZERO;
            cs.setFont(fuente(false), 9);
            for (Object[] fila : datos) {
                if (y < 60) {
                    cs.close();
                    cs = nuevaPagina(doc);
                    y = 750;
                    cs.setFont(fuente(false), 9);
                }
                String equipo = normalizarTexto((String) fila[0]);
                BigDecimal valor = (BigDecimal) fila[2];
                grandTotal = grandTotal.add(valor);

                cs.beginText(); cs.newLineAtOffset(50, y); cs.showText(equipo); cs.endText();
                cs.beginText(); cs.newLineAtOffset(320, y);
                cs.showText(String.format("%,.2f", valor)); cs.endText();
                y -= 13;
            }

            // Totalizador
            y -= 5;
            cs.moveTo(50, y + 8); cs.lineTo(w - 50, y + 8); cs.stroke();
            cs.setFont(fuente(true), 10);
            cs.beginText(); cs.newLineAtOffset(50, y - 4); cs.showText("TOTAL CONFEDERACION"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(320, y - 4);
            cs.showText(String.format("%,.2f", grandTotal)); cs.endText();

            if (datos.isEmpty()) {
                cs.beginText(); cs.setFont(fuente(false), 10);
                cs.newLineAtOffset(50, y - 4);
                cs.showText("Sin datos para esta confederacion."); cs.endText();
            }

            cs.close();
            doc.save(archivo);
        }

        return archivo.getAbsolutePath();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REPORTE b4 — Equipos que jugarán en cada país anfitrión
    // ─────────────────────────────────────────────────────────────────────────

    public static String reporteEquiposPorPaisAnfitrion() throws Exception {

        GrupoDAOImpl grupoDAO = new GrupoDAOImpl();
        List<Object[]> datos = grupoDAO.equiposPorPaisAnfitrion();

        File dir = asegurarDirectorio();
        String nombre = "reporte_equipos_anfitrion_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        File archivo = new File(dir, nombre);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float w = page.getMediaBox().getWidth();

            escribirTitulo(cs, "Equipos que juegan en cada pais anfitrion", w);

            float y = 730;
            cs.setFont(fuente(true), 10);
            cs.beginText(); cs.newLineAtOffset(50, y); cs.showText("Pais Sede"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(200, y); cs.showText("Equipo"); cs.endText();
            cs.beginText(); cs.newLineAtOffset(380, y); cs.showText("Pais del Equipo"); cs.endText();
            y -= 12;
            cs.moveTo(50, y + 4); cs.lineTo(w - 50, y + 4); cs.stroke();
            y -= 5;

            String paisActual = "";
            cs.setFont(fuente(false), 9);
            for (Object[] fila : datos) {
                if (y < 60) {
                    cs.close();
                    cs = nuevaPagina(doc);
                    y = 750;
                    cs.setFont(fuente(false), 9);
                }

                String sede = normalizarTexto((String) fila[0]);
                String equipo = normalizarTexto((String) fila[1]);
                String paisEq = normalizarTexto((String) fila[2]);

                // Separador visual al cambiar de sede
                if (!sede.equals(paisActual)) {
                    if (!paisActual.isEmpty()) { y -= 5; }
                    cs.setFont(fuente(true), 10);
                    cs.beginText(); cs.newLineAtOffset(50, y); cs.showText(sede); cs.endText();
                    cs.setFont(fuente(false), 9);
                    paisActual = sede;
                    y -= 14;
                }

                cs.beginText(); cs.newLineAtOffset(200, y); cs.showText(equipo); cs.endText();
                cs.beginText(); cs.newLineAtOffset(380, y); cs.showText(paisEq); cs.endText();
                y -= 13;
            }

            if (datos.isEmpty()) {
                cs.beginText(); cs.setFont(fuente(false), 10);
                cs.newLineAtOffset(50, y);
                cs.showText("Sin datos. Verifica que los partidos esten registrados."); cs.endText();
            }

            cs.close();
            doc.save(archivo);
        }

        return archivo.getAbsolutePath();
    }
}