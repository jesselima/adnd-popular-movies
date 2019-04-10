package com.udacity.popularmovies.moviesbookmarks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.moviedetails.view.MovieDetailsActivity;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.shared.utils.DateUtils;
import com.udacity.popularmovies.shared.utils.NetworkUtils;

import java.text.DecimalFormat;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Cursor cursor;
    private final Context context;

    public BookmarkAdapter(Context contextInput) {
        this.context = contextInput;
    }

    private static Bitmap convertImageBytesToBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_list_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookmarkViewHolder holder, int position) {

        if (!cursor.moveToPosition(position))
            return;

        // Data to be shown in the UI
        final String originalTitle = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_ORIGINAL_TITLE));
        final String releaseDate = DateUtils.simpleDateFormat(cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_RELEASE_DATE)));
        final int runtime = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_RUNTIME));
        final String genres = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_GENRES));
        final String tagline = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_TAGLINE));
        final double voteAverage = cursor.getDouble(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_VOTE_AVERAGE));
        final int voteCount = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_VOTE_COUNT));
        final byte[] movieBytesImage = cursor.getBlob(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_MOVIE_IMAGE));

        // Dialog Objects (This values are displayed inside the Dialog
        final String overview = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_OVERVIEW));
        final String languages = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_SPOKEN_LANGUAGES));
        final int budget = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_BUDGET));
        final int revenue = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_REVENUE));
        final double popularity = cursor.getDouble(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_POPULARITY));
        final String homepage = cursor.getString(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_HOMEPAGE));

        final int watched = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_IS_WATCHED));
        if (watched == 0) holder.textViewIsWatched.setVisibility(View.INVISIBLE);
        else holder.textViewIsWatched.setVisibility(View.VISIBLE);

        holder.textViewOriginalTitle.setText(originalTitle);
        holder.textViewReleaseDate.setText(releaseDate);
        holder.textViewRuntime.setText(String.valueOf(runtime));
        holder.textViewRuntime.append(context.getString(R.string.min));
        holder.textViewGenres.setText(genres);
        holder.textViewTagline.setText(tagline);
        holder.textViewVoteAverage.setText(String.valueOf(voteAverage));
        holder.textViewVoteCount.setText(String.valueOf(voteCount));
        holder.imageViewMoviePoster.setImageBitmap(convertImageBytesToBitmap(movieBytesImage));

        final long idSQLite = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_API_ID));
        holder.itemView.setTag(idSQLite);

        final int movieApiId = cursor.getInt(cursor.getColumnIndex(BookmarkContract.BookmarkEntry.COLUMN_API_ID));

        holder.buttonMovieDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setMessage(R.string.more_details);
                alertDialog.setTitle(originalTitle);

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                // Inflates the layout to to show in the dialog
                @SuppressLint("InflateParams") View viewDialog = layoutInflater.inflate(R.layout.dialog_movie_details, null);

                // Set the dialog view value values.
                TextView textViewDialogLanguages = viewDialog.findViewById(R.id.dialog_tv_original_language_db);
                textViewDialogLanguages.setText(languages);
                TextView textViewDialogPopularity = viewDialog.findViewById(R.id.dialog_tv_popularity_db);
                textViewDialogPopularity.setText(String.valueOf(popularity));
                TextView textViewDialogBudget = viewDialog.findViewById(R.id.dialog_tv_budget_db);
                textViewDialogBudget.setText(String.valueOf(formatNumber(revenue)));
                TextView textViewDialogRevenue = viewDialog.findViewById(R.id.dialog_tv_revenue_db);
                textViewDialogRevenue.setText(String.valueOf(formatNumber(budget)));
                TextView textViewDialogOverView = viewDialog.findViewById(R.id.dialog_tv_overview_db);
                textViewDialogOverView.setText(overview);

                // Set the inflated layout into the Dialog object
                alertDialog.setView(viewDialog);

                alertDialog.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.setNegativeButton(R.string.full_details, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

                alertDialog.setPositiveButton(R.string.homepage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

                AlertDialog dialog = alertDialog.create();
                dialog.show();

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

    private String formatNumber(int numberToBeFormated) {
        DecimalFormat myFormatter = new DecimalFormat("$###,###,###.##");
        return myFormatter.format(numberToBeFormated);
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder {

        private final Button buttonMovieDetails;

        private final TextView  textViewOriginalTitle;
        private final TextView  textViewReleaseDate;
        private final TextView  textViewRuntime;
        private final TextView  textViewGenres;
        private final TextView  textViewTagline;
        private final TextView  textViewVoteAverage;
        private final TextView  textViewVoteCount;
        private final ImageView imageViewMoviePoster;
        private final TextView  textViewIsWatched;

        BookmarkViewHolder(View itemView) {
            super(itemView);

            buttonMovieDetails      = itemView.findViewById(R.id.bt_item_list_details_db);
            textViewOriginalTitle   = itemView.findViewById(R.id.tv_original_title_db);
            textViewReleaseDate     = itemView.findViewById(R.id.tv_release_date_db);
            textViewRuntime         = itemView.findViewById(R.id.tv_runtime_db);
            textViewGenres          = itemView.findViewById(R.id.tv_genres_db);
            textViewTagline         = itemView.findViewById(R.id.tv_tagline_db);
            textViewVoteAverage     = itemView.findViewById(R.id.tv_vote_average_db);
            textViewVoteCount       = itemView.findViewById(R.id.tv_vote_count_db);
            imageViewMoviePoster    = itemView.findViewById(R.id.iv_movie_poster_db);
            textViewIsWatched       = itemView.findViewById(R.id.tv_label_watched_bookmark);
        }
    }
}
