package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPartDownloadProgressQuery;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/08/28
 * @CmdId 208
 * @FunctionName part_download_progress_query
 * @Description 部件下载进度查询
 * @FileName URoUkitSmartPartDownloadProgressQueryFormatter.java
 * 
 **/
 public class URoUkitSmartPartDownloadProgressQueryFormatter extends URoUkitSmartCommandFormatter<PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse> {

    private URoUkitSmartPartDownloadProgressQueryFormatter(){}

    public static final URoUkitSmartPartDownloadProgressQueryFormatter INSTANCE = new URoUkitSmartPartDownloadProgressQueryFormatter();

    @Override
    public URoResponse<PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse data;
        try {
            PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse response = PbPartDownloadProgressQuery.PartDownloadProgressQueryResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbPartDownloadProgressQuery.PartDownloadProgressQueryRequest.Builder builder = PbPartDownloadProgressQuery.PartDownloadProgressQueryRequest.newBuilder();
        builder.setPart(request.getParameter("part", 0));
        return builder.build().toByteArray();
    }

}
