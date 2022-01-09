package edu.client.gui.utils;

import edu.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;

public class LoaderPanel extends JFrame {

  private static LoaderPanel instance;

  private LoaderPanel(String msg) {
    final Map<String, Font> customFont = UIUtils.loadFont();

    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel(msg, SwingConstants.CENTER);
    label.setFont(customFont.get("12"));
    ImageIcon image = null;
    try {
      image = new ImageIcon(LoaderPanel.class.getResourceAsStream("/imgs/loader.gif").readAllBytes());
    } catch (IOException | NullPointerException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to load loader gif.");
    }

    label.setIcon(image);
    panel.add(label, BorderLayout.CENTER);

    this.setTitle("Loading...");
    this.setContentPane(panel);
    this.setSize(250, 100);
    this.setLocationRelativeTo(null);
    this.setType(Type.UTILITY);
  }

  public static LoaderPanel getInstance() {
    if (instance == null) {
      instance = new LoaderPanel("In process...");
    }

    return instance;
  }

  public void showLoader() {
    this.setVisible(true);
  }

  public void hideLoader() {
    this.setVisible(false);
  }

}