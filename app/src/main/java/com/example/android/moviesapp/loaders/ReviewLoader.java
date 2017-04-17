package com.example.android.moviesapp.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.moviesapp.models.Review;

import java.util.ArrayList;

import static com.example.android.moviesapp.rest.JsonParser.getReviewDataFromJson;
import static com.example.android.moviesapp.rest.ApiConnection.LOAD_REVIEWS;
import static com.example.android.moviesapp.rest.ApiConnection.getApiResponse;


public class ReviewLoader extends AsyncTaskLoader<ArrayList<Review>> {


    private final String LOG_TAG = TrailerInfoLoader.class.getSimpleName();
    private ArrayList<Review> reviewArrayList;
    private long tmdbId;

    public ReviewLoader(Context context, long tmdbId) {
        super(context);
        this.tmdbId = tmdbId;
    }

    @Override
    public void onStartLoading() {
        if (reviewArrayList == null) {
            forceLoad();
        } else {
            deliverResult(reviewArrayList);
        }
    }

    @Override
    public ArrayList<Review> loadInBackground() {
        String reviewJsonStr = getApiResponse(LOAD_REVIEWS, tmdbId);
        return getReviewDataFromJson(reviewJsonStr);
    }

    @Override
    public void deliverResult(ArrayList<Review> reviewList) {
        reviewArrayList = reviewList;
        super.deliverResult(reviewArrayList);
    }
}
