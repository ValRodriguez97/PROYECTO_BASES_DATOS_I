package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.Bitacora;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AuditPanel extends JPanel {

      private final GestionDatosService gestion;
      private final MundialService mundial;

      private JTable table;
      private DefaultTableModel model;
      private JTextField desdeField;
      private JTextField hastaField;
      private JLabel statusLabel;

      private static final String[] COLUMNS = {
            "#", "Usuario (ID)", "Fecha Ingreso", "Fecha Salida", "Duración", "Estado"
      };

      private static final DateTimeFormatter FMT_DISPLAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
      private static final DateTimeFormatter FMT_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

      public AuditPanel(GestionDatosService gestion, MundialService mundial) {
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
            content.add(Box.createVerticalStrut(20));
            content.add(buildFilterCard());
            content.add(Box.createVerticalStrut(16));
            content.add(buildTable());
            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.add(UIFactory.heading("Bitácora del Sistema"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Registro de ingresos y salidas de todos los usuarios"));

            JButton pdfBtn = tealButton("📄 Exportar PDF");
            pdfBtn.addActionListener(e -> exportarPDF());

            p.add(left,   BorderLayout.WEST);
            p.add(pdfBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildFilterCard() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(20, 24, 20, 24)));

            JLabel title = UIFactory.sectionTitle("🔍  Filtrar por Rango de Fecha/Hora");
            card.add(title, BorderLayout.NORTH);
            card.add(Box.createVerticalStrut(14));

            JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

            JPanel desdeWrap = labeledField("Desde (yyyy-MM-dd)");
            desdeField = (JTextField) desdeWrap.getClientProperty("field");
            desdeField.setText(LocalDate.now().minusDays(30).format(FMT_INPUT));

            JPanel hastaWrap = labeledField("Hasta (yyyy-MM-dd)");
            hastaField = (JTextField) hastaWrap.getClientProperty("field");
            hastaField.setText(LocalDate.now().format(FMT_INPUT));

            JButton buscarBtn = purpleButton("▶  Buscar");
            buscarBtn.addActionListener(e -> buscarRegistros());

            JButton hoyBtn = tealButton("Hoy");
            hoyBtn.addActionListener(e -> {
                  String hoy = LocalDate.now().format(FMT_INPUT);
                  desdeField.setText(hoy);
                  hastaField.setText(hoy);
                  buscarRegistros();
            });

            JButton semanaBtn = UIFactory.outlineButton("Última semana");
            semanaBtn.addActionListener(e -> {
                  hastaField.setText(LocalDate.now().format(FMT_INPUT));
                  desdeField.setText(LocalDate.now().minusDays(7).format(FMT_INPUT));
                  buscarRegistros();
            });

            row.add(desdeWrap);
            row.add(hastaWrap);
            row.add(buscarBtn);
            row.add(hoyBtn);
            row.add(semanaBtn);

            card.add(row, BorderLayout.CENTER);
            return card;
      }

      private JPanel labeledField(String labelText) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            JLabel lbl = UIFactory.formLabel(labelText);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            JTextField tf = UIFactory.textField("");
            tf.setAlignmentX(LEFT_ALIGNMENT);
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            p.add(lbl);
            p.add(Box.createVerticalStrut(4));
            p.add(tf);
            p.putClientProperty("field", tf);
            return p;
      }

      private JPanel buildTable() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel th = new JPanel(new BorderLayout());
            th.setBackground(new Color(0xF8F9FC));
            th.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(14, 20, 14, 20)));
            th.add(UIFactory.sectionTitle("Registros de Bitácora"), BorderLayout.WEST);
            statusLabel = new JLabel("Usa el filtro para cargar registros");
            statusLabel.setFont(UIFonts.BODY_SM);
            statusLabel.setForeground(UIColors.TEXT_MUTED);
            th.add(statusLabel, BorderLayout.EAST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(4).setMaxWidth(100);
            table.getColumnModel().getColumn(5).setMaxWidth(110);

            table.getColumnModel().getColumn(5).setCellRenderer((t, v, sel, foc, r, c) -> {
                  String estado = v != null ? v.toString() : "";
                  boolean activo = "En sesión".equals(estado);
                  Color bg = activo ? UIColors.SUCCESS_BG : UIColors.INFO_BG;
                  Color fg = activo ? UIColors.SUCCESS_FG : UIColors.INFO_FG;
                  JLabel badge = UIFactory.badge(estado, bg, fg);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE
                  : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            card.add(th, BorderLayout.NORTH);
            card.add(UIFactory.scrollPane(table), BorderLayout.CENTER);

            JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
            foot.setBackground(new Color(0xF8F9FC));
            foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));
            foot.add(statusLabel);
            card.add(foot, BorderLayout.SOUTH);

            return card;
      }


      private void buscarRegistros() {
            try {
                  LocalDateTime desde = LocalDate.parse(desdeField.getText().trim(), FMT_INPUT)
                  .atStartOfDay();
                  LocalDateTime hasta = LocalDate.parse(hastaField.getText().trim(), FMT_INPUT)
                  .atTime(LocalTime.MAX);

                  List<Bitacora> registros = gestion.listarBitacoraPorRango(desde, hasta);
                  poblarTabla(registros);

            } catch (DateTimeParseException ex) {
                  JOptionPane.showMessageDialog(this,
                  "Formato de fecha inválido. Usa yyyy-MM-dd (ej: 2026-06-11).",
                  "Error de formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                  cargarDemo();
                  JOptionPane.showMessageDialog(this,
                  "No se pudieron cargar los datos reales: " + ex.getMessage()
                  + "\nMostrando datos de demostración.",
                  "Aviso", JOptionPane.WARNING_MESSAGE);
            }
      }

      private void poblarTabla(List<Bitacora> registros) {
            model.setRowCount(0);
            int idx = 1;
            for (Bitacora b : registros) {
                  String ingreso = b.getFechaHoraIngreso() != null
                  ? b.getFechaHoraIngreso().format(FMT_DISPLAY) : "-";
                  String salida  = b.getFechaHoraSalida() != null
                  ? b.getFechaHoraSalida().format(FMT_DISPLAY) : "-";

                  String duracion = "-";
                  String estado   = "En sesión";
                  if (b.getFechaHoraSalida() != null && b.getFechaHoraIngreso() != null) {
                  long mins = java.time.Duration.between(
                        b.getFechaHoraIngreso(), b.getFechaHoraSalida()).toMinutes();
                  duracion = mins + " min";
                  estado   = "Cerrada";
                  }

                  model.addRow(new Object[]{
                  idx++,
                  "ID: " + b.getIdUsuario(),
                  ingreso,
                  salida,
                  duracion,
                  estado
                  });
            }
            statusLabel.setText(registros.size() + " registro(s) encontrado(s)");
      }

      private void cargarDemo() {
            model.setRowCount(0);
            Object[][] demo = {
                  {1, "ID: 1 (admin)",    "08/05/2026 09:00:00", "08/05/2026 17:30:00", "510 min", "Cerrada"},
                  {2, "ID: 2 (usuario1)", "08/05/2026 10:15:00", "08/05/2026 12:00:00", "105 min", "Cerrada"},
                  {3, "ID: 3 (guest1)",   "08/05/2026 14:00:00", "-",                   "-",       "En sesión"},
                  {4, "ID: 1 (admin)",    "07/05/2026 08:45:00", "07/05/2026 18:00:00", "555 min", "Cerrada"},
                  {5, "ID: 2 (usuario1)", "07/05/2026 11:30:00", "07/05/2026 13:30:00", "120 min", "Cerrada"},
            };
            for (Object[] row : demo) model.addRow(row);
            statusLabel.setText("5 registro(s) — datos de demostración");
      }

      private void exportarPDF() {
            String desdeStr = desdeField.getText().trim();
            String hastaStr = hastaField.getText().trim();
            if (desdeStr.isEmpty() || hastaStr.isEmpty()) {
                  JOptionPane.showMessageDialog(this,
                  "Por favor define el rango de fechas antes de exportar.",
                  "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                  LocalDateTime desde = LocalDate.parse(desdeStr, FMT_INPUT).atStartOfDay();
                  LocalDateTime hasta = LocalDate.parse(hastaStr, FMT_INPUT).atTime(LocalTime.MAX);
                  String ruta = mundial.generarReporteBitacora(desde, hasta);
                  JOptionPane.showMessageDialog(this,
                  "PDF generado correctamente:\n" + ruta,
                  "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this,
                  "Error al generar el PDF: " + ex.getMessage(),
                  "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                  setCursor(Cursor.getDefaultCursor());
            }
      }

      private JButton purpleButton(String text) {
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
            btn.setBackground(UIColors.PURPLE);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(UIColors.PURPLE_LIGHT); }
                  public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(UIColors.PURPLE); }
            });
            return btn;
      }

      private JButton tealButton(String text) {
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
            btn.setBackground(UIColors.TURQUOISE);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0x009688)); }
                  public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(UIColors.TURQUOISE); }
            });
            return btn;
      }
}