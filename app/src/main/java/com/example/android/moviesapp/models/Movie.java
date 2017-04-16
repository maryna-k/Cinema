package com.example.android.moviesapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.example.android.moviesapp.rest.MDBConnection.searchCategories;

public class Movie implements Serializable {

    private String title;
    @SerializedName("overview")
    private String overview; //string for synopsis
    @SerializedName("vote_average")
    private double rating;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("genre_ids")
    private ArrayList<Integer> genreIds = new ArrayList<Integer>();
    private String genre;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterAddress; //small poster image
    @SerializedName("backdrop_path")
    private String backdropAddress; //header image
    @SerializedName("id")
    private long tmdb_id;
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
        this.tmdb_id = mdb_id;
        this.posterStoragePath = posterStoragePath;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        else {
            Movie m = (Movie)obj;
            return (this.title.equals(m.title) &&
                    this.overview.equals(m.overview) &&
                    this.rating == m.rating &&
                    this.voteCount == m.voteCount &&
                    this.genre == m.genre &&
                    this.releaseDate == m.releaseDate &&
                    this.posterAddress == m.posterAddress &&
                    this.tmdb_id == m.tmdb_id);
        }
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

    public void setGenreString() {
        String genreStr = "";
        for(int i = 0; i < genreIds.size(); i++){
            int id = genreIds.get(i);

            for (Map.Entry<String, String> entry : searchCategories.entrySet()) {
                if ((Integer.toString(id)).equals(entry.getValue())) {
                    genreStr = genreStr + entry.getKey();
                    if(i < genreIds.size() - 1) {
                        genreStr = genreStr + ", ";
                    }
                }
            }
        }
        this.genre = genreStr;
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

    public long getTmdb_id() {
        return tmdb_id;
    }

    public void setTmdb_id(long tmdb_id) {
        this.tmdb_id = tmdb_id;
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
