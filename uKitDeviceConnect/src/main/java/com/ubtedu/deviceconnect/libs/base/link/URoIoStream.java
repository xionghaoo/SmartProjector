package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public abstract class URoIoStream<T> {

    private T source;

    protected int write(byte[] data, int offset, int length) {
        return 0;
    }

    protected int read(byte[] data, int offset, int length) {
        return 0;
    }

    protected abstract InputStream getInputStream() throws IOException;
    protected abstract OutputStream getOutputStream() throws IOException;
    public abstract boolean isConnected();
    protected abstract boolean disconnectInternal() throws IOException;

    protected @NonNull T getSource() {
        return source;
    }

    public URoIoStream(@NonNull T source) throws Exception {
        this.source = source;
    }

    public boolean disconnect() throws IOException {
        return !isConnected() || disconnectInternal();
    }

    protected class PipedTunnel extends Thread {

        private PipedOutputStream os;
        private PipedInputStream is;

        public PipedTunnel() throws IOException {
            super("PipedTunnel");
            is = new PipedInputStream();
            os = new PipedOutputStream();
            is.connect(os);
        }

        private LinkedBlockingQueue<byte[]> messageQueue = new LinkedBlockingQueue<>();
        private boolean isStopped = false;

        @Override
        public void run() {
            while(!isStopped) {
                try {
                    byte[] data = messageQueue.take();
                    os.write(data);
                    os.flush();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        public void close() {
            isStopped = true;
            messageQueue.clear();
            URoIoUtils.close(is);
            URoIoUtils.close(os);
            interrupt();
        }

        public void open() {
            start();
        }

        public void write(byte[] data) {
            messageQueue.offer(Arrays.copyOf(data, data.length));
        }

        public void write(byte[] b, int off, int len) {
            messageQueue.offer(Arrays.copyOfRange(b, off, off + len));
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return is.read(b, off, len);
        }

        public OutputStream getOutputStream() {
            return os;
        }

        public InputStream getInputStream() {
            return is;
        }

    }

}
