package com.ubtedu.deviceconnect.libs.base.product.core.queue;

/**
 * @Author naOKi
 * @Date 2018/11/13
 **/
public interface URoRequestFilter {
    boolean accept(URoRequest cmd);
}
