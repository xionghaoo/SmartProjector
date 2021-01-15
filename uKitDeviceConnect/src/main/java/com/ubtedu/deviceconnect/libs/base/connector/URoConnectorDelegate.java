package com.ubtedu.deviceconnect.libs.base.connector;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public interface URoConnectorDelegate {
    void onConnectorStatusChange(URoConnector connector, URoConnectorStatus status);
}
