package Server;

import Handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {
    protected final int port;
    protected final String role;
    protected String id = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    protected int offset;

    public Server(int port, String role) {
        this.port = port;
        this.role = role;
    }

    public abstract void start();

    public int getPort() {
        return port;
    }

    public String getRole() {
        return role;
    }
}
