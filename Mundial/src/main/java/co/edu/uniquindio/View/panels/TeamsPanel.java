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
            left.add(UIFactory.heading("Gestión de Equipos"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Administra los 48 equipos del Mundial FIFA 2026"));

            JButton addBtn = UIFactory.primaryButton("+ Crear Equipo");
            addBtn.addActionListener(e -> showTeamForm(null));

            p.add(left, BorderLayout.WEST);
            p.add(addBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildFilters() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.WHITE);
            p.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)
            ));

            JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
            row.setOpaque(false);

            searchField = UIFactory.textField("Buscar equipo...");
            JComboBox<String> confFilter = UIFactory.comboBox(
                  "Todas las confederaciones", "UEFA", "CONMEBOL", "CONCACAF", "CAF", "AFC", "OFC");
            JButton searchBtn = UIFactory.primaryButton("Buscar");
            searchBtn.addActionListener(e -> loadData());

            row.add(searchField);
            row.add(confFilter);
            row.add(searchBtn);

            p.add(row, BorderLayout.CENTER);
            return p;
      }

      private JPanel buildTable() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel tableHeader = new JPanel(new BorderLayout());
            tableHeader.setBackground(new Color(0xF8F9FC));
            tableHeader.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)
            ));
            JLabel headerTitle = UIFactory.sectionTitle("Equipos Registrados");
            tableHeader.add(headerTitle, BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 6; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(5).setMaxWidth(110);
            table.getColumnModel().getColumn(6).setMaxWidth(120);

            table.getColumnModel().getColumn(2).setCellRenderer((t, v, sel, foc, r, c) -> {
                  if (v == null) return new JLabel();
                  String sigla = v.toString().trim();
                  Color[] colors = UIColors.confederacionColors(sigla);
                  JLabel badge = UIFactory.badge("  " + sigla + "  ", colors[0], colors[1]);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE : (r%2==0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            table.getColumnModel().getColumn(6).setCellRenderer((t, v, sel, foc, r, c) ->
                  buildActionButtons(r));
            table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                  private int editRow;
                  @Override public Component getTableCellEditorComponent(
                        JTable t, Object v, boolean sel, int row, int col) {
                  editRow = row;
                  return buildActionButtons(row);
                  }
                  @Override public Object getCellEditorValue() { return ""; }
            });

            JScrollPane sp = UIFactory.scrollPane(table);

            card.add(tableHeader, BorderLayout.NORTH);
            card.add(sp, BorderLayout.CENTER);
            return card;
      }

      private JPanel buildActionButtons(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
            p.setOpaque(false);

            JButton edit = new JButton("✏");
            edit.setFont(UIFonts.BODY_SM);
            edit.setForeground(UIColors.BLUE);
            edit.setBackground(UIColors.INFO_BG);
            edit.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            edit.setFocusPainted(false);
            edit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            edit.addActionListener(e -> {
                  if (row < model.getRowCount()) showTeamForm(row);
            });

            JButton del = new JButton("🗑");
            del.setFont(UIFonts.BODY_SM);
            del.setForeground(UIColors.RED);
            del.setBackground(UIColors.ERROR_BG);
            del.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            del.setFocusPainted(false);
            del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            del.addActionListener(e -> {
                  if (row < model.getRowCount()) deleteTeam(row);
            });

            p.add(edit);
            p.add(del);
            return p;
      }

      private void loadData() {
            model.setRowCount(0);
            try {
                  List<Equipo> equipos = gestion.listarEquipos();
                  String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  int idx = 1;
                  for (Equipo e : equipos) {
                  if (!search.isEmpty() && !e.getNombre().toLowerCase().contains(search)) continue;
                  String conf = e.getConfederacion() != null ? e.getConfederacion().getSigla() : "-";
                  String pais = e.getPais() != null ? e.getPais().getNombre() : "-";
                  String dt   = e.getDirectorTecnico() != null
                        ? e.getDirectorTecnico().getNombre() + " " + e.getDirectorTecnico().getApellido() : "-";
                  model.addRow(new Object[]{idx++, e.getNombre(), conf, pais, dt, "#" + e.getRankingFifa(), ""});
                  }
            } catch (Exception ex) {
                  loadDemoData();
            }
      }

      private void loadDemoData() {
            model.setRowCount(0);
            Object[][] demo = {
                  {1, "Argentina",     "CONMEBOL", "Argentina",      "Lionel Scaloni",      "#1",  ""},
                  {2, "Francia",       "UEFA",     "Francia",        "Didier Deschamps",    "#2",  ""},
                  {3, "Brasil",        "CONMEBOL", "Brasil",         "Dorival Junior",      "#4",  ""},
                  {4, "España",        "UEFA",     "España",         "Luis de la Fuente",   "#3",  ""},
                  {5, "Inglaterra",    "UEFA",     "Inglaterra",     "Gareth Southgate",    "#5",  ""},
                  {6, "México",        "CONCACAF", "México",         "Hernán Lozano",       "#12", ""},
                  {7, "Marruecos",     "CAF",      "Marruecos",      "Walid Regragui",      "#14", ""},
                  {8, "Japón",         "AFC",      "Japón",          "Hajime Moriyasu",     "#23", ""},
            };
            for (Object[] row : demo) model.addRow(row);
      }

      private void showTeamForm(Integer editRow) {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow == null ? "Crear Equipo" : "Editar Equipo", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(480, 420);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
            form.setBackground(Color.WHITE);

            JTextField nameField = UIFactory.textField("Ej: Argentina");
            JComboBox<String> confBox = UIFactory.comboBox("UEFA", "CONMEBOL", "CONCACAF", "CAF", "AFC", "OFC");
            JTextField paisField = UIFactory.textField("Ej: Argentina");
            JTextField dtField   = UIFactory.textField("Ej: Lionel Scaloni");
            JTextField rankField = UIFactory.textField("Ej: 1");
            JTextField valorField= UIFactory.textField("Ej: $850M");

            if (editRow != null) {
                  nameField.setText(model.getValueAt(editRow, 1).toString());
                  confBox.setSelectedItem(model.getValueAt(editRow, 2).toString());
                  paisField.setText(model.getValueAt(editRow, 3).toString());
                  dtField.setText(model.getValueAt(editRow, 4).toString());
            }

            form.add(UIFactory.formLabel("Nombre del Equipo")); form.add(nameField);
            form.add(UIFactory.formLabel("Confederación"));     form.add(confBox);
            form.add(UIFactory.formLabel("País"));              form.add(paisField);
            form.add(UIFactory.formLabel("Director Técnico")); form.add(dtField);
            form.add(UIFactory.formLabel("Ranking FIFA"));      form.add(rankField);
            form.add(UIFactory.formLabel("Valor Total"));       form.add(valorField);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 16));
            footer.setBackground(new Color(0xF8F9FC));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dialog.dispose());

            JButton save = UIFactory.primaryButton("Guardar Equipo");
            save.addActionListener(e -> {
                  if (editRow != null) {
                  model.setValueAt(nameField.getText(), editRow, 1);
                  model.setValueAt(confBox.getSelectedItem(), editRow, 2);
                  model.setValueAt(paisField.getText(), editRow, 3);
                  model.setValueAt(dtField.getText(), editRow, 4);
                  } else {
                  model.addRow(new Object[]{model.getRowCount()+1, nameField.getText(),
                        confBox.getSelectedItem(), paisField.getText(), dtField.getText(),
                        "#"+rankField.getText(), ""});
                  }
                  dialog.dispose();
            });

            footer.add(cancel);
            footer.add(save);

            dialog.add(form, BorderLayout.CENTER);
            dialog.add(footer, BorderLayout.SOUTH);
            dialog.setVisible(true);
      }

      private void deleteTeam(int row) {
            String name = model.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                  "¿Eliminar el equipo \"" + name + "\"?", "Confirmar eliminación",
                  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) model.removeRow(row);
      }
}
