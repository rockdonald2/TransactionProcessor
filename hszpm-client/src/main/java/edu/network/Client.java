package edu.network;

import edu.cnp.parts.CnpParts;
import edu.gui.ClientController;
import edu.gui.ClientView;
import edu.utils.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class Client {

	private String inputPath;
	private String outputPath;
	private ClientController controller;

	public Client(ClientController controller) {
		this.controller = controller;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getInputPath() {
		return inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * Elvégez egy tranzakciócsomag feldolgozást az állapotként tárolt input és output eléréseket felhasználva.
	 * Elküldi a szerver-nek az említett elérési útakat, kimeneti adatként pedig a tranzakciókat kapja.
	 * A tranzakciókat átadja a ClientController instanciának, amely elvégzi a megjelenítés kiadását.
	 */
	public void requestProcess() {
		if (this.inputPath == null || this.outputPath == null) {
			ClientView.showErrorMessage("Client error: input or output paths are not set");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: input or output paths are not set");

			return;
		}

		Socket s = null;
		try {
			s = new Socket("localhost", 11111);
		} catch (IOException e) {
			ClientView.showErrorMessage("Client error: error while creating socket");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating socket");

			return;
		}

		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			ClientView.showErrorMessage("Client error: error while creating in/out streams");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating in/out streams");

			return;
		}

		out.println(inputPath);
		out.println(outputPath);
		out.flush();

		ObjectInputStream inClient = null;
		try {
			inClient = new ObjectInputStream(s.getInputStream());
			Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers = (Map<CnpParts, ArrayList<BigDecimal>>) inClient.readObject();

			ClientView.showInformationMessage("Payments successfully processed");
			Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Payments successfully processed");

		} catch (IOException | ClassNotFoundException e) {
			ClientView.showErrorMessage("Client error: error while recreating map of customers");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while recreating map of customers");
		}

		try {
			out.close();
			inClient.close();
			s.close();
		} catch (IOException e) {
			ClientView.showErrorMessage("Client error: error while reading server answer");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while reading server answer");
		}
	}

}
