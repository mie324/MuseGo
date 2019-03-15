package com.ece1778.musego.Model;

public class User {

    private String username;
    private String avatar;
    private String bio;
    private int role;

    public User(){

    }

    public User(String username, String avatar, String bio, int role) {
        this.username = username;
        this.avatar = avatar;
        this.bio = bio;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
