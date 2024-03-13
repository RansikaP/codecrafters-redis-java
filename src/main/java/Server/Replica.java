package Server;

import Commands.Constants;
import Handler.ReplicaHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Replica extends Server{
    private String masterHost;
    private int masterPort;

    public Replica(int port, String role, String masterHost, int masterPort) {
        super(port, role);
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    @Override
    public void start() {
        try {
            handshake();
            System.out.println("about to listen");
            listen();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void startThread(ExecutorService threads, Socket clientSocket) {
        threads.submit(new ReplicaHandler(clientSocket, this, this.cache));
    }

    public void handshake() throws IOException, InterruptedException {
        Socket masterSocket = new Socket(masterHost, masterPort);
        masterSocket.getOutputStream().write(Constants.PING.getBytes());
        masterSocket.getOutputStream().flush();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(masterSocket.getInputStream())
        );

        if (!reader.readLine().equalsIgnoreCase("+Pong"))
            throw new IOException();

        String out = String.format("%s$%d\r\n%d\r\n", Constants.REPLCONF_listening_port, String.valueOf(port).length(), port);
        masterSocket.getOutputStream().write(out.getBytes());
        masterSocket.getOutputStream().flush();

        if (!reader.readLine().equalsIgnoreCase("+OK"))
            throw new IOException();

        masterSocket.getOutputStream().write(Constants.REPLCONF_capa_psync2.getBytes());
        masterSocket.getOutputStream().flush();

        if (!reader.readLine().equalsIgnoreCase("+OK"))
            throw new IOException();

        masterSocket.getOutputStream().write(Constants.PSYNC_HANDSHAKE.getBytes());
        masterSocket.getOutputStream().flush();

        String line = reader.readLine();
        if (line.contains("+FULLRESYNC")) {
            this.id = line.substring(12, 52);
            int temp_offset = Integer.parseInt(line.substring(53));
            int fileSize = Integer.parseInt(reader.readLine().substring(1));
            char[] buffer = new char[fileSize];
            int bytesRead = reader.read(buffer, 0, fileSize - 1);
            String rdbFile = new String(buffer, 0, fileSize);
            bytesRead = reader.read(buffer, 0, fileSize - 1);
            rdbFile = new String(buffer, 0, fileSize);
            System.out.println(rdbFile);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new ReplicaHandler(masterSocket, this, this.cache));
            executor.shutdown();
        }
    }
}
