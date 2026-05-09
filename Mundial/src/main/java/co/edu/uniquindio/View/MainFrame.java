package co.edu.uniquindio.View;

import co.edu.uniquindio.View.components.SidebarPanel;
import co.edu.uniquindio.View.panels.*;
import co.edu.uniquindio.View.utils.*;
import co.edu.uniquindio.services.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final SistemaSeguridadService seguridad = new SistemaSeguridadService();
    private final MundialService          mundial   = new MundialService();
    private final GestionDatosService     gestion   = new GestionDatosService(seguridad);

    private final JPanel root = new JPanel(new CardLayout());

    private JPanel       appShell;
    private SidebarPanel sidebar;
    private JPanel       contentArea;
    private CardLayout   contentLayout;

    private DashboardPanel  dashboardPanel;
    private QueriesPanel    queriesPanel;
    private PlayersPanel    playersPanel;
    private MatchesPanel    matchesPanel;

    private static final String CARD_LOGIN = "login";
    private static final String CARD_APP   = "app";

    private static final String P_DASHBOARD = "dashboard";
    private static final String P_TEAMS     = "teams";
    private static final String P_PLAYERS   = "players";
    private static final String P_MATCHES   = "matches";
    private static final String P_STADIUMS  = "stadiums";
    private static final String P_QUERIES   = "queries";
    private static final String P_REPORTS   = "reports";
    private static final String P_USERS     = "users";
    private static final String P_AUDIT     = "audit";

    public MainFrame() {
        super("FIFA World Cup 2026 — Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setPreferredSize(new Dimension(1280, 760));

        try {
            ImageIcon ico = new ImageIcon(
                    getClass().getResource("/co/edu/uniquindio/View/assets/icon.png"));
            setIconImage(ico.getImage());
        } catch (Exception ignored) {}

        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setContentPane(root);
        LoginPanel loginPanel = new LoginPanel(seguridad);
        loginPanel.setOnLoginSuccess(this::onLoginSuccess);
        root.add(loginPanel, CARD_LOGIN);
        root.add(new JPanel(), CARD_APP);
        ((CardLayout) root.getLayout()).show(root, CARD_LOGIN);
    }

    private void onLoginSuccess() {
        buildAppShell();
        ((CardLayout) root.getLayout()).show(root, CARD_APP);

        var u = seguridad.getUsuarioActual();
        sidebar.setUserInfo(
                u.getNombreUsuario(),
                u.getNombreUsuario() + "@fifa2026.com",
                u.getTipoUsuario().name()
        );
        sidebar.setActivePanel(P_DASHBOARD);
    }

    private void onLogout() {
        seguridad.logout();
        CardLayout cl = (CardLayout) root.getLayout();

        dashboardPanel = null;
        queriesPanel   = null;
        playersPanel   = null;
        matchesPanel   = null;

        for (Component c : root.getComponents()) {
            if (c instanceof LoginPanel) { root.remove(c); break; }
        }
        LoginPanel newLogin = new LoginPanel(seguridad);
        newLogin.setOnLoginSuccess(this::onLoginSuccess);
        root.add(newLogin, CARD_LOGIN, 0);
        cl.show(root, CARD_LOGIN);
    }

    private void buildAppShell() {
        if (appShell != null) root.remove(appShell);

        appShell = new JPanel(new BorderLayout());

        sidebar = new SidebarPanel();
        sidebar.setNavListener(this::navigate);
        sidebar.setOnLogout(this::onLogout);
        appShell.add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentArea   = new JPanel(contentLayout);
        contentArea.setBackground(UIColors.BG_PAGE);

        var u = seguridad.getUsuarioActual();
        boolean esCRUD  = u != null && u.puedeEjecutarCRUD();
        boolean esAdmin = u != null && u.puedeCrearUsuarios();

        dashboardPanel = new DashboardPanel(gestion);
        queriesPanel   = new QueriesPanel(gestion, mundial);

        contentArea.add(dashboardPanel, P_DASHBOARD);
        contentArea.add(queriesPanel,   P_QUERIES);
        contentArea.add(new ReportsPanel(gestion, mundial), P_REPORTS);

        if (esCRUD) {
            playersPanel = new PlayersPanel(gestion);
            matchesPanel = new MatchesPanel(gestion);

            contentArea.add(new TeamsPanel(gestion),   P_TEAMS);
            contentArea.add(playersPanel,              P_PLAYERS);
            contentArea.add(matchesPanel,              P_MATCHES);
            contentArea.add(new StadiumsPanel(gestion), P_STADIUMS);
        } else {
            contentArea.add(buildAccessDeniedPanel("Equipos",
                    "Tu tipo de usuario no tiene permisos para acceder a esta sección."), P_TEAMS);
            contentArea.add(buildAccessDeniedPanel("Jugadores",
                    "Tu tipo de usuario no tiene permisos para acceder a esta sección."), P_PLAYERS);
            contentArea.add(buildAccessDeniedPanel("Partidos",
                    "Tu tipo de usuario no tiene permisos para acceder a esta sección."), P_MATCHES);
            contentArea.add(buildAccessDeniedPanel("Estadios",
                    "Tu tipo de usuario no tiene permisos para acceder a esta sección."), P_STADIUMS);
        }

        if (esAdmin) {
            contentArea.add(new UsersPanel(gestion, seguridad), P_USERS);
            contentArea.add(new AuditPanel(gestion, mundial),   P_AUDIT);
        } else {
            contentArea.add(buildAccessDeniedPanel("Usuarios",
                    "Solo el Administrador puede gestionar usuarios del sistema."), P_USERS);
            contentArea.add(buildAccessDeniedPanel("Bitácora",
                    "Solo el Administrador puede acceder a la bitácora."), P_AUDIT);
        }

        contentLayout.show(contentArea, P_DASHBOARD);
        appShell.add(contentArea, BorderLayout.CENTER);
        root.add(appShell, CARD_APP);
        root.revalidate();
    }


    private void navigate(String panelName) {
        var u = seguridad.getUsuarioActual();
        if (u == null) { onLogout(); return; }

        contentLayout.show(contentArea, panelName);
        sidebar.setActivePanel(panelName);

        switch (panelName) {
            case P_DASHBOARD -> { if (dashboardPanel != null) dashboardPanel.refresh(); }
            case P_QUERIES   -> { if (queriesPanel   != null) queriesPanel.refresh();   }
            case P_PLAYERS   -> { if (playersPanel   != null) playersPanel.refresh();   }
            case P_MATCHES   -> { if (matchesPanel   != null) matchesPanel.refresh();   }
        }
    }

    private JPanel buildAccessDeniedPanel(String seccion, String mensaje) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIColors.BG_PAGE);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, UIColors.BORDER),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 44));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Acceso Restringido — " + seccion);
        titulo.setFont(UIFonts.HEADING_MD);
        titulo.setForeground(UIColors.TEXT_PRIMARY);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel msg = new JLabel(
                "<html><div style='text-align:center;width:280px'>" + mensaje + "</div></html>");
        msg.setFont(UIFonts.BODY_MD);
        msg.setForeground(UIColors.TEXT_SECONDARY);
        msg.setAlignmentX(CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(14));
        card.add(titulo);
        card.add(Box.createVerticalStrut(8));
        card.add(msg);

        p.add(card);
        return p;
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 8);
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}