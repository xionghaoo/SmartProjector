package com.ubtedu.ukit.bluetooth.model;

public class SteeringGear {
    public final static int API_CLOCKWISE = 1;
    public final static int API_COUNTERCLOCKWISE = 2;
    public final static int API_STOP = 3;

    public enum Speed {
        VS(0x0080), // 非常慢 128
        S(0x00EA), // 慢速 234
        M(0x0154), // 中速 340
        F(0x020E), // 快速 526
        VF(0x0292); // 非常快 658
        int value;

        public int getValue() {
            return value;
        }

        Speed(int value) {
            this.value = value;
        }
    }

    public enum Mode {
        STOP((byte) 0x00), // 停止
        CLOCKWISE((byte) 0x01), // 顺时针转动
        COUNTERCLOCKWISE((byte) 0x02); // 逆时针转动
        byte value;

        Mode(byte value) {
            this.value = value;
        }
    }

    public static int getSpeedInMode(int speed, Mode mode) {
        int speedInt;
        if (Mode.CLOCKWISE.equals(mode)) {
            speedInt = Math.abs(speed);
        } else if (Mode.COUNTERCLOCKWISE.equals(mode)) {
            speedInt = -Math.abs(speed);
        } else {
            speedInt = 0;
        }
        return speedInt;
    }

    public static int getSpeedInMode(int speed, int intMode) {
        Mode mode = Mode.CLOCKWISE;
        switch (intMode) {
            case 1://顺时针
                mode = Mode.CLOCKWISE;
                break;
            case 2://逆时针
                mode = Mode.COUNTERCLOCKWISE;
                break;
            case 3://停止
                mode = Mode.STOP;
                break;
        }
        return getSpeedInMode(speed, mode);
    }


}
