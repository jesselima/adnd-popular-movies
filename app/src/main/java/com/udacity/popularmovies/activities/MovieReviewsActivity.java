package com.udacity.popularmovies.activities;

import android.app.LoaderManager.LoaderCallbacks;
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
import com.udacity.popularmovies.adapters.MovieReviewsAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.loaders.ReviewListLoader;
import com.udacity.popularmovies.models.MovieReview;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


public class MovieReviewsActivity extends AppCompatActivity implements LoaderCallbacks<List<MovieReview>> {

    private static final String API_KEY = BuildConfig.API_KEY;

    // String to identify the activity when using logging messages
    private static final String LOG_TAG = MovieReviewsActivity.class.getSimpleName();
    // Movie List Loader ID
    private static final int MOVIE_REVIEWS_LOADER_ID = 3;
    // Global variable to be used with system language abbreviation in two letters
    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;
    private int page = 1;
    // Global toast object to avoid toast objects queue
    private Toast toast;
    // Vie objects to control UI warnings
    private TextView tvNetworkStatus;
    private ImageView imageViewNoReviews;
    private TextView textViewNoReviewsWarning;
    private ProgressBar loadingIndicator;
    // Objects ro set and control RecyclerView and the list of movie data
    private RecyclerView recyclerViewReviews;
    private MovieReviewsAdapter movieReviewsAdapter;
    private final ArrayList<MovieReview> movieReviewsList = new ArrayList<>();

    private int movieId;
    private String movieOriginalTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);

        loadingIndicator = findViewById(R.id.loading_indicator);
        imageViewNoReviews = findViewById(R.id.iv_no_reviews_placeholder);
        tvNetworkStatus = findViewById(R.id.tv_network_status);
        textViewNoReviewsWarning = findViewById(R.id.tv_warning_no_reviews);

        // RecyclerView references and setups
        recyclerViewReviews = findViewById(R.id.rv_movies_reviews);
        movieReviewsAdapter = new MovieReviewsAdapter(this, movieReviewsList);
        recyclerViewReviews.setAdapter(movieReviewsAdapter);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setHasFixedSize(true);

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
    public Loader<List<MovieReview>> onCreateLoader(int i, Bundle bundle) {

        Uri stringBaseUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + String.valueOf(movieId) + "/reviews");
        Uri.Builder uriBuilder = stringBaseUrl.buildUpon();

        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.PAGE, String.valueOf(page));

        return new ReviewListLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> movieReviews) {
        Log.d(LOG_TAG, "onLoadFinished Started. Outputting data...");

        // When the onCreateLoader finish its job, it will pass the data do this method.
        if (movieReviews == null || movieReviews.isEmpty()) {
            // If there is no movie to show give a warning to the user in the UI.
            showNoResultsWarning();
            hideLoadingIndicator();
            hideConnectionWarning();
        } else {
            hideLoadingIndicator();
            hideConnectionWarning();
            hideNoResultsWarning();
        }

        movieReviewsList.clear();
        movieReviewsList.addAll(movieReviews);
        movieReviewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<MovieReview>> loader) {}

    private void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }
    private void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showNoResultsWarning() {
        imageViewNoReviews.setVisibility(View.VISIBLE);
        textViewNoReviewsWarning.setVisibility(View.VISIBLE);
    }
    private void hideNoResultsWarning() {
        imageViewNoReviews.setVisibility(View.GONE);
        textViewNoReviewsWarning.setVisibility(View.GONE);
    }

    private void showConnectionWarning() {
        hideNoResultsWarning();
        recyclerViewReviews.setVisibility(View.GONE);
        tvNetworkStatus.setVisibility(View.VISIBLE);
    }
    private void hideConnectionWarning() {
        recyclerViewReviews.setVisibility(View.VISIBLE);
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
            loaderManager.initLoader(MOVIE_REVIEWS_LOADER_ID, null, this);
        }
    }

}
