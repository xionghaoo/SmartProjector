package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public abstract class URoComponent {

    public static final String INVALID_VERSION = "00000000";

    protected int componentId;
    protected String componentName;
    protected String version;
    protected URoComponentType componentType;
    protected URoProduct product;
    protected boolean changeIdEnable = true;
    protected final ArrayList<URoError> errors = new ArrayList<>();

    public URoComponent(int componentId, String componentName, URoProduct product) {
        this(componentId, componentName, null, product);
    }

    public URoComponent(int componentId, String componentName, String version, URoProduct product) {
        this.componentId = componentId;
        this.componentName = componentName;
        this.version = version == null ? INVALID_VERSION : version;
        this.product = product;
    }

    public int getComponentId() {
        return componentId;
    }

    protected void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getVersion() {
        return version;
    }

    public URoComponentType getComponentType() {
        return componentType;
    }

    public URoProduct getProduct() {
        return product;
    }

    public void setProduct(URoProduct product) {
        this.product = product;
    }

    public boolean isChangeIdEnable() {
        return changeIdEnable;
    }

    public ArrayList<URoError> getErrors() {
        synchronized (errors) {
            return new ArrayList<>(errors);
        }
    }

    public boolean hasError(URoError error) {
        synchronized (errors) {
            return errors.contains(error);
        }
    }

    public void resetErrors() {
        synchronized (errors) {
            errors.clear();
        }
    }

    public void addError(URoError error) {
        synchronized (errors) {
            if (!errors.contains(error)) {
                errors.add(error);
            }
        }
    }

    public boolean isMatch(URoComponentID componentID) {
        return componentID != null
                && componentID.getId() == componentId
                && componentID.getComponentType().equals(getComponentType());
    }

    public boolean changeID(int newId, URoCompletionCallback<Void> callback) {
        if(!changeIdEnable) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.UNKNOWN, callback);
            return false;
        }
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_MODIFY_ID);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, componentType);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID, componentId);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID, newId);
        invocation.setCompletionCallback(callback);
        invocation.sendToTarget(product);
        return true;
    }

    public boolean setEnable(boolean enabled, URoCompletionCallback<Void> callback) {
        URoInvocation invocation=new URoInvocation(URoInvocationNames.INVOCATION_SENSOR_ENABLE);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, componentType);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, enabled);
        invocation.setCompletionCallback(callback);
        invocation.sendToTarget(product);
        return false;
    }

    public boolean check(URoCompletionCallback<Boolean> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_COMPONENT_CHECK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, new URoComponentID(componentType, componentId));
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(product);
    }

    public final void registerEvent() {
//        EventBus.getDefault().register(this);
    }

    public final void unregisterEvent() {
//        EventBus.getDefault().unregister(this);
    }

}
