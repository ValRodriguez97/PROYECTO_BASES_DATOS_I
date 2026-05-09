package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.View.utils.*;
import co.edu.uniquindio.services.SistemaSeguridadService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginPanel extends JPanel {

      private final SistemaSeguridadService seguridad;
      private JTextField  userField;
      private JPasswordField passField;
      private JLabel errorLabel;
      private Runnable onLoginSuccess;

      public LoginPanel(SistemaSeguridadService seguridad) {
            this.seguridad = seguridad;
            setLayout(new GridBagLayout());
            build();
      }

      @Override
      protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                  0, 0, UIColors.PURPLE,
                  getWidth(), getHeight(), new Color(0x1565C0));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Círculos decorativos
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillOval(-60, -60, 320, 320);
            g2.setColor(new Color(255, 255, 255, 10));
            g2.fillOval(getWidth() - 200, getHeight() - 200, 380, 380);

            g2.dispose();
      }

      private void build() {
            JPanel card = buildCard();

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 18, 0);
            add(card, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(buildFlagRow(), gbc);
      }

      private JPanel buildCard() {
            JPanel card = new JPanel() {
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0, 0, 0, 35));
                        g2.fill(new RoundRectangle2D.Float(6, 8, getWidth() - 10, getHeight() - 6, 18, 18));
                        g2.setColor(Color.WHITE);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 6, getHeight() - 4, 18, 18));
                        g2.dispose();
                        super.paintComponent(g);
                  }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(400, 490));
            card.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 40));

            JPanel logoRow = buildLogoRow();
            logoRow.setAlignmentX(CENTER_ALIGNMENT);
            card.add(logoRow);
            card.add(Box.createVerticalStrut(22));

            JLabel formTitle = new JLabel("Iniciar Sesión");
            formTitle.setFont(UIFonts.HEADING_LG);
            formTitle.setForeground(UIColors.TEXT_PRIMARY);
            formTitle.setAlignmentX(CENTER_ALIGNMENT);
            card.add(formTitle);
            card.add(Box.createVerticalStrut(24));

            JLabel userLabel = UIFactory.formLabel("Usuario");
            userLabel.setAlignmentX(LEFT_ALIGNMENT);
            userField = UIFactory.textField("Ingresa tu usuario");
            userField.setAlignmentX(LEFT_ALIGNMENT);
            userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            card.add(userLabel);
            card.add(Box.createVerticalStrut(5));
            card.add(userField);
            card.add(Box.createVerticalStrut(14));

            JLabel passLabel = UIFactory.formLabel("Contraseña");
            passLabel.setAlignmentX(LEFT_ALIGNMENT);
            passField = UIFactory.passwordField("Ingresa tu contraseña");
            passField.setAlignmentX(LEFT_ALIGNMENT);
            passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            card.add(passLabel);
            card.add(Box.createVerticalStrut(5));
            card.add(passField);
            card.add(Box.createVerticalStrut(6));

            errorLabel = new JLabel(" ");
            errorLabel.setFont(UIFonts.BODY_SM);
            errorLabel.setForeground(UIColors.RED);
            errorLabel.setAlignmentX(CENTER_ALIGNMENT);
            card.add(errorLabel);
            card.add(Box.createVerticalStrut(8));

            JButton loginBtn = UIFactory.primaryButton("Iniciar Sesión");
            loginBtn.setAlignmentX(CENTER_ALIGNMENT);
            loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            loginBtn.addActionListener(e -> doLogin());
            card.add(loginBtn);

            card.add(Box.createVerticalGlue());
            card.add(UIFactory.separator());
            card.add(Box.createVerticalStrut(14));

            JLabel footer = new JLabel("Sistema de Base de Datos · Proyecto Universitario");
            footer.setFont(UIFonts.BODY_SM);
            footer.setForeground(UIColors.TEXT_MUTED);
            footer.setAlignmentX(CENTER_ALIGNMENT);
            card.add(footer);

            ActionListener enterAction = e -> doLogin();
            userField.addActionListener(enterAction);
            passField.addActionListener(enterAction);

            return card;
      }

      private JPanel buildLogoRow() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            p.setOpaque(false);

            JPanel icon = new JPanel() {
                  { setPreferredSize(new Dimension(56, 56)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(0, 0, UIColors.PURPLE, 56, 56, UIColors.MAGENTA);
                        g2.setPaint(gp);
                        g2.fill(new RoundRectangle2D.Float(0, 0, 56, 56, 14, 14));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 28));
                        g2.drawString("⚽", 9, 41);
                        g2.dispose();
                  }
            };

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("FIFA World Cup 2026");
            title.setFont(UIFonts.HEADING_MD);
            title.setForeground(UIColors.TEXT_PRIMARY);
            JLabel sub = new JLabel("Sistema de Gestión Administrativo");
            sub.setFont(UIFonts.BODY_SM);
            sub.setForeground(UIColors.TEXT_SECONDARY);
            text.add(title);
            text.add(sub);

            p.add(icon); p.add(text);
            return p;
      }

      private JPanel buildFlagRow() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            p.setOpaque(false);
            for (String s : new String[]{"🇲🇽 México", "🇺🇸 Estados Unidos", "🇨🇦 Canadá"}) {
                  JLabel fl = new JLabel(s);
                  fl.setFont(UIFonts.BODY_SM);
                  fl.setForeground(new Color(255, 255, 255, 190));
                  p.add(fl);
            }
            return p;
      }

      private void doLogin() {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                  errorLabel.setText("Por favor completa todos los campos.");
                  return;
            }
            try {
                  seguridad.login(user, pass);
                  errorLabel.setText(" ");
                  if (onLoginSuccess != null) onLoginSuccess.run();
            } catch (Exception ex) {
                  errorLabel.setText(ex.getMessage());
                  passField.setText("");
            }
      }

      public void setOnLoginSuccess(Runnable r) { this.onLoginSuccess = r; }
}