package edu.client.gui;

import edu.client.exception.LayerException;
import edu.client.gui.utils.LoaderPanel;
import edu.cnp.parts.CnpParts;
import edu.network.exception.ClientException;
import edu.client.exception.RequestProcessFailureException;
import edu.client.network.Client;
import edu.utils.Logger;
import edu.utils.PropertyProvider;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ClientController {

    public static final int WIN_SIZE = 650;

    private final ClientMainView view;
    private final ClientModel model;

    public ClientController() {
        this.model = new ClientModel();
        this.view = new ClientMainView(this);
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

    public void setCurrentView(JPanel newView) {
        SwingUtilities.invokeLater(() -> {
            view.setContentPane(newView);
            view.repaint();
            view.revalidate();
        });
    }

    public void createTableOf(String of) {
        switch (of) {
            case "metrices" -> {
                var rawData = model.getMetrices();
                var rawDataColumns = rawData.keySet();
                rawDataColumns.remove("errors");
                String[] columns = rawDataColumns.toArray(new String[] {});
                String[][] data = new String[1][columns.length];

                int i = 0;
                for (var key : rawData.keySet()) {
                    data[0][i++] = rawData.get(key).toString();
                }

                new ClientTableView(columns, data);
            }
            case "statistics" -> {
                var customers = model.getCustomers();
                String[] columns = CnpParts.getParts();
                String[][] data = new String[customers.keySet().size()][];

                int i = 0;
                for (var customer : customers.keySet()) {
                    data[i] = customer.toStringParts();
                    ++i;
                }

                new ClientTableView(columns, data);
            }
            default -> throw new LayerException("Unknown table type.");
        }
    }

    public void setCustomers(Map<CnpParts, List<BigDecimal>> mapOfCustomers) {
        model.setCustomers(mapOfCustomers);
    }

    public void setMetrices(JSONObject metrices) {
        model.setMetrices(metrices);
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

        SwingWorker<Boolean, Object> longProcess = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    new Client(ClientController.this).requestProcess(input.getAbsolutePath(), output.getAbsolutePath());
                    ClientController.this.setCurrentView(view.getSecondaryView());
                    return true;
                } catch (ClientException e) {
                    ClientMainView.showErrorMessage(e.getMessage());
                }

                return false;
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
