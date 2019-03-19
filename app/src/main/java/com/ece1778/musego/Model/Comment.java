package com.ece1778.musego.Model;

public class Comment {

    private String postId;
    private String userId;
    private String content;
    private String profilePhoto;
    private String username;
    private String timestamp;

    public Comment(){

    }


    public Comment(String postId, String userId, String content, String profilePhoto, String username, String timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.profilePhoto = profilePhoto;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
