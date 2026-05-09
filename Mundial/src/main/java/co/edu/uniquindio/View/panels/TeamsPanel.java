package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeamsPanel extends JPanel {

      private final GestionDatosService gestion;
      private JTable table;
      private DefaultTableModel model;
      private JTextField searchField;
      private JComboBox<String> confFilter;

      private static final String[] COLUMNS = {
            "#", "Equipo", "Confederación", "País", "Director Técnico", "Ranking FIFA", "Acciones"
      };

      public TeamsPanel(GestionDatosService gestion) {
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
            left.add(UIFactory.heading("Equipos"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Gestión de los 48 equipos del Mundial FIFA 2026"));

            JButton addBtn = UIFactory.primaryButton("+ Nuevo Equipo");
            addBtn.addActionListener(e -> showTeamForm(null));

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

            searchField = UIFactory.textField("Buscar por nombre...");
            confFilter  = UIFactory.comboBox(
                  "Todas las confederaciones", "UEFA", "CONMEBOL", "CONCACAF", "CAF", "AFC", "OFC");
            JButton btn = UIFactory.primaryButton("Buscar");
            btn.addActionListener(e -> loadData());

            row.add(searchField);
            row.add(confFilter);
            row.add(btn);
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
            th.add(UIFactory.sectionTitle("Equipos Registrados"), BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 6; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(44);
            table.getColumnModel().getColumn(2).setPreferredWidth(110);
            table.getColumnModel().getColumn(5).setMaxWidth(110);
            table.getColumnModel().getColumn(6).setMaxWidth(120);

            // Renderer confederación
            table.getColumnModel().getColumn(2).setCellRenderer((t, v, sel, foc, r, c) -> {
                  if (v == null) return new JLabel();
                  Color[] cols = UIColors.confederacionColors(v.toString().trim());
                  JLabel badge = UIFactory.badge("  " + v + "  ", cols[0], cols[1]);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

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
                  List<Equipo> equipos = gestion.listarEquipos();
                  String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  String conf   = confFilter  != null && confFilter.getSelectedIndex() > 0
                  ? (String) confFilter.getSelectedItem() : "";
                  int idx = 1;
                  for (Equipo e : equipos) {
                        if (!search.isEmpty() && !e.getNombre().toLowerCase().contains(search)) continue;
                        String sigla = e.getConfederacion() != null ? e.getConfederacion().getSigla() : "";
                        if (!conf.isEmpty() && !conf.equals(sigla)) continue;
                        String pais = e.getPais() != null ? e.getPais().getNombre() : "-";
                        String dt   = e.getDirectorTecnico() != null
                              ? e.getDirectorTecnico().getNombre() + " " + e.getDirectorTecnico().getApellido() : "-";
                        model.addRow(new Object[]{idx++, e.getNombre(), sigla, pais, dt, "#" + e.getRankingFifa(), ""});
                  }
            } catch (Exception ex) {
                  showError("No se pudieron cargar los equipos: " + ex.getMessage());
            }
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            p.setOpaque(false);

            JButton edit = smallBtn("✏", UIColors.BLUE, UIColors.INFO_BG);
            edit.addActionListener(e -> { if (row < model.getRowCount()) showTeamForm(row); });

            JButton del = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
            del.addActionListener(e -> {
                  if (row >= model.getRowCount()) return;
                  String name = model.getValueAt(row, 1).toString();
                  int c = JOptionPane.showConfirmDialog(this,
                  "¿Eliminar el equipo \"" + name + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
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

      private void showTeamForm(Integer editRow) {
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow == null ? "Nuevo Equipo" : "Editar Equipo",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(460, 400);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
            form.setBackground(Color.WHITE);
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

            JTextField nameF  = UIFactory.textField("Ej: Argentina");
            JComboBox<String> confBox = UIFactory.comboBox("UEFA","CONMEBOL","CONCACAF","CAF","AFC","OFC");
            JTextField paisF  = UIFactory.textField("Ej: Argentina");
            JTextField dtF    = UIFactory.textField("Ej: Lionel Scaloni");
            JTextField rankF  = UIFactory.textField("Ej: 1");

            if (editRow != null) {
                  nameF.setText(model.getValueAt(editRow, 1).toString());
                  confBox.setSelectedItem(model.getValueAt(editRow, 2).toString());
                  paisF.setText(model.getValueAt(editRow, 3).toString());
                  dtF.setText(model.getValueAt(editRow, 4).toString());
            }

            form.add(UIFactory.formLabel("Nombre del Equipo")); form.add(nameF);
            form.add(UIFactory.formLabel("Confederación"));     form.add(confBox);
            form.add(UIFactory.formLabel("País"));              form.add(paisF);
            form.add(UIFactory.formLabel("Director Técnico"));  form.add(dtF);
            form.add(UIFactory.formLabel("Ranking FIFA"));      form.add(rankF);

            JPanel footer = buildDialogFooter();
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = UIFactory.primaryButton(editRow == null ? "Guardar" : "Actualizar");
            save.addActionListener(e -> {
                  String nombre = nameF.getText().trim();
                  if (nombre.isEmpty()) { showError("El nombre es obligatorio."); return; }
                  if (editRow != null) {
                  model.setValueAt(nombre, editRow, 1);
                  model.setValueAt(confBox.getSelectedItem(), editRow, 2);
                  model.setValueAt(paisF.getText(), editRow, 3);
                  model.setValueAt(dtF.getText(), editRow, 4);
                  } else {
                  model.addRow(new Object[]{model.getRowCount()+1, nombre,
                        confBox.getSelectedItem(), paisF.getText(), dtF.getText(),
                        "#" + rankF.getText(), ""});
                  }
                  dlg.dispose();
            });
            footer.add(cancel); footer.add(save);

            dlg.add(form,   BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private JPanel buildDialogFooter() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
            p.setBackground(new Color(0xF8F9FC));
            p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));
            return p;
      }

      private void showError(String msg) {
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
      }
}