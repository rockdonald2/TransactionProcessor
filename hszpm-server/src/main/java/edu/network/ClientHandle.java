package edu.network;

import edu.cnp.parts.CnpParts;
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
		// TODO: atalakit exception eseten

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while creating in/out streams");

			return; // TODO: exception
		}

		FileInputStream paymentsInputStream = null;
		try {

			paymentsInputStream = new FileInputStream(in.readLine());
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while finding input tranzactions");

			return;
		}

		FileOutputStream paymentsOutputStream = null;
		try {
			paymentsOutputStream = new FileOutputStream(in.readLine());
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while finding output file");

			return;
		}

		Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers = null;
		try {
			PayProcessorFactory factory = new PayProcessorFactory();
			PayProcessor processor = new PayProcessorFactory().getProcessor(PropertyProvider.getProperty("processor.type"));
			mapOfCustomers = processor.process(paymentsInputStream, paymentsOutputStream);
		} catch (ProcessFailureException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while processing payments");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());

			return;
		}

		ObjectOutputStream outClient = null;
		try {
			outClient = new ObjectOutputStream(client.getOutputStream());
			outClient.writeObject(mapOfCustomers);
			outClient.flush();
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while serializing map of customers");
		}

		try {
			in.close();
			outClient.close();
			client.close();
		} catch (IOException | NullPointerException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while closing socket");
		}
	}

}
