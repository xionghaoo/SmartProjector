package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.queue;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoDeviceReceivedMessage;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestQueueManager;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.URoUkitLegacyProtocolHandler;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.Arrays;

import static com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.URoUkitLegacyProtocolHandler.HEAD1;
import static com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.URoUkitLegacyProtocolHandler.HEAD2;
import static com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.URoUkitLegacyProtocolHandler.TAIL;

/**
 * @Author naOKi
 * @Date 2019/06/14
 **/
public class URoUkitLegacyRequestQueueManager extends URoRequestQueueManager {

    public URoUkitLegacyRequestQueueManager(URoLinkModel linkModel, URoProtocolHandler protocolHandler) {
        super(linkModel, protocolHandler);
    }

    @Override
    protected long minimizeSendInterval() {
        return 50;
    }

    @Override
    protected long minReceivedTimeout() {
        return 50;
    }

    @Override
    protected long maxReceivedTimeout() {
        return 10000;
    }

    @Override
    protected URoCommandRequestResult handleUKitCommandRequest(URoRequest request) throws Exception {
        byte[] requestData = encodeRequestData(request);
        if(requestData == null) {
            return REQUEST_FAILURE_RESULT;
        }
        write(requestData);
        return new URoCommandRequestResult(true, true, false, request.getCmd().getName());
    }

    @Override
    protected URoDeviceReceivedMessage handleUKitCommandResponse() throws Exception {
        byte[] head1 = new byte[1];
        byte[] head2 = new byte[1];
        byte[] length = new byte[1];
        byte[] cmdCode = new byte[1];
        byte[] bizData = null;
        byte[] checksum = new byte[1];
        byte[] tail = new byte[1];
        byte[] rawResponse = null;
        do {
            if (Arrays.equals(head2, HEAD1)) {
                head1[0] = head2[0];
                URoLogUtils.e("Set head1 = %02X, when head2 = %02X", head1[0], head2[0]);
            } else {
                if (read(head1) != head1.length) {
                    URoLogUtils.e("Receive head1 failure");
                    break;
                }
            }
            if (read(head2) != head2.length) {
                URoLogUtils.e("Receive head2 failure");
                break;
            }
            if (!Arrays.equals(head1, HEAD1) || !Arrays.equals(head2, HEAD2)) {
                URoLogUtils.e("Skip invalid head data: %02X %02X", head1[0], head2[0]);
                continue;
            }
            if (read(length) != length.length) {
                URoLogUtils.e("Receive length failure");
                break;
            }
            if (read(cmdCode) != cmdCode.length) {
                URoLogUtils.e("Receive cmdCode failure");
                break;
            }
            int bizDataLength = Math.min(URoUkitLegacyProtocolHandler.byte2Int(length[0]) - 5, 1000);
            bizData = new byte[bizDataLength];
            if (read(bizData) != bizData.length) {
                URoLogUtils.e("Receive bizData failure");
                break;
            }
            if (read(checksum) != checksum.length) {
                URoLogUtils.e("Receive checksum failure");
                break;
            }
            if (read(tail) != tail.length) {
                URoLogUtils.e("Receive tail failure");
                break;
            }
            byte destChecksum = URoUkitLegacyProtocolHandler.checksum(length[0], cmdCode[0], bizData);
            if (checksum[0] != destChecksum) {
                URoProtocolHandler.printByteData("Invalid data received:", " <<<<< ", head1, head2, length, cmdCode, bizData, checksum, tail);
                URoLogUtils.e("Not match checksum: %02X != %02X", checksum[0], destChecksum);
                break;
            }
            if (!Arrays.equals(tail, TAIL)) {
                URoProtocolHandler.printByteData("Invalid data received:", " <<<<< ", head1, head2, length, cmdCode, bizData, checksum, tail);
                URoLogUtils.e("Invalid tail data: %02X", tail[0]);
                break;
            }
            rawResponse = URoProtocolHandler.arrayJoin(head1, head2, length, cmdCode, bizData, checksum, tail);
            break;
        } while(true);
        URoDeviceReceivedMessage result = null;
        if(rawResponse != null) {
            URoCommand cmd = URoUkitLegacyProtocolHandler.conversionCommandType(cmdCode[0]);
            result = new URoDeviceReceivedMessage(cmd, null, bizData, rawResponse, System.currentTimeMillis());
        }
        return result;
    }

}
