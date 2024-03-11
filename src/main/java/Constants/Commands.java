package Constants;

public class Commands {
    public static final String ping = "ping";
    public static final String PING = "*1\r\n$4\r\nping\r\n";
    public static final String PONG = "+PONG\r\n";
    public static final String echo = "echo";
    public static final String get = "get";
    public static final String set = "set";
    public static final String OK = "+OK\r\n";
    public static final String NULL_BULK_STRING = "$-1\r\n";
    public static final String px = "px";
    public static final String info = "info";
    public static final String REPLCONF = "*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n";

}
