package com.example.android.moviesapp.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.moviesapp.models.YouTubeTrailer;

import java.util.ArrayList;

import static com.example.android.moviesapp.rest.JsonParser.getTrailerDataFromJson;
import static com.example.android.moviesapp.rest.MDBConnection.LOAD_TRAILER_INFO;
import static com.example.android.moviesapp.rest.MDBConnection.getApiResponse;


public class TrailerInfoLoader extends AsyncTaskLoader<ArrayList<YouTubeTrailer>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();
    private ArrayList<YouTubeTrailer> trailerArrayList;
    private long tmdb_id;

    public TrailerInfoLoader(Context context, long tmdb_id) {
        super(context);
        this.tmdb_id = tmdb_id;
    }

    @Override
    public void onStartLoading() {
        if (trailerArrayList == null) {
            forceLoad();
        } else {
            deliverResult(trailerArrayList);
        }
    }

    @Override
    public ArrayList<YouTubeTrailer> loadInBackground() {
        String trailerJsonStr = getApiResponse(LOAD_TRAILER_INFO, tmdb_id);
        return getTrailerDataFromJson(trailerJsonStr);
    }

    @Override
    public void deliverResult(ArrayList<YouTubeTrailer> trailerList) {
        trailerArrayList = trailerList;
        super.deliverResult(trailerArrayList);
    }
}
