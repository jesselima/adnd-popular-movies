package com.udacity.popularmovies.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.udacity.popularmovies.models.MovieVideo;
import com.udacity.popularmovies.utils.QueryUtilsMovieVideoList;

import java.util.List;

/**
 * Created by jesse on 01/09/18.
 * This is a part of the project adnd-popular-movies.
 */
public class VideoListLoader extends AsyncTaskLoader<List<MovieVideo>> {

    private static final String LOG_TAG = VideoListLoader.class.getSimpleName();

    private final String videosRequestUrl;

    public VideoListLoader(Context context, String url) {
        super(context);
        videosRequestUrl = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<MovieVideo> loadInBackground() {
        if (videosRequestUrl == null) {
            return null;
        }
        return QueryUtilsMovieVideoList.fetchMovieVideoListData(videosRequestUrl);
    }
}
