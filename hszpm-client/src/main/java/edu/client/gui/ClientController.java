package edu.client.gui;

import edu.client.gui.utils.LoaderPanel;
import edu.network.exception.ClientException;
import edu.client.exception.RequestProcessFailureException;
import edu.client.network.Client;
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
    PropertyProvider.setClientProperty("input.format", path, true);
  }

  public void updateInputPath(JButton btn) {
    File selected = view.showChooser();
    updateBtnLabel(btn, selected);
    model.setInput(selected);
  }

  public void updateOutputFormat(String path) {
    PropertyProvider.setClientProperty("output.format", path, true);
  }

  public void updateOutputPath(JButton btn) {
    File selected = view.showChooser();
    updateBtnLabel(btn, selected);
    model.setOutput(selected);
  }

  public void updateMetricsType(String type) {
    PropertyProvider.setClientProperty("processor.type", type, true);
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

    SwingWorker<Object, Object> loadingProcess = new SwingWorker<>() {
      @Override
      protected Object doInBackground() throws Exception {
        LoaderPanel.getInstance().showLoader();
        view.toggleProcessBtn();
        return null;
      }
    };
    loadingProcess.execute();

    SwingWorker<Object, Object> longProcess = new SwingWorker<>() {
      @Override
      protected Object doInBackground() throws Exception {
        try {
          new Client(ClientController.this).requestProcess(input.getAbsolutePath(), output.getAbsolutePath());
        } catch (ClientException e) {
          ClientView.showErrorMessage(e.getMessage());
        }

        return null;
      }

      @Override
      protected void done() {
        LoaderPanel.getInstance().hideLoader();
        view.toggleProcessBtn();
      }
    };
    longProcess.execute();
  }

  public static void main(String[] args) {
    new ClientController();
  }

}
