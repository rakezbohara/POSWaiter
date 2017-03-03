package com.app.rakez.dungatrial1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAKEZ on 2/13/2017.
 */
public class Album {
    private String name;
    private int thumbnail;
    private String state;


    public Album(String name, int thumbnail, String state) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}