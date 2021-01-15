package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevRecordingFileInfo;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 3001
 * @FunctionName intelligent_dev_recording_file_info
 * @Description 获取录音文件列表
 * @FileName URoUkitSmartIntelligentDevRecordingFileInfoFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevRecordingFileInfoFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoResponse> {

    private URoUkitSmartIntelligentDevRecordingFileInfoFormatter(){}

    public static final URoUkitSmartIntelligentDevRecordingFileInfoFormatter INSTANCE = new URoUkitSmartIntelligentDevRecordingFileInfoFormatter();

    @Override
    public URoResponse<PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoResponse data;
        try {
            PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoResponse response = PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoRequest.Builder builder = PbIntelligentDevRecordingFileInfo.IntelligentDevRecordingFileInfoRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
