package com.ubtedu.base.net.socket.tcp.server.listener;

import com.ubtedu.base.net.socket.tcp.client.TcpClient;
import com.ubtedu.base.net.socket.tcp.client.bean.TcpMsg;
import com.ubtedu.base.net.socket.tcp.server.TcpServer;

/**
 * tcpserver
 */
public interface TcpServerListener {
    void onCreated(TcpServer server);

    void onListened(TcpServer server);

    void onAccept(TcpServer server, TcpClient tcpClient);

    void onSended(TcpServer server, TcpClient tcpClient, TcpMsg tcpMsg);

    void onReceive(TcpServer server, TcpClient tcpClient, TcpMsg tcpMsg);

    void onValidationFail(TcpServer server, TcpClient client, TcpMsg tcpMsg);

    void onClientClosed(TcpServer server, TcpClient tcpClient, String msg, Exception e);

    void onServerClosed(TcpServer server, String msg, Exception e);
}
