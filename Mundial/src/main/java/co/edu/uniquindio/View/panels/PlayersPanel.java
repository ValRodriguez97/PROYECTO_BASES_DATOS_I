package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PlayersPanel extends JPanel {

    private final GestionDatosService gestion;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> posFilter;

    // Guardamos la lista original para poder editar/eliminar por índice real
    private List<Jugador> jugadoresActuales = List.of();

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

    /** Llamado desde MainFrame al navegar a este panel. */
    public void refresh() {
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
        content.setMaximumSize(new Dimension(1200, Integer.MAX_VALUE));

        JPanel header = buildHeader();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filters = buildFilters();
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tablePanel = buildTable();
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(header);
        content.add(Box.createVerticalStrut(18));
        content.add(filters);
        content.add(Box.createVerticalStrut(14));
        content.add(tablePanel);

        return content;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(UIFactory.heading("Jugadores"));
        left.add(Box.createVerticalStrut(4));
        left.add(UIFactory.subheading("Gestión de jugadores registrados en el torneo"));

        JButton addBtn = UIFactory.tealButton("+ Nuevo Jugador");
        addBtn.setPreferredSize(new Dimension(180, 42));
        addBtn.addActionListener(e -> showPlayerForm(null));

        p.add(left, BorderLayout.WEST);
        p.add(addBtn, BorderLayout.EAST);
        return p;
    }

    private JPanel buildFilters() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, UIColors.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = UIFactory.sectionTitle("Filtros de búsqueda");

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);
        searchField = UIFactory.textField("Buscar jugador...");
        searchField.setPreferredSize(new Dimension(280, 38));

        // FIX: el filtro de posición debe coincidir con los nombres reales de BD.
        // Se usan los mismos valores que devuelve PosicionDAOImpl (configurados en BD).
        posFilter = UIFactory.comboBox("Todas las posiciones",
                "Portero", "Defensa", "Centrocampista", "Delantero");
        posFilter.setPreferredSize(new Dimension(200, 38));

        JButton btn = UIFactory.tealButton("Buscar");
        btn.setPreferredSize(new Dimension(120, 38));
        btn.addActionListener(e -> loadData());

        row.add(searchField);
        row.add(posFilter);
        row.add(btn);

        card.add(title);
        card.add(Box.createVerticalStrut(14));
        card.add(row);
        return card;
    }

    private JPanel buildTable() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowHeight(42);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(44);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setMaxWidth(54);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setMaxWidth(82);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setMaxWidth(78);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setMaxWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(110);
        table.getColumnModel().getColumn(8).setMaxWidth(120);

        // Renderer de posición con badge de color
        table.getColumnModel().getColumn(3).setCellRenderer((t, v, sel, foc, r, c) -> {
            String pos = v != null ? v.toString() : "";
            Color bg, fg;
            switch (pos) {
                case "Portero"        -> { bg = new Color(0xF3E8FF); fg = new Color(0x6D28D9); }
                case "Defensa"        -> { bg = UIColors.SUCCESS_BG; fg = UIColors.SUCCESS_FG; }
                case "Centrocampista" -> { bg = UIColors.INFO_BG;    fg = UIColors.INFO_FG;    }
                default               -> { bg = UIColors.ERROR_BG;   fg = UIColors.RED;        }
            }
            JLabel badge = UIFactory.badge(pos, bg, fg);
            badge.setOpaque(true);
            badge.setBackground(sel ? UIColors.PURPLE_PALE : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            wrap.setBackground(badge.getBackground());
            wrap.add(badge);
            return wrap;
        });

        // Renderer de valor de mercado
        table.getColumnModel().getColumn(7).setCellRenderer((t, v, sel, foc, r, c) -> {
            JLabel l = new JLabel(v != null ? v.toString() : "", SwingConstants.RIGHT);
            l.setFont(UIFonts.LABEL_BOLD);
            l.setForeground(sel ? UIColors.PURPLE : UIColors.BLUE);
            l.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 10));
            return l;
        });

        // Renderer y editor de acciones
        table.getColumnModel().getColumn(8).setCellRenderer(
                (t, v, sel, foc, r, c) -> buildActionBtns(r));
        table.getColumnModel().getColumn(8).setCellEditor(
                new DefaultCellEditor(new JCheckBox()) {
                    @Override
                    public Component getTableCellEditorComponent(
                            JTable t, Object v, boolean sel, int row, int col) {
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

    /** Lee del backend y repinta la tabla respetando los filtros activos. */
    private void loadData() {
        model.setRowCount(0);
        try {
            jugadoresActuales = gestion.listarJugadores();
            String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
            String pos    = posFilter   != null && posFilter.getSelectedIndex() > 0
                    ? (String) posFilter.getSelectedItem() : "";
            int idx = 1;
            for (Jugador j : jugadoresActuales) {
                String posNom = j.getPosicion() != null ? j.getPosicion().getNombre() : "";
                if (!search.isEmpty()
                        && !j.getNombre().toLowerCase().contains(search)
                        && !j.getApellido().toLowerCase().contains(search)) continue;
                if (!pos.isEmpty() && !pos.equals(posNom)) continue;

                model.addRow(new Object[]{
                    idx++,
                    j.getNombre() + " " + j.getApellido(),
                    j.getEquipo()   != null ? j.getEquipo().getNombre()    : "-",
                    posNom,
                    j.getEdad(),
                    j.getEstatura() + " m",
                    j.getPeso()     + " kg",
                    // FIX: mostrar formateado con 2 decimales
                    j.getValorMercado() != null
                            ? String.format("%.2f", j.getValorMercado()) : "-",
                    ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar jugadores: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildActionBtns(int row) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        p.setOpaque(false);

        JButton edit = smallBtn("✏", UIColors.BLUE, UIColors.INFO_BG);
        edit.addActionListener(e -> {
            if (row < jugadoresActuales.size()) showPlayerForm(jugadoresActuales.get(row));
        });

        JButton del = smallBtn("🗑", UIColors.RED, UIColors.ERROR_BG);
        del.addActionListener(e -> {
            if (row >= jugadoresActuales.size()) return;
            Jugador j = jugadoresActuales.get(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar al jugador " + j.getNombre() + " " + j.getApellido() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    gestion.eliminarJugador(j.getIdJugador());
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        p.add(edit);
        p.add(del);
        return p;
    }

    private JButton smallBtn(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(38, 30));
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    /**
     * Abre el formulario modal para crear o editar un jugador.
     * @param jugador null = nuevo jugador; objeto = editar existente.
     */
    private void showPlayerForm(Jugador jugador) {
        boolean esNuevo = (jugador == null);

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                esNuevo ? "Nuevo Jugador" : "Editar Jugador",
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        // Cargar combos desde el backend
        List<Equipo>   equipos    = List.of();
        List<Posicion> posiciones = List.of();
        try { equipos    = gestion.listarEquipos();    } catch (Exception ignored) {}
        try { posiciones = gestion.listarPosiciones(); } catch (Exception ignored) {}

        String[] equipoNoms = equipos.stream().map(Equipo::getNombre).toArray(String[]::new);
        String[] posNoms    = posiciones.stream().map(Posicion::getNombre).toArray(String[]::new);

        JTextField nameF  = UIFactory.textField("Ej: Lionel");
        JTextField apF    = UIFactory.textField("Ej: Messi");
        JTextField fechaF = UIFactory.textField("Ej: 1987-06-24");
        JTextField estF   = UIFactory.textField("Ej: 1.70");
        JTextField pesoF  = UIFactory.textField("Ej: 67");
        JTextField valorF = UIFactory.textField("Ej: 30.00");

        JComboBox<String> posBox  = posNoms.length > 0
                ? UIFactory.comboBox(posNoms)
                : UIFactory.comboBox("Portero", "Defensa", "Centrocampista", "Delantero");
        JComboBox<String> teamBox = equipoNoms.length > 0
                ? UIFactory.comboBox(equipoNoms)
                : UIFactory.comboBox("(sin equipos registrados)");

        // Pre-rellenar si es edición
        if (!esNuevo) {
            nameF.setText(jugador.getNombre());
            apF.setText(jugador.getApellido());
            if (jugador.getFechaNacimiento() != null)
                fechaF.setText(jugador.getFechaNacimiento().toString());
            if (jugador.getEstatura() != null)
                estF.setText(jugador.getEstatura().toPlainString());
            if (jugador.getPeso() != null)
                pesoF.setText(jugador.getPeso().toPlainString());
            if (jugador.getValorMercado() != null)
                valorF.setText(jugador.getValorMercado().toPlainString());
            if (jugador.getPosicion() != null)
                posBox.setSelectedItem(jugador.getPosicion().getNombre());
            if (jugador.getEquipo() != null)
                teamBox.setSelectedItem(jugador.getEquipo().getNombre());
        }

        form.add(UIFactory.formLabel("Nombre"));             form.add(nameF);
        form.add(UIFactory.formLabel("Apellido"));           form.add(apF);
        form.add(UIFactory.formLabel("Fecha Nacimiento"));   form.add(fechaF);
        form.add(UIFactory.formLabel("Posición"));           form.add(posBox);
        form.add(UIFactory.formLabel("Equipo"));             form.add(teamBox);
        form.add(UIFactory.formLabel("Estatura (m)"));       form.add(estF);
        form.add(UIFactory.formLabel("Peso (kg)"));          form.add(pesoF);
        form.add(UIFactory.formLabel("Valor Mercado (M€)")); form.add(valorF);

        JPanel footer = buildDialogFooter();
        JButton cancel = UIFactory.outlineButton("Cancelar");
        cancel.addActionListener(e -> dlg.dispose());

        final List<Equipo>   equiposFinal    = equipos;
        final List<Posicion> posicionesFinal = posiciones;

        JButton save = UIFactory.tealButton(esNuevo ? "Guardar" : "Actualizar");
        save.addActionListener(e -> {
            String nombre   = nameF.getText().trim();
            String apellido = apF.getText().trim();
            String fechaTxt = fechaF.getText().trim();
            String estTxt   = estF.getText().trim();
            String pesoTxt  = pesoF.getText().trim();
            String valorTxt = valorF.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                        "Nombre y apellido son obligatorios.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // FIX: parsear a LocalDate
                LocalDate fechaNac = fechaTxt.isEmpty() ? null : LocalDate.parse(fechaTxt);

                // FIX: los campos del modelo son BigDecimal, no double
                BigDecimal estatura = estTxt.isEmpty()   ? BigDecimal.ZERO : new BigDecimal(estTxt);
                BigDecimal peso     = pesoTxt.isEmpty()  ? BigDecimal.ZERO : new BigDecimal(pesoTxt);
                BigDecimal valor    = valorTxt.isEmpty() ? BigDecimal.ZERO : new BigDecimal(valorTxt);

                int idxEq  = teamBox.getSelectedIndex();
                int idxPos = posBox.getSelectedIndex();
                Equipo   eqSel  = (!equiposFinal.isEmpty()    && idxEq  >= 0) ? equiposFinal.get(idxEq)     : null;
                Posicion posSel = (!posicionesFinal.isEmpty() && idxPos >= 0) ? posicionesFinal.get(idxPos)  : null;

                if (esNuevo) {
                    // FIX: construir objeto Jugador y llamar gestion.crearJugador(Jugador)
                    Jugador nuevo = new Jugador();
                    nuevo.setNombre(nombre);
                    nuevo.setApellido(apellido);
                    nuevo.setFechaNacimiento(fechaNac);
                    nuevo.setEstatura(estatura);
                    nuevo.setPeso(peso);
                    nuevo.setValorMercado(valor);
                    nuevo.setPosicion(posSel);
                    nuevo.setEquipo(eqSel);
                    gestion.crearJugador(nuevo);
                } else {
                    // FIX: actualizar los campos con BigDecimal y llamar gestion.actualizarJugador(Jugador)
                    jugador.setNombre(nombre);
                    jugador.setApellido(apellido);
                    jugador.setFechaNacimiento(fechaNac);
                    jugador.setEstatura(estatura);
                    jugador.setPeso(peso);
                    jugador.setValorMercado(valor);
                    jugador.setPosicion(posSel);
                    jugador.setEquipo(eqSel);
                    gestion.actualizarJugador(jugador);
                }

                dlg.dispose();
                loadData(); // refrescar tabla desde BD

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dlg,
                        "Fecha inválida. Usa el formato yyyy-MM-dd.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg,
                        "Estatura, peso y valor deben ser números válidos (usa punto decimal).", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg,
                        "Error al guardar: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
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
}