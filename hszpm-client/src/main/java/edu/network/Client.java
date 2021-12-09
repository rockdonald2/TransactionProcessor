package edu.network;

import edu.cnp.parts.CnpParts;
import edu.exception.LayerException;
import edu.gui.ClientController;
import edu.gui.ClientView;
import edu.utils.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

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
		if (inputPath == null || outputPath == null) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: input or output paths are not set");
			throw new LayerException("Input or output file not specified");
		}

		Socket s;
		try {
			s = new Socket("localhost", 11111);
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating socket");
			throw new LayerException("Error while creating communication socket with server.");
		}

		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating in/out streams");
			throw new LayerException("Error while creating communication streams.");
		}

		out.println(inputPath);
		out.println(outputPath);
		out.flush();

		ObjectInputStream inClient;
		try {
			inClient = new ObjectInputStream(s.getInputStream());
			Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers = (Map<CnpParts, ArrayList<BigDecimal>>) inClient.readObject();
			Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Payments successfully processed");
		} catch (Exception e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while recreating map of customers");
			throw new LayerException("Error while recreating map of customers.");
		}

		try {
			out.close();
			inClient.close();
			s.close();
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while reading server answer");
			throw new LayerException("Error while reading server result.");
		}
	}

}
