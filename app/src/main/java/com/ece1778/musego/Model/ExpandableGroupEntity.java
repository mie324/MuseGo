package com.ece1778.musego.Model;

import java.util.ArrayList;

public class ExpandableGroupEntity {

    private String header;
    private ArrayList<Path> children;
    private boolean isExpand;

    public ExpandableGroupEntity(String header, boolean isExpand, ArrayList<Path> children){
        this.header = header;
        this.isExpand = isExpand;
        this.children = children;
    }


    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ArrayList<Path> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Path> children) {
        this.children = children;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
