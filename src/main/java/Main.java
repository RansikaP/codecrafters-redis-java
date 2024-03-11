import Server.*;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        int port = 6379;
        String role = "master";
        Server redis = null;
        Options options = new Options();
        CommandLineParser cmdParser = new DefaultParser();

        Option portOption = Option.builder().longOpt("port").hasArg().desc("Server Port Number").build();
        options.addOption(portOption);
        Option roleOption = Option.builder().longOpt("replicaof").hasArgs().valueSeparator(' ').desc("Defines if server is master or slave").build();
        options.addOption(roleOption);

        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("port")) {
                port = Integer.parseInt(cmd.getOptionValue("port"));
            }
            if (cmd.hasOption("replicaof")) {
                role = "slave";
                String masterHost = cmd.getOptionValues("replicaof")[0];
                int masterPort = Integer.parseInt(cmd.getOptionValues("replicaof")[1]);
                redis = new Slave(port, role, masterHost, masterPort);
            } else
                redis = new Master(port, role);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        redis.start();
    }

}
