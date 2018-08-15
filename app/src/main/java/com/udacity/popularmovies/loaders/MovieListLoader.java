package com.udacity.popularmovies.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.QueryUtilsMovieList;

import java.util.List;

/**
 * Created by jesse on 12/06/18.
 * This is a part of the project adnd-popular-movies.
 */
@SuppressWarnings("unused")
public class MovieListLoader extends AsyncTaskLoader<List<Movie>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MovieListLoader.class.getName();

    /**
     * Query URL
     */
    private final String mUrl;

    /**
     * Constructs a new {@link MovieListLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MovieListLoader(Context context, String url) {
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
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a List of Movie.
        return QueryUtilsMovieList.fetchMovieData(mUrl);
    }


}
