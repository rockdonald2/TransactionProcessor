package edu.client.gui;

import edu.client.exception.LayerException;
import edu.client.gui.utils.LoaderPanel;
import edu.client.utils.ConfigProvider;
import edu.client.utils.exception.ConfigProviderException;
import edu.cnp.parts.CnpParts;
import edu.network.exception.ClientException;
import edu.client.exception.RequestProcessFailureException;
import edu.client.network.Client;
import edu.utils.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ClientController {

    public static final int WIN_SIZE = 700;

    private final ClientMainView view;
    private final ClientModel model;

    public ClientController() {
        this.model = new ClientModel();
        loadConfig();
        this.view = new ClientMainView(this);
    }

    private void loadConfig() {
        try {
            ConfigProvider.load();
        } catch (ConfigProviderException ignored) {
            ClientMainView.showErrorMessage("Failed to load application configuration, reverting back to default.");
        }
    }

    private void updateBtnLabel(JButton btn, File selected) {
        if (selected != null && selected.exists()) {
            view.updateBtnLabel(btn, String.format("Selected %s", selected.getName()));
        } else {
            view.updateBtnLabel(btn, "Select file");
        }
    }

    public void updateInputFormat(String path) {
        ConfigProvider.setProperty("input.format", path, true);
    }

    public void updateInputPath(JButton btn, JMenuItem secondaryBtn) {
        File selected = view.showFileChooser();
        updateBtnLabel(btn, selected);
        model.setInput(selected);
    }

    public void updateOutputFormat(String path) {
        ConfigProvider.setProperty("output.format", path, true);
    }

    public void updateOutputPath(JButton btn, JMenuItem secondaryBtn) {
        File selected = view.showFileChooser();
        updateBtnLabel(btn, selected);
        model.setOutput(selected);
    }

    public void updateMetricsType(String type) {
        ConfigProvider.setProperty("processor.type", type, true);
    }

    public void setCurrentView(JPanel newView) {
        SwingUtilities.invokeLater(() -> {
            view.setContentPane(newView);
            view.repaint();
            view.revalidate();
        });
    }

    public void setCustomers(Map<CnpParts, List<BigDecimal>> mapOfCustomers) {
        model.setCustomers(mapOfCustomers);
    }

    public void setMetrices(JSONObject metrices) {
        model.setMetrices(metrices);
    }

    public String[] getCustomerCnps() {
        var customers = model.getCustomers();

        if (customers != null) return customers.keySet().stream().map(CnpParts::cnp).toList().toArray(new String[] {});

        return new String[] {};
    }

    public void createTableOf(String of) {
        switch (of) {
            case "metrices" -> {
                var rawData = model.getMetrices();
                var rawDataColumns = rawData.keySet();
                rawDataColumns.remove("errors");
                String[] columns = rawDataColumns.toArray(new String[]{});
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
            case "transactions" -> {
                var cnp = view.getCurrentlySelectedCnp();
                var customers = model.getCustomers();
                var cnpParts = customers.keySet().stream().filter(elem -> elem.cnp().equals(cnp)).findFirst().orElse(null);

                if (cnpParts != null) {
                    var transactions = customers.get(cnpParts);

                    String[] columns = {"Transactions"};
                    String[][] data = new String[transactions.size()][];

                    for (int i = 0; i < transactions.size(); ++i) {
                        data[i] = new String[]{ transactions.get(i).toString() };
                    }

                    new ClientTableView(columns, data);
                } else {
                    ClientMainView.showErrorMessage("Unable to find transactions for this CNP.");
                }
            }
            default -> throw new LayerException("Unknown table type.");
        }
    }

    public void createChartOf(String selectedItem) {
        SwingUtilities.invokeLater(() -> {
            var customers = model.getCustomers();

            Set<String> groups = new HashSet<>();
            customers.keySet().forEach(customer -> {
                try {
                    groups.add(CnpParts.class.getMethod(
                                    StringUtils.replace(CaseUtils.toCamelCase(selectedItem, false), " ", ""))
                            .invoke(customer).toString());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                }
            });

            Map<String, List<BigDecimal>> valuesByGroups = new HashMap<>();
            groups.forEach(group -> {
                valuesByGroups.put(group,
                        customers.keySet().stream().filter(customer ->
                        {
                            try {
                                return CnpParts.class.getMethod(
                                                StringUtils.replace(CaseUtils.toCamelCase(selectedItem, false), " ", ""))
                                        .invoke(customer).toString().equals(group);
                            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                                return false;
                            }
                        }).flatMap(customer -> customers.get(customer).stream()).collect(Collectors.toList())
                );
            });

            var dataSet = new DefaultCategoryDataset();
            groups.forEach(group -> {
                dataSet.setValue(valuesByGroups.get(group).stream()
                                .mapToDouble(BigDecimal::doubleValue).average().orElse(0.0)
                        , "Average by group", group);
            });

            var chart = ChartFactory.createBarChart("Average by " + selectedItem, "",
                    "Average by group", dataSet, PlotOrientation.VERTICAL, false, true, false);
            chart.setAntiAlias(true);
            new ClientChartView(chart);
        });
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
                    view.toggleFileMenu();
                    return true;
                } catch (ClientException e) {
                    ClientMainView.showErrorMessage(e.getMessage());
                }

                return false;
            }

            @Override
            protected void done() {
                view.refreshCustomerCnpList();
                LoaderPanel.getInstance().hideLoader();
                SwingUtilities.invokeLater(() -> ClientController.this.setCurrentView(view.getSecondaryView()));
                view.toggleProcessBtn();
            }
        };
        longProcess.execute();
    }

    public static void main(String[] args) {
        new ClientController();
    }
}
