package com.example.android.moviesapp.models;


import java.io.Serializable;

public class YouTubeTrailer implements Serializable{

    private String type;
    private String key;

    public YouTubeTrailer(){}

    public YouTubeTrailer(String key) {
        this.key = key;
    }

    public YouTubeTrailer(YouTubeTrailer original){
        key = original.key;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
