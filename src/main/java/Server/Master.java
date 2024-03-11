package Server;

import Handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master extends Server{
    protected String id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    protected int offset;

    public Master(int port, String role) {
        super(port, role);
        this.id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
        this.offset = 0;
    }

    @Override
    public void start() {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ExecutorService threads = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            // Wait for connection from clients.
            while (true) {
                clientSocket = serverSocket.accept();
                threads.submit(new ClientHandler(clientSocket, this));
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

    public String getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }
}
