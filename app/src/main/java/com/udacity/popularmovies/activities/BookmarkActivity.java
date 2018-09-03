package com.udacity.popularmovies.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.BookmarkAdapter;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.localdatabase.BookmarkDbHelper;

public class BookmarkActivity extends AppCompatActivity {

    private final static String LOG_TAG = BookmarkActivity.class.getSimpleName();

    private BookmarkAdapter bookmarkAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private BookmarkDbHelper bookmarkDbHelper;

    private TextView textViewNoBookmarks, textViewNavigateToBookmarks;
    private ImageView imageViewNoBookmarks;
    Button buttonNavigateToMovies;

    private RecyclerView recyclerViewBookmark = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        textViewNoBookmarks = findViewById(R.id.text_view_no_bookmarks);
        textViewNavigateToBookmarks = findViewById(R.id.text_view_navigate_to_bookmarks);
        imageViewNoBookmarks = findViewById(R.id.image_view_no_bookmarks);
        buttonNavigateToMovies = findViewById(R.id.bt_navigate_to_movies);

        recyclerViewBookmark = findViewById(R.id.recycler_view_bookmark);
        recyclerViewBookmark.setLayoutManager(new LinearLayoutManager(this));

        bookmarkDbHelper = new BookmarkDbHelper(this);
        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();

        final Cursor cursor = updateBookmarkList();
        bookmarkAdapter = new BookmarkAdapter(this, cursor);
        recyclerViewBookmark.setAdapter(bookmarkAdapter);

        if (bookmarkAdapter.getItemCount() == 0) {
            showNoBookmarkWarning();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

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
    }
    // Close onCreate


    @Override
    protected void onResume() {
        super.onResume();
        getBookmarks();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getBookmarks();
    }

    private Cursor updateBookmarkList() {
        Cursor cursor = getBookmarks();
        bookmarkAdapter = new BookmarkAdapter(this, cursor);
        return cursor;
    }

    private Cursor getBookmarks() {
        if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = bookmarkDbHelper.getReadableDatabase();
        }
        return sqLiteDatabase.query(
                BookmarkContract.BookmarkEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BookmarkContract.BookmarkEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    private boolean deleteBookmark(long id) {
        return sqLiteDatabase.delete(BookmarkContract.BookmarkEntry.TABLE_NAME,
                BookmarkContract.BookmarkEntry._ID + "=" + id, null) > 0;
    }

    private void showNoBookmarkWarning() {
        recyclerViewBookmark.setVisibility(View.GONE);
        textViewNoBookmarks.setVisibility(View.VISIBLE);
        imageViewNoBookmarks.setVisibility(View.VISIBLE);
        buttonNavigateToMovies.setVisibility(View.VISIBLE);
        textViewNavigateToBookmarks.setVisibility(View.VISIBLE);
    }

    private void hideNoBookmarkWarning() {
        recyclerViewBookmark.setVisibility(View.VISIBLE);
        textViewNoBookmarks.setVisibility(View.GONE);
        imageViewNoBookmarks.setVisibility(View.GONE);
        buttonNavigateToMovies.setVisibility(View.GONE);
        textViewNavigateToBookmarks.setVisibility(View.GONE);
    }

    private void showDeleteConfirmationDialog(final long movieApiId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this movie from bookmarks?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteBookmark(movieApiId);

                bookmarkAdapter.swapCursor(getBookmarks());

                if (bookmarkAdapter.getItemCount() == 0) {
                    showNoBookmarkWarning();
                } else {
                    hideNoBookmarkWarning();
                    bookmarkAdapter.swapCursor(getBookmarks());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    bookmarkAdapter.swapCursor(getBookmarks());
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
