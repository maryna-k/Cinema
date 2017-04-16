package com.example.android.moviesapp.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.moviesapp.Objects.Review;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.android.moviesapp.utilities.JsonParser.getReviewDataFromJson;
import static com.example.android.moviesapp.utilities.MDBConnection.LOAD_REVIEWS;
import static com.example.android.moviesapp.utilities.MDBConnection.getJsonResponse;


public class ReviewLoader extends AsyncTaskLoader<ArrayList<Review>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();
    private ArrayList<Review> reviewArrayList;
    private long tmdb_id;

    public ReviewLoader(Context context, long tmdb_id){
        super(context);
        this.tmdb_id = tmdb_id;
    }

    @Override
    public void onStartLoading(){
        if(reviewArrayList == null) {
            forceLoad();
        } else {
            deliverResult(reviewArrayList);
        }
    }

    @Override
    public ArrayList<Review> loadInBackground() {
        String reviewJsonStr = getJsonResponse(LOAD_REVIEWS, tmdb_id);

        try {
            return getReviewDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(ArrayList<Review> reviewList){
        reviewArrayList = reviewList;
        super.deliverResult(reviewArrayList);
    }
}
