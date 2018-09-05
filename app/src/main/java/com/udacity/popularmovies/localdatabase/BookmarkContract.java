package com.udacity.popularmovies.localdatabase;

import android.provider.BaseColumns;

import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiConfig.JsonKey;

/**
 * Created by jesse on 29/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkContract {

    public static final class BookmarkEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie_bookmark";
        public static final String COLUMN_API_ID = "api_id";
        public static final String COLUMN_ORIGINAL_TITLE = JsonKey.ORIGINAL_TITLE;
        public static final String COLUMN_RELEASE_DATE = JsonKey.RELEASE_DATE;
        public static final String COLUMN_RUNTIME = JsonKey.RUNTIME;
        public static final String COLUMN_GENRES = JsonKey.GENRES;
        public static final String COLUMN_HOMEPAGE = JsonKey.HOMEPAGE;
        public static final String COLUMN_TAGLINE = JsonKey.TAGLINE;
        public static final String COLUMN_OVERVIEW = JsonKey.OVERVIEW;
        public static final String COLUMN_SPOKEN_LANGUAGES = JsonKey.SPOKEN_LANGUAGES;
        public static final String COLUMN_VOTE_AVERAGE = JsonKey.VOTE_AVERAGE;
        public static final String COLUMN_VOTE_COUNT = JsonKey.VOTE_COUNT;
        public static final String COLUMN_POPULARITY = JsonKey.POPULARITY;
        public static final String COLUMN_BUDGET = JsonKey.BUDGET;
        public static final String COLUMN_REVENUE = JsonKey.REVENUE;
        public static final String COLUMN_MOVIE_IMAGE = ApiConfig.UrlParamKey.IMAGE_POSTER_W185;
        public static final String COLUMN_TIMESTAMP = "saved_date";

    }
}
