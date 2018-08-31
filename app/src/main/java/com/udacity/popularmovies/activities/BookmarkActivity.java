package com.udacity.popularmovies.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
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

    private TextView textViewNoBookmarks;
    private ImageView imageViewNoBookmarks;

    private RecyclerView recyclerViewBookmark = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        textViewNoBookmarks = findViewById(R.id.text_view_no_bookmarks);
        imageViewNoBookmarks = findViewById(R.id.image_view_no_bookmarks);

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
                long id = (long) viewHolder.itemView.getTag();

                deleteBookmark(id);
                bookmarkAdapter.swapCursor(getBookmarks());

                if (bookmarkAdapter.getItemCount() == 0) {
                    showNoBookmarkWarning();
                } else {
                    hideNoBookmarkWarning();
                    bookmarkAdapter.swapCursor(getBookmarks());
                }
            }
        }).attachToRecyclerView(recyclerViewBookmark);

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
    }

    private void hideNoBookmarkWarning() {
        recyclerViewBookmark.setVisibility(View.VISIBLE);
        textViewNoBookmarks.setVisibility(View.GONE);
        imageViewNoBookmarks.setVisibility(View.GONE);
    }
}
