package com.ubtedu.deviceconnect.libs.base.model;

import androidx.annotation.NonNull;

public class URoComponentID {
    private int id;
    private URoComponentType componentType;

    public URoComponentID(@NonNull URoComponentType componentType, int id) {
        this.id = id;
        this.componentType = componentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public URoComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(URoComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoComponentID)) return false;

        URoComponentID that = (URoComponentID) o;

        if (getId() != that.getId()) return false;
        return getComponentType() == that.getComponentType();
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getComponentType() != null ? getComponentType().hashCode() : 0);
        return result;
    }

}
