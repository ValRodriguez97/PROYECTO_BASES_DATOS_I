package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class StadiumsPanel extends JPanel {

      private final GestionDatosService gestion;
      private JTable table;
      private DefaultTableModel model;
      private JTextField searchField;

      private static final String[] COLUMNS = {
            "#", "Estadio", "Ciudad", "País", "Capacidad", "Acciones"
      };

      public StadiumsPanel(GestionDatosService gestion) {
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
            content.add(buildCitiesRow());
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
            left.add(UIFactory.heading("Estadios y Ciudades"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Administra las sedes del Mundial FIFA 2026"));
            JButton addBtn = tealButton("+ Agregar Estadio");
            addBtn.addActionListener(e -> showStadiumForm(-1));
            p.add(left, BorderLayout.WEST);
            p.add(addBtn, BorderLayout.EAST);
            return p;
      }

      private JPanel buildCitiesRow() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel headerRow = new JPanel(new BorderLayout());
            headerRow.setBackground(new Color(0xF8F9FC));
            headerRow.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(14, 20, 14, 20)));
            headerRow.add(UIFactory.sectionTitle("Ciudades Sede"), BorderLayout.WEST);
            card.add(headerRow, BorderLayout.NORTH);

            String[][] ciudades = {
                  {"Ciudad de México", "México"},
                  {"Guadalajara",      "México"},
                  {"Monterrey",        "México"},
                  {"Nueva York",       "Estados Unidos"},
                  {"Los Ángeles",      "Estados Unidos"},
                  {"Dallas",           "Estados Unidos"},
                  {"San Francisco",    "Estados Unidos"},
                  {"Miami",            "Estados Unidos"},
                  {"Seattle",          "Estados Unidos"},
                  {"Boston",           "Estados Unidos"},
                  {"Houston",          "Estados Unidos"},
                  {"Kansas City",      "Estados Unidos"},
                  {"Toronto",          "Canadá"},
                  {"Vancouver",        "Canadá"},
            };

            JPanel grid = new JPanel(new GridLayout(2, 7, 10, 10));
            grid.setBackground(Color.WHITE);
            grid.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            for (String[] c : ciudades) {
                  JPanel cityCard = new JPanel(new BorderLayout(0, 4));
                  cityCard.setBackground(new Color(0xF0FDFA));
                  cityCard.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, new Color(0x00BFA5, false)),
                  BorderFactory.createEmptyBorder(10, 10, 10, 10)));

                  JLabel nombre = new JLabel(c[0]);
                  nombre.setFont(UIFonts.LABEL_BOLD);
                  nombre.setForeground(UIColors.TEXT_PRIMARY);
                  JLabel pais = new JLabel(c[1]);
                  pais.setFont(UIFonts.BODY_SM);
                  pais.setForeground(UIColors.TEXT_SECONDARY);

                  cityCard.add(nombre, BorderLayout.CENTER);
                  cityCard.add(pais,   BorderLayout.SOUTH);
                  grid.add(cityCard);
            }

            card.add(grid, BorderLayout.CENTER);
            return card;
      }

      private JPanel buildFilters() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.WHITE);
            p.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)));
            JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
            row.setOpaque(false);
            searchField = UIFactory.textField("Buscar estadio...");
            JComboBox<String> paisFilter = UIFactory.comboBox(
                  "Todos los países", "México", "Estados Unidos", "Canadá");
            JButton btn = tealButton("Buscar");
            btn.addActionListener(e -> loadData());
            row.add(searchField);
            row.add(paisFilter);
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
            th.add(UIFactory.sectionTitle("Estadios Registrados"), BorderLayout.WEST);

            model = new DefaultTableModel(COLUMNS, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return c == 5; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(4).setMaxWidth(100);
            table.getColumnModel().getColumn(5).setMaxWidth(120);

            table.getColumnModel().getColumn(4).setCellRenderer((t, v, sel, foc, r, c) -> {
                  JLabel l = new JLabel(v != null ? v.toString() : "", SwingConstants.CENTER);
                  l.setFont(UIFonts.LABEL_BOLD);
                  l.setForeground(sel ? UIColors.PURPLE : UIColors.TURQUOISE);
                  l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                  return l;
            });

            table.getColumnModel().getColumn(5).setCellRenderer(
                  (t, v, sel, foc, r, c) -> buildActionBtns(r));
            table.getColumnModel().getColumn(5).setCellEditor(
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
                  List<Estadio> estadios = gestion.listarEstadios();
                  String filtro = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                  int idx = 1;
                  for (Estadio e : estadios) {
                  if (!filtro.isEmpty() && !e.getNombre().toLowerCase().contains(filtro)) continue;
                  String ciudad = e.getCiudad() != null ? e.getCiudad().getNombre() : "-";
                  String pais   = (e.getCiudad() != null && e.getCiudad().getPais() != null)
                        ? e.getCiudad().getPais().getNombre() : "-";
                  model.addRow(new Object[]{
                        idx++, e.getNombre(), ciudad, pais,
                        String.format("%,d", e.getCapacidad()), ""
                  });
                  }
            } catch (Exception ex) {
                  loadDemoData();
            }
      }

      private void loadDemoData() {
            model.setRowCount(0);
            Object[][] demo = {
                  {1, "Estadio Azteca",  "Ciudad de México","México",         "87,523",""},
                  {2, "Estadio Akron",   "Guadalajara",     "México",         "49,850",""},
                  {3, "Estadio BBVA",    "Monterrey",       "México",         "51,350",""},
                  {4, "MetLife Stadium", "Nueva York",      "Estados Unidos", "82,500",""},
                  {5, "SoFi Stadium",    "Los Ángeles",     "Estados Unidos", "70,240",""},
                  {6, "AT&T Stadium",    "Dallas",          "Estados Unidos", "80,000",""},
                  {7, "Levi's Stadium",  "San Francisco",   "Estados Unidos", "68,500",""},
                  {8, "Hard Rock Stadium","Miami",          "Estados Unidos", "64,767",""},
                  {9, "Lumen Field",     "Seattle",         "Estados Unidos", "72,000",""},
                  {10,"Gillette Stadium","Boston",          "Estados Unidos", "64,628",""},
                  {11,"BMO Stadium",     "Toronto",         "Canadá",         "30,500",""},
                  {12,"BC Place",        "Vancouver",       "Canadá",         "54,500",""},
            };
            for (Object[] row : demo) model.addRow(row);
      }

      private void showStadiumForm(int editRow) {
            JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                  editRow < 0 ? "Agregar Estadio" : "Editar Estadio",
                  Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(480, 340);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            form.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
            form.setBackground(Color.WHITE);

            JTextField nombreF   = UIFactory.textField("Ej: Estadio Azteca");
            JTextField ciudadF   = UIFactory.textField("Ej: Ciudad de México");
            JComboBox<String> paisBox = UIFactory.comboBox("México","Estados Unidos","Canadá");
            JTextField capF      = UIFactory.textField("Ej: 87523");

            if (editRow >= 0) {
                  nombreF.setText(model.getValueAt(editRow, 1).toString());
                  ciudadF.setText(model.getValueAt(editRow, 2).toString());
                  paisBox.setSelectedItem(model.getValueAt(editRow, 3).toString());
                  capF.setText(model.getValueAt(editRow, 4).toString().replace(",",""));
            }

            form.add(UIFactory.formLabel("Nombre del Estadio")); form.add(nombreF);
            form.add(UIFactory.formLabel("Ciudad"));             form.add(ciudadF);
            form.add(UIFactory.formLabel("País Sede"));          form.add(paisBox);
            form.add(UIFactory.formLabel("Capacidad"));          form.add(capF);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 16));
            footer.setBackground(new Color(0xF8F9FC));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));
            JButton cancel = UIFactory.outlineButton("Cancelar");
            cancel.addActionListener(e -> dlg.dispose());
            JButton save = tealButton("Guardar Estadio");
            save.addActionListener(e -> {
                  try {
                  int cap = Integer.parseInt(capF.getText().trim().replace(",",""));
                  String capFmt = String.format("%,d", cap);
                  if (editRow >= 0) {
                        model.setValueAt(nombreF.getText(), editRow, 1);
                        model.setValueAt(ciudadF.getText(), editRow, 2);
                        model.setValueAt(paisBox.getSelectedItem(), editRow, 3);
                        model.setValueAt(capFmt, editRow, 4);
                  } else {
                        model.addRow(new Object[]{
                              model.getRowCount() + 1,
                              nombreF.getText(), ciudadF.getText(),
                              paisBox.getSelectedItem(), capFmt, ""
                        });
                  }
                  dlg.dispose();
                  } catch (NumberFormatException ex) {
                  JOptionPane.showMessageDialog(dlg,
                        "La capacidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
                  }
            });
            footer.add(cancel); footer.add(save);
            dlg.add(form,   BorderLayout.CENTER);
            dlg.add(footer, BorderLayout.SOUTH);
            dlg.setVisible(true);
      }

      private JPanel buildActionBtns(int row) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
            p.setOpaque(false);
            JButton edit = new JButton("✏");
            styleAction(edit, UIColors.BLUE, UIColors.INFO_BG);
            edit.addActionListener(e -> showStadiumForm(row));
            JButton del = new JButton("🗑");
            styleAction(del, UIColors.RED, UIColors.ERROR_BG);
            del.addActionListener(e -> {
                  if (row < model.getRowCount()) {
                  int c = JOptionPane.showConfirmDialog(this,
                        "¿Eliminar este estadio?", "Confirmar", JOptionPane.YES_NO_OPTION);
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
