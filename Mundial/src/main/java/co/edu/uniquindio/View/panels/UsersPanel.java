package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.model.Enum.TipoUsuario;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.SistemaSeguridadService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsersPanel extends JPanel {

      private final GestionDatosService gestion;
      private final SistemaSeguridadService seguridad;

      private JTable table;
      private DefaultTableModel model;
      private JTextField searchField;

      private static final String[] COLUMNS = { "#", "Usuario", "Tipo", "Acciones" };

      public UsersPanel(GestionDatosService gestion, SistemaSeguridadService seguridad) {
            this.gestion = gestion;
            this.seguridad = seguridad;
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
            content.add(buildStatsRow());
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
            left.add(UIFactory.heading("Usuarios"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Gestión de usuarios del sistema"));

            JButton addBtn = UIFactory.primaryButton("+ Nuevo Usuario");
            addBtn.addActionListener(e -> showUserForm(null));

            p.add(left, BorderLayout.WEST);
            p.add(addBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildStatsRow() {
            JPanel p = new JPanel(new GridLayout(1, 3, 14, 0));
            p.setOpaque(false);
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

            int[] counts = {0, 0, 0};
            try {
                  for (Usuario u : gestion.listarUsuarios()) {
                  switch (u.getTipoUsuario()) {
                        case ADMINISTRADOR -> counts[0]++;
                        case TRADICIONAL -> counts[1]++;
                        case ESPORADICO -> counts[2]++;
                  }
                  }
            } catch (Exception ignored) {}

            p.add(UIFactory.statCard(String.valueOf(counts[0]), "Administradores",       UIColors.PURPLE,    "⚙"));
            p.add(UIFactory.statCard(String.valueOf(counts[1]), "Usuarios Tradicionales", UIColors.BLUE,     "👤"));
            p.add(UIFactory.statCard(String.valueOf(counts[2]), "Usuarios Esporádicos",   UIColors.TURQUOISE,"👥"));
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

            searchField = UIFactory.textField("Buscar usuario...");
            JComboBox<String> tipoFilter = UIFactory.comboBox(
                  "Todos los tipos", "ADMINISTRADOR", "TRADICIONAL", "ESPORADICO");
            JButton btn = UIFactory.primaryButton("Buscar");
            btn.addActionListener(e -> loadData());

            row.add(searchField); row.add(tipoFilter); row.add(btn);
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
            th.add(UIFactory.sectionTitle("Usuarios Registrados"), BorderLayout.WEST);

            JLabel warning = new JLabel("⚠  Solo el Administrador puede gestionar usuarios");
            warning.setFont(UIFonts.BODY_SM);
            warning.setForeground(UIColors.WARNING_FG);
            th.add(warning, BorderLayout.EAST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 3; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(44);
            table.getColumnModel().getColumn(2).setMaxWidth(160);
            table.getColumnModel().getColumn(3).setMaxWidth(140);

            table.getColumnModel().getColumn(2).setCellRenderer((t, v, sel, foc, r, c) -> {
                  JLabel badge = UIFactory.statusBadge(v != null ? v.toString() : "");
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE
                  : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            table.getColumnModel().getColumn(3).setCellRenderer((t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
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
                  List<Usuario> usuarios = gestion.listarUsuarios();
                  String filtro = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  int idx = 1;
                  for (Usuario u : usuarios) {
                        if (!filtro.isEmpty() && !u.getNombreUsuario().toLowerCase().contains(filtro)) continue;
                        model.addRow(new Object[]{
                              idx++, u.getNombreUsuario(), u.getTipoUsuario().name(), ""
                        });
                  }
            } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this,
                  "Error al cargar usuarios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
      }


      private void showUserForm(Integer editRow) {
            boolean esNuevo = (editRow == null);
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  esNuevo ? "Nuevo Usuario" : "Editar Usuario",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(420, esNuevo ? 300 : 260);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
            form.setBackground(Color.WHITE);
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

            JTextField userF = UIFactory.textField("Ej: juan2026");
            JPasswordField passF = UIFactory.passwordField("Mínimo 4 caracteres");
            JComboBox<String> tipoBox = UIFactory.comboBox("TRADICIONAL", "ESPORADICO");

            if (!esNuevo) {
                  userF.setText(model.getValueAt(editRow, 1).toString());
                  tipoBox.setSelectedItem(model.getValueAt(editRow, 2).toString());
            }

            form.add(UIFactory.formLabel("Nombre de Usuario")); form.add(userF);
            if (esNuevo) {
                  form.add(UIFactory.formLabel("Contraseña")); form.add(passF);
            }
            form.add(UIFactory.formLabel("Tipo"));             form.add(tipoBox);

            JPanel footer = buildFooter();
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = UIFactory.primaryButton(esNuevo ? "Crear" : "Guardar");
            save.addActionListener(e -> {
                  String nombre = userF.getText().trim();
                  if (nombre.isEmpty()) {
                  JOptionPane.showMessageDialog(dlg, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                  return;
                  }
                  try {
                        if (esNuevo) {
                              String pass = new String(passF.getPassword());
                              TipoUsuario tipo = TipoUsuario.valueOf(tipoBox.getSelectedItem().toString());
                              seguridad.crearUsuario(nombre, pass, tipo);
                              loadData();
                        } else {
                              model.setValueAt(nombre, editRow, 1);
                              model.setValueAt(tipoBox.getSelectedItem(), editRow, 2);
                        }
                        dlg.dispose();
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });
            footer.add(cancel); footer.add(save);
            dlg.add(form,   BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private void showChangePasswordDialog(int row) {
            String nombreUsuario = model.getValueAt(row, 1).toString();
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  "Cambiar Contraseña — " + nombreUsuario,
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(400, 220);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
            form.setBackground(Color.WHITE);
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

            JPasswordField nueva = UIFactory.passwordField("Nueva contraseña");
            JPasswordField nueva2 = UIFactory.passwordField("Confirmar contraseña");
            form.add(UIFactory.formLabel("Nueva Contraseña"));     form.add(nueva);
            form.add(UIFactory.formLabel("Confirmar Contraseña")); form.add(nueva2);

            JPanel footer = buildFooter();
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = UIFactory.primaryButton("Cambiar");
            save.addActionListener(e -> {
                  String p1 = new String(nueva.getPassword());
                  String p2 = new String(nueva2.getPassword());
                  if (!p1.equals(p2)) {
                  JOptionPane.showMessageDialog(dlg, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                  return;
                  }
                  try {
                  List<Usuario> todos = gestion.listarUsuarios();
                  todos.stream()
                        .filter(u -> u.getNombreUsuario().equals(nombreUsuario))
                        .findFirst()
                        .ifPresent(u -> {
                              try { seguridad.cambiarContrasena(u.getIdUsuario(), p1); }
                              catch (Exception ex) { throw new RuntimeException(ex); }
                        });
                  JOptionPane.showMessageDialog(dlg, "Contraseña actualizada.");
                  dlg.dispose();
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });
            footer.add(cancel); footer.add(save);
            dlg.add(form,   BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 4));
            p.setOpaque(false);

            JButton edit = smallBtn("✏", UIColors.BLUE, UIColors.INFO_BG);
            edit.setToolTipText("Editar");
            edit.addActionListener(e -> { if (row < model.getRowCount()) showUserForm(row); });

            JButton pwd = smallBtn("🔑", UIColors.TURQUOISE, new Color(0xE0F7FA));
            pwd.setToolTipText("Cambiar contraseña");
            pwd.addActionListener(e -> { if (row < model.getRowCount()) showChangePasswordDialog(row); });

            JButton del = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
            del.setToolTipText("Eliminar");
            del.addActionListener(e -> {
                  if (row >= model.getRowCount()) return;
                  String nombre = model.getValueAt(row, 1).toString();
                  int c = JOptionPane.showConfirmDialog(this,
                  "¿Eliminar el usuario \"" + nombre + "\"?",
                  "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                  if (c != JOptionPane.YES_OPTION) return;
                  try {
                  List<Usuario> todos = gestion.listarUsuarios();
                  todos.stream()
                        .filter(u -> u.getNombreUsuario().equals(nombre))
                        .findFirst()
                        .ifPresent(u -> {
                              try { gestion.eliminarUsuario(u.getIdUsuario()); }
                              catch (Exception ex) { throw new RuntimeException(ex); }
                        });
                  model.removeRow(row);
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });

            p.add(edit); p.add(pwd); p.add(del);
            return p;
      }

      private JButton smallBtn(String text, Color fg, Color bg) {
            JButton btn = new JButton(text);
            btn.setFont(UIFonts.BODY_SM);
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
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