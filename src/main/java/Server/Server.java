package Server;

import Handler.ClientHandler;
import Handler.MasterHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {
    protected final int port;
    protected final String role;
    protected String id;
    protected int offset;
    protected HashMap<String, String> cache;

    public Server(int port, String role) {
        this.port = port;
        this.role = role;
        this.offset = 0;
        this.id = "";
        this.cache = new HashMap<String, String>();
    }

    public void start() {
        listen();
    }

    public void listen() {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ExecutorService threads = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            // Wait for connection from clients.
            System.out.println("starting handler thread");
            while (true) {
                clientSocket = serverSocket.accept();
                startThread(threads, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    public abstract void startThread(ExecutorService threads, Socket clientSocket);

    public int getPort() {
        return this.port;
    }

    public String getId() { return this.id; }

    public String getRole() {
        return this.role;
    }

    public int getOffset() { return this.offset; }

    public HashMap<String, String> getCache() { return cache; }
}
