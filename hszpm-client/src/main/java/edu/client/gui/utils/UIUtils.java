package edu.client.gui.utils;

import edu.client.utils.ConfigProvider;
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
            if (info.getName().equals(ConfigProvider.getProperty("look.and.feel"))) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to initialize look and feel.");
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
                customFont = Font.createFont(Font.TRUETYPE_FONT, new File(UIUtils.class.getResource(String.format("/fonts/%s.ttf", ConfigProvider.getProperty("font"))).getPath())).deriveFont(i);
                ge.registerFont(customFont);
                fonts.put(String.valueOf((int) i), customFont);
            }

            return fonts;
        } catch (IOException | FontFormatException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to load fonts. " + e.getMessage());
        }

        return Collections.emptyMap();
    }

    public static void fillWithBlankLabels(JPanel panel, int times) {
        for (int i = 0; i < times; i++) {
            panel.add(new JLabel());
        }
    }

    public static void fillWithSeparators(JPanel panel, int times) {
        for (int i = 0; i < times; i++) {
            panel.add(new JSeparator());
        }
    }
}
