package com.udacity.popularmovies.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.MovieListAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiConfig.UrlParamKey;
import com.udacity.popularmovies.config.ApiConfig.UrlParamValue;
import com.udacity.popularmovies.databinding.ActivityMovieListBinding;
import com.udacity.popularmovies.loaders.MovieListLoader;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.AdaptiveGridLayout;
import com.udacity.popularmovies.utils.BottomNavigationBehavior;
import com.udacity.popularmovies.utils.BottomNavigationViewHelper;
import com.udacity.popularmovies.utils.LanguageUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String API_KEY = BuildConfig.API_KEY;

    // String to identify the activity when using logging messages
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    // Movie List Loader ID
    private static final int MOVIE_LOADER_ID = 100;
    private ArrayList<Movie> movieList = new ArrayList<>();


    // TODO TEST IT!!!!!
    // Implementation for save state
    private static String MOVIE_LIST_STATE = "list_state";
    private static final String RECYCLER_VIEW_STATE = "recycler_layout";
    private Parcelable savedRecyclerLayoutState;

    // Global variable to be used with system language abbreviation in two letters
    private final String loadApiLanguage = UrlParamValue.LANGUAGE_DEFAULT;
    // This object is updated to true when user do a search. Then the loader will use a search query string
    private boolean isDoingSearch = false;
    // This object is updated with a string when the user runs a search
    private String queryTerm = "";
    // Page value for pagination control
    private int page = 1;
    // Sort by default value. The first list off
    private String sortBy = UrlParamValue.POPULAR;
    // Global toast object to avoid toast objects queue
    private Toast toast;
    // Constants to control objects visibility more easily
    private static final int HIDE = View.GONE;
    private static final int SHOW = View.VISIBLE;
    // Objects ro set and control RecyclerView and the list of movie data
    private MovieListAdapter movieListAdapter;
    private boolean showAdultContent;

    // Binding class object to access the objects in the layout.
    private ActivityMovieListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        if (savedInstanceState != null){
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_STATE);
            savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_list);

        setToolbar();
        setupSharedPreferences();

        // Set and handle actions on BottonNavigation
        BottomNavigationViewHelper.disableShiftMode(binding.bottomNavigationView);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.button_nav_most_popular:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        } else {
                            page = 1;
                            sortBy = UrlParamValue.POPULAR;
                            restartLoader();
                        }
                        break;
                    case R.id.button_nav_most_rated:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        } else {
                            page = 1;
                            sortBy = UrlParamValue.TOP_RATED;
                            restartLoader();
                        }
                        break;
                    case R.id.button_pagination_backward:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        } else {
                            if (page == 1) {
                                doToast(getResources().getString(R.string.you_are_at_page_1));
                            }else {
                                paginationBackward();
                            }
                        }
                        break;
                    case R.id.button_pagination_forward:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        } else {
                            paginationForward();
                        }
                        break;
                    case R.id.button_bookmarks:
                        Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });

        // attaching bottom sheet behaviour - hide / show on scroll
        // Author: Android Hive, Source: https://www.androidhive.info/2017/12/android-working-with-bottom-navigation/
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        binding.loadingIndicator.setIndeterminate(true);

        // Setup the adapter adapter and RecyclerView to receive data.
        movieListAdapter = new MovieListAdapter(this, movieList);
        binding.rvMovies.setAdapter(movieListAdapter);
        ViewCompat.setNestedScrollingEnabled(binding.rvMovies, false);
        // Calculates the number of columns in the Grip according to screen width size.
        int numberOfColumns = AdaptiveGridLayout.calculateNoOfColumns(getApplicationContext());

        // Sets the adapter that provides the data and the views to represent the data in this widget.
        binding.rvMovies.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        // RecyclerView can perform several optimizations if it can know in advance that RecyclerView's
        // size is not affected by the adapter contents. If is set true. It will allow RecyclerView
        // to avoid invalidating the whole layout when its adapter contents change.
        // In others words, set true if adapter changes cannot affect the size of the RecyclerView.
        // Official documentation: https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html#setHasFixedSize(boolean)
        binding.rvMovies.setHasFixedSize(true);

        /*
         * Load the list of available api languages from {@link ApiConfig} according to api documentation.
         */
        LanguageUtils.checkSystemLanguage(loadApiLanguage);

        // Check if the device is connected ot internet and if so start the Loader.
        checkConnectionAndStartLoader();
    } // Closes onCreate


    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        showAdultContent = sharedPreferences.getBoolean(
                getString(R.string.pref_show_adult_key),
                getResources().getBoolean(R.bool.pref_show_adult_default)
        );

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_show_adult_key))) {
            showAdultContent = sharedPreferences.getBoolean(
                    key,
                    getResources().getBoolean(R.bool.pref_show_adult_default)
            );
            restartLoader();
        }else {
            showAdultContent = sharedPreferences.getBoolean(
                    key,
                    getResources().getBoolean(R.bool.pref_show_adult)
            );
            restartLoader();
        }
    }

    private void setToolbar() {
        binding.toolbarMovieList.setTitle(R.string.app_name);
        binding.toolbarMovieList.setSubtitle(R.string.the_movie_database);
        setSupportActionBar(binding.toolbarMovieList);
    }

    /*** Methods for BottonNavigation control ***/
    private void restartLoader() {
        progressBarStatus(SHOW);
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    // When clicked, restart the loader with the current page - 1. So the loader will make another
    // request to load the previous 20 movies. If the current page is == 1 toast a warning to the user
    private void paginationBackward() {
        movieList.clear();
        noResultsWarning(HIDE);
        connectionWarning(HIDE);
        // The validation if the user is already at page 1 is done in the switch on the
        // BottonNavigationMenu because this method should even called if the current page == 1.
        page--;
        movieList.clear();
        restartLoader();
        binding.rvMovies.scrollToPosition(0);
        doToast(getResources().getString(R.string.page) + String.valueOf(page));
    }

    // When clicked, restart the loader with the current page + 1. So the loader will make another
    // request to load the next 20 movies. If the current.
    private void paginationForward() {
        movieList.clear();
        noResultsWarning(HIDE);
        connectionWarning(HIDE);
        page++;
        restartLoader();
        binding.rvMovies.scrollToPosition(0);
        doToast(String.valueOf(getString(R.string.page) + page));
    }

    /* IMPLEMENTATION FOR LoaderCallbacks */

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id     is the loader id.
     * @param bundle A mapping from String keys to various Parcelable values.
     * @return a new MovieListLoader object. This loader will receive the request URL and
     * make this request to the server in a background thread.
     */
    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader Started...");

        if (isDoingSearch) {
            Uri getBaseSearchUrl = Uri.parse(ApiConfig.getBaseUrlSearch());
            Uri.Builder uriBuilder = getBaseSearchUrl.buildUpon();
            uriBuilder.appendQueryParameter(UrlParamKey.API_KEY, API_KEY);
            uriBuilder.appendQueryParameter(UrlParamKey.INCLUDE_ADULT, String.valueOf(showAdultContent));
            uriBuilder.appendQueryParameter(UrlParamKey.QUERY, queryTerm);
            return new MovieListLoader(this, uriBuilder.toString());
        }else {
            Uri getBaseMovieListUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + sortBy);
            Uri.Builder uriBuilder = getBaseMovieListUrl.buildUpon();
            uriBuilder.appendQueryParameter(UrlParamKey.API_KEY, API_KEY);
            uriBuilder.appendQueryParameter(UrlParamKey.LANGUAGE, loadApiLanguage);
            uriBuilder.appendQueryParameter(UrlParamKey.INCLUDE_ADULT, String.valueOf(showAdultContent));
            uriBuilder.appendQueryParameter(UrlParamKey.PAGE, String.valueOf(page));
            return new MovieListLoader(this, uriBuilder.toString());
        }
    }

    /**
     * Called when a previously created loader has finished its load
     *
     * @param loader The Loader that has finished.
     * @param movies The data generated by the Loader. In this case, a list of movies.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {
        Log.d(LOG_TAG, "onLoadFinished Started. Outputting data...");

        // When the onCreateLoader finish its job, it will pass the data do this method.
        if (movies == null || movies.isEmpty()) {
            noResultsWarning(SHOW);
            binding.rvMovies.setVisibility(HIDE);
            // If there is no movie to show give a warning to the user in the UI.
            if (NetworkUtils.isDeviceConnected(this)) {
                connectionWarning(HIDE);
                noResultsWarning(SHOW);
            } else {
                noResultsWarning(HIDE);
                connectionWarning(SHOW);
            }
        } else {
            // If the movie list has data, hide the loading indicator, hide connection warnings (if needed)
            // and hide no results UI warnings.
            progressBarStatus(HIDE);
            connectionWarning(HIDE);
            noResultsWarning(HIDE);
            binding.rvMovies.setVisibility(SHOW);
            // Clear the previous list of movies to avoid memory leaks.
            movieList.clear();
            // Add the movies returned from the loader to the movie list
            movieList.addAll(movies);
            // Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
            movieListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }


    /* === HELPER METHODS === */

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

    private void checkConnectionAndStartLoader() {
        // Check internet connection before start the loader
        if (!NetworkUtils.isDeviceConnected(this)) {
            // If device is not connected show connections warnings on the screen.
            progressBarStatus(HIDE);
            noResultsWarning(HIDE);
            connectionWarning(SHOW);
        } else {
            connectionWarning(HIDE);
            noResultsWarning(HIDE);
            progressBarStatus(SHOW);
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem); // Cast is required

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doToast(query);
                searchView.clearFocus();
                isDoingSearch = true;
                movieList.clear();
                queryTerm = query;
                restartLoader();
                // Updates the search flag back to false.
                isDoingSearch = false;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * UI HIDE/SHOW WARNING
     * HELPER METHODS FOR UI WARNINGS - SHOW/HIDE WARNINGS FOR LOADING, INTERNET CONNECTION AND
     * NO RESULTS RETURNED FROM THE SERVER.
     */

    private void progressBarStatus(int VISIBILITY) {
        binding.loadingIndicator.setVisibility(VISIBILITY);
    }

    private void noResultsWarning(int VISIBILITY) {
        binding.ivNoMoviesPlaceholder.setVisibility(VISIBILITY);
        binding.tvWarningNoMovies.setVisibility(VISIBILITY);
        if (VISIBILITY == SHOW) progressBarStatus(HIDE);
    }

    private void connectionWarning(int VISIBILITY) {
        binding.tvNetworkStatus.setVisibility(VISIBILITY);
        binding.ivWarningNoConnectionList.setVisibility(VISIBILITY);
        if (VISIBILITY == SHOW) binding.rvMovies.setVisibility(HIDE);
        else binding.rvMovies.setVisibility(SHOW);
    }


    /**
     * LIFECYCLE METHODS
     */

    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkUtils.isDeviceConnected(this)) {
            connectionWarning(HIDE);
        } else {
            connectionWarning(SHOW);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
            connectionWarning(HIDE);
        } else {
            connectionWarning(SHOW);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
            connectionWarning(HIDE);
            noResultsWarning(HIDE);
        } else {
            connectionWarning(SHOW);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(MOVIE_LIST_STATE, movieList);
        savedInstanceState.putParcelable(RECYCLER_VIEW_STATE, binding.rvMovies.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_STATE);
        savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        super.onRestoreInstanceState(savedInstanceState);
    }


}
