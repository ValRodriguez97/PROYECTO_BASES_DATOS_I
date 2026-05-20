package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TeamsPanel extends JPanel {

    private final GestionDatosService gestion;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> confFilter;
    private List<Equipo> equiposActuales = new ArrayList<>();

    public void refresh() {
        loadData();
    }

    private static final String[] COLUMNS = {
        "#", "Equipo", "Conf.", "País", "Director Técnico", "Ranking", "Acciones"
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
        left.add(UIFactory.heading("Equipos"));
        left.add(Box.createVerticalStrut(4));
        left.add(UIFactory.subheading("Gestión de los 48 equipos del Mundial FIFA 2026"));

        JButton addBtn = UIFactory.primaryButton("+ Nuevo Equipo");
        addBtn.addActionListener(e -> showTeamForm(null));

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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Columnas proporcionales
        table.getColumnModel().getColumn(0).setPreferredWidth(36);
        table.getColumnModel().getColumn(0).setMaxWidth(44);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setMaxWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setMaxWidth(110);

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

        JScrollPane tableScroll = UIFactory.scrollPane(table);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(th, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        model.setRowCount(0);
        equiposActuales = new ArrayList<>();
        try {
            List<Equipo> todos = gestion.listarEquipos();
            String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
            String conf   = confFilter  != null && confFilter.getSelectedIndex() > 0
                    ? (String) confFilter.getSelectedItem() : "";
            int idx = 1;
            for (Equipo e : todos) {
                if (!search.isEmpty() && !e.getNombre().toLowerCase().contains(search)) continue;
                String sigla = e.getConfederacion() != null ? e.getConfederacion().getSigla() : "";
                if (!conf.isEmpty() && !conf.equals(sigla)) continue;
                String pais = e.getPais() != null ? e.getPais().getNombre() : "-";
                String dt   = e.getDirectorTecnico() != null
                        ? e.getDirectorTecnico().getNombre() + " " + e.getDirectorTecnico().getApellido() : "-";
                equiposActuales.add(e);
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
        edit.addActionListener(e -> {
            if (row < equiposActuales.size()) showTeamForm(equiposActuales.get(row));
        });
        JButton del = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
        del.addActionListener(e -> {
            if (row >= equiposActuales.size()) return;
            Equipo eq = equiposActuales.get(row);
            int c = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el equipo \"" + eq.getNombre() + "\"?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            try {
                gestion.eliminarEquipo(eq.getIdEquipo());
                loadData();
            } catch (Exception ex) {
                showError("Error al eliminar: " + ex.getMessage());
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

    private void showTeamForm(Equipo equipo) {
        boolean esNuevo = (equipo == null);

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                esNuevo ? "Nuevo Equipo" : "Editar Equipo",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        // Cargar datos reales desde BD
        List<Pais>            paises       = new ArrayList<>();
        List<Confederacion>   confs        = new ArrayList<>();
        List<DirectorTecnico> directores   = new ArrayList<>();

        try { paises     = gestion.listarPaises();            } catch (Exception ex) { ex.printStackTrace(); }
        try { confs      = gestion.listarConfederaciones();   } catch (Exception ex) { ex.printStackTrace(); }
        try { directores = gestion.listarDirectoresTecnicos();} catch (Exception ex) { ex.printStackTrace(); }

        String[] nombresConf = confs.stream().map(Confederacion::getNombre).toArray(String[]::new);
        String[] nombresPais = paises.stream().map(Pais::getNombre).toArray(String[]::new);
        String[] nombresDT   = directores.stream()
                .map(d -> d.getNombre() + " " + d.getApellido())
                .toArray(String[]::new);

        JTextField nameF  = UIFactory.textField("Ej: Argentina");
        JTextField rankF  = UIFactory.textField("Ej: 1");

        JComboBox<String> confBox = nombresConf.length > 0
                ? UIFactory.comboBox(nombresConf) : UIFactory.comboBox("(sin confederaciones)");
        JComboBox<String> paisBox = nombresPais.length > 0
                ? UIFactory.comboBox(nombresPais) : UIFactory.comboBox("(sin paises)");
        JComboBox<String> dtBox   = nombresDT.length > 0
                ? UIFactory.comboBox(nombresDT)   : UIFactory.comboBox("(sin directores)");

        // Pre-rellenar si es edición
        if (!esNuevo) {
            nameF.setText(equipo.getNombre());
            rankF.setText(String.valueOf(equipo.getRankingFifa()));
            if (equipo.getConfederacion() != null) confBox.setSelectedItem(equipo.getConfederacion().getNombre());
            if (equipo.getPais() != null)          paisBox.setSelectedItem(equipo.getPais().getNombre());
            if (equipo.getDirectorTecnico() != null) {
                dtBox.setSelectedItem(equipo.getDirectorTecnico().getNombre()
                        + " " + equipo.getDirectorTecnico().getApellido());
            }
        }

        form.add(UIFactory.formLabel("Nombre del Equipo")); form.add(nameF);
        form.add(UIFactory.formLabel("Confederación"));     form.add(confBox);
        form.add(UIFactory.formLabel("País"));              form.add(paisBox);
        form.add(UIFactory.formLabel("Director Técnico"));  form.add(dtBox);
        form.add(UIFactory.formLabel("Ranking FIFA"));      form.add(rankF);

        final List<Pais>            paisesFinal    = paises;
        final List<Confederacion>   confsFinal     = confs;
        final List<DirectorTecnico> directoresFinal = directores;

        JPanel footer = buildDialogFooter();
        JButton cancel = UIFactory.outlineButton("Cancelar");
        cancel.addActionListener(e -> dlg.dispose());

        JButton save = UIFactory.primaryButton(esNuevo ? "Guardar" : "Actualizar");
        save.addActionListener(e -> {
            String nombre = nameF.getText().trim();
            String rankTxt = rankF.getText().trim();
            if (nombre.isEmpty()) { showError("El nombre es obligatorio."); return; }

            int idxConf = confBox.getSelectedIndex();
            int idxPais = paisBox.getSelectedIndex();
            int idxDT   = dtBox.getSelectedIndex();

            if (confsFinal.isEmpty() || idxConf < 0 || idxConf >= confsFinal.size()) {
                showError("Selecciona una confederación válida."); return;
            }
            if (paisesFinal.isEmpty() || idxPais < 0 || idxPais >= paisesFinal.size()) {
                showError("Selecciona un país válido."); return;
            }
            if (directoresFinal.isEmpty() || idxDT < 0 || idxDT >= directoresFinal.size()) {
                showError("Selecciona un director técnico válido."); return;
            }

            try {
                int ranking = rankTxt.isEmpty() ? 99 : Integer.parseInt(rankTxt);

                Confederacion   confSel  = confsFinal.get(idxConf);
                Pais            paisSel  = paisesFinal.get(idxPais);
                DirectorTecnico dtSel    = directoresFinal.get(idxDT);

                if (esNuevo) {
                    Equipo nuevo = new Equipo();
                    nuevo.setNombre(nombre);
                    nuevo.setRankingFifa(ranking);
                    nuevo.setConfederacion(confSel);
                    nuevo.setPais(paisSel);
                    nuevo.setDirectorTecnico(dtSel);
                    gestion.crearEquipo(nuevo);
                } else {
                    equipo.setNombre(nombre);
                    equipo.setRankingFifa(ranking);
                    equipo.setConfederacion(confSel);
                    equipo.setPais(paisSel);
                    equipo.setDirectorTecnico(dtSel);
                    gestion.actualizarEquipo(equipo);
                }
                dlg.dispose();
                loadData();
            } catch (NumberFormatException ex) {
                showError("El ranking debe ser un número entero.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Error al guardar: " + ex.getMessage());
            }
        });

        footer.add(cancel);
        footer.add(save);
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