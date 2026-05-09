package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.View.utils.*;
import co.edu.uniquindio.model.*;
import co.edu.uniquindio.model.Enum.Condicion;
import co.edu.uniquindio.services.GestionDatosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MatchesPanel extends JPanel {

    private final GestionDatosService gestion;

    private JTable table;
    private DefaultTableModel model;

    private JTextField searchField;
    private JComboBox<String> groupFilter;

    // SOLO partidos visibles en la tabla
    private List<Partido> partidosActuales = new ArrayList<>();

    private List<Grupo> gruposActuales = new ArrayList<>();

    private static final String[] COLUMNS = {
            "#",
            "Grupo",
            "Local",
            "Visitante",
            "Fecha / Hora",
            "Estadio",
            "Acciones"
    };

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter FMT_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public MatchesPanel(GestionDatosService gestion) {

        this.gestion = gestion;

        setBackground(UIColors.BG_PAGE);

        setLayout(new BorderLayout());

        build();

        loadData();
    }

    public void refresh() {

        recargarFiltroGrupos();

        loadData();
    }

    private void build() {

        JScrollPane scroll = UIFactory.scrollPane(buildContent());

        scroll.setBackground(UIColors.BG_PAGE);

        scroll.getViewport().setBackground(UIColors.BG_PAGE);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {

        JPanel content = new JPanel();

        content.setBackground(UIColors.BG_PAGE);

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24
                )
        );

        content.setMaximumSize(
                new Dimension(1200, Integer.MAX_VALUE)
        );

        JPanel header = buildHeader();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filters = buildFilters();
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tablePanel = buildTable();
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(header);
        content.add(Box.createVerticalStrut(14));

        content.add(filters);
        content.add(Box.createVerticalStrut(12));

        content.add(tablePanel);

        return content;
    }

    private JPanel buildHeader() {

        JPanel p = new JPanel(new BorderLayout());

        p.setOpaque(false);

        p.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 70)
        );

        JPanel left = new JPanel();

        left.setOpaque(false);

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(UIFactory.heading("Partidos"));

        left.add(Box.createVerticalStrut(4));

        left.add(
                UIFactory.subheading(
                        "Programación de encuentros del Mundial FIFA 2026"
                )
        );

        JButton addBtn = UIFactory.primaryButton("+ Nuevo Partido");

        addBtn.setPreferredSize(new Dimension(170, 42));

        addBtn.addActionListener(e -> showMatchForm(null));

        p.add(left, BorderLayout.WEST);
        p.add(addBtn, BorderLayout.EAST);

        return p;
    }

    private JPanel buildFilters() {

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(12, UIColors.BORDER),
                        BorderFactory.createEmptyBorder(
                                18, 18, 18, 18
                        )
                )
        );

        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 95)
        );

        JLabel title = UIFactory.sectionTitle("Filtros de búsqueda");

        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 0)
        );

        row.setOpaque(false);

        searchField = UIFactory.textField("Buscar equipo...");

        searchField.setPreferredSize(
                new Dimension(260, 38)
        );

        groupFilter = UIFactory.comboBox("Todos los grupos");

        groupFilter.setPreferredSize(
                new Dimension(180, 38)
        );

        JButton btn = UIFactory.primaryButton("Buscar");

        btn.setPreferredSize(new Dimension(120, 38));

        btn.addActionListener(e -> loadData());

        row.add(searchField);
        row.add(groupFilter);
        row.add(btn);

        card.add(title);
        card.add(Box.createVerticalStrut(14));
        card.add(row);

        recargarFiltroGrupos();

        return card;
    }

    private void recargarFiltroGrupos() {

        if (groupFilter == null) {
            return;
        }

        try {

            gruposActuales = gestion.listarGrupos();

        } catch (Exception ex) {

            ex.printStackTrace();

            gruposActuales = new ArrayList<>();
        }

        groupFilter.removeAllItems();

        groupFilter.addItem("Todos los grupos");

        for (Grupo g : gruposActuales) {
            groupFilter.addItem("Grupo " + g.getLetra());
        }
    }

    private JPanel buildTable() {

        JPanel card = new JPanel(new BorderLayout());

        card.setBackground(Color.WHITE);

        card.setBorder(new RoundedBorder(10, UIColors.BORDER));

        JPanel th = new JPanel(new BorderLayout());

        th.setBackground(new Color(0xF8F9FC));

        th.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                UIColors.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                14, 18, 14, 18
                        )
                )
        );

        th.add(
                UIFactory.sectionTitle("Partidos Registrados"),
                BorderLayout.WEST
        );

        model = new DefaultTableModel(COLUMNS, 0) {

            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };

        table = new JTable(model);

        UIFactory.styleTable(table);

        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        table.setRowHeight(42);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(44);

        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setMaxWidth(90);

        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);

        table.getColumnModel().getColumn(4).setPreferredWidth(160);

        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setMaxWidth(120);

        table.getColumnModel().getColumn(6).setCellRenderer(
                (t, v, sel, foc, r, c) -> buildActionBtns(r)
        );

        table.getColumnModel().getColumn(6).setCellEditor(
                new DefaultCellEditor(new JCheckBox()) {

                    @Override
                    public Component getTableCellEditorComponent(
                            JTable t,
                            Object v,
                            boolean sel,
                            int row,
                            int col
                    ) {
                        return buildActionBtns(row);
                    }

                    @Override
                    public Object getCellEditorValue() {
                        return "";
                    }
                }
        );

        JScrollPane tableScroll = UIFactory.scrollPane(table);

        tableScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        card.add(th, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);

        return card;
    }

    /**
     * Cargar partidos aplicando filtros.
     */
    private void loadData() {

        model.setRowCount(0);

        try {

            List<Partido> todos = gestion.listarPartidos();

            String filtro = searchField != null
                    ? searchField.getText().trim().toLowerCase()
                    : "";

            String grupoSel = groupFilter != null
                    && groupFilter.getSelectedIndex() > 0
                    ? (String) groupFilter.getSelectedItem()
                    : "";

            // IMPORTANTE:
            // SOLO partidos visibles
            partidosActuales = new ArrayList<>();

            int idx = 1;

            for (Partido p : todos) {

                String local = p.getEquipoLocal() != null
                        ? p.getEquipoLocal().getNombre()
                        : "—";

                String visitante = p.getEquipoVisitante() != null
                        ? p.getEquipoVisitante().getNombre()
                        : "—";

                String grupo = p.getGrupo() != null
                        ? "Grupo " + p.getGrupo().getLetra()
                        : "-";

                String estadio = p.getEstadio() != null
                        ? p.getEstadio().getNombre()
                        : "-";

                boolean coincideBusqueda =
                        filtro.isEmpty()
                                || local.toLowerCase().contains(filtro)
                                || visitante.toLowerCase().contains(filtro);

                boolean coincideGrupo =
                        grupoSel.isEmpty()
                                || grupo.equals(grupoSel);

                if (!coincideBusqueda || !coincideGrupo) {
                    continue;
                }

                partidosActuales.add(p);

                model.addRow(new Object[]{
                        idx++,
                        grupo,
                        local,
                        visitante,
                        p.getHoraFecha() != null
                                ? p.getHoraFecha().format(FMT)
                                : "-",
                        estadio,
                        ""
                });
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar partidos: "
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showMatchForm(Partido partido) {

        boolean esNuevo = partido == null;

        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                esNuevo
                        ? "Nuevo Partido"
                        : "Editar Partido",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dlg.setSize(560, 430);

        dlg.setLocationRelativeTo(this);

        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(
                new GridLayout(5, 2, 10, 10)
        );

        form.setBackground(Color.WHITE);

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 12, 24
                )
        );

        List<Equipo> equipos = new ArrayList<>();
        List<Grupo> grupos = new ArrayList<>();
        List<Estadio> estadios = new ArrayList<>();

        try {

            equipos = gestion.listarEquipos();

            // ORDENAR POR RANKING FIFA DESC
            equipos.sort((a, b) ->
                    Integer.compare(
                            b.getRankingFifa(),
                            a.getRankingFifa()
                    )
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            grupos = gestion.listarGrupos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            estadios = gestion.listarEstadios();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String[] nEq = equipos.stream()
                .map(Equipo::getNombre)
                .toArray(String[]::new);

        String[] nGr = grupos.stream()
                .map(g -> "Grupo " + g.getLetra())
                .toArray(String[]::new);

        String[] nEs = estadios.stream()
                .map(Estadio::getNombre)
                .toArray(String[]::new);

        JComboBox<String> localBox =
                nEq.length > 0
                        ? UIFactory.comboBox(nEq)
                        : UIFactory.comboBox("(sin equipos)");

        JComboBox<String> visitBox =
                nEq.length > 0
                        ? UIFactory.comboBox(nEq)
                        : UIFactory.comboBox("(sin equipos)");

        JComboBox<String> grupoBox =
                nGr.length > 0
                        ? UIFactory.comboBox(nGr)
                        : UIFactory.comboBox("(sin grupos)");

        JComboBox<String> estadioBox =
                nEs.length > 0
                        ? UIFactory.comboBox(nEs)
                        : UIFactory.comboBox("(sin estadios)");

        JTextField fechaF = UIFactory.textField(
                "Ej: 2026-06-11T18:00"
        );

        // EDICIÓN
        if (!esNuevo) {

            if (partido.getEquipoLocal() != null) {
                localBox.setSelectedItem(
                        partido.getEquipoLocal().getNombre()
                );
            }

            if (partido.getEquipoVisitante() != null) {
                visitBox.setSelectedItem(
                        partido.getEquipoVisitante().getNombre()
                );
            }

            if (partido.getEstadio() != null) {
                estadioBox.setSelectedItem(
                        partido.getEstadio().getNombre()
                );
            }

            if (partido.getGrupo() != null) {
                grupoBox.setSelectedItem(
                        "Grupo " + partido.getGrupo().getLetra()
                );
            }

            if (partido.getHoraFecha() != null) {
                fechaF.setText(
                        partido.getHoraFecha().format(FMT_INPUT)
                );
            }
        }

        form.add(UIFactory.formLabel("Equipo Local"));
        form.add(localBox);

        form.add(UIFactory.formLabel("Equipo Visitante"));
        form.add(visitBox);

        form.add(UIFactory.formLabel("Grupo"));
        form.add(grupoBox);

        form.add(UIFactory.formLabel("Estadio"));
        form.add(estadioBox);

        form.add(UIFactory.formLabel("Fecha y Hora"));
        form.add(fechaF);

        final List<Equipo> equiposFinal = equipos;
        final List<Grupo> gruposFinal = grupos;
        final List<Estadio> estadiosFinal = estadios;

        JPanel footer = buildFooter();

        JButton cancel = UIFactory.outlineButton("Cancelar");

        cancel.addActionListener(e -> dlg.dispose());

        JButton save = UIFactory.primaryButton(
                esNuevo ? "Guardar" : "Actualizar"
        );

        save.addActionListener(e -> {

            String fechaTxt = fechaF.getText().trim();

            if (fechaTxt.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dlg,
                        "La fecha y hora son obligatorias.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try {

                LocalDateTime fecha =
                        LocalDateTime.parse(
                                fechaTxt,
                                FMT_INPUT
                        );

                int idxEq1 = localBox.getSelectedIndex();
                int idxEq2 = visitBox.getSelectedIndex();

                int idxGr = grupoBox.getSelectedIndex();
                int idxEs = estadioBox.getSelectedIndex();

                if (idxEq1 < 0 || idxEq1 >= equiposFinal.size()) {

                    JOptionPane.showMessageDialog(
                            dlg,
                            "Selecciona el equipo local."
                    );

                    return;
                }

                if (idxEq2 < 0 || idxEq2 >= equiposFinal.size()) {

                    JOptionPane.showMessageDialog(
                            dlg,
                            "Selecciona el equipo visitante."
                    );

                    return;
                }

                if (idxEq1 == idxEq2) {

                    JOptionPane.showMessageDialog(
                            dlg,
                            "Local y visitante no pueden ser iguales."
                    );

                    return;
                }

                Equipo eqLocal = equiposFinal.get(idxEq1);

                Equipo eqVisit = equiposFinal.get(idxEq2);

                Grupo grupoSel =
                        (!gruposFinal.isEmpty() && idxGr >= 0)
                                ? gruposFinal.get(idxGr)
                                : null;

                Estadio estadioSel =
                        (!estadiosFinal.isEmpty() && idxEs >= 0)
                                ? estadiosFinal.get(idxEs)
                                : null;

                if (grupoSel == null) {

                    JOptionPane.showMessageDialog(
                            dlg,
                            "Debes seleccionar un grupo."
                    );

                    return;
                }

                if (estadioSel == null) {

                    JOptionPane.showMessageDialog(
                            dlg,
                            "Debes seleccionar un estadio."
                    );

                    return;
                }

                if (esNuevo) {

                    Partido nuevo = new Partido();

                    nuevo.setHoraFecha(fecha);

                    nuevo.setGrupo(grupoSel);

                    nuevo.setEstadio(estadioSel);

                    DetallePartido detLocal =
                            new DetallePartido(
                                    0,
                                    eqLocal,
                                    Condicion.LOCAL,
                                    0
                            );

                    DetallePartido detVisit =
                            new DetallePartido(
                                    0,
                                    eqVisit,
                                    Condicion.VISITANTE,
                                    0
                            );
                    

                    nuevo.agregarDetalle(detLocal);
                    nuevo.agregarDetalle(detVisit);

                    System.out.println("Guardando partido...");
                    System.out.println(eqLocal.getNombre());
                    System.out.println(eqVisit.getNombre());

                    gestion.crearPartido(nuevo);

                } else {

                    partido.setHoraFecha(fecha);

                    partido.setGrupo(grupoSel);

                    partido.setEstadio(estadioSel);

                    gestion.actualizarPartido(partido);
                }

                dlg.dispose();

                loadData();

            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(
                        dlg,
                        "Formato inválido. Usa yyyy-MM-ddTHH:mm",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        dlg,
                        "Error al guardar: "
                                + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        footer.add(cancel);
        footer.add(save);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(footer, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel buildActionBtns(int row) {

        JPanel p = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 4, 4)
        );

        p.setOpaque(false);

        JButton edit = smallBtn(
                "✏",
                UIColors.BLUE,
                UIColors.INFO_BG
        );

        edit.addActionListener(e -> {

            if (row < partidosActuales.size()) {
                showMatchForm(partidosActuales.get(row));
            }
        });

        JButton del = smallBtn(
                "🗑",
                UIColors.RED,
                UIColors.ERROR_BG
        );

        del.addActionListener(e -> {

            if (row >= partidosActuales.size()) {
                return;
            }

            Partido partido = partidosActuales.get(row);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Eliminar este partido?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                try {

                    gestion.eliminarPartido(
                            partido.getIdPartido()
                    );

                    loadData();

                } catch (Exception ex) {

                    ex.printStackTrace();

                    JOptionPane.showMessageDialog(
                            this,
                            "Error al eliminar: "
                                    + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        p.add(edit);
        p.add(del);

        return p;
    }

    private JButton smallBtn(String text, Color fg, Color bg) {

        JButton btn = new JButton(text);

        btn.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 14)
        );

        btn.setForeground(fg);

        btn.setBackground(bg);

        btn.setFocusPainted(false);

        btn.setBorderPainted(false);

        btn.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btn.setPreferredSize(
                new Dimension(38, 30)
        );

        btn.setBorder(
                BorderFactory.createEmptyBorder()
        );

        return btn;
    }

    private JPanel buildFooter() {

        JPanel p = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 14)
        );

        p.setBackground(new Color(0xF8F9FC));

        p.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        UIColors.BORDER
                )
        );

        return p;
    }
}