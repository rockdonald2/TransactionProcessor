package edu.client.gui;

import edu.client.gui.utils.UIUtils;
import edu.client.utils.ConfigProvider;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientConfigView extends JFrame {

    private final Map<String, Font> customFont;

    List<JTextField> editProps;

    public ClientConfigView(ClientController controller) {
        customFont = UIUtils.loadFont();

        JPanel mainPanel = new JPanel();
        editProps = new ArrayList<>();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        var props = ConfigProvider.getKeys().stream()
                .filter(elem -> !elem.contains("input") && !elem.contains("output") && !elem.contains("processor")).toList();

        mainPanel.setLayout(new GridLayout(props.size() + 2, 1));

        for (var prop : props) {
            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new GridLayout(1, 2));

            JLabel propLabel = new JLabel(prop);
            propLabel.setFont(customFont.get("13"));
            tmpPanel.add(propLabel);

            JTextField editProp = new JTextField();
            editProp.setText(ConfigProvider.getProperty(prop));
            editProp.setFont(customFont.get("13"));
            editProps.add(editProp);
            tmpPanel.add(editProp);

            mainPanel.add(tmpPanel);
        }

        UIUtils.fillWithBlankLabels(mainPanel, 1);

        JPanel saveCancelPanel = new JPanel();
        saveCancelPanel.setLayout(new GridLayout(1, 2));

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(customFont.get("14"));
        saveBtn.addActionListener((e) -> {
            controller.saveConfig(props, editProps.stream().map(JTextComponent::getText).toList());
            this.dispose();
        });
        saveBtn.setBackground(new Color(174, 254, 255));
        saveCancelPanel.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(customFont.get("14"));
        cancelBtn.addActionListener((e) -> this.dispose());
        cancelBtn.setBackground(new Color(255, 89, 89));
        saveCancelPanel.add(cancelBtn);

        mainPanel.add(saveCancelPanel);

        this.setSize(new Dimension(ClientController.WIN_SIZE / 2, ClientController.WIN_SIZE / 2));
        this.setContentPane(mainPanel);
        this.setTitle("Configuration");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

}
