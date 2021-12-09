package edu.gui;

import edu.exception.RequestProcessFailureException;
import edu.gui.utils.UIUtils;
import edu.utils.Logger;
import edu.utils.PropertyProvider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ClientView extends JFrame {

  private final Map<String, Font> customFont; // custom font különböző méretekben
  private final JPanel contentPanel; // főpanel
  private JPanel controlsPanel; // felső vezérlőpanel
  private JComboBox<String> inputFormatsList; // input formátumokat tartalmazó dropdown
  private JComboBox<String> outputFormatsList; // output formátumokat
  private JComboBox<String> metricsTypesList; // mutatóösszeállítás típusokat
  private JPanel chooserPanel; // felső dropdownokat tartalmazó panel
  private JButton btnInputFileChooser; //  input fájlkiválasztó
  private JButton btnOutputFileChooser; // output fájlkiválasztó
  private JPanel processPanel; // feldolgozást elindító gombot tartalmazó panel
  private JButton processBtn; // feldolgozást elindító gomb

  private ClientController controller;

  public ClientView(ClientController controller) {
    this.controller = controller;

    UIUtils.initLookAndFeel();
    customFont = UIUtils.loadFont();

    this.setTitle("Transaction processor");
    this.setMinimumSize(new Dimension(ClientController.WIN_SIZE, ClientController.WIN_SIZE));

    contentPanel = new JPanel();
    contentPanel.setLayout(new GridLayout(3, 1));
    contentPanel.setPreferredSize(new Dimension(ClientController.WIN_SIZE, ClientController.WIN_SIZE));

    initControlsPanel();
    contentPanel.add(controlsPanel);

    initChooserPanel();
    contentPanel.add(chooserPanel);

    initProcessBtn();
    contentPanel.add(processPanel);

    this.setContentPane(contentPanel);
  }

  private void initControlsPanel() {
    controlsPanel = new JPanel();
    controlsPanel.setLayout(new GridLayout(2, 1));
    JLabel heading = new JLabel("Transaction processor", SwingConstants.CENTER);
    heading.setFont(customFont.get("24"));
    controlsPanel.add(heading);

    JPanel controlsGroup = new JPanel();
    controlsGroup.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.LINE_START;

    JLabel inputLabel = new JLabel("Input file format");
    inputLabel.setFont(customFont.get("14"));
    inputFormatsList = new JComboBox<>(new String[]{"csv"});
    inputFormatsList.setSelectedItem(PropertyProvider.getProperty("input.format"));
    inputFormatsList.setFont(customFont.get("13"));
    inputFormatsList.addItemListener(e -> controller.updateInputFormat(String.valueOf(inputFormatsList.getSelectedItem())));

    gbc.gridx = 0;
    gbc.weightx = 0.5;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    controlsGroup.add(inputLabel, gbc);

    gbc.gridx = 1;
    gbc.weightx = 3;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 0;
    controlsGroup.add(inputFormatsList, gbc);

    JLabel outputLabel = new JLabel("Output file format");
    outputLabel.setFont(customFont.get("14"));
    outputFormatsList = new JComboBox<>(new String[]{"json"});
    outputFormatsList.setSelectedItem(PropertyProvider.getProperty("output.format"));
    outputFormatsList.setFont(customFont.get("13"));
    outputFormatsList.addItemListener(e -> controller.updateOutputFormat(String.valueOf(outputFormatsList.getSelectedItem())));

    gbc.gridx = 0;
    gbc.weightx = 0.5;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    controlsGroup.add(outputLabel, gbc);

    gbc.gridx = 1;
    gbc.weightx = 3;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    controlsGroup.add(outputFormatsList, gbc);

    JLabel typesLabel = new JLabel("Metrics format");
    typesLabel.setFont(customFont.get("14"));
    metricsTypesList = new JComboBox<>(new String[]{"simple"});
    metricsTypesList.setFont(customFont.get("13"));
    metricsTypesList.setSelectedItem(PropertyProvider.getProperty("processor.type"));
    metricsTypesList.addItemListener(e -> controller.updateMetricsType(String.valueOf(metricsTypesList.getSelectedItem())));

    gbc.gridx = 0;
    gbc.weightx = 0.5;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.VERTICAL;
    controlsGroup.add(typesLabel, gbc);

    gbc.gridx = 1;
    gbc.weightx = 3;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.BOTH;
    controlsGroup.add(metricsTypesList, gbc);

    controlsPanel.add(controlsGroup);
  }

  private void initChooserPanel() {
    chooserPanel = new JPanel();
    chooserPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;

    btnInputFileChooser = new JButton("Select input file");
    btnInputFileChooser.setFont(customFont.get("13"));
    try {
      btnInputFileChooser.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/input.png")))));
    } catch (IOException | NullPointerException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for input button.");
    }
    btnInputFileChooser.addActionListener(e -> controller.updateInputPath(btnInputFileChooser));

    gbc.gridx = 0;
    gbc.gridy = 0;
    chooserPanel.add(btnInputFileChooser, gbc);

    btnOutputFileChooser = new JButton("Select output file");
    btnOutputFileChooser.setFont(customFont.get("13"));
    try {
      btnOutputFileChooser.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/output.png")))));
    } catch (IOException | NullPointerException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for output button.");
    }
    btnOutputFileChooser.addActionListener(e -> controller.updateOutputPath(btnOutputFileChooser));

    gbc.gridx = 1;
    gbc.gridy = 0;
    chooserPanel.add(btnOutputFileChooser, gbc);
  }

  private void initProcessBtn() {
    processPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    processBtn = new JButton("Process transactions");
    processBtn.setFont(customFont.get("18"));
    try {
      processBtn.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/process.png")))));
    } catch (IOException | NullPointerException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for process request button.");
    }

    processBtn.addActionListener(e -> {
      try {
        controller.requestProcess();
      } catch (RequestProcessFailureException ex) {
        ClientView.this.showErrorMessage(ex.getMessage());
      }
    });

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    processPanel.add(processBtn, gbc);
  }

  /**
   * Megjeleníti a fájlkiválasztót, majd visszatéríti a kijelölt állományt.
   *
   * @return kijelölt állomány vagy null, ha nem választott ki semmit
   */
  public File showChooser() {
    JFileChooser fileChooser = new JFileChooser();
    int returnValue = fileChooser.showOpenDialog(this);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    }

    return null;
  }

  public void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void showInformationMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
  }

  public void updateBtnLabel(JButton btn, String text) {
    btn.setText(text);
  }

}
