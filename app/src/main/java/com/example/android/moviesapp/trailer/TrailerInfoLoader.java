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


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName() + "LOG";
    private ArrayList<YouTubeTrailer> trailerArrayList;

    public TrailerInfoLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        if(trailerArrayList == null) {
            forceLoad();
        } else{
            deliverResult(trailerArrayList);
        }
        Log.v(LOG_TAG, "Loader: onStartLoading");
    }

    @Override
    public ArrayList<YouTubeTrailer> loadInBackground() {
        Log.v(LOG_TAG, "Loader: loadInBackground");
        String trailerJsonStr = getJsonResponse(LOAD_TRAILER_INFO);

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
