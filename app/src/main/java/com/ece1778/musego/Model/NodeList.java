package com.ece1778.musego.Model;


import java.util.List;



public class NodeList {


    private Node start_node;
    private Node end_node;
    private List<Node> nodes;


    public NodeList(){

    }

    public NodeList(Node start_node, Node end_node, List<Node> nodes) {
        this.start_node = start_node;
        this.end_node = end_node;
        this.nodes = nodes;
    }

    public Node getStart_node() {
        return start_node;
    }

    public void setStart_node(Node start_node) {
        this.start_node = start_node;
    }

    public Node getEnd_node() {
        return end_node;
    }

    public void setEnd_node(Node end_node) {
        this.end_node = end_node;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }



}

