package com.udacity.popularmovies.moviesfeed.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig.UrlParamValue;
import com.udacity.popularmovies.config.SettingsActivity;
import com.udacity.popularmovies.databinding.ActivityMovieListBinding;
import com.udacity.popularmovies.moviesbookmarks.BookmarkActivity;
import com.udacity.popularmovies.moviesfeed.models.Movie;
import com.udacity.popularmovies.moviesfeed.models.MoviesResponse;
import com.udacity.popularmovies.service.MovieDataService;
import com.udacity.popularmovies.service.RetrofitInstance;
import com.udacity.popularmovies.shared.utils.AdaptiveGridLayout;
import com.udacity.popularmovies.shared.utils.BottomNavigationBehavior;
import com.udacity.popularmovies.shared.utils.BottomNavigationViewHelper;
import com.udacity.popularmovies.shared.utils.LanguageUtils;
import com.udacity.popularmovies.shared.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    private List<Movie> movieList = new ArrayList<>();

    private final String loadApiLanguage = UrlParamValue.LANGUAGE_DEFAULT;
    private boolean isDoingSearch = false;
    private String queryTerm = "";
    private int page = 1;
    private String sortBy = UrlParamValue.POPULAR;
    private Toast toast;
    private static final int HIDE = View.GONE;
    private static final int SHOW = View.VISIBLE;
    private MovieListAdapter movieListAdapter;
    private boolean showAdultContent;
    private ActivityMovieListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_list);
        LanguageUtils.checkSystemLanguage(loadApiLanguage);

        setToolbar();
        setupSharedPreferences();

        // Set and handle actions on BottomNavigation
        BottomNavigationViewHelper.disableShiftMode(binding.bottomNavigationView);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.button_nav_most_popular:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        } else {
                            sortBy = UrlParamValue.POPULAR;
                            getPopularMovies();
                        }
                        break;
                    case R.id.button_nav_most_rated:
                        if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                            doToast(getResources().getString(R.string.warning_check_internet_connection));
                        } else {
                            sortBy = UrlParamValue.TOP_RATED;
                            getPopularMovies();
                        }
                        break;
                    case R.id.button_bookmarks:
                        Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        binding.loadingIndicator.setIndeterminate(true);

        getPopularMovies();

    } // Closes onCreate



    private void getPopularMovies() {

        MovieDataService movieDataService = RetrofitInstance.getService();

        Call<MoviesResponse> call = movieDataService.getMovies(sortBy, loadApiLanguage, BuildConfig.API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.body() != null) {

                    List<Movie> movies = response.body().getMovies();
                    MovieListAdapter moviesAdapter = new MovieListAdapter(getApplicationContext(), movies);
                    binding.rvMovies.setAdapter(moviesAdapter);
                    ViewCompat.setNestedScrollingEnabled(binding.rvMovies, false);
                    int numberOfColumns = AdaptiveGridLayout.calculateNoOfColumns(getApplicationContext());
                    binding.rvMovies.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns));
                    binding.rvMovies.setHasFixedSize(true);
                    moviesAdapter.notifyDataSetChanged();

                    if (movies != null) {
                        if (movies.size() > 0){
                            progressBarStatus(HIDE);
                        } else {
                            progressBarStatus(HIDE);
                            noResultsWarning(SHOW);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
            }
        });

    }

    // TODO: Helpers methods and preferences

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
            getPopularMovies();
        }else {
            showAdultContent = sharedPreferences.getBoolean(
                    key,
                    getResources().getBoolean(R.bool.pref_show_adult)
            );
            getPopularMovies();
        }
    }

    private void setToolbar() {
        binding.toolbarMovieList.setTitle(R.string.app_name);
        binding.toolbarMovieList.setSubtitle(R.string.the_movie_database);
        setSupportActionBar(binding.toolbarMovieList);
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

    private void checkConnectionAndLoadContent() {
        // Check internet connection before start the loader
        if (!NetworkUtils.isDeviceConnected(this)) {
            // If device is not connected show connections warnings on the screen.
//            progressBarStatus(HIDE);
//            noResultsWarning(HIDE);
//            connectionWarning(SHOW);
        } else {
//            connectionWarning(HIDE);
//            noResultsWarning(HIDE);
//            progressBarStatus(SHOW);
            getPopularMovies();
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
                getPopularMovies();
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
//            connectionWarning(HIDE);
        } else {
//            connectionWarning(SHOW);
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
//            connectionWarning(HIDE);
        } else {
//            connectionWarning(SHOW);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
//            connectionWarning(HIDE);
//            noResultsWarning(HIDE);
        } else {
//            connectionWarning(SHOW);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
