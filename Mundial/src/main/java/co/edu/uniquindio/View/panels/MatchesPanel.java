package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
            content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

            content.add(buildHeader());
            content.add(Box.createVerticalStrut(18));
            content.add(buildFilters());
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
            left.add(UIFactory.heading("Partidos"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Programación de encuentros del Mundial FIFA 2026"));

            JButton addBtn = UIFactory.primaryButton("+ Nuevo Partido");
            addBtn.addActionListener(e -> showMatchForm(-1));
            p.add(left,   BorderLayout.WEST);
            p.add(addBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildFilters() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            card.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(10, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
            JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
            row.setOpaque(false);
            searchField = UIFactory.textField("Buscar equipo...");
            groupFilter = UIFactory.comboBox(
                  "Todos los grupos","A","B","C","D","E","F","G","H","I","J","K","L");
            JButton btn = UIFactory.primaryButton("Buscar");
            btn.addActionListener(e -> loadData());
            row.add(searchField); row.add(groupFilter); row.add(btn);
            card.add(row, BorderLayout.CENTER);
            return card;
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
            th.add(UIFactory.sectionTitle("Partidos Registrados"), BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 6; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(44);
            table.getColumnModel().getColumn(1).setMaxWidth(70);
            table.getColumnModel().getColumn(6).setMaxWidth(110);

            table.getColumnModel().getColumn(6).setCellRenderer((t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                  @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
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
                  String filtro   = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  String grupoSel = groupFilter != null && groupFilter.getSelectedIndex() > 0
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
                  JOptionPane.showMessageDialog(this,
                  "Error al cargar partidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
      }

      private void showMatchForm(int editRow) {
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow < 0 ? "Nuevo Partido" : "Editar Partido",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(480, 340);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
            form.setBackground(Color.WHITE);
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

            List<Equipo>  equipos = List.of();
            List<Grupo>   grupos = List.of();
            List<Estadio> estadios = List.of();
            try { equipos = gestion.listarEquipos(); } catch (Exception ignored) {}
            try { grupos = gestion.listarGrupos(); } catch (Exception ignored) {}
            try { estadios = gestion.listarEstadios(); } catch (Exception ignored) {}

            String[] nEq = equipos.stream().map(Equipo::getNombre).toArray(String[]::new);
            String[] nGr = grupos.stream().map(g -> String.valueOf(g.getLetra())).toArray(String[]::new);
            String[] nEs = estadios.stream().map(Estadio::getNombre).toArray(String[]::new);

            JComboBox<String> localBox = nEq.length > 0 ? UIFactory.comboBox(nEq) : UIFactory.comboBox("(sin equipos)");
            JComboBox<String> visitBox = nEq.length > 0 ? UIFactory.comboBox(nEq) : UIFactory.comboBox("(sin equipos)");
            JComboBox<String> grupoBox = nGr.length > 0 ? UIFactory.comboBox(nGr) : UIFactory.comboBox("(sin grupos)");
            JComboBox<String> estadioBox = nEs.length > 0 ? UIFactory.comboBox(nEs) : UIFactory.comboBox("(sin estadios)");
            JTextField fechaF = UIFactory.textField("Ej: 2026-06-11T18:00");

            if (editRow >= 0) {
                  localBox.setSelectedItem(model.getValueAt(editRow, 2));
                  visitBox.setSelectedItem(model.getValueAt(editRow, 3));
                  estadioBox.setSelectedItem(model.getValueAt(editRow, 5));
            }

            form.add(UIFactory.formLabel("Equipo Local")); form.add(localBox);
            form.add(UIFactory.formLabel("Equipo Visitante")); form.add(visitBox);
            form.add(UIFactory.formLabel("Grupo")); form.add(grupoBox);
            form.add(UIFactory.formLabel("Estadio")); form.add(estadioBox);
            form.add(UIFactory.formLabel("Fecha y Hora")); form.add(fechaF);

            JPanel footer = buildFooter();
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = UIFactory.primaryButton(editRow < 0 ? "Guardar" : "Actualizar");
            save.addActionListener(e -> {
                  if (editRow >= 0) {
                  model.setValueAt(localBox.getSelectedItem(),  editRow, 2);
                  model.setValueAt(visitBox.getSelectedItem(),  editRow, 3);
                  model.setValueAt(estadioBox.getSelectedItem(),editRow, 5);
                  } else {
                  model.addRow(new Object[]{
                        model.getRowCount() + 1,
                        grupoBox.getSelectedItem(),
                        localBox.getSelectedItem(),
                        visitBox.getSelectedItem(),
                        fechaF.getText().trim(),
                        estadioBox.getSelectedItem(), ""
                  });
                  }
                  dlg.dispose();
            });
            footer.add(cancel); footer.add(save);
            dlg.add(form, BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            p.setOpaque(false);
            JButton edit = smallBtn("✏", UIColors.BLUE, UIColors.INFO_BG);
            edit.addActionListener(e -> showMatchForm(row));
            JButton del  = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
            del.addActionListener(e -> {
                  if (row >= model.getRowCount()) return;
                  int c = JOptionPane.showConfirmDialog(this,
                  "¿Eliminar este partido?", "Confirmar", JOptionPane.YES_NO_OPTION);
                  if (c == JOptionPane.YES_OPTION) model.removeRow(row);
            });
            p.add(edit); p.add(del);
            return p;
      }

      private JButton smallBtn(String text, Color fg, Color bg) {
            JButton btn = new JButton(text);
            btn.setFont(UIFonts.BODY_SM);
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
      }

      private JPanel buildFooter() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
            p.setBackground(new Color(0xF8F9FC));
            p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));
            return p;
      }
}