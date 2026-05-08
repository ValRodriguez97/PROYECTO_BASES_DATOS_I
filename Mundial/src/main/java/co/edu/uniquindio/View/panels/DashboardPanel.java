package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DashboardPanel extends JPanel {

      private final GestionDatosService gestion;

      public DashboardPanel(GestionDatosService gestion) {
            this.gestion = gestion;
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
            content.add(Box.createVerticalStrut(28));

            content.add(buildStatsRow());
            content.add(Box.createVerticalStrut(28));

            content.add(buildMainRow());
            content.add(Box.createVerticalStrut(28));

            content.add(buildQuickActions());

            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);

            JLabel title = UIFactory.heading("Dashboard");
            JLabel sub = UIFactory.subheading("Vista general del sistema FIFA World Cup 2026");

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.add(title);
            left.add(Box.createVerticalStrut(4));
            left.add(sub);

            p.add(left, BorderLayout.WEST);
            return p;
      }

      private JPanel buildStatsRow() {
            JPanel p = new JPanel(new GridLayout(1, 6, 16, 0));
            p.setOpaque(false);

            String[] labels  = {"Equipos", "Jugadores", "Estadios", "Partidos", "Usuarios", "Grupos"};
            String[] values  = {"48",       "180+",      "12",       "36",       "15",       "12"};
            String[] symbols = {"⚽",        "👤",         "🏟",       "📅",       "⚙",        "🔷"};
            Color[]  colors  = {UIColors.PURPLE, UIColors.BLUE, UIColors.TURQUOISE,
                              UIColors.MAGENTA, UIColors.RED, new Color(0xF59E0B)};

            try {
                  int equipos  = gestion.listarEquipos().size();
                  int jugadores = gestion.listarJugadores().size();
                  int estadios  = gestion.listarEstadios().size();
                  int partidos  = gestion.listarPartidos().size();
                  int grupos    = gestion.listarGrupos().size();
                  values[0] = String.valueOf(equipos);
                  values[1] = String.valueOf(jugadores);
                  values[2] = String.valueOf(estadios);
                  values[3] = String.valueOf(partidos);
                  values[5] = String.valueOf(grupos);
            } catch (Exception ignored) {}

            for (int i = 0; i < labels.length; i++) {
                  p.add(UIFactory.statCard(values[i], labels[i], colors[i], symbols[i]));
            }
            return p;
      }

      private JPanel buildMainRow() {
            JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
            p.setOpaque(false);

            p.add(buildMatchesCard());
            p.add(buildPlayersCard());
            return p;
      }

      private JPanel buildMatchesCard() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel header = buildCardHeader("📅  Próximos Partidos", UIColors.PURPLE, UIColors.BLUE);
            card.add(header, BorderLayout.NORTH);

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            String[][] matches = {
                  {"México",         "Argentina",  "2026-06-11", "Estadio Azteca"},
                  {"Estados Unidos", "Brasil",     "2026-06-12", "MetLife Stadium"},
                  {"Canadá",         "España",     "2026-06-13", "BMO Stadium"},
            };

            for (String[] m : matches) {
                  content.add(buildMatchRow(m[0], m[1], m[2], m[3]));
                  content.add(Box.createVerticalStrut(10));
            }

            card.add(content, BorderLayout.CENTER);
            return card;
      }

      private JPanel buildMatchRow(String t1, String t2, String date, String stadium) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(false);
            p.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            JPanel teams = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            teams.setOpaque(false);
            JLabel t1L = new JLabel(t1); t1L.setFont(UIFonts.LABEL_BOLD); t1L.setForeground(UIColors.TEXT_PRIMARY);
            JLabel vs  = new JLabel("vs"); vs.setFont(UIFonts.BODY_SM); vs.setForeground(UIColors.TEXT_MUTED);
            JLabel t2L = new JLabel(t2); t2L.setFont(UIFonts.LABEL_BOLD); t2L.setForeground(UIColors.TEXT_PRIMARY);
            teams.add(t1L); teams.add(vs); teams.add(t2L);

            JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            info.setOpaque(false);
            JLabel dateL = new JLabel("📅 " + date); dateL.setFont(UIFonts.BODY_SM); dateL.setForeground(UIColors.TEXT_SECONDARY);
            info.add(dateL);

            p.add(teams, BorderLayout.WEST);
            p.add(info, BorderLayout.EAST);
            return p;
      }

      private JPanel buildPlayersCard() {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new RoundedBorder(12, UIColors.BORDER));

            JPanel header = buildCardHeader("🏆  Jugadores Destacados", UIColors.BLUE, UIColors.TURQUOISE);
            card.add(header, BorderLayout.NORTH);

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            String[][] players = {
                  {"1", "Lionel Messi",    "Argentina", "$30M",  "Delantero"},
                  {"2", "Kylian Mbappé",   "Francia",   "$180M", "Delantero"},
                  {"3", "Erling Haaland",  "Noruega",   "$150M", "Delantero"},
                  {"4", "Vinicius Junior", "Brasil",     "$200M", "Extremo"},
            };

            for (String[] pl : players) {
                  content.add(buildPlayerRow(pl[0], pl[1], pl[2], pl[3], pl[4]));
                  content.add(Box.createVerticalStrut(10));
            }

            card.add(content, BorderLayout.CENTER);
            return card;
      }

      private JPanel buildPlayerRow(String rank, String name, String team, String value, String pos) {
            JPanel p = new JPanel(new BorderLayout(12, 0));
            p.setOpaque(false);
            p.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            JPanel numBox = new JPanel() {
                  { setPreferredSize(new Dimension(32, 32)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setPaint(new GradientPaint(0, 0, UIColors.BLUE, 32, 32, UIColors.TURQUOISE));
                  g2.fillOval(0, 0, 31, 31);
                  g2.setColor(Color.WHITE);
                  g2.setFont(UIFonts.LABEL_MD);
                  FontMetrics fm = g2.getFontMetrics();
                  g2.drawString(rank, (32 - fm.stringWidth(rank))/2, (32+fm.getAscent()-fm.getDescent())/2);
                  g2.dispose();
                  }
            };

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            JLabel nameL = new JLabel(name); nameL.setFont(UIFonts.LABEL_BOLD); nameL.setForeground(UIColors.TEXT_PRIMARY);
            JLabel teamL = new JLabel(team + " · " + pos); teamL.setFont(UIFonts.BODY_SM); teamL.setForeground(UIColors.TEXT_SECONDARY);
            info.add(nameL); info.add(teamL);

            JLabel valueL = new JLabel(value);
            valueL.setFont(UIFonts.LABEL_BOLD);
            valueL.setForeground(UIColors.BLUE);

            p.add(numBox, BorderLayout.WEST);
            p.add(info, BorderLayout.CENTER);
            p.add(valueL, BorderLayout.EAST);
            return p;
      }

      private JPanel buildCardHeader(String title, Color from, Color to) {
            JPanel p = new JPanel(new BorderLayout()) {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setPaint(new GradientPaint(0, 0, new Color(from.getRed(), from.getGreen(), from.getBlue(), 15),
                              getWidth(), 0, new Color(to.getRed(), to.getGreen(), to.getBlue(), 15)));
                  g2.fillRect(0, 0, getWidth(), getHeight());
                  g2.dispose();
                  super.paintComponent(g);
                  }
            };
            p.setOpaque(false);
            p.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 20, 16, 20)
            ));
            JLabel l = UIFactory.sectionTitle(title);
            p.add(l, BorderLayout.WEST);
            return p;
      }

      private JPanel buildQuickActions() {
            JPanel outer = new JPanel(new BorderLayout()) {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setPaint(new GradientPaint(0, 0, UIColors.PURPLE, getWidth(), 0, UIColors.TURQUOISE));
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                  g2.dispose();
                  super.paintComponent(g);
                  }
            };
            outer.setOpaque(false);
            outer.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

            JLabel title = new JLabel("⚡  Acciones Rápidas");
            title.setFont(UIFonts.HEADING_MD);
            title.setForeground(Color.WHITE);

            JPanel btnRow = new JPanel(new GridLayout(1, 4, 16, 0));
            btnRow.setOpaque(false);

            String[][] actions = {{"⚽ Nuevo Equipo", "teams"}, {"👤 Nuevo Jugador", "players"},
                                    {"📅 Nuevo Partido", "matches"}, {"📊 Ver Reportes", "reports"}};

            for (String[] a : actions) {
                  JPanel btn = new JPanel() {
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 255, 255, 30));
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                        g2.dispose();
                        super.paintComponent(g);
                  }
                  };
                  btn.setOpaque(false);
                  btn.setLayout(new BorderLayout());
                  btn.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
                  btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                  JLabel bl = new JLabel(a[0]);
                  bl.setFont(UIFonts.LABEL_BOLD);
                  bl.setForeground(Color.WHITE);
                  btn.add(bl, BorderLayout.CENTER);
                  btnRow.add(btn);
            }

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(title, BorderLayout.WEST);

            outer.add(top, BorderLayout.NORTH);
            outer.add(Box.createVerticalStrut(16), BorderLayout.CENTER);
            outer.add(btnRow, BorderLayout.SOUTH);

            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(outer);
            return wrap;
      }
}
