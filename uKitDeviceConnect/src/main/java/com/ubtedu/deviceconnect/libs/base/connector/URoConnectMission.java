package com.ubtedu.deviceconnect.libs.base.connector;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.mission.URoMission;
import com.ubtedu.deviceconnect.libs.base.model.URoConnectInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.concurrent.Callable;

/**
 * @Author naOKi
 * @Date 2019/08/14
 **/
public abstract class URoConnectMission<C extends URoConnectInfo, T> extends URoMission<T> {

    private C connectItem;
    private long timeout = 0;
    private int retryTimes = 0;

    public URoConnectMission(C connectItem) {
        this(connectItem, null);
    }

    public URoConnectMission(C connectItem, @NonNull URoCompletionCallback<T> callback) {
        super(callback);
        this.connectItem = connectItem;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    protected abstract T connect(C connectItem) throws Exception;
    protected abstract void disconnect(T connection);

    @Override
    protected final void onMissionStart() throws Throwable {
        URoLogUtils.d("URoConnectMission: Start");
        Callable<URoCompletionResult<T>> firstAction = new Callable<URoCompletionResult<T>>() {
            @Override
            public URoCompletionResult<T> call() throws Exception {
                T connection = connect(connectItem);
                if(isStopped()) {
                    disconnect(connection);
                    connection = null;
                }
                return new URoCompletionResult<>(connection, URoError.SUCCESS);
            }
        };
        performNext(firstAction, timeout, retryTimes);
    }

    @Override
    protected final void onMissionRelease() {
        URoLogUtils.d("URoConnectMission: Complete");
    }

    @Override
    protected final void onPreviousResult(URoCompletionResult result) throws Throwable {
        if(result.isSuccess()) {
            notifyComplete((T)result.getData());
        } else {
            notifyError(result.getError());
        }
    }

}
