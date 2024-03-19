package Commands;

import java.util.Base64;

public class Constants {
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
    public static final String REPLCONF_listening_port = "*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n";
    public static final String REPLCONF_capa_psync2 = "*3\r\n$8\r\nREPLCONF\r\n$4\r\ncapa\r\n$6\r\npsync2\r\n";
    public static final String PSYNC_HANDSHAKE = "*3\r\n$5\r\nPSYNC\r\n$1\r\n?\r\n$2\r\n-1\r\n";
    public static final String replconf = "replconf";
    public static final String psync = "psync";
    public static final String FULLRESYNC = "FULLRESYNC";
    public static final byte[] EMPTY_RDB = Base64.getDecoder().decode("UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==");
    public static final String wait = "wait";
}
