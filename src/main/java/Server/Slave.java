package Server;

import Constants.Commands;
import Handler.ClientHandler;
import Handler.SlaveHandler;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Slave extends Server{
    private String masterHost;
    private int masterPort;
    public Slave(int port, String role, String masterHost, int masterPort) {
        super(port, role);
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    @Override
    public void start() {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ExecutorService threads = Executors.newCachedThreadPool();

        try {
            handshake();
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            // Wait for connection from clients.
            while (true) {
                clientSocket = serverSocket.accept();
                threads.submit(new SlaveHandler(clientSocket, this));
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

    public void handshake() throws IOException {
        Socket masterSocket = new Socket(masterHost, masterPort);
        masterSocket.getOutputStream().write(Commands.PING.getBytes());
        masterSocket.getOutputStream().flush();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(masterSocket.getInputStream())
        );

        if (!reader.readLine().equalsIgnoreCase("+Pong"))
            throw new IOException();

        String out = String.format("%s$%d\r\n%d\r\n", Commands.REPLCONF_listening_port, String.valueOf(port).length(), port);
        masterSocket.getOutputStream().write(out.getBytes());
        masterSocket.getOutputStream().flush();

        if (!reader.readLine().equalsIgnoreCase("+OK"))
            throw new IOException();

        masterSocket.getOutputStream().write(Commands.REPLCONF_capa_psync2.getBytes());
        masterSocket.getOutputStream().flush();

        if (!reader.readLine().equalsIgnoreCase("+OK"))
            throw new IOException();

        masterSocket.getOutputStream().write(Commands.PSYNC_HANDSHAKE.getBytes());
        masterSocket.getOutputStream().flush();
    }
}