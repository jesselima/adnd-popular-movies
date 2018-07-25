package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.Movie;

import java.util.List;

/**
 * Created by jesse on 12/06/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieAdapter extends RecyclerView.Adapter {

    private List<Movie> movieList;
    private Context context;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        MovieViewHolder holder = (MovieViewHolder) viewHolder;

        Movie movie  = movieList.get(position) ;

    }

    @Override
    public int getItemCount() {
        // return the number of items in the list.
        return movieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        final ImageView mImageMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mImageMoviePoster = itemView.findViewById(R.id.iv_movie_poster);

        }
    }

}
