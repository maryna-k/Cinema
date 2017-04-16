package com.example.android.moviesapp.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.moviesapp.models.Movie;

import java.util.ArrayList;

import static com.example.android.moviesapp.rest.JsonParser.getMovieArrayFromJson;
import static com.example.android.moviesapp.rest.MDBConnection.LOAD_MOVIES;
import static com.example.android.moviesapp.rest.MDBConnection.getApiResponse;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private final String LOG_TAG = MovieLoader.class.getSimpleName();
    private ArrayList<Movie> movieArrayList;

    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    public void onStartLoading() {
        if (movieArrayList == null) {
            forceLoad();
        } else {
            deliverResult(movieArrayList);
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        String movieJsonStr = getApiResponse(LOAD_MOVIES, -1);
        return getMovieArrayFromJson(movieJsonStr);
    }

    @Override
    public void deliverResult(ArrayList<Movie> movieList) {
        movieArrayList = movieList;
        super.deliverResult(movieArrayList);
    }
}
