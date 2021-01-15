package com.ubtedu.ukit.bluetooth.peripheral.setting.modifyid;


import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;

/**
 * @Author naOKi
 * @Date 2018/12/20
 **/
public interface PeripheralSettingModifyIdContracts {
    abstract class UI extends BaseUI<Presenter> {
        public abstract void updateBoardInfo(URoMainBoardInfo data);

        public abstract void refreshUI();

        public abstract void showRefreshView();

        public abstract void hideRefreshView();

        public abstract void updateConnectStatus(boolean isConnected);

        public abstract void updateModifyResult(boolean isSuccess, boolean isSteeringGear, URoComponentType type, int sourceId, int targetId);
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void init();

        public abstract void reloadBoardInfo();

        public abstract void disconnect();

        public abstract void modifyId(boolean isSteeringGear, URoComponentType type, int sourceId, int targetId);
    }
}
