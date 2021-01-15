/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.peripheral.setting.modifyid;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;

/**
 * @Author naOKi
 * @Date 2018/12/20
 **/
public class PeripheralSettingModifyIdPresenter extends PeripheralSettingModifyIdContracts.Presenter {

//    private UKitDeviceActionSequence refreshActionSequence = null;
//    private UKitDeviceActionSequence modifyActionSequence = null;

    public PeripheralSettingModifyIdPresenter() {
    }

    @Override
    public void init() {
        PeripheralSettingModifyIdContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.updateBoardInfo(BluetoothHelper.getBoardInfoData());
    }

    @Override
    public void onResume() {
        PeripheralSettingModifyIdContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        if(!BluetoothHelper.isConnected()) {
            return;
        }
        ui.updateConnectStatus(BluetoothHelper.isConnected());
    }

    @Override
    public void disconnect() {
        BluetoothHelper.disconnect();
    }

    @Override
    public void release() {
//        if (refreshActionSequence != null) {
//            refreshActionSequence.abort();
//            refreshActionSequence = null;
//        }
//        if (modifyActionSequence != null) {
//            modifyActionSequence.abort();
//            modifyActionSequence = null;
//        }
        BluetoothHelper.disconnect();
    }

    @Override
    public void reloadBoardInfo() {
        PeripheralSettingModifyIdContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.showRefreshView();
//        if (refreshActionSequence != null && !refreshActionSequence.hasFinish()) {
//            refreshActionSequence.abort();
//        }
//        refreshActionSequence = UKitDeviceActionSequenceFactory.createRefreshBoardInfoActionSequence(new IActionSequenceCallback() {
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                PeripheralSettingModifyIdContracts.UI ui = getView();
//                if (ui == null) {
//                    return;
//                }
//                ui.hideRefreshView();
//                if (isSuccess) {
//                    ui.refreshUI();
//                }
//            }
//        });
//        refreshActionSequence.execute();
        URoInvocation invocation = BtInvocationFactory.readMainboardInfo();
        invocation.setCompletionCallback(new URoCompletionCallback() {
            @Override
            public void onComplete(URoCompletionResult result) {
                PeripheralSettingModifyIdContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                ui.hideRefreshView();
                if (result.isSuccess()) {
                    ui.refreshUI();
                }
            }
        });
        BluetoothHelper.addCommand(invocation);
    }

//	@Override
//	public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//		PeripheralSettingModifyIdContracts.UI ui = getView();
//		if(ui == null) {
//			return;
//		}
//		if(newState) {
//			ui.updateConnectStatus(newState);
//		}
//	}

    @Override
    public void modifyId(final boolean isSteeringGear, final URoComponentType type, final int sourceId, final int targetId) {
//        if (modifyActionSequence != null && !modifyActionSequence.hasFinish()) {
//            modifyActionSequence.abort();
//        }
//        modifyActionSequence = UKitDeviceActionSequence.newActionSequence();
//        if (isSteeringGear) {
//            modifyActionSequence.command(UKitCmdSteeringGearModify.newInstance(sourceId, targetId));
//        } else {
//            modifyActionSequence.command(UKitCmdSensorModify.newInstance(type, sourceId, targetId));
//        }
//        modifyActionSequence.add(UKitDeviceActionSequenceFactory.createRefreshBoardInfoActionSequence(1000));
//        modifyActionSequence.callback(new IActionSequenceCallback() {
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                if (getView() != null) {
//                    getView().updateModifyResult(isSuccess, isSteeringGear, type, sourceId, targetId);
//                }
//            }
//        });
//        if (!modifyActionSequence.execute()) {
//            if (getView() != null) {
//                getView().updateModifyResult(false, isSteeringGear, type, sourceId, targetId);
//            }
//        }
        URoComponentType _type = isSteeringGear ? URoComponentType.SERVOS : type;
        BluetoothHelper.addCommand(BtInvocationFactory.changeID(sourceId, targetId, _type, new IUKitCommandResponse<Void>() {
            @Override
            public void onUKitCommandResponse(URoCompletionResult<Void> result) {
                if (getView() != null) {
                    getView().updateModifyResult(result.isSuccess(), isSteeringGear, type, sourceId, targetId);
                }
            }
        }));
    }
}
