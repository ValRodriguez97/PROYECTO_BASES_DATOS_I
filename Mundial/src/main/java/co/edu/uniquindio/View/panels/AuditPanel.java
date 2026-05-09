package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.Bitacora;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
            "#", "ID Usuario", "Fecha Ingreso", "Fecha Salida", "Duración", "Estado"
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
            content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

            content.add(buildHeader());
            content.add(Box.createVerticalStrut(18));
            content.add(buildFilterCard());
            content.add(Box.createVerticalStrut(14));
            content.add(buildTable());
            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.add(UIFactory.heading("Bitácora"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Registro de ingresos y salidas del sistema"));

            JButton pdfBtn = UIFactory.tealButton("📄 Exportar PDF");
            pdfBtn.addActionListener(e -> exportarPDF());
            p.add(left, BorderLayout.WEST);
            p.add(pdfBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildFilterCard() {
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(Color.WHITE);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            card.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(10, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 18, 16, 18)
            ));

            JLabel title = UIFactory.sectionTitle("Filtrar por Rango de Fecha");
            card.add(title, BorderLayout.NORTH);

            JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
            row.setOpaque(false);

            JPanel desdeWrap = labeledField("Desde (yyyy-MM-dd)");
            desdeField = (JTextField) desdeWrap.getClientProperty("field");
            desdeField.setText(LocalDate.now().minusDays(30).format(FMT_INPUT));

            JPanel hastaWrap = labeledField("Hasta (yyyy-MM-dd)");
            hastaField = (JTextField) hastaWrap.getClientProperty("field");
            hastaField.setText(LocalDate.now().format(FMT_INPUT));

            JButton buscarBtn = UIFactory.primaryButton("Buscar");
            buscarBtn.addActionListener(e -> buscarRegistros());

            JButton hoyBtn = UIFactory.tealButton("Hoy");
            hoyBtn.addActionListener(e -> {
                  String hoy = LocalDate.now().format(FMT_INPUT);
                  desdeField.setText(hoy);
                  hastaField.setText(hoy);
                  buscarRegistros();
            });

            row.add(desdeWrap);
            row.add(hastaWrap);
            row.add(buscarBtn);
            row.add(hoyBtn);
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
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            p.add(lbl);
            p.add(Box.createVerticalStrut(4));
            p.add(tf);
            p.putClientProperty("field", tf);
            return p;
      }

      private JPanel buildTable() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(10, UIColors.BORDER));

            JPanel th = new JPanel(new BorderLayout());
            th.setBackground(new Color(0xF8F9FC));
            th.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(14, 18, 14, 18)
            ));
            th.add(UIFactory.sectionTitle("Registros"), BorderLayout.WEST);

            statusLabel = new JLabel("Usa el filtro para cargar registros");
            statusLabel.setFont(UIFonts.BODY_SM);
            statusLabel.setForeground(UIColors.TEXT_MUTED);
            th.add(statusLabel, BorderLayout.EAST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(44);
            table.getColumnModel().getColumn(1).setMaxWidth(90);
            table.getColumnModel().getColumn(4).setMaxWidth(100);
            table.getColumnModel().getColumn(5).setMaxWidth(110);

            // Badge estado
            table.getColumnModel().getColumn(5).setCellRenderer((t, v, sel, foc, r, c) -> {
                  String estado = v != null ? v.toString() : "";
                  boolean activo = "En sesión".equals(estado);
                  JLabel badge = UIFactory.badge(estado,
                  activo ? UIColors.SUCCESS_BG : UIColors.INFO_BG,
                  activo ? UIColors.SUCCESS_FG : UIColors.INFO_FG);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            card.add(th, BorderLayout.NORTH);
            card.add(UIFactory.scrollPane(table), BorderLayout.CENTER);
            return card;
      }

      private void buscarRegistros() {
            try {
                  LocalDateTime desde = LocalDate.parse(desdeField.getText().trim(), FMT_INPUT).atStartOfDay();
                  LocalDateTime hasta = LocalDate.parse(hastaField.getText().trim(), FMT_INPUT).atTime(LocalTime.MAX);
                  List<Bitacora> registros = gestion.listarBitacoraPorRango(desde, hasta);
                  poblarTabla(registros);
            } catch (DateTimeParseException ex) {
                  JOptionPane.showMessageDialog(this,
                  "Formato de fecha inválido. Usa yyyy-MM-dd (ej: 2026-06-11).",
                  "Error de formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this,
                  "Error al cargar registros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
      }

      private void poblarTabla(List<Bitacora> registros) {
            model.setRowCount(0);
            int idx = 1;
            for (Bitacora b : registros) {
                  String ingreso = b.getFechaHoraIngreso() != null
                  ? b.getFechaHoraIngreso().format(FMT_DISPLAY) : "-";
                  String salida  = b.getFechaHoraSalida()  != null
                  ? b.getFechaHoraSalida().format(FMT_DISPLAY)  : "-";
                  String duracion = "-";
                  String estado = "En sesión";
                  if (b.getFechaHoraSalida() != null && b.getFechaHoraIngreso() != null) {
                  long mins = java.time.Duration.between(
                        b.getFechaHoraIngreso(), b.getFechaHoraSalida()).toMinutes();
                  duracion = mins + " min";
                  estado = "Cerrada";
                  }
                  model.addRow(new Object[]{idx++, b.getIdUsuario(), ingreso, salida, duracion, estado});
            }
            statusLabel.setText(registros.size() + " registro(s) encontrado(s)");
      }

      private void exportarPDF() {
            String desdeStr = desdeField.getText().trim();
            String hastaStr = hastaField.getText().trim();
            if (desdeStr.isEmpty() || hastaStr.isEmpty()) {
                  JOptionPane.showMessageDialog(this,
                  "Define el rango de fechas antes de exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                  LocalDateTime desde = LocalDate.parse(desdeStr, FMT_INPUT).atStartOfDay();
                  LocalDateTime hasta = LocalDate.parse(hastaStr, FMT_INPUT).atTime(LocalTime.MAX);
                  String ruta = mundial.generarReporteBitacora(desde, hasta);
                  JOptionPane.showMessageDialog(this,
                  "PDF generado:\n" + ruta, "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this,
                  "Error al generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                  setCursor(Cursor.getDefaultCursor());
            }
      }
      }