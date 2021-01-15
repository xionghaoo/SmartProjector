package com.ubtedu.deviceconnect.libs.base.invocation;

import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public interface URoInvokeHandler {

    URoError invoke(URoInvocation invocation);

}
