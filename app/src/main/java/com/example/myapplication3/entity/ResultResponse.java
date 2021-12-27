package com.example.myapplication3.entity;

import java.util.List;

public class ResultResponse {
    private int code;
    private String token;
    private String state;
    private List<PictureEntity> datas;

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

    public List<PictureEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<PictureEntity> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "ResultResponse{" +
                "code=" + code +
                ", token='" + token + '\'' +
                ", state='" + state + '\'' +
                ", datas=" + datas +
                '}';
    }
}
