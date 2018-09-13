package com.udacity.popularmovies.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;

public class Player1Activity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{



    private static final String API_KEY_YOUTUBE = ApiConfig.getYoutubeApiKey();

    // String to identify the activity when using logging messages
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    private String videoID;
    YouTubePlayerView youTubeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_1);

        getIncomingIntent();
        Log.d("Video ID: ", videoID);

        youTubeView = findViewById(R.id.youTube_player_view);
        youTubeView.initialize(API_KEY_YOUTUBE, this);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(videoID);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Oh no! " + youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("videoID")) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                videoID = bundle.getString("videoID");
            }
        }
    }
}
