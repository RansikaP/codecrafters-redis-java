package Handler;

import Commands.CommandConstructor;
import Commands.Constants;
import Server.Master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MasterHandler extends ClientHandler implements Runnable{
    private Master server;
    private int count = 0;

    public MasterHandler(Socket clientSocket, Master server, HashMap<String, String> cache) {
        super(clientSocket, cache);
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
                int cmdByteLength = 1;
                if (command.startsWith("*")) {
                    int cmdLength = Integer.parseInt(command.substring(1));
                    cmdByteLength += String.valueOf(cmdLength).length() + 2;
                    List<String> commands = new ArrayList<>(cmdLength * 2);
                    for (int i = 0; i < cmdLength * 2; i++) {
                        commands.add(reader.readLine());
                        cmdByteLength += commands.getLast().length() + 2;
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
                            this.server.addOffset(cmdByteLength);
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
                            this.server.getReplicas().add(this.getClientSocket());
                            psync();
                            break;
                        case Constants.wait:
                            waitC(commands);
                            break;
                        default:
                            System.out.println("invalid command");
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: "+ e.getMessage());
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
        for(Socket replica: this.server.getReplicas()) {
            String out = CommandConstructor.getCommand(commands.get(1), commands);
            OutputStream repOut = replica.getOutputStream();
            repOut.write(out.getBytes());
            repOut.flush();
            this.count++;
            System.out.println("sent to replica");
        }
    }

    private void waitC(List<String> commands) throws IOException, InterruptedException {
        int replicas = this.server.getReplicas().size();
        boolean flag = false;

        if (this.server.getOffset() > 0) {
            replicas = Integer.parseInt(commands.get(3));
            flag = true;
            TimeUnit.MILLISECONDS.sleep(500);
        }

        System.out.println("this is reply to wait: " + replicas);
        this.getClientSocket().getOutputStream().write(String.format(":%d\r\n", replicas).getBytes());
        this.getClientSocket().getOutputStream().flush();
    }

}
