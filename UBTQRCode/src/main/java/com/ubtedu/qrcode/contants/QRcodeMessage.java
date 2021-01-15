package com.ubtedu.qrcode.contants;

/**
 * Created by qinicy on 2017/5/27.
 */

public interface QRcodeMessage {
    int auto_focus = 0x01;
    int decode = 0x02;
    int decode_failed = 0x03;
    int decode_succeeded = 0x04;
    int encode_failed = 0x05;
    int encode_succeeded = 0x06;
    int quit = 0x07;
    int restart_preview = 0x08;
}
