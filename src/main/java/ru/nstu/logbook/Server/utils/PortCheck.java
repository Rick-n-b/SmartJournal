package ru.nstu.logbook.Server.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;


public class PortCheck {
    public static final int MAX_PORT = 65535;
    public static final int MIN_PORT = 1;

    public static boolean portAvailable(int port) {
        if(!portValid(port))
            return false;
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            if (ds != null) {
                ds.close();
            }
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) {
                    /* should not be thrown */
                }
            }
        }
        return false;
    }

    public static boolean portValid(int port) {
        return port >= MIN_PORT && port <= MAX_PORT;
    }
}
