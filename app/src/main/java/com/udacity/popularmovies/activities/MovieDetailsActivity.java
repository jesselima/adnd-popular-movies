package com.udacity.popularmovies.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.udacity.popularmovies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getIncomingIntent();

    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("movieId")){
           Bundle bundle = getIntent().getExtras();
           movieId = bundle.getInt("movieId");
           Log.v("ID From Recycler:", String.valueOf(movieId));

        }
    }
}
