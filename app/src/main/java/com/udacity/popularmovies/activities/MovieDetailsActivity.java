package com.udacity.popularmovies.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiKey;
import com.udacity.popularmovies.loaders.MovieLoader;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.Arrays;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    private int movieId;
    public static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private static final int MOVIE_DETAILS_LOADER_ID = 2;
    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getIncomingIntent();


        if (!NetworkUtils.isDeviceConnected(this)){
            doToast("You are not connected!");
        }else {
            // Shows loading indicator and Kick off the loader
            doToast("Kicking loader off...");
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        }

        String[] apiLanguagesList = ApiConfig.LANGUAGES;

        // Verify is the api supports the system language. If not API data will be load in english as default
        if (Arrays.asList(apiLanguagesList).contains(Resources.getSystem().getConfiguration().locale.getLanguage())){
            loadApiLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
        }else {
            doToast(getResources().getString(R.string.warning_language));
        }

    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("movieId")){
           Bundle bundle = getIntent().getExtras();
           movieId = bundle.getInt("movieId");
           Log.v("ID From Recycler:", String.valueOf(movieId));

        }
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader Started...");

        Uri baseUrl = Uri.parse(ApiConfig.getBaseMovieDetailsUrl() + String.valueOf(movieId));
        Uri.Builder uriBuilder = baseUrl.buildUpon();
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, ApiKey.getApiKey());
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);

        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        Log.d(LOG_TAG, "onLoadFinished Started... Outputting data");
        Log.v("onLoadFinished: ", movie.toString());


    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {}

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
