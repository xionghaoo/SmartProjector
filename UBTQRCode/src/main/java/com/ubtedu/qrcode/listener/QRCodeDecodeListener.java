package com.ubtedu.qrcode.listener;

/**
 * Created by qinicy on 2017/5/27.
 */

public interface QRCodeDecodeListener {
    void onDecode(String result);
    void onFail(String message);
}
