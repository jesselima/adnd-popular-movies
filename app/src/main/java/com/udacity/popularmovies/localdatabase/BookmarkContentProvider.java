package com.udacity.popularmovies.localdatabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by jesse on 08/09/18.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkContentProvider extends ContentProvider {

    public static final int BOOKMARKS = 100;
    public static final int BOOKMARK_WITH_ID = 101;

    /* This way UriMatcher can be accessed throughout all the provider code, and don't forget to set it equal to the return value */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        // NO_MATCH construct a empty matcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add matches with addUri(String authority, String path, int code):
        // Directory
        uriMatcher.addURI(BookmarkContract.AUTHORITY, BookmarkContract.PATH_MOVIES_BOOKMARKS, BOOKMARKS);
        // Single item
        uriMatcher.addURI(BookmarkContract.AUTHORITY, BookmarkContract.PATH_MOVIES_BOOKMARKS + "/#", BOOKMARK_WITH_ID);

        return uriMatcher;
    }

    // BookmarkDbHelper to provide access to the database
    private  BookmarkDbHelper bookmarkDbHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        bookmarkDbHelper = new BookmarkDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
