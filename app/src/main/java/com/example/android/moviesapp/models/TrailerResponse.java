package com.example.android.moviesapp.models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrailerResponse {
    @SerializedName("results")
    private ArrayList<YouTubeTrailer> results;

    public ArrayList<YouTubeTrailer> getResults() {
        return results;
    }

    public void setResults(ArrayList<YouTubeTrailer> results) {
        this.results = results;
    }
}
