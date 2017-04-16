package com.example.android.moviesapp.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.moviesapp.Objects.Movie;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.utilities.JsonParser.getMovieArrayFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_MOVIES;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private final String LOG_TAG = MovieLoader.class.getSimpleName();
    private ArrayList<Movie> movieArrayList;

    public MovieLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        if(movieArrayList == null){
            forceLoad();
        } else {
            deliverResult(movieArrayList);
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground(){
        String movieJsonStr = getJsonResponse(LOAD_MOVIES, -1);

        try {
            return getMovieArrayFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(ArrayList<Movie> movieList){
        movieArrayList = movieList;
        super.deliverResult(movieArrayList);
    }
}
