package com.ece1778.musego.Model;

import java.io.Serializable;
import java.util.List;

public class Path implements Serializable {

    private static final long serialVersionUID=1L;


    private String pId;
    private String userId;
    private String timestamp;
    private String title;
    private String description;
    private String floor;
    private String estimated_time;
    private List<String> tags;
    private String privacy;
    private Node start_node;
    private Node end_node;
    private List<Node> nodes;


    public Path(){

    }


    public Path(String userId, String timestamp, String title, String description, String floor, String estimated_time, List<String> tags, String privacy, Node start_node, Node end_node, List<Node> nodes) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.title = title;
        this.description = description;
        this.floor = floor;
        this.estimated_time = estimated_time;
        this.tags = tags;
        this.privacy = privacy;
        this.start_node = start_node;
        this.end_node = end_node;
        this.nodes = nodes;
    }

    public Path(String pId, String userId, String timestamp, String title, String description, String floor, String estimated_time, List<String> tags, String privacy, Node start_node, Node end_node, List<Node> nodes) {
        this.pId = pId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.title = title;
        this.description = description;
        this.floor = floor;
        this.estimated_time = estimated_time;
        this.tags = tags;
        this.privacy = privacy;
        this.start_node = start_node;
        this.end_node = end_node;
        this.nodes = nodes;


    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(String estimated_time) {
        this.estimated_time = estimated_time;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
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
