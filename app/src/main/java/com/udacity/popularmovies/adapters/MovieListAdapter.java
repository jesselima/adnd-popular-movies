package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.Movie;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();

    private ArrayList<Movie> movieList = new ArrayList<>();
    private Context mContext;

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
    public void onBindViewHolder(@NonNull MovieListAdapter.MovieViewHolder holder, final int position) {

        holder.textViewMovieTitle.setText(movieList.get(position).getmTitle());

        Picasso.get()
                .load(movieList.get(position).getmPosterPath())
                .placeholder(R.drawable.poster_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.poster_image_place_holder)
                .into(holder.mImageViewMoviePoster);

        holder.mImageViewMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id =  movieList.get(position).getmId();
                String title =  movieList.get(position).getmTitle();
                Toast.makeText(mContext, "ID: " +String.valueOf(id) + " - "+ title , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageViewMoviePoster;
        private TextView textViewMovieTitle;

        private MovieViewHolder(View itemView) {
            super(itemView);
            mImageViewMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            textViewMovieTitle = itemView.findViewById(R.id.tv_movie_title);
        }

    }

}
