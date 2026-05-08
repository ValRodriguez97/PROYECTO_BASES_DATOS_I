package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.model.Enum.TipoUsuario;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.SistemaSeguridadService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

      public class UsersPanel extends JPanel {

      private final GestionDatosService gestion;
      private final SistemaSeguridadService seguridad;

      private JTable table;
      private DefaultTableModel model;
      private JTextField searchField;

      private static final String[] COLUMNS = {
            "#", "Usuario", "Tipo", "Acciones"
      };

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
            content.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
            content.add(buildHeader());
            content.add(Box.createVerticalStrut(20));
            content.add(buildStatsRow());
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
            left.add(UIFactory.heading("Gestión de Usuarios"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Administra los usuarios del sistema FIFA World Cup 2026"));

            JButton addBtn = purpleButton("+ Crear Usuario");
            addBtn.addActionListener(e -> showUserForm(null));

            p.add(left, BorderLayout.WEST);
            p.add(addBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildStatsRow() {
            JPanel p = new JPanel(new GridLayout(1, 3, 16, 0));
            p.setOpaque(false);

            int[] counts = {0, 0, 0};
            try {
                  List<Usuario> todos = gestion.listarUsuarios();
                  for (Usuario u : todos) {
                        switch (u.getTipoUsuario()) {
                              case ADMINISTRADOR -> counts[0]++;
                              case TRADICIONAL -> counts[1]++;
                              case ESPORADICO -> counts[2]++;
                        }
                  }
            } catch (Exception ignored) {}

            p.add(UIFactory.statCard(String.valueOf(counts[0]), "Administradores", UIColors.PURPLE, "⚙"));
            p.add(UIFactory.statCard(String.valueOf(counts[1]), "Usuarios Tradicionales", UIColors.BLUE, "👤"));
            p.add(UIFactory.statCard(String.valueOf(counts[2]), "Usuarios Esporádicos", UIColors.TURQUOISE, "👥"));
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

            searchField = UIFactory.textField("Buscar usuario...");
            JComboBox<String> tipoFilter = UIFactory.comboBox(
                  "Todos los tipos", "ADMINISTRADOR", "TRADICIONAL", "ESPORADICO");
            JButton btn = purpleButton("Buscar");
            btn.addActionListener(e -> loadData());

            row.add(searchField);
            row.add(tipoFilter);
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
            th.add(UIFactory.sectionTitle("Usuarios Registrados"), BorderLayout.WEST);

            JLabel warning = new JLabel("⚠ Solo el Administrador puede gestionar usuarios");
            warning.setFont(UIFonts.BODY_SM);
            warning.setForeground(UIColors.WARNING_FG);
            th.add(warning, BorderLayout.EAST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 3; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(2).setMaxWidth(160);
            table.getColumnModel().getColumn(3).setMaxWidth(140);

            table.getColumnModel().getColumn(2).setCellRenderer((t, v, sel, foc, r, c) -> {
                  JLabel badge = UIFactory.statusBadge(v != null ? v.toString() : "");
                  badge.setOpaque(true);
                  badge.setBackground(sel ? UIColors.PURPLE_PALE
                  : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
                  JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                  wrap.setBackground(badge.getBackground());
                  wrap.add(badge);
                  return wrap;
            });

            table.getColumnModel().getColumn(3).setCellRenderer(
                  (t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(3).setCellEditor(
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
                  List<Usuario> usuarios = gestion.listarUsuarios();
                  String filtro = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  int idx = 1;
                  for (Usuario u : usuarios) {
                  if (!filtro.isEmpty() && !u.getNombreUsuario().toLowerCase().contains(filtro)) continue;
                  model.addRow(new Object[]{
                        idx++,
                        u.getNombreUsuario(),
                        u.getTipoUsuario().name(),
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
                  {1, "admin", "ADMINISTRADOR", ""},
                  {2, "usuario1", "TRADICIONAL", ""},
                  {3, "guest1", "ESPORADICO", ""},
                  {4, "usuario2", "TRADICIONAL", ""},
            };
            for (Object[] row : demo) model.addRow(row);
      }

      private void showUserForm(Integer editRow) {
            boolean esNuevo = (editRow == null);

            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  esNuevo ? "Crear Usuario" : "Editar Usuario",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(440, esNuevo ? 320 : 280);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
            form.setBackground(Color.WHITE);

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
            form.add(UIFactory.formLabel("Tipo de Usuario")); form.add(tipoBox);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 16));
            footer.setBackground(new Color(0xF8F9FC));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());

            JButton save = purpleButton(esNuevo ? "Crear Usuario" : "Guardar Cambios");
            save.addActionListener(e -> {
                  String nombre = userF.getText().trim();
                  if (nombre.isEmpty()) {
                  JOptionPane.showMessageDialog(dlg, "El nombre de usuario no puede estar vacío.",
                        "Error", JOptionPane.ERROR_MESSAGE);
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

            footer.add(cancel);
            footer.add(save);
            dlg.add(form, BorderLayout.CENTER);
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

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
            form.setBackground(Color.WHITE);

            JPasswordField nueva  = UIFactory.passwordField("Nueva contraseña");
            JPasswordField nueva2 = UIFactory.passwordField("Confirmar contraseña");

            form.add(UIFactory.formLabel("Nueva Contraseña"));    form.add(nueva);
            form.add(UIFactory.formLabel("Confirmar Contraseña")); form.add(nueva2);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 16));
            footer.setBackground(new Color(0xF8F9FC));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());

            JButton save = purpleButton("Cambiar");
            save.addActionListener(e -> {
                  String p1 = new String(nueva.getPassword());
                  String p2 = new String(nueva2.getPassword());
                  if (!p1.equals(p2)) {
                  JOptionPane.showMessageDialog(dlg, "Las contraseñas no coinciden.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                  return;
                  }
                  try {
                  // Buscamos el idUsuario por posición en tabla
                  List<Usuario> todos = gestion.listarUsuarios();
                  todos.stream()
                        .filter(u -> u.getNombreUsuario().equals(nombreUsuario))
                        .findFirst()
                        .ifPresent(u -> {
                              try { seguridad.cambiarContrasena(u.getIdUsuario(), p1); }
                              catch (Exception ex) { throw new RuntimeException(ex); }
                        });
                  JOptionPane.showMessageDialog(dlg, "Contraseña actualizada correctamente.");
                  dlg.dispose();
                  } catch (Exception ex) {
                  JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });

            footer.add(cancel);
            footer.add(save);
            dlg.add(form,   BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 6));
            p.setOpaque(false);

            JButton edit = new JButton("✏");
            styleAction(edit, UIColors.BLUE, UIColors.INFO_BG);
            edit.setToolTipText("Editar usuario");
            edit.addActionListener(e -> {
                  if (row < model.getRowCount()) showUserForm(row);
            });

            JButton pwd = new JButton("🔑");
            styleAction(pwd, UIColors.TURQUOISE, new Color(0xE0F7FA));
            pwd.setToolTipText("Cambiar contraseña");
            pwd.addActionListener(e -> {
                  if (row < model.getRowCount()) showChangePasswordDialog(row);
            });

            JButton del = new JButton("🗑");
            styleAction(del, UIColors.RED, UIColors.ERROR_BG);
            del.setToolTipText("Eliminar usuario");
            del.addActionListener(e -> {
                  if (row >= model.getRowCount()) return;
                  String nombre = model.getValueAt(row, 1).toString();
                  int c = JOptionPane.showConfirmDialog(this,
                  "¿Eliminar el usuario \"" + nombre + "\"?\nEsta acción no se puede deshacer.",
                  "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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

      private void styleAction(JButton btn, Color fg, Color bg) {
            btn.setFont(UIFonts.BODY_SM);
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
}
