package edu.gui;

import javax.swing.*;

public class ClientController {

	public static final int WIN_SIZE = 650;

	private final ClientView view;

	/**
	 * Létrehozza a megfelelő Model-t és View-t, és megjeleníti a felhasználónak a View-t.
	 */
	public ClientController() {
		this.view = new ClientView(this);

		this.view.setSize(WIN_SIZE, WIN_SIZE);
		this.view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.view.setVisible(true);
		this.view.setLocationRelativeTo(null);
		this.view.pack();
	}

	public static void main(String[] args) {
		ClientController controller = new ClientController();
	}

}
