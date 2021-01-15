package com.ubtedu.deviceconnect.libs.base.link;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

import java.util.HashSet;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoLinkManager {

    public final HashSet<URoLink> links;

    public URoLinkManager() {
        links = new HashSet<>();
    }

    public boolean addLink(URoLink link) {
        synchronized (links) {
            return links.add(link);
        }
    }

    public boolean removeLink(URoLink link) {
        synchronized (links) {
            return links.remove(link);
        }
    }

    public URoLink getLink(URoLinkModel linkModel) {
        URoLink result = null;
        synchronized (links) {
            for (URoLink link : links) {
                if (link.matching(linkModel)) {
                    result = link;
                    break;
                }
            }
        }
        return result;
    }

    public boolean isExistLink(URoLinkModel linkModel) {
        boolean exist = false;
        synchronized (links) {
            for (URoLink link : links) {
                if (link.matching(linkModel)) {
                    exist = true;
                    break;
                }
            }
        }
        return exist;
    }

}
