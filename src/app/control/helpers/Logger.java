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
}
