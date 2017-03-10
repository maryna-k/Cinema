package com.example.android.moviesapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.utilities.JsonParser.getIDsFromJson;
import static com.example.android.moviesapp.utilities.JsonParser.getMovieDataFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_MOVIE;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_MOVIES_IDS;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private final String LOG_TAG = MovieLoader.class.getSimpleName() + "LOG";
    private ArrayList<Movie> movieArrayList;

    public MovieLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        if(movieArrayList == null){
            Log.v(LOG_TAG, "Loader: onStartLoading");
            forceLoad();
        } else {
            Log.v(LOG_TAG, "Loader: onStartLoading");
            deliverResult(movieArrayList);
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground(){
        Log.v(LOG_TAG, "Loader: loadInBackground");
        ArrayList<Movie> movieArrayList = new ArrayList<Movie>();
        String idsJsonStr = getJsonResponse(LOAD_MOVIES_IDS, -1);
        try {
            ArrayList<Long> ids = getIDsFromJson(idsJsonStr);
            for(int i = 0; i < ids.size(); i++){
                long movie_id = ids.get(i);
                String movieJsonStr = getJsonResponse(LOAD_MOVIE, movie_id);
                Movie movie = getMovieDataFromJson(movieJsonStr);
                movieArrayList.add(movie);
            }
            return movieArrayList;
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
