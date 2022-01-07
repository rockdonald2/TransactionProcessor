package edu.client.network;

import edu.utils.ConfigProvider;
import edu.cnp.parts.CnpParts;
import edu.client.exception.LayerException;
import edu.client.gui.ClientController;
import edu.network.FileTransfer;
import edu.utils.Logger;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Client {

    private ClientController controller;

    public Client(ClientController controller) {
        this.controller = controller;
    }

    /**
     * Elvégez egy tranzakciócsomag feldolgozást az állapotként tárolt input és output eléréseket felhasználva.
     * Elküldi a szerver-nek az említett elérési útakat, kimeneti adatként pedig a tranzakciókat kapja.
     * A tranzakciókat átadja a ClientController instanciának, amely elvégzi a megjelenítés kiadását.
     */
    public void requestProcess(String inputPath, String outputPath) {
        Socket s = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            if (inputPath == null || outputPath == null) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: input or output paths are not set");
                throw new LayerException("Input or output file not specified");
            }

            try {
                s = new Socket("localhost", Integer.parseInt(ConfigProvider.getProperty("connection.port")));
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating socket");
                throw new LayerException("Error while creating communication socket with server.");
            }

            try {
                out = new ObjectOutputStream(s.getOutputStream());
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating in/out streams");
                throw new LayerException("Error while creating communication streams.");
            }

            File input = new File(inputPath);
            File output = new File(outputPath);

            if (!input.exists()) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: non-existing input file.");
                throw new LayerException("Non-existing input file.");
            }

            if (!input.getName().contains(ConfigProvider.getProperty("input.format"))) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: incompatible type of input file with currently set input format.");
                throw new LayerException("Incompatible type of input file with currently set input format.");
            }

            if (!output.exists()) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: non-existing output file.");
                throw new LayerException("Non-existing output file.");
            }

            if (!output.canWrite()) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: cannot write to output file.");
                throw new LayerException("Cannot write to output file.");
            }

            if (!output.getName().contains(ConfigProvider.getProperty("output.format"))) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: incompatible type of output file with currently set input format.");
                throw new LayerException("Incompatible type of input file with currently set input format.");
            }

            byte[] inputByteContent = null;
            try {
                inputByteContent = Files.readAllBytes(Path.of(input.getPath()));
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                throw new LayerException("Error while opening input file.");
            }

            FileTransfer inFt = new FileTransfer(inputByteContent);
            Properties config = new Properties();
            config.setProperty("processor.type", ConfigProvider.getProperty("processor.type"));
            config.setProperty("input.format", ConfigProvider.getProperty("input.format"));
            config.setProperty("output.format", ConfigProvider.getProperty("output.format"));
            inFt.setProperties(config);
            try {
                out.writeObject(inFt);
                out.flush();
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                throw new LayerException("Error while sending data to server.");
            }

            try {
                in = new ObjectInputStream(s.getInputStream());
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                throw new LayerException("Error while reading server answer.");
            }

            FileTransfer outFt = null;
            Object answer = null;
            try {
                answer = in.readObject();
            } catch (Exception e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                throw new LayerException("Error while recreating output file.");
            }

            boolean wasException = false;
            try {
                outFt = (FileTransfer) answer;
            } catch (ClassCastException e) {
                wasException = true;
                Exception ex = (Exception) answer;
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, ex.getMessage());
                throw new LayerException(ex.getMessage());
            }

            try {
                // ? Alapvetően két dolgot fog visszaküldeni a szerver, először az output fájl tartalmát, majd a map-ünket,
                // ? de megtörténhet, hogy valamilyen kivétel jön vissza
                Files.write(Path.of(output.getPath()), outFt.getData());
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                throw new LayerException("Error while writing to output file.");
            }

            try {
                Map<CnpParts, List<BigDecimal>> mapOfCustomers = (Map<CnpParts, List<BigDecimal>>) in.readObject();
                controller.setCustomers(mapOfCustomers);
                controller.setMetrices(new JSONObject(new String(Files.readAllBytes(Path.of(output.getPath())))));
                Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Payments successfully processed");
            } catch (Exception e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while recreating map of customers");
                throw new LayerException("Error while recreating map of customers.");
            }
        } catch (LayerException e) {
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

                if (s != null) {
                    s.close();
                }
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while closing communication.");
                throw new LayerException("Error while closing communication.");
            }
        }
    }

}
