package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsPanel extends JPanel {

      private final GestionDatosService gestion;
      private final MundialService  mundial;

      private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      private static final String[][] REPORTS = {
            {"b1", "📋", "Bitácora por Rango de Fecha",
                  "Exporta todos los registros de ingreso/salida en un rango de fecha/hora."},
            {"b2", "👤", "Jugadores por Peso, Estatura y Equipo",
                  "Filtra jugadores por rangos de peso y estatura, opcionalmente por equipo."},
            {"b3", "💰", "Valor Total por Confederación",
                  "Muestra el valor total de mercado de los jugadores de cada equipo, agrupado por confederación."},
            {"b4", "🌎", "Equipos por País Anfitrión",
                  "Lista los equipos y sus países de origen que jugarán en cada país sede."},
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
            content.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

            content.add(buildHeader());
            content.add(Box.createVerticalStrut(24));

            JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
            grid.setOpaque(false);
            grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));

            grid.add(buildReportCard(0));
            grid.add(buildReportCard(1));
            grid.add(buildReportCard(2));
            grid.add(buildReportCard(3));

            content.add(grid);
            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
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
            String[] r = REPORTS[index];
            Color[] palette = {
                  UIColors.PURPLE, UIColors.BLUE, UIColors.TURQUOISE, UIColors.MAGENTA
            };
            Color accent = palette[index];

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(14, UIColors.BORDER));

            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14)) {
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

            JPanel iconBox = new JPanel() {
                  { setPreferredSize(new Dimension(44, 44)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(accent);
                        g2.fill(new RoundRectangle2D.Float(0, 0, 44, 44, 12, 12));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
                        g2.drawString(r[1], 8, 32);
                        g2.dispose();
                  }
            };

            JPanel labelCol = new JPanel();
            labelCol.setOpaque(false);
            labelCol.setLayout(new BoxLayout(labelCol, BoxLayout.Y_AXIS));
            JLabel tag = UIFactory.badge(r[0].toUpperCase(), new Color(
                  accent.getRed(), accent.getGreen(), accent.getBlue(), 30), accent);
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
            body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            JLabel desc = new JLabel("<html><body style='width:220px'>" + r[3] + "</body></html>");
            desc.setFont(UIFonts.BODY_SM);
            desc.setForeground(UIColors.TEXT_SECONDARY);
            desc.setAlignmentX(LEFT_ALIGNMENT);
            body.add(desc);
            body.add(Box.createVerticalStrut(14));

            switch (index) {
                  case 0 -> body.add(buildParamsB1());
                  case 1 -> body.add(buildParamsB2());
                  case 2 -> body.add(buildParamsB3());
                  case 3 -> body.add(buildParamsB4(accent));
            }

            card.add(body, BorderLayout.CENTER);
            return card;
      }

      private JPanel buildParamsB1() {
            JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
            p.setOpaque(false);
            p.setAlignmentX(LEFT_ALIGNMENT);

            JTextField desdeF = UIFactory.textField(LocalDate.now().minusDays(7).format(FMT));
            JTextField hastaF = UIFactory.textField(LocalDate.now().format(FMT));

            p.add(UIFactory.formLabel("Desde (yyyy-MM-dd)")); p.add(desdeF);
            p.add(UIFactory.formLabel("Hasta (yyyy-MM-dd)")); p.add(hastaF);

            JButton btn = magentaButton("Generar PDF — b1", UIColors.PURPLE);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.addActionListener(e -> {
                  try {
                  LocalDateTime desde = LocalDate.parse(desdeF.getText().trim(), FMT).atStartOfDay();
                  LocalDateTime hasta = LocalDate.parse(hastaF.getText().trim(), FMT).atTime(LocalTime.MAX);
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  String ruta = mundial.generarReporteBitacora(desde, hasta);
                  setCursor(Cursor.getDefaultCursor());
                  JOptionPane.showMessageDialog(this, "PDF generado:\n" + ruta);
                  } catch (Exception ex) {
                  setCursor(Cursor.getDefaultCursor());
                  JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });

            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.add(p);
            wrap.add(Box.createVerticalStrut(10));
            wrap.add(btn);
            return wrap;
      }


      private JPanel buildParamsB2() {
            JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
            p.setOpaque(false);
            p.setAlignmentX(LEFT_ALIGNMENT);

            JTextField pesoMinF = UIFactory.textField("50");
            JTextField pesoMaxF = UIFactory.textField("100");
            JTextField estMinF = UIFactory.textField("1.60");
            JTextField estMaxF = UIFactory.textField("2.10");

            String[] equipoNoms;
            List<Equipo> equipos = List.of();
            try { equipos = gestion.listarEquipos(); } catch (Exception ignored) {}
            equipoNoms = new String[equipos.size() + 1];
            equipoNoms[0] = "Todos los equipos";
            int idx = 1;
            for (Equipo eq : equipos) equipoNoms[idx++] = eq.getNombre();
            JComboBox<String> equipoBox = UIFactory.comboBox(
                  equipoNoms.length == 1 ? new String[]{"Todos los equipos"} : equipoNoms);
            final List<Equipo> equiposFinal = equipos;

            p.add(UIFactory.formLabel("Peso mínimo (kg)")); p.add(pesoMinF);
            p.add(UIFactory.formLabel("Peso máximo (kg)")); p.add(pesoMaxF);
            p.add(UIFactory.formLabel("Estatura mín (m)")); p.add(estMinF);
            p.add(UIFactory.formLabel("Estatura máx (m)")); p.add(estMaxF);
            p.add(UIFactory.formLabel("Equipo")); p.add(equipoBox);

            JButton btn = magentaButton("Generar PDF — b2", UIColors.BLUE);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.addActionListener(e -> {
                  try {
                        BigDecimal pMin = new BigDecimal(pesoMinF.getText().trim());
                        BigDecimal pMax = new BigDecimal(pesoMaxF.getText().trim());
                        BigDecimal eMin = new BigDecimal(estMinF.getText().trim());
                        BigDecimal eMax = new BigDecimal(estMaxF.getText().trim());
                        int selIdx = equipoBox.getSelectedIndex();
                        int idEq = (selIdx <= 0 || selIdx > equiposFinal.size())
                              ? 0 : equiposFinal.get(selIdx - 1).getIdEquipo();
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        String ruta = mundial.generarReporteJugadoresFiltrados(pMin, pMax, eMin, eMax, idEq);
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, "PDF generado:\n" + ruta);
                  } catch (Exception ex) {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });

            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.add(p);
            wrap.add(Box.createVerticalStrut(10));
            wrap.add(btn);
            return wrap;
      }


      private JPanel buildParamsB3() {
            JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
            p.setOpaque(false);
            p.setAlignmentX(LEFT_ALIGNMENT);

            List<co.edu.uniquindio.model.Confederacion> confs = List.of();
            try { confs = gestion.listarConfederaciones(); } catch (Exception ignored) {}

            String[] confNoms = confs.isEmpty()
                  ? new String[]{"UEFA","CONMEBOL","CONCACAF","CAF","AFC","OFC"}
                  : confs.stream().map(co.edu.uniquindio.model.Confederacion::getNombre).toArray(String[]::new);
            JComboBox<String> confBox = UIFactory.comboBox(confNoms);
            final List<co.edu.uniquindio.model.Confederacion> confsFinal = confs;

            p.add(UIFactory.formLabel("Confederación")); p.add(confBox);

            JButton btn = magentaButton("Generar PDF — b3", UIColors.TURQUOISE);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.addActionListener(e -> {
                  try {
                        int selIdx = confBox.getSelectedIndex();
                        int idConf = 1;
                        String nombre = confBox.getSelectedItem().toString();
                        if (!confsFinal.isEmpty() && selIdx >= 0 && selIdx < confsFinal.size()) {
                              idConf  = confsFinal.get(selIdx).getIdConfederacion();
                              nombre  = confsFinal.get(selIdx).getNombre();
                        }
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        String ruta = mundial.generarReporteValorPorConfederacion(idConf, nombre);
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, "PDF generado:\n" + ruta);
                  } catch (Exception ex) {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });

            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.add(p);
            wrap.add(Box.createVerticalStrut(10));
            wrap.add(btn);
            return wrap;
      }


      private JPanel buildParamsB4(Color accent) {
            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

            JLabel info = new JLabel("<html>No requiere parámetros adicionales.<br>" +
                  "Genera el listado completo de equipos por cada país sede.</html>");
            info.setFont(UIFonts.BODY_SM);
            info.setForeground(UIColors.TEXT_MUTED);
            info.setAlignmentX(LEFT_ALIGNMENT);
            wrap.add(info);
            wrap.add(Box.createVerticalStrut(10));

            JButton btn = magentaButton("Generar PDF — b4", accent);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.addActionListener(e -> {
                  try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        String ruta = mundial.generarReporteEquiposPorAnfitrion();
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, "PDF generado:\n" + ruta);
                  } catch (Exception ex) {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });

            wrap.add(btn);
            return wrap;
      }

      private JButton magentaButton(String text, Color color) {
            JButton btn = new JButton(text) {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(getBackground());
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                  g2.dispose();
                  super.paintComponent(g);
                  }
            };
            btn.setFont(UIFonts.BUTTON_MD);
            btn.setForeground(Color.WHITE);
            btn.setBackground(color);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseEntered(java.awt.event.MouseEvent e) {
                  btn.setBackground(color.darker());
                  }
                  public void mouseExited(java.awt.event.MouseEvent e) {
                  btn.setBackground(color);
                  }
            });
            return btn;
      }
      }