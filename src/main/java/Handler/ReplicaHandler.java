package Handler;

import Commands.Constants;
import Server.Replica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplicaHandler extends ClientHandler implements Runnable{
    private final Replica server;
    public ReplicaHandler(Socket clientSocket, Replica server, HashMap<String, String> cache) {
        super(clientSocket, cache);
        this.server = server;
    }

    @Override
    public void run() {
        //Reading input
        String command;
        try {
            System.out.println("inside handler");
            System.out.println("Souck: " + this.getClientSocket().toString() + "\n running on this thread: " + Thread.activeCount());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.getClientSocket().getInputStream())
            );
            System.out.println("inside handler reader ready?: " + reader.ready());
            while ((command = reader.readLine()) != null) {
                System.out.println("inside handler reader ready? 2: " + reader.ready());
                if (command.startsWith("*")) {
                    int cmdLength = Integer.parseInt(command.substring(1));
                    List<String> commands = new ArrayList<>(cmdLength * 2);
                    for (int i = 0; i < cmdLength * 2; i++) {
                        commands.add(reader.readLine());
                    }
                    System.out.println(commands.get(1) + " thread #: " + Thread.currentThread().getName());

                    switch (commands.get(1).toLowerCase()) {
                        case Constants.ping:
                            this.ping();
                            break;
                        case Constants.echo:
                            echo(commands);
                            break;
                        case Constants.set:
                            set(commands, this.getCache());
                            break;
                        case Constants.get:
                            get(commands, this.getCache());
                            break;
                        case Constants.info:
                            info();
                            break;
                        case Constants.replconf:
                            replconf(commands);
                        default:
                            System.out.println("invalid command");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("error in handler");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void replconf(List<String> commands) throws IOException {
        if (commands.get(3).equalsIgnoreCase("getack")) {
            String out = String.format("*3\\r\\n$8\\r\\nREPLCONF\\r\\n$3\\r\\nACK\\r\\n$1\\r\\n%d\\r\\n", this.server.getOffset());
            this.getClientSocket().getOutputStream().write(out.getBytes());
            this.getClientSocket().getOutputStream().flush();
        }
    }

    private void info() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("# Replication\nrole:%s", this.server.getRole()));

        String out = String.format("$%d\r\n%s\r\n", builder.length(), builder.toString());

        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().flush();
    }
}
