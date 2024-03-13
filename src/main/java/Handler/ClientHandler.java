package Handler;

import Commands.Constants;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ClientHandler implements Runnable{
    private Socket clientSocket;
    private HashMap<String, String> cache;

    public ClientHandler(Socket clientSocket, HashMap<String, String> cache) {
        this.clientSocket = clientSocket;
        this.cache = cache;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public HashMap<String, String> getCache() {
        return cache;
    }

    void ping() throws IOException {
        clientSocket.getOutputStream().write(Constants.PONG.getBytes());
        clientSocket.getOutputStream().flush();

    }

    void echo(List<String> commands) throws IOException {
        String out = commands.get(2) + "\r\n" + commands.getLast() +"\r\n";
        clientSocket.getOutputStream().write(out.getBytes());
        clientSocket.getOutputStream().flush();

    }

    void set(List<String> commands, HashMap<String, String> cache) throws IOException {
        cache.put(commands.get(3), commands.get(5));
        System.out.println("slave set after");
        clientSocket.getOutputStream().write(Constants.OK.getBytes());
        clientSocket.getOutputStream().flush();
        if (commands.size() > 6 && commands.get(7).equalsIgnoreCase(Constants.px)) {
            String key = commands.get(3);
            long time = Long.parseLong(commands.get(9));
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                try{
                    cache.remove(key);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }, time, TimeUnit.MILLISECONDS);
        }

    }

    void get(List<String> commands, HashMap<String, String> cache) throws IOException {
        String value = cache.get(commands.get(3));
        System.out.println(value);
        if (value != null && !value.isBlank()) {
            String out = "$" + value.length() + "\r\n" + value + "\r\n";
            clientSocket.getOutputStream().write(out.getBytes());
            clientSocket.getOutputStream().flush();
        } else {
            clientSocket.getOutputStream().write(Constants.NULL_BULK_STRING.getBytes());
            clientSocket.getOutputStream().flush();
        }

    }

}
