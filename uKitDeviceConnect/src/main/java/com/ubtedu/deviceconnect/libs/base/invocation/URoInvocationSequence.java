package com.ubtedu.deviceconnect.libs.base.invocation;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/09/18
 **/
public class URoInvocationSequence extends URoInvocation {

    private ArrayList<URoInvokeParameter> invokes = new ArrayList<>();

    private boolean loop = false;
    private int loopTime = -1;
    private boolean sendTogether = false;

    public URoInvocationSequence() {
        super(URoInvocationNames.INVOCATION_INVOKE_SEQUENCE);
    }

    protected URoInvocationSequence(URoInvocationSequence other) {
        super(other);
        this.invokes.addAll(other.invokes);
        this.sendTogether = other.sendTogether;
        this.loop = other.loop;
        this.loopTime = other.loopTime;
    }

    @Override
    protected URoInvocationSequence copy() {
        return new URoInvocationSequence(this);
    }

    public ArrayList<URoInvokeParameter> getInvokes() {
        return new ArrayList<>(invokes);
    }

    public boolean isSendTogether() {
        return sendTogether;
    }

    public boolean isLoop() {
        return loop;
    }

    public int loopTime() {
        return loopTime;
    }

    public static abstract class URoInvokeParameter {
        private boolean ignoreFailure = false;
        private long timeout;

        public boolean isIgnoreFailure() {
            return ignoreFailure;
        }

        public URoInvokeParameter setIgnoreFailure(boolean ignoreFailure) {
            this.ignoreFailure = ignoreFailure;
            return this;
        }

        public long getTimeout() {
            return timeout;
        }

        public URoInvokeParameter setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }
    }

    public static class URoInvocationParameter extends URoInvokeParameter {
        private URoInvocation invocation;

        public URoInvocationParameter(URoInvocation invocation) {
            this.invocation = invocation;
        }

        public URoInvocation getInvocation() {
            return invocation;
        }
    }

    public static class URoSleepParameter extends URoInvokeParameter {
        private long timeMs;

        public URoSleepParameter(long timeMs) {
            this.timeMs = timeMs;
        }

        public long getTimeMs() {
            return timeMs;
        }
    }

    public static class URoDelayParameter extends URoInvokeParameter {
        private long timeMs;

        public URoDelayParameter(long timeMs) {
            this.timeMs = timeMs;
        }

        public long getTimeMs() {
            return timeMs;
        }
    }

    public URoInvocationSequence sleep(long timeMs) {
        return sleep(timeMs, false, 0);
    }

    public URoInvocationSequence sleep(long timeMs, boolean ignoreFailure) {
        return sleep(timeMs, ignoreFailure, 0);
    }

    public URoInvocationSequence sleep(long timeMs, boolean ignoreFailure, long timeout) {
        invokes.add(new URoSleepParameter(Math.max(timeMs, 0)).setIgnoreFailure(ignoreFailure).setTimeout(timeout));
        return this;
    }

    public URoInvocationSequence delay(long timeMs) {
        return delay(timeMs, false, 0);
    }

    public URoInvocationSequence delay(long timeMs, boolean ignoreFailure) {
        return delay(timeMs, ignoreFailure, 0);
    }

    public URoInvocationSequence delay(long timeMs, boolean ignoreFailure, long timeout) {
        invokes.add(new URoDelayParameter(Math.max(timeMs, 0)).setIgnoreFailure(ignoreFailure).setTimeout(timeout));
        return this;
    }

    public URoInvocationSequence action(URoInvocation invocation) {
        return action(invocation, false, 0);
    }

    public URoInvocationSequence action(URoInvocation invocation, boolean ignoreFailure) {
        return action(invocation, ignoreFailure, 0);
    }

    public URoInvocationSequence action(URoInvocation invocation, boolean ignoreFailure, long timeout) {
        if(invocation != null) {
            invokes.add(new URoInvocationParameter(invocation).setIgnoreFailure(ignoreFailure).setTimeout(timeout));
        }
        return this;
    }

    public URoInvocationSequence loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public URoInvocationSequence loopTime(int loopTime) {
        this.loopTime = loopTime;
        return this;
    }

    public URoInvocationSequence sendTogether(boolean sendTogether) {
        this.sendTogether = sendTogether;
        return this;
    }

}
