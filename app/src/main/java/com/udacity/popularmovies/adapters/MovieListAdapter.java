package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.activities.MovieDetailsActivity;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.models.Movie;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private final ArrayList<Movie> movieList;
    private final Context mContext;

    public MovieListAdapter(Context context, ArrayList<Movie> movieList) {
       this.movieList = movieList;
       this.mContext = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter.MovieViewHolder holder, int position) {

        final int adapterPosition = holder.getAdapterPosition();

        holder.textViewOriginalTitle.setText(movieList.get(adapterPosition).getMovieOriginalTitle());

        Picasso.get()
            .load(movieList.get(position).getMoviePosterPath())
            .placeholder(R.drawable.poster_image_place_holder)
            .fit().centerInside()
            .error(R.drawable.poster_image_place_holder)
            .into(holder.imageViewMoviePoster);

        holder.imageViewMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int    id      =  movieList.get(adapterPosition).getMovieId();
            String title   =  movieList.get(adapterPosition).getMovieOriginalTitle();

            Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                intent.putExtra(ApiConfig.JsonKey.ID, id);
                intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, title);
            mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewMoviePoster;
        private final TextView textViewOriginalTitle;

        private MovieViewHolder(View itemView) {
            super(itemView);
            imageViewMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            textViewOriginalTitle = itemView.findViewById(R.id.tv_movie_original_title);
        }
    }
}
