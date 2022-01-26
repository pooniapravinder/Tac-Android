package com.wookes.tac.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class e {
    //Check VPN
    public static boolean a() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp()) iface = networkInterface.getName();
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return false;
    }
}
