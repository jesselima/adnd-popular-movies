package com.udacity.popularmovies.activities;


import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.MovieVideosAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.loaders.VideoListLoader;
import com.udacity.popularmovies.models.MovieVideo;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieVideosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieVideo>> {

    private static final String API_KEY = BuildConfig.API_KEY;

    // String to identify the activity when using logging messages
    private static final String LOG_TAG = MovieVideosActivity.class.getSimpleName();
    // Movie List Loader ID
    private static final int MOVIE_VIDEOS_LOADER_ID = 4;
    // Global variable to be used with system language abbreviation in two letters
    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;
    // Global toast object to avoid toast objects queue
    private Toast toast;
    // Vie objects to control UI warnings
    private TextView tvNetworkStatus;
    private ImageView imageViewNoVideos;
    private TextView textViewNoVideosWarning;
    private ProgressBar loadingIndicator;
    // Objects ro set and control RecyclerView and the list of movie data
    private RecyclerView recyclerViewVideos;
    private MovieVideosAdapter movieVideosAdapter;
    private final ArrayList<MovieVideo> movieVideosList = new ArrayList<>();

    private int movieId;
    private String movieOriginalTitle = "";
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_videos);

        loadingIndicator = findViewById(R.id.loading_indicator);
        imageViewNoVideos = findViewById(R.id.iv_no_movies_placeholder);
        tvNetworkStatus = findViewById(R.id.tv_network_status);
        textViewNoVideosWarning = findViewById(R.id.tv_warning_no_reviews);

        // RecyclerView references and setups
        recyclerViewVideos = findViewById(R.id.rv_movies_videos);
        movieVideosAdapter = new MovieVideosAdapter(this, movieVideosList);
        recyclerViewVideos.setAdapter(movieVideosAdapter);
        recyclerViewVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVideos.setHasFixedSize(true);

        getIncomingIntent();
        checkConnectionAndStartLoader();
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(ApiConfig.JsonKey.ID) && getIntent().hasExtra(ApiConfig.JsonKey.ORIGINAL_TITLE)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                movieId = bundle.getInt(ApiConfig.JsonKey.ID);
                movieOriginalTitle = bundle.getString(ApiConfig.JsonKey.ORIGINAL_TITLE);
            }
        }
    }

    @Override
    public Loader<List<MovieVideo>> onCreateLoader(int i, Bundle bundle) {

        Uri stringBaseUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + String.valueOf(movieId) + "/videos");
        Uri.Builder uriBuilder = stringBaseUrl.buildUpon();

        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.PAGE, String.valueOf(page));

        Log.d(LOG_TAG + "Request URL", uriBuilder.toString());
        return new VideoListLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> movieVideos) {
        Log.d(LOG_TAG, "onLoadFinished Started. Outputting data...");

        // When the onCreateLoader finish its job, it will pass the data do this method.
        if (movieVideos == null || movieVideos.isEmpty()) {
            // If there is no movie to show give a warning to the user in the UI.
            showNoResultsWarning();
        } else {
            hideLoadingIndicator();
            hideConnectionWarning();
            hideNoResultsWarning();
        }

        movieVideosList.clear();
        movieVideosList.addAll(movieVideos);
        movieVideosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<MovieVideo>> loader) {

    }

    private void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }
    private void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showNoResultsWarning() {
        imageViewNoVideos.setVisibility(View.VISIBLE);
        textViewNoVideosWarning.setVisibility(View.VISIBLE);
    }
    private void hideNoResultsWarning() {
        imageViewNoVideos.setVisibility(View.GONE);
        textViewNoVideosWarning.setVisibility(View.GONE);
    }

    private void showConnectionWarning() {
        hideNoResultsWarning();
        recyclerViewVideos.setVisibility(View.GONE);
        tvNetworkStatus.setVisibility(View.VISIBLE);
    }
    private void hideConnectionWarning() {
        recyclerViewVideos.setVisibility(View.VISIBLE);
        tvNetworkStatus.setVisibility(View.GONE);
    }

    private void checkConnectionAndStartLoader() {
        // Check internet connection before start the loader
        if (!NetworkUtils.isDeviceConnected(this)) {
            // If device is not connected show connections warnings on the screen.
            hideLoadingIndicator();
            hideNoResultsWarning();
            showConnectionWarning();
        } else {
            // Is device is connected Shows loading indicator and Kick off the loader
            showLoadingIndicator();
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_VIDEOS_LOADER_ID, null, this);
        }
    }




}
