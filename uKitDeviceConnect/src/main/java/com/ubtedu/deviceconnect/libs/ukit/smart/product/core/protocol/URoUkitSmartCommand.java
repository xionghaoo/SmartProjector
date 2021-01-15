package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitSmartCommand extends URoCommand {

    private final int dev;
    private final int cmd;

    public URoUkitSmartCommand(String name, int dev, int cmd) {
        this.dev = dev;
        this.cmd = cmd;
        setName(name);
    }

    @Override
    public String toString() {
        return "URoUkitSmartCommand{" +
                "dev=" + dev +
                ", cmd=" + cmd +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoUkitSmartCommand)) return false;

        URoUkitSmartCommand that = (URoUkitSmartCommand) o;

        if (dev != that.dev) return false;
        return cmd == that.cmd;
    }

    @Override
    public int hashCode() {
        int result = dev;
        result = 31 * result + cmd;
        return result;
    }

    public int getCmd() {
        return cmd;
    }

    public int getDev() {
        return dev;
    }

}
