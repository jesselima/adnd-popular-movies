package com.udacity.popularmovies.localdatabase;

import android.net.Uri;
import android.provider.BaseColumns;

import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiConfig.JsonKey;

/**
 * Created by jesse on 29/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkContract {

    // 1TODO: Content authority
    public static final String AUTHORITY = "com.udacity.popularmovies";
    // 2TODO: Base content URI
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // 3TODO: Paths to bookmarks directory
    public static final String PATH_MOVIES_BOOKMARKS = "movie_bookmark";

    public static final class BookmarkEntry implements BaseColumns {

        // 4TODO: Content URI for data in BookmarkEntry class
        public static final Uri CONTENT_URI =
                BookmarkContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_BOOKMARKS).build();

        public static final String TABLE_NAME               = "movie_bookmark";
        public static final String COLUMN_API_ID            = "api_id";
        public static final String COLUMN_ORIGINAL_TITLE    = JsonKey.ORIGINAL_TITLE;
        public static final String COLUMN_RELEASE_DATE      = JsonKey.RELEASE_DATE;
        public static final String COLUMN_RUNTIME           = JsonKey.RUNTIME;
        public static final String COLUMN_GENRES            = JsonKey.GENRES;
        public static final String COLUMN_HOMEPAGE          = JsonKey.HOMEPAGE;
        public static final String COLUMN_TAGLINE           = JsonKey.TAGLINE;
        public static final String COLUMN_OVERVIEW          = JsonKey.OVERVIEW;
        public static final String COLUMN_SPOKEN_LANGUAGES  = JsonKey.SPOKEN_LANGUAGES;
        public static final String COLUMN_VOTE_AVERAGE      = JsonKey.VOTE_AVERAGE;
        public static final String COLUMN_VOTE_COUNT        = JsonKey.VOTE_COUNT;
        public static final String COLUMN_POPULARITY        = JsonKey.POPULARITY;
        public static final String COLUMN_BUDGET            = JsonKey.BUDGET;
        public static final String COLUMN_REVENUE           = JsonKey.REVENUE;
        public static final String COLUMN_IS_WATCHED        = "watched";
        public static final String COLUMN_MOVIE_IMAGE       = ApiConfig.UrlParamKey.IMAGE_POSTER_W185;
        public static final String COLUMN_TIMESTAMP         = "saved_date";

    }
}
