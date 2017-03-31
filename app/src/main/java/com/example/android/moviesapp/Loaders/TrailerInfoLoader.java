package com.example.android.moviesapp.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.moviesapp.Objects.YouTubeTrailer;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.utilities.JsonParser.getTrailerDataFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_TRAILER_INFO;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;


public class TrailerInfoLoader extends AsyncTaskLoader<ArrayList<YouTubeTrailer>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();
    private ArrayList<YouTubeTrailer> trailerArrayList;
    private long tmdb_id;

    public TrailerInfoLoader(Context context, long tmdb_id){
        super(context);
        this.tmdb_id = tmdb_id;
    }

    @Override
    public void onStartLoading(){
        if(trailerArrayList == null) {
            forceLoad();
        } else{
            deliverResult(trailerArrayList);
        }
    }

    @Override
    public ArrayList<YouTubeTrailer> loadInBackground() {
        String trailerJsonStr = getJsonResponse(LOAD_TRAILER_INFO, tmdb_id);

        try {
            return getTrailerDataFromJson(trailerJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(ArrayList<YouTubeTrailer> trailerList){
        trailerArrayList = trailerList;
        super.deliverResult(trailerArrayList);
    }
}
