package com.example.android.moviesapp.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.moviesapp.models.YouTubeTrailer;

import java.util.ArrayList;

import static com.example.android.moviesapp.rest.JsonParser.getTrailerDataFromJson;
import static com.example.android.moviesapp.rest.ApiConnection.LOAD_TRAILER_INFO;
import static com.example.android.moviesapp.rest.ApiConnection.getApiResponse;


public class TrailerInfoLoader extends AsyncTaskLoader<ArrayList<YouTubeTrailer>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();
    private ArrayList<YouTubeTrailer> trailerArrayList;
    private long tmdbId;

    public TrailerInfoLoader(Context context, long tmdbId) {
        super(context);
        this.tmdbId = tmdbId;
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
        String trailerJsonStr = getApiResponse(LOAD_TRAILER_INFO, tmdbId);
        return getTrailerDataFromJson(trailerJsonStr);
    }

    @Override
    public void deliverResult(ArrayList<YouTubeTrailer> trailerList) {
        trailerArrayList = trailerList;
        super.deliverResult(trailerArrayList);
    }
}
