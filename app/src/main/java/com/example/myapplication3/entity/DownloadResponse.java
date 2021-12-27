package com.example.myapplication3.entity;

public class DownloadResponse {
    private int code;
    private String token;
    private String state;
    private String data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DownloadResponse{" +
                "code=" + code +
                ", token='" + token + '\'' +
                ", state='" + state + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
