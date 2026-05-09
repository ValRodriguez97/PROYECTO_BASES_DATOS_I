package co.edu.uniquindio.View.utils;

import java.awt.*;

public class UIColors {

      private UIColors() {}

      // -- Colores FIFA 2026 --
      public static final Color PURPLE = new Color(0x4A148C);
      public static final Color PURPLE_LIGHT = new Color(0x6A1FAC);
      public static final Color PURPLE_PALE = new Color(0xEDE7F6);
      public static final Color BLUE = new Color(0x1565C0);
      public static final Color BLUE_LIGHT = new Color(0x1976D2);
      public static final Color TURQUOISE = new Color(0x00796B);
      public static final Color MAGENTA = new Color(0xAD1457);
      public static final Color RED = new Color(0xC62828);
      
      // -- Neutros --
       public static final Color BG_PAGE = new Color(0xF4F6F9);
      public static final Color BG_CARD = Color.WHITE;
      public static final Color BG_HOVER = new Color(0xF1F3F7);
      public static final Color SIDEBAR_BG = new Color(0x1C1C2E);
      public static final Color SIDEBAR_ACCENT= new Color(0x2A2A42);
      public static final Color BORDER = new Color(0xE2E6EA);
      public static final Color BORDER_STRONG = new Color(0xCDD3DA);
      public static final Color TEXT_PRIMARY = new Color(0x1A1A2E);
      public static final Color TEXT_SECONDARY = new Color(0x5A6474);
      public static final Color TEXT_MUTED = new Color(0x9AA3AD);

      // -- Estados --
      public static final Color SUCCESS_BG = new Color(0xE8F5E9);
      public static final Color SUCCESS_FG = new Color(0x1B5E20);
      public static final Color WARNING_BG = new Color(0xFFFDE7);
      public static final Color WARNING_FG = new Color(0xE65100);
      public static final Color ERROR_BG = new Color(0xFFEBEE);
      public static final Color ERROR_FG = new Color(0xB71C1C);
      public static final Color INFO_BG = new Color(0xE3F2FD);
      public static final Color INFO_FG = new Color(0x0D47A1);

      // -- Badges por confederación --
      public static final Color UEFA_BG = new Color(0xE3F2FD);
      public static final Color UEFA_FG = new Color(0x1565C0);
      public static final Color CONMEBOL_BG = new Color(0xE8F5E9);
      public static final Color CONMEBOL_FG = new Color(0x1B5E20);
      public static final Color CONCACAF_BG = new Color(0xFFF8E1);
      public static final Color CONCACAF_FG = new Color(0xE65100);
      public static final Color CAF_BG = new Color(0xFCE4EC);
      public static final Color CAF_FG = new Color(0x880E4F);
      public static final Color AFC_BG = new Color(0xFFEBEE);
      public static final Color AFC_FG = new Color(0xB71C1C);
      public static final Color OFC_BG = new Color(0xE0F2F1);
      public static final Color OFC_FG = new Color(0x00695C);

      public static Color[] confederacionColors(String sigla) {
        if (sigla == null) return new Color[]{INFO_BG, INFO_FG};
        return switch (sigla.toUpperCase()) {
            case "UEFA" -> new Color[]{UEFA_BG, UEFA_FG};
            case "CONMEBOL" -> new Color[]{CONMEBOL_BG, CONMEBOL_FG};
            case "CONCACAF" -> new Color[]{CONCACAF_BG, CONCACAF_FG};
            case "CAF" -> new Color[]{CAF_BG, CAF_FG};
            case "AFC" -> new Color[]{AFC_BG, AFC_FG};
            case "OFC" -> new Color[]{OFC_BG, OFC_FG};
            default -> new Color[]{INFO_BG, INFO_FG};
        };
    }
}
