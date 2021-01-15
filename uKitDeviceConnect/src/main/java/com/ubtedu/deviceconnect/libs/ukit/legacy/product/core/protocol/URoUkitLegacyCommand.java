package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitLegacyCommand extends URoCommand {

    private final int cmdCode;

    public URoUkitLegacyCommand(String name, int cmdCode) {
        this.cmdCode = cmdCode;
        setName(name);
    }

    @Override
    public String toString() {
        return "URoUkitLegacyCommand{" +
                "cmdCode=" + cmdCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoUkitLegacyCommand)) return false;

        URoUkitLegacyCommand that = (URoUkitLegacyCommand) o;

        return cmdCode == that.cmdCode;
    }

    @Override
    public int hashCode() {
        return cmdCode;
    }

    public int getCmdCode() {
        return cmdCode;
    }
}
