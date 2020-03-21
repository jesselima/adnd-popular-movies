package dev.jesselima.jmovies.adapters;

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
import dev.jesselima.jmovies.R;
import dev.jesselima.jmovies.activities.MovieDetailsActivity;
import dev.jesselima.jmovies.config.ApiConfig;
import dev.jesselima.jmovies.models.Movie;

import java.util.ArrayList;

/**
 * An {@link MovieListAdapter} knows how to create a list item layout for each movie item
 * in the data source (a list of {@link Movie} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like RecyclerView
 * to be displayed to the user.
 * Official Documentation: https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private final ArrayList<Movie> movieList;
    private final Context mContext;

    /**
     * Constructs a new {@link MovieListAdapter}.
     *
     * @param context  of the app
     * @param movieList is the list of movies, which is the data source of the adapter
     */
    public MovieListAdapter(Context context, ArrayList<Movie> movieList) {
       this.movieList = movieList;
       this.mContext = context;
    }

    /**
     * Inflates the layout for each movie item and return that view.
     * @param viewType The view type of the new View.
     * @param viewGroup  The ViewGroup into which the new View will be added after it is bound to an adapter position. It's the ViewGroup object used by the Inflater.
     * @return a movie item view object represents the inflated layout filled with
     * data for each item(movie) in the list on the UI
     * Official Documentation: https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter#onBindViewHolder
     */
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_movie, viewGroup, false);
        return new MovieViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * It replaces the contents of a view (invoked by the layout manager)
     *
     * @param position The position of the item within the adapter's data set.
     * @param holder is the ViewGroup object used by the Inflater. The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     *
     */
    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter.MovieViewHolder holder, int position) {

        final int adapterPosition = holder.getAdapterPosition();

        // Updates the UI for the given adapter position
        holder.textViewOriginalTitle.setText(movieList.get(adapterPosition).getMovieOriginalTitle());
        holder.textViewReleaseDate.setText(movieList.get(adapterPosition).getMovieReleaseDate());
        holder.textViewVoteAverage.setText(String.valueOf(movieList.get(adapterPosition).getMovieVoteAverage()));

        // Load the movie poster using Picasso library to make asynchronous http request.
        // Puts a placeholder on the ImageView while the image from the server is not complete load
        Picasso.get()
            .load(movieList.get(position).getMoviePosterPath())
            .placeholder(R.drawable.poster_image_place_holder)
            .fit().centerInside()
            .error(R.drawable.poster_image_place_holder)
            .into(holder.imageViewMoviePoster);

        // Makes the each movie poster clickable. When user clicks on the poster the MovieDetailsActivity is called.
        // The movie Id and Title is sent to MovieDetailsActivity.
        // The ID will be used to make a request od movie details. The title will be used to set the title on the screen on the MovieDetailsActivity launch.
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * RecyclerView.Adapter implementations should subclass ViewHolder and add fields for caching potentially expensive findViewById(int) results.
     * All UI references in the item list does not require recall all IDs over and over again while the view is recycled.
     */
    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewMoviePoster;
        private final TextView textViewOriginalTitle;
        private final TextView textViewReleaseDate;
        private final TextView textViewVoteAverage;

        private MovieViewHolder(View itemView) {
            super(itemView);
            imageViewMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            textViewOriginalTitle = itemView.findViewById(R.id.tv_movie_original_title);
            textViewReleaseDate = itemView.findViewById(R.id.tv_release_date);
            textViewVoteAverage = itemView.findViewById(R.id.tv_vote_average);
        }
    }
}
