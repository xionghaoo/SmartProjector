package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.UroComplexItemResult;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartCommand;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartProtocolHandler;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevComplexSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @CmdId 3500
 * @FunctionName intelligent_dev_complex_set
 * @Description 复合设置指定传感器信息
 * @FileName URoUkitSmartIntelligentDevComplexSetFormatter.java
 **/
public class URoUkitSmartIntelligentDevComplexSetFormatter extends URoUkitSmartCommandFormatter<List<UroComplexItemResult>> {

    private URoUkitSmartIntelligentDevComplexSetFormatter(){}

    public static final URoUkitSmartIntelligentDevComplexSetFormatter INSTANCE = new URoUkitSmartIntelligentDevComplexSetFormatter();

    @Override
    public URoResponse<List<UroComplexItemResult>> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevComplexSet.IntelligentDevComplexSetResponse data;
        List<UroComplexItemResult> responseList = new ArrayList<>();
        try {
            data = PbIntelligentDevComplexSet.IntelligentDevComplexSetResponse.parseFrom(bizData);
            if (data != null) {
                List<PbIntelligentDevComplexSet.IntelligentDevComplexSetResponse.dev_info_t> list = data.getDevInfoList();
                for (int i = 0; i < list.size(); i++) {
                    PbIntelligentDevComplexSet.IntelligentDevComplexSetResponse.dev_info_t item = list.get(i);
                    responseList.add(UroComplexItemResult.newInstance(null,item.getId(), item.getAck(), item.getCmd()));
                }
            }
        } catch (InvalidProtocolBufferException ignored) {
        }
        return URoResponse.newInstance(success, null, rawResponse, responseList);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        ArrayList<URoRequest> requestArray = new ArrayList<>(request.getParameter("requestArray", null));
        PbIntelligentDevComplexSet.IntelligentDevComplexSetRequest.Builder complexSetRequest = PbIntelligentDevComplexSet.IntelligentDevComplexSetRequest.newBuilder();
        for (int i = 0; i < requestArray.size(); i++) {
            URoRequest innerRequest = requestArray.get(i);
            PbIntelligentDevComplexSet.IntelligentDevComplexSetRequest.dev_info_t.Builder builder = PbIntelligentDevComplexSet.IntelligentDevComplexSetRequest.dev_info_t.newBuilder();
            URoUkitSmartCommand cmd = (URoUkitSmartCommand) innerRequest.getCmd();
            builder.setCmd(cmd.getCmd());
            builder.setDev(cmd.getDev());
            builder.setId((int) innerRequest.getParameter("id", -1));
            URoUkitSmartCommandFormatter formatter = (URoUkitSmartCommandFormatter) URoUkitSmartProtocolHandler.conversionCommandFormatter(cmd);
            byte[] bytes = formatter.encodeRequestMessage(innerRequest);
            builder.setData(ByteString.copyFrom(bytes));
            complexSetRequest.addDevInfo(builder.build());
        }
        return complexSetRequest.build().toByteArray();
    }

}
