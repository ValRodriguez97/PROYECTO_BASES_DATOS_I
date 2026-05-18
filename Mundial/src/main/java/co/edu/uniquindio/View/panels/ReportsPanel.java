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
            add(scroll, BorderLayout.CENTER);
      }

      private JPanel buildContent() {
            JPanel content = new JPanel();
            content.setBackground(UIColors.BG_PAGE);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

            content.add(buildHeader());
            content.add(Box.createVerticalStrut(22));

            JPanel grid = new JPanel(new GridLayout(2, 2, 18, 18));
            grid.setOpaque(false);
            // FIX: la altura anterior (620) cortaba el card B2 que tiene 5 filas de
            // parámetros + botón. Se sube a 820 para que el botón "Generar PDF" sea
            // visible en todos los cards.
            grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 820));
            for (int i = 0; i < 4; i++) grid.add(buildReportCard(i));
            content.add(grid);

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

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(10, UIColors.BORDER));

            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12)) {
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.dispose();
                        super.paintComponent(g);
                  }
            };
            header.setOpaque(false);
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER));

            // Ícono circular
            JPanel iconBox = new JPanel() {
                  { setPreferredSize(new Dimension(40, 40)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(accent);
                        g2.fill(new RoundRectangle2D.Float(0, 0, 40, 40, 10, 10));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(r[1], (40 - fm.stringWidth(r[1])) / 2, (40 + fm.getAscent() - fm.getDescent()) / 2);
                        g2.dispose();
                  }
            };

            JPanel labelCol = new JPanel();
            labelCol.setOpaque(false);
            labelCol.setLayout(new BoxLayout(labelCol, BoxLayout.Y_AXIS));
            JLabel tag   = UIFactory.badge(r[0], new Color(
                  accent.getRed(), accent.getGreen(), accent.getBlue(), 25), accent);
            JLabel title = new JLabel(r[2]);
            title.setFont(UIFonts.HEADING_SM);
            title.setForeground(UIColors.TEXT_PRIMARY);
            labelCol.add(tag);
            labelCol.add(Box.createVerticalStrut(4));
            labelCol.add(title);

            header.add(iconBox);
            header.add(labelCol);
            card.add(header, BorderLayout.NORTH);

            JPanel body = new JPanel();
            body.setOpaque(false);
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

            JLabel desc = new JLabel("<html><body style='width:220px'>" + r[3] + "</body></html>");
            desc.setFont(UIFonts.BODY_SM);
            desc.setForeground(UIColors.TEXT_SECONDARY);
            desc.setAlignmentX(LEFT_ALIGNMENT);
            body.add(desc);
            body.add(Box.createVerticalStrut(12));

            JPanel params = switch (index) {
                  case 0 -> buildB1Params(accent);
                  case 1 -> buildB2Params(accent);
                  case 2 -> buildB3Params(accent);
                  default -> buildB4Params(accent);
            };
            params.setAlignmentX(LEFT_ALIGNMENT);
            body.add(params);

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
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  try {
                  LocalDateTime desde = LocalDate.parse(desdeF.getText().trim(), FMT).atStartOfDay();
                  LocalDateTime hasta = LocalDate.parse(hastaF.getText().trim(), FMT).atTime(LocalTime.MAX);
                  String ruta = mundial.generarReporteBitacora(desde, hasta);
                  showPdfPreview(ruta);
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  } finally { setCursor(Cursor.getDefaultCursor()); }
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
            JTextField estMinF = UIFactory.textField("1.60");
            JTextField estMaxF = UIFactory.textField("2.10");

            List<Equipo> equipos = List.of();
            try { equipos = gestion.listarEquipos(); } catch (Exception ignored) {}

            String[] noms = new String[equipos.size() + 1];
            noms[0] = "Todos los equipos";
            for (int i = 0; i < equipos.size(); i++) noms[i + 1] = equipos.get(i).getNombre();
            JComboBox<String> equipoBox = UIFactory.comboBox(noms);
            final List<Equipo> eqFinal = equipos;

            p.add(UIFactory.formLabel("Peso mín (kg)")); p.add(pesoMinF);
            p.add(UIFactory.formLabel("Peso máx (kg)")); p.add(pesoMaxF);
            p.add(UIFactory.formLabel("Estatura mín (m)")); p.add(estMinF);
            p.add(UIFactory.formLabel("Estatura máx (m)")); p.add(estMaxF);
            p.add(UIFactory.formLabel("Equipo")); p.add(equipoBox);

            JButton btn = accentButton("Generar PDF", accent);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            btn.addActionListener(e -> {
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  try {
                        BigDecimal pMin = new BigDecimal(pesoMinF.getText().trim());
                        BigDecimal pMax = new BigDecimal(pesoMaxF.getText().trim());
                        BigDecimal eMin = new BigDecimal(estMinF.getText().trim());
                        BigDecimal eMax = new BigDecimal(estMaxF.getText().trim());
                        int sel = equipoBox.getSelectedIndex();
                        int idEq = (sel <= 0 || sel > eqFinal.size())
                              ? 0 : eqFinal.get(sel - 1).getIdEquipo();
                        String ruta = mundial.generarReporteJugadoresFiltrados(pMin, pMax, eMin, eMax, idEq);
                        showPdfPreview(ruta);
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  } finally { setCursor(Cursor.getDefaultCursor()); }
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
                  ? new String[]{"UEFA","CONMEBOL","CONCACAF","CAF","AFC","OFC"}
                  : confs.stream()
                        .map(co.edu.uniquindio.model.Confederacion::getNombre)
                        .toArray(String[]::new);
            JComboBox<String> confBox = UIFactory.comboBox(noms);
            final List<co.edu.uniquindio.model.Confederacion> confsFinal = confs;

            p.add(UIFactory.formLabel("Confederación")); p.add(confBox);

            JButton btn = accentButton("Generar PDF", accent);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            btn.addActionListener(e -> {
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  try {
                  int sel = confBox.getSelectedIndex();
                  int idConf = 1;
                  String nombre = confBox.getSelectedItem().toString();
                  if (!confsFinal.isEmpty() && sel >= 0 && sel < confsFinal.size()) {
                        idConf = confsFinal.get(sel).getIdConfederacion();
                        nombre = confsFinal.get(sel).getNombre();
                  }
                  String ruta = mundial.generarReporteValorPorConfederacion(idConf, nombre);
                  showPdfPreview(ruta);
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  } finally { setCursor(Cursor.getDefaultCursor()); }
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
                  "<html>No requiere parámetros adicionales.<br>" +
                  "Genera el listado completo de equipos por cada país sede.</html>");
            info.setFont(UIFonts.BODY_SM);
            info.setForeground(UIColors.TEXT_MUTED);
            info.setAlignmentX(LEFT_ALIGNMENT);
            wrap.add(info);
            wrap.add(Box.createVerticalStrut(10));

            JButton btn = accentButton("Generar PDF", accent);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            btn.addActionListener(e -> {
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  try {
                        String ruta = mundial.generarReporteEquiposPorAnfitrion();
                        showPdfPreview(ruta);
                  } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  } finally { setCursor(Cursor.getDefaultCursor()); }
            });

            wrap.add(btn);
            return wrap;
      }

      /**
       * Abre un diálogo modal con la vista previa del PDF generado.
       * Renderiza cada página con PDFBox y permite navegar entre páginas.
       * Si por alguna razón no se puede renderizar (PDF corrupto, etc.) cae de
       * vuelta a un mensaje con la ruta para que el usuario lo abra a mano.
       */
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
                        "PDF generado en:\n" + pdfPath + "\n\nNo se pudo abrir la vista previa: " + ex.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
            }
            final PDFRenderer renderer = new PDFRenderer(doc);
            final int totalPages = doc.getNumberOfPages();

            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  "Vista previa: " + pdfFile.getName(),
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(820, 900);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());
            dlg.addWindowListener(new java.awt.event.WindowAdapter() {
                  @Override public void windowClosed(java.awt.event.WindowEvent e) {
                        try { doc.close(); } catch (Exception ignored) {}
                  }
            });
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JLabel pathLabel = new JLabel("Archivo: " + pdfFile.getAbsolutePath());
            pathLabel.setFont(UIFonts.BODY_SM);
            pathLabel.setForeground(UIColors.TEXT_SECONDARY);
            pathLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            dlg.add(pathLabel, BorderLayout.NORTH);

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.TOP);
            imageLabel.setBackground(new Color(0xEEEEEE));
            imageLabel.setOpaque(true);
            JScrollPane scroll = new JScrollPane(imageLabel);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            dlg.add(scroll, BorderLayout.CENTER);

            JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            nav.setBackground(new Color(0xF8F9FC));
            nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));
            JButton prev = new JButton("◀ Anterior");
            JButton next = new JButton("Siguiente ▶");
            JLabel pageInfo = new JLabel();
            pageInfo.setFont(UIFonts.LABEL_BOLD);
            JButton openExternal = new JButton("Abrir en visor del sistema");

            final int[] current = {0};
            Runnable renderPage = () -> {
                  try {
                        BufferedImage img = renderer.renderImageWithDPI(current[0], 110);
                        imageLabel.setIcon(new ImageIcon(img));
                        imageLabel.setText(null);
                        pageInfo.setText("Página " + (current[0] + 1) + " de " + totalPages);
                        prev.setEnabled(current[0] > 0);
                        next.setEnabled(current[0] < totalPages - 1);
                  } catch (Exception ex) {
                        ex.printStackTrace();
                        imageLabel.setIcon(null);
                        imageLabel.setText("No se pudo renderizar la página: " + ex.getMessage());
                  }
            };

            prev.addActionListener(e -> { if (current[0] > 0)              { current[0]--; renderPage.run(); }});
            next.addActionListener(e -> { if (current[0] < totalPages - 1) { current[0]++; renderPage.run(); }});
            openExternal.addActionListener(e -> {
                  try { Desktop.getDesktop().open(pdfFile); }
                  catch (Exception ex) {
                        JOptionPane.showMessageDialog(dlg,
                              "No se pudo abrir con el visor del sistema:\n" + ex.getMessage(),
                              "Aviso", JOptionPane.WARNING_MESSAGE);
                  }
            });

            nav.add(prev);
            nav.add(pageInfo);
            nav.add(next);
            nav.add(Box.createHorizontalStrut(20));
            nav.add(openExternal);
            dlg.add(nav, BorderLayout.SOUTH);

            renderPage.run();
            dlg.setVisible(true);
      }

      private JButton accentButton(String text, Color accent) {
            JButton btn = new JButton(text) {
                  @Override protected void paintComponent(Graphics g) {
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
                  public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(accent); }
            });
            return btn;
      }
}