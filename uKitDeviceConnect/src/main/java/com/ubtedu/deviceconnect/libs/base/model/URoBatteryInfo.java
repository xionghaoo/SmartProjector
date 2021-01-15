package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public abstract class URoBatteryInfo {

    private int batteryRemaining;
    private float voltage;
    private boolean isCharging;
    private boolean isFullCharge;

//    protected URoBatteryInfo() {
//    }
//
//    public URoBatteryInfo(int batteryRemaining, int voltage, boolean isCharging, boolean isFullCharge) {
//        this.batteryRemaining = batteryRemaining;
//        this.voltage = voltage;
//        this.isCharging = isCharging;
//        this.isFullCharge = isFullCharge;
//    }

    protected void setBatteryRemaining(int batteryRemaining) {
        this.batteryRemaining = batteryRemaining;
    }

    protected void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    protected void setCharging(boolean charging) {
        isCharging = charging;
    }

    protected void setFullCharge(boolean fullCharge) {
        isFullCharge = fullCharge;
    }

    public int getBatteryRemaining() {
        return batteryRemaining;
    }

    public float getVoltage() {
        return voltage;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public boolean isLowBattery() {
        return true;
    }

    public boolean isEmptyBattery() {
        return true;
    }

    public boolean isFullCharge() {
        return isFullCharge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoBatteryInfo)) return false;

        URoBatteryInfo that = (URoBatteryInfo) o;

        if (getBatteryRemaining() != that.getBatteryRemaining()) return false;
        if (Float.compare(that.getVoltage(), getVoltage()) != 0) return false;
        if (isCharging() != that.isCharging()) return false;
        return isFullCharge() == that.isFullCharge();
    }

    @Override
    public int hashCode() {
        int result = getBatteryRemaining();
        result = 31 * result + (getVoltage() != +0.0f ? Float.floatToIntBits(getVoltage()) : 0);
        result = 31 * result + (isCharging() ? 1 : 0);
        result = 31 * result + (isFullCharge() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "URoBatteryInfo{" +
                "batteryRemaining=" + batteryRemaining +
                ", voltage=" + voltage +
                ", isCharging=" + isCharging +
                ", isFullCharge=" + isFullCharge +
                '}';
    }

}
