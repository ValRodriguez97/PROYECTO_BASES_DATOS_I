package co.edu.uniquindio.View.utils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public final class UIFactory {
      
      private UIFactory() {}

      // -- PANELES --
      public static JPanel card() {
            JPanel p = new JPanel();
            p.setBackground(UIColors.BG_CARD);
            p.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));
            return p;
      }

      public static JPanel page() {
            JPanel p = new JPanel();
            p.setBackground(UIColors.BG_PAGE);
            return p;
      }

      // -- BOTONES --
      public static JButton primaryButton(String text) {
            return styledButton(text, UIColors.PURPLE, UIColors.PURPLE_LIGHT, Color.WHITE);
      }

      public static JButton outlineButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(UIFonts.BUTTON_MD);
            btn.setForeground(UIColors.TEXT_PRIMARY);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER_STRONG),
                  BorderFactory.createEmptyBorder(6, 16, 6, 16)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                  public void mouseEntered(MouseEvent e) { btn.setBackground(UIColors.BG_HOVER); }
                  public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
            });
            return btn;
      }

      public static JButton dangerButton(String text) {
            return styledButton(text, UIColors.RED, new Color(0xCC1137), Color.WHITE);
      }

      public static JButton tealButton(String text) {
            return styledButton(text, UIColors.TURQUOISE, new Color(0x009688), Color.WHITE);
      }

      private static JButton styledButton(String text, Color bg, Color hover, Color fg) {
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
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                  public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
                  public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
            });
            return btn;
      }

      // -- ETIQUETAS --
      public static JLabel heading(String text) {
            JLabel l = new JLabel(text);
            l.setFont(UIFonts.HEADING_XL);
            l.setForeground(UIColors.TEXT_PRIMARY);
            return l;
      }
      
      public static JLabel subheading(String text) {
            JLabel l = new JLabel(text);
            l.setFont(UIFonts.BODY_MD);
            l.setForeground(UIColors.TEXT_SECONDARY);
            return l;
      }
      
      public static JLabel sectionTitle(String text) {
            JLabel l = new JLabel(text);
            l.setFont(UIFonts.HEADING_MD);
            l.setForeground(UIColors.TEXT_PRIMARY);
            return l;
      }
      
      public static JLabel formLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(UIFonts.LABEL_BOLD);
            l.setForeground(UIColors.TEXT_PRIMARY);
            return l;
      }

      // -- BADGE -- 
      public static JLabel badge(String text, Color bg, Color fg) {
            JLabel l = new JLabel(text) {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(bg);
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                  g2.dispose();
                  super.paintComponent(g);
                  }
            };
            l.setFont(UIFonts.BADGE);
            l.setForeground(fg);
            l.setOpaque(false);
            l.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
            return l;
      }

       public static JLabel statusBadge(String text) {
            boolean active = "Activo".equalsIgnoreCase(text) || "ADMINISTRADOR".equalsIgnoreCase(text)
                  || "TRADICIONAL".equalsIgnoreCase(text) || "ESPORADICO".equalsIgnoreCase(text);
            Color bg = active ? UIColors.SUCCESS_BG : UIColors.ERROR_BG;
            Color fg = active ? UIColors.SUCCESS_FG : UIColors.ERROR_FG;
            if ("ADMINISTRADOR".equalsIgnoreCase(text)) { bg = UIColors.PURPLE_PALE; fg = UIColors.PURPLE; }
            if ("TRADICIONAL".equalsIgnoreCase(text))   { bg = UIColors.INFO_BG;     fg = UIColors.INFO_FG; }
            if ("ESPORADICO".equalsIgnoreCase(text))    { bg = UIColors.WARNING_BG;  fg = UIColors.WARNING_FG; }
            return badge(text, bg, fg);
      }

      // -- INPUTS --
      public static JTextField textField(String placeholder) {
            JTextField tf = new JTextField();
            tf.setFont(UIFonts.BODY_MD);
            tf.setForeground(UIColors.TEXT_PRIMARY);
            tf.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            tf.putClientProperty("JTextField.placeholderText", placeholder);
            return tf;
      }
      
      public static JPasswordField passwordField(String placeholder) {
            JPasswordField pf = new JPasswordField();
            pf.setFont(UIFonts.BODY_MD);
            pf.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            pf.putClientProperty("JTextField.placeholderText", placeholder);
            return pf;
      }

       public static JComboBox<String> comboBox(String... items) {
            JComboBox<String> cb = new JComboBox<>(items);
            cb.setFont(UIFonts.BODY_MD);
            cb.setBackground(Color.WHITE);
            cb.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            return cb;
      }

      // -- TABLA  --
      public static JTable styledTable(String[] columns) {
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                  @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable table = new JTable(model);
            styleTable(table);
            return table;
      }

      public static void styleTable(JTable table) {
            table.setFont(UIFonts.TABLE_CELL);
            table.setRowHeight(44);
            table.setShowVerticalLines(false);
            table.setGridColor(UIColors.BORDER);
            table.setBackground(Color.WHITE);
            table.setSelectionBackground(UIColors.PURPLE_PALE);
            table.setSelectionForeground(UIColors.PURPLE);
            table.setIntercellSpacing(new Dimension(0, 0));
            table.setFillsViewportHeight(true);

            JTableHeader header = table.getTableHeader();
            header.setFont(UIFonts.TABLE_HEADER);
            header.setBackground(new Color(0xF8F9FC));
            header.setForeground(UIColors.TEXT_PRIMARY);
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UIColors.BORDER));
            header.setReorderingAllowed(false);
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                  { setHorizontalAlignment(LEFT); setOpaque(true); }
                  @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                  super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                  setBackground(new Color(0xF8F9FC));
                  setForeground(UIColors.TEXT_PRIMARY);
                  setFont(UIFonts.TABLE_HEADER);
                  setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UIColors.BORDER),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                  return this;
                  }
            });

            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                  @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                  super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                  if (sel) {
                        setBackground(UIColors.PURPLE_PALE);
                        setForeground(UIColors.PURPLE);
                  } else {
                        setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD));
                        setForeground(UIColors.TEXT_PRIMARY);
                  }
                  setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                  return this;
                  }
            });
      }

      // -- SSCROLL PANE --
      public static JScrollPane scrollPane(Component c) {
            JScrollPane sp = new JScrollPane(c);
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.getViewport().setBackground(Color.WHITE);
            sp.getVerticalScrollBar().setUnitIncrement(16);
            return sp;
      }

      // -- SEPARADOR  --
      public static JSeparator separator() {
            JSeparator sep = new JSeparator();
            sep.setForeground(UIColors.BORDER);
            return sep;
      }

      // -- AVATAR --
      public static JPanel avatar(String initials, Color bg) {
            JPanel p = new JPanel() {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(bg);
                  g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                  g2.setColor(Color.WHITE);
                  g2.setFont(UIFonts.LABEL_MD);
                  FontMetrics fm = g2.getFontMetrics();
                  int x = (getWidth() - fm.stringWidth(initials)) / 2;
                  int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                  g2.drawString(initials, x, y);
                  g2.dispose();
                  }
            };
            p.setPreferredSize(new Dimension(36, 36));
            p.setOpaque(false);
            return p;
      }

      // -- PANEL GRADIENTE --
      public static JPanel gradientPanel(Color from, Color to, int height) {
            return new JPanel() {
                  { setPreferredSize(new Dimension(0, height)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setPaint(new GradientPaint(0, 0, from, getWidth(), 0, to));
                  g2.fillRect(0, 0, getWidth(), getHeight());
                  g2.dispose();
                  }
            };
      }

      // -- STAT CARD --
      public static JPanel statCard(String value, String label, Color iconBg, String symbol) {
            JPanel card = new JPanel(new BorderLayout(0, 8));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(12, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JPanel iconBox = new JPanel() {
                  { setPreferredSize(new Dimension(48, 48)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(iconBg);
                  g2.fill(new RoundRectangle2D.Float(0, 0, 48, 48, 12, 12));
                  g2.setColor(Color.WHITE);
                  g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                  FontMetrics fm = g2.getFontMetrics();
                  g2.drawString(symbol, (48 - fm.stringWidth(symbol)) / 2,
                              (48 - fm.getHeight()) / 2 + fm.getAscent());
                  g2.dispose();
                  }
            };

            JLabel numLabel = new JLabel(value);
            numLabel.setFont(UIFonts.STAT_NUMBER);
            numLabel.setForeground(UIColors.TEXT_PRIMARY);
      
            JLabel lblLabel = new JLabel(label);
            lblLabel.setFont(UIFonts.STAT_LABEL);
            lblLabel.setForeground(UIColors.TEXT_SECONDARY);
      
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setOpaque(false);
            bottom.add(lblLabel, BorderLayout.NORTH);
            bottom.add(numLabel, BorderLayout.CENTER);
      
            card.add(iconBox, BorderLayout.WEST);
            card.add(bottom,  BorderLayout.CENTER);
            return card;
      }
}
