package com.ubtedu.deviceconnect.libs.base.mission;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackWrap;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public class URoSequenceMission<T> extends URoCommandMission<T> {

    private ArrayList<URoInvocationSequence.URoInvokeParameter> invokes;

    private ArrayList<URoInvocationSequence.URoInvocationParameter> expects;
    private HashMap<URoInvocationSequence.URoInvocationParameter, URoCompletionResult> expectResults;

    private boolean loop;
    private int loopTime;
    private boolean sendTogether;

    private long actionTimeMs;

    public URoSequenceMission(@NonNull URoProduct product,
              ArrayList<URoInvocationSequence.URoInvokeParameter> invokes,
              boolean loop,
              int loopTime,
              boolean sendTogether) {
        super(product);
        this.invokes = invokes;
        this.loop = loop;
        this.loopTime = loopTime;
        this.sendTogether = sendTogether;
    }

    private URoInvocationSequence.URoInvokeParameter getCurrentParameter() {
        if(invokes == null || invokes.size() == 0) {
            return null;
        }
        int currentStep = getCurrentStepRound();
        if(currentStep < invokes.size()) {
            return invokes.get(currentStep);
        } else {
            return null;
        }
    }

    private URoInvocationSequence.URoInvokeParameter getNextParameter() {
        if(invokes == null || invokes.size() == 0) {
            return null;
        }
        int currentStep = getCurrentStepRound();
        int index;
        if(loop) {
            if (loopTime <= 0) {
                index = (currentStep + 1) % invokes.size();
            } else {
                int currLoopTime = (getCurrentStep() + 1) / invokes.size();
                if (currLoopTime < loopTime) {
                    index = (currentStep + 1) % invokes.size();
                } else {
                    return null;
                }
            }
        } else {
            index = currentStep + 1;
        }
        if(index < invokes.size()) {
            return invokes.get(index);
        } else {
            return null;
        }
    }

    private void performNext() {
        URoInvocationSequence.URoInvokeParameter nextAction = getNextParameter();
        if(nextAction == null) {
            notifyComplete(null);
            return;
        }
        long lastActionTimeMs = actionTimeMs;
        actionTimeMs = System.currentTimeMillis();
        if(nextAction instanceof URoInvocationSequence.URoInvocationParameter) {
            URoInvocation invocation = ((URoInvocationSequence.URoInvocationParameter) nextAction).getInvocation();
            performNext(invocation.getInvocationName(), invocation);
        } else if(nextAction instanceof URoInvocationSequence.URoSleepParameter) {
            long timeMs = ((URoInvocationSequence.URoSleepParameter) nextAction).getTimeMs();
            performNext(new URoDelayAction(System.currentTimeMillis(), timeMs));
        } else if(nextAction instanceof URoInvocationSequence.URoDelayParameter) {
            long timeMs = ((URoInvocationSequence.URoDelayParameter) nextAction).getTimeMs();
            performNext(new URoDelayAction(lastActionTimeMs, timeMs));
        } else {
            notifyError(URoError.INVALID);
        }
    }

    @Override
    protected final void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        // do nothing
    }

    @Override
    protected void onPreviousResult(URoCompletionResult result) throws Throwable {
        // do nothing
    }

    @Override
    protected void onPerformComplete(URoCompletionResult result) {
        resetTimeout(0);
        if(result.isSuccess()) {
            performNext();
        } else {
            URoInvocationSequence.URoInvokeParameter parameter = getCurrentParameter();
            if(parameter != null && parameter.isIgnoreFailure()) {
                performNext();
            } else {
                notifyError(result.getError());
            }
        }
    }

    private void sendInvocationTogether() throws Throwable {
        if(invokes.isEmpty()) {
            notifyComplete(null);
            return;
        }
        expects = new ArrayList<>();
        for(URoInvocationSequence.URoInvokeParameter invoke : invokes) {
            if(invoke instanceof URoInvocationSequence.URoInvocationParameter) {
                URoInvocationSequence.URoInvocationParameter invocationParameter = (URoInvocationSequence.URoInvocationParameter) invoke;
                if(!expects.contains(invocationParameter)) {
                    expects.add(invocationParameter);
                }
            }
        }
        if(expects.isEmpty()) {
            notifyComplete(null);
            return;
        }
        expectResults = new HashMap<>();
        for(URoInvocationSequence.URoInvocationParameter invocationParameter : expects) {
            URoInvocation invocation = invocationParameter.getInvocation();
            invocation.setCompletionCallback(new URoCompletionCallbackWrap(invocation.getCompletionCallback()) {
                @Override
                public boolean onCompleteInternal(URoCompletionResult result) {
                    if(expects.contains(invocationParameter)) {
                        expects.remove(invocationParameter);
                        expectResults.put(invocationParameter, result);
                        if(expects.isEmpty()) {
                            boolean hasError = false;
                            for(URoCompletionResult v : expectResults.values()) {
                                if(!v.isSuccess()) {
                                    hasError = true;
                                    break;
                                }
                            }
                            if(!hasError) {
                                sendCallbackDirectly(result);
                                notifyComplete(null);
                            } else {
                                notifyError(URoError.UNKNOWN);
                            }
                        }
                    }
                    return result.isSuccess();
                }
            });
            sendRequestDirectly(invocation);
        }
    }

    @Override
    protected void onMissionStart() throws Throwable {
        if(sendTogether) {
            sendInvocationTogether();
        } else {
            setTotalStep(invokes.size());
            performNext();
        }
    }

    private class URoDelayAction implements Callable<URoCompletionResult<Void>> {

        private long baseTimeMs;
        private long delayTimeMs;

        public URoDelayAction(long baseTimeMs, long delayTimeMs) {
            this.baseTimeMs = baseTimeMs;
            this.delayTimeMs = delayTimeMs;
        }

        @Override
        public URoCompletionResult<Void> call() throws Exception {
            long currTimeMs = System.currentTimeMillis();
            long sleepTimeMs = baseTimeMs + delayTimeMs - currTimeMs;
            if(sleepTimeMs > 0) {
                Thread.sleep(sleepTimeMs);
            }
            return new URoCompletionResult<>(null, URoError.SUCCESS);
        }

    }

}
