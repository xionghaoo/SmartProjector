package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.UroComplexItemResult;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartCommand;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartProtocolHandler;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevComplexGet;

import java.util.ArrayList;
import java.util.List;

/**
 * @CmdId 3501
 * @FunctionName intelligent_dev_complex_get
 * @Description 复合获取指定传感器信息
 * @FileName URoUkitSmartIntelligentDevComplexSetFormatter.java
 **/
public class URoUkitSmartIntelligentDevComplexGetFormatter extends URoUkitSmartCommandFormatter<List<UroComplexItemResult>> {

    private URoUkitSmartIntelligentDevComplexGetFormatter(){}

    public static final URoUkitSmartIntelligentDevComplexGetFormatter INSTANCE = new URoUkitSmartIntelligentDevComplexGetFormatter();

    @Override
    public URoResponse<List<UroComplexItemResult>> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevComplexGet.IntelligentDevComplexGetResponse data;
        List<UroComplexItemResult> responseList = new ArrayList<>();
        try {
            data = PbIntelligentDevComplexGet.IntelligentDevComplexGetResponse.parseFrom(bizData);
            if (data != null) {
                List<PbIntelligentDevComplexGet.IntelligentDevComplexGetResponse.dev_info_t> list = data.getDevInfoList();
                for (int i = 0; i < list.size(); i++) {
                    PbIntelligentDevComplexGet.IntelligentDevComplexGetResponse.dev_info_t item = list.get(i);
                    ByteString itemData = item.getData();
                    byte[] dataArray = itemData != null ? itemData.toByteArray() : null;
                    URoUkitSmartCommandFormatter formatter = (URoUkitSmartCommandFormatter) URoUkitSmartProtocolHandler.conversionCommandFormatter(URoUkitSmartProtocolHandler.conversionCommandType(item.getDev(), item.getCmd()));
                    if (formatter != null && dataArray != null) {
                        URoResponse itemResponse = formatter.decodeResponseMessage(item.getAck(), dataArray, null);
                        responseList.add(UroComplexItemResult.newInstance(itemResponse.getData(), item.getId(), item.getAck(), item.getCmd()));
                    }
                }
            }
        } catch (InvalidProtocolBufferException ignored) {
        }
        return URoResponse.newInstance(success, null, rawResponse, responseList);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        ArrayList<URoRequest> requestArray = new ArrayList<>(request.getParameter("requestArray", null));
        PbIntelligentDevComplexGet.IntelligentDevComplexGetRequest.Builder complexGetRequest = PbIntelligentDevComplexGet.IntelligentDevComplexGetRequest.newBuilder();
        for (int i = 0; i < requestArray.size(); i++) {
            URoRequest innerRequest = requestArray.get(i);
            PbIntelligentDevComplexGet.IntelligentDevComplexGetRequest.dev_info_t.Builder builder = PbIntelligentDevComplexGet.IntelligentDevComplexGetRequest.dev_info_t.newBuilder();
            URoUkitSmartCommand cmd = (URoUkitSmartCommand) innerRequest.getCmd();
            builder.setCmd(cmd.getCmd());
            builder.setDev(cmd.getDev());
            builder.setId((int) innerRequest.getParameter("id", -1));
            URoUkitSmartCommandFormatter formatter = (URoUkitSmartCommandFormatter) URoUkitSmartProtocolHandler.conversionCommandFormatter(cmd);
            byte[] bytes = formatter.encodeRequestMessage(innerRequest);
            builder.setData(ByteString.copyFrom(bytes));
            complexGetRequest.addDevInfo(builder.build());
        }
        return complexGetRequest.build().toByteArray();
    }
}
