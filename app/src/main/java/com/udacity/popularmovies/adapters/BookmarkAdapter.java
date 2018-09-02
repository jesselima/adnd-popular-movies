package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.activities.MovieDetailsActivity;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.utils.DateUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

/**
 * This is the adapter for the RecyclerView of MovieBookmark objects.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>{

    private Cursor cursor;
    private Context context;

    public BookmarkAdapter (Context contextInput, Cursor cursorInput) {
        this.context = contextInput;
        this.cursor = cursorInput;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_list_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {

        if (!cursor.moveToPosition(position))
            return;

        // Data to be shown in the UI
        final String originalTitle = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_ORIGINAL_TITLE));
        String releaseDate = DateUtils.simpleDateFormat(cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_RELEASE_DATE)));
        int runtime = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_RUNTIME));
        String genres = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_GENRES));

        holder.textViewOriginalTitle.setText(originalTitle);
        holder.textViewReleaseDate.setText(releaseDate);
        holder.textViewRuntime.setText(String.valueOf(runtime));
        holder.textViewRuntime.append("min");
        holder.textViewGenres.setText(genres);

        // "id" is the ID index in the database.
        long idSQLite = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry._ID));
        holder.itemView.setTag(idSQLite);

        // Data for additional actions
        final int movieApiId = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_API_ID));
        holder.buttonMovieDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isDeviceConnected(context)) {
                    Intent intent = new Intent(context.getApplicationContext(), MovieDetailsActivity.class);
                    intent.putExtra(ApiConfig.JsonKey.ID, movieApiId);
                    intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, originalTitle);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.warning_check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        final String homepage = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_HOMEPAGE));
        holder.buttonMovieHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isDeviceConnected(context)) {
                    Uri uriWebPage = Uri.parse(homepage);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uriWebPage);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, R.string.warning_check_internet_connection, Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder {

        TextView textViewOriginalTitle, textViewReleaseDate, textViewRuntime, textViewGenres;
        Button buttonMovieHomepage, buttonMovieDetails;

        public BookmarkViewHolder(View itemView) {
            super(itemView);

            textViewOriginalTitle = itemView.findViewById(R.id.tv_original_title);
            textViewReleaseDate = itemView.findViewById(R.id.tv_release_date);
            textViewRuntime = itemView.findViewById(R.id.tv_runtime);
            textViewGenres = itemView.findViewById(R.id.tv_genres);
            buttonMovieDetails = itemView.findViewById(R.id.bt_item_list_details);
            buttonMovieHomepage = itemView.findViewById(R.id.bt_item_list_homepage);
        }
    }
}
