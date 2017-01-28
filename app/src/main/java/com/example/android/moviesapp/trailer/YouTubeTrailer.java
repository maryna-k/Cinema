package com.example.android.moviesapp.trailer;


public class YouTubeTrailer {

    public String key;

    public YouTubeTrailer(String key) {
        this.key = key;
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
