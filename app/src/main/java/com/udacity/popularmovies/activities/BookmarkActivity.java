package com.udacity.popularmovies.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.BookmarkAdapter;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.localdatabase.BookmarkDbHelper;

public class BookmarkActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String LOG_TAG = BookmarkActivity.class.getSimpleName();
    private static final int LOADER_ID_MOVIE_BOOKMARKS_LIST = 3;
    Toast toast;


    private SQLiteDatabase sqLiteDatabase;
    private BookmarkDbHelper bookmarkDbHelper = new BookmarkDbHelper(this);

    private TextView textViewNoBookmarks, textViewNavigateToBookmarks;
    private ImageView imageViewNoBookmarks;
    Button buttonNavigateToMovies;

    private RecyclerView recyclerViewBookmark;
    private BookmarkAdapter bookmarkAdapter;

    Cursor mCursorData = null; // TODO: This load the bookmarks when activity starts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();

        textViewNoBookmarks = findViewById(R.id.text_view_no_bookmarks);
        textViewNavigateToBookmarks = findViewById(R.id.text_view_navigate_to_bookmarks);
        imageViewNoBookmarks = findViewById(R.id.image_view_no_bookmarks);
        buttonNavigateToMovies = findViewById(R.id.bt_navigate_to_movies);

        recyclerViewBookmark = findViewById(R.id.recycler_view_bookmark);
        recyclerViewBookmark.setLayoutManager(new LinearLayoutManager(this));

//        mCursorData = updateBookmarkList();// TODO
        bookmarkAdapter = new BookmarkAdapter(this);
        bookmarkAdapter.notifyDataSetChanged();
        recyclerViewBookmark.setAdapter(bookmarkAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long movieApiId = (long) viewHolder.itemView.getTag();

                showDeleteConfirmationDialog(movieApiId);

            }
        }).attachToRecyclerView(recyclerViewBookmark);

        buttonNavigateToMovies = findViewById(R.id.bt_navigate_to_movies);
        buttonNavigateToMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MovieListActivity.class);
                startActivity(intent);
            }
        });

        // Start the Loader Callbacks to query the list of movie bookmarks asynchronously
         getSupportLoaderManager().initLoader(LOADER_ID_MOVIE_BOOKMARKS_LIST, null, this);

    }
    // Close onCreate

    @Override
    protected void onResume() {
        super.onResume();
         getSupportLoaderManager().restartLoader(LOADER_ID_MOVIE_BOOKMARKS_LIST, null, this);
    }

    /* TODO *** Loader Callbacks *** */
    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a cursor object to receive the cursor data returned from the task
            Cursor mCursorData = null;

            @Override
            protected void onStartLoading() {
                if (mCursorData != null) {
                    deliverResult(mCursorData);
                }else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(
                            BookmarkContract.BookmarkEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            BookmarkContract.BookmarkEntry.COLUMN_TIMESTAMP + " DESC");
                }catch (Exception e) {
                    Log.e(LOG_TAG, "Fail to load data");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mCursorData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorData = data;
        bookmarkAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        sqLiteDatabase = bookmarkDbHelper.getReadableDatabase();
        bookmarkAdapter.swapCursor(null);
    }

    private void restartLoaderBookmarks() {
        getSupportLoaderManager().restartLoader(LOADER_ID_MOVIE_BOOKMARKS_LIST, null, this);
    }


    /* TODO *** DATABASE MANIPULATION *** */

    private boolean deleteBookmark(long id) {
        sqLiteDatabase.isOpen();
        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();
        return sqLiteDatabase.delete(BookmarkContract.BookmarkEntry.TABLE_NAME,
                BookmarkContract.BookmarkEntry._ID + "=" + id, null) > 0;
    }

    private void showDeleteConfirmationDialog(final long movieApiId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this movie from bookmarks?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (deleteBookmark(movieApiId)) {
                    doToast("Deleted!");
                } else {
                    doToast("Deleted!");
                }
                restartLoaderBookmarks();

                if (bookmarkAdapter.getItemCount() == 0) {
                    showNoBookmarkWarning();
                } else {
                    hideNoBookmarkWarning();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    restartLoaderBookmarks();
//                    bookmarkAdapter.swapCursor(getBookmarks());
//                    bookmarkAdapter.swapCursor(mCursorData);
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /* UI WARNINGS TODO******* */

    /**
     *
     */
    private void showNoBookmarkWarning() {
        recyclerViewBookmark.setVisibility(View.GONE);
        textViewNoBookmarks.setVisibility(View.VISIBLE);
        imageViewNoBookmarks.setVisibility(View.VISIBLE);
        buttonNavigateToMovies.setVisibility(View.VISIBLE);
        textViewNavigateToBookmarks.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    private void hideNoBookmarkWarning() {
        recyclerViewBookmark.setVisibility(View.VISIBLE);
        textViewNoBookmarks.setVisibility(View.GONE);
        imageViewNoBookmarks.setVisibility(View.GONE);
        buttonNavigateToMovies.setVisibility(View.GONE);
        textViewNavigateToBookmarks.setVisibility(View.GONE);
    }

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
