package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.Movie;

import java.util.List;

/**
 * Created by jesse on 12/06/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    public MovieListAdapter(Context context, List<Movie> movieList) {
       this.context = context;
       this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieListAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter.MovieViewHolder holder, int position) {

        Movie movie = movieList.get(position);

        holder.mMovieTitle.setText(movie.getmTitle());

        Picasso.get()
                .load(movie.getmPosterPath())
                .placeholder(R.drawable.poster_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.poster_image_place_holder)
                .into(holder.mImageViewMoviePoster);

    }

    @Override
    public int getItemCount() {
        // return the number of items in the list.
        return movieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        final ImageView mImageViewMoviePoster;
        final TextView mMovieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mImageViewMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            mMovieTitle = itemView.findViewById(R.id.tv_movie_title);

        }
    }

}
