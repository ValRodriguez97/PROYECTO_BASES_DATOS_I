package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final GestionDatosService gestion;
    private final MundialService mundial;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[][] REPORTS = {
        {"B1", "📋", "Bitácora por Rango de Fecha",
            "Exporta todos los registros de ingreso/salida en un rango de fechas."},
        {"B2", "👤", "Jugadores por Peso, Estatura y Equipo",
            "Filtra jugadores por rangos de peso y estatura, con opción de equipo específico."},
        {"B3", "💰", "Valor Total por Confederación",
            "Valor total de mercado de jugadores por equipo, agrupado por confederación."},
        {"B4", "🌎", "Equipos por País Anfitrión",
            "Lista los equipos que jugarán en cada país sede (México, EE.UU., Canadá)."},
    };

    private static final Color[] ACCENTS = {
        UIColors.PURPLE, UIColors.BLUE, UIColors.TURQUOISE, UIColors.MAGENTA
    };

    public ReportsPanel(GestionDatosService gestion, MundialService mundial) {
        this.gestion = gestion;
        this.mundial = mundial;
        setBackground(UIColors.BG_PAGE);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JScrollPane scroll = UIFactory.scrollPane(buildContent());
        scroll.setBackground(UIColors.BG_PAGE);
        scroll.getViewport().setBackground(UIColors.BG_PAGE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(UIColors.BG_PAGE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel header = buildHeader();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(22));

        for (int i = 0; i < 4; i++) {
            JPanel card = buildReportCard(i);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(card);
            if (i < 3) content.add(Box.createVerticalStrut(16));
        }

        return content;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(UIFactory.heading("Reportes PDF"));
        left.add(Box.createVerticalStrut(4));
        left.add(UIFactory.subheading("Genera los 4 reportes requeridos en formato PDF"));

        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel buildReportCard(int index) {
        Color accent = ACCENTS[index];
        String[] r = REPORTS[index];

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(10, UIColors.BORDER));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header del card
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
                  g2.fillRect(0, 0, getWidth(), getHeight());
                  g2.dispose();
                  super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER));

        JPanel iconBox = new JPanel() {
            { setPreferredSize(new Dimension(40, 40)); setOpaque(false); }
            @Override
            protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(accent);
                  g2.fill(new RoundRectangle2D.Float(0, 0, 40, 40, 10, 10));
                  g2.setColor(Color.WHITE);
                  g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
                  FontMetrics fm = g2.getFontMetrics();
                  g2.drawString(r[1],
                        (40 - fm.stringWidth(r[1])) / 2,
                        (40 + fm.getAscent() - fm.getDescent()) / 2);
                  g2.dispose();
            }
        };

        JPanel labelCol = new JPanel();
        labelCol.setOpaque(false);
        labelCol.setLayout(new BoxLayout(labelCol, BoxLayout.Y_AXIS));
        JLabel tag = UIFactory.badge(r[0],
                  new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25), accent);
        JLabel title = new JLabel(r[2]);
        title.setFont(UIFonts.HEADING_SM);
        title.setForeground(UIColors.TEXT_PRIMARY);
        labelCol.add(tag);
        labelCol.add(Box.createVerticalStrut(4));
        labelCol.add(title);

        header.add(iconBox);
        header.add(labelCol);
        card.add(header, BorderLayout.NORTH);

        // Body: descripción a la izquierda, parámetros+botón a la derecha
        JPanel body = new JPanel(new BorderLayout(24, 0));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel desc = new JLabel(
                  "<html><body style='width:260px'>" + r[3] + "</body></html>");
        desc.setFont(UIFonts.BODY_SM);
        desc.setForeground(UIColors.TEXT_SECONDARY);
        desc.setVerticalAlignment(SwingConstants.TOP);

        JPanel params = switch (index) {
            case 0  -> buildB1Params(accent);
            case 1  -> buildB2Params(accent);
            case 2  -> buildB3Params(accent);
            default -> buildB4Params(accent);
        };

        body.add(desc,   BorderLayout.WEST);
        body.add(params, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);
        return card;
   }

    private JPanel buildB1Params(Color accent) {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setOpaque(false);

        JTextField desdeF = UIFactory.textField(LocalDate.now().minusDays(7).format(FMT));
        JTextField hastaF = UIFactory.textField(LocalDate.now().format(FMT));

        p.add(UIFactory.formLabel("Desde (yyyy-MM-dd)")); p.add(desdeF);
        p.add(UIFactory.formLabel("Hasta (yyyy-MM-dd)")); p.add(hastaF);

        JButton btn = accentButton("Generar PDF", accent);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(e -> {
            btn.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    LocalDateTime desde = LocalDate.parse(desdeF.getText().trim(), FMT).atStartOfDay();
                    LocalDateTime hasta = LocalDate.parse(hastaF.getText().trim(), FMT).atTime(LocalTime.MAX);
                    return mundial.generarReporteBitacora(desde, hasta);
                }
                @Override
                protected void done() {
                    btn.setEnabled(true);
                    try { showPdfPreview(get()); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.add(p);
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(btn);
        return wrap;
    }

    private JPanel buildB2Params(Color accent) {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setOpaque(false);

        JTextField pesoMinF = UIFactory.textField("50");
        JTextField pesoMaxF = UIFactory.textField("100");
        JTextField estMinF  = UIFactory.textField("1.60");
        JTextField estMaxF  = UIFactory.textField("2.10");

        List<Equipo> equipos = List.of();
        try { equipos = gestion.listarEquipos(); } catch (Exception ignored) {}

        String[] noms = new String[equipos.size() + 1];
        noms[0] = "Todos los equipos";
        for (int i = 0; i < equipos.size(); i++) noms[i + 1] = equipos.get(i).getNombre();
        JComboBox<String> equipoBox = UIFactory.comboBox(noms);
        final List<Equipo> eqFinal = equipos;

        p.add(UIFactory.formLabel("Peso min (kg)"));    p.add(pesoMinF);
        p.add(UIFactory.formLabel("Peso max (kg)"));    p.add(pesoMaxF);
        p.add(UIFactory.formLabel("Estatura min (m)")); p.add(estMinF);
        p.add(UIFactory.formLabel("Estatura max (m)")); p.add(estMaxF);
        p.add(UIFactory.formLabel("Equipo"));           p.add(equipoBox);

        JButton btn = accentButton("Generar PDF", accent);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(e -> {
            btn.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    BigDecimal pMin = new BigDecimal(pesoMinF.getText().trim());
                    BigDecimal pMax = new BigDecimal(pesoMaxF.getText().trim());
                    BigDecimal eMin = new BigDecimal(estMinF.getText().trim());
                    BigDecimal eMax = new BigDecimal(estMaxF.getText().trim());
                    int sel  = equipoBox.getSelectedIndex();
                    int idEq = (sel <= 0 || sel > eqFinal.size())
                            ? 0 : eqFinal.get(sel - 1).getIdEquipo();
                    return mundial.generarReporteJugadoresFiltrados(pMin, pMax, eMin, eMax, idEq);
                }
                @Override
                protected void done() {
                    btn.setEnabled(true);
                    try { showPdfPreview(get()); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.add(p);
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(btn);
        return wrap;
    }

    private JPanel buildB3Params(Color accent) {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setOpaque(false);

        List<co.edu.uniquindio.model.Confederacion> confs = List.of();
        try { confs = gestion.listarConfederaciones(); } catch (Exception ignored) {}

        String[] noms = confs.isEmpty()
                ? new String[]{"UEFA", "CONMEBOL", "CONCACAF", "CAF", "AFC", "OFC"}
                : confs.stream()
                        .map(co.edu.uniquindio.model.Confederacion::getNombre)
                        .toArray(String[]::new);
        JComboBox<String> confBox = UIFactory.comboBox(noms);
        final List<co.edu.uniquindio.model.Confederacion> confsFinal = confs;

        p.add(UIFactory.formLabel("Confederacion")); p.add(confBox);

        JButton btn = accentButton("Generar PDF", accent);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(e -> {
            btn.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    int sel    = confBox.getSelectedIndex();
                    int idConf = 1;
                    String nombre = confBox.getSelectedItem().toString();
                    if (!confsFinal.isEmpty() && sel >= 0 && sel < confsFinal.size()) {
                        idConf = confsFinal.get(sel).getIdConfederacion();
                        nombre = confsFinal.get(sel).getNombre();
                    }
                    return mundial.generarReporteValorPorConfederacion(idConf, nombre);
                }
                @Override
                protected void done() {
                    btn.setEnabled(true);
                    try { showPdfPreview(get()); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.add(p);
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(btn);
        return wrap;
    }

    private JPanel buildB4Params(Color accent) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JLabel info = new JLabel(
                "<html>No requiere parametros adicionales.<br>" +
                "Genera el listado completo de equipos por cada pais sede.</html>");
        info.setFont(UIFonts.BODY_SM);
        info.setForeground(UIColors.TEXT_MUTED);
        info.setAlignmentX(LEFT_ALIGNMENT);
        wrap.add(info);
        wrap.add(Box.createVerticalStrut(10));

        JButton btn = accentButton("Generar PDF", accent);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(e -> {
            btn.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return mundial.generarReporteEquiposPorAnfitrion();
                }
                @Override
                protected void done() {
                    btn.setEnabled(true);
                    try { showPdfPreview(get()); }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        wrap.add(btn);
        return wrap;
    }

    private void showPdfPreview(String pdfPath) {
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "El PDF no existe en la ruta:\n" + pdfPath,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PDDocument doc;
        try {
            doc = Loader.loadPDF(pdfFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final PDFRenderer renderer   = new PDFRenderer(doc);
        final int         totalPages = doc.getNumberOfPages();
        final int[]       current    = {0};

        // ── Ventana principal del visor ──
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Vista previa — " + pdfFile.getName(),
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(860, 920);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                try { doc.close(); } catch (Exception ignored) {}
            }
        });

        // ── Barra superior con ruta ──
        JLabel pathLabel = new JLabel(" " + pdfFile.getAbsolutePath());
        pathLabel.setFont(UIFonts.BODY_SM);
        pathLabel.setForeground(UIColors.TEXT_SECONDARY);
        pathLabel.setOpaque(true);
        pathLabel.setBackground(new Color(0xF8F9FC));
        pathLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        dlg.add(pathLabel, BorderLayout.NORTH);

        // ── Área de imagen con scroll SOLO vertical ──
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.TOP);
        imageLabel.setBackground(new Color(0xDDDDDD));
        imageLabel.setOpaque(true);

        // Panel contenedor que centra la imagen horizontalmente
        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        imageContainer.setBackground(new Color(0xDDDDDD));
        imageContainer.add(imageLabel);

        JScrollPane scroll = new JScrollPane(imageContainer);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        dlg.add(scroll, BorderLayout.CENTER);

        // ── Barra de navegación inferior ──
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        nav.setBackground(new Color(0xF8F9FC));
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

        JButton prev         = UIFactory.outlineButton("◀ Anterior");
        JButton next         = UIFactory.outlineButton("Siguiente ▶");
        JLabel  pageInfo     = new JLabel();
        pageInfo.setFont(UIFonts.LABEL_BOLD);
        pageInfo.setForeground(UIColors.TEXT_PRIMARY);

        JButton openExternal = UIFactory.tealButton("Abrir en visor externo");

        prev.setEnabled(false);
        next.setEnabled(totalPages > 1);

        // ── Renderizado de página con SwingWorker ──
        Runnable renderPage = () -> {
            prev.setEnabled(false);
            next.setEnabled(false);
            pageInfo.setText("Cargando...");
            imageLabel.setIcon(null);
            imageLabel.setText("Renderizando página " + (current[0] + 1) + "...");

            new SwingWorker<BufferedImage, Void>() {
                @Override
                protected BufferedImage doInBackground() throws Exception {
                    // Calcular DPI para que la página quepa en el ancho del diálogo
                    // sin scroll horizontal (ancho útil ≈ 820px, página carta = 8.5in)
                    float dpi = (dlg.getWidth() - 40) / 8.5f;
                    if (dpi < 80)  dpi = 80;
                    if (dpi > 150) dpi = 150;
                    return renderer.renderImageWithDPI(current[0], dpi);
                }
                @Override
                protected void done() {
                    try {
                        BufferedImage img = get();
                        imageLabel.setIcon(new ImageIcon(img));
                        imageLabel.setText(null);
                        pageInfo.setText("Página " + (current[0] + 1) + " / " + totalPages);
                        prev.setEnabled(current[0] > 0);
                        next.setEnabled(current[0] < totalPages - 1);
                        scroll.getVerticalScrollBar().setValue(0);
                        imageContainer.revalidate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        imageLabel.setIcon(null);
                        imageLabel.setText("Error al renderizar: " + ex.getMessage());
                    }
                }
            }.execute();
        };

        prev.addActionListener(e -> {
            if (current[0] > 0) { current[0]--; renderPage.run(); }
        });
        next.addActionListener(e -> {
            if (current[0] < totalPages - 1) { current[0]++; renderPage.run(); }
        });

        openExternal.addActionListener(e -> {
            try {
                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    JOptionPane.showMessageDialog(dlg,
                            "Abre el archivo manualmente en:\n" + pdfFile.getAbsolutePath(),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg,
                        "No se pudo abrir el visor externo:\n" + ex.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        nav.add(prev);
        nav.add(pageInfo);
        nav.add(next);
        nav.add(Box.createHorizontalStrut(24));
        nav.add(openExternal);
        dlg.add(nav, BorderLayout.SOUTH);

        // Renderizar primera página al abrir
        renderPage.run();
        dlg.setVisible(true);
    }

    private JButton accentButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIFonts.BUTTON_MD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(accent.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(accent); }
        });
        return btn;
    }
}