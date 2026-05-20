package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumsPanel extends JPanel {

    private final GestionDatosService gestion;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public void refresh() {
        loadData();
    }

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
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(UIColors.BG_PAGE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        content.add(buildHeader());
        content.add(Box.createVerticalStrut(14));
        content.add(buildFilters());
        content.add(Box.createVerticalStrut(12));
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
        left.add(UIFactory.heading("Estadios"));
        left.add(Box.createVerticalStrut(4));
        left.add(UIFactory.subheading("Sedes del Mundial FIFA 2026"));

        JButton addBtn = UIFactory.tealButton("+ Nuevo Estadio");
        addBtn.addActionListener(e -> showForm(-1));
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
        searchField = UIFactory.textField("Buscar estadio...");
        JComboBox<String> paisFilter = UIFactory.comboBox(
                "Todos los países", "México", "Estados Unidos", "Canadá");
        JButton btn = UIFactory.tealButton("Buscar");
        btn.addActionListener(e -> loadData());
        row.add(searchField); row.add(paisFilter); row.add(btn);
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
        th.add(UIFactory.sectionTitle("Estadios Registrados"), BorderLayout.WEST);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };
        table = new JTable(model);
        UIFactory.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getColumnModel().getColumn(0).setPreferredWidth(36);
        table.getColumnModel().getColumn(0).setMaxWidth(44);
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Estadio (nombres largos)
        table.getColumnModel().getColumn(2).setPreferredWidth(140); // Ciudad
        table.getColumnModel().getColumn(3).setPreferredWidth(130); // País
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Capacidad
        table.getColumnModel().getColumn(4).setMaxWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setMaxWidth(110);

        table.getColumnModel().getColumn(4).setCellRenderer((t, v, sel, foc, r, c) -> {
            JLabel l = new JLabel(v != null ? v.toString() : "", SwingConstants.CENTER);
            l.setFont(UIFonts.LABEL_BOLD);
            l.setForeground(sel ? UIColors.PURPLE : UIColors.TURQUOISE);
            l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return l;
        });

        table.getColumnModel().getColumn(5).setCellRenderer((t, v, sel, foc, r, c) -> buildActionBtns(r));
        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
                return buildActionBtns(row);
            }
            @Override public Object getCellEditorValue() { return ""; }
        });

        JScrollPane tableScroll = UIFactory.scrollPane(table);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(th, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
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
            JOptionPane.showMessageDialog(this,
                    "Error al cargar estadios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showForm(int editRow) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                editRow < 0 ? "Nuevo Estadio" : "Editar Estadio",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(460, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        // Cargar ciudades reales desde BD
        List<Ciudad> ciudades = new ArrayList<>();
        try { ciudades = gestion.listarCiudades(); } catch (Exception ex) { ex.printStackTrace(); }

        String[] nombresCiudad = ciudades.stream()
                .map(c -> c.getNombre() + " (" + (c.getPais() != null ? c.getPais().getNombre() : "-") + ")")
                .toArray(String[]::new);

        JTextField nombreF = UIFactory.textField("Ej: Estadio Azteca");
        JComboBox<String> ciudadBox = nombresCiudad.length > 0
                ? UIFactory.comboBox(nombresCiudad)
                : UIFactory.comboBox("(sin ciudades registradas)");
        JTextField capF = UIFactory.textField("Ej: 87523");

        final List<Ciudad> ciudadesFinal = ciudades;

        // Pre-rellenar si es edición
        if (editRow >= 0) {
            nombreF.setText(model.getValueAt(editRow, 1).toString());
            String ciudadEnTabla = model.getValueAt(editRow, 2).toString();
            String paisEnTabla   = model.getValueAt(editRow, 3).toString();
            String buscar = ciudadEnTabla + " (" + paisEnTabla + ")";
            for (int i = 0; i < nombresCiudad.length; i++) {
                if (nombresCiudad[i].equals(buscar)) {
                    ciudadBox.setSelectedIndex(i);
                    break;
                }
            }
            capF.setText(model.getValueAt(editRow, 4).toString().replace(",", ""));
        }

        form.add(UIFactory.formLabel("Nombre del Estadio")); form.add(nombreF);
        form.add(UIFactory.formLabel("Ciudad"));             form.add(ciudadBox);
        form.add(UIFactory.formLabel("Capacidad"));          form.add(capF);

        JPanel footer = buildFooter();
        JButton cancel = UIFactory.outlineButton("Cancelar");
        cancel.addActionListener(e -> dlg.dispose());

        JButton save = UIFactory.tealButton(editRow < 0 ? "Guardar" : "Actualizar");
        save.addActionListener(e -> {
            String nombre = nombreF.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                        "El nombre del estadio es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ciudadesFinal.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                        "No hay ciudades registradas en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idxCiudad = ciudadBox.getSelectedIndex();
            if (idxCiudad < 0 || idxCiudad >= ciudadesFinal.size()) {
                JOptionPane.showMessageDialog(dlg,
                        "Selecciona una ciudad válida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int cap = Integer.parseInt(capF.getText().trim().replace(",", ""));
                Ciudad ciudadSel = ciudadesFinal.get(idxCiudad);

                if (editRow < 0) {
                    // CREAR
                    Estadio nuevo = new Estadio();
                    nuevo.setNombre(nombre);
                    nuevo.setCapacidad(cap);
                    nuevo.setCiudad(ciudadSel);
                    gestion.crearEstadio(nuevo);
                } else {
                    // EDITAR — necesitamos el idEstadio original
                    // Lo obtenemos recargando la lista actual de estadios
                    List<Estadio> estadiosActuales = gestion.listarEstadios();
                    String filtro = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                    List<Estadio> filtrados = estadiosActuales.stream()
                            .filter(est -> filtro.isEmpty() || est.getNombre().toLowerCase().contains(filtro))
                            .toList();
                    if (editRow >= filtrados.size()) {
                        JOptionPane.showMessageDialog(dlg,
                                "No se encontró el estadio a editar.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Estadio existente = filtrados.get(editRow);
                    existente.setNombre(nombre);
                    existente.setCapacidad(cap);
                    existente.setCiudad(ciudadSel);
                    gestion.actualizarEstadio(existente);
                }

                dlg.dispose();
                loadData();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg,
                        "La capacidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg,
                        "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(cancel);
        footer.add(save);
        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(footer, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel buildActionBtns(int row) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        p.setOpaque(false);

        JButton edit = smallBtn("✏", UIColors.BLUE, UIColors.INFO_BG);
        edit.addActionListener(e -> { if (row < model.getRowCount()) showForm(row); });

        JButton del = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
        del.addActionListener(e -> {
            if (row >= model.getRowCount()) return;
            String nombreEstadio = model.getValueAt(row, 1).toString();
            int c = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el estadio \"" + nombreEstadio + "\"?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            try {
                List<Estadio> estadiosActuales = gestion.listarEstadios();
                String filtro = searchField != null ? searchField.getText().trim().toLowerCase() : "";
                List<Estadio> filtrados = estadiosActuales.stream()
                        .filter(est -> filtro.isEmpty() || est.getNombre().toLowerCase().contains(filtro))
                        .toList();
                if (row >= filtrados.size()) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontró el estadio a eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                gestion.eliminarEstadio(filtrados.get(row).getIdEstadio());
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(edit);
        p.add(del);
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