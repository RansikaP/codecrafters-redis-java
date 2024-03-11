import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        int port = 6379;
        String role = "master";
        Options options = new Options();
        CommandLineParser cmdParser = new DefaultParser();

//        if (args.length >= 2) {
//            if (args[0].equalsIgnoreCase("--port")) {
//                try {
//                    port = Integer.parseInt(args[1]);
//                    System.out.println("Port: " + port);
//                    Redis = new Server(port);
//                } catch (NumberFormatException e) {
//                    System.out.println(e.getMessage());
//                }
//            }
//        } else {
//            Redis = new Server();
//        }

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
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        System.out.println(role);
        Server Redis = new Server(port, role);
        Redis.start();
    }


}
