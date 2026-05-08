package co.edu.uniquindio.View.panels;

import co.edu.uniquindio.View.utils.*;
import co.edu.uniquindio.services.SistemaSeguridadService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginPanel extends JPanel{

      private final SistemaSeguridadService seguridad;
      private JTextField userField;
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
      
            GradientPaint grad = new GradientPaint(
                  0, 0,              UIColors.PURPLE,
                  getWidth() / 2f, getHeight(), UIColors.BLUE,
                  true);
            g2.setPaint(grad);
            g2.fillRect(0, 0, getWidth(), getHeight());
      
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval(-80, -80, 400, 400);
      
            g2.setColor(new Color(0xE91E63, false));
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f);
            g2.setComposite(ac);
            g2.fillOval(getWidth() - 200, getHeight() - 200, 500, 500);
      
            g2.dispose();
      }
      
      private void build() {
            JPanel card = new JPanel() {
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0, 0, 0, 40));
                        g2.fill(new RoundRectangle2D.Float(4, 8, getWidth() - 8, getHeight() - 4, 20, 20));
                        g2.setColor(Color.WHITE);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                        g2.dispose();
                        super.paintComponent(g);
                  }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(420, 520));
            card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 44));
      
            
            JPanel logoRow = buildLogo();
            logoRow.setAlignmentX(CENTER_ALIGNMENT);
 
            JLabel formTitle = new JLabel("Iniciar Sesión");
            formTitle.setFont(UIFonts.HEADING_LG);
            formTitle.setForeground(UIColors.TEXT_PRIMARY);
            formTitle.setAlignmentX(CENTER_ALIGNMENT);
      
            JLabel userLabel = UIFactory.formLabel("Usuario");
            userLabel.setAlignmentX(LEFT_ALIGNMENT);
            userField = UIFactory.textField("Ingresa tu usuario");
            userField.setAlignmentX(LEFT_ALIGNMENT);
            userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      
            JLabel passLabel = UIFactory.formLabel("Contraseña");
            passLabel.setAlignmentX(LEFT_ALIGNMENT);
            passField = UIFactory.passwordField("Ingresa tu contraseña");
            passField.setAlignmentX(LEFT_ALIGNMENT);
            passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      
            errorLabel = new JLabel(" ");
            errorLabel.setFont(UIFonts.BODY_SM);
            errorLabel.setForeground(UIColors.RED);
            errorLabel.setAlignmentX(CENTER_ALIGNMENT);
      
            JButton loginBtn = UIFactory.primaryButton("Iniciar Sesión");
            loginBtn.setAlignmentX(CENTER_ALIGNMENT);
            loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            loginBtn.addActionListener(e -> doLogin());
      
            ActionListener enterAction = e -> doLogin();
            userField.addActionListener(enterAction);
            passField.addActionListener(enterAction);
      
            JLabel footer = new JLabel("Sistema de Base de Datos · Proyecto Universitario");
            footer.setFont(UIFonts.BODY_SM);
            footer.setForeground(UIColors.TEXT_MUTED);
            footer.setAlignmentX(CENTER_ALIGNMENT);
 
            card.add(logoRow);
            card.add(Box.createVerticalStrut(24));
            card.add(formTitle);
            card.add(Box.createVerticalStrut(28));
            card.add(userLabel);
            card.add(Box.createVerticalStrut(6));
            card.add(userField);
            card.add(Box.createVerticalStrut(16));
            card.add(passLabel);
            card.add(Box.createVerticalStrut(6));
            card.add(passField);
            card.add(Box.createVerticalStrut(8));
            card.add(errorLabel);
            card.add(Box.createVerticalStrut(8));
            card.add(loginBtn);
            card.add(Box.createVerticalGlue());
            card.add(UIFactory.separator());
            card.add(Box.createVerticalStrut(16));
            card.add(footer);
      
            add(card);
 
            JPanel flagRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            flagRow.setOpaque(false);
            for (String s : new String[]{"🇲🇽 México", "🇺🇸 Estados Unidos", "🇨🇦 Canadá"}) {
                  JLabel fl = new JLabel(s);
                  fl.setFont(UIFonts.BODY_SM);
                  fl.setForeground(new Color(255, 255, 255, 180));
                  flagRow.add(fl);
            }
      
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
            add(card, gbc);
            gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);
            add(flagRow, gbc);
      }
 
      private JPanel buildLogo() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            p.setOpaque(false);
      
            JPanel icon = new JPanel() {
                  { setPreferredSize(new Dimension(64, 64)); setOpaque(false); }
                  @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setPaint(new GradientPaint(0, 0, UIColors.PURPLE, 64, 64, UIColors.MAGENTA));
                        g2.fill(new RoundRectangle2D.Float(0, 0, 64, 64, 16, 16));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
                        g2.drawString("⚽", 10, 47);
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
      
            p.add(icon);
            p.add(text);
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
