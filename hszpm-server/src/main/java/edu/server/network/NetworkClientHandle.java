package edu.server.network;

import edu.cnp.parts.CnpParts;
import edu.network.FileTransfer;
import edu.server.ClientHandle;
import edu.network.exception.ServerException;
import edu.server.exception.SocketFailureException;
import edu.pay.exception.general.GeneralException;
import edu.pay.processor.PayProcessor;
import edu.pay.processor.PayProcessorFactory;
import edu.utils.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class NetworkClientHandle extends Thread implements ClientHandle {

  private final Socket client;

  public NetworkClientHandle(Socket client) {
    this.client = client;
  }

  /**
   * Kezeli a kliens kérését. Megkapja a kimeneti és bemeneti állományok elérési útvonalát.
   * Majd kérve egy feldolgozó egységet, elvégzi a feldolgozást, az eredményként kapott tranzakciókat visszaküldi az adatfolyamon.
   */
  @Override
  public void run() {
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    try {
      try {
        in = new ObjectInputStream(client.getInputStream());
      } catch (IOException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while creating in/out streams");
        throw new SocketFailureException("Error while creating in/out.");
      }

      FileTransfer inFt = null;
      try {
        inFt = (FileTransfer) in.readObject();
      } catch (Exception e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        throw new SocketFailureException("Error while recreating input data.");
      }

      Properties config = inFt.getProperties();

      if (Objects.isNull(config)) {
        throw new SocketFailureException("Error because of missing config.");
      }

      InputStream paymentsInputStream = new ByteArrayInputStream(inFt.getData());
      try {
        out = new ObjectOutputStream(client.getOutputStream());
      } catch (IOException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        throw new SocketFailureException("Error while accessing client's output stream.");
      }

      Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers = null;
      try {
        PayProcessorFactory factory = new PayProcessorFactory();
        PayProcessor processor = new PayProcessorFactory().getProcessor(config.getProperty("processor.type"));
        mapOfCustomers = processor.process(paymentsInputStream, config.getProperty("input.format"), out, config.getProperty("output.format"));
      } catch (GeneralException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while processing payments");
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        try {
          out.writeObject(e);
        } catch (IOException ex) {
          Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to write exception to client.");
        }
        throw new SocketFailureException("Error while processing payments.");
      }

      try {
        out.writeObject(mapOfCustomers);
        out.flush();
      } catch (IOException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while serializing map of customers.");
        throw new SocketFailureException("Error while serializing map of customers.");
      }

      Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Successfully processed client service.");
    } catch (ServerException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
      try {
        if (out != null) {
          out.writeObject(e);
        }
      } catch (IOException ex) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to write exception to client.");
      }
    } finally {
      try {
        if (in != null) {
          in.close();
        }

        if (out != null) {
          out.close();
        }

        client.close();
        NetworkClientHandlePool pool = NetworkClientHandlePool.getInstance();
        pool.decrementClients();
      } catch (IOException | NullPointerException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while closing socket");
        throw new SocketFailureException("error while closing communication socket.");
      } catch (ServerException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        throw e;
      }
    }
  }

}
