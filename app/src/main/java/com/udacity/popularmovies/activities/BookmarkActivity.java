package com.udacity.popularmovies.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.BookmarkAdapter;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;
import com.udacity.popularmovies.localdatabase.BookmarkDbHelper;

public class BookmarkActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String LOG_TAG = BookmarkActivity.class.getSimpleName();
    private static final int LOADER_ID_MOVIE_BOOKMARKS_LIST = 3;
    private Toast toast;
    private Button buttonNavigateToMovies;
    private SQLiteDatabase sqLiteDatabase;
    private final BookmarkDbHelper bookmarkDbHelper = new BookmarkDbHelper(this);
    private TextView textViewNoBookmarks, textViewNavigateToBookmarks;
    private ImageView imageViewNoBookmarks;
    private RecyclerView recyclerViewBookmark;
    private BookmarkAdapter bookmarkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();

        textViewNoBookmarks         = findViewById(R.id.text_view_no_bookmarks);
        textViewNavigateToBookmarks = findViewById(R.id.text_view_navigate_to_bookmarks);
        imageViewNoBookmarks        = findViewById(R.id.image_view_no_bookmarks);
        buttonNavigateToMovies      = findViewById(R.id.bt_navigate_to_movies);
        recyclerViewBookmark        = findViewById(R.id.recycler_view_bookmark);

        recyclerViewBookmark.setLayoutManager(new LinearLayoutManager(this));
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

    } // Close onCreate


    /* === LOADER CALLBACKS === */

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable final Bundle loaderArgs) {

        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a cursor object to receive the cursor data returned from the task
            Cursor mCursorData = null;

            @Override
            protected void onStartLoading() {
                if (mCursorData != null) {
                    deliverResult(mCursorData);
                } else {
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
                } catch (Exception e) {
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
        if (data.getCount() < 1) {
            showNoBookmarkWarning();
        } else {
            hideNoBookmarkWarning();
        }
        bookmarkAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookmarkAdapter.swapCursor(null);
    }

    private void restartLoaderBookmarks() {
        BookmarkDbHelper bookmarkDbHelper = new BookmarkDbHelper(this);
        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();
        sqLiteDatabase.isOpen();
        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();
        getSupportLoaderManager().restartLoader(LOADER_ID_MOVIE_BOOKMARKS_LIST, null, this);
    }


    /* === DATABASE MANIPULATION === */

    private boolean deleteBookmark(long id) {

        String stringId = Long.toString(id);
        Uri uri = BookmarkEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        Log.v("DELETE URI: ", uri.toString());

        return getContentResolver().delete(
                uri,
                BookmarkEntry._ID + "=" + id,
                null
        ) > 0;
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean deleteAllBookmarks() {
        int rowsDeleted = getContentResolver().delete(
                BookmarkEntry.CONTENT_URI,
                null,
                null);

        if (rowsDeleted > 0) {
            doToast(rowsDeleted + getResources().getString(R.string.number_of_deleted_bookmarks));
            showNoBookmarkWarning();
        }

        // After delete all Bookmarks
        restartLoaderBookmarks();
        // Return true if the row is deleted from database.
        return rowsDeleted > 0;
    }


    private void showDeleteConfirmationDialog(final long movieApiId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_movie_bookmark_item);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (deleteBookmark(movieApiId)) {
                    doToast(getString(R.string.deleted));
                    restartLoaderBookmarks();
                }else {
                    doToast(getString(R.string.warning_could_not_be_deleted));
                }

                if (bookmarkAdapter.getItemCount() == 0) {
                    showNoBookmarkWarning();
                } else {
                    hideNoBookmarkWarning();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    restartLoaderBookmarks();
                    doToast(getString(R.string.canceled));
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /* === UI WARNINGS === */

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


    /* === BOOKMARKS ACTION MENU === */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark_menu, menu);
        return true;
    }

    /**
     * This method handles the clicked item menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_bookmarks_item:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * When "Delete all entries is clicked this confirmation dialog pops up.
     */
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.warning_delete_all_bookmarks);

        builder.setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllBookmarks();
            }
        });
        builder.setNegativeButton(R.string.no_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /* === LIFECYCLE METHODS === */

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("===>>> onResume", " called");
        restartLoaderBookmarks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("===>>> onStart", " called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("===>>> onPause", " called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restartLoaderBookmarks();
        Log.d("===>>> onRestart", " called");
    }

}
