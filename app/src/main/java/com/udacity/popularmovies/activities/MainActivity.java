package com.udacity.popularmovies.activities;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.MovieAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Global variable to be used with system language abbreviation in two letters
    private String loadApiLanguage = "en";

    // Global toast object to avoid toast objects queue
    private Toast toast;

    private TextView tvNetworkStatus;
    private ImageView imageViewNoMovies;
    private TextView textViewNoImageWarning;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loading_indicator);
        imageViewNoMovies = findViewById(R.id.iv_no_movies_placeholder);
        textViewNoImageWarning = findViewById(R.id.tv_warning_no_movies);

        /**
         * Load the list of available api languages from {@link ApiConfig} according to api documentation.
         */
        String[] apiLanguagesList = ApiConfig.LANGUAGES;

        // Verify is the api supports the system language. If not API data will be load in english as default
        if (Arrays.asList(apiLanguagesList).contains(Resources.getSystem().getConfiguration().locale.getLanguage())){
            loadApiLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
        }else {
            doToast(getResources().getString(R.string.warning_language));
        }

        // Reference to the TextView that show to the user when there is no active connection to internet.
        tvNetworkStatus = findViewById(R.id.tv_network_status);

        recyclerView = findViewById(R.id.rv_movies);

        // Instantiates a ArrayList of movies
        List<Movie> movieList = new ArrayList<>();

        movieList.add(new Movie(10, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(11, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(12, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(13, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(14, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(15, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(16, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));
        movieList.add(new Movie(17, "http://image.tmdb.org/t/p/w185/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg"));

        if (movieList.isEmpty()){
            loadingIndicator.setVisibility(View.GONE);
            showNoResultsWarning();
            doToast("No movies available");
            return;
        }
        hideNoResultsWarning();

        loadingIndicator.setVisibility(View.GONE);

        // Creates a new MovieAdapter, pass the ArrayList of movies and the context to this new MovieAdapter object.
        recyclerView.setAdapter(new MovieAdapter(movieList, this));

        GridLayoutManager layout = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layout);

    }


    /**
     * Check internet connection when activity is resumed.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkUtils.isDeviceConnected(this)){
            hideConnectionWarning();
        }else {
            showConnectionWarning();
        }
    }

    /**
     * Check internet connection when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.isDeviceConnected(this)){
            hideConnectionWarning();
        }else {
            showConnectionWarning();
        }
    }

    /**
     * Check internet connection when activity is restarted.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (NetworkUtils.isDeviceConnected(this)){
            hideConnectionWarning();
        }else {
            showConnectionWarning();
        }
    }

    private void showNoResultsWarning(){
        imageViewNoMovies.setVisibility(View.VISIBLE);
        textViewNoImageWarning.setVisibility(View.VISIBLE);
    }
    private void hideNoResultsWarning(){
        imageViewNoMovies.setVisibility(View.GONE);
        textViewNoImageWarning.setVisibility(View.GONE);
    }

    private void showConnectionWarning(){
        recyclerView.setVisibility(View.GONE);
        tvNetworkStatus.setVisibility(View.VISIBLE);
        hideNoResultsWarning();
    }
    private void hideConnectionWarning(){
        recyclerView.setVisibility(View.VISIBLE);
        tvNetworkStatus.setVisibility(View.GONE);
    }

    /**
     * This method makes the reuse of toast object to avoid toasts queue
     *
     * @param toastThisText is the text you want to show in the toast.
     */
    private void doToast(String toastThisText) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, toastThisText, Toast.LENGTH_LONG);
        toast.show();
    }
}
