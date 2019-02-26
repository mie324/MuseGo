package com.ece1778.musego.Model;

public class Rotation {

    private float qx;
    private float qy;
    private float qz;
    private float qw;

    public Rotation(){

    }

    public Rotation(float qx, float qy, float qz, float qw) {
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
        this.qw = qw;
    }

    public float getQx() {
        return qx;
    }

    public void setQx(float qx) {
        this.qx = qx;
    }

    public float getQy() {
        return qy;
    }

    public void setQy(float qy) {
        this.qy = qy;
    }

    public float getQz() {
        return qz;
    }

    public void setQz(float qz) {
        this.qz = qz;
    }

    public float getQw() {
        return qw;
    }

    public void setQw(float qw) {
        this.qw = qw;
    }
}

