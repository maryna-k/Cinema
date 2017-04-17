package com.example.android.moviesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.fragments.DetailFragment;
import com.example.android.moviesapp.fragments.ReviewDialogFragment;
import com.example.android.moviesapp.models.Review;

import java.util.ArrayList;

import butterknife.BindBool;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements DetailFragment.ReviewFragmentCallback,
        DetailFragment.SettingsFragmentCallback {

    private final String LOG_TAG = DetailActivity.class.getSimpleName() + "LOG";
    private Toolbar toolbar;
    @BindBool(R.bool.isTabletTwoPane) boolean isTabletTwoPane;
    @BindBool(R.bool.isTabletPortrait) boolean isTabletPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
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
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container_detail_activity, new DetailFragment()).commit();
            }
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
