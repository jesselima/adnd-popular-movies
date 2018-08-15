package com.udacity.popularmovies.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.QueryUtilsMovieDetails;

/**
 * Created by jesse on 12/06/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieLoader extends AsyncTaskLoader<Movie> {

    /**
     * Tag for log messages
     */
    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieLoader.class.getName();

    /**
     * Query URL
     */
    private final String mUrl;

    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     * If the input Url object is null the method will nos proceed.
     */
    @Override
    public Movie loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract the Movie details.
        return QueryUtilsMovieDetails.fetchMovieData(mUrl);
    }


}
