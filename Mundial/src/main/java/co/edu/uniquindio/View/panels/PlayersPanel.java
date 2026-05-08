package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class PlayersPanel extends JPanel {

      private final GestionDatosService gestion;
      private JTable table;
      private DefaultTableModel model;

      private static final String[] COLUMNS = {
            "#", "Jugador", "Equipo", "Posición", "Edad", "Estatura", "Peso", "Valor Mercado", "Acciones"
      };

      public PlayersPanel(GestionDatosService gestion) {
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
            left.add(UIFactory.heading("Gestión de Jugadores"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Administra los jugadores del Mundial FIFA 2026"));

            JButton addBtn = UIFactory.tealButton("+ Agregar Jugador");
            addBtn.addActionListener(e -> showPlayerForm(null));

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

            JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
            row.setOpaque(false);

            JTextField search = UIFactory.textField("Buscar jugador...");
            JComboBox<String> posFilter = UIFactory.comboBox(
                  "Todas las posiciones", "Portero", "Defensa", "Centrocampista", "Delantero");
            JComboBox<String> teamFilter = UIFactory.comboBox(
                  "Todos los equipos", "Argentina", "Brasil", "Francia", "España", "México");
            JButton btn = UIFactory.tealButton("Buscar");
            btn.addActionListener(e -> loadData());

            row.add(search); row.add(posFilter); row.add(teamFilter); row.add(btn);
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
            tableHeader.add(UIFactory.sectionTitle("Jugadores Registrados"), BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 8; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(4).setMaxWidth(50);
            table.getColumnModel().getColumn(5).setMaxWidth(70);
            table.getColumnModel().getColumn(6).setMaxWidth(60);
            table.getColumnModel().getColumn(8).setMaxWidth(120);

            table.getColumnModel().getColumn(7).setCellRenderer((t, v, sel, foc, r, c) -> {
                  JLabel l = new JLabel(v != null ? v.toString() : "");
                  l.setFont(UIFonts.LABEL_BOLD);
                  l.setForeground(sel ? UIColors.PURPLE : UIColors.BLUE);
                  l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                  return l;
            });

            table.getColumnModel().getColumn(3).setCellRenderer((t, v, sel, foc, r, c) -> {
                  String pos = v != null ? v.toString() : "";
                  Color bg, fg;
                  switch (pos) {
                  case "Portero"       -> { bg = new Color(0xF3E8FF); fg = new Color(0x7C3AED); }
                  case "Defensa"       -> { bg = UIColors.SUCCESS_BG; fg = UIColors.SUCCESS_FG; }
                  case "Centrocampista"-> { bg = UIColors.INFO_BG; fg = UIColors.INFO_FG; }
                  default              -> { bg = UIColors.ERROR_BG; fg = UIColors.RED; }
                  }
                  JLabel badge = UIFactory.badge(pos, bg, fg);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE : (r%2==0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            table.getColumnModel().getColumn(8).setCellRenderer((t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                  @Override public Component getTableCellEditorComponent(
                        JTable t, Object v, boolean sel, int row, int col) {
                  return buildActionBtns(row);
                  }
                  @Override public Object getCellEditorValue() { return ""; }
            });

            card.add(tableHeader, BorderLayout.NORTH);
            card.add(UIFactory.scrollPane(table), BorderLayout.CENTER);
            return card;
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
            p.setOpaque(false);
            JButton edit = new JButton("✏"); styleActionBtn(edit, UIColors.BLUE, UIColors.INFO_BG);
            edit.addActionListener(e -> showPlayerForm(row));
            JButton del  = new JButton("🗑"); styleActionBtn(del, UIColors.RED, UIColors.ERROR_BG);
            del.addActionListener(e -> {
                  if (row < model.getRowCount()) {
                  int c = JOptionPane.showConfirmDialog(this, "¿Eliminar jugador?", "Confirmar", JOptionPane.YES_NO_OPTION);
                  if (c == JOptionPane.YES_OPTION) model.removeRow(row);
                  }
            });
            p.add(edit); p.add(del);
            return p;
      }

      private void styleActionBtn(JButton btn, Color fg, Color bg) {
            btn.setFont(UIFonts.BODY_SM);
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      private void loadData() {
            model.setRowCount(0);
            try {
                  List<Jugador> jugadores = gestion.listarJugadores();
                  int idx = 1;
                  for (Jugador j : jugadores) {
                  model.addRow(new Object[]{
                        idx++,
                        j.getNombre() + " " + j.getApellido(),
                        j.getEquipo() != null ? j.getEquipo().getNombre() : "-",
                        j.getPosicion() != null ? j.getPosicion().getNombre() : "-",
                        j.getEdad(),
                        j.getEstatura() + "m",
                        j.getPeso() + "kg",
                        "€" + j.getValorMercado() + "M",
                        ""
                  });
                  }
            } catch (Exception ex) {
                  loadDemoData();
            }
      }

      private void loadDemoData() {
            model.setRowCount(0);
            Object[][] demo = {
                  {1, "Lionel Messi",     "Argentina", "Delantero",      38, "1.70m", "67kg",  "€30M",  ""},
                  {2, "Kylian Mbappé",    "Francia",   "Delantero",      27, "1.78m", "73kg",  "€180M", ""},
                  {3, "Erling Haaland",   "Noruega",   "Delantero",      25, "1.95m", "88kg",  "€150M", ""},
                  {4, "Vinicius Junior",  "Brasil",    "Delantero",      25, "1.76m", "73kg",  "€200M", ""},
                  {5, "Jude Bellingham",  "Inglaterra","Centrocampista",  22, "1.86m", "75kg",  "€180M", ""},
                  {6, "Pedri González",   "España",    "Centrocampista",  23, "1.74m", "60kg",  "€120M", ""},
                  {7, "Florian Wirtz",    "Alemania",  "Centrocampista",  22, "1.76m", "70kg",  "€150M", ""},
                  {8, "Jamal Musiala",    "Alemania",  "Centrocampista",  22, "1.76m", "70kg",  "€130M", ""},
            };
            for (Object[] row : demo) model.addRow(row);
      }

      private void showPlayerForm(Integer editRow) {
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow == null ? "Agregar Jugador" : "Editar Jugador", Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(500, 480);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
            form.setBackground(Color.WHITE);

            JTextField nameF    = UIFactory.textField("Ej: Lionel Messi");
            JComboBox<String> posBox = UIFactory.comboBox("Portero", "Defensa", "Centrocampista", "Delantero");
            JComboBox<String> teamBox = UIFactory.comboBox("Argentina","Brasil","Francia","España","México","Alemania");
            JTextField ageF     = UIFactory.textField("Ej: 38");
            JTextField estF     = UIFactory.textField("Ej: 1.70");
            JTextField pesoF    = UIFactory.textField("Ej: 67");
            JTextField valorF   = UIFactory.textField("Ej: 30");
            JTextField fechaF   = UIFactory.textField("Ej: 1987-06-24");

            if (editRow != null) {
                  nameF.setText(model.getValueAt(editRow, 1).toString());
                  posBox.setSelectedItem(model.getValueAt(editRow, 3).toString());
                  teamBox.setSelectedItem(model.getValueAt(editRow, 2).toString());
            }

            form.add(UIFactory.formLabel("Nombre Completo")); form.add(nameF);
            form.add(UIFactory.formLabel("Posición"));        form.add(posBox);
            form.add(UIFactory.formLabel("Equipo"));          form.add(teamBox);
            form.add(UIFactory.formLabel("Fecha Nacimiento")); form.add(fechaF);
            form.add(UIFactory.formLabel("Edad"));            form.add(ageF);
            form.add(UIFactory.formLabel("Estatura (m)"));    form.add(estF);
            form.add(UIFactory.formLabel("Peso (kg)"));       form.add(pesoF);
            form.add(UIFactory.formLabel("Valor Mercado (M€)")); form.add(valorF);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 16));
            footer.setBackground(new Color(0xF8F9FC));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());

            JButton save = UIFactory.tealButton("Guardar Jugador");
            save.addActionListener(e -> {
                  if (editRow != null) {
                  model.setValueAt(nameF.getText(), editRow, 1);
                  model.setValueAt(teamBox.getSelectedItem(), editRow, 2);
                  model.setValueAt(posBox.getSelectedItem(), editRow, 3);
                  } else {
                  model.addRow(new Object[]{model.getRowCount()+1, nameF.getText(),
                        teamBox.getSelectedItem(), posBox.getSelectedItem(),
                        ageF.getText(), estF.getText()+"m", pesoF.getText()+"kg",
                        "€"+valorF.getText()+"M", ""});
                  }
                  dlg.dispose();
            });

            footer.add(cancel); footer.add(save);
            dlg.add(form, BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }
}
