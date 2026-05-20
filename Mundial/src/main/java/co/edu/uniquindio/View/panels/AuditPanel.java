package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.model.Bitacora;
import co.edu.uniquindio.services.GestionDatosService;
import co.edu.uniquindio.services.MundialService;
import co.edu.uniquindio.View.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.io.File;



public class AuditPanel extends JPanel {

    private final GestionDatosService gestion;
    private final MundialService mundial;

    private JTable table;
    private DefaultTableModel model;
    private JTextField desdeField;
    private JTextField hastaField;
    private JLabel statusLabel;

    private static final String[] COLUMNS = {
        "#", "Usuario (ID)", "Fecha Ingreso", "Fecha Salida", "Duración", "Estado"
    };
    private static final DateTimeFormatter FMT_DISPLAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FMT_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AuditPanel(GestionDatosService gestion, MundialService mundial) {
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
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(UIColors.BG_PAGE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        content.add(buildHeader());
        content.add(Box.createVerticalStrut(14));
        content.add(buildFilterCard());
        content.add(Box.createVerticalStrut(12));
        content.add(buildTable());
        return content;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(UIFactory.heading("Bitácora"));
        left.add(Box.createVerticalStrut(4));
        left.add(UIFactory.subheading(
                "Registro de ingresos y salidas del sistema"
        ));

        JButton pdfBtn = UIFactory.tealButton("📄 Exportar PDF");
        pdfBtn.setPreferredSize(new Dimension(170, 42));
        pdfBtn.setMaximumSize(new Dimension(170, 42));
        pdfBtn.setMinimumSize(new Dimension(170, 42));
        pdfBtn.addActionListener(e -> exportarPDF());

        p.add(left);
        p.add(Box.createHorizontalGlue());
        p.add(pdfBtn);

        return p;
    }

    private JPanel buildFilterCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, UIColors.BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel title = UIFactory.sectionTitle(
                "Filtrar por Rango de Fecha"
        );
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        JPanel desdeWrap = labeledField("Desde (yyyy-MM-dd)");
        desdeField = (JTextField) desdeWrap.getClientProperty("field");
        desdeField.setText(
                LocalDate.now()
                        .minusDays(30)
                        .format(FMT_INPUT)
        );

        JPanel hastaWrap = labeledField("Hasta (yyyy-MM-dd)");
        hastaField = (JTextField) hastaWrap.getClientProperty("field");
        hastaField.setText(
                LocalDate.now().format(FMT_INPUT)
        );

        JButton buscarBtn = UIFactory.primaryButton("Buscar");
        buscarBtn.setPreferredSize(new Dimension(120, 40));
        buscarBtn.setMaximumSize(new Dimension(120, 40));
        buscarBtn.setMinimumSize(new Dimension(120, 40));
        buscarBtn.addActionListener(e -> buscarRegistros());

        JButton hoyBtn = UIFactory.tealButton("Hoy");
        hoyBtn.setPreferredSize(new Dimension(90, 40));
        hoyBtn.setMaximumSize(new Dimension(90, 40));
        hoyBtn.setMinimumSize(new Dimension(90, 40));
        hoyBtn.addActionListener(e -> {
            String hoy = LocalDate.now().format(FMT_INPUT);
            desdeField.setText(hoy);
            hastaField.setText(hoy);
            buscarRegistros();
        });

        row.add(desdeWrap);
        row.add(Box.createHorizontalStrut(14));
        row.add(hastaWrap);
        row.add(Box.createHorizontalStrut(18));
        row.add(buscarBtn);
        row.add(Box.createHorizontalStrut(10));
        row.add(hoyBtn);
        card.add(row);
        return card;
    }

    private JPanel labeledField(String labelText) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(220, 62));

        JLabel lbl = UIFactory.formLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tf = UIFactory.textField("");
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setPreferredSize(new Dimension(220, 38));
        tf.setMaximumSize(new Dimension(220, 38));
        tf.setMinimumSize(new Dimension(220, 38));

        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(tf);
        p.putClientProperty("field", tf);

        return p;
    }

    private JPanel buildTable() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(10, UIColors.BORDER));

        JPanel th = new JPanel(new BorderLayout());
        th.setBackground(new Color(0xF8F9FC));
        th.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        th.add(UIFactory.sectionTitle("Registros"), BorderLayout.WEST);

        statusLabel = new JLabel("Usa el filtro para cargar registros");
        statusLabel.setFont(UIFonts.BODY_SM);
        statusLabel.setForeground(UIColors.TEXT_MUTED);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        th.add(statusLabel, BorderLayout.EAST);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIFactory.styleTable(table);
        table.setRowHeight(42);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getColumnModel().getColumn(0).setPreferredWidth(36);
        table.getColumnModel().getColumn(0).setMaxWidth(44);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setMaxWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setMaxWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setMaxWidth(110);

        table.getColumnModel().getColumn(5).setCellRenderer((t, v, sel, foc, r, c) -> {
            String estado = v != null ? v.toString() : "";
            boolean activo = "En sesión".equals(estado);
            JLabel badge = UIFactory.badge(estado,
                    activo ? UIColors.SUCCESS_BG : UIColors.INFO_BG,
                    activo ? UIColors.SUCCESS_FG : UIColors.INFO_FG);
            badge.setOpaque(true);
            badge.setBackground(sel ? UIColors.PURPLE_PALE : (r % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD)));
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
            wrap.setBackground(badge.getBackground());
            wrap.add(badge);
            return wrap;
        });

        JScrollPane tableScroll = UIFactory.scrollPane(table);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(th, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        return card;
    }

    private void buscarRegistros() {
        try {
            LocalDateTime desde = LocalDate.parse(desdeField.getText().trim(), FMT_INPUT).atStartOfDay();
            LocalDateTime hasta = LocalDate.parse(hastaField.getText().trim(), FMT_INPUT).atTime(LocalTime.MAX);
            List<Bitacora> registros = gestion.listarBitacoraPorRango(desde, hasta);
            poblarTabla(registros);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Usa yyyy-MM-dd (ej: 2026-06-11).",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar registros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(List<Bitacora> registros) {
        model.setRowCount(0);
        int idx = 1;
        for (Bitacora b : registros) {
            String ingreso  = b.getFechaHoraIngreso() != null ? b.getFechaHoraIngreso().format(FMT_DISPLAY) : "-";
            String salida   = b.getFechaHoraSalida()  != null ? b.getFechaHoraSalida().format(FMT_DISPLAY)  : "-";
            String duracion = "-";
            String estado   = "En sesión";
            if (b.getFechaHoraSalida() != null && b.getFechaHoraIngreso() != null) {
                long mins = java.time.Duration.between(b.getFechaHoraIngreso(), b.getFechaHoraSalida()).toMinutes();
                duracion = mins + " min";
                estado   = "Cerrada";
            }
            model.addRow(new Object[]{idx++, b.getIdUsuario(), ingreso, salida, duracion, estado});
        }
        statusLabel.setText(registros.size() + " registro(s) encontrado(s)");
    }

    private void exportarPDF() {
    String desdeStr = desdeField.getText().trim();
    String hastaStr = hastaField.getText().trim();
    if (desdeStr.isEmpty() || hastaStr.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Define el rango de fechas antes de exportar.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    LocalDateTime desde;
    LocalDateTime hasta;
    try {
        desde = LocalDate.parse(desdeStr, FMT_INPUT).atStartOfDay();
        hasta = LocalDate.parse(hastaStr, FMT_INPUT).atTime(LocalTime.MAX);
    } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(this,
                "Formato de fecha inválido. Usa yyyy-MM-dd",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
        return;
    }

    new SwingWorker<String, Void>() {
        @Override
        protected String doInBackground() throws Exception {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            return mundial.generarReporteBitacora(desde, hasta);
        }
        @Override
        protected void done() {
            setCursor(Cursor.getDefaultCursor());
            try {
                String ruta = get();
                mostrarVisorPdf(ruta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AuditPanel.this,
                        "Error al generar el PDF: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }.execute();
}

    private void mostrarVisorPdf(String pdfPath) {
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "El PDF no existe en la ruta:\n" + pdfPath,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        org.apache.pdfbox.pdmodel.PDDocument doc;
        try {
            doc = org.apache.pdfbox.Loader.loadPDF(pdfFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
        final int totalPages = doc.getNumberOfPages();
        final int[] current  = {0};

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Vista previa — " + pdfFile.getName(),
                Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(860, 920);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                try { doc.close(); } catch (Exception ignored) {}
            }
        });

        // Barra superior con ruta
        JLabel pathLabel = new JLabel(" " + pdfFile.getAbsolutePath());
        pathLabel.setFont(UIFonts.BODY_SM);
        pathLabel.setForeground(UIColors.TEXT_SECONDARY);
        pathLabel.setOpaque(true);
        pathLabel.setBackground(new Color(0xF8F9FC));
        pathLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        dlg.add(pathLabel, BorderLayout.NORTH);

        // Área de imagen
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.TOP);
        imageLabel.setBackground(new Color(0xDDDDDD));
        imageLabel.setOpaque(true);

        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        imageContainer.setBackground(new Color(0xDDDDDD));
        imageContainer.add(imageLabel);

        JScrollPane scroll = new JScrollPane(imageContainer);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        dlg.add(scroll, BorderLayout.CENTER);

        // Barra de navegación inferior
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        nav.setBackground(new Color(0xF8F9FC));
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

        JButton prev         = UIFactory.outlineButton("◀ Anterior");
        JButton next         = UIFactory.outlineButton("Siguiente ▶");
        JLabel  pageInfo     = new JLabel();
        pageInfo.setFont(UIFonts.LABEL_BOLD);
        pageInfo.setForeground(UIColors.TEXT_PRIMARY);
        JButton openExternal = UIFactory.tealButton("Abrir en visor externo");

        prev.setEnabled(false);
        next.setEnabled(totalPages > 1);

        Runnable renderPage = () -> {
            prev.setEnabled(false);
            next.setEnabled(false);
            pageInfo.setText("Cargando...");
            imageLabel.setIcon(null);
            imageLabel.setText("Renderizando pagina " + (current[0] + 1) + "...");

            new SwingWorker<java.awt.image.BufferedImage, Void>() {
                @Override
                protected java.awt.image.BufferedImage doInBackground() throws Exception {
                    float dpi = (dlg.getWidth() - 40) / 8.5f;
                    if (dpi < 80)  dpi = 80;
                    if (dpi > 150) dpi = 150;
                    return renderer.renderImageWithDPI(current[0], dpi);
                }
                @Override
                protected void done() {
                    try {
                        java.awt.image.BufferedImage img = get();
                        imageLabel.setIcon(new ImageIcon(img));
                        imageLabel.setText(null);
                        pageInfo.setText("Pagina " + (current[0] + 1) + " / " + totalPages);
                        prev.setEnabled(current[0] > 0);
                        next.setEnabled(current[0] < totalPages - 1);
                        scroll.getVerticalScrollBar().setValue(0);
                        imageContainer.revalidate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        imageLabel.setIcon(null);
                        imageLabel.setText("Error al renderizar: " + ex.getMessage());
                    }
                }
            }.execute();
        };

        prev.addActionListener(e -> {
            if (current[0] > 0) { current[0]--; renderPage.run(); }
        });
        next.addActionListener(e -> {
            if (current[0] < totalPages - 1) { current[0]++; renderPage.run(); }
        });

        openExternal.addActionListener(e -> {
            try {
                if (java.awt.Desktop.isDesktopSupported()
                        && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
                    java.awt.Desktop.getDesktop().open(pdfFile);
                } else {
                    JOptionPane.showMessageDialog(dlg,
                            "Abre el archivo manualmente en:\n" + pdfFile.getAbsolutePath(),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg,
                        "No se pudo abrir el visor externo:\n" + ex.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        nav.add(prev);
        nav.add(pageInfo);
        nav.add(next);
        nav.add(Box.createHorizontalStrut(24));
        nav.add(openExternal);
        dlg.add(nav, BorderLayout.SOUTH);

        renderPage.run();
        dlg.setVisible(true);
    }
}