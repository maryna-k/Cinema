package com.example.android.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.android.moviesapp.review.Review;
import com.example.android.moviesapp.review.ReviewActivity;
import com.example.android.moviesapp.review.ReviewDialogFragment;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity implements DetailFragment.ReviewFragmentCallback,
        DetailFragment.SettingsFragmentCallback {

    private final String LOG_TAG = DetailActivity.class.getSimpleName() + "LOG";
    Toolbar toolbar;
    private boolean isTabletTwoPane;
    private boolean isTabletPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTabletTwoPane = getResources().getBoolean(R.bool.isTabletTwoPane);
        isTabletPortrait = getResources().getBoolean(R.bool.isTabletPortrait);
        if (isTabletTwoPane) {
            Fragment fr = getSupportFragmentManager().findFragmentById(R.id.detail_container_detail_activity);
            getSupportFragmentManager().beginTransaction().remove(fr).commit();
            this.finish();
        } else {
            setContentView(R.layout.activity_detail);

            toolbar = (Toolbar) findViewById(R.id.detail_activity_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.background_toolbar_translucent));
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container_detail_activity, new DetailFragment()).commit();
            }
            Log.v(LOG_TAG, "onCreate");
        }
    }

    @Override
    public void onSettingsMenuItemSelected(boolean selected){
        if(selected){
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    public void onMoreReviewsSelected(ArrayList<Review> reviewList, String title, int color) {
        /*start ReviewActivity on a phone, but display a dialog on a tablet*/
        if(!isTabletTwoPane && !isTabletPortrait) {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("reviews", reviewList);
            intent.putExtra("title", title);
            intent.putExtra("color", color);
            startActivity(intent);
        } else {
            DialogFragment newFragment = ReviewDialogFragment.newInstance(reviewList, title);
            newFragment.show(getSupportFragmentManager(), "dialog");
        }
    }
}
