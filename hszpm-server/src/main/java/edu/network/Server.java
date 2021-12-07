package edu.network;

import edu.network.exceptions.SocketFailureException;
import edu.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private static ClientHandlePool handlePool;

	public static void main(String[] args) {
		handlePool = ClientHandlePool.getInstance();

		try {
			var ss = new ServerSocket(11111);

			while (true) {
				handlePool.handleClient(ss.accept());
			}
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.CRITICAL, "Error while creating ServerSocket");
			throw new SocketFailureException("Error while creating ServerSocket");
		}
	}

}

// TODO: elérhető kellene legyen egy parancs, amivel lehetséges a szerver leállítása a folyamat megölése helyett
