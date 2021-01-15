package com.ubtedu.base.net.socket.udp.client.listener;

import com.ubtedu.base.net.socket.udp.client.UdpClient;
import com.ubtedu.base.net.socket.udp.client.bean.UdpMsg;

/**
 */
public interface UdpClientListener {
    void onStarted(UdpClient client);

    void onStoped(UdpClient client);

    void onSended(UdpClient client, UdpMsg udpMsg);

    void onReceive(UdpClient client, UdpMsg udpMsg);

    void onError(UdpClient client, UdpMsg sourceMsg,String msg, Exception e);

    class SimpleUdpClientListener implements UdpClientListener {

        @Override
        public void onStarted(UdpClient client) {

        }

        @Override
        public void onStoped(UdpClient client) {

        }

        @Override
        public void onSended(UdpClient XUdp, UdpMsg udpMsg) {

        }

        @Override
        public void onReceive(UdpClient client, UdpMsg msg) {

        }

        @Override
        public void onError(UdpClient client, UdpMsg sourceMsg, String msg, Exception e) {

        }


    }

}
