package com.example.android.moviesapp.rest;

import android.util.Log;

import com.example.android.moviesapp.models.Movie;
import com.example.android.moviesapp.models.MovieResponse;
import com.example.android.moviesapp.models.Review;
import com.example.android.moviesapp.models.ReviewResponse;
import com.example.android.moviesapp.models.TrailerResponse;
import com.example.android.moviesapp.models.YouTubeTrailer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;


public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName() + "LOG";

    //parse json received from request to get multiple movies of particular genre
    public static ArrayList<Movie> getMovieArrayFromJson(String movieJsonStr) {
        if (movieJsonStr == null) return null;
        Gson gson = new GsonBuilder().create();
        MovieResponse movieResponse = gson.fromJson(movieJsonStr, MovieResponse.class);
        return movieResponse.getResults();
    }

    //parse json received from request to get single movie by its id
    public static Movie getSingleMovieFromJson(String movieJsonStr){
        if (movieJsonStr == null) return null;
        Gson gson = new GsonBuilder().create();
        Movie movie = gson.fromJson(movieJsonStr, Movie.class);
        Log.v(LOG_TAG, movie.getTitle());
        movie.setGenreStringByName();
        Log.v(LOG_TAG, movie.getGenre());
        return movie;
    }

    public static ArrayList<YouTubeTrailer> getTrailerDataFromJson(String trailerJsonStr) {
        if (trailerJsonStr == null) return null;
        Gson gson = new GsonBuilder().create();
        TrailerResponse tResponse = gson.fromJson(trailerJsonStr, TrailerResponse.class);
        return findTrailersFromList(tResponse.getResults());
    }

    //helper method that discards all types of videos except trailers
    private static ArrayList<YouTubeTrailer> findTrailersFromList(ArrayList<YouTubeTrailer> fullList){
        ArrayList<YouTubeTrailer> trailers = new ArrayList<>();
        for(int i = 0; i < fullList.size(); i++){
            if(fullList.get(i).getType().equals("Trailer")){
                trailers.add(fullList.get(i));
            }
        }
        return trailers;
    }

    public static ArrayList<Review> getReviewDataFromJson(String reviewJsonStr) {
        if (reviewJsonStr == null) return null;
        Gson gson = new GsonBuilder().create();
        ReviewResponse rResponse = gson.fromJson(reviewJsonStr, ReviewResponse.class);
        return rResponse.getResults();
    }
}
