package com.ubtedu.deviceconnect.libs.base.interfaces;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoComponentErrorListener {
    void onReportComponentError(URoProduct product, URoComponentID component, URoError error);
}
