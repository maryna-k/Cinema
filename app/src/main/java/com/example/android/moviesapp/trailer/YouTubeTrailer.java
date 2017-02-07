package com.example.android.moviesapp.trailer;


import java.io.Serializable;

public class YouTubeTrailer implements Serializable{

    private String key;

    public YouTubeTrailer(String key) {
        this.key = key;
    }

    public YouTubeTrailer(YouTubeTrailer original){
        key = original.key;
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
