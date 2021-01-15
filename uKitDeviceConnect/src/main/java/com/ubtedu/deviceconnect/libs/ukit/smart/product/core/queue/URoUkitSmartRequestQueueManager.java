package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.queue;

import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoDeviceReceivedMessage;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestQueueManager;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartProtocolHandler;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMessageHeader;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartProtocolHandler.HEAD;

/**
 * @Author naOKi
 * @Date 2019/06/14
 **/
public class URoUkitSmartRequestQueueManager extends URoRequestQueueManager {

    private final boolean isUartCom;
    private ByteArrayOutputStream invalidDataBaos = null;

    public URoUkitSmartRequestQueueManager(URoLinkModel linkModel, URoProtocolHandler protocolHandler) {
        super(linkModel, protocolHandler);
        isUartCom = linkModel != null && URoLinkType.COM.equals(linkModel.linkType);
        if(isUartCom) {
            invalidDataBaos = new ByteArrayOutputStream();
        }
    }

    @Override
    protected long minimizeSendInterval() {
        return 0;
    }

    @Override
    protected long minReceivedTimeout() {
        return 50L;
    }

    @Override
    protected long maxReceivedTimeout() {
        return 30000L;
    }

    @Override
    protected void resetQueue() {
        super.resetQueue();
        if(isUartCom && invalidDataBaos != null) {
            invalidDataBaos.reset();
        }
    }

    @Override
    protected URoCommandRequestResult handleUKitCommandRequest(URoRequest request) throws Exception {
        byte[] requestData = encodeRequestData(request);
        if(requestData == null) {
            return REQUEST_FAILURE_RESULT;
        }
        write(requestData);
        return new URoCommandRequestResult(true, true, false, request.getKey());
    }

    private final static String COLOR_PATTERN = "\\033\\[([01];3[0-9]|0)m";
    private final static String WARNING_PATTERN = "W (";
    private final static String INFO_PATTERN = "I (";
    private final static String ERROR_PATTERN = "E (";
    private void printUkitBoardLog(String content) {
        if(!TextUtils.isEmpty(content)) {
            String newContent = content.replaceAll(COLOR_PATTERN, "");
            if(!TextUtils.equals(newContent, content)) {
                content = newContent.trim();
                if (content.startsWith(INFO_PATTERN)) {
                    content = content.substring(2);
                    URoLogUtils.i("[uKitBoard] %s", content);
                    return;
                } else if (content.startsWith(ERROR_PATTERN)) {
                    content = content.substring(2);
                    URoLogUtils.e("[uKitBoard] %s", content);
                    return;
                } else if (content.startsWith(WARNING_PATTERN)) {
                    content = content.substring(2);
                    URoLogUtils.w("[uKitBoard] %s", content);
                    return;
                }
            }
        }
        URoLogUtils.d("[uKitBoard] %s", content);
    }

    @Override
    protected URoDeviceReceivedMessage handleUKitCommandResponse() throws Exception {
        byte[] head = new byte[1];
        byte[] headLength = new byte[1];
        byte[] headData = null;
        byte[] headCrc8 = new byte[1];
        byte[] bizData = null;
        byte[] bizCrc8 = new byte[1];
        byte[] rawResponse = null;
        PbMessageHeader.Header header = null;
        do {
            if (read(head) != head.length) {
                URoLogUtils.e("Receive head failure");
                break;
            }
            if (!Arrays.equals(head, HEAD)) {
                if(isUartCom) {
                    if(head[0] == '\n') {
                        if(invalidDataBaos.size() != 0) {
//                            printByteData("xxxx", " ---- ", invalidDataBaos.toByteArray());
                            String a = new String(invalidDataBaos.toByteArray());
                            printUkitBoardLog(new String(invalidDataBaos.toByteArray()));
//                            printByteData("xxxx1", " ---- ", a.getBytes());
                            invalidDataBaos.reset();
                        }
                    } else {
                        invalidDataBaos.write(head);
                    }
                } else {
                    URoLogUtils.e("Skip invalid head data: %02X", head[0]);
                }
                break;
            } else {
                if(isUartCom) {
                    if(invalidDataBaos.size() != 0) {
                        printUkitBoardLog(new String(invalidDataBaos.toByteArray()));
                        invalidDataBaos.reset();
                    }
                }
            }
            if (read(headLength) != headLength.length) {
                URoLogUtils.e("Receive headLength failure");
                break;
            }
            headData = new byte[headLength[0]];
            if (read(headData) != headData.length) {
                URoLogUtils.e("Receive headData failure");
                break;
            }
            if (read(headCrc8) != headCrc8.length) {
                URoLogUtils.e("Receive headCrc8 failure");
                break;
            }
            byte headDataChecksum = URoUkitSmartProtocolHandler.checksum(headData);
            if (headCrc8[0] != headDataChecksum) {
                URoProtocolHandler.printByteData("Invalid data received:", " <<<<< ", head, headLength, headData, headCrc8);
                URoLogUtils.e("Not match checksum: %02X != %02X", headCrc8[0], headDataChecksum);
                break;
            }
            header = PbMessageHeader.Header.parseFrom(headData);
            if(header.getDataLen() == 0) {
                bizData = new byte[0];
                rawResponse = URoProtocolHandler.arrayJoin(head, headLength, headData, headCrc8);
            } else {
                bizData = new byte[header.getDataLen()];
                if (read(bizData) != bizData.length) {
                    URoLogUtils.e("Receive bizData failure");
                    break;
                }
                if (read(bizCrc8) != bizCrc8.length) {
                    URoLogUtils.e("Receive bizCrc8 failure");
                    break;
                }
                byte bizDataChecksum = URoUkitSmartProtocolHandler.checksum(bizData);
                if (bizCrc8[0] != bizDataChecksum) {
                    URoProtocolHandler.printByteData("Invalid data received:", " <<<<< ", head, headLength, headData, headCrc8, bizData, bizCrc8);
                    URoLogUtils.e("Not match checksum: %02X != %02X", bizCrc8[0], bizDataChecksum);
                    break;
                }
                rawResponse = URoProtocolHandler.arrayJoin(head, headLength, headData, headCrc8, bizData, bizCrc8);
            }
            break;
        } while(true);
        URoDeviceReceivedMessage result = null;
        if(rawResponse != null) {
            String identity = String.valueOf(header.getSeq());
            int errorCode = header.getAck();
            URoCommand cmd = URoUkitSmartProtocolHandler.conversionCommandType(header.getDev(), header.getCmd());
            result = new URoDeviceReceivedMessage(cmd, errorCode, identity, bizData, rawResponse, System.currentTimeMillis());
            result.setExtraData(header);
        }
        return result;
    }
}
