package com.ubtedu.deviceconnect.libs.base.model;

import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.utils.URoNumberConversionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public class URoComponentInfo<T> {
    //可用ID
    private ArrayList<Integer> availableIds = new ArrayList<>();
    //异常ID
    private ArrayList<Integer> abnormalIds = new ArrayList<>();
    //版本号不一致ID
    private ArrayList<Integer> conflictIds = new ArrayList<>();
    //版本号
    private String version = URoComponent.INVALID_VERSION;

    private HashMap<Integer, String> versions = new HashMap<>();

    private HashMap<Integer,ArrayList<T>> subComponents = new HashMap<>();

    public void addSubComponent(int parentId,T data){
        if (availableIds.contains(parentId) || abnormalIds.contains(parentId) || conflictIds.contains(parentId)) {
            ArrayList<T> subComponentList = subComponents.get(parentId);
            if (subComponentList == null) {
                subComponentList = new ArrayList<>();
            }
            subComponentList.add(data);
            subComponents.put(parentId, subComponentList);
        }
    }

    public ArrayList<T> getSubComponent(int parentId){
        ArrayList<T> arrayList=subComponents.get(parentId);
        if (arrayList!=null){
            return new ArrayList<>(arrayList);
        }
        return new ArrayList<>();
    }

    public void clearSubComponent(){
        if (subComponents!=null){
            subComponents.clear();
        }
    }

    private void parseIds(int value, ArrayList<Integer> target) {
        target.clear();
        if (value != 0) {
            for (int i = 0; i < 32; i++) {
                if (((1 << i) & value) != 0) {
                    target.add(i + 1);
                }
            }
        }
    }
    
    public void addIdVersion(int id, String version) {
        if(TextUtils.isEmpty(version)) {
            return;
        }
        if(!availableIds.contains(Integer.valueOf(id))) {
            return;
        }
        versions.put(id, version);
        String maxVersion = Collections.max(versions.values(), new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Long i1, i2;
                try {
                    i1 = Long.parseLong(o1, 16);
                } catch(Throwable e) {
                    i1 = Long.MIN_VALUE;
                }
                try {
                    i2 = Long.parseLong(o2, 16);
                } catch(Throwable e) {
                    i2 = Long.MIN_VALUE;
                }
                return Long.compare(i1, i2);
            }
        });
        this.version = maxVersion;
        conflictIds.clear();
        for(Map.Entry<Integer, String> entry : versions.entrySet()) {
            if(!TextUtils.equals(maxVersion, entry.getValue())) {
                conflictIds.add(entry.getKey());
                availableIds.remove(entry.getKey());
            }
        }
    }
    
    public void addAvailableId(int id) {
        availableIds.add(id);
    }
    
    public void addAbnormalId(int id) {
        abnormalIds.add(id);
    }

    private void setAvailableIdByBits(int bits) {
        parseIds(bits, availableIds);
    }

    private void setAbnormalIdByBits(int bits) {
        parseIds(bits, abnormalIds);
    }

    private void setConflictIdByBits(int bits) {
        parseIds(bits, conflictIds);
    }

    private void setVersionByBytes(byte[] bytes, int type) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if (type == 1) {
                sb.append(String.format(Locale.US, "%02d", Integer.parseInt(String.format(Locale.US, "%x", b), 16)));
            } else {
                sb.append(String.format(Locale.US, "%02X", b));
            }
        }
        version = sb.toString();
    }

    public int getAvailableIdInteger() {
        int result = 0;
        for (Integer availableId : availableIds) {
            result |= 1 << (availableId - 1);
        }
        return result;
    }

    public byte getAvailableIdByte() {
        byte result = 0;
        for (Integer availableId : availableIds) {
            if (availableId <= 0 || availableId > 8) {
                continue;
            }
            result |= 1 << (availableId - 1);
        }
        return result;
    }

    public ArrayList<Integer> getAvailableIds() {
        ArrayList<Integer> availableIdList = new ArrayList<>(availableIds);
        Collections.sort(availableIdList);
        return availableIdList;
    }

    public ArrayList<Integer> getAbnormalIds() {
        return new ArrayList<>(abnormalIds);
    }

    public ArrayList<Integer> getConflictIds() {
        return new ArrayList<>(conflictIds);
    }

    public boolean hasAbnormalId() {
        return !abnormalIds.isEmpty();
    }

    public boolean hasConflictId() {
        return !conflictIds.isEmpty();
    }

    public boolean isOK() {
        return !availableIds.isEmpty() && abnormalIds.isEmpty() && conflictIds.isEmpty();
    }

    public boolean isUpgradeable() {
        return (!availableIds.isEmpty() || !conflictIds.isEmpty()) && abnormalIds.isEmpty();
    }

    public String getVersion() {
        return version;
    }

    public String getVersionById(Integer id) {
        String version = versions.get(id);
        return TextUtils.isEmpty(version) ? this.version : version;
    }

    public void ignoreConflict() {
        conflictIds.clear();
    }

    public boolean setup(byte[] bytes, int availableSize, int abnormalSize, int versionSize, int conflictSize) {
        return setup(bytes, availableSize, abnormalSize, versionSize, conflictSize, 1);
    }

    public boolean setup(byte[] bytes, int availableSize, int abnormalSize, int versionSize, int conflictSize, int type) {
        if (bytes.length != (availableSize + abnormalSize + versionSize + conflictSize)) {
            return false;
        }
        int offset = 0;
        setAvailableIdByBits(byteToInt(Arrays.copyOfRange(bytes, offset, offset + availableSize)));
        offset += availableSize;
        setAbnormalIdByBits(byteToInt(Arrays.copyOfRange(bytes, offset, offset + abnormalSize)));
        offset += abnormalSize;
        setVersionByBytes(Arrays.copyOfRange(bytes, offset, offset + versionSize), type);
        offset += versionSize;
        setConflictIdByBits(byteToInt(Arrays.copyOfRange(bytes, offset, offset + conflictSize)));
        return true;
    }

    private int byteToInt(byte... bytes) {
        if (bytes == null || bytes.length == 0 || bytes.length > 4) {
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format(Locale.US,"%02X", bytes[i]));
        }
        return URoNumberConversionUtil.hex2Integer(sb.toString(), 16);
    }

    public void updateVersion(String version) {
        if (TextUtils.isEmpty(version) || TextUtils.equals(version, URoComponent.INVALID_VERSION)) {
            return;
        }
        if (TextUtils.equals(this.version, URoComponent.INVALID_VERSION) || (availableIds.isEmpty() && abnormalIds.isEmpty() && conflictIds.isEmpty())) {
            return;
        }
        this.version = version;
        for (Integer id : conflictIds) {
            if (availableIds.contains(id)) {
                continue;
            }
            availableIds.add(id);
        }
        Collections.sort(availableIds);
        conflictIds.clear();
    }

    public void updateId(Integer sourceId, Integer targetId) {
        if (Integer.compare(sourceId, targetId) == 0) {
            return;
        }
        if (TextUtils.equals(this.version, URoComponent.INVALID_VERSION) || (availableIds.isEmpty() && abnormalIds.isEmpty() && conflictIds.isEmpty())) {
            return;
        }
        if (availableIds.contains(sourceId)) {
            availableIds.remove(sourceId);
            availableIds.add(targetId);
            Collections.sort(availableIds);
        }
        if (abnormalIds.contains(sourceId)) {
            abnormalIds.remove(sourceId);
            abnormalIds.add(targetId);
            Collections.sort(abnormalIds);
        }
        if (conflictIds.contains(sourceId)) {
            conflictIds.remove(sourceId);
            conflictIds.add(targetId);
            Collections.sort(conflictIds);
        }
    }

    public HashMap<Integer, String> getVersions() {
        return new HashMap<>(versions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("version:").append(version).append(", ");
        sb.append("availableIds:").append(availableIds).append(", ");
        sb.append("abnormalIds:").append(abnormalIds).append(", ");
        sb.append("conflictIds:").append(conflictIds);
        sb.append("}");
        return sb.toString();
    }
}
