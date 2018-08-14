package com.udacity.popularmovies.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiKey;
import com.udacity.popularmovies.loaders.MovieLoader;
import com.udacity.popularmovies.models.Company;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utils.DateUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private static final int MOVIE_DETAILS_LOADER_ID = 2;
    private int movieId;
    private String movieOriginalTitle = "";
    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;

    private Toast toast;

    private ImageView imageViewMoviePoster, imageViewMovieBackdrop;
    private TextView textViewOverview, textViewReleaseDate, textViewRuntime, textViewTitle, textViewVoteAverage, textViewOriginalLanguage, textViewTagline, textViewPopularity, textViewVoteCount, textViewBuget, textViewRevenue, textViewGenres;
    private TextView textViewNetworkStatus, textViewNoMovieDetails;

    private TextView textViewProductionCompanies;
    private ImageView imageViewProductionCompanies;
    private Movie movieData = new Movie();

    private String movieHomepageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // UI REFERENCES
        imageViewMoviePoster = findViewById(R.id.iv_movie_poster);
        imageViewMovieBackdrop = findViewById(R.id.iv_movie_backdrop);

        textViewOverview = findViewById(R.id.tv_overview);
        textViewReleaseDate = findViewById(R.id.tv_release_date);
        textViewRuntime = findViewById(R.id.tv_runtime);
        textViewTitle = findViewById(R.id.tv_title);
        textViewOriginalLanguage = findViewById(R.id.tv_original_language);
        textViewTagline = findViewById(R.id.tv_tagline);
        textViewPopularity = findViewById(R.id.tv_popularity);
        textViewVoteCount = findViewById(R.id.tv_vote_count);
        textViewBuget = findViewById(R.id.tv_buget);
        textViewRevenue = findViewById(R.id.tv_revenue);
        textViewVoteAverage = findViewById(R.id.tv_vote_average);
        textViewGenres = findViewById(R.id.tv_genres);

        // Warnings UI View references.
        textViewNetworkStatus = findViewById(R.id.tv_network_status);
        textViewNoMovieDetails = findViewById(R.id.tv_no_movie_details);

        textViewProductionCompanies = findViewById(R.id.tv_production_company);
        imageViewProductionCompanies = findViewById(R.id.iv_production_company);

        Button buttonHomepage = findViewById(R.id.bt_home_page);
        buttonHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieData.getMovieHomepage().equals("null")){
                    doToast(getString(R.string.warning_homepage_not_available));
                }else {
                    openWebPage(movieHomepageUrl);
                }

            }
        });

        FloatingActionButton floatingShareButton = findViewById(R.id.float_share_button);
        floatingShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieData.getMovieHomepage().equals("null")){
                    doToast(getString(R.string.warning_homepage_not_available));
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, movieData.getMovieHomepage());
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_to)));
                }

            }
        });



        String[] apiLanguagesList = ApiConfig.LANGUAGES;
        // Verify is the api supports the system language. If not API data will be load in english as default
        if (Arrays.asList(apiLanguagesList).contains(Resources.getSystem().getConfiguration().locale.getLanguage())) {
            loadApiLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
        } else {
            doToast(getResources().getString(R.string.warning_language));
        }

        // Hide the Tagline TextView due to API not provide tagline do english language only or the
        // movie occasionally may have  a empty tagline value. Then the space will be shown.
        if (loadApiLanguage.equals("")) {
            textViewTagline.setVisibility(View.GONE);
        } else {
            textViewTagline.setVisibility(View.VISIBLE);
        }

        getIncomingIntent();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(movieOriginalTitle);

        if (!NetworkUtils.isDeviceConnected(this)) {
            doToast(getString(R.string.warning_you_are_not_connected));
            textViewNetworkStatus.setVisibility(View.VISIBLE);
        } else {
            // Shows loading indicator and Kick off the loader
            Log.d(LOG_TAG, "Loader Kick off...");
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        }

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(ApiConfig.JsonKey.ID) && getIntent().hasExtra(ApiConfig.JsonKey.ORIGINAL_TITLE)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                movieId = bundle.getInt(ApiConfig.JsonKey.ID);
                movieOriginalTitle = bundle.getString(ApiConfig.JsonKey.ORIGINAL_TITLE);
            }
        }
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader Started...");

        Uri baseUrl = Uri.parse(ApiConfig.getBaseMovieDetailsUrl() + String.valueOf(movieId));
        Uri.Builder uriBuilder = baseUrl.buildUpon();
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, ApiKey.getApiKey());
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);

        Log.d("Request URL ===>>>", uriBuilder.toString()); // TODO: REMOVE
        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        Log.d(LOG_TAG, "onLoadFinished Started. Outputting data...");

        movieData = movie;

        if (!isMovieValid(movieData)) {
            textViewNetworkStatus.setVisibility(View.GONE);
            updateUI(movieData);
            movieHomepageUrl = movieData.getMovieHomepage();
        } else {
            textViewNoMovieDetails.setVisibility(View.VISIBLE);
        }
    }

    private boolean isMovieValid(Movie movie) {
        return movie == null;
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {}

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

    private void updateUI(Movie movie) {

        Picasso.get()
                .load(movie.getMoviePosterPath())
                .placeholder(R.drawable.poster_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.poster_image_place_holder)
                .into(imageViewMoviePoster);

        Picasso.get()
                .load(movie.getMovieBackdropPath())
                .placeholder(R.drawable.backdrop_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.backdrop_image_place_holder)
                .into(imageViewMovieBackdrop);

        // TODO: REMOVE
        Log.v("\nPOSTER URL: ", movie.getMoviePosterPath());
        Log.v("\nBACKDROP URL: ", movie.getMovieBackdropPath());

        textViewOverview.setText(movie.getMovieOverview());
        textViewVoteAverage.setText(String.valueOf(movie.getMovieVoteAverage()));

        textViewRuntime.setText(String.valueOf(movie.getMovieRunTime() + getString(R.string.min)));

        String formatedDate = DateUtils.simpleDateFormat(movie.getMovieReleaseDate());
        textViewReleaseDate.setText(formatedDate);

        textViewTitle.setText(movie.getMovieTitle());
        textViewOriginalLanguage.setText(movie.getMovieOriginalLanguage());

        String taglineWithQuotes = movie.getMovieTagline();
        textViewTagline.setText(taglineWithQuotes);

        textViewPopularity.setText(String.valueOf(movie.getMoviePopularity()));
        textViewVoteCount.setText(String.valueOf(movie.getMovieVoteCount()));

        textViewBuget.setText(formatNumber(movie.getMovieBuget()));
        textViewRevenue.setText(formatNumber(movie.getMovieRevenue()));

        textViewGenres.setText(movie.getMovieGenres());

        ArrayList<Company> companies = movie.getCompaniesArrayList();
        if (companies.size() > 0){
            textViewProductionCompanies.setVisibility(View.VISIBLE);
            imageViewProductionCompanies.setVisibility(View.VISIBLE);
        }else {
            textViewProductionCompanies.setVisibility(View.GONE);
            imageViewProductionCompanies.setVisibility(View.GONE);
        }

        for (int i = 0; companies.size() > i; i++){

            Company company = companies.get(i);

            ImageView imageView = new ImageView(this); // TODO: SOLVE IT!!!

            String fullLogoPathUrl = ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W185 + company.getCompanyLogoPath();
            Log.v("\nCOMPANY LOGO: ", fullLogoPathUrl); // TODO:
            Picasso.get()
                    .load(fullLogoPathUrl)
                    .fit().centerInside()
                    .into(imageViewProductionCompanies);
        }

    }

    private String formatNumber(int numberToBeFormated) {
        DecimalFormat myFormatter = new DecimalFormat("$###,###,###.##");
        return myFormatter.format(numberToBeFormated);
    }

    private void openWebPage(String url) {
        Uri uriWebPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriWebPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
