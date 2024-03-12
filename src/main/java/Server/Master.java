package Server;

import Handler.ClientHandler;
import Handler.MasterHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master extends Server{
    private String id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    private int offset;
    private List<Integer> replicas;

    public Master(int port, String role) {
        super(port, role);
        this.id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
        this.offset = 0;
        this.replicas = new ArrayList<Integer>();
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
                threads.submit(new MasterHandler(clientSocket, this));
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

    public List<Integer> getReplicas() { return replicas; }
}
