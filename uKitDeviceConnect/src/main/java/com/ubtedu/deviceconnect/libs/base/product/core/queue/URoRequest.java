package com.ubtedu.deviceconnect.libs.base.product.core.queue;

import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvokeParameters;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;

/**
 * @Author naOKi
 **/
public class URoRequest extends URoInvokeParameters {

    private boolean abort = false;

    public void abort() {
        abort = true;
    }

    public boolean isAborted() {
        return abort;
    }

    private URoCommand cmd;
    private String key;
    private byte[] preBuildEncodeRequestData;

    public URoRequest(URoCommand cmd) {
        this.cmd = cmd;
    }

    public URoRequest(URoCommand cmd, URoInvocation invocation) {
        super(invocation != null ? invocation.getParameters() : null);
        this.cmd = cmd;
    }

    public URoCommand getCmd() {
        return cmd;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public byte[] getPreBuildEncodeRequestData() {
        synchronized (this) {
            return preBuildEncodeRequestData;
        }
    }

    private void setPreBuildEncodeRequestData(byte[] preBuildEncodeRequestData) {
        synchronized (this) {
            this.preBuildEncodeRequestData = preBuildEncodeRequestData;
        }
    }

    public boolean prepare(URoProtocolHandler protocolHandler, int sequenceId) throws Exception {
        synchronized (this) {
            if(preBuildEncodeRequestData == null) {
                byte[] encodeRequestData = protocolHandler.encodeRequestMessage(this, sequenceId);
                setPreBuildEncodeRequestData(encodeRequestData);
            }
            return preBuildEncodeRequestData != null;
        }
    }

}
