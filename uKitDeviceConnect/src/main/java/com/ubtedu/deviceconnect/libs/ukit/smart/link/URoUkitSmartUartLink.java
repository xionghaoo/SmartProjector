package com.ubtedu.deviceconnect.libs.ukit.smart.link;

import androidx.annotation.NonNull;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ubtedu.deviceconnect.libs.base.link.URoUsbDeviceStream;
import com.ubtedu.deviceconnect.libs.base.link.URoUsbDeviceStreamLink;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitSmartUartLink extends URoUsbDeviceStreamLink {

    public URoUkitSmartUartLink(@NonNull URoLinkModel model, @NonNull UsbSerialPort serialPort) throws Exception {
        super(model, new URoUsbDeviceStream(serialPort));
    }

}