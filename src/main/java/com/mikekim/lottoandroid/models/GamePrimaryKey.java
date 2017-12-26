package com.mikekim.lottoandroid.models;

import java.io.Serializable;


public class GamePrimaryKey implements Serializable {
    private String name;
    private String date;

    public GamePrimaryKey() {
    }

    public GamePrimaryKey(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
