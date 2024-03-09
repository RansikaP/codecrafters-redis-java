import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable{

    private Socket clientSocket;
    private HashMap<String, String> cache;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.cache = new HashMap<>();
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
                    for (int i = 0; i < cmdLength * 2; i++) {
                        commands.add(reader.readLine());
                        System.out.println(commands.getLast());
                    }

                    switch (commands.get(1).toLowerCase()) {
                        case Constants.PING:
                            this.ping();
                            break;
                        case Constants.ECHO:
                            echo(commands);
                            break;
                        case Constants.SET:
                            set(commands, cache);
                            break;
                        case Constants.GET:
                            get(commands, cache);
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

    private void ping() throws IOException {
        clientSocket.getOutputStream().write(Constants.PONG.getBytes());
        clientSocket.getOutputStream().flush();
    }

    private void echo(List<String> commands) throws IOException {
        String out = commands.get(2) + "\r\n" + commands.getLast() +"\r\n";
        clientSocket.getOutputStream().write(out.getBytes());
        clientSocket.getOutputStream().flush();
    }

    private void set(List<String> commands, HashMap<String, String> cache) throws IOException {
        cache.put(commands.get(3), commands.get(5));
        clientSocket.getOutputStream().write(Constants.OK.getBytes());
        clientSocket.getOutputStream().flush();
        if (commands.size() > 6 && commands.get(7).equalsIgnoreCase(Constants.PX)) {
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

    private void get(List<String> commands, HashMap<String, String> cache) throws IOException {
        String value = cache.get(commands.get(3));
        if (value.isEmpty()) {
            clientSocket.getOutputStream().write(Constants.NULL_BULK_STRING.getBytes());
            clientSocket.getOutputStream().flush();
        } else {
            String out = "$" + value.length() + "\r\n" + value + "\r\n";
            clientSocket.getOutputStream().write(out.getBytes());
            clientSocket.getOutputStream().flush();
        }

    }


}