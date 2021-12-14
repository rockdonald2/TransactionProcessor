package edu.client.gui;

import edu.client.gui.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ClientTableView extends JFrame {

  private final Map<String, Font> customFont;

  public ClientTableView(String[] columns, String[][] data) {
    customFont = UIUtils.loadFont();

    JTable table = new JTable(data, columns) {
      public boolean isCellEditable(int row, int column) {
        return false;
      };
    };
    table.setFont(customFont.get("12"));
    table.setDragEnabled(false);
    table.setRowHeight(30);

    JScrollPane scrollTablePanel = new JScrollPane(table);
    this.setContentPane(scrollTablePanel);
    this.setTitle("Process Result Table");
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setVisible(true);
    this.pack();
    this.setLocationRelativeTo(null);
  }

}
