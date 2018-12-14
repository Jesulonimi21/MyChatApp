package com.example.jesulonimi.firstchatapp;

public class User {
     String image;
     String name;
     String status;
   private  String THumb_image;

    public String getTHumb_image() {
        return THumb_image;
    }

    public void setTHumb_image(String THumb_image) {
        this.THumb_image = THumb_image;
    }

    public User() {
    }

    public User(String image, String name, String status) {
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
