package edu.gui;

import edu.cnp.parts.CnpParts;
import edu.network.Client;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class ClientController {

	private final ClientModel model;
	private final ClientView view;

	/**
	 * Létrehozza a megfelelő Model-t és View-t, és megjeleníti a felhasználónak a View-t.
	 */
	public ClientController() {
		this.model = new ClientModel();
		this.view = new ClientView();

		this.view.setController(this);
		this.view.setBounds(100, 100, 400, 400);
		this.view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.view.setVisible(true);
	}

	/**
	 * Beállítja a bemeneti állomány elérési útvonalát a View-tól kapott File-al.
	 * @param file
	 * 						bemeneti .csv állomány
	 */
	public void setInputPath(File file) {
		if (file == null) {
			ClientView.showErrorMessage("Usage error: no file selected");
			setInputButtonColor(Color.RED);

			return;
		}

		if (!file.getName().contains(".csv")) {
			ClientView.showErrorMessage("Usage error: Wrong file extension");
			model.setWrongCsvInput(true);
			setInputButtonColor(Color.RED);
		} else {
			model.setCsvInputPath(file.getAbsolutePath());
			model.setWrongCsvInput(false);
			setInputButtonColor(Color.GREEN);

			setRequestProcessActive(!model.isWrongJsonInput());
		}
	}

	/**
	 * Beállítja a kimeneti állomány elérési útvonalát a View-tól kapott File-al.
	 * @param file
	 * 						kimeneti .json állomány
	 */
	public void setOutputPath(File file) {
		if (file == null) {
			ClientView.showErrorMessage("Usage error: no file selected");
			setOutputButtonColor(Color.RED);

			return;
		}

		if (!file.getName().contains(".json")) {
			ClientView.showErrorMessage("Usage error: Wrong file extension");
			model.setWrongJsonInput(true);
			setOutputButtonColor(Color.RED);
		} else {
			model.setWrongJsonInput(false);
			model.setJsonOutputPath(file.getAbsolutePath());
			setOutputButtonColor(Color.GREEN);

			setRequestProcessActive(!model.isWrongCsvInput());
		}
	}

	public void setInputButtonColor(Color color) {
		view.getShowInputFileChooser().setBackground(color);
	}

	public void setOutputButtonColor(Color color) {
		view.getShowOutputFileChooser().setBackground(color);
	}

	/**
	 * Létrehoz egy új kliens-t, amely elvégzi a bemeneti és kimeneti állományokkal a megfelelő tranzakciócsomag feldolgozást.
	 */
	public void requestProcess() {
		if (model.validInputs()) {
			Client client = new Client(this);
			client.setInputPath(model.getCsvInputPath());
			client.setOutputPath(model.getJsonOutputPath());
			client.requestProcess();
		} else {
			if (model.isWrongCsvInput()) {
				setInputButtonColor(Color.RED);
			} else if (model.isWrongJsonInput()) {
				setOutputButtonColor(Color.RED);
			}

			view.getRequestProcess().setEnabled(false);
		}
	}

	/**
	 * Megjeleníti, felhasználva a View-t, az adott kliens adatait egy felugró ablak formájában.
	 * @param client
	 * 							megjelenítendő kliens
	 */
	public void requestShowData(CnpParts client) {
		if (client == null) {
			ClientView.showInformationMessage("Please select a CNP first");

			return;
		}

		view.showClientData(client, model.getClientPayments(client));
	}

	/**
	 * Megjeleníti a feldolgozási eredményt, a kimeneti .json állományt felhasználva.
	 */
	public void requestShowMetrices() {
		InputStream is = null;
		try {
			is = new FileInputStream(model.getJsonOutputPath());
		} catch (FileNotFoundException fileNotFoundException) {
			ClientView.showErrorMessage("Usage error: .json output file not found");

			return;
		}

		view.showMetricesData(new JSONObject(new JSONTokener(is)));
	}

	/**
	 * A kliens-től visszakapja a tranzakciókat, amit átad a Model-nek.
	 * @param mapOfCustomers
	 * 											tranzakciók
	 */
	public void receiveMapOfCustomers(Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		model.setMapOfCustomers(mapOfCustomers);
		populateList();
	}

	/**
	 * Feltölti a View CNP listáját a megfelelőkkel, majd kéri a View frissítését.
	 */
	private void populateList() {
		view.getCnpList().setListData(model.getMapOfCustomers().keySet().toArray());
		setShowDataActive(true);
		setShowMetricesActive(true);
		updateView();
	}

	public void setRequestProcessActive(boolean flag) {
		view.getRequestProcess().setEnabled(flag);
	}

	public void setShowDataActive(boolean flag) {
		view.getShowData().setEnabled(flag);
	}

	public void setShowMetricesActive(boolean flag) {
		view.getShowMetrices().setEnabled(flag);
	}

	public void updateView() {
		view.setVisible(true);
	}

	public static void main(String[] args) {
		ClientController controller = new ClientController();
	}

}
