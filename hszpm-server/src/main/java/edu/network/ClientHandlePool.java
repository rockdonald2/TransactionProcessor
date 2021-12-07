package edu.network;

import edu.network.exceptions.SocketFailureException;
import edu.utils.Logger;
import edu.utils.PropertyProvider;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandlePool {

  private static ClientHandlePool instance;
  private static final int POOL_SIZE = Integer.parseInt(PropertyProvider.getProperty("pool.size"));
  private final AtomicInteger current;

  private ClientHandlePool() {
    current = new AtomicInteger(0);
  }

  public static synchronized ClientHandlePool getInstance() {
    if (instance == null) {
      instance = new ClientHandlePool();
    }

    return instance;
  }

  public synchronized void incrementClients() {
    current.incrementAndGet();
  }

  public synchronized void decrementClients() {
    current.decrementAndGet();
    notifyAll();
  }

  public synchronized boolean canHandleNewClient() {
    return current.get() != POOL_SIZE;
  }

  public synchronized void handleClient(Socket client) {
    while (!canHandleNewClient()) {
      try {
        wait();
      } catch (InterruptedException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Client thread interrupted.");
        throw new SocketFailureException("Client thread interrupted.");
      }
    }

    incrementClients();
    (new ClientHandle(client)).start();
  }

}
