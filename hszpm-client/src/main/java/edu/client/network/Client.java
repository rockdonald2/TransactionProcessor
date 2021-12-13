package edu.client.network;

import edu.client.exception.RequestProcessFailureException;
import edu.cnp.parts.CnpParts;
import edu.client.exception.LayerException;
import edu.client.gui.ClientController;
import edu.network.FileTransfer;
import edu.utils.Logger;
import edu.utils.PropertyProvider;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
			s = new Socket("localhost", Integer.parseInt(PropertyProvider.getServerProperty("port")));
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating socket");
			throw new LayerException("Error while creating communication socket with server.");
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(s.getOutputStream());
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while creating in/out streams");
			throw new LayerException("Error while creating communication streams.");
		}

		File input = new File(inputPath);
		File output = new File(outputPath);

		if (!input.exists()) {
			throw new LayerException("Non-existing input file.");
		}

		if (!input.getName().contains(PropertyProvider.getClientProperty("input.format"))) {
			throw new LayerException("Incompatible type of input file with currently set input format.");
		}

		if (!output.exists()) {
			throw new LayerException("Non-existing output file.");
		}

		if (!output.canWrite()) {
			throw new LayerException("Cannot write to output file.");
		}

		if (!output.getName().contains(PropertyProvider.getClientProperty("output.format"))) {
			throw new LayerException("Incompatible type of input file with currently set input format.");
		}

		byte[] inputByteContent = null;
		try {
			inputByteContent = Files.readAllBytes(Path.of(input.getPath()));
		} catch (IOException e) {
			throw new LayerException("Error while opening input file.");
		}

		FileTransfer inFt = new FileTransfer(inputByteContent);
		try {
			out.writeObject(inFt);
			out.flush();
		} catch (IOException e) {
			throw new LayerException("Error while sending data to server.");
		}

		ObjectInputStream in;
		try {
			in = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			throw new LayerException("Error while reading server answer.");
		}

		FileTransfer outFt = null;
		try {
		 	outFt = (FileTransfer) in.readObject();
		} catch (Exception e) {
			throw new LayerException("Error while recreating output file.");
		}

		try {
			// ? Alapvetően két dolgot fog visszaküldeni a szerver, először az output fájl tartalmát, majd a map-ünket
			Files.write(Path.of(output.getPath()), outFt.getData());
		} catch (IOException e) {
			throw new LayerException("Error while writing to output file.");
		}

		try {
			Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers = (Map<CnpParts, ArrayList<BigDecimal>>) in.readObject();
			Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Payments successfully processed");
		} catch (Exception e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while recreating map of customers");
			throw new LayerException("Error while recreating map of customers.");
		}

		try {
			out.close();
			in.close();
			s.close();
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Client error: error while reading server answer");
			throw new LayerException("Error while reading server result.");
		}
	}

}
