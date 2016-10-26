package com.example.android.moviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /*Api for adding fragments at run-time:
    https://developer.android.com/training/basics/fragments/fragment-ui.html */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if the activity is restored, no need to create new fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieGridFragment()).commit();
        }
    }
}
