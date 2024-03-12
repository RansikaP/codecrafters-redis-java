package Commands;

import java.util.List;

public final class CommandConstructor {
    private CommandConstructor() {}

    public static String getCommand(String command, List<String> parameters) {
        switch (command.toLowerCase()) {
            case Constants.set:
                return set(parameters);
            default:
                return null;
        }
    }

    public static String set(List<String> parameters) {
        return String.format("*3\r\n$3\r\nset\r\n$%d\r\n%s\r\n$%d\r\n%s\r\n", Integer.parseInt(parameters.get(3)), parameters.get(4), Integer.parseInt(parameters.get(5)), parameters.get(6));
    }
}
