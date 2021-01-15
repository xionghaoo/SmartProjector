/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.about;

import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.utils.ResourceUtils;
import com.ubtedu.ukit.project.blockly.BlocklyPresenter;
import com.ubtedu.ukit.user.gdpr.GdprContracts;
import com.ubtedu.ukit.user.gdpr.GdprHelper;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

import java.util.List;

/**
 * @author qinicy
 * @date 2018/12/19
 */
public class MenuAboutPresenter extends MenuAboutContracts.Presenter {

    private String mBlocklyVersion;

    private GdprHelper mGdprHelper;

    public MenuAboutPresenter() {

    }


    @Override
    public void initGdprs(){
        mGdprHelper = new GdprHelper(mContext, mGdprContracts);
    }
    @Override
    public String getBlocklyVersion() {
        if (TextUtils.isEmpty(mBlocklyVersion)) {
            String sdcardVersion = readSdcardBlocklyVersionCode();
            String assetsVersion = readAssetsBlocklyVersionCode();
            if (StringUtil.getNumberFromString(sdcardVersion) > StringUtil.getNumberFromString(assetsVersion)) {
                mBlocklyVersion = sdcardVersion;
            } else {
                mBlocklyVersion = assetsVersion;
            }
        }
        if (mBlocklyVersion != null){
            mBlocklyVersion = mBlocklyVersion.trim();
        }
        return mBlocklyVersion;
    }

    @Override
    public void toShowGdprPact(int type) {
        mGdprHelper.showGdprPacts(type);
    }


    private String readAssetsBlocklyVersionCode() {
        return ResourceUtils.readAssets2String(UKitApplication.getInstance(), BlocklyPresenter.BLOCKLY_ASSET_VERSION, "utf-8");
    }

    private String readSdcardBlocklyVersionCode() {
        return FileHelper.readTxtFile(BlocklyPresenter.SDCARD_BLOCKLY_VERSION);
    }


    @Override
    public void release() {
        if (mGdprHelper!=null) {
            mGdprHelper.release();
        }
        super.release();
    }

    private GdprContracts mGdprContracts = new GdprContracts() {
        @Override
        public void showLoading(boolean isCancelable) {
            if (getView() != null) {
                getView().getUIDelegate().showLoading(isCancelable);
            }
        }

        @Override
        public void hideLoading() {
            if (getView() != null) {
                getView().getUIDelegate().hideLoading();
            }
        }

        @Override
        public void showReadOnlyGdprPactDialog(GdprUserPactInfo info) {
            if (getView() != null) {
                getView().showGdprPactDialog(info.type, info.abstractText, info.url);
            }
        }

        @Override
        public void showInteractiveGdprPacts(List<GdprUserPactInfo> infos) {
            if (getView() != null) {
                getView().showGdprPactDialog(GdprUserPactInfo.GDPR_TYPE_ALL, "", "");
            }
        }


        @Override
        public void toastShort(String string) {
            if (getView() != null) {
                getView().getUIDelegate().toastShort(string);
            }
        }

        @Override
        public void onLoginAcceptGdprPact(boolean success) {

        }
    };
}
