package Handler;

import Constants.Commands;
import Server.Master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
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
                        case Commands.ping:
                            ping();
                            break;
                        case Commands.echo:
                            echo(commands);
                            break;
                        case Commands.set:
                            set(commands, this.getCache());
                            break;
                        case Commands.get:
                            get(commands, this.getCache());
                            break;
                        case Commands.info:
                            info();
                            break;
                        case Commands.replconf:
                            replconf();
                            break;
                        case Commands.psync:
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

    public void replconf() throws IOException {
        this.getClientSocket().getOutputStream().write(Commands.OK.getBytes());
        this.getClientSocket().getOutputStream().flush();
    }

    public void psync() throws IOException {
        String out = String.format("+%s %s %d\r\n", Commands.FULLRESYNC, this.server.getId(), this.server.getOffset());
        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().flush();
        String empty_RDB = new String(Base64.getDecoder().decode(Commands.EMPTY_RDB));
        out = String.format("$%d\r\n%s", empty_RDB.length(), empty_RDB);
        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().flush();
    }
}
