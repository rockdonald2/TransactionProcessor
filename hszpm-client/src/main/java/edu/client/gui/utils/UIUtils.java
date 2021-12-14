package edu.client.gui.utils;

import edu.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class UIUtils {

  public static void initLookAndFeel() {
    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
      if ("Nimbus".equals(info.getName())) {
        try {
          UIManager.setLookAndFeel(info.getClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
          Logger.getLogger().logMessage(Logger.LogLevel.CRITICAL, "Failed to initialize look and feel.");
        }
        break;
      }
    }
  }

  public static Map<String, Font> loadFont() {
    Map<String, Font> fonts = new HashMap<>();

    try {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      Font customFont;

      for (float i = 10; i <= 24; ++i) {
        customFont = Font.createFont(Font.TRUETYPE_FONT, new File(UIUtils.class.getResource("/fonts/openSans.ttf").getPath())).deriveFont(i);
        ge.registerFont(customFont);
        fonts.put(String.valueOf((int) i), customFont);
      }

      return fonts;
    } catch (IOException | FontFormatException e) {
      System.err.println(e.getMessage());
      // TODO: exception
    }

    return Collections.emptyMap();
  }

  public static void fillWithBlankLabels(JPanel panel, int times) {
    for (int i = 0; i < times; i++) {
      panel.add(new JLabel());
    }
  }

}
