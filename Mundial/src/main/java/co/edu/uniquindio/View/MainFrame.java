package co.edu.uniquindio.View;

import co.edu.uniquindio.View.components.SidebarPanel;
import co.edu.uniquindio.View.panels.*;
import co.edu.uniquindio.services.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

      private final SistemaSeguridadService seguridad = new SistemaSeguridadService();
      private final MundialService mundial = new MundialService();
      private final GestionDatosService gestion = new GestionDatosService(seguridad);

      private final JPanel root = new JPanel(new CardLayout());

      private static final String CARD_LOGIN = "login";
      private static final String CARD_APP = "app";

      private JPanel appShell;
      private SidebarPanel sidebar;
      private JPanel contentArea;
      private CardLayout contentLayout;

      private static final String P_DASHBOARD = "dashboard";
      private static final String P_TEAMS = "teams";
      private static final String P_PLAYERS = "players";
      private static final String P_MATCHES = "matches";
      private static final String P_STADIUMS = "stadiums";
      private static final String P_QUERIES = "queries";
      private static final String P_REPORTS = "reports";
      private static final String P_USERS = "users";
      private static final String P_AUDIT = "audit";

      public MainFrame() {
            super("FIFA World Cup 2026 — Sistema de Gestión");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(1100, 700));
            setPreferredSize(new Dimension(1280, 780));

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

            root.add(new JPanel(), CARD_APP); // placeholder

            CardLayout cl = (CardLayout) root.getLayout();
            cl.show(root, CARD_LOGIN);
      }


      private void onLoginSuccess() {
            buildAppShell();
            CardLayout cl = (CardLayout) root.getLayout();
            cl.show(root, CARD_APP);

            var u = seguridad.getUsuarioActual();
            sidebar.setUserInfo(
                  u.getNombreUsuario(),
                  u.getTipoUsuario().name().toLowerCase() + "@fifa2026.com",
                  u.getTipoUsuario().name()
            );
            sidebar.setActivePanel(P_DASHBOARD);
      }

      private void onLogout() {
            seguridad.logout();

            CardLayout cl = (CardLayout) root.getLayout();
            cl.show(root, CARD_LOGIN);

            root.remove(0);
            LoginPanel newLogin = new LoginPanel(seguridad);
            newLogin.setOnLoginSuccess(this::onLoginSuccess);
            root.add(newLogin, CARD_LOGIN, 0);
            cl.show(root, CARD_LOGIN);
      }

      private void buildAppShell() {
            if (appShell != null) {
                  root.remove(appShell);
            }

            appShell = new JPanel(new BorderLayout());

            sidebar = new SidebarPanel();
            sidebar.setNavListener(this::navigate);
            sidebar.setOnLogout(this::onLogout);
            appShell.add(sidebar, BorderLayout.WEST);

            contentLayout = new CardLayout();
            contentArea = new JPanel(contentLayout);

            contentArea.add(new DashboardPanel(gestion), P_DASHBOARD);
            contentArea.add(new TeamsPanel(gestion), P_TEAMS);
            contentArea.add(new PlayersPanel(gestion), P_PLAYERS);
            contentArea.add(new MatchesPanel(gestion), P_MATCHES);
            contentArea.add(new StadiumsPanel(gestion), P_STADIUMS);
            contentArea.add(new QueriesPanel(gestion, mundial), P_QUERIES);
            contentArea.add(new ReportsPanel(gestion, mundial), P_REPORTS);
            contentArea.add(buildUsersPanel(), P_USERS);
            contentArea.add(new AuditPanel(gestion, mundial), P_AUDIT);

            contentLayout.show(contentArea, P_DASHBOARD);
            appShell.add(contentArea, BorderLayout.CENTER);

            root.add(appShell, CARD_APP);
            root.revalidate();
      }

 
      private JPanel buildUsersPanel() {
            var u = seguridad.getUsuarioActual();
            if (u != null && u.puedeCrearUsuarios()) {
                  return new UsersPanel(gestion, seguridad);
            }
            return buildAccessDeniedPanel("Usuarios",
                  "Solo el Administrador puede gestionar usuarios del sistema.");
      }


      private void navigate(String panelName) {
            var u = seguridad.getUsuarioActual();
            if (u == null) { onLogout(); return; }

            boolean esCRUD = panelName.equals(P_TEAMS) || panelName.equals(P_PLAYERS)
                        || panelName.equals(P_MATCHES) || panelName.equals(P_STADIUMS);
            boolean esAdmin = panelName.equals(P_USERS) || panelName.equals(P_AUDIT);

            if (esCRUD && !u.puedeEjecutarCRUD()) {
                  JOptionPane.showMessageDialog(this,
                  "Tu tipo de usuario no tiene permisos para acceder a esta sección.",
                  "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                  return;
            }
            if (esAdmin && !u.puedeCrearUsuarios()) {
                  JOptionPane.showMessageDialog(this,
                  "Solo el Administrador puede acceder a esta sección.",
                  "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                  return;
            }

            contentLayout.show(contentArea, panelName);
            sidebar.setActivePanel(panelName);
      }

      private JPanel buildAccessDeniedPanel(String seccion, String mensaje) {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(new Color(0xF5F7FA));

            JPanel card = new JPanel();
            card.setBackground(Color.WHITE);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createLineBorder(new Color(0xE5E7EB), 1, true),
                  BorderFactory.createEmptyBorder(40, 50, 40, 50)));

            JLabel icon = new JLabel("🔒");
            icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
            icon.setAlignmentX(CENTER_ALIGNMENT);

            JLabel titulo = new JLabel("Acceso Restringido — " + seccion);
            titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            titulo.setForeground(new Color(0x1A1A2E));
            titulo.setAlignmentX(CENTER_ALIGNMENT);

            JLabel msg = new JLabel("<html><div style='text-align:center'>" + mensaje + "</div></html>");
            msg.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            msg.setForeground(new Color(0x64748B));
            msg.setAlignmentX(CENTER_ALIGNMENT);

            card.add(icon);
            card.add(Box.createVerticalStrut(16));
            card.add(titulo);
            card.add(Box.createVerticalStrut(10));
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

            SwingUtilities.invokeLater(() -> {
                  MainFrame frame = new MainFrame();
                  frame.setVisible(true);
            });
      }
}
