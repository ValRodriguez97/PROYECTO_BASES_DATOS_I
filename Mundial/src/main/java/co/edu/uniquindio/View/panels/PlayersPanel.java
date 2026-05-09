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
      private JTextField searchField;
      private JComboBox<String> posFilter;

      private static final String[] COLUMNS = {
            "#", "Jugador", "Equipo", "Posición", "Edad", "Estatura", "Peso", "Valor (M€)", "Acciones"
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
            left.add(UIFactory.heading("Jugadores"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Gestión de jugadores registrados en el torneo"));

            JButton addBtn = UIFactory.tealButton("+ Nuevo Jugador");
            addBtn.addActionListener(e -> showPlayerForm(null));

            p.add(left, BorderLayout.WEST);
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

            searchField = UIFactory.textField("Buscar jugador...");
            posFilter = UIFactory.comboBox("Todas las posiciones", "Portero", "Defensa", "Centrocampista", "Delantero");
            JButton btn = UIFactory.tealButton("Buscar");
            btn.addActionListener(e -> loadData());

            row.add(searchField); row.add(posFilter); row.add(btn);
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
            th.add(UIFactory.sectionTitle("Jugadores Registrados"), BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 8; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(44);
            table.getColumnModel().getColumn(4).setMaxWidth(52);
            table.getColumnModel().getColumn(5).setMaxWidth(80);
            table.getColumnModel().getColumn(6).setMaxWidth(70);
            table.getColumnModel().getColumn(7).setPreferredWidth(90);
            table.getColumnModel().getColumn(8).setMaxWidth(110);

            table.getColumnModel().getColumn(3).setCellRenderer((t, v, sel, foc, r, c) -> {
                  String pos = v != null ? v.toString() : "";
                  Color bg, fg;
                  switch (pos) {
                  case "Portero" -> { bg = new Color(0xF3E8FF); fg = new Color(0x6D28D9); }
                  case "Defensa" -> { bg = UIColors.SUCCESS_BG; fg = UIColors.SUCCESS_FG; }
                  case "Centrocampista" -> { bg = UIColors.INFO_BG;    fg = UIColors.INFO_FG;    }
                  default -> { bg = UIColors.ERROR_BG;   fg = UIColors.RED;        }
                  }
                  JLabel badge = UIFactory.badge(pos, bg, fg);
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            table.getColumnModel().getColumn(7).setCellRenderer((t, v, sel, foc, r, c) -> {
                  JLabel l = new JLabel(v != null ? v.toString() : "", SwingConstants.RIGHT);
                  l.setFont(UIFonts.LABEL_BOLD);
                  l.setForeground(sel ? UIColors.PURPLE : UIColors.BLUE);
                  l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 14));
                  return l;
            });

            table.getColumnModel().getColumn(8).setCellRenderer((t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
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
                  List<Jugador> jugadores = gestion.listarJugadores();
                  String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  String pos    = posFilter   != null && posFilter.getSelectedIndex() > 0
                  ? (String) posFilter.getSelectedItem() : "";
                  int idx = 1;
                  for (Jugador j : jugadores) {
                        String posNom = j.getPosicion() != null ? j.getPosicion().getNombre() : "";
                        if (!search.isEmpty()
                              && !j.getNombre().toLowerCase().contains(search)
                              && !j.getApellido().toLowerCase().contains(search)) continue;
                        if (!pos.isEmpty() && !pos.equals(posNom)) continue;

                        model.addRow(new Object[]{
                              idx++,
                              j.getNombre() + " " + j.getApellido(),
                              j.getEquipo()   != null ? j.getEquipo().getNombre()   : "-",
                              posNom,
                              j.getEdad(),
                              j.getEstatura() + " m",
                              j.getPeso()     + " kg",
                              j.getValorMercado(),
                              ""
                        });
                  }
            } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this,
                  "Error al cargar jugadores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            p.setOpaque(false);
            JButton edit = smallBtn("✏", UIColors.BLUE, UIColors.INFO_BG);
            edit.addActionListener(e -> { if (row < model.getRowCount()) showPlayerForm(row); });
            JButton del = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
            del.addActionListener(e -> {
                  if (row >= model.getRowCount()) return;
                  int c = JOptionPane.showConfirmDialog(this,
                  "¿Eliminar este jugador?", "Confirmar", JOptionPane.YES_NO_OPTION);
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

      private void showPlayerForm(Integer editRow) {
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow == null ? "Nuevo Jugador" : "Editar Jugador",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(480, 460);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
            form.setBackground(Color.WHITE);
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

            List<Equipo> equipos = List.of();
            try { equipos = gestion.listarEquipos(); } catch (Exception ignored) {}
            String[] equipoNoms = equipos.stream().map(Equipo::getNombre).toArray(String[]::new);

            JTextField nameF = UIFactory.textField("Ej: Lionel");
            JTextField apF = UIFactory.textField("Ej: Messi");
            JTextField fechaF = UIFactory.textField("Ej: 1987-06-24");
            JTextField estF = UIFactory.textField("Ej: 1.70");
            JTextField pesoF = UIFactory.textField("Ej: 67");
            JTextField valorF = UIFactory.textField("Ej: 30.00");
            JComboBox<String> posBox = UIFactory.comboBox("Portero","Defensa","Centrocampista","Delantero");
            JComboBox<String> teamBox = equipoNoms.length > 0
                  ? UIFactory.comboBox(equipoNoms)
                  : UIFactory.comboBox("(sin equipos registrados)");

            if (editRow != null) {
                  String full = model.getValueAt(editRow, 1).toString();
                  String[] parts = full.split(" ", 2);
                  nameF.setText(parts[0]);
                  if (parts.length > 1) apF.setText(parts[1]);
                  posBox.setSelectedItem(model.getValueAt(editRow, 3).toString());
            }

            form.add(UIFactory.formLabel("Nombre")); form.add(nameF);
            form.add(UIFactory.formLabel("Apellido")); form.add(apF);
            form.add(UIFactory.formLabel("Fecha Nacimiento")); form.add(fechaF);
            form.add(UIFactory.formLabel("Posición")); form.add(posBox);
            form.add(UIFactory.formLabel("Equipo")); form.add(teamBox);
            form.add(UIFactory.formLabel("Estatura (m)")); form.add(estF);
            form.add(UIFactory.formLabel("Peso (kg)")); form.add(pesoF);
            form.add(UIFactory.formLabel("Valor Mercado (M€)")); form.add(valorF);

            JPanel footer = buildDialogFooter();
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = UIFactory.tealButton(editRow == null ? "Guardar" : "Actualizar");
            save.addActionListener(e -> {
                  String nombre = nameF.getText().trim() + " " + apF.getText().trim();
                  if (nombre.isBlank()) return;
                  if (editRow != null) {
                  model.setValueAt(nombre.trim(), editRow, 1);
                  model.setValueAt(posBox.getSelectedItem(), editRow, 3);
                  } else {
                  model.addRow(new Object[]{
                        model.getRowCount() + 1,
                        nombre.trim(),
                        teamBox.getSelectedItem(),
                        posBox.getSelectedItem(),
                        "-",
                        estF.getText() + " m",
                        pesoF.getText() + " kg",
                        valorF.getText(),
                        ""
                  });
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
}