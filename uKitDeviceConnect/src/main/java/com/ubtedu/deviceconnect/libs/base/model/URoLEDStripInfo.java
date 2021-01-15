package com.ubtedu.deviceconnect.libs.base.model;

public class URoLEDStripInfo {
    private int port;
    private int ledNumber;

    public URoLEDStripInfo(int port, int ledNumber) {
        this.port = port;
        this.ledNumber = ledNumber;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getLedNumber() {
        return ledNumber;
    }

    public void setLedNumber(int ledNumber) {
        this.ledNumber = ledNumber;
    }
}
