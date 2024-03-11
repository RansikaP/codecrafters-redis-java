package Handler;

import Constants.Commands;
import Server.Slave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SlaveHandler extends ClientHandler{
    private Slave server;
    public SlaveHandler(Socket clientSocket, Slave server) {
        super(clientSocket);
        this.server = server;
    }

    @Override
    public void run() {
        //Reading input
        String command;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.getClientSocket().getInputStream())
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
                            this.ping();
                            break;
                        case Commands.ECHO:
                            echo(commands);
                            break;
                        case Commands.SET:
                            set(commands, this.getCache());
                            break;
                        case Commands.GET:
                            get(commands, this.getCache());
                            break;
                        case Commands.INFO:
                            info();
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

        String out = String.format("$%d\r\n%s\r\n", builder.length(), builder.toString());

        this.getClientSocket().getOutputStream().write(out.getBytes());
        this.getClientSocket().getOutputStream().flush();
    }
}
