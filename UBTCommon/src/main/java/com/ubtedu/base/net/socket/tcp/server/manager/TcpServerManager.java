package com.ubtedu.base.net.socket.tcp.server.manager;

import com.ubtedu.base.net.socket.tcp.server.TcpServer;

import java.util.HashSet;
import java.util.Set;

/**
 * tcpserver
 */
public class TcpServerManager {
    private static Set<TcpServer> sMXTcpServers = new HashSet<>();

    public static void putTcpServer(TcpServer TcpServer) {
        sMXTcpServers.add(TcpServer);
    }

    public static TcpServer getTcpServer(int port) {
        if (port == 0){
            return null;
        }
        for (TcpServer ts : sMXTcpServers) {
            if (ts.getPort() == port) {
                return ts;
            }
        }
        return null;
    }
}
