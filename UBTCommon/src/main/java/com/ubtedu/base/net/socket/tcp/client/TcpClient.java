package com.ubtedu.base.net.socket.tcp.client;

import android.util.Log;

import com.ubtedu.base.net.socket.BaseSocket;
import com.ubtedu.base.net.socket.exception.Error;
import com.ubtedu.base.net.socket.tcp.client.bean.TargetInfo;
import com.ubtedu.base.net.socket.tcp.client.bean.TcpMsg;
import com.ubtedu.base.net.socket.tcp.client.listener.TcpClientListener;
import com.ubtedu.base.net.socket.tcp.client.manager.TcpClientManager;
import com.ubtedu.base.net.socket.tcp.client.state.ClientState;
import com.ubtedu.base.net.socket.utils.CharsetUtil;
import com.ubtedu.base.net.socket.utils.ExceptionUtils;
import com.ubtedu.base.net.socket.utils.UBTSocketLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * tcp客户端
 */
public class TcpClient extends BaseSocket {
    public static final String TAG = "TcpClient";
    protected TargetInfo mTargetInfo;//目标ip和端口号
    protected Socket mSocket;
    protected ClientState mClientState;
    protected TcpConnConfig mTcpConnConfig;
    protected ConnectionThread mConnectionThread;
    protected SendThread mSendThread;
    protected ReceiveThread mReceiveThread;
    protected List<TcpClientListener> mTcpClientListeners;
    private LinkedBlockingQueue<TcpMsg> msgQueue;

    /**
     * 发送文件时，每次发送Byte的长度
     */
    private final static int SEND_BYTES_LENGHT = 1024 * 40;

    private TcpClient() {
        super();
    }

    public TcpClient(TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        init(targetInfo, tcpConnConfig);
    }

    /**
     * 创建tcp连接，需要提供服务器信息
     *
     * @param targetInfo
     * @return
     */
    public static TcpClient getTcpClient(TargetInfo targetInfo) {
        return getTcpClient(targetInfo, null);
    }

    public static TcpClient getTcpClient(TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        TcpClient TcpClient = TcpClientManager.getTcpClient(targetInfo);
        if (TcpClient == null) {
            TcpClient = new TcpClient();
            TcpClient.init(targetInfo, tcpConnConfig);
            TcpClientManager.putTcpClient(TcpClient);
        }
        return TcpClient;
    }

    /**
     * 根据socket创建client端，目前仅用在socketServer接受client之后
     *
     * @param socket
     * @return
     */
    public static TcpClient getTcpClient(Socket socket, TargetInfo targetInfo) {
        return getTcpClient(socket, targetInfo, null);
    }

    public static TcpClient getTcpClient(Socket socket, TargetInfo targetInfo, TcpConnConfig connConfig) {
        if (!socket.isConnected()) {
            ExceptionUtils.throwException("socket is closeed");
        }
        TcpClient tcpClient = new TcpClient();
        tcpClient.init(targetInfo, connConfig);
        tcpClient.mSocket = socket;
        tcpClient.mClientState = ClientState.Connected;
        tcpClient.onConnectSuccess();
        return tcpClient;
    }


    private void init(TargetInfo targetInfo, TcpConnConfig connConfig) {
        this.mTargetInfo = targetInfo;
        mClientState = ClientState.Disconnected;
        mTcpClientListeners = new ArrayList<>();
        if (mTcpConnConfig == null && connConfig == null) {
            mTcpConnConfig = new TcpConnConfig.Builder().create();
        } else if (connConfig != null) {
            mTcpConnConfig = connConfig;
        }
    }

    public synchronized TcpMsg sendMsg(String message) {
        TcpMsg msg = new TcpMsg(message, mTargetInfo, TcpMsg.MsgType.SendMsg);
        return sendMsg(msg);
    }

    public synchronized TcpMsg sendMsg(byte[] message) {
        TcpMsg msg = new TcpMsg(message, mTargetInfo, TcpMsg.MsgType.SendMsg);
        return sendMsg(msg);
    }

    public synchronized TcpMsg sendMsg(TcpMsg msg) {
        if (isDisconnected()) {
            UBTSocketLog.d(TAG, "发送消息 " + msg + "，当前没有tcp连接，先进行连接");
            connect();
        }
        boolean re = enqueueTcpMsg(msg);
        if (re) {
            return msg;
        }
        return null;
    }

    public synchronized TcpMsg sendFile(String path) {
        if (path == null) {
            TcpMsg msg = new TcpMsg(Error.FILE_NOT_FOUND);
            msg.setMsgType(TcpMsg.MsgType.SendFile);
            notifyError(msg);
            return msg;
        }
        TcpMsg msg = new TcpMsg();
        msg.setTarget(mTargetInfo);
        msg.setMsgType(TcpMsg.MsgType.SendFile);
        msg.setFilePath(path);
        return sendMsg(msg);
    }

    public synchronized boolean cancelMsg(TcpMsg msg) {
        return getSendThread().cancel(msg);
    }

    public synchronized boolean cancelMsg(int msgId) {
        return getSendThread().cancel(msgId);
    }

    public synchronized void connect() {
        if (!isDisconnected()) {
            UBTSocketLog.d(TAG, "已经连接了或正在连接");
            return;
        }
        UBTSocketLog.d(TAG, "tcp connecting");
        setClientState(ClientState.Connecting);//正在连接
        getConnectionThread().start();
    }

    public synchronized Socket getSocket() {
        if (mSocket == null || isDisconnected() || !mSocket.isConnected()) {
            mSocket = new Socket();
            try {
                mSocket.setSoTimeout((int) mTcpConnConfig.getReceiveTimeout());
            } catch (SocketException e) {
//                e.printStackTrace();
            }
        }
        return mSocket;
    }

    public TcpConnConfig getTcpConnConfig() {
        return mTcpConnConfig;
    }

    public synchronized void disconnect() {
        disconnect("手动关闭tcpclient", null);
    }

    protected synchronized void onErrorDisConnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        disconnect(msg, e);
        if (mTcpConnConfig.isReconnect()) {//重连
            connect();
        }
    }

    protected synchronized void disconnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        closeSocket();
        getConnectionThread().interrupt();
        getSendThread().interrupt();
        getReceiveThread().interrupt();
        setClientState(ClientState.Disconnected);
        notifyDisconnected(msg, e);
        UBTSocketLog.d(TAG, "tcp closed");
    }

    private synchronized boolean closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return true;
    }

    //连接已经连接，接下来的流程，创建发送和接受消息的线程
    private void onConnectSuccess() {
        UBTSocketLog.d(TAG, "tcp connect 建立成功");
        setClientState(ClientState.Connected);//标记为已连接
        getSendThread().start();

//        getReceiveThread().start();
    }

    /**
     * tcp连接线程
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            try {
                int localPort = mTcpConnConfig.getLocalPort();
                if (localPort > 0) {
                    if (!getSocket().isBound()) {
                        getSocket().bind(new InetSocketAddress(localPort));
                    }
                }
                getSocket().connect(new InetSocketAddress(mTargetInfo.getIp(), mTargetInfo.getPort()),
                        (int) mTcpConnConfig.getConnTimeout());
                UBTSocketLog.d(TAG, "创建连接成功,target=" + mTargetInfo + ",localport=" + localPort);
            } catch (Exception e) {
                UBTSocketLog.d(TAG, "创建连接失败,target=" + mTargetInfo + "," + e);
                onErrorDisConnect("创建连接失败", e);
                return;
            }
            notifyConnected();
            onConnectSuccess();
        }
    }

    public boolean enqueueTcpMsg(final TcpMsg tcpMsg) {
        if (tcpMsg == null || getMsgQueue().contains(tcpMsg)) {
            return false;
        }
        try {
            getMsgQueue().put(tcpMsg);
            return true;
        } catch (InterruptedException e) {
//            e.printStackTrace();
            tcpMsg.setError(Error.THREAD_INTERRUPT);
            notifyError(tcpMsg);
        }
        return false;
    }

    protected LinkedBlockingQueue<TcpMsg> getMsgQueue() {
        if (msgQueue == null) {
            msgQueue = new LinkedBlockingQueue<>();
        }
        return msgQueue;
    }

    /**
     * 另起一条线程通知文件发送进度
     */
    private class SendFileNotifyThread extends Thread {
        TcpMsg mMsg;
        private boolean mStopNotify;

        private double mSendBytes;
        /**
         * 通知间隔时间
         */
        private final static int NOTIFY_INTERNAL_TIME = 800;
        /**
         * 如果刷新时间大于5个NOTIFY_INTERNAL_TIME,
         * 发送的数据长度还没有变化，则认为连接已经被中断了。
         */
        private int mRefreshTime;

        public SendFileNotifyThread(TcpMsg msg) {
            mMsg = msg;
        }

        public void stopNotify() {
            mStopNotify = true;
            mMsg = null;
        }

        @Override
        public void run() {
            super.run();
            try {
                while (!mStopNotify && isConnected()) {
                    if (mMsg != null && mMsg.getFileTotalBytes() != 0) {

                        double sendBytes = mMsg.getFileSendBytes();
                        if (sendBytes == mSendBytes) {
                            if (mRefreshTime >= 12 * NOTIFY_INTERNAL_TIME) {
                                mMsg.setError(Error.DISCONNECT);
                                notifyError(mMsg);
                                mStopNotify = true;
                                disconnect();
                                Log.e("CONNECT_CHECK", "Socket connect state is interrupted when sending file.");
                                break;
                            }
                            Log.e("CONNECT_CHECK", "mRefreshTime:"+mRefreshTime);
                            mRefreshTime += NOTIFY_INTERNAL_TIME;
                        } else {
                            mMsg.setSendProgress(sendBytes / mMsg.getFileTotalBytes());
                            notifySended(mMsg);
                            mRefreshTime = 0;
                        }
                        mSendBytes = sendBytes;
                        sleep(NOTIFY_INTERNAL_TIME);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean checkConnected() {
            try {
                getSocket().sendUrgentData(0xFF);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private class SendThread extends Thread {
        private TcpMsg sendingTcpMsg;

        protected SendThread setSendingTcpMsg(TcpMsg sendingTcpMsg) {
            this.sendingTcpMsg = sendingTcpMsg;
            return this;
        }

        public TcpMsg getSendingTcpMsg() {
            return this.sendingTcpMsg;
        }

        public boolean cancel(TcpMsg packet) {
            return getMsgQueue().remove(packet);
        }

        public boolean cancel(int tcpMsgID) {
            return getMsgQueue().remove(new TcpMsg(tcpMsgID));
        }

        @Override
        public void run() {
            TcpMsg msg;
            try {
                while (isConnected() && !Thread.interrupted() && (msg = getMsgQueue().take()) != null) {
                    setSendingTcpMsg(msg);//设置正在发送的
                    UBTSocketLog.d(TAG, "tcp sending msg=" + msg);
                    if (msg.getMsgType() != TcpMsg.MsgType.SendFile) {
                        byte[] data = msg.getSourceDataBytes();
                        if (data == null) {//根据编码转换消息
                            data = CharsetUtil.stringToData(msg.getSourceDataString(), mTcpConnConfig.getCharsetName());
                            try {
                                sendData(msg, data);
                            } catch (IOException e) {
                                e.printStackTrace();
                                msg.setError(Error.IO_EXCEPTION);
                                notifyError(msg);
                            }
                        }
                    } else {
                        File file = new File(msg.getFilePath());
                        if (!file.exists() || !file.isFile()) {
                            msg.setError(Error.FILE_NOT_FOUND);
                            notifyError(msg);
                            return;
                        }
                        SendFileNotifyThread notifyThread = null;
                        try {
                            FileInputStream fin = new FileInputStream(file);
                            msg.setFileTotalBytes(file.length());

                            notifyThread = new SendFileNotifyThread(msg);
                            notifyThread.start();


                            int length = 0;
                            byte[] sendBytes = new byte[SEND_BYTES_LENGHT];
                            while ((length = fin.read(sendBytes, 0, sendBytes.length)) > 0) {
                                msg.setFileSendBytes(msg.getFileSendBytes() + SEND_BYTES_LENGHT);
                                getSocket().getOutputStream().write(sendBytes, 0, length);
                                getSocket().getOutputStream().flush();
                            }
                            notifyThread.stopNotify();
                            notifyThread = null;
                            //确保最后进度是100
                            msg.setSendProgress(1.0d);
                            msg.setFileSendBytes(msg.getFileTotalBytes());
                            notifySended(msg);

                            getSocket().close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            msg.setError(Error.FILE_NOT_FOUND);
                            notifyError(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            msg.setError(Error.IO_EXCEPTION);
                            notifyError(msg);
                        } finally {
                            if (notifyThread != null) {
                                notifyThread.stopNotify();
                            }
                        }
                    }

                }
            } catch (InterruptedException e) {
                //没有必要处理这个异常
//                e.printStackTrace();
//                TcpMsg tcpMsg = new TcpMsg(Error.THREAD_INTERRUPT);
//                tcpMsg.setMsgType(TcpMsg.MsgType.SendFile);
//                notifyError(tcpMsg);
            }
        }

        private void sendData(TcpMsg msg, byte[] data) throws IOException {
            if (data != null && data.length > 0) {
                getSocket().getOutputStream().write(data);
                getSocket().getOutputStream().flush();
                msg.setTime();
                notifySended(msg);
            }
        }
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream is = getSocket().getInputStream();
                while (isConnected() && !Thread.interrupted()) {
                    byte[] result = mTcpConnConfig.getStickPackageHelper().execute(is);//粘包处理
                    if (result == null) {//报错
                        UBTSocketLog.d(TAG, "tcp Receive 接收到空数据 " + Arrays.toString(result));
//                        onErrorDisConnect("粘包处理中发送错误", null);
                        continue;
                    }
                    UBTSocketLog.d(TAG, "tcp Receive 解决粘包之后的数据 " + Arrays.toString(result));
                    TcpMsg tcpMsg = new TcpMsg(result, mTargetInfo, TcpMsg.MsgType.Receive);
                    tcpMsg.setTime();
                    String msgstr = CharsetUtil.dataToString(result, mTcpConnConfig.getCharsetName());
                    tcpMsg.setSourceDataString(msgstr);
                    boolean va = mTcpConnConfig.getValidationHelper().execute(result);
                    if (!va) {
                        UBTSocketLog.d(TAG, "tcp Receive 数据验证失败 ");
                        notifyError(tcpMsg);//验证失败
                        continue;
                    }
                    byte[][] decodebytes = mTcpConnConfig.getDecodeHelper().execute(result, mTargetInfo, mTcpConnConfig);
                    tcpMsg.setEndDecodeData(decodebytes);
                    UBTSocketLog.d(TAG, "tcp Receive  succ msg= " + tcpMsg);
                    notifyReceive(tcpMsg);//notify listener
                }
            } catch (Exception e) {
                UBTSocketLog.d(TAG, "tcp Receive  error  " + e);
                onErrorDisConnect("接受消息错误", e);
            }
        }
    }

    protected ReceiveThread getReceiveThread() {
        if (mReceiveThread == null || !mReceiveThread.isAlive()) {
            mReceiveThread = new ReceiveThread();
        }
        return mReceiveThread;
    }

    protected SendThread getSendThread() {
        if (mSendThread == null || !mSendThread.isAlive()) {
            mSendThread = new SendThread();
        }
        return mSendThread;
    }

    protected ConnectionThread getConnectionThread() {
        if (mConnectionThread == null || !mConnectionThread.isAlive() || mConnectionThread.isInterrupted()) {
            mConnectionThread = new ConnectionThread();
        }
        return mConnectionThread;
    }

    public ClientState getClientState() {
        return mClientState;
    }

    protected void setClientState(ClientState state) {
        if (mClientState != state) {
            mClientState = state;
        }
    }

    public boolean isDisconnected() {
        return getClientState() == ClientState.Disconnected;
    }

    public boolean isConnected() {
        return getClientState() == ClientState.Connected;
    }

    private void notifyConnected() {
        TcpClientListener l;
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onConnected(TcpClient.this);
                }
            });
        }
    }

    private void notifyDisconnected(final String msg, final Exception e) {
        TcpClientListener l;
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onDisconnected(TcpClient.this, msg, e);
                }
            });
        }
    }


    private void notifyReceive(final TcpMsg tcpMsg) {
        TcpClientListener l;
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onReceive(TcpClient.this, tcpMsg);
                }
            });
        }
    }


    private void notifySended(final TcpMsg tcpMsg) {
        TcpClientListener l;
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onSended(TcpClient.this, tcpMsg);
                }
            });
        }
    }

    private void notifyError(final TcpMsg tcpMsg) {
        TcpClientListener l;
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onError(TcpClient.this, tcpMsg);
                }
            });
        }
    }

    public TargetInfo getTargetInfo() {
        return mTargetInfo;
    }

    public void addTcpClientListener(TcpClientListener listener) {
        if (mTcpClientListeners.contains(listener)) {
            return;
        }
        mTcpClientListeners.add(listener);
    }

    public void removeTcpClientListener(TcpClientListener listener) {
        mTcpClientListeners.remove(listener);
    }

    public void config(TcpConnConfig tcpConnConfig) {
        mTcpConnConfig = tcpConnConfig;
    }

    @Override
    public String toString() {
        return "TcpClient{" +
                "mTargetInfo=" + mTargetInfo + ",state=" + mClientState + ",isconnect=" + isConnected() +
                '}';
    }
}
