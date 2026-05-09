package co.edu.uniquindio.View.components;

import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {

      public interface NavListener {
            void onNavigate(String panelName);
      }

      private final List<NavItem> items = new ArrayList<>();
      private String activePanel = "dashboard";
      private NavListener listener;
      private String userName = "Administrador";
      private String userEmail = "admin@fifa2026.com";
      private String userRole = "ADMINISTRADOR";
      private Runnable onLogout;

      // ID → [icono, etiqueta]
      private static final String[][] NAV_ITEMS = {
            {"dashboard", "⊞", "Dashboard"},
            {"teams", "⚽", "Equipos"},
            {"players", "👤", "Jugadores"},
            {"matches", "📅", "Partidos"},
            {"stadiums", "🏟", "Estadios"},
            {"queries", "🔍", "Consultas"},
            {"reports", "📊", "Reportes"},
            {"users", "⚙", "Usuarios"},
            {"audit", "📋", "Bitácora"},
      };

      public SidebarPanel() {
            setPreferredSize(new Dimension(230, 0));
            setBackground(UIColors.SIDEBAR_BG);
            setLayout(new BorderLayout());
            build();
      }


      private void build() {
            add(buildLogoPanel(), BorderLayout.NORTH);
            add(buildNavScroll(), BorderLayout.CENTER);
            add(buildUserPanel(), BorderLayout.SOUTH);
      }

      private JPanel buildLogoPanel() {
            JPanel outer = new JPanel(new BorderLayout());
            outer.setOpaque(false);
            outer.setBorder(BorderFactory.createEmptyBorder(20, 16, 16, 16));

            JPanel iconBox = new JPanel() {
                  { setPreferredSize(new Dimension(40, 40)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(0, 0, UIColors.PURPLE, 40, 40, UIColors.MAGENTA);
                        g2.setPaint(gp);
                        g2.fill(new RoundRectangle2D.Float(0, 0, 40, 40, 10, 10));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
                        g2.drawString("⚽", 8, 29);
                        g2.dispose();
                  }
            };

            JPanel textCol = new JPanel();
            textCol.setOpaque(false);
            textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("FIFA 2026");
            title.setFont(UIFonts.SIDEBAR_TITLE);
            title.setForeground(Color.WHITE);
            JLabel sub = new JLabel("Admin System");
            sub.setFont(UIFonts.BODY_SM);
            sub.setForeground(new Color(0x94A3B8));
            textCol.add(title);
            textCol.add(sub);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            row.setOpaque(false);
            row.add(iconBox);
            row.add(Box.createHorizontalStrut(10));
            row.add(textCol);

            outer.add(row, BorderLayout.CENTER);

            JSeparator sep = new JSeparator();
            sep.setForeground(new Color(0x2D2D44));
            outer.add(sep, BorderLayout.SOUTH);

            return outer;
      }

      private JScrollPane buildNavScroll() {
            JPanel navPanel = new JPanel();
            navPanel.setOpaque(false);
            navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
            navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (String[] item : NAV_ITEMS) {
                  NavItem ni = new NavItem(item[0], item[1], item[2]);
                  items.add(ni);
                  navPanel.add(ni);
                  navPanel.add(Box.createVerticalStrut(2));
            }

            JScrollPane scroll = new JScrollPane(navPanel);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
            return scroll;
      }

      private JPanel buildUserPanel() {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2D2D44)),
                  BorderFactory.createEmptyBorder(12, 12, 16, 12)
            ));

            JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            userRow.setOpaque(false);

            JPanel avatar = new JPanel() {
                  { setPreferredSize(new Dimension(34, 34)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  GradientPaint gp = new GradientPaint(0, 0, UIColors.BLUE, 34, 34, UIColors.TURQUOISE);
                  g2.setPaint(gp);
                  g2.fillOval(0, 0, 33, 33);
                  g2.setColor(Color.WHITE);
                  g2.setFont(UIFonts.LABEL_MD);
                  String initials = userName.length() >= 2
                        ? userName.substring(0, 2).toUpperCase() : "AD";
                  FontMetrics fm = g2.getFontMetrics();
                  g2.drawString(initials,
                        (34 - fm.stringWidth(initials)) / 2,
                        (34 + fm.getAscent() - fm.getDescent()) / 2);
                  g2.dispose();
                  }
            };

            JPanel nameCol = new JPanel();
            nameCol.setOpaque(false);
            nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));
            JLabel nameL = new JLabel(userName);
            nameL.setFont(UIFonts.LABEL_BOLD);
            nameL.setForeground(Color.WHITE);
            JLabel roleL = new JLabel(userRole);
            roleL.setFont(UIFonts.BODY_SM);
            roleL.setForeground(new Color(0x94A3B8));
            nameCol.add(nameL);
            nameCol.add(roleL);

            userRow.add(avatar);
            userRow.add(nameCol);
            p.add(userRow);
            p.add(Box.createVerticalStrut(8));

            JButton logoutBtn = new JButton("⬆  Cerrar Sesión");
            logoutBtn.setFont(UIFonts.SIDEBAR_ITEM);
            logoutBtn.setForeground(new Color(0x94A3B8));
            logoutBtn.setContentAreaFilled(false);
            logoutBtn.setBorderPainted(false);
            logoutBtn.setFocusPainted(false);
            logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
            logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            logoutBtn.addActionListener(e -> { if (onLogout != null) onLogout.run(); });
            logoutBtn.addMouseListener(new MouseAdapter() {
                  public void mouseEntered(MouseEvent e) { logoutBtn.setForeground(Color.WHITE); }
                  public void mouseExited(MouseEvent e)  { logoutBtn.setForeground(new Color(0x94A3B8)); }
            });
            p.add(logoutBtn);
            return p;
      }

      private class NavItem extends JPanel {
            private final String panelId;
            private boolean hovered = false;

            NavItem(String id, String icon, String label) {
                  this.panelId = id;
                  setOpaque(false);
                  setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                  setPreferredSize(new Dimension(210, 40));
                  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                  setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

                  JLabel iconL = new JLabel(icon);
                  iconL.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

                  JLabel labelL = new JLabel(label);
                  labelL.setFont(UIFonts.SIDEBAR_ITEM);

                  add(iconL); add(labelL);

                  updateColors();

                  addMouseListener(new MouseAdapter() {
                  public void mouseClicked(MouseEvent e) {
                              activePanel = panelId;
                              items.forEach(NavItem::updateColors);
                              if (listener != null) listener.onNavigate(panelId);
                        }
                        public void mouseEntered(MouseEvent e) { hovered = true;  updateColors(); repaint(); }
                        public void mouseExited(MouseEvent e)  { hovered = false; updateColors(); repaint(); }
                  });
            }

            private boolean isActive() { return activePanel.equals(panelId); }

            void updateColors() {
                  boolean active = isActive();
                  Color fg = (active || hovered) ? Color.WHITE : new Color(0xAAB4C8);
                  for (Component c : getComponents()) {
                  if (c instanceof JLabel l) l.setForeground(fg);
                  }
            }

            @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  if (isActive()) {
                  g2.setColor(UIColors.PURPLE);
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                  } else if (hovered) {
                  g2.setColor(UIColors.SIDEBAR_ACCENT);
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                  }
                  g2.dispose();
                  super.paintComponent(g);
            }
      }

      public void setNavListener(NavListener l) { this.listener = l; }
      public void setOnLogout(Runnable r) { this.onLogout = r; }
      public void setActivePanel(String panel) { this.activePanel = panel; items.forEach(NavItem::updateColors); repaint(); }

      public void setUserInfo(String name, String email, String role) {
            this.userName = name;
            this.userEmail = email;
            this.userRole = role;
            removeAll();
            build();
            revalidate();
            repaint();
      }
}