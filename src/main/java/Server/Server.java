package Server;

import Handler.ClientHandler;

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

    public abstract void start();

    public int getPort() {
        return port;
    }

    public String getRole() {
        return role;
    }

    public int getOffset() { return this.offset; }

    public HashMap<String, String> getCache() { return cache; }
}
