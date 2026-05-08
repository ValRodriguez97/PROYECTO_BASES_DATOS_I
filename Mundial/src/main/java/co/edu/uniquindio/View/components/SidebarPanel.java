package co.edu.uniquindio.View.components;

import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel{
            public interface NavListener {
            void onNavigate(String panelName);
      }
      
      private final List<NavItem> items = new ArrayList<>();
      private String activePanel = "dashboard";
      private NavListener listener;
      private String userName = "Administrador";
      private String userRole = "admin@fifa2026.com";
      private Runnable onLogout;
      
      // Ítems de navegación
      private static final String[][] NAV_ITEMS = {
            {"dashboard", "Dashboard"},
            {"teams", "Equipos"},
            {"players", "Jugadores"},
            {"matches", "Partidos"},
            {"stadiums", "Estadios"},
            {"queries", "Consultas"},
            {"reports", "Reportes"},
            {"users", "Usuarios"},
            {"audit", "Bitácora"},
      };

      public SidebarPanel() {
            setPreferredSize(new Dimension(240, 0));
            setBackground(UIColors.SIDEBAR_BG);
            setLayout(new BorderLayout());
            build();
      }
 
      private void build() {
            JPanel logoPanel = buildLogo();
      
            JPanel navPanel = new JPanel();
            navPanel.setOpaque(false);
            navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
            navPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
      
            for (String[] item : NAV_ITEMS) {
                  NavItem ni = new NavItem(item[0], item[1], item[2]);
                  items.add(ni);
                  navPanel.add(ni);
                  navPanel.add(Box.createVerticalStrut(2));
            }
      
            JScrollPane nav = new JScrollPane(navPanel);
            nav.setOpaque(false);
            nav.getViewport().setOpaque(false);
            nav.setBorder(BorderFactory.createEmptyBorder());
            nav.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            nav.getVerticalScrollBar().setOpaque(false);
            nav.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
      
            JPanel userPanel = buildUserPanel();
      
            add(logoPanel, BorderLayout.NORTH);
            add(nav, BorderLayout.CENTER);
            add(userPanel, BorderLayout.SOUTH);
      }
 
      private JPanel buildLogo() {
            JPanel p = new JPanel(new BorderLayout()) {
                  @Override protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setColor(new Color(0x2D2D44));
                  g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                  g2.dispose();
                  }
            };
            p.setOpaque(false);
            p.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
      
            JPanel icon = new JPanel() {
                  { setPreferredSize(new Dimension(44, 44)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setPaint(new GradientPaint(0, 0, UIColors.PURPLE, 44, 44, UIColors.MAGENTA));
                  g2.fill(new RoundRectangle2D.Float(0, 0, 44, 44, 12, 12));
                  g2.setColor(Color.WHITE);
                  g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
                  g2.drawString("⚽", 8, 32);
                  g2.dispose();
                  }
            };
 
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("FIFA 2026");
            title.setFont(UIFonts.SIDEBAR_TITLE);
            title.setForeground(Color.WHITE);
            JLabel sub = new JLabel("Admin System");
            sub.setFont(UIFonts.BODY_SM);
            sub.setForeground(new Color(0x94A3B8));
            text.add(title);
            text.add(sub);
      
            p.add(icon, BorderLayout.WEST);
            p.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
            p.add(text, BorderLayout.EAST);
      
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrap.setOpaque(false);
            wrap.add(icon);
            wrap.add(Box.createHorizontalStrut(12));
            wrap.add(text);
            JPanel outer = new JPanel(new BorderLayout());
            outer.setOpaque(false);
            outer.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
            outer.add(wrap, BorderLayout.CENTER);
            return outer;
      }
 
      private JPanel buildUserPanel() {
            JPanel p = new JPanel() {
                  @Override protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setColor(new Color(0x2D2D44));
                  g2.fillRect(0, 0, getWidth(), 1);
                  g2.dispose();
                  }
            };
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(12, 12, 16, 12));
      
            JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            userRow.setOpaque(false);
 
            JPanel avatar = new JPanel() {
                  { setPreferredSize(new Dimension(36, 36)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setPaint(new GradientPaint(0, 0, UIColors.BLUE, 36, 36, UIColors.TURQUOISE));
                  g2.fillOval(0, 0, 35, 35);
                  g2.setColor(Color.WHITE);
                  g2.setFont(UIFonts.LABEL_BOLD);
                  g2.drawString("AD", 6, 24);
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
 
            JButton logoutBtn = new JButton("⬆  Cerrar Sesión") {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  if (getModel().isRollover()) {
                        g2.setColor(UIColors.SIDEBAR_ACCENT);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                  }
                  g2.dispose();
                  super.paintComponent(g);
                  }
            };

            logoutBtn.setFont(UIFonts.SIDEBAR_ITEM);
            logoutBtn.setForeground(new Color(0x94A3B8));
            logoutBtn.setContentAreaFilled(false);
            logoutBtn.setBorderPainted(false);
            logoutBtn.setFocusPainted(false);
            logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
            logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            logoutBtn.addActionListener(e -> { if (onLogout != null) onLogout.run(); });
            p.add(logoutBtn);
            return p;
      }
 
    // -- NavItem --
 
      private class NavItem extends JPanel {
            private final String panelName;
            private boolean hovered = false;
      
            NavItem(String panel, String icon, String label) {
                  this.panelName = panel;
                  setOpaque(false);
                  setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
                  setPreferredSize(new Dimension(216, 42));
                  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                  setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
      
                  JLabel iconL = new JLabel(icon);
                  iconL.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
                  iconL.setForeground(isActive() ? Color.WHITE : new Color(0xAAB4C8));
      
                  JLabel labelL = new JLabel(label);
                  labelL.setFont(UIFonts.SIDEBAR_ITEM);
                  labelL.setForeground(isActive() ? Color.WHITE : new Color(0xAAB4C8));
      
                  add(iconL);
                  add(labelL);
 
                  addMouseListener(new MouseAdapter() {
                  public void mouseClicked(MouseEvent e) {
                        activePanel = panelName;
                        items.forEach(NavItem::repaint);
                        if (listener != null) listener.onNavigate(panelName);
                  }
                  public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                  public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                  });
            }
 
            private boolean isActive() { return activePanel.equals(panelName); }
      
            @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  if (isActive()) {
                  g2.setColor(UIColors.PURPLE);
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                  } else if (hovered) {
                  g2.setColor(UIColors.SIDEBAR_ACCENT);
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                  }
                  g2.dispose();
                  for (Component c : getComponents()) {
                        if (c instanceof JLabel l) {
                              l.setForeground(isActive() || hovered ? Color.WHITE : new Color(0xAAB4C8));
                        }
                  }
                  super.paintComponent(g);
            }
      }
 
      // -- Setters --
      
      public void setNavListener(NavListener l) { this.listener = l; }
      public void setOnLogout(Runnable r)        { this.onLogout = r; }
      public void setActivePanel(String panel)   { this.activePanel = panel; repaint(); }
      
      public void setUserInfo(String name, String email, String role) {
            this.userName = name;
            this.userRole = email;
            removeAll();
            build();
            revalidate();
            repaint();
      }
}
