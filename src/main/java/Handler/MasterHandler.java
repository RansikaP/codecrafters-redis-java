package Handler;

import Commands.CommandConstructor;
import Commands.Constants;
import Server.Master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterHandler extends ClientHandler {
    private Master server;

    public MasterHandler(Socket clientSocket, Master server) {
        super(clientSocket);
        this.server = server;
    }

    @Override
    public void run() {
        //Reading input
        String command;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClientSocket().getInputStream())
            );

            while ((command = reader.readLine()) != null) {
                if (command.startsWith("*")) {
                    int cmdLength = Integer.parseInt(command.substring(1));
                    List<String> commands = new ArrayList<>(cmdLength * 2);
                    for (int i = 0; i < cmdLength * 2; i++) {
                        commands.add(reader.readLine());
                    }

                    switch (commands.get(1).toLowerCase()) {
                        case Constants.ping:
                            ping();
                            break;
                        case Constants.echo:
                            echo(commands);
                            break;
                        case Constants.set:
                            set(commands, this.getCache());
                            syncReplicas(commands);
                            break;
                        case Constants.get:
                            get(commands, this.getCache());
                            break;
                        case Constants.info:
                            info();
                            break;
                        case Constants.replconf:
                            replconf(commands);
                            break;
                        case Constants.psync:
                            psync();
                            break;
                        default:
                            System.out.println("invalid command");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void info() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("# Replication\nrole:%s", this.server.getRole()));
        builder.append(String.format("\nmaster_replid:%s\nmaster_repl_offset:%s", this.server.getId(), this.server.getOffset()));

        String out = String.format("$%d\r\n%s\r\n", builder.length(), builder.toString());

        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().flush();
    }

    private void replconf(List<String> commands) throws IOException {
        if (commands.get(3).equalsIgnoreCase("listening-port"))
            this.server.getReplicas().add(Integer.parseInt(commands.get(5)));
        this.getClientSocket().getOutputStream().write(Constants.OK.getBytes());
        this.getClientSocket().getOutputStream().flush();
    }

    private void psync() throws IOException {
        String out = String.format("+%s %s %d\r\n", Constants.FULLRESYNC, this.server.getId(), this.server.getOffset());
        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().flush();
        out = String.format("$%d\r\n", Constants.EMPTY_RDB.length);
        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().write(Constants.EMPTY_RDB);
        this.getClientSocket().getOutputStream().flush();
    }

    private void syncReplicas(List<String> commands) throws IOException {
        for(int replica: this.server.getReplicas()) {
            String out = CommandConstructor.getCommand(commands.get(1), commands);
            System.out.println("Sending to replica: " + out);
            OutputStream repOut = new Socket("localhost", replica).getOutputStream();
            repOut.write(out.getBytes());
            repOut.flush();
        }
    }
}
