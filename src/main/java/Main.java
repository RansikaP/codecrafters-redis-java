import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port;
        Server Redis = null;

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("--port")) {
                try {
                    port = Integer.parseInt(args[1]);
                    System.out.println("Port: " + port);
                    Redis = new Server(port);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            Redis = new Server();
        }

        Redis.start();
    }


}
