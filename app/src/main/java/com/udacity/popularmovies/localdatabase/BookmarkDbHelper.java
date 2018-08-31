package com.udacity.popularmovies.localdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;

/**
 * Created by jesse on 29/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkDbHelper extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = "popular_movies.db";
    // If you change the database schema, database version must be incremented
    private static final int DATABASE_VERSION = 1;

    public BookmarkDbHelper(Context context) {
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE " + BookmarkEntry.TABLE_NAME + " (" +
                BookmarkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookmarkEntry.COLUMN_API_ID + " INTEGER NOT NULL, " +
                BookmarkEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                BookmarkEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                BookmarkEntry.COLUMN_RUNTIME + " INTEGER NOT NULL, " +
                BookmarkEntry.COLUMN_GENRES + " TEXT NOT NULL, " +
                BookmarkEntry.COLUMN_HOMEPAGE + " TEXT, " +
                BookmarkEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BookmarkEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
