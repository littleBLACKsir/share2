package com.example.myapplication3.entity;

import java.io.Serializable;

public class PictureEntity implements Serializable {

    private int pid;
    private String url;
    private int liked;
    private String picturename;
    private String username;
    private boolean collect;

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public String getPicturename() {
        return picturename;
    }

    public void setPicturename(String picturename) {
        this.picturename = picturename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "PictureEntity{" +
                "url='" + url + '\'' +
                ", dianzan=" + liked +
                ", picturename='" + picturename + '\'' +
                ", collect=" + collect +
                '}';
    }
}
