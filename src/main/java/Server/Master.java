package Server;

import Handler.ClientHandler;
import Handler.MasterHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master extends Server{
    private int offset;
    private List<Socket> replicas;

    public Master(int port, String role) {
        super(port, role);
        this.id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
        this.replicas = new ArrayList<Socket>();
    }

    @Override
    public void startThread(ExecutorService threads, Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
        );
        threads.submit(new MasterHandler(clientSocket, this, this.cache));
    }


    public List<Socket> getReplicas() { return replicas; }
}
