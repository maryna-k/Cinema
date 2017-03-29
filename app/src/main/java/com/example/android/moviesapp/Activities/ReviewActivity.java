package com.example.android.moviesapp.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.Objects.Review;
import com.example.android.moviesapp.Fragments.ReviewDialogFragment;

import java.util.ArrayList;


public class ReviewActivity extends AppCompatActivity{

    private final String LOG_TAG = ReviewActivity.class.getSimpleName();

    private ArrayList<Review> reviewList;
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                (intent.getIntExtra("color", getResources().getColor(R.color.colorPrimaryDark))));

        if (savedInstanceState == null) {
            ReviewDialogFragment fragment = new ReviewDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable(REVIEW_LIST, reviewList);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.review_container, fragment).commit();
        }
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
