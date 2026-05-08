package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.*;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class QueriesPanel extends JPanel {

      private final GestionDatosService gestion;
      private final MundialService      mundial;

      private JPanel resultsCard;
      private JTable table;
      private DefaultTableModel model;
      private JLabel statusLabel;

      private JComboBox<String> estadioCombo;
      private List<Estadio>     estadiosList = List.of();

      private int selectedQuery = -1;

      private static final String[][] QUERIES = {
            {"a1", "Jugador más costoso por confederación",
                  "Muestra el jugador con mayor valor de mercado de cada confederación"},
            {"a2", "Partidos por estadio",
                  "Lista todos los partidos programados en un estadio seleccionado"},
            {"a3", "Equipo más costoso por país sede",
                  "Muestra el equipo con mayor valor total por cada país anfitrión"},
            {"a4", "Jugadores sub-21 por equipo",
                  "Cantidad de jugadores menores de 21 años en cada equipo"},
      };

      public QueriesPanel(GestionDatosService gestion, MundialService mundial) {
            this.gestion  = gestion;
            this.mundial  = mundial;
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
            content.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

            content.add(buildHeader());
            content.add(Box.createVerticalStrut(24));

            JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
            body.setOpaque(false);
            body.add(buildQuerySelector());
            body.add(buildResultsPanel());
            body.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
            content.add(body);
            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.add(UIFactory.heading("Consultas del Sistema"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Ejecuta las consultas requeridas sobre los datos del Mundial FIFA 2026"));
            p.add(left, BorderLayout.WEST);
            return p;
      }

      private JPanel buildQuerySelector() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel th = new JPanel(new BorderLayout());
            th.setBackground(new Color(0xF8F9FC));
            th.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)));
            th.add(UIFactory.sectionTitle("Consultas Disponibles"), BorderLayout.WEST);
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
            JPanel btn = new JPanel(new BorderLayout(0, 4));
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JLabel tag = UIFactory.badge(QUERIES[index][0].toUpperCase(),
                  UIColors.PURPLE_PALE, UIColors.PURPLE);
            JLabel title = new JLabel(QUERIES[index][1]);
            title.setFont(UIFonts.LABEL_BOLD);
            title.setForeground(UIColors.TEXT_PRIMARY);
            JLabel desc = new JLabel("<html>" + QUERIES[index][2] + "</html>");
            desc.setFont(UIFonts.BODY_SM);
            desc.setForeground(UIColors.TEXT_SECONDARY);

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            top.setOpaque(false);
            top.add(tag);
            top.add(title);

            btn.add(top,  BorderLayout.NORTH);
            btn.add(desc, BorderLayout.CENTER);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseClicked(java.awt.event.MouseEvent e) {
                  selectedQuery = index;
                  executeQuery();
                  }
                  public void mouseEntered(java.awt.event.MouseEvent e) {
                  btn.setBackground(UIColors.PURPLE_PALE);
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

            JPanel filterCard = new JPanel(new BorderLayout());
            filterCard.setBackground(Color.WHITE);
            filterCard.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(14, 20, 14, 20)));

            try { estadiosList = gestion.listarEstadios(); } catch (Exception ex) { estadiosList = List.of(); }
            String[] estadioNoms = estadiosList.stream()
                  .map(Estadio::getNombre).toArray(String[]::new);
            estadioCombo = UIFactory.comboBox(estadioNoms.length == 0
                  ? new String[]{"(sin estadios)"} : estadioNoms);

            JPanel filterRow = new JPanel(new BorderLayout(12, 0));
            filterRow.setOpaque(false);
            JLabel lbl = UIFactory.formLabel("Estadio (solo para consulta a2):");
            filterRow.add(lbl,          BorderLayout.WEST);
            filterRow.add(estadioCombo, BorderLayout.CENTER);

            JButton runBtn = purpleButton("▶  Ejecutar Consulta");
            runBtn.addActionListener(e -> executeQuery());
            filterRow.add(runBtn, BorderLayout.EAST);

            filterCard.add(filterRow, BorderLayout.CENTER);
            outer.add(filterCard);
            outer.add(Box.createVerticalStrut(16));

            resultsCard = new JPanel(new BorderLayout());
            resultsCard.setBackground(Color.WHITE);
            resultsCard.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel resHeader = new JPanel(new BorderLayout());
            resHeader.setBackground(new Color(0xF8F9FC));
            resHeader.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)));
            resHeader.add(UIFactory.sectionTitle("Resultados"), BorderLayout.WEST);
            statusLabel = new JLabel("Selecciona una consulta para ver los resultados");
            statusLabel.setFont(UIFonts.BODY_SM);
            statusLabel.setForeground(UIColors.TEXT_MUTED);
            resHeader.add(statusLabel, BorderLayout.EAST);
            resultsCard.add(resHeader, BorderLayout.NORTH);

            model = new DefaultTableModel(new String[]{"(sin datos)"}, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            UIFactory.styleTable(table);
            resultsCard.add(UIFactory.scrollPane(table), BorderLayout.CENTER);

            outer.add(resultsCard);
            return outer;
      }

      // -- logica de  ejecución --

      private void executeQuery() {
            if (selectedQuery < 0) {
                  JOptionPane.showMessageDialog(this,
                  "Selecciona primero una consulta.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
            String[] cols = {"Confederación","Equipo","Jugador","Valor (M€)"};
            resetTable(cols);
            for (Jugador j : lista) {
                  String conf = (j.getEquipo() != null && j.getEquipo().getConfederacion() != null)
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
                  JOptionPane.showMessageDialog(this, "Selecciona un estadio válido.",
                  "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
            }
            int idEstadio = estadiosList.get(idx).getIdEstadio();
            List<Partido> partidos = mundial.partidosPorEstadio(idEstadio);
            String[] cols = {"ID","Grupo","Local","Visitante","Fecha/Hora"};
            resetTable(cols);
            for (Partido p : partidos) {
                  model.addRow(new Object[]{
                        p.getIdPartido(),
                        p.getGrupo()   != null ? "Grupo " + p.getGrupo().getLetra() : "-",
                        p.getEquipoLocal()     != null ? p.getEquipoLocal().getNombre()     : "—",
                        p.getEquipoVisitante() != null ? p.getEquipoVisitante().getNombre() : "—",
                        p.getHoraFecha()       != null ? p.getHoraFecha().toString()        : "-"
                  });
            }
            statusLabel.setText(partidos.size() + " partido(s)");
      }

      private void runA3() throws Exception {
            List<Object[]> lista = mundial.equipoMasCostosoPorPaisSede();
            String[] cols = {"País Sede","Equipo","Valor Total (M€)"};
            resetTable(cols);
            for (Object[] fila : lista) {
                  model.addRow(new Object[]{
                        fila[0], fila[1],
                        String.format("%.2f", fila[2])
                  });
            }
            statusLabel.setText(lista.size() + " resultado(s)");
      }

      private void runA4() throws Exception {
            List<Object[]> lista = mundial.cantidadJugadoresSub21PorEquipo();
            String[] cols = {"Equipo","Jugadores sub-21"};
            resetTable(cols);
            for (Object[] fila : lista) {
                  model.addRow(new Object[]{ fila[0], fila[1] });
            }
            statusLabel.setText(lista.size() + " equipo(s)");
      }

      private void resetTable(String[] cols) {
            model = new DefaultTableModel(cols, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table.setModel(model);
            UIFactory.styleTable(table);
            statusLabel.setText("…");
      }

    // -- HELPER --

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
