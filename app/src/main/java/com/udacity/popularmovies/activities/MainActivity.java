package com.udacity.popularmovies.activities;


import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 1;

    // Global variable to be used with system language abbreviation in two letters
    private String loadApiLanguage = UrlParamValue.LANGUAGE_DEFAULT;
    private int page = 1;
    // Global toast object to avoid toast objects queue
    private Toast toast;

    private TextView tvNetworkStatus;
    private ImageView imageViewNoMovies;
    private TextView textViewNoImageWarning;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private MovieListAdapter movieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // References for loading indicator and warnings when the is no movies in the list.
        loadingIndicator = findViewById(R.id.loading_indicator);
        imageViewNoMovies = findViewById(R.id.iv_no_movies_placeholder);
        textViewNoImageWarning = findViewById(R.id.tv_warning_no_movies);
        // Reference to the TextView that show to the user when there is no active connection to internet.
        tvNetworkStatus = findViewById(R.id.tv_network_status);
        recyclerView = findViewById(R.id.rv_movies);

        /**
         * Load the list of available api languages from {@link ApiConfig} according to api documentation.
         */
        String[] apiLanguagesList = ApiConfig.LANGUAGES;

        // Verify is the api supports the system language. If not API data will be load in english as default
        if (Arrays.asList(apiLanguagesList).contains(Resources.getSystem().getConfiguration().locale.getLanguage())){
            loadApiLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
        }else {
            doToast(getResources().getString(R.string.warning_language));
        }

        if (!NetworkUtils.isDeviceConnected(this)){
            hideLoadingIndicator();
            hideNoResultsWarning();
            showConnectionWarning();
        }else {
            // Kick off the loader
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        }

    } // Closes onCreate


    // Implementation of loader call backs
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle bundle) {

        Uri getBaseMovieListUrl = Uri.parse(ApiConfig.getBaseMovieApiUrlV3());
        Uri.Builder uriBuilder = getBaseMovieListUrl.buildUpon();

        uriBuilder.appendQueryParameter(UrlParamKey.API_KEY, ApiKey.getApiKey());
        uriBuilder.appendQueryParameter(UrlParamKey.LANGUAGE, loadApiLanguage);
        uriBuilder.appendQueryParameter(UrlParamKey.SORT_BY, UrlParamValue.POPULARITY_DESC);
        uriBuilder.appendQueryParameter(UrlParamKey.INCLUDE_ADULT, UrlParamValue.INCLUDE_ADULT_FALSE);
        uriBuilder.appendQueryParameter(UrlParamKey.PAGE, String.valueOf(page));

        return new MovieListLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        hideLoadingIndicator();
        hideConnectionWarning();

        // When the onCreateLoader finish its job, it will pass the data do this method.
        if (movies == null || movies.isEmpty()){
            showNoResultsWarning();
        }else {
            movieListAdapter = new MovieListAdapter(this, movies);
            recyclerView.setAdapter(movieListAdapter);
            GridLayoutManager layout = new GridLayoutManager(this,2);
            recyclerView.setLayoutManager(layout);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Check internet connection when activity is resumed.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkUtils.isDeviceConnected(this)){
            hideConnectionWarning();
        }else {
            showConnectionWarning();
        }
    }

    /**
     * Check internet connection when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.isDeviceConnected(this)){
            hideConnectionWarning();
        }else {
            showConnectionWarning();
        }
    }

    /**
     * Check internet connection when activity is restarted.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (NetworkUtils.isDeviceConnected(this)){
            hideConnectionWarning();
        }else {
            showConnectionWarning();
        }
    }

    private void showLoadingIndicator(){
        loadingIndicator.setVisibility(View.VISIBLE);
    }
    private void hideLoadingIndicator(){
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showNoResultsWarning(){
        imageViewNoMovies.setVisibility(View.VISIBLE);
        textViewNoImageWarning.setVisibility(View.VISIBLE);
    }
    private void hideNoResultsWarning(){
        imageViewNoMovies.setVisibility(View.GONE);
        textViewNoImageWarning.setVisibility(View.GONE);
    }

    private void showConnectionWarning(){
        recyclerView.setVisibility(View.GONE);
        tvNetworkStatus.setVisibility(View.VISIBLE);
        hideNoResultsWarning();
    }
    private void hideConnectionWarning(){
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
