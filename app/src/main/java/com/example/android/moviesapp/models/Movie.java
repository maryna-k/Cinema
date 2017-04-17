package com.example.android.moviesapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.example.android.moviesapp.rest.ApiConnection.searchCategories;

public class Movie implements Serializable {

    private String title;
    @SerializedName("overview")
    private String overview; //string for synopsis
    @SerializedName("vote_average")
    private double rating;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("genre_ids")
    private ArrayList<Integer> genreIds = new ArrayList<Integer>(1);
    private ArrayList<Genre> genres = new ArrayList<>(1);
    private String genre;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterAddress; //small poster image
    @SerializedName("backdrop_path")
    private String backdropAddress; //header image
    @SerializedName("id")
    private long tmdbId;
    private String posterStoragePath;

    public Movie() {}

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
        this.tmdbId = mdb_id;
        this.posterStoragePath = posterStoragePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        else {
            Movie m = (Movie) obj;
            return (this.title.equals(m.title) &&
                    this.overview.equals(m.overview) &&
                    this.rating == m.rating &&
                    this.voteCount == m.voteCount &&
                    this.genre == m.genre &&
                    this.releaseDate == m.releaseDate &&
                    this.posterAddress == m.posterAddress &&
                    this.tmdbId == m.tmdbId);
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    //helper method that binds genre ids and their corresponding names and sets a genre String
    public void setGenreStringById() {
        String genreStr = "";
        for (int i = 0; i < genreIds.size(); i++) {
            int id = genreIds.get(i);

            for (Map.Entry<String, String> entry : searchCategories.entrySet()) {
                if ((Integer.toString(id)).equals(entry.getValue())) {
                    genreStr = genreStr + entry.getKey();
                    if (i < genreIds.size() - 1) {
                        genreStr = genreStr + ", ";
                    }
                }
            }
        }
        this.genre = genreStr;
        genreIds = null;
    }

    //helper method that sets a genre String from the arraylist of Genre objects
    public void setGenreStringByName() {
        String genreStr = "";
        for (int i = 0; i < genres.size(); i++) {
            String name = genres.get(i).getName();
            genreStr += name;
            if (i < genres.size() - 1) {
                genreStr = genreStr + ", ";
            }
        }
        this.genre = genreStr;
        genreIds = null;
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

    public String getBackdropAddress() {
        return backdropAddress;
    }

    public void setBackdropAddress(String backdropAddress) {
        this.backdropAddress = backdropAddress;
    }

    public long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public void setPosterStoragePath(String path) {
        posterStoragePath = path;
    }

    public String getPosterStoragePath() {
        return posterStoragePath;
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
