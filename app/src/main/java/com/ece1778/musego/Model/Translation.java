package com.ece1778.musego.Model;

public class Translation {

    private float tx;
    private float ty;
    private float tz;

    public Translation(){

    }

    public Translation(float tx, float ty, float tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }


    public float getTx() {
        return tx;
    }

    public void setTx(float tx) {
        this.tx = tx;
    }

    public float getTy() {
        return ty;
    }

    public void setTy(float ty) {
        this.ty = ty;
    }

    public float getTz() {
        return tz;
    }

    public void setTz(float tz) {
        this.tz = tz;
    }
}
