package co.edu.uniquindio.View.utils;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedBorder extends AbstractBorder {

      private final int radius;
      private final Color color;

      public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
      }

      @Override
      public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, radius, radius));
            g2.dispose();
      }

      @Override
      public Insets getBorderInsets(Component c) {
            int ins = Math.max(1, radius / 4);
            return new Insets(ins, ins, ins, ins);
      }

      @Override
      public Insets getBorderInsets(Component c, Insets insets) {
            int ins = Math.max(1, radius / 4);
            insets.set(ins, ins, ins, ins);
            return insets;
      }
}