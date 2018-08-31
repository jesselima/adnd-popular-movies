package com.udacity.popularmovies.localdatabase;

import android.provider.BaseColumns;

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
        public static final String COLUMN_TIMESTAMP = "saved_date";

    }
}
