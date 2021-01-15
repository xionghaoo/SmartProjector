package com.ubtedu.deviceconnect.libs.base.connector;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoConnectInfo;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoConnector<C extends URoConnectInfo> {

    protected URoConnectorDelegate delegate;
    protected URoConnectorStatus status;

    public abstract boolean isSearching();
    public abstract void startSearch(URoDiscoveryCallback<C> callback, long timeoutMs);
    public abstract void stopSearch();

    protected abstract void connect(C connectItem, long timeout, URoCompletionCallback<Void> callback);
    protected abstract boolean connectReset();


}
