package com.ubtedu.deviceconnect.libs.base.model;

import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;

import java.util.UUID;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoLinkModel {
    public final URoLinkType linkType;
    public final String linkName;
    public final String linkID;

    public URoLinkModel(URoLinkType linkType, String linkName, String linkID) {
        this.linkType = linkType;
        this.linkName = linkName;
        this.linkID = linkID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoLinkModel)) return false;

        URoLinkModel that = (URoLinkModel) o;

        if (linkType != that.linkType) return false;
        return linkName != null ? linkName.equals(that.linkName) : that.linkName == null;
    }

    @Override
    public int hashCode() {
        int result = linkType.hashCode();
        result = 31 * result + (linkName != null ? linkName.hashCode() : 0);
        return result;
    }

}
