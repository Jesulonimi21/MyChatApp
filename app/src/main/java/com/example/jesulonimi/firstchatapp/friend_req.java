package com.example.jesulonimi.firstchatapp;

public class friend_req {
    public friend_req() {
    }

    String request_type;

    public friend_req(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
