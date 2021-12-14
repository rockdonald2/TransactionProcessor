package edu.client.gui;

import edu.client.gui.utils.UIUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;

public class ClientChartView extends JFrame {

    public ClientChartView(JFreeChart chart) {
        var customFont = UIUtils.loadFont();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        chartPanel.setFont(customFont.get("14"));

        this.add(chartPanel);
        this.pack();
        this.setTitle("Average by group chart");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

}
