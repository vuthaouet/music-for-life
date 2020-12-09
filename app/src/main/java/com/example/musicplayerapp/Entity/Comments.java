package com.example.musicplayerapp.Entity;

import java.util.Date;

public class Comments {
    private String uId;
    private String uName;
    private String content;
    private String date;

    public String getUId() {
        return uId;
    }

    public void setUId(String id) {
        this.uId = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
