package com.example.android.moviesapp.review;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.moviesapp.trailer.TrailerInfoLoader;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.utilities.JsonParser.getReviewDataFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_REVIEWS;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;


public class ReviewLoader extends AsyncTaskLoader<ArrayList<Review>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();

    public ReviewLoader(Context context){
        super(context);
    }

    @Override
    public void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<Review> loadInBackground() {
        String reviewJsonStr = getJsonResponse(LOAD_REVIEWS);

        try {
            return getReviewDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
