package com.udacity.popularmovies.activities;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
    Toast toast;

    private TextView tvNetworkStatus;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        List<Movie> movieList = new ArrayList<Movie>();

        // Add title to the movie
        movieList.add(new Movie("The Avengers"));
        movieList.add(new Movie("Transformers"));
        movieList.add(new Movie("X-Men"));
        movieList.add(new Movie("The Lord of The Rings"));
        movieList.add(new Movie("SALT"));
        movieList.add(new Movie("Shooter"));
        movieList.add(new Movie("Need for Speed"));
        movieList.add(new Movie("Fast and Furious"));

        // Creates a new MovieAdapter, pass the ArrayList of movies and the context to this new MovieAdapter object.
        recyclerView.setAdapter(new MovieAdapter(movieList, this));

        RecyclerView.LayoutManager layout = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layout);

        Log.v("\n", "\n");
        Log.v("Test movieList", String.valueOf(movieList.get(0).getmTitle()));
        Log.v("Test movieList", String.valueOf(movieList.get(1).getmTitle()));

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

    private void showConnectionWarning(){
        recyclerView.setVisibility(View.GONE);
        tvNetworkStatus.setVisibility(View.VISIBLE);
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
