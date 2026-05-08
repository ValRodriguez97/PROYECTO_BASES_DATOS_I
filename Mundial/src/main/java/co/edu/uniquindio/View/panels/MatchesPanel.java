package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MatchesPanel extends JPanel {

      private final GestionDatosService gestion;
      private JTable table;
      private DefaultTableModel model;
      private JTextField searchField;
      private JComboBox<String> groupFilter;

      private static final String[] COLUMNS = {
            "#", "Grupo", "Local", "Visitante", "Fecha / Hora", "Estadio", "Acciones"
      };
      private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

      public MatchesPanel(GestionDatosService gestion) {
            this.gestion = gestion;
            setBackground(UIColors.BG_PAGE);
            setLayout(new BorderLayout());
            build();
            loadData();
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
            content.add(buildFilters());
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
            left.add(UIFactory.heading("Gestión de Partidos"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Administra los encuentros del Mundial FIFA 2026"));
            JButton addBtn = magentaButton("+ Programar Partido");
            addBtn.addActionListener(e -> showMatchForm(-1));
            p.add(left, BorderLayout.WEST);
            p.add(addBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildFilters() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.WHITE);
            p.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)));
            JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
            row.setOpaque(false);
            searchField = UIFactory.textField("Buscar equipo...");
            groupFilter = UIFactory.comboBox(
                  "Todos los grupos","A","B","C","D","E","F","G","H","I","J","K","L");
            JButton btn = magentaButton("Buscar");
            btn.addActionListener(e -> loadData());
            row.add(searchField);
            row.add(groupFilter);
            row.add(btn);
            p.add(row, BorderLayout.CENTER);
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
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)));
            th.add(UIFactory.sectionTitle("Partidos Registrados"), BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 6; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(1).setMaxWidth(70);
            table.getColumnModel().getColumn(6).setMaxWidth(130);

            table.getColumnModel().getColumn(1).setCellRenderer((t, v, sel, foc, r, c) -> {
                  JLabel badge = UIFactory.badge("  " + v + "  ", UIColors.PURPLE_PALE, UIColors.PURPLE);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE
                  : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            table.getColumnModel().getColumn(6).setCellRenderer(
                  (t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(6).setCellEditor(
                  new DefaultCellEditor(new JCheckBox()) {
                        @Override public Component getTableCellEditorComponent(
                              JTable t, Object v, boolean sel, int row, int col) {
                              return buildActionBtns(row);
                        }
                        @Override public Object getCellEditorValue() { return ""; }
                  });

            card.add(th, BorderLayout.NORTH);
            card.add(UIFactory.scrollPane(table), BorderLayout.CENTER);
            return card;
      }

      private void loadData() {
            model.setRowCount(0);
            try {
                  List<Partido> partidos = gestion.listarPartidos();
                  String filtro = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  String grupoSel = (groupFilter != null && groupFilter.getSelectedIndex() > 0)
                  ? (String) groupFilter.getSelectedItem() : "";
                  int idx = 1;
                  for (Partido p : partidos) {
                        String local     = p.getEquipoLocal()     != null ? p.getEquipoLocal().getNombre()     : "—";
                        String visitante = p.getEquipoVisitante() != null ? p.getEquipoVisitante().getNombre() : "—";
                        String grupo     = p.getGrupo()   != null ? String.valueOf(p.getGrupo().getLetra())  : "-";
                        String estadio   = p.getEstadio() != null ? p.getEstadio().getNombre()               : "-";
                        if (!filtro.isEmpty()
                              && !local.toLowerCase().contains(filtro)
                              && !visitante.toLowerCase().contains(filtro)) continue;
                        if (!grupoSel.isEmpty() && !grupo.equals(grupoSel)) continue;
                        model.addRow(new Object[]{
                              idx++, grupo, local, visitante,
                              p.getHoraFecha() != null ? p.getHoraFecha().format(FMT) : "-",
                              estadio, ""
                        });
                  }
            } catch (Exception ex) {
                  loadDemoData();
            }
      }

      private void loadDemoData() {
            model.setRowCount(0);
            Object[][] demo = {
                  {1,"A","Alemania","Argentina",    "11/06/2026 18:00","Estadio Azteca",  ""},
                  {2,"A","Marruecos","Japón",       "12/06/2026 21:00","Estadio Akron",   ""},
                  {3,"B","Francia","Brasil",         "12/06/2026 15:00","MetLife Stadium", ""},
                  {4,"B","USA","Corea del Sur",      "13/06/2026 18:00","SoFi Stadium",    ""},
                  {5,"C","España","Uruguay",         "13/06/2026 21:00","SoFi Stadium",    ""},
                  {6,"C","México","Senegal",         "14/06/2026 18:00","Estadio Azteca",  ""},
                  {7,"D","Portugal","Colombia",      "14/06/2026 21:00","AT&T Stadium",    ""},
                  {8,"D","Canadá","Nigeria",         "15/06/2026 18:00","Levi's Stadium",  ""},
            };
            for (Object[] row : demo) model.addRow(row);
      }

      private void showMatchForm(int editRow) {
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow < 0 ? "Programar Partido" : "Editar Partido",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(520, 380);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
            form.setBackground(Color.WHITE);

            List<Equipo> equipos;
            try { equipos = gestion.listarEquipos(); } catch (Exception ex) { equipos = List.of(); }
            String[] noms = equipos.stream().map(Equipo::getNombre).toArray(String[]::new);

            JComboBox<String> localBox     = UIFactory.comboBox(noms);
            JComboBox<String> visitanteBox = UIFactory.comboBox(noms);

            List<Grupo>   grupos;
            List<Estadio> estadios;
            try { grupos   = gestion.listarGrupos();   } catch (Exception ex) { grupos   = List.of(); }
            try { estadios = gestion.listarEstadios(); } catch (Exception ex) { estadios = List.of(); }

            String[] nomGrupos   = grupos.stream()
                  .map(g -> String.valueOf(g.getLetra())).toArray(String[]::new);
            String[] nomEstadios = estadios.stream()
                  .map(Estadio::getNombre).toArray(String[]::new);

            JComboBox<String> grupoBox   = UIFactory.comboBox(nomGrupos);
            JComboBox<String> estadioBox = UIFactory.comboBox(nomEstadios);
            JTextField fechaF = UIFactory.textField("ej: 2026-06-11T18:00");

            if (editRow >= 0) {
                  localBox.setSelectedItem(model.getValueAt(editRow, 2));
                  visitanteBox.setSelectedItem(model.getValueAt(editRow, 3));
                  estadioBox.setSelectedItem(model.getValueAt(editRow, 5));
            }

            form.add(UIFactory.formLabel("Equipo Local"));     form.add(localBox);
            form.add(UIFactory.formLabel("Equipo Visitante")); form.add(visitanteBox);
            form.add(UIFactory.formLabel("Grupo"));            form.add(grupoBox);
            form.add(UIFactory.formLabel("Estadio"));          form.add(estadioBox);
            form.add(UIFactory.formLabel("Fecha y Hora"));     form.add(fechaF);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 16));
            footer.setBackground(new Color(0xF8F9FC));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = magentaButton("Guardar Partido");
            save.addActionListener(e -> {
                  if (editRow >= 0) {
                  model.setValueAt(localBox.getSelectedItem(),     editRow, 2);
                  model.setValueAt(visitanteBox.getSelectedItem(), editRow, 3);
                  model.setValueAt(estadioBox.getSelectedItem(),   editRow, 5);
                  } else {
                        model.addRow(new Object[]{
                              model.getRowCount() + 1,
                              grupoBox.getSelectedItem(),
                              localBox.getSelectedItem(),
                              visitanteBox.getSelectedItem(),
                              fechaF.getText().trim(),
                              estadioBox.getSelectedItem(), ""
                        });
                  }
                  dlg.dispose();
            });
            footer.add(cancel);
            footer.add(save);
            dlg.add(form,   BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
            p.setOpaque(false);
            JButton edit = new JButton("✏");
            styleAction(edit, UIColors.BLUE, UIColors.INFO_BG);
            edit.addActionListener(e -> showMatchForm(row));
            JButton del = new JButton("🗑");
            styleAction(del, UIColors.RED, UIColors.ERROR_BG);
            del.addActionListener(e -> {
                  if (row < model.getRowCount()) {
                        int c = JOptionPane.showConfirmDialog(this,
                              "¿Eliminar este partido?", "Confirmar", JOptionPane.YES_NO_OPTION);
                        if (c == JOptionPane.YES_OPTION) model.removeRow(row);
                  }
            });
            p.add(edit); p.add(del);
            return p;
      }

      private void styleAction(JButton btn, Color fg, Color bg) {
            btn.setFont(UIFonts.BODY_SM);
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      private JButton magentaButton(String text) {
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
            btn.setBackground(UIColors.MAGENTA);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0xC2185B)); }
                  public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(UIColors.MAGENTA); }
            });
            return btn;
      }
}