package com.android.chatapp.model;

public class User {
    String username;
    String userID;
    String password;
    String email;
    String profilePictureLink;
    public User(String username, String userID, String password, String email) {
        this.username = username;
        this.userID = userID;
        this.password = password;
        this.email = email;
    }
    public User() {
    }

    public User(String username, String userID, String password) {
        this.username = username;
        this.userID = userID;
        this.password = password;
    }

    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
