package co.edu.uniquindio.View.utils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public final class UIFactory {

      private UIFactory() {}

      public static JButton primaryButton(String text) {
            return styledButton(text, UIColors.PURPLE, UIColors.PURPLE_LIGHT, Color.WHITE);
      }

      public static JButton tealButton(String text) {
            return styledButton(text, UIColors.TURQUOISE, new Color(0x00695C), Color.WHITE);
      }

      public static JButton dangerButton(String text) {
            return styledButton(text, UIColors.RED, new Color(0xB71C1C), Color.WHITE);
      }

      public static JButton outlineButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(UIFonts.BUTTON_MD);
            btn.setForeground(UIColors.TEXT_PRIMARY);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(8, UIColors.BORDER_STRONG),
                  BorderFactory.createEmptyBorder(7, 18, 7, 18)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                  public void mouseEntered(MouseEvent e) { btn.setBackground(UIColors.BG_HOVER); }
                  public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
            });
            return btn;
      }

      private static JButton styledButton(String text, Color bg, Color hover, Color fg) {
            JButton btn = new JButton(text) {
                  @Override protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(getBackground());
                  g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
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
            Color bg = UIColors.INFO_BG, fg = UIColors.INFO_FG;
            if ("ADMINISTRADOR".equalsIgnoreCase(text)) { bg = UIColors.PURPLE_PALE; fg = UIColors.PURPLE; }
            else if ("TRADICIONAL".equalsIgnoreCase(text))  { bg = UIColors.SUCCESS_BG; fg = UIColors.SUCCESS_FG; }
            else if ("ESPORADICO".equalsIgnoreCase(text))   { bg = UIColors.WARNING_BG; fg = UIColors.WARNING_FG; }
            return badge(text, bg, fg);
      }


      public static JTextField textField(String placeholder) {
            JTextField tf = new JTextField();
            tf.setFont(UIFonts.BODY_MD);
            tf.setForeground(UIColors.TEXT_PRIMARY);
            tf.setBackground(Color.WHITE);
            tf.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(7, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            tf.putClientProperty("JTextField.placeholderText", placeholder);
            return tf;
      }

      public static JPasswordField passwordField(String placeholder) {
            JPasswordField pf = new JPasswordField();
            pf.setFont(UIFonts.BODY_MD);
            pf.setBackground(Color.WHITE);
            pf.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(7, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            pf.putClientProperty("JTextField.placeholderText", placeholder);
            return pf;
      }

      public static JComboBox<String> comboBox(String... items) {
            JComboBox<String> cb = new JComboBox<>(items);
            cb.setFont(UIFonts.BODY_MD);
            cb.setBackground(Color.WHITE);
            return cb;
      }


      public static void styleTable(JTable table) {
            table.setFont(UIFonts.TABLE_CELL);
            table.setRowHeight(42);
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
                              BorderFactory.createEmptyBorder(0, 14, 0, 14)));
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
                        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                        return this;
                  }
            });
      }


      public static JScrollPane scrollPane(Component c) {
            JScrollPane sp = new JScrollPane(c);
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.getViewport().setBackground(Color.WHITE);
            sp.getVerticalScrollBar().setUnitIncrement(16);
            return sp;
      }


      public static JSeparator separator() {
            JSeparator sep = new JSeparator();
            sep.setForeground(UIColors.BORDER);
            return sep;
      }

      public static JPanel statCard(String value, String label, Color accent, String symbol) {
            JPanel card = new JPanel(new BorderLayout(14, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                  new RoundedBorder(10, UIColors.BORDER),
                  BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));

            JPanel iconBox = new JPanel() {
                  { setPreferredSize(new Dimension(46, 46)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 20));
                        g2.fill(new RoundRectangle2D.Float(0, 0, 46, 46, 10, 10));
                        g2.setColor(accent);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(symbol,
                              (46 - fm.stringWidth(symbol)) / 2,
                              (46 - fm.getHeight()) / 2 + fm.getAscent());
                        g2.dispose();
                  }
            };

            JPanel texts = new JPanel();
            texts.setOpaque(false);
            texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
            JLabel lbl = new JLabel(label);
            lbl.setFont(UIFonts.STAT_LABEL);
            lbl.setForeground(UIColors.TEXT_MUTED);
            JLabel num = new JLabel(value);
            num.setFont(UIFonts.STAT_NUMBER);
            num.setForeground(UIColors.TEXT_PRIMARY);
            texts.add(lbl);
            texts.add(Box.createVerticalStrut(2));
            texts.add(num);

            card.add(iconBox, BorderLayout.WEST);
            card.add(texts,   BorderLayout.CENTER);
            return card;
      }
}