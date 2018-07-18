package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.Movie;

import java.util.List;

/**
 * Created by jesse on 12/06/18.
 * This is a part of the project adnd-popular-movies.
 */
// TODO 7 - Create the class MovieAdapter and extends RecyclerView.Adapter. Use ALT + ENTER to implement methods
public class MovieAdapter extends RecyclerView.Adapter {

    private List<Movie> movieList;
    private Context context;

    public MovieAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // TODO - 10
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);

        // TODO - 9
        MovieViewHolder holder = new MovieViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // TODO 11
        MovieViewHolder holder = (MovieViewHolder) viewHolder;

        // TODO 12
        Movie movie  = movieList.get(position) ;
        holder.mTitle.setText(movie.getmTitle());
    }

    @Override
    public int getItemCount() {
        // return the number of items in the list.
        return movieList.size();
    }


    // TODO 8 - Implementing a ViewHolder Class
    public class MovieViewHolder extends RecyclerView.ViewHolder {

//        final ImageView mImagePoster;
        final TextView mTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);

//            mImagePoster = itemView.findViewById(R.id.iv_movie_poster);
            mTitle = itemView.findViewById(R.id.tv_movie_title);
        }
    }

}
