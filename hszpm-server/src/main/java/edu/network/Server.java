package edu.network;

import edu.network.exceptions.SocketFailureException;
import edu.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	public static void main(String[] args) {
		try {
			var ss = new ServerSocket(11111);

			while (true) {
				(new ClientHandle(ss.accept())).start();
				// TODO: elérhető kellene legyen egy parancs, amivel lehetséges a szerver leállítása a folyamat megölése helyett
				// TODO: korlátozzuk a párhuzamosan kiszolgálható kliensek számát
			}
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.CRITICAL, "Error while creating ServerSocket");
			throw new SocketFailureException("Error while creating ServerSocket");
		}
	}

}
