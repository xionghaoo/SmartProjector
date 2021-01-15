package com.ubtedu.ukit.bluetooth.connect.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoLEDStripInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.connect.widget.PeripheralView;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;
import com.ubtedu.ukit.project.vo.ModelInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @Author naOKi
 * @Date 2018/11/20
 **/
public class BluetoothConnectItemAdapter extends RecyclerView.Adapter<BluetoothConnectItemAdapter.BluetoothDeviceViewHolder> {

    private ArrayList<PeripheralItem> items;
    private URoMainBoardInfo boardInfoData;
    private ModelInfo modelInfo;
    private IBluetoothItemClickListener itemClickListener;

    private boolean showIdentity = true;
    private boolean showUpgradeable = true;

    private UpgradeResult upgradeResult = null;
    private UpgradeProcess upgradeProcess = null;

    private static class UpgradeResult {
        private boolean isSteeringGear;
        private URoComponentType sensorType;
        private boolean isSuccess;
        private UpgradeResult(boolean isSteeringGear, URoComponentType sensorType, boolean isSuccess) {
            this.isSteeringGear = isSteeringGear;
            this.sensorType = sensorType;
            this.isSuccess = isSuccess;
        }
        private boolean isSameTypeOfUpgradeProcess(UpgradeProcess upgradeProcess) {
            if(upgradeProcess == null) {
                return false;
            }
            if(this.isSteeringGear != upgradeProcess.isSteeringGear) {
                return false;
            }
            if(this.isSteeringGear) {
                return true;
            }
            return this.sensorType.equals(upgradeProcess.sensorType);
        }
    }

    private static class UpgradeProcess {
        private boolean isSteeringGear;
        private URoComponentType sensorType;
        private int process;
        private UpgradeProcess(boolean isSteeringGear, URoComponentType sensorType, int process) {
            this.isSteeringGear = isSteeringGear;
            this.sensorType = sensorType;
            this.process = process;
        }
    }

    public static class PeripheralItem implements Comparable<PeripheralItem> {
        private boolean isSteeringGear;
        private int steeringGearMode;
        private URoComponentType type;
        private int id;
        private int parentId;
        private boolean isUpgradeable;
        private boolean isAbnormal;
        private boolean isConflict;
        private boolean isUnmatch;
        private String version;

        public PeripheralItem(boolean isSteeringGear, URoComponentType type, int parentId, int id, boolean isUpgradeable, boolean isAbnormal, boolean isConflict, String version) {
            this.isSteeringGear = isSteeringGear;
            this.type = type;
            this.id = id;
            this.isUpgradeable = isUpgradeable;
            this.isAbnormal = isAbnormal;
            this.isConflict = isConflict;
            this.version = version;
            this.parentId = parentId;
        }

        public PeripheralItem(boolean isSteeringGear, URoComponentType type, int id, boolean isUpgradeable, boolean isAbnormal, boolean isConflict, String version) {
            this(isSteeringGear, type, -1, id, isUpgradeable, isAbnormal, isConflict, version);
        }
        public boolean isUnmatch() {
            return isUnmatch;
        }
        public void setUnmatch(boolean unmatch) {
            isUnmatch = unmatch;
        }
        public boolean isSteeringGear() {
            return isSteeringGear;
        }
        public URoComponentType getType() {
            return type;
        }
        public int getId() {
            return id;
        }
        public boolean isUpgradeable() {
            return isUpgradeable;
        }
        public boolean isAbnormal() {
            return isAbnormal;
        }
        public boolean isConflict() {
            return isConflict;
        }
        public String getVersion() {
            return version;
        }
        public int getSteeringGearMode() {
            return steeringGearMode;
        }
        public void setSteeringGearMode(int steeringGearMode) {
            this.steeringGearMode = steeringGearMode;
        }
        @Override
        public int compareTo(@NonNull PeripheralItem o) {
            int priority1 = getPriority();
            int priority2 = o.getPriority();
            if(priority1 == priority2) {
                return getId() - o.getId();
            } else {
                return priority1 - priority2;
            }
        }
        private int getPriority() {
            if(isSteeringGear()) {
                return 0;
            } else {
                Integer priority = SENSOR_PRIORITY.get(getType());
                return priority == null ? Integer.MAX_VALUE : priority;
            }
        }
        private static final HashMap<URoComponentType, Integer> SENSOR_PRIORITY = new HashMap<>();
        static {
            SENSOR_PRIORITY.put(URoComponentType.MOTOR, 1);
            SENSOR_PRIORITY.put(URoComponentType.INFRAREDSENSOR, 2);
            SENSOR_PRIORITY.put(URoComponentType.ULTRASOUNDSENSOR, 3);
            SENSOR_PRIORITY.put(URoComponentType.LED, 4);
            SENSOR_PRIORITY.put(URoComponentType.TOUCHSENSOR, 5);
            SENSOR_PRIORITY.put(URoComponentType.ENVIRONMENTSENSOR, 6);
            SENSOR_PRIORITY.put(URoComponentType.BRIGHTNESSSENSOR, 7);
            SENSOR_PRIORITY.put(URoComponentType.SOUNDSENSOR, 8);
            SENSOR_PRIORITY.put(URoComponentType.COLORSENSOR, 9);
            SENSOR_PRIORITY.put(URoComponentType.SPEAKER, 10);
            SENSOR_PRIORITY.put(URoComponentType.LED_BELT, 11);
            SENSOR_PRIORITY.put(URoComponentType.LED_STRIP, 12);
        }
    }

    public BluetoothConnectItemAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItemClickListener(IBluetoothItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IBluetoothItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setBoardInfoData(URoMainBoardInfo boardInfoData) {
        this.boardInfoData = boardInfoData;
        apply();
    }

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
        apply();
    }

    public void setShowIdentity(boolean showIdentity) {
        if(this.showIdentity == showIdentity) {
            return;
        }
        this.showIdentity = showIdentity;
        apply();
    }

    public void setShowUpgradeable(boolean showUpgradeable) {
        if(this.showUpgradeable == showUpgradeable) {
            return;
        }
        this.showUpgradeable = showUpgradeable;
        apply();
    }

    public void clear() {
        boardInfoData = null;
        apply();
    }

    private void prepare(ArrayList<PeripheralItem> items, URoComponentInfo data, boolean isSteeringGear, URoComponentType type) {
        prepare(items, data, isSteeringGear, type, null);
    }

    private void prepare(ArrayList<PeripheralItem> items, URoComponentInfo data, boolean isSteeringGear, URoComponentType type, ArrayList<Integer> modelList) {
        prepare(items, data, isSteeringGear, type, modelList, null);
    }

    private void prepare(ArrayList<PeripheralItem> items, URoComponentInfo data, boolean isSteeringGear, URoComponentType type, ArrayList<Integer> modelList, ArrayList<Integer> modelWheelList) {
        HashSet<Integer> ids = new HashSet<>();
        ids.addAll(data.getAvailableIds());
        ids.addAll(data.getAbnormalIds());
        ids.addAll(data.getConflictIds());
        if(modelList != null) {
            ids.addAll(modelList);
        }
        if(ids.isEmpty()) {
            return;
        }
        HashSet<Integer> unmatch = new HashSet<>();
        if(modelList != null) {
            //多接的ID
            HashSet<Integer> unmatch1 = new HashSet<>();
            unmatch1.addAll(data.getAvailableIds());
            unmatch1.addAll(data.getAbnormalIds());
            unmatch1.addAll(data.getConflictIds());
            unmatch1.removeAll(modelList);
            unmatch.addAll(unmatch1);
            //少接的ID
            HashSet<Integer> unmatch2 = new HashSet<>();
            unmatch2.addAll(modelList);
            unmatch2.removeAll(data.getAvailableIds());
            unmatch2.removeAll(data.getAbnormalIds());
            unmatch2.removeAll(data.getConflictIds());
            unmatch.addAll(unmatch2);
        }
        ArrayList<Integer> idList = new ArrayList<>(ids);
        Collections.sort(idList);
        boolean isUpgradeable;
        boolean isConflict = showUpgradeable && (!data.getConflictIds().isEmpty() || BluetoothHelper.isComponentHasErrorVersion(type));
        if (isSteeringGear) {
            isUpgradeable = showUpgradeable && OtaHelper.isSteeringGearUpgradeable();
        } else {
            if (URoComponentType.MOTOR.equals(type)
                    || URoComponentType.ULTRASOUNDSENSOR.equals(type)
                    || URoComponentType.ENVIRONMENTSENSOR.equals(type)
                    || URoComponentType.BRIGHTNESSSENSOR.equals(type)
                    || URoComponentType.SOUNDSENSOR.equals(type)
                    || URoComponentType.COLORSENSOR.equals(type)) {
                isUpgradeable = false;
                isConflict = false;
            } else {
                isUpgradeable = showUpgradeable && OtaHelper.isSensorUpgradeable(type);
            }
        }
        for(Integer id : idList) {
            boolean isAbnormal = data.getAbnormalIds().contains(id);
            boolean isUnmatch = unmatch.contains(id);
            PeripheralItem peripheralItem = new PeripheralItem(isSteeringGear, type, id, isUpgradeable, isAbnormal, isConflict, data.getVersionById(id));
            peripheralItem.setUnmatch(isUnmatch);
            items.add(peripheralItem);
            if (URoComponentType.LED_BELT.equals(type)) {
                ArrayList<URoLEDStripInfo> ledStripList = data.getSubComponent(id);
                if (ledStripList == null) {
                    continue;
                }
                for (int i = 0; i < ledStripList.size(); i++) {
                    URoLEDStripInfo ledStripInfo = ledStripList.get(i);
                    if (ledStripInfo != null && ledStripInfo.getLedNumber() > 0) {
                        PeripheralItem subPeripheralItem = new PeripheralItem(false, URoComponentType.LED_STRIP, id, ledStripInfo.getPort(), false, false, false, "");
                        items.add(subPeripheralItem);
                    }
                }
            }
        }
    }

    private void prepare(ArrayList<PeripheralItem> items) {
        if(boardInfoData == null) {
            return;
        }
        if(modelInfo == null) {
            prepare(items, boardInfoData.steeringGear, true, URoComponentType.SERVOS);
            prepare(items, boardInfoData.motor, false, URoComponentType.MOTOR);
        } else {
            prepare(items, boardInfoData.steeringGear, true, URoComponentType.SERVOS, modelInfo.steeringGear);
            prepare(items, boardInfoData.motor, false, URoComponentType.MOTOR, modelInfo.motor);
        }
        prepare(items, boardInfoData.infrared, false, URoComponentType.INFRAREDSENSOR);
        prepare(items, boardInfoData.ultrasound, false, URoComponentType.ULTRASOUNDSENSOR);
        prepare(items, boardInfoData.lighting, false, URoComponentType.LED);
        prepare(items, boardInfoData.touch, false, URoComponentType.TOUCHSENSOR);
        prepare(items, boardInfoData.humiture, false, URoComponentType.ENVIRONMENTSENSOR);
        prepare(items, boardInfoData.envLight, false, URoComponentType.BRIGHTNESSSENSOR);
        prepare(items, boardInfoData.sound, false, URoComponentType.SOUNDSENSOR);
        prepare(items, boardInfoData.color, false, URoComponentType.COLORSENSOR);
        prepare(items, boardInfoData.speaker, false, URoComponentType.SPEAKER);
        prepare(items, boardInfoData.ledBelt, false, URoComponentType.LED_BELT);
        Collections.sort(items);
    }

    private void apply() {
        ArrayList<PeripheralItem> newItems = new ArrayList<>();
        prepare(newItems);
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public boolean hasUpgradeableItems() {
        boolean result = false;
        for(PeripheralItem item : items) {
            if(item.isUpgradeable) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean hasAbnormalItems() {
        boolean result = false;
        for(PeripheralItem item : items) {
            if(item.isAbnormal) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean hasConflictItems() {
        boolean result = false;
        for(PeripheralItem item : items) {
            if(item.isConflict) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean hasUnmatchItems() {
        boolean result = false;
        for(PeripheralItem item : items) {
            if(item.isUnmatch) {
                result = true;
                break;
            }
        }
        return result;
    }

    @NonNull
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        PeripheralView peripheralView = new PeripheralView(viewGroup.getContext());
        peripheralView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT));
        BluetoothDeviceViewHolder vh = new BluetoothDeviceViewHolder(peripheralView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceViewHolder viewHolder, int i) {
        UpgradeProcess upgradeProcess;
        UpgradeResult upgradeResult;
        synchronized (BluetoothConnectItemAdapter.class) {
            upgradeProcess = this.upgradeProcess;
            upgradeResult = this.upgradeResult;
        }
        PeripheralItem item = getItem(i);
        if(item.isSteeringGear) {
            viewHolder.peripheralView.setAsSteeringGear(item.id, item.getSteeringGearMode());
        } else {
            viewHolder.peripheralView.setAsSensor(item.type, item.id,item.parentId);
        }
        if(showIdentity) {
            boolean isUpgradeSuccess = false;
            if(upgradeResult != null) {
                if(item.isSteeringGear) {
                    isUpgradeSuccess = item.isSteeringGear == upgradeResult.isSteeringGear && upgradeResult.isSuccess;
                } else {
                    isUpgradeSuccess = item.getType().equals(upgradeResult.sensorType) && upgradeResult.isSuccess;
                }
            }
            viewHolder.peripheralView.markAsFlag(item.isUpgradeable, isUpgradeSuccess, item.isAbnormal, item.isConflict, item.isUnmatch);
            if(upgradeProcess != null && (upgradeResult == null || !upgradeResult.isSameTypeOfUpgradeProcess(upgradeProcess))) {
                if((item.isSteeringGear && upgradeProcess.isSteeringGear) || (!item.isSteeringGear && item.getType().equals(upgradeProcess.sensorType))) {
                    viewHolder.peripheralView.updateUpgradeProcess(upgradeProcess.process);
                }
            }
        }
        viewHolder.peripheralView.getIconView().setTag(i);
        if(itemClickListener != null) {
            viewHolder.peripheralView.getIconView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null) {
                        itemClickListener.onBluetoothItemClick(v, (int)v.getTag());
                    }
                }
            });
        }
    }

    public PeripheralItem getItem(int i) {
        if(i < 0 || i >= items.size()) {
            return null;
        }
        return items.get(i);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateUpgradeProcess(boolean isSteeringGear, URoComponentType sensorType, int process) {
        updateUpgradeProcess(isSteeringGear, sensorType, process, true);
    }

    public void updateUpgradeProcess(boolean isSteeringGear, URoComponentType sensorType, int process, boolean notifyChanged) {
        synchronized (BluetoothConnectItemAdapter.class) {
            upgradeProcess = new UpgradeProcess(isSteeringGear, sensorType, process);
        }
        if(notifyChanged) {
            notifyDataSetChanged();
        }
    }

    public void updateUpgradeResult(boolean isSteeringGear, URoComponentType sensorType, boolean isSuccess) {
        updateUpgradeResult(isSteeringGear, sensorType, isSuccess, true);
    }

    public void updateUpgradeResult(boolean isSteeringGear, URoComponentType sensorType, boolean isSuccess, boolean notifyChanged) {
        synchronized (BluetoothConnectItemAdapter.class) {
            upgradeResult = new UpgradeResult(isSteeringGear, sensorType, isSuccess);
            if(isSuccess) {
                for (PeripheralItem item : items) {
                    if ((item.isUpgradeable || item.isConflict) && item.isSteeringGear == isSteeringGear && item.type == sensorType) {
                        item.isUpgradeable = false;
                        item.isConflict = false;
                    }
                }
            }
        }
        if(notifyChanged) {
            notifyDataSetChanged();
        }
    }

    public void clearUpgradeIdentity() {
        if(!showIdentity) {
            return;
        }
        synchronized (BluetoothConnectItemAdapter.class) {
            upgradeProcess = null;
            upgradeResult = null;
        }
        notifyDataSetChanged();
    }

    public static class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {
        private BluetoothDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.peripheralView = (PeripheralView)itemView;
        }
        private PeripheralView peripheralView;
    }
    
}
