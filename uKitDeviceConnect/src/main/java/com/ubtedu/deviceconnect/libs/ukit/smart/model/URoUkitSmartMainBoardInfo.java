package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitSmartMainBoardInfo extends URoMainBoardInfo {

    public URoUkitSmartMainBoardInfo(URoMainBoardInfo mainBoardInfo) {
        if(mainBoardInfo != null) {
            boardVersion = mainBoardInfo.boardVersion;
            mcuVersion = mainBoardInfo.mcuVersion;
            steeringGear = mainBoardInfo.steeringGear;
            infrared = mainBoardInfo.infrared;
            touch = mainBoardInfo.touch;
            lighting = mainBoardInfo.lighting;
            ultrasound = mainBoardInfo.ultrasound;
            speaker = mainBoardInfo.speaker;
            envLight = mainBoardInfo.envLight;
            sound = mainBoardInfo.sound;
            humiture = mainBoardInfo.humiture;
            color = mainBoardInfo.color;
            motor = mainBoardInfo.motor;
            sucker = mainBoardInfo.sucker;
            ledBelt = mainBoardInfo.ledBelt;
            lcd = mainBoardInfo.lcd;
            vision = mainBoardInfo.vision;
        }
    }
    
    public void addIdVersion(URoComponentID id, String version) {
        if(id == null) {
            return;
        }
        addIdVersion(id.getComponentType(), id.getId(), version);
    }
    
    public void addIdVersion(URoComponentType type, int id, String version) {
        if(type == null) {
            return;
        }
        URoComponentInfo componentInfo = getComponentInfo(type);
        if(componentInfo == null) {
            return;
        }
        componentInfo.addIdVersion(id, version);
    }
    
    public void addAbnormalId(URoComponentID id) {
        if(id == null) {
            return;
        }
        addAbnormalId(id.getComponentType(), id.getId());
    }
    
    public void addAbnormalId(URoComponentType type, int id) {
        if(type == null) {
            return;
        }
        URoComponentInfo componentInfo = getComponentInfo(type);
        if(componentInfo == null) {
            return;
        }
        componentInfo.addAbnormalId(id);
    }
    
    public void addAvailableId(URoComponentID id) {
        if(id == null) {
            return;
        }
        addAvailableId(id.getComponentType(), id.getId());
    }
    
    public void addAvailableId(URoComponentType type, int id) {
        if(type == null) {
            return;
        }
        URoComponentInfo componentInfo = getComponentInfo(type);
        if(componentInfo == null) {
            return;
        }
        componentInfo.addAvailableId(id);
    }

}
