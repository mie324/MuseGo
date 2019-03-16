package com.ece1778.musego.Model;

import java.io.Serializable;
import java.util.List;

public class Path implements Serializable {

    private static final long serialVersionUID = 1L;


    private String pId;
    private String userId;
    private String username;
    private String userAvatar;
    private String userBio;
    private String timestamp;
    private String title;
    private String description;
    private String floor;
    private String estimated_time;
    private List<String> tags;
    private List<String> likeList;
    private List<String> imgList;
    private Node start_node;
    private Node end_node;
    private List<Node> nodes;


    public Path() {

    }


//    public Path(String userId, String timestamp, String title, String description, String floor, String estimated_time, List<String> tags, String privacy,List<String> likeList, Node start_node, Node end_node, List<Node> nodes) {
//        this.userId = userId;
//        this.timestamp = timestamp;
//        this.title = title;
//        this.description = description;
//        this.floor = floor;
//        this.estimated_time = estimated_time;
//        this.tags = tags;
//        this.privacy = privacy;
//        this.likeList = likeList;
//        this.start_node = start_node;
//        this.end_node = end_node;
//        this.nodes = nodes;
//    }

//    public Path(String pId, String userId, String timestamp, String title, String description, String floor, String estimated_time, List<String> tags, String privacy,List<String> likeList, Node start_node, Node end_node, List<Node> nodes) {
//        this.pId = pId;
//        this.userId = userId;
//        this.timestamp = timestamp;
//        this.title = title;
//        this.description = description;
//        this.floor = floor;
//        this.estimated_time = estimated_time;
//        this.tags = tags;
//        this.privacy = privacy;
//        this.likeList = likeList;
//        this.start_node = start_node;
//        this.end_node = end_node;
//        this.nodes = nodes;
//
//
//    }

    public Path(String pId, String userId, String username, String userAvatar, String userBio, String timestamp, String title, String description, String floor, String estimated_time, List<String> tags, List<String> likeList, List<String> imgList, Node start_node, Node end_node, List<Node> nodes) {
        this.pId = pId;
        this.userId = userId;
        this.username = username;
        this.userAvatar = userAvatar;
        this.userBio = userBio;
        this.timestamp = timestamp;
        this.title = title;
        this.description = description;
        this.floor = floor;
        this.estimated_time = estimated_time;
        this.tags = tags;
        this.likeList = likeList;
        this.imgList = imgList;
        this.start_node = start_node;
        this.end_node = end_node;
        this.nodes = nodes;
    }

    public Path(String userId, String username, String userAvatar, String userBio, String timestamp, String title, String description, String floor, String estimated_time, List<String> tags, List<String> likeList, List<String> imgList, Node start_node, Node end_node, List<Node> nodes) {
        this.userId = userId;
        this.username = username;
        this.userAvatar = userAvatar;
        this.userBio = userBio;
        this.timestamp = timestamp;
        this.title = title;
        this.description = description;
        this.floor = floor;
        this.estimated_time = estimated_time;
        this.tags = tags;
        this.likeList = likeList;
        this.imgList = imgList;
        this.start_node = start_node;
        this.end_node = end_node;
        this.nodes = nodes;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public List<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<String> likeList) {
        this.likeList = likeList;
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

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
