package com.udacity.popularmovies.activities;


import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.MovieListAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiConfig.UrlParamKey;
import com.udacity.popularmovies.config.ApiConfig.UrlParamValue;
import com.udacity.popularmovies.config.ApiKey;
import com.udacity.popularmovies.loaders.MovieListLoader;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.AdaptiveGridLayout;
import com.udacity.popularmovies.utils.BottomNavigationBehavior;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    // Movie List Loader ID
    private static final int MOVIE_LOADER_ID = 1;
    // Global variable to be used with system language abbreviation in two letters
    private String loadApiLanguage = UrlParamValue.LANGUAGE_DEFAULT;
    // Page value for pagination control
    private int page = 1;
    // Sort by default value
    private String sortBy = UrlParamValue.POPULARITY_DESC;
    // Global toast object to avoid toast objects queue
    private Toast toast;
    // Vie objects to control UI warnings
    private TextView tvNetworkStatus;
    private ImageView imageViewNoMovies;
    private TextView textViewNoImageWarning;
    private ProgressBar loadingIndicator;
    // Objects ro set and control RecyclerView and the list of movie data
    private RecyclerView recyclerView;
    private MovieListAdapter movieListAdapter;
    private final ArrayList<Movie> movieList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Set and handle actions on BottonNavigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.button_nav_most_popular:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        }else {
                            page = 1;
                            sortBy = UrlParamValue.POPULARITY_DESC;
                            restartLoader();
                        }
                        break;
                    case R.id.button_nav_most_rated:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        }else {
                            page = 1;
                            sortBy = UrlParamValue.VOTE_COUNT_DESC;
                            restartLoader();
                        }
                        break;
                    case R.id.button_pagination_backward:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        }else {
                            paginationBackward();
                        }
                        break;
                    case R.id.button_pagination_forward:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        }else {
                            paginationForward();
                        }
                        break;
                }
                return true;
            }
        });

        // attaching bottom sheet behaviour - hide / show on scroll
        // Author: Android Hive, Source: https://www.androidhive.info/2017/12/android-working-with-bottom-navigation/
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());


        loadingIndicator = findViewById(R.id.loading_indicator);
        imageViewNoMovies = findViewById(R.id.iv_no_movies_placeholder);
        textViewNoImageWarning = findViewById(R.id.tv_warning_no_movies);
        tvNetworkStatus = findViewById(R.id.tv_network_status);

        // Setup the adapter adapter and RecyclerView to receive data.
        movieListAdapter = new MovieListAdapter(this, movieList);
        recyclerView = findViewById(R.id.rv_movies);
        recyclerView.setAdapter(movieListAdapter);
        int numberOfColumns = AdaptiveGridLayout.calculateNoOfColumns(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);

        /*
         * Load the list of available api languages from {@link ApiConfig} according to api documentation.
         */
        String[] apiLanguagesList = ApiConfig.LANGUAGES;

        // Verify is the api supports the system language. If not API data will be load in english as default
        if (Arrays.asList(apiLanguagesList).contains(Resources.getSystem().getConfiguration().locale.getLanguage())) {
            loadApiLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
        } else {
            doToast(getResources().getString(R.string.warning_language));
        }

        // Check internet connection before start the loader
        if (!NetworkUtils.isDeviceConnected(this)) {
            hideLoadingIndicator();
            hideNoResultsWarning();
            showConnectionWarning();
        } else {
            // Shows loading indicator and Kick off the loader
            showLoadingIndicator();
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        }
    } // Closes onCreate

    /*** Methods for BottonNavigation control ***/
    private void restartLoader() {
        showLoadingIndicator();
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    private void paginationBackward() {
        movieList.clear();
        hideNoResultsWarning();
        hideConnectionWarning();
        if (page == 1){
            doToast(getResources().getString(R.string.you_are_at_page_1));
        }else {
            page--;
            movieList.clear();
            restartLoader();
            doToast(getResources().getString(R.string.page) + String.valueOf(page));
        }
    }
    private void paginationForward() {
        movieList.clear();
        hideNoResultsWarning();
        hideConnectionWarning();
        page++;
        restartLoader();
        doToast(String.valueOf("Page " + page));
    }

    // Implementation of loader call backs
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader Started...");

        Uri getBaseMovieListUrl = Uri.parse(ApiConfig.getBaseMovieApiUrlV3());
        Uri.Builder uriBuilder = getBaseMovieListUrl.buildUpon();

        uriBuilder.appendQueryParameter(UrlParamKey.API_KEY, ApiKey.getApiKey());
        uriBuilder.appendQueryParameter(UrlParamKey.LANGUAGE, loadApiLanguage);
        uriBuilder.appendQueryParameter(UrlParamKey.SORT_BY, sortBy);
        uriBuilder.appendQueryParameter(UrlParamKey.INCLUDE_ADULT, UrlParamValue.INCLUDE_ADULT_FALSE);
        uriBuilder.appendQueryParameter(UrlParamKey.PAGE, String.valueOf(page));

        return new MovieListLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        Log.d(LOG_TAG, "onLoadFinished Started. Outputting data...");

        // When the onCreateLoader finish its job, it will pass the data do this method.
        if (movies == null || movies.isEmpty()) {
            showNoResultsWarning();
        } else {
            hideLoadingIndicator();
            hideConnectionWarning();
            hideNoResultsWarning();
            movieList.clear();
            movieList.addAll(movies);
            movieListAdapter.notifyDataSetChanged(); //TODO: CHECK IT!!!!
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    /**
     * Check internet connection when activity is resumed.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    /**
     * Check internet connection when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    /**
     * Check internet connection when activity is restarted.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    private void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showNoResultsWarning() {
        imageViewNoMovies.setVisibility(View.VISIBLE);
        textViewNoImageWarning.setVisibility(View.VISIBLE);
    }

    private void hideNoResultsWarning() {
        imageViewNoMovies.setVisibility(View.GONE);
        textViewNoImageWarning.setVisibility(View.GONE);
    }

    private void showConnectionWarning() {
        recyclerView.setVisibility(View.GONE);
        tvNetworkStatus.setVisibility(View.VISIBLE);
        hideNoResultsWarning();
    }

    private void hideConnectionWarning() {
        recyclerView.setVisibility(View.VISIBLE);
        tvNetworkStatus.setVisibility(View.GONE);
    }

    /**
     * This method makes the reuse of toast object to avoid toasts queue
     *
     * @param toastThisText is the text you want to show in the toast.
     */
    private void doToast(String toastThisText) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, toastThisText, Toast.LENGTH_LONG);
        toast.show();
    }

}
