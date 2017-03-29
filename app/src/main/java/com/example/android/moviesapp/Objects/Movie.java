package com.example.android.moviesapp.Objects;

import java.io.Serializable;

public class Movie implements Serializable {

    private String title;
    private String overview; //string for synopsis
    private double rating;
    private int voteCount;
    private String genre;
    private String releaseDate;
    private String posterAddress; //small poster image
    private String backdropAddress; //header image
    private long mdb_id;
    private String posterStoragePath;
    private boolean favorite;

    public Movie() {
    }

    public Movie(String title, String overview, double rating, int voteCount, String genre,
                 String releaseDate, String posterAddress, String posterStoragePath, String backdropAddress, Long mdb_id) {
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.voteCount = voteCount;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.posterAddress = posterAddress;
        this.backdropAddress = backdropAddress;
        this.mdb_id = mdb_id;
        this.posterStoragePath = posterStoragePath;
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

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getGenre(){
        return genre;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterAddress() {
        return posterAddress;
    }

    public void setPosterAddress(String imageAddress) {
        this.posterAddress = imageAddress;
    }

    public String getBackdropAddress(){
        return backdropAddress;
    }

    public void setBackdropAddress(String backdropAddress){
        this.backdropAddress = backdropAddress;
    }

    public long getMdb_id() {
        return mdb_id;
    }

    public void setMdb_id(long mdb_id) {
        this.mdb_id = mdb_id;
    }

    public void setPosterStoragePath(String path){
        posterStoragePath = path;
    }

    public String getPosterStoragePath(){
        return posterStoragePath;
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
                ", posterAddress='" + posterAddress + '\'' +
                '}';
    }
}
