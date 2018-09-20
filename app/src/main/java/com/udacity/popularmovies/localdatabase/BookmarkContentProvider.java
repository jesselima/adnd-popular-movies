package com.udacity.popularmovies.localdatabase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;

import java.util.Objects;

/**
 * Created by jesse on 08/09/18.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkContentProvider extends ContentProvider {

    private static final int BOOKMARKS = 100;
    private static final int BOOKMARK_WITH_ID = 101;

    /* This way UriMatcher can be accessed throughout all the provider code, and don't forget to set it equal to the return value */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // gaining access to read our underlying database
        final SQLiteDatabase db = bookmarkDbHelper.getReadableDatabase();

        // URI matcher to get a match number that identifies the passed in URI
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match) {
            case BOOKMARKS:
                returnCursor = db.query(
                        BookmarkEntry.TABLE_NAME,   // table
                        projection,  // columns
                        selection,   // selection
                        selectionArgs,   // selectionArgs
                        null,      // groupBy
                        null,       // having
                        sortOrder);     // orderBy
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        // Set notification changes. It tells the cursor what content URI it was created for.
        // This way, if anything changes in the URI, the cursor will know.
        returnCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        // Return a cursor with data (this cursor might be empty or full)
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Get access to the popular_movies database (with write privileges)
        final SQLiteDatabase db = bookmarkDbHelper.getWritableDatabase();
        // Write UR matching code to identify the match  for the bookmarks directory
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case BOOKMARKS:
                // Insert values into database
                // If the insert does not work the return will bee -1. If  It's success return the new row id.
                long id = db.insert(BookmarkEntry.TABLE_NAME, null, contentValues);

                if (id > 0){
                    // success
                    returnUri = ContentUris.withAppendedId(BookmarkEntry.CONTENT_URI, id);
                }else {
                    // failed
                    throw new UnsupportedOperationException("Failed to insert row into: " + uri);
                }
                break;
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        // Notify the resolver if the uri has been changed
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = bookmarkDbHelper.getWritableDatabase();
        int rowsDeleted;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKMARKS:
                rowsDeleted = db.delete(BookmarkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKMARK_WITH_ID:

                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                selection = BookmarkEntry._ID + "=?";
                selectionArgs = new String[] { id };
                rowsDeleted = db.delete(BookmarkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete not supported for " + uri);
        }
        if (rowsDeleted > 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
