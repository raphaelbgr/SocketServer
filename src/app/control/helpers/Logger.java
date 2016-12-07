package app.control.helpers;


import app.ServerMain;

/**
 * Created by raphaelbernardo on 06/12/16.
 */
public class Logger {
    public static void log(String log) {
        if (ServerMain.VERBOSE) {
            System.out.println(ServerMain.getTimestamp() + " " + log);
        }
    }

    public static void logServer(String log) {
        if (ServerMain.VERBOSE) {
            System.out.println(ServerMain.getTimestamp() + " SERVER> " + log);
        }
    }

    public static void logDb(String query) {
        if (ServerMain.DEBUG) {
            System.out.println(ServerMain.getTimestamp() + " SQL> " + query);
        }
    }
}
