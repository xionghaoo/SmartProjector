package com.ubtedu.deviceconnect.libs.base.invocation;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoInvocation extends URoInvokeParameters {

    private String invocationName;

    private URoCompletionCallback completionCallback;

    private URoInvokeHandler invokeHandler;

    public URoInvocation(String invocationName) {
        this.invocationName = invocationName;
        this.completionCallback = null;
    }

    protected URoInvocation(URoInvocation other) {
        super(other.getParameters());
        this.invocationName = other.invocationName;
        this.completionCallback = other.completionCallback;
    }

    protected URoInvocation copy() {
        return new URoInvocation(this);
    }

    public void setInvocationName(String invocationName) {
        this.invocationName = invocationName;
    }

    public String getInvocationName() {
        return invocationName;
    }

    public void setInvokeHandler(URoInvokeHandler invokeHandler) {
        this.invokeHandler = invokeHandler;
    }

    public URoInvokeHandler getInvokeHandler() {
        return invokeHandler;
    }

    public <T> URoCompletionCallback<T> getCompletionCallback() {
        return completionCallback;
    }

    public void setCompletionCallback(URoCompletionCallback completionCallback) {
        this.completionCallback = completionCallback;
    }

    public boolean invoke() {
        return sendToTarget(invokeHandler);
    }

    public boolean sendToTarget(URoInvokeHandler invokeHandler) {
        if(invokeHandler != null) {
            URoInvocation invocation = this.copy();
            URoError error = invokeHandler.invoke(invocation);
            if(!error.isSuccess()) {
                URoCompletionCallbackHelper.sendErrorCallback(error, completionCallback);
            }
            return error.isSuccess();
        } else {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.NO_TARGET, completionCallback);
        }
        return false;
    }

}
