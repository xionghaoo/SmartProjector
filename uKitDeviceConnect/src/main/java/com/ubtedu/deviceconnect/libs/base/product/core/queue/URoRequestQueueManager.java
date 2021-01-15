package com.ubtedu.deviceconnect.libs.base.product.core.queue;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.link.URoLink;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoProductInterceptor;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.router.URoLinkRouter;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author naOKi
 **/
public abstract class URoRequestQueueManager {

    protected final static URoCommandRequestResult REQUEST_FAILURE_RESULT = new URoCommandRequestResult(false, false, false, null);

    private Handler handler;

    private final long MINIMIZE_SEND_INTERVAL;
    private final long MIN_RECEIVED_TIMEOUT;
    private final long MAX_RECEIVED_TIMEOUT;

    private final PriorityBlockingQueue<URoMessageTask> taskQueue;

    private final HashMap<String, LinkedBlockingQueue<URoDeviceReceivedMessage>> receiveQueue;

//    private IUKitDeviceMessageListener messageListener;

    private long lastSendTime = -1;

    private ReceiveThread receiveThread = null;
    private final Object lockReceiveThread = new Object();

    private SendThread sendThread = null;
    private final Object lockSendThread = new Object();

    private final URoRequestFilter commonExecutionFilter;

    private URoRequestFilter executionFilter = null;
    private final Object lockExecutionFilter = new Object();

    private URoLinkModel linkModel;
    private URoProtocolHandler protocolHandler;

    private int sequenceId = 0;
    private final Object lockSequenceId = new Object();

    private URoProductInterceptor interceptor;

    protected URoRequestQueueManager(URoLinkModel linkModel, URoProtocolHandler protocolHandler) {
        handler = new Handler(Looper.getMainLooper());
        taskQueue = new PriorityBlockingQueue<>();
        receiveQueue = new HashMap<>();
        this.linkModel = linkModel;
        this.protocolHandler = protocolHandler;
        commonExecutionFilter = defaultUKitMessageFilter();
        MINIMIZE_SEND_INTERVAL = Math.max(minimizeSendInterval(), 0);
        MIN_RECEIVED_TIMEOUT = Math.max(minReceivedTimeout(), 50);
        MAX_RECEIVED_TIMEOUT = Math.max(MIN_RECEIVED_TIMEOUT, Math.min(maxReceivedTimeout(), 60000));
    }

    protected abstract long minimizeSendInterval();

    protected abstract long minReceivedTimeout();
    protected abstract long maxReceivedTimeout();

    protected abstract URoCommandRequestResult handleUKitCommandRequest(URoRequest request) throws Exception;
    protected abstract URoDeviceReceivedMessage handleUKitCommandResponse() throws Exception;

    private int nextSequenceId() {
        int sequenceId;
        synchronized (lockSequenceId) {
            sequenceId = ++this.sequenceId;
        }
        return sequenceId;
    }

    private void resetSequenceId() {
        synchronized (lockSequenceId) {
            this.sequenceId = 0;
        }
    }

    protected byte[] encodeRequestData(URoRequest request) throws Exception {
        byte[] encodeRequestData;
        encodeRequestData = request.getPreBuildEncodeRequestData();
        if(encodeRequestData == null) {
            int sequenceId = nextSequenceId();
            if (request.prepare(protocolHandler, sequenceId)) {
                encodeRequestData = request.getPreBuildEncodeRequestData();
            } else {
                encodeRequestData = protocolHandler.encodeRequestMessage(request, sequenceId);
            }
        }
        return encodeRequestData;
    }

    protected URoResponse decodeResponseData(URoCommand cmd, int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        return protocolHandler.decodeResponseMessage(cmd, errorCode, bizData, rawResponse);
    }

    protected void notifyResponse(URoDeviceReceivedMessage receiveData) {
        if(receiveData == null) {
            return;
        }
        URoCommand cmd = receiveData.cmd;
        if (!protocolHandler.ignorePrintData(cmd)) {
            URoProtocolHandler.printByteData("Message data response: " + cmd.getName(), " <<<<< ", receiveData.rawResponse);
        }
        URoResponse response = receiveData.response;
        if(interceptor != null) {
            URoResponse newResponse = interceptor.onInterceptResponse(response);
            if(newResponse != null) {
                response = newResponse;
                receiveData.response = response;
            }
        }
        if(response.isAborted()) {
            URoLogUtils.e("Response ABORT!");
            return;
        }
        if(response.isPush()) {
            URoLogUtils.e("Push Message!");
            return;
        }
        String key;
        if(!TextUtils.isEmpty(receiveData.identity)) {
            key = receiveData.identity;
        } else {
            key = cmd.getName();
        }
        LinkedBlockingQueue<URoDeviceReceivedMessage> queue = receiveQueue.get(key);
        if(queue == null && receiveQueue.containsKey(key)) {
            synchronized (receiveQueue) {
                receiveQueue.remove(key);
            }
            return;
        }
        if(queue == null && !receiveQueue.containsKey(key) && !TextUtils.isEmpty(receiveData.identity)) {
            queue = new LinkedBlockingQueue<>();
            synchronized (receiveQueue) {
                receiveQueue.put(key, queue);
            }
        }
        if(queue != null) {
            cleanupHistoryData(queue);
            queue.offer(receiveData);
        }
    }

//    public void setMessageListener(IUKitDeviceMessageListener messageListener) {
//        this.messageListener = messageListener;
//    }

    public void setInterceptor(URoProductInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    private void cleanupHistoryData(LinkedBlockingQueue<URoDeviceReceivedMessage> queue) {
        while(queue.size() >= 10) {
            URoDeviceReceivedMessage historyData = queue.peek();
            if(historyData != null && (System.currentTimeMillis() - historyData.receiveTime) > 1000) {
                queue.remove(historyData);
            } else {
                break;
            }
        }
    }

    protected URoRequestFilter defaultUKitMessageFilter() {
        return null;
    }

    protected boolean isConnected() {
        URoLink link = URoLinkRouter.getInstance().findLink(linkModel);
        return link != null && link.isConnected();
    }

    protected int read(@NonNull byte[] data) throws Exception {
        try {
            int readLen;
            int totalRead = 0;
            while(totalRead < data.length && (readLen = read(data, totalRead, data.length - totalRead)) > 0) {
                totalRead += readLen;
            }
            return totalRead;
        } catch (Exception e) {
            URoLogUtils.e(e);
            throw e;
        }
    }

    protected int read(@NonNull byte[] data, int offset, int length) throws Exception {
        URoLink link = URoLinkRouter.getInstance().findLink(linkModel);
        return link != null ? link.read(data, offset, length) : -1;
    }

    protected void write(@NonNull byte[] data) throws Exception {
        write(data, 0, data.length);
    }

    protected void write(@NonNull byte[] data, int offset, int length) throws Exception {
        URoLink link = URoLinkRouter.getInstance().findLink(linkModel);
        if(link != null) {
            link.write(data, offset, length);
        }
    }

    private class ProcessThread extends Thread {

        private URoMessageTask task;
        private URoCommandRequestResult requestResult;
        private long sendTime;

        public ProcessThread(URoMessageTask task, long sendTime, URoCommandRequestResult requestResult) {
            super("Thread-ProcessThread");
            this.task = task;
            this.sendTime = sendTime;
            this.requestResult = requestResult;
        }

        @Override
        public void run() {
            try {
                if (task.getRequest() == null || task.getRequest().isAborted() || !isConnected()) {
                    return;
                }
                if(!task.getRequest().isAborted()) {
                    awaitResult(task, sendTime, requestResult);
                } else {
                    URoLogUtils.e("Request ABORT!");
                }
            } catch (Exception e) {
                URoLogUtils.e(e);
                notifyFailure();
            }
        }

        private void notifyFailure() {
            notifyCallback(null);
        }

        private void notifyCallback(URoResponse response) {
            if(task == null || task.callback == null) {
                return;
            }
            final URoRequestCallback callback = task.callback;
            final URoResponse _result = response != null ? response : URoResponse.newInstance(false, task.getRequest(), null, null);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onRequestResult(_result);
                }
            });
        }

        private void awaitResult(URoMessageTask task, long sendTime, URoCommandRequestResult requestResult) throws Exception {
            final URoRequestCallback callback = task.getCallback();
            if(callback != null) {
                String key;
                key = requestResult.registerResultIdentity;
//                URoLogUtils.e("Wait for result [%s]", key);
                LinkedBlockingQueue<URoDeviceReceivedMessage> queue = receiveQueue.get(key);
                if(queue == null) {
                    queue = new LinkedBlockingQueue<>();
                    synchronized (receiveQueue) {
                        receiveQueue.put(key, queue);
                    }
                }
                long timeoutThreshold = Math.min(Math.max(task.getRequest().timeoutThreshold(), MIN_RECEIVED_TIMEOUT), MAX_RECEIVED_TIMEOUT);
//                long timeoutThreshold = 10000L;
                URoDeviceReceivedMessage receiveData;
                try {
                    long cmdSendTime = sendTime;
                    long receiveStartTime = System.currentTimeMillis();
                    do {
                        receiveData = null;
                        timeoutThreshold -= (System.currentTimeMillis() - receiveStartTime);
                        if (timeoutThreshold <= 0) {
                            break;
                        }
                        receiveData = queue.poll(timeoutThreshold, TimeUnit.MILLISECONDS);
                        if (receiveData != null && receiveData.receiveTime < cmdSendTime) {
                            URoLogUtils.e(String.format("无效数据[%s]！开始接收时间：%s, 数据产生时间：%s", task.getRequest().getCmd().getName(), String.valueOf(receiveStartTime), String.valueOf(receiveData.receiveTime)));
                        } else if (receiveData == null) {
                            URoLogUtils.e(String.format("无效数据[%s]！接收超时", task.getRequest().getCmd().getName()));
                        }
                    } while (receiveData != null && receiveData.receiveTime < cmdSendTime);
                } catch (Exception e) {
                    receiveData = null;
                } finally {
                    if(requestResult.registerResult) {
                        synchronized (receiveQueue) {
                            receiveQueue.put(key, null);
                        }
                    }
                }
                notifyCallback(receiveData != null ? receiveData.response : null);
            }
        }

    }

    private class SendThread extends Thread {

        public SendThread() {
            super("Thread-SendThread");
        }

        @Override
        public void run() {
            while(true) {
                URoMessageTask task = null;
                try {
                    long sleepTime = Math.min(lastSendTime + MINIMIZE_SEND_INTERVAL - System.currentTimeMillis(), MINIMIZE_SEND_INTERVAL);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    task = taskQueue.take();
                    if(task.getRequest() == null || !isConnected()) {
                        continue;
                    }
                    URoRequest request = task.getRequest();
                    if(interceptor != null) {
                        URoRequest newRequest = interceptor.onInterceptRequest(request);
                        if(newRequest != null) {
                            request = newRequest;
                            task.updateRequest(request);
                        }
                    }
                    if(request.isAborted()) {
                        continue;
                    }
                    URoCommandRequestResult requestResult = handleUKitCommandRequest(request);
                    if (!requestResult.isSuccess) {
                        return;
                    }
                    lastSendTime = System.currentTimeMillis();
                    new ProcessThread(task, lastSendTime, requestResult).start();
                } catch (Exception e) {
                    URoLogUtils.e(e);
                } finally {
                    if(task != null) {
                        taskQueue.remove(task);
                    }
                }
            }
        }

    }

    private class ReceiveThread extends Thread {

        public ReceiveThread() {
            super("Thread-ReceiveThread");
        }

        @Override
        public void run() {
            while(isConnected()) {
                try {
                    readResponse();
                } catch (IOException e) {
                    URoLogUtils.e(e);
                    URoLink link = URoLinkRouter.getInstance().findLink(linkModel);
                    if(link != null) {
                        URoLinkRouter.getInstance().disconnectLink(link);
                    }
                } catch (Exception e) {
                    URoLogUtils.e(e);
                }
            }
        }

        public void readResponse() throws Exception {
            URoDeviceReceivedMessage receiveData = handleUKitCommandResponse();
//            URoLogUtils.e("Get for result [%s]", receiveData.cmdType.name());
            if(receiveData == null) {
                return;
            }
            URoResponse response = decodeResponseData(receiveData.cmd, receiveData.errorCode, receiveData.bizData, receiveData.rawResponse);
            receiveData.response = response;
            response.setExtraData(receiveData.getExtraData());
            response.setErrorCode(receiveData.errorCode);
            notifyResponse(receiveData);
        }

    }

    protected static class URoCommandRequestResult {
        public boolean isSuccess;
        public boolean awaitResult;
        public boolean registerResult;
        public String registerResultIdentity;
        public URoCommandRequestResult(boolean isSuccess, boolean awaitResult, boolean registerResult, String registerResultIdentity) {
            this.isSuccess = isSuccess;
            this.awaitResult = awaitResult;
            this.registerResult = registerResult;
            this.registerResultIdentity = registerResultIdentity;
        }
    }

    private class URoMessageTask implements Comparable<URoMessageTask> {
        private URoRequest request;
        private URoRequestPriority priority;
        private Object tag;
        private URoRequestCallback callback;
        private long timestamp = -1;
        public URoMessageTask(URoRequest request, URoRequestPriority priority, Object tag, URoRequestCallback callback) {
            this.request = request;
            this.priority = priority;
            this.tag = tag;
            this.callback = callback;
        }
        public void updateRequest(URoRequest request) {
            this.request = request;
        }
        public String getKey() {
            return request.getKey();
        }
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        public Object getTag() {
            return tag;
        }
        public URoRequest getRequest() {
            return request;
        }
        public URoRequestPriority getPriority() {
            return priority;
        }
        public URoRequestCallback getCallback() {
            return callback;
        }
        @Override
        public int compareTo(@NonNull URoMessageTask o) {
            int result = getPriority().ordinal() - o.getPriority().ordinal();
            if(result == 0) {
                result = Long.compare(timestamp, o.timestamp);
            }
            return result;
        }
    }

    public <T> boolean addRequest(URoRequest request, URoRequestPriority priority, Object tag, URoRequestCallback<T> callback) {
        if(request == null) {
            return false;
        }
        if(!isConnected()) {
            return false;
        }
        if(!isCommandAccept(request)) {
            return false;
        }
        if(priority == null) {
            priority = URoRequestPriority.NORMAL;
        }
        if(tag != null) {
            removeByTag(tag);
        }
        if(URoCommandConstants.CMD_BOARD_STOP.equals(request.getCmd())) {
            resetQueue();
        }
//        if(URoCommandConstants.CMD_SENSOR_VALUE.equals(request.getCmd()) && callback != null) {
//            new Throwable().printStackTrace();
//        }
        startReceiveThread();
        startSendThread();
        URoMessageTask task = new URoMessageTask(request, priority, tag, callback);
        task.setTimestamp(System.nanoTime());
        new Thread() {
            @Override
            public void run() {
                try {
                    //提前编码指令，应该用简单的Thread就可以了，后面发送的时候如果发现没有初始化还会再次prepare的，有同步锁不担心重复执行
                    request.prepare(protocolHandler, nextSequenceId());
                } catch (Throwable e) {
                    // do nothing
                }
            }
        }.start();
        taskQueue.offer(task);
        return true;
    }

    public void prepare() {
        startReceiveThread();
    }

    private void startSendThread() {
        synchronized (lockSendThread) {
            if(sendThread != null && sendThread.isAlive()) {
                return;
            }
            sendThread = new SendThread();
            sendThread.start();
        }
    }

    private void startReceiveThread() {
        synchronized (lockReceiveThread) {
            if(receiveThread != null && receiveThread.isAlive()) {
                return;
            }
            receiveThread = new ReceiveThread();
            receiveThread.start();
        }
    }

    public void cleanup() {
        try {
            resetQueue();
        } catch (Throwable e) {
            // do nothing
        }
        try {
            receiveQueue.clear();
        } catch (Throwable e) {
            // do nothing
        }
        resetSequenceId();
    }

    public void setExecutionFilter(URoRequestFilter executionFilter) {
        synchronized (lockExecutionFilter) {
            this.executionFilter = executionFilter;
        }
    }

    public void cleanupExecutionFilter() {
        synchronized (lockExecutionFilter) {
            executionFilter = null;
        }
    }

    private boolean isCommandAccept(URoRequest cmd) {
        synchronized (lockExecutionFilter) {
            return (executionFilter == null || executionFilter.accept(cmd)) && (commonExecutionFilter == null || commonExecutionFilter.accept(cmd));
        }
    }

    public boolean isQueueEmpty() {
        return taskQueue.size() == 0;
    }

    public boolean isQueueContain(Object tag) {
        if(tag == null) {
            return false;
        }
        Iterator<URoMessageTask> iterator = taskQueue.iterator();
        while(iterator.hasNext()) {
            URoMessageTask task = iterator.next();
            if(task != null && task.tag != null && task.tag.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private void removeByTag(Object tag) {
        if(tag == null) {
            return;
        }
        Iterator<URoMessageTask> iterator = taskQueue.iterator();
        while(iterator.hasNext()) {
            URoMessageTask task = iterator.next();
            if(task != null && task.tag != null && task.tag.equals(tag)) {
                taskQueue.remove(task);
            }
        }
    }

    protected void resetQueue() {
        Iterator<URoMessageTask> iterator = taskQueue.iterator();
        URoResponse abortResponse = URoResponse.newInstance(false, null);
        URoCompletionResult abortResult = new URoCompletionResult<>(abortResponse, URoError.ABORT);
        while(iterator.hasNext()) {
            URoMessageTask task = iterator.next();
            taskQueue.remove(task);
            URoCompletionCallbackHelper.sendCallback(abortResult, new URoRequestCallbackWrapper<>(task.callback));
        }
    }

    public long getLastSendTime() {
        return lastSendTime;
    }

}
