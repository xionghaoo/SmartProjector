package com.ubtedu.deviceconnect.libs.base.invocation;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoInvokeParameters {

    private HashMap<String, Object> parameters;

    public URoInvokeParameters() {
        this.parameters = new HashMap<>();
    }

    public URoInvokeParameters(Map<String, Object> parameters) {
        this();
        if(parameters != null) {
            this.parameters.putAll(parameters);
        }
    }

    public <T> void setParameter(String key, T value) {
        parameters.put(key, value);
    }

    public <T> T getParameter(String key, T defaultValue) {
        try {
            return parameters.containsKey(key) ? (T)parameters.get(key) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void removeParameter(String key) {
        parameters.remove(key);
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    public HashMap<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    public <K, V> void addPairParam(String key, K pair1, V pair2) {
        HashMap<K, V> hashMap = (HashMap<K, V>)parameters.get(key);
        if(hashMap == null) {
            hashMap = new HashMap<>();
            parameters.put(key, hashMap);
        }
        hashMap.put(pair1, pair2);
    }

    public <V> void addListParam(String key, V value) {
        ArrayList<V> list = (ArrayList<V>)parameters.get(key);
        if(list == null) {
            list = new ArrayList<>();
            parameters.put(key, list);
        }
        list.add(value);
    }

    public void setPriority(URoRequestPriority priority) {
        setParameter("priority", priority);
    }

    public URoRequestPriority getPriority() {
        return getParameter("priority", URoRequestPriority.NORMAL);
    }

    public void setTag(String tag) {
        setParameter("tag", tag);
    }

    public String getTag() {
        return getParameter("tag", null);
    }

    public void setTimeoutThreshold(long timeoutThreshold) {
        setParameter("timeoutThreshold", timeoutThreshold);
    }

    public long timeoutThreshold() {
        return getParameter("timeoutThreshold", 10000L);
    }

    public void setSendComponentError(boolean sendComponentError) {
        setParameter("sendComponentError", sendComponentError);
    }

    public boolean isSendComponentError() {
        return getParameter("sendComponentError", true);
    }

    public int getRetry() {
        return getParameter("retry", 0);
    }

    public void setRetry(int retry) {
        setParameter("retry", retry);
    }

}
