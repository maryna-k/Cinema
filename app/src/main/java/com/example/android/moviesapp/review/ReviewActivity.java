package com.example.android.moviesapp.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.SettingsActivity;

import java.util.ArrayList;


public class ReviewActivity extends AppCompatActivity{

    ArrayList<Review> reviewList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ReviewAdapter mAdapter;

    private final String LOG_TAG = ReviewActivity.class.getSimpleName();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Set a Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        reviewList = (ArrayList<Review>) intent.getSerializableExtra("reviews");
        getSupportActionBar().setTitle(intent.getStringExtra("title"));

        if (savedInstanceState == null) {

        }
        mRecyclerView = (RecyclerView) findViewById(R.id.review_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new ReviewAdapter(reviewList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
