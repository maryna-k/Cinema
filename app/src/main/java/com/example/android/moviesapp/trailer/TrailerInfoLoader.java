package com.example.android.moviesapp.trailer;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.utilities.JsonParser.getTrailerDataFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_TRAILER_INFO;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;


public class TrailerInfoLoader extends AsyncTaskLoader<ArrayList<YouTubeTrailer>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();

    public TrailerInfoLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<YouTubeTrailer> loadInBackground() {
        String trailerJsonStr = getJsonResponse(LOAD_TRAILER_INFO);

        try {
            return getTrailerDataFromJson(trailerJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
