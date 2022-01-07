package edu.server.network;

import edu.server.Server;
import edu.server.exception.SocketFailureException;
import edu.utils.Logger;
import edu.utils.PropertyProvider;

import java.io.IOException;
import java.net.ServerSocket;

public class NetworkServer implements Server {

	private final NetworkClientHandlePool handlePool;
	private boolean _on;

	private static NetworkServer server;

	private NetworkServer() {
		handlePool = NetworkClientHandlePool.getInstance();
		_on = false;
	}

	public static synchronized NetworkServer getInstance() {
		if (server == null) {
			server = new NetworkServer();
		}

		return server;
	}

	public synchronized void startServer() {
		_on = true;

		try {
			var ss = new ServerSocket((Integer.parseInt(PropertyProvider.getProperty("port"))));

			while (_on) {
				handlePool.handleClient(ss.accept());

				wait(50);
			}
		} catch (InterruptedException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.CRITICAL, "Error while checking status.");
			throw new SocketFailureException("Error while checking status.");
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.CRITICAL, "Error while creating ServerSocket");
			throw new SocketFailureException("Error while creating ServerSocket.");
		} finally {
			_on = false;
		}
	}

	public synchronized void stopServer() {
		_on = false;
	}

}
