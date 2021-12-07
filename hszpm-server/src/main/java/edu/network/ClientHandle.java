package edu.network;

import edu.cnp.parts.CnpParts;
import edu.network.exceptions.ServerException;
import edu.network.exceptions.SocketFailureException;
import edu.pay.exception.general.processor.ProcessFailureException;
import edu.pay.processor.PayProcessor;
import edu.pay.processor.PayProcessorFactory;
import edu.utils.Logger;
import edu.utils.PropertyProvider;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ClientHandle extends Thread {

	private final Socket client;

	public ClientHandle(Socket client) {
		this.client = client;
	}

	/**
	 * Kezeli a kliens kérését. Megkapja a kimeneti és bemeneti állományok elérési útvonalát.
	 * Majd kérve egy feldolgozó egységet, elvégzi a feldolgozást, az eredményként kapott tranzakciókat visszaküldi az adatfolyamon.
	 */
	@Override
	public void run() {
		BufferedReader in = null;
		ObjectOutputStream outClient = null;
		try {
			try {
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while creating in/out streams");
				throw new SocketFailureException("Error while creating in/out.");
			}

			FileInputStream paymentsInputStream = null;
			try {
				paymentsInputStream = new FileInputStream(in.readLine());
			} catch (IOException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while finding input tranzactions");
				throw new SocketFailureException("Error while finding input tranzactions.");
			}

			FileOutputStream paymentsOutputStream = null;
			try {
				paymentsOutputStream = new FileOutputStream(in.readLine());
			} catch (IOException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while finding output file");
				throw new SocketFailureException("Error while finding output file.");
			}

			Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers = null;
			try {
				PayProcessorFactory factory = new PayProcessorFactory();
				PayProcessor processor = new PayProcessorFactory().getProcessor(PropertyProvider.getProperty("processor.type"));
				mapOfCustomers = processor.process(paymentsInputStream, paymentsOutputStream);
			} catch (ProcessFailureException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while processing payments");
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
				throw new SocketFailureException("Error while processing payments.");
			}

			try {
				outClient = new ObjectOutputStream(client.getOutputStream());
				outClient.writeObject(mapOfCustomers);
				outClient.flush();
			} catch (IOException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while serializing map of customers.");
				throw new SocketFailureException("Error while serializing map of customers.");
			}
		} catch (ServerException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
			throw e;
		} finally {
			try {
				assert in != null;
				in.close();
				assert outClient != null;
				outClient.close();
				client.close();

				ClientHandlePool pool = ClientHandlePool.getInstance();
				pool.decrementClients();
			} catch (IOException | NullPointerException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while closing socket");
			}
		}
	}

}
