package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.MovieReview;

import java.util.ArrayList;

/**
 * Created by jesse on 01/09/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {

    private static final String LOG_TAG = MovieReviewsAdapter.class.getSimpleName();

    private final ArrayList<MovieReview> movieReviewList;
    private final Context mContext;

    public MovieReviewsAdapter(Context context, ArrayList<MovieReview> reviewList) {
        this.movieReviewList = reviewList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_review, viewGroup, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();

        holder.textViewReviewAuthor.setText(movieReviewList.get(adapterPosition).getReviewAuthor());
        holder.textViewReviewContent.setText(movieReviewList.get(adapterPosition).getReviewAContent());

        final String commentUrl = movieReviewList.get(adapterPosition).getReviewUrl();
        holder.itemView.setTag(commentUrl);
        holder.buttonSeeReviewOnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentUrl.equals("null") || TextUtils.isEmpty(commentUrl)) {
                    Toast.makeText(mContext, R.string.comment_url_not_available, Toast.LENGTH_SHORT).show();
                }else {
                    Uri uriWebPage = Uri.parse(commentUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uriWebPage);
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieReviewList.size();
    }

    public static class MovieReviewViewHolder extends RecyclerView.ViewHolder{

        private final TextView textViewReviewAuthor;
        private final TextView textViewReviewContent;
        private final Button buttonSeeReviewOnWeb;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            textViewReviewAuthor = itemView.findViewById(R.id.tv_review_author);
            textViewReviewContent = itemView.findViewById(R.id.tv_review_content);
            buttonSeeReviewOnWeb = itemView.findViewById(R.id.bt_see_review_on_web);
        }
    }
}
