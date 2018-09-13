package com.udacity.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;

public class Player2Activity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    Toast toast;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private String videoID;
    private String videoTitle;
    private TextView textViewVideoTitle;

    /* Listener Implementation */
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_2);

        getIncomingIntent();
        Log.d("Video ID: ", videoID);
        Log.d("Video videoTitle: ", videoTitle);

        textViewVideoTitle = findViewById(R.id.text_view_video_title);
        if (!TextUtils.isEmpty(videoTitle)) {
            textViewVideoTitle.setText(videoTitle);
        }

        youTubeView = findViewById(R.id.youtube_view_player);
        youTubeView.initialize(ApiConfig.getYoutubeApiKey(), this);

        /* Listener Implementation  */
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("videoID") || getIntent().hasExtra("videoTitle")) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                videoID = bundle.getString("videoID");
                videoTitle = bundle.getString("videoTitle");
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {
            youTubePlayer.cueVideo(videoID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(ApiConfig.getYoutubeApiKey(), this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }


    /**
     * The above creates classes that implement the YouTubePlayer.PlaybackEventListener and
     * YouTubePlayer.PlayerStateChangeListener interfaces. For each class, I have implemented the
     * interface methods and included a comment of when the callback is invoked. You can take
     * whatever action you want in each callback. For our example, I have included a Toast output
     * for the onPlaying(), onPaused() and onStopped() methods that will output a message
     * when the event happens.
     */

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            toastIt("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }


    /**
     * This method makes the reuse of toast object to avoid toasts queue
     *
     * @param toastItText is the text you want to show in the toast.
     */
    private void toastIt(String toastItText) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, toastItText, Toast.LENGTH_LONG);
        toast.show();
    }
}
