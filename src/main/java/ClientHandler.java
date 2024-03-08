import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientHandler implements Runnable{

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        //Reading input
        String command;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

            while ((command = reader.readLine()) != null) {
                System.out.println("Command: " + command);

                if (command.startsWith("*")) {
                    int cmdLength = Integer.parseInt(command.substring(1));
                    List<String> commands = new ArrayList<>(cmdLength * 2);
                    for (int i = 0; i < cmdLength * 2; i++)
                        commands.add(reader.readLine());

                    switch (commands.get(1).toLowerCase()) {
                        case Constants.PING:
                            clientSocket.getOutputStream().write(Constants.PONG.getBytes());
                            clientSocket.getOutputStream().flush();
                            break;
                        case Constants.ECHO:
                            String out = commands.get(2) + "\r\n" + commands.getLast() +"\r\n";
                            clientSocket.getOutputStream().write(out.getBytes());
                            clientSocket.getOutputStream().flush();
                            break;
                        default:
                            System.out.println("invalid command");

                    }
                }

                if (command.trim().equalsIgnoreCase("ping")) {
                    clientSocket.getOutputStream().write("+PONG\r\n".getBytes());
                    clientSocket.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
