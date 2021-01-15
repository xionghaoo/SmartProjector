package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileDownloadProgressQuery;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 260
 * @FunctionName：file_download_progress_query
 * @Description：文件下载进度查询
 * @FileName: URoUkitSmartFileDownloadProgressQueryFormatter.java
 * 
 **/
public class URoUkitSmartFileDownloadProgressQueryFormatter extends URoUkitSmartCommandFormatter<PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse> {

    private URoUkitSmartFileDownloadProgressQueryFormatter(){}

    public static final URoUkitSmartFileDownloadProgressQueryFormatter INSTANCE = new URoUkitSmartFileDownloadProgressQueryFormatter();

    @Override
    public URoResponse<PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse data;
        try {
            PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse response = PbFileDownloadProgressQuery.FileDownloadProgressQueryResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
