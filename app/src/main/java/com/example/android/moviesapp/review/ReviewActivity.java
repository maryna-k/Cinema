package com.example.android.moviesapp.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.SettingsActivity;

import java.util.ArrayList;


public class ReviewActivity extends AppCompatActivity{

    private ArrayList<Review> reviewList;

    private final String LOG_TAG = ReviewActivity.class.getSimpleName() + "LOG";

    public static final String REVIEW_LIST = "review_list";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        reviewList = (ArrayList<Review>) intent.getSerializableExtra("reviews");
        getSupportActionBar().setTitle(intent.getStringExtra("title"));

        if (savedInstanceState == null) {
            ReviewDialogFragment fragment = new ReviewDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable(REVIEW_LIST, reviewList);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.review_container, fragment).commit();
        }
        Log.v(LOG_TAG, "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.v(LOG_TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
