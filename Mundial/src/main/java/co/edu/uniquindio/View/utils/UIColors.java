package co.edu.uniquindio.View.utils;

import java.awt.*;

public class UIColors {

      private UIColors() {}

      // -- Colores FIFA 2026 --
      public static final Color PURPLE = new Color(0x4A148C);
      public static final Color PURPLE_LIGHT = new Color(0x6A1FAC);
      public static final Color PURPLE_PALE = new Color(0xEDE7F6);
      public static final Color BLUE = new Color(0x0066FF);
      public static final Color BLUE_LIGHT = new Color(0x3385FF);
      public static final Color TURQUOISE = new Color(0x00BFA5);
      public static final Color MAGENTA = new Color(0xE91E63);
      public static final Color RED = new Color(0xFF1744);
      
      // -- Neutros --
      public static final Color BG_PAGE = new Color(0xF5F7FA);
      public static final Color BG_CARD = Color.WHITE;
      public static final Color BG_HOVER = new Color(0xF8F9FC);
      public static final Color SIDEBAR_BG = new Color(0x1A1A2E);
      public static final Color SIDEBAR_ACCENT = new Color(0x2D2D44);
      public static final Color BORDER = new Color(0xE5E7EB);
      public static final Color BORDER_STRONG = new Color(0xD1D5DB);
      public static final Color TEXT_PRIMARY = new Color(0x1A1A2E);
      public static final Color TEXT_SECONDARY = new Color(0x64748B);
      public static final Color TEXT_MUTED = new Color(0x94A3B8);

      // -- Estados --
      public static final Color SUCCESS_BG = new Color(0xDCFCE7);
      public static final Color SUCCESS_FG = new Color(0x166534);
      public static final Color WARNING_BG = new Color(0xFEF9C3);
      public static final Color WARNING_FG = new Color(0x854D0E);
      public static final Color ERROR_BG = new Color(0xFEE2E2);
      public static final Color ERROR_FG = new Color(0x991B1B);
      public static final Color INFO_BG = new Color(0xEFF6FF);
      public static final Color INFO_FG = new Color(0x1E40AF);

      // -- Badges por confederación --
      public static final Color UEFA_BG = new Color(0xDBEAFE);
      public static final Color UEFA_FG = new Color(0x1D4ED8);
      public static final Color CONMEBOL_BG = new Color(0xDCFCE7);
      public static final Color CONMEBOL_FG = new Color(0x166534);
      public static final Color CONCACAF_BG = new Color(0xFFF7ED);
      public static final Color CONCACAF_FG = new Color(0xC2410C);
      public static final Color CAF_BG = new Color(0xFDF4FF);
      public static final Color CAF_FG = new Color(0x86198F);
      public static final Color AFC_BG = new Color(0xFFF1F2);
      public static final Color AFC_FG = new Color(0xBE123C);
      public static final Color OFC_BG = new Color(0xF0FDFA);
      public static final Color OFC_FG = new Color(0x0F766E);

      // -- Gradientes para headers de card --
      public static GradientPaint gradientPurpleBlue(int width) {
            return new GradientPaint(0, 0, PURPLE, width, 0, BLUE);
      }
      
      public static GradientPaint gradientMagentaRed(int width) {
            return new GradientPaint(0, 0, MAGENTA, width, 0, RED);
      }
      
      public static GradientPaint gradientBlueTurquoise(int width) {
            return new GradientPaint(0, 0, BLUE, width, 0, TURQUOISE);
      }

      public static Color[] confederacionColors(String sigla) {
        if (sigla == null) return new Color[]{INFO_BG, INFO_FG};
        return switch (sigla.toUpperCase()) {
            case "UEFA"     -> new Color[]{UEFA_BG, UEFA_FG};
            case "CONMEBOL" -> new Color[]{CONMEBOL_BG, CONMEBOL_FG};
            case "CONCACAF" -> new Color[]{CONCACAF_BG, CONCACAF_FG};
            case "CAF"      -> new Color[]{CAF_BG, CAF_FG};
            case "AFC"      -> new Color[]{AFC_BG, AFC_FG};
            case "OFC"      -> new Color[]{OFC_BG, OFC_FG};
            default         -> new Color[]{INFO_BG, INFO_FG};
        };
      }
}
