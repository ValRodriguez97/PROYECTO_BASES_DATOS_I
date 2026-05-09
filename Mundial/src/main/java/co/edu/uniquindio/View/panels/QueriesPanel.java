package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

      public class QueriesPanel extends JPanel {

      private final GestionDatosService gestion;
      private final MundialService mundial;

      private JTable table;
      private DefaultTableModel model;
      private JLabel statusLabel;
      private JComboBox<String> estadioCombo;
      private List<Estadio> estadiosList = List.of();
      private int selectedQuery = -1;

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
            UIColors.PURPLE, UIColors.BLUE, UIColors.TURQUOISE, UIColors.MAGENTA
      };

      public QueriesPanel(GestionDatosService gestion, MundialService mundial) {
            this.gestion = gestion;
            this.mundial = mundial;
            setBackground(UIColors.BG_PAGE);
            setLayout(new BorderLayout());
            build();
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
            content.add(Box.createVerticalStrut(20));

            JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
            body.setOpaque(false);
            body.setMaximumSize(new Dimension(Integer.MAX_VALUE, 560));
            body.add(buildQuerySelector());
            body.add(buildResultsPanel());
            content.add(body);

            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.add(UIFactory.heading("Consultas"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Ejecuta las consultas requeridas sobre los datos del torneo"));

            p.add(left, BorderLayout.WEST);
            return p;
      }

      private JPanel buildQuerySelector() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(10, UIColors.BORDER));

            JPanel th = sectionHeader("Consultas Disponibles");
            card.add(th, BorderLayout.NORTH);

            JPanel list = new JPanel();
            list.setBackground(Color.WHITE);
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            list.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            for (int i = 0; i < QUERIES.length; i++) {
                  list.add(buildQueryBtn(i));
                  list.add(Box.createVerticalStrut(8));
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
                  BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            top.setOpaque(false);
            JLabel tag   = UIFactory.badge(QUERIES[index][0], new Color(
                  accent.getRed(), accent.getGreen(), accent.getBlue(), 25), accent);
            JLabel title = new JLabel(QUERIES[index][1]);
            title.setFont(UIFonts.LABEL_BOLD);
            title.setForeground(UIColors.TEXT_PRIMARY);
            top.add(tag); top.add(title);

            JLabel desc = new JLabel("<html>" + QUERIES[index][2] + "</html>");
            desc.setFont(UIFonts.BODY_SM);
            desc.setForeground(UIColors.TEXT_SECONDARY);

            btn.add(top,  BorderLayout.NORTH);
            btn.add(desc, BorderLayout.CENTER);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseClicked(java.awt.event.MouseEvent e) {
                  selectedQuery = index;
                  executeQuery();
                  }
                  public void mouseEntered(java.awt.event.MouseEvent e) {
                  btn.setBackground(UIColors.BG_HOVER);
                  }
                  public void mouseExited(java.awt.event.MouseEvent e) {
                  btn.setBackground(Color.WHITE);
                  }
            });
            return btn;
      }

      private JPanel buildResultsPanel() {
            JPanel outer = new JPanel();
            outer.setBackground(UIColors.BG_PAGE);
            outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

            JPanel filterCard = new JPanel(new BorderLayout(12, 0));
            filterCard.setBackground(Color.WHITE);
            filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            filterCard.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(10, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));

            try { estadiosList = gestion.listarEstadios(); } catch (Exception ignored) {}
            String[] estadioNoms = estadiosList.isEmpty()
                  ? new String[]{"(sin estadios registrados)"}
                  : estadiosList.stream().map(Estadio::getNombre).toArray(String[]::new);
            estadioCombo = UIFactory.comboBox(estadioNoms);

            JLabel lbl = UIFactory.formLabel("Estadio (consulta A2):");

            JButton runBtn = UIFactory.primaryButton("▶  Ejecutar");
            runBtn.addActionListener(e -> executeQuery());

            filterCard.add(lbl, BorderLayout.WEST);
            filterCard.add(estadioCombo, BorderLayout.CENTER);
            filterCard.add(runBtn, BorderLayout.EAST);
            outer.add(filterCard);
            outer.add(Box.createVerticalStrut(12));

            JPanel resultsCard = new JPanel(new BorderLayout());
            resultsCard.setBackground(Color.WHITE);
            resultsCard.setBorder(new RoundedBorder(10, UIColors.BORDER));

            JPanel rh = sectionHeader("Resultados");
            statusLabel = new JLabel("Selecciona una consulta");
            statusLabel.setFont(UIFonts.BODY_SM);
            statusLabel.setForeground(UIColors.TEXT_MUTED);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
            rh.add(statusLabel, BorderLayout.EAST);
            resultsCard.add(rh, BorderLayout.NORTH);

            model = new DefaultTableModel(new String[]{"—"}, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            resultsCard.add(UIFactory.scrollPane(table), BorderLayout.CENTER);

            outer.add(resultsCard);
            return outer;
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
            }
      }

      private void runA1() throws Exception {
            List<Jugador> lista = mundial.jugadorMasCostosoPorConfederacion();
            resetTable("Confederación", "Equipo", "Jugador", "Valor (M€)");
            for (Jugador j : lista) {
                  String conf  = j.getEquipo() != null && j.getEquipo().getConfederacion() != null
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
            if (idx < 0 || idx >= estadiosList.size()) {
                  JOptionPane.showMessageDialog(this,
                  "Selecciona un estadio válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
            }
            int idEstadio = estadiosList.get(idx).getIdEstadio();
            List<Partido> partidos = mundial.partidosPorEstadio(idEstadio);
            resetTable("ID", "Grupo", "Local", "Visitante", "Fecha / Hora");
            for (Partido p : partidos) {
                  model.addRow(new Object[]{
                        p.getIdPartido(),
                        p.getGrupo() != null ? "Grupo " + p.getGrupo().getLetra(): "-",
                        p.getEquipoLocal() != null ? p.getEquipoLocal().getNombre(): "—",
                        p.getEquipoVisitante() != null ? p.getEquipoVisitante().getNombre(): "—",
                        p.getHoraFecha() != null ? p.getHoraFecha().toString(): "-"
                  });
            }
            statusLabel.setText(partidos.size() + " partido(s)");
      }

      private void runA3() throws Exception {
            List<Object[]> lista = mundial.equipoMasCostosoPorPaisSede();
            resetTable("País Sede", "Equipo", "Valor Total (M€)");
            for (Object[] fila : lista) {
                  model.addRow(new Object[]{
                  fila[0], fila[1], String.format("%.2f", fila[2])
                  });
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
            model = new DefaultTableModel(cols, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table.setModel(model);
            UIFactory.styleTable(table);
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