package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.activities.MovieDetailsActivity;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.utils.DateUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.text.DecimalFormat;

/**
 * This is the adapter for the RecyclerView of MovieBookmark objects.
 * This is a part of the project adnd-popular-movies.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>{

    private Cursor cursor;
    private Context context;

    public BookmarkAdapter (Context contextInput) {
        this.context = contextInput;
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
        String tagline = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_TAGLINE));
        String overview = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_OVERVIEW));
        String languages = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_SPOKEN_LANGUAGES));
        double voteAverage = cursor.getDouble(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_VOTE_AVERAGE));
        int voteCount = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_VOTE_COUNT));
        double popularity = cursor.getDouble(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_POPULARITY));
        int budget = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_BUDGET));
        int revenue = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_REVENUE));
        byte[] movieBytesImage = cursor.getBlob(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_MOVIE_IMAGE));

        holder.textViewOriginalTitle.setText(originalTitle);
        holder.textViewReleaseDate.setText(releaseDate);
        holder.textViewRuntime.setText(String.valueOf(runtime));
        holder.textViewRuntime.append("min");
        holder.textViewGenres.setText(genres);
        holder.textViewTagline.setText(tagline);
        holder.textViewOverview.setText(overview);
        holder.textViewLanguages.setText(languages);
        holder.textViewVoteAverage.setText(String.valueOf(voteAverage));
        holder.textViewVoteCount.setText(String.valueOf(voteCount));
        holder.textViewPopularity.setText(String.valueOf(popularity));
        holder.textViewBudget.setText(formatNumber(budget));
        holder.textViewRevenue.setText(formatNumber(revenue));
        holder.imageViewMoviePoster.setImageBitmap(convertImageBytesToBitmap(movieBytesImage));

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
        if (cursor == null) {
            return 0;
        }
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

        private final Button buttonMovieHomepage;
        private final Button buttonMovieDetails;

        private final TextView textViewOriginalTitle;
        private final TextView textViewReleaseDate;
        private final TextView textViewRuntime;
        private final TextView textViewGenres;
        private final TextView textViewTagline;
        private final TextView textViewOverview;
        private final TextView textViewLanguages;
        private final TextView textViewVoteAverage;
        private final TextView textViewVoteCount;
        private final TextView textViewPopularity;
        private final TextView textViewBudget;
        private final TextView textViewRevenue;
        private final ImageView imageViewMoviePoster;

        public BookmarkViewHolder(View itemView) {
            super(itemView);

            buttonMovieDetails = itemView.findViewById(R.id.bt_item_list_details_db);
            buttonMovieHomepage = itemView.findViewById(R.id.bt_item_list_homepage_db);

            textViewOriginalTitle = itemView.findViewById(R.id.tv_original_title_db);
            textViewReleaseDate = itemView.findViewById(R.id.tv_release_date_db);
            textViewRuntime = itemView.findViewById(R.id.tv_runtime_db);
            textViewGenres = itemView.findViewById(R.id.tv_genres_db);
            textViewTagline = itemView.findViewById(R.id.tv_tagline_db);
            textViewOverview = itemView.findViewById(R.id.tv_overview_db);
            textViewLanguages = itemView.findViewById(R.id.tv_original_language_db);
            textViewVoteAverage = itemView.findViewById(R.id.tv_vote_average_db);
            textViewVoteCount = itemView.findViewById(R.id.tv_vote_count_db);
            textViewPopularity = itemView.findViewById(R.id.tv_popularity_db);
            textViewBudget = itemView.findViewById(R.id.tv_budget_db);
            textViewRevenue = itemView.findViewById(R.id.tv_revenue_db);
            imageViewMoviePoster = itemView.findViewById(R.id.iv_movie_poster_db);
        }
    }

    private static Bitmap convertImageBytesToBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private String formatNumber(int numberToBeFormated) {
        DecimalFormat myFormatter = new DecimalFormat("$###,###,###.##");
        return myFormatter.format(numberToBeFormated);
    }
}
