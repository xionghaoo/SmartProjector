package com.ubtedu.deviceconnect.libs.base.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoProcessChangeCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.event.URoComponentErrorEvent;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.URoProductInterceptor;
import com.ubtedu.deviceconnect.libs.base.product.URoProductManager;
import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.zip.CRC32;

/**
 * @Author naOKi
 * @Date 2019/08/19
 **/
public abstract class URoCommandMission<T> extends URoMission<T> implements URoProductInterceptor {

    private URoProduct product;
    private URoCoreProduct coreProduct;

    private boolean hasSetupIntercept = false;

    private URoMissionAbortSignal subMissionAbortSignal = null;

    public URoCommandMission(@NonNull URoProduct product) {
        this.product = product;
        this.coreProduct = URoProductManager.getInstance().getProduct(product.getProductID());
    }

    public URoCommandMission(@NonNull URoProduct product, URoCompletionCallback<T> callback) {
        super(callback);
        this.product = product;
        this.coreProduct = URoProductManager.getInstance().getProduct(product.getProductID());
    }

    @Override
    public boolean start() {
        boolean result = super.start();
        if(result) {
            product.addMission(this);
        }
        return result;
    }

    protected void performNext(@NonNull Object identity, @NonNull URoInvocation nextAction) {
        performNext(identity, nextAction, -1, -1);
    }

    protected void performNext(@NonNull Object identity, @NonNull URoInvocation nextAction, long timeoutMs) {
        performNext(identity, nextAction, timeoutMs, -1);
    }

    protected void performNext(@NonNull Object identity, @NonNull URoInvocation nextAction, long timeoutMs, int retryTimes) {
        if(timeoutMs > 0) {
            nextAction.setTimeoutThreshold(timeoutMs);
        }
        if(retryTimes > 0) {
            nextAction.setRetry(retryTimes);
        }
        performNext(new URoCommandInvocation(nextAction, identity));
    }

    protected void performNext(@NonNull Object identity, @NonNull URoRequest nextAction) {
        performNext(identity, nextAction, -1, -1);
    }

    protected void performNext(@NonNull Object identity, @NonNull URoRequest nextAction, long timeoutMs) {
        performNext(identity, nextAction, timeoutMs, -1);
    }

    protected void performNext(@NonNull Object identity, @NonNull URoRequest nextAction, long timeoutMs, int retryTimes) {
        if(timeoutMs > 0) {
            nextAction.setTimeoutThreshold(timeoutMs);
        }
        if(retryTimes > 0) {
            nextAction.setRetry(retryTimes);
        }
        performNext(new URoCommandRequest(nextAction, identity), timeoutMs);
    }

    @Override
    protected void onMissionRelease() {
        cleanupIntercept();
    }

    @Override
    public final URoRequest onInterceptRequest(URoRequest request) {
        if(request == null) {
            return null;
        } else {
            return onInterceptRequest(request.getCmd(), request);
        }
    }

    @Override
    public final URoResponse onInterceptResponse(URoResponse response) {
        if(response == null) {
            return null;
        } else {
            return onInterceptResponse(response.getCmd(), response);
        }
    }

    protected void setupIntercept() {
        synchronized (this) {
            if (!hasSetupIntercept) {
                product.addInterceptor(this);
                hasSetupIntercept = true;
            }
        }
    }

    protected void cleanupIntercept() {
        synchronized (this) {
            if (hasSetupIntercept) {
                hasSetupIntercept = false;
                product.removeInterceptor(this);
            }
        }
    }

    @Override
    public boolean stop() {
        boolean result = super.stop();
        if(subMissionAbortSignal != null) {
            subMissionAbortSignal.abort();
            subMissionAbortSignal = null;
        }
        return result;
    }

    protected URoRequest onInterceptRequest(URoCommand cmd, URoRequest request) {
        return null;
    }

    protected URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
        return null;
    }

    protected abstract void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable;

    protected boolean checkPreviousResult(boolean isSuccess, Object identity, @NonNull URoCompletionResult result) {
        return isSuccess;
    }

    @Override
    protected void onPreviousResult(URoCompletionResult _result) throws Throwable {
        URoCommandActionResult result = (URoCommandActionResult)_result.getData();
        if(checkPreviousResult(result.isSuccess, result.identity, result.response)) {
            onPreviousResult(result.identity, result.response);
        } else {
            notifyError(URoError.UNKNOWN);
        }
    }

    protected class URoCommandActionResult {
        public boolean isSuccess;
        public Object identity;
        public URoCompletionResult response;
        public URoCommandActionResult(Object identity, URoCompletionResult response) {
            this.isSuccess = response != null && response.isSuccess();
            this.identity = identity;
            this.response = response;
        }
    }

    private abstract class URoCommandAction extends URoMissionCallback implements Callable<URoCompletionResult<URoCommandActionResult>> {
        @Override public void onMissionNextStep(int currentStep, int totalStep) {}
        @Override public void onMissionBegin() {}
        @Override public void onMissionEnd() {}
        @Override public void onProcessPercentChanged(int percent) {}
    }

    protected class URoCommandInvocation extends URoCommandAction {

        private URoInvocation invocation;
        private Object identity;
        private URoCompletionResult response;
        private final Object lock = new Object();
        private URoCompletionCallback<T> callbackWrap;

        private URoCommandInvocation(URoInvocation invocation, Object identity) {
            this.invocation = invocation;
            this.identity = identity;
        }

        @Override
        public void onComplete(URoCompletionResult result) {
            response = result;
            URoCompletionCallbackHelper.sendCallback(result, callbackWrap);
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public void onMissionNextStep(int currentStep, int totalStep) {
            if(callbackWrap instanceof URoMissionCallback) {
                ((URoMissionCallback<T>) callbackWrap).onMissionNextStep(currentStep, totalStep);
            }
        }

        @Override
        public void onMissionAccept(URoMissionAbortSignal abortSignal) {
            super.onMissionAccept(abortSignal);
            subMissionAbortSignal = abortSignal;
            if(callbackWrap instanceof URoMissionCallback) {
                ((URoMissionCallback<T>) callbackWrap).onMissionAccept(abortSignal);
            }
        }

        @Override
        public void onMissionBegin() {
            if(callbackWrap instanceof URoMissionCallback) {
                ((URoMissionCallback<T>) callbackWrap).onMissionBegin();
            }
        }

        @Override
        public void onMissionEnd() {
            if(callbackWrap instanceof URoMissionCallback) {
                ((URoMissionCallback<T>) callbackWrap).onMissionEnd();
            }
        }

        @Override
        public void onProcessPercentChanged(int percent) {
            if(callbackWrap instanceof URoProcessChangeCallback) {
                ((URoProcessChangeCallback) callbackWrap).onProcessPercentChanged(percent);
            }
        }

        @Override
        public URoCompletionResult call() throws Exception {
            URoCompletionCallback<T> callback = invocation.getCompletionCallback();
            if(callback != null) {
                callbackWrap = callback;
            }
            invocation.setCompletionCallback(this);
            invocation.sendToTarget(product);
            if(response == null) {
                synchronized (lock) {
                    lock.wait();
                }
            }
            return new URoCompletionResult<>(new URoCommandActionResult(identity, response), response.isSuccess() ? URoError.SUCCESS : URoError.UNKNOWN);
        }

    }

    protected class URoCommandRequest extends URoCommandAction {

        private URoRequest request;
        private Object identity;
        private URoCompletionResult response;
        private final Object lock = new Object();

        private URoCommandRequest(URoRequest request, Object identity) {
            this.request = request;
            this.identity = identity;
        }

        @Override
        public void onComplete(URoCompletionResult result) {
            response = result;
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public URoCompletionResult call() throws Exception {
            coreProduct.addRequest(request, request.getPriority(), request.getTag(), this);
            if(response == null) {
                synchronized (lock) {
                    lock.wait();
                }
            }
            return new URoCompletionResult<>(new URoCommandActionResult(identity, response), response.isSuccess() ? URoError.SUCCESS : (response.getError() == null ? URoError.UNKNOWN : response.getError()));
        }

    }
    
    protected void sendRequestDirectly(URoInvocation invocation) {
        invocation.sendToTarget(product);
    }

    protected void sendRequestDirectly(URoRequest request) {
        coreProduct.addRequest(request, request.getPriority(), request.getTag(), null);
    }

    protected void sendComponentError(@NonNull URoComponentType componentType, int id, @NonNull URoError error) {
        EventBus.getDefault().post(new URoComponentErrorEvent(coreProduct.getLinkModel(), componentType, id, error));
    }

    protected static int getCrc32(byte[] data) {
        int result = 0;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(data);
            result = getCrc32(bais);
        } catch (Exception e) {
            // ignore
        } finally {
            URoIoUtils.close(bais);
        }
        return result;
    }

    protected static int getCrc32(File file) {
        int result = 0;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            result = getCrc32(fis);
        } catch (Exception e) {
            // ignore
        } finally {
            URoIoUtils.close(fis);
        }
        return result;
    }

    protected static int getCrc32(InputStream is) {
        CRC32 crc32 = new CRC32();
        try {
            byte[] buffer = new byte[8 * 1024];
            int readLen;
            while((readLen = is.read(buffer, 0, buffer.length)) > 0) {
                crc32.update(buffer, 0, readLen);
            }
        } catch (Exception e) {
            // ignore
        }
        return (int)crc32.getValue();
    }

}
