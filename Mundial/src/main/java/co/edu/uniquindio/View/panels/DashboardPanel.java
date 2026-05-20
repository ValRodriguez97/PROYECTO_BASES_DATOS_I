package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final GestionDatosService gestion;

    // FIX: guardamos referencias a los stat labels para actualizarlos en refresh()
    private JLabel[] statLabels;

    public DashboardPanel(GestionDatosService gestion) {
        this.gestion = gestion;
        setBackground(UIColors.BG_PAGE);
        setLayout(new BorderLayout());
        build();
    }

    /**
     * FIX: MainFrame llama refresh() al navegar al dashboard.
     * Recarga los contadores de stats desde la BD.
     */
    public void refresh() {
        if (statLabels == null) return;
        int[] values = fetchStats();
        for (int i = 0; i < statLabels.length; i++) {
            statLabels[i].setText(String.valueOf(values[i]));
        }
    }

    private int[] fetchStats() {
        int[] values = {0, 0, 0, 0, 0};
        try { values[0] = gestion.listarEquipos().size();   } catch (Exception ignored) {}
        try { values[1] = gestion.listarJugadores().size(); } catch (Exception ignored) {}
        try { values[2] = gestion.listarEstadios().size();  } catch (Exception ignored) {}
        try { values[3] = gestion.listarPartidos().size();  } catch (Exception ignored) {}
        try { values[4] = gestion.listarGrupos().size();    } catch (Exception ignored) {}
        return values;
    }

    private void build() {
        JScrollPane scroll = UIFactory.scrollPane(buildContent());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.setBackground(UIColors.BG_PAGE);
        scroll.getViewport().setBackground(UIColors.BG_PAGE);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        JPanel content = new JPanel();
        content.setBackground(UIColors.BG_PAGE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setPreferredSize(new Dimension(980, 760));
        content.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        content.add(buildHeader());
        content.add(Box.createVerticalStrut(28));
        content.add(buildWelcomeCard());
        content.add(Box.createVerticalStrut(28));
        content.add(buildStatsSection());
        content.add(Box.createVerticalStrut(28));
        content.add(buildSystemInfoCard());

        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrapper.setOpaque(false);
        leftWrapper.add(content);

        root.add(leftWrapper, BorderLayout.NORTH);
        return root;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = UIFactory.heading("Dashboard");
        JLabel sub   = UIFactory.subheading("Vista general del sistema — FIFA World Cup 2026");
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        return p;
    }

    private JPanel buildStatsSection() {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.setMaximumSize(new Dimension(980, 320));

        JLabel title = UIFactory.sectionTitle("Resumen General");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(title);
        outer.add(Box.createVerticalStrut(18));

        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        String[] labels  = {"Equipos", "Jugadores", "Estadios", "Partidos", "Grupos"};
        String[] symbols = {"⚽", "👤", "🏟", "📅", "🔷"};
        Color[]  colors  = {
            UIColors.PURPLE,
            UIColors.BLUE,
            UIColors.TURQUOISE,
            UIColors.MAGENTA,
            new Color(0xE65100)
        };

        int[] values = fetchStats();
        statLabels = new JLabel[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel[] ref = new JLabel[1];
            JPanel card  = buildStatCard(String.valueOf(values[i]), labels[i], colors[i], symbols[i], ref);
            statLabels[i] = ref[0];
            grid.add(card);
        }

        JPanel empty = new JPanel();
        empty.setOpaque(false);
        grid.add(empty);

        outer.add(grid);
        return outer;
    }

    /**
     * Versión propia de statCard que expone el JLabel de valor a través de valueRef[0].
     */
    private JPanel buildStatCard(String value, String label, Color accent,
                              String symbol, JLabel[] valueRef) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, UIColors.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JPanel iconBox = new JPanel() {
            { setPreferredSize(new Dimension(46, 46)); setOpaque(false); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 20));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, 46, 46, 10, 10));
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

        // Guardamos referencia directa al label del número
        valueRef[0] = num;

        texts.add(lbl);
        texts.add(Box.createVerticalStrut(2));
        texts.add(num);

        card.add(iconBox, BorderLayout.WEST);
        card.add(texts,   BorderLayout.CENTER);
        return card;
    }


    private JPanel buildWelcomeCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIColors.PURPLE, getWidth(), 0, new Color(0x1565C0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(980, 145));
        card.setMaximumSize(new Dimension(980, 145));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(34, 36, 34, 36));

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sistema de Gestión — FIFA World Cup 2026");
        title.setFont(UIFonts.HEADING_XL);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(
            "<html>Administra equipos, jugadores, grupos, " +
            "estadios y partidos desde un único sistema.</html>"
        );
        sub.setFont(UIFonts.BODY_MD);
        sub.setForeground(new Color(255, 255, 255, 210));

        texts.add(title);
        texts.add(Box.createVerticalStrut(12));
        texts.add(sub);

        JLabel icon = new JLabel("⚽");
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 64));
        icon.setForeground(new Color(255, 255, 255, 220));

        card.add(texts, BorderLayout.CENTER);
        card.add(icon, BorderLayout.EAST);
        return card;
    }

    private JPanel buildSystemInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, UIColors.BORDER),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(980, 170));

        JLabel title = UIFactory.sectionTitle("Información del Sistema");
        JLabel desc  = new JLabel(
            "<html>Este sistema permite gestionar toda la información " +
            "administrativa relacionada con la FIFA World Cup 2026, " +
            "incluyendo equipos, jugadores, grupos, estadios, partidos " +
            "y generación de reportes.</html>"
        );
        desc.setFont(UIFonts.BODY_MD);
        desc.setForeground(UIColors.TEXT_SECONDARY);

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(desc);
        return card;
    }
}