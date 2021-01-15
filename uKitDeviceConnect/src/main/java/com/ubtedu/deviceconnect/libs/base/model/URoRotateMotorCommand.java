package com.ubtedu.deviceconnect.libs.base.model;

public class URoRotateMotorCommand {
    private int id;
    private int speed;
    private int time;

    public URoRotateMotorCommand(int id,int speed,int time){
        this.id=id;
        this.speed=speed;
        this.time=time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
