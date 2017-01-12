package com.example.android.moviesapp;

import java.io.Serializable;

public class Movie implements Serializable{

    private String title;
    private String overview; //string for synopsis
    private double rating;
    private int vote_count;
    private String releaseDate;
    private String imageAddress;
    private long mdb_id;
    private boolean favorite;

    public Movie() {
    }

    public Movie(String title, String overview, double rating, String releaseDate, String imageAddress) {
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.imageAddress = imageAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public long getMdb_id() {
        return mdb_id;
    }

    public void setMdb_id(long mdb_id) {
        this.mdb_id = mdb_id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", rating=" + rating +
                ", releaseDate='" + releaseDate + '\'' +
                ", imageAddress='" + imageAddress + '\'' +
                '}';
    }
}
