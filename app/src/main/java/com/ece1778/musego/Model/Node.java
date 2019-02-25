package com.ece1778.musego.Model;

public class Node {

    private Translation t;
    private Rotation r;
    private String tag;
    private String description;

    public Node(){

    }

    public Node(Translation t, Rotation r, String tag){
        this.t = t;
        this.r = r;
        this.tag = tag;

    }

    public Node(Translation t, Rotation r, String tag, String description){
        this.t = t;
        this.r = r;
        this.tag = tag;
        this.description = description;

    }


    public Translation getT() {
        return t;
    }

    public void setT(Translation t) {
        this.t = t;
    }

    public Rotation getR() {
        return r;
    }

    public void setR(Rotation r) {
        this.r = r;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
