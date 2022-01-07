package edu.client.gui;

import edu.client.exception.RequestProcessFailureException;
import edu.client.gui.utils.UIUtils;
import edu.client.utils.ConfigProvider;
import edu.cnp.parts.CnpParts;
import edu.utils.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ClientMainView extends JFrame {

    private final Map<String, Font> customFont; // custom font különböző méretekben

    private JPanel contentPanelMainView; // főpanel
    private JPanel controlsPanel; // felső vezérlőpanel
    private JComboBox<String> inputFormatsList; // input formátumokat tartalmazó dropdown
    private JComboBox<String> outputFormatsList; // output formátumokat
    private JComboBox<String> metricsTypesList; // mutatóösszeállítás típusokat
    private JPanel chooserPanel; // felső dropdownokat tartalmazó panel
    private JButton btnInputFileChooser; //  input fájlkiválasztó
    private JButton btnOutputFileChooser; // output fájlkiválasztó
    private JPanel processPanel; // feldolgozást elindító gombot tartalmazó panel
    private JButton processBtn; // feldolgozást elindító gomb

    private JPanel contentPanelSecondaryView;
    private JComboBox<String> groupByList;

    private JMenu fileMenu;

    private final ClientController controller;

    public ClientMainView(ClientController controller) {
        this.controller = controller;

        UIUtils.initLookAndFeel();
        customFont = UIUtils.loadFont();

        this.setTitle("Transaction processor");
        this.setMinimumSize(new Dimension(ClientController.WIN_SIZE, ClientController.WIN_SIZE));

        initMainPanel();
        initSecondaryPanel();

        initMenuBar();

        // main view
        this.setContentPane(contentPanelMainView);
        this.setSize(ClientController.WIN_SIZE, ClientController.WIN_SIZE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public JPanel getMainView() {
        return contentPanelMainView;
    }

    public JPanel getSecondaryView() {
        return contentPanelSecondaryView;
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.setFont(customFont.get("13"));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setFont(customFont.get("13"));
        aboutMenu.setMnemonic(KeyEvent.VK_A);

        JMenuItem about = new JMenuItem("About");
        about.setFont(customFont.get("12"));
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, "Created by Lukacs Zsolt in 2021 - Simple server-client based transaction processor which makes use of my CNP validator."));

        JMenuItem exit = new JMenuItem("Exit");
        exit.setFont(customFont.get("12"));
        exit.addActionListener(e -> System.exit(0));

        JMenuItem inputFile = new JMenuItem("Select file");
        inputFile.setFont(customFont.get("12"));
        try {
            inputFile.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/input.png")))));
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for input menubutton.");
        }
        inputFile.addActionListener(e -> controller.updateInputPath(btnInputFileChooser, inputFile));

        JMenuItem outputFile = new JMenuItem("Select file");
        outputFile.setFont(customFont.get("12"));
        try {
            outputFile.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/output.png")))));
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for output menubutton.");
        }
        outputFile.addActionListener(e -> controller.updateOutputPath(btnOutputFileChooser, outputFile));

        fileMenu.add(inputFile);
        fileMenu.add(outputFile);
        aboutMenu.add(about);
        aboutMenu.add(exit);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        this.setJMenuBar(menuBar);
    }

    private void initMainPanel() {
        contentPanelMainView = new JPanel();
        contentPanelMainView.setLayout(new GridLayout(3, 1));
        contentPanelMainView.setPreferredSize(new Dimension(ClientController.WIN_SIZE, ClientController.WIN_SIZE));

        initControlsPanel();
        contentPanelMainView.add(controlsPanel);

        initChooserPanel();
        contentPanelMainView.add(chooserPanel);

        initProcessBtn();
        contentPanelMainView.add(processPanel);
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
        inputFormatsList.setSelectedItem(ConfigProvider.getProperty("input.format"));
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
        outputFormatsList.setSelectedItem(ConfigProvider.getProperty("output.format"));
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
        metricsTypesList.setSelectedItem(ConfigProvider.getProperty("processor.type"));
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
        btnInputFileChooser.addActionListener(e -> controller.updateInputPath(btnInputFileChooser, null));

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
        btnOutputFileChooser.addActionListener(e -> controller.updateOutputPath(btnOutputFileChooser, null));

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
                showErrorMessage(ex.getMessage());
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

    private void initSecondaryPanel() {
        contentPanelSecondaryView = new JPanel();
        contentPanelSecondaryView.setLayout(new GridLayout(4, 1));
        contentPanelSecondaryView.setPreferredSize(new Dimension(ClientController.WIN_SIZE, ClientController.WIN_SIZE));

        initBackPanel();
        initShowMetrices();
        initShowStatistics();
        initCreateChart();
    }

    private void initBackPanel() {
        JPanel backPanel = new JPanel(new GridLayout(2, 3));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(customFont.get("14"));
        try {
            backBtn.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/left-arrow.png")))));
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for back button.");
        }
        backBtn.addActionListener((e) -> {
            controller.setCurrentView(contentPanelMainView);
            this.toggleFileMenu();
        });

        backPanel.add(backBtn);
        UIUtils.fillWithBlankLabels(backPanel, 2);
        UIUtils.fillWithSeparators(backPanel, 3);
        contentPanelSecondaryView.add(backPanel);
    }

    private void initShowMetrices() {
        JButton metricesBtn = new JButton("Show metrices");
        metricesBtn.setFont(customFont.get("14"));
        try {
            metricesBtn.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/table.png")))));
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for show metrices button.");
        }
        metricesBtn.addActionListener((e) -> controller.createTableOf("metrices"));

        contentPanelSecondaryView.add(metricesBtn);
    }

    private void initShowStatistics() {
        JButton statisticsBtn = new JButton("Show customers' statistics");
        statisticsBtn.setFont(customFont.get("14"));
        try {
            statisticsBtn.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/table.png")))));
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for show statistics button.");
        }
        statisticsBtn.addActionListener((e) -> controller.createTableOf("statistics"));

        contentPanelSecondaryView.add(statisticsBtn);
    }

    private void initCreateChart() {
        JPanel chartPanel = new JPanel(new GridLayout(1, 2));
        JPanel chartControls = new JPanel(new GridLayout(3, 1));
        JLabel groupByLabel = new JLabel("Group by:");
        groupByLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        groupByLabel.setFont(customFont.get("14"));
        chartControls.add(groupByLabel);
        groupByList = new JComboBox<>(CnpParts.getParts());
        chartControls.add(groupByList);
        UIUtils.fillWithBlankLabels(chartControls, 1);

        chartPanel.add(chartControls);

        JButton createChartBtn = new JButton("Create chart");
        try {
            createChartBtn.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/imgs/chart.png")))));
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Failed to set icon for create chart button.");
        }
        createChartBtn.setFont(customFont.get("14"));
        chartPanel.add(createChartBtn);
        createChartBtn.addActionListener((e) -> controller.createChartOf(groupByList.getSelectedItem().toString()));

        contentPanelSecondaryView.add(chartPanel);
    }

    /**
     * Megjeleníti a fájlkiválasztót, majd visszatéríti a kijelölt állományt.
     *
     * @return kijelölt állomány vagy null, ha nem választott ki semmit
     */
    public File showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    public File showDirectoryChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateBtnLabel(JButton btn, String text) {
        btn.setText(text);
    }

    public void toggleProcessBtn() {
        this.processBtn.setEnabled(!this.processBtn.isEnabled());
    }

    public void toggleFileMenu() {
        this.fileMenu.setEnabled(!this.fileMenu.isEnabled());
    }

}
