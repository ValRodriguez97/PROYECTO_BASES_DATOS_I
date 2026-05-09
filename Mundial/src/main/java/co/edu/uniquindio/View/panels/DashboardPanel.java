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
            content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

            content.add(buildHeader());
            content.add(Box.createVerticalStrut(24));
            content.add(buildStatsRow());
            content.add(Box.createVerticalStrut(24));
            content.add(buildWelcomeCard());
            content.add(Box.createVerticalStrut(24));
            content.add(buildQuickActions());

            return content;
      }

      private JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.add(UIFactory.heading("Dashboard"));
            left.add(Box.createVerticalStrut(4));
            left.add(UIFactory.subheading("Vista general del sistema — FIFA World Cup 2026"));

            p.add(left, BorderLayout.WEST);
            return p;
      }

      private JPanel buildStatsRow() {
            JPanel p = new JPanel(new GridLayout(1, 5, 14, 0));
            p.setOpaque(false);
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            String[] labels  = {"Equipos", "Jugadores", "Estadios", "Partidos", "Grupos"};
            String[] symbols = {"⚽", "👤", "🏟", "📅", "🔷"};
            Color[]  colors  = {
                  UIColors.PURPLE, UIColors.BLUE,
                  UIColors.TURQUOISE, UIColors.MAGENTA, new Color(0xE65100)
            };

            int[] values = {0, 0, 0, 0, 0};
            try {
                  values[0] = gestion.listarEquipos().size();
                  values[1] = gestion.listarJugadores().size();
                  values[2] = gestion.listarEstadios().size();
                  values[3] = gestion.listarPartidos().size();
                  values[4] = gestion.listarGrupos().size();
            } catch (Exception ignored) {}

            for (int i = 0; i < labels.length; i++) {
                  p.add(UIFactory.statCard(String.valueOf(values[i]), labels[i], colors[i], symbols[i]));
            }
            return p;
      }

      private JPanel buildWelcomeCard() {
            JPanel card = new JPanel(new BorderLayout()) {
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(
                              0, 0, UIColors.PURPLE,
                              getWidth(), 0, new Color(0x1565C0));
                        g2.setPaint(gp);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                        g2.dispose();
                        super.paintComponent(g);
                  }
            };
            card.setOpaque(false);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
            card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

            JPanel texts = new JPanel();
            texts.setOpaque(false);
            texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("Sistema de Gestión — FIFA World Cup 2026");
            title.setFont(UIFonts.HEADING_LG);
            title.setForeground(Color.WHITE);

            JLabel sub = new JLabel("Ingresa datos desde los módulos del menú lateral.");
            sub.setFont(UIFonts.BODY_MD);
            sub.setForeground(new Color(255, 255, 255, 200));

            texts.add(title);
            texts.add(Box.createVerticalStrut(8));
            texts.add(sub);

            JLabel icon = new JLabel("⚽");
            icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 56));

            card.add(texts, BorderLayout.CENTER);
            card.add(icon,  BorderLayout.EAST);
            return card;
      }


      private JPanel buildQuickActions() {
            JPanel outer = new JPanel(new BorderLayout());
            outer.setOpaque(false);
            outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

            JLabel title = UIFactory.sectionTitle("Accesos Rápidos");
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

            String[][] actions = {
                  {"⚽", "Equipos", "Registrar equipos participantes"},
                  {"👤", "Jugadores", "Agregar jugadores al plantel"},
                  {"📅", "Partidos", "Programar encuentros"},
                  {"📊", "Reportes", "Generar reportes PDF"},
            };

            JPanel grid = new JPanel(new GridLayout(1, 4, 14, 0));
            grid.setOpaque(false);

            for (String[] a : actions) {
                  grid.add(buildActionCard(a[0], a[1], a[2]));
            }

            outer.add(title, BorderLayout.NORTH);
            outer.add(grid, BorderLayout.CENTER);
            return outer;
      }

      private JPanel buildActionCard(String icon, String label, String desc) {
            JPanel card = new JPanel(new BorderLayout(0, 6));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(10, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel iconL = new JLabel(icon);
            iconL.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));

            JLabel labelL = new JLabel(label);
            labelL.setFont(UIFonts.LABEL_BOLD);
            labelL.setForeground(UIColors.TEXT_PRIMARY);

            JLabel descL = new JLabel("<html>" + desc + "</html>");
            descL.setFont(UIFonts.BODY_SM);
            descL.setForeground(UIColors.TEXT_MUTED);

            JPanel textCol = new JPanel();
            textCol.setOpaque(false);
            textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
            textCol.add(labelL);
            textCol.add(Box.createVerticalStrut(3));
            textCol.add(descL);

            card.add(iconL,   BorderLayout.NORTH);
            card.add(textCol, BorderLayout.CENTER);

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                  public void mouseEntered(java.awt.event.MouseEvent e) {
                  card.setBackground(UIColors.BG_HOVER);
                  }
                  public void mouseExited(java.awt.event.MouseEvent e) {
                  card.setBackground(Color.WHITE);
                  }
            });

            return card;
      }
}