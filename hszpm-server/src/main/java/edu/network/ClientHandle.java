package edu.network;

import edu.cnp.CnpParts;
import edu.pay.processor.PayProcessor;
import edu.utils.Logger;

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
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while creating in/out streams");

			return;
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
			mapOfCustomers = null; //PayProcessor.getProcessor().process(paymentsInputStream, paymentsOutputStream);
			throw new IOException();
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Server error: error while processing payments");

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
