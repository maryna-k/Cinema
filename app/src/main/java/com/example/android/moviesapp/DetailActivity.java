package com.example.android.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.moviesapp.review.Review;
import com.example.android.moviesapp.review.ReviewActivity;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity implements DetailFragment.ReviewFragmentCallback {

    private final String LOG_TAG = DetailActivity.class.getSimpleName() + "LOG";
    Toolbar toolbar;
    private boolean isTabletTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTabletTwoPane = getResources().getBoolean(R.bool.isTabletTwoPane);
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
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_toolbar_translucent));
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container_detail_activity, new DetailFragment()).commit();
            }
            Log.v(LOG_TAG, "onCreate");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        Log.v(LOG_TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMoreReviewsSelected(ArrayList<Review> reviewList, String title) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("reviews", reviewList);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
