package co.edu.uniquindio.View.utils;

import java.awt.*;

public class UIFonts {
      
      private UIFonts() {}

      // -- Fuentes base del sistema
      private static final String BASE = Font.SANS_SERIF;
      
      public static final Font HEADING_XL = new Font(BASE, Font.BOLD, 22);
      public static final Font HEADING_LG = new Font(BASE, Font.BOLD, 18);
      public static final Font HEADING_MD = new Font(BASE, Font.BOLD, 15);
      public static final Font HEADING_SM = new Font(BASE, Font.BOLD, 13);
      
      public static final Font BODY_LG = new Font(BASE, Font.PLAIN, 14);
      public static final Font BODY_MD = new Font(BASE, Font.PLAIN, 13);
      public static final Font BODY_SM = new Font(BASE, Font.PLAIN, 12);
      
      public static final Font LABEL_BOLD = new Font(BASE, Font.BOLD, 13);
      public static final Font LABEL_MD = new Font(BASE, Font.BOLD, 12);
      
      public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 12);
      public static final Font SIDEBAR_ITEM = new Font(BASE, Font.PLAIN, 13);
      public static final Font SIDEBAR_TITLE = new Font(BASE, Font.BOLD, 14);
      
      public static final Font STAT_NUMBER = new Font(BASE, Font.BOLD, 26);
      public static final Font STAT_LABEL = new Font(BASE, Font.PLAIN, 11);
      
      public static final Font TABLE_HEADER= new Font(BASE, Font.BOLD, 12);
      public static final Font TABLE_CELL = new Font(BASE, Font.PLAIN, 12);
      public static final Font BUTTON_MD = new Font(BASE, Font.BOLD, 13);
      public static final Font BADGE = new Font(BASE, Font.BOLD, 11);
}
