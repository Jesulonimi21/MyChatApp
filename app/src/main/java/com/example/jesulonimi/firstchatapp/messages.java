package com.example.jesulonimi.firstchatapp;

public class messages {
    String message;
    boolean seen;
    long time;
    String type;
String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public messages() {
    }

    public messages(String message, boolean seen, long time, String type, String from) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from= from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
