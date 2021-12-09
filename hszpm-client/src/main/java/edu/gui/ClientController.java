package edu.gui;

import edu.exception.ClientException;
import edu.exception.RequestProcessFailureException;
import edu.network.Client;
import edu.utils.Logger;
import edu.utils.PropertyProvider;

import javax.swing.*;
import java.io.File;

public class ClientController {

  public static final int WIN_SIZE = 650;

  private final ClientView view;
  private final ClientModel model;

  public ClientController() {
    this.model = new ClientModel();
    this.view = new ClientView(this);
    this.view.setSize(WIN_SIZE, WIN_SIZE);
    this.view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.view.setVisible(true);
    this.view.setLocationRelativeTo(null);
    this.view.pack();
  }

  private void updateBtnLabel(JButton btn, File selected) {
    if (selected != null && selected.exists()) {
      view.updateBtnLabel(btn, String.format("Selected %s", selected.getName()));
    } else {
      view.updateBtnLabel(btn, "Select input file");
    }
  }

  public void updateInputFormat(String path) {
    PropertyProvider.setProperty("input.format", path);
  }

  public void updateInputPath(JButton btn) {
    File selected = view.showChooser();
    updateBtnLabel(btn, selected);
    model.setInput(selected);
  }

  public void updateOutputFormat(String path) {
    PropertyProvider.setProperty("output.format", path);
  }

  public void updateOutputPath(JButton btn) {
    File selected = view.showChooser();
    updateBtnLabel(btn, selected);
    model.setOutput(selected);
  }

  public void updateMetricsType(String type) {
    PropertyProvider.setProperty("processor.type", type);
  }

  public void requestProcess() throws RequestProcessFailureException {
    File input = model.getInput();
    File output = model.getOutput();

    if (input == null) {
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Input file not specified");
      throw new RequestProcessFailureException("Input file not specified.");
    }

    if (output == null) {
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Output file not specified");
      throw new RequestProcessFailureException("Output file not specified.");
    }

    try {
      new Client(this).requestProcess(input.getAbsolutePath(), output.getAbsolutePath());
    } catch (ClientException e) {
      throw new RequestProcessFailureException(e.getMessage());
    }
  }

  public static void main(String[] args) {
    new ClientController();
  }

}
