/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.gdpr;


import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/4/4
 **/
public interface GdprContracts {
    void showLoading(boolean isCancelable);
    void hideLoading();
    void showReadOnlyGdprPactDialog(GdprUserPactInfo info);
    void showInteractiveGdprPacts(List<GdprUserPactInfo> infos);

    void toastShort(String string);
    void onLoginAcceptGdprPact(boolean success);
}
