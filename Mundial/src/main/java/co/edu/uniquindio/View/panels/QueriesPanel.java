package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QueriesPanel extends JPanel {

    private final GestionDatosService gestion;
    private final MundialService mundial;

    private JTable table;
    private DefaultTableModel model;

    private JLabel statusLabel;
    private JComboBox<String> estadioCombo;

    private JPanel filterCard;
    private JPanel resultsContainer;

    private JButton runBtn;
    private JLabel estadioLabel;

    // Lista viva: se recarga desde el backend cada vez que se selecciona A2
    private List<Estadio> estadiosList = new ArrayList<>();

    private int selectedQuery = -1;

    private final List<JPanel> queryCards = new ArrayList<>();

    private static final String[][] QUERIES = {
            {"A1", "Jugador más costoso por confederación",
                    "Jugador con mayor valor de mercado en cada confederación"},
            {"A2", "Partidos por estadio",
                    "Todos los partidos programados en un estadio seleccionado"},
            {"A3", "Equipo más costoso por país sede",
                    "Equipo con mayor valor total en cada país anfitrión"},
            {"A4", "Jugadores sub-21 por equipo",
                    "Cantidad de jugadores menores de 21 años por equipo"},
    };

    private static final Color[] QUERY_COLORS = {
            UIColors.PURPLE,
            UIColors.BLUE,
            UIColors.TURQUOISE,
            UIColors.MAGENTA
    };

    public QueriesPanel(GestionDatosService gestion, MundialService mundial) {
        this.gestion = gestion;
        this.mundial = mundial;
        setBackground(UIColors.BG_PAGE);
        setLayout(new BorderLayout());
        build();
    }

    /**
     * Llamado desde MainFrame cada vez que el usuario navega a este panel,
     * para que los combos y datos estén siempre frescos.
     */
    public void refresh() {
        if (selectedQuery == 1) {
            recargarComboEstadios();
        }
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
        content.setLayout(new BorderLayout(0, 20));
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setOpaque(false);
        main.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(20, 0));
        center.setOpaque(false);

        JPanel queries = buildQuerySelector();
        queries.setPreferredSize(new Dimension(380, 700));
        center.add(queries, BorderLayout.WEST);
        center.add(buildResultsPanel(), BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);
        content.add(main, BorderLayout.CENTER);
        return content;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(UIFactory.heading("Consultas"));
        left.add(Box.createVerticalStrut(4));
        left.add(UIFactory.subheading(
                "Ejecuta las consultas requeridas sobre los datos del torneo"));
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel buildQuerySelector() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(350, 620));
        card.setBorder(new RoundedBorder(10, UIColors.BORDER));
        card.add(sectionHeader("Consultas Disponibles"), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(Color.WHITE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < QUERIES.length; i++) {
            JPanel queryCard = buildQueryBtn(i);
            queryCards.add(queryCard);
            list.add(queryCard);
            list.add(Box.createVerticalStrut(10));
        }

        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQueryBtn(int index) {
        Color accent = QUERY_COLORS[index];

        JPanel btn = new JPanel(new BorderLayout(0, 4));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, UIColors.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(320, 110));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setOpaque(false);

        JLabel tag = UIFactory.badge(
                QUERIES[index][0],
                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25),
                accent
        );

        JLabel title = new JLabel(QUERIES[index][1]);
        title.setFont(UIFonts.LABEL_BOLD);
        title.setForeground(UIColors.TEXT_PRIMARY);

        top.add(tag);
        top.add(title);

        JLabel desc = new JLabel(
                "<html><body style='width:230px'>" + QUERIES[index][2] + "</body></html>");
        desc.setFont(UIFonts.BODY_SM);
        desc.setForeground(UIColors.TEXT_SECONDARY);

        btn.add(top, BorderLayout.NORTH);
        btn.add(desc, BorderLayout.CENTER);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedQuery = index;

                for (JPanel c : queryCards) c.setBackground(Color.WHITE);
                btn.setBackground(new Color(
                        accent.getRed(), accent.getGreen(), accent.getBlue(), 20));

                boolean needsFilter = (index == 1);

                if (needsFilter) {
                    // CORRECCIÓN PRINCIPAL: recargar combo desde BD antes de mostrarlo
                    recargarComboEstadios();
                }

                filterCard.setVisible(needsFilter);

                SwingUtilities.invokeLater(() -> {
                    filterCard.revalidate();
                    resultsContainer.revalidate();
                    revalidate();
                    repaint();
                });

                if (!needsFilter) {
                    executeQuery();
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedQuery != index) btn.setBackground(UIColors.BG_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedQuery != index) btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    /**
     * Consulta el backend y recarga el JComboBox de estadios con datos frescos.
     */
    private void recargarComboEstadios() {
        estadiosList = new ArrayList<>();
        try {
            estadiosList = gestion.listarEstadios();
        } catch (Exception ignored) {}

        estadioCombo.removeAllItems();
        if (estadiosList.isEmpty()) {
            estadioCombo.addItem("(sin estadios registrados)");
        } else {
            for (Estadio est : estadiosList) {
                estadioCombo.addItem(est.getNombre());
            }
        }
    }

    private JPanel buildResultsPanel() {
        resultsContainer = new JPanel(new BorderLayout(0, 14));
        resultsContainer.setBackground(UIColors.BG_PAGE);

        // --- Tarjeta de filtro (A2) ---
        filterCard = new JPanel();
        filterCard.setLayout(new BoxLayout(filterCard, BoxLayout.X_AXIS));
        filterCard.setBackground(Color.WHITE);
        filterCard.setPreferredSize(new Dimension(0, 64));
        filterCard.setVisible(false);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, UIColors.BORDER),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        // Combo vacío al inicio; se llena al seleccionar A2
        estadioCombo = UIFactory.comboBox(new String[]{"(sin estadios registrados)"});
        estadioCombo.setMaximumSize(new Dimension(260, 40));

        estadioLabel = UIFactory.formLabel("Estadio:");

        runBtn = UIFactory.primaryButton("▶ Ejecutar");
        runBtn.addActionListener(e -> executeQuery());

        filterCard.add(estadioLabel);
        filterCard.add(Box.createHorizontalStrut(12));
        filterCard.add(estadioCombo);
        filterCard.add(Box.createHorizontalStrut(12));
        filterCard.add(runBtn);
        filterCard.add(Box.createHorizontalGlue());

        // --- Tarjeta de resultados ---
        JPanel resultsCard = new JPanel(new BorderLayout());
        resultsCard.setBackground(Color.WHITE);
        resultsCard.setBorder(new RoundedBorder(10, UIColors.BORDER));

        JPanel rh = new JPanel(new BorderLayout());
        rh.setBackground(new Color(0xF8F9FC));
        rh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLbl = UIFactory.sectionTitle("Resultados");
        statusLabel = new JLabel("Selecciona una consulta");
        statusLabel.setFont(UIFonts.BODY_SM);
        statusLabel.setForeground(UIColors.TEXT_MUTED);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        rh.add(titleLbl, BorderLayout.WEST);
        rh.add(statusLabel, BorderLayout.EAST);
        resultsCard.add(rh, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"—"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UIFactory.styleTable(table);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        JScrollPane tableScroll = UIFactory.scrollPane(table);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        resultsCard.add(tableContainer, BorderLayout.CENTER);

        resultsContainer.add(filterCard, BorderLayout.NORTH);
        resultsContainer.add(resultsCard, BorderLayout.CENTER);

        return resultsContainer;
    }

    private void executeQuery() {
        if (selectedQuery < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona primero una consulta de la lista.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            switch (selectedQuery) {
                case 0 -> runA1();
                case 1 -> runA2();
                case 2 -> runA3();
                case 3 -> runA4();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al ejecutar la consulta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
            SwingUtilities.invokeLater(() -> { revalidate(); repaint(); });
        }
    }

    private void runA1() throws Exception {
        List<Jugador> lista = mundial.jugadorMasCostosoPorConfederacion();
        resetTable("Confederación", "Equipo", "Jugador", "Valor (M€)");
        for (Jugador j : lista) {
            String conf = j.getEquipo() != null && j.getEquipo().getConfederacion() != null
                    ? j.getEquipo().getConfederacion().getNombre() : "-";
            String equipo = j.getEquipo() != null ? j.getEquipo().getNombre() : "-";
            model.addRow(new Object[]{
                    conf, equipo,
                    j.getNombre() + " " + j.getApellido(),
                    String.format("%.2f", j.getValorMercado())
            });
        }
        statusLabel.setText(lista.size() + " resultado(s)");
    }

    private void runA2() throws Exception {
        int idx = estadioCombo.getSelectedIndex();
        if (estadiosList.isEmpty() || idx < 0 || idx >= estadiosList.size()) {
            JOptionPane.showMessageDialog(this,
                    "No hay estadios disponibles o selecciona uno válido.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEstadio = estadiosList.get(idx).getIdEstadio();
        List<Partido> partidos = mundial.partidosPorEstadio(idEstadio);
        resetTable("ID", "Grupo", "Local", "Visitante", "Fecha / Hora");
        for (Partido p : partidos) {
            model.addRow(new Object[]{
                    p.getIdPartido(),
                    p.getGrupo()           != null ? "Grupo " + p.getGrupo().getLetra()   : "-",
                    p.getEquipoLocal()     != null ? p.getEquipoLocal().getNombre()        : "—",
                    p.getEquipoVisitante() != null ? p.getEquipoVisitante().getNombre()    : "—",
                    p.getHoraFecha()       != null ? p.getHoraFecha().toString()           : "-"
            });
        }
        statusLabel.setText(partidos.size() + " partido(s)");
    }

    private void runA3() throws Exception {
        List<Object[]> lista = mundial.equipoMasCostosoPorPaisSede();
        resetTable("País Sede", "Equipo", "Valor Total (M€)");
        for (Object[] fila : lista) {
            model.addRow(new Object[]{ fila[0], fila[1], String.format("%.2f", fila[2]) });
        }
        statusLabel.setText(lista.size() + " resultado(s)");
    }

    private void runA4() throws Exception {
        List<Object[]> lista = mundial.cantidadJugadoresSub21PorEquipo();
        resetTable("Equipo", "Jugadores sub-21");
        for (Object[] fila : lista) {
            model.addRow(new Object[]{ fila[0], fila[1] });
        }
        statusLabel.setText(lista.size() + " equipo(s)");
    }

    private void resetTable(String... cols) {
        model.setRowCount(0);
        model.setColumnCount(0);
        for (String col : cols) model.addColumn(col);
        table.revalidate();
        table.repaint();
        statusLabel.setText("…");
    }

    private JPanel sectionHeader(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xF8F9FC));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        p.add(UIFactory.sectionTitle(title), BorderLayout.WEST);
        return p;
    }
}