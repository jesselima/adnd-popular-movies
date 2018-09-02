package com.udacity.popularmovies.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.CompanyListAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.loaders.MovieLoader;
import com.udacity.popularmovies.localdatabase.BookmarkContract;
import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;
import com.udacity.popularmovies.localdatabase.BookmarkDbHelper;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.models.MovieProductionCompany;
import com.udacity.popularmovies.utils.DateUtils;
import com.udacity.popularmovies.utils.LanguageUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    // The Loader ID to be used by the LoaderManager
    private static final int MOVIE_DETAILS_LOADER_ID = 2;

    private int movieId;
    private String movieOriginalTitle = "";
    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;

    private Toast toast;

    // Object for UI references
    private ImageView imageViewMoviePoster, imageViewMovieBackdrop;
    private TextView textViewOverview, textViewReleaseDate, textViewRuntime, textViewTitle, textViewVoteAverage, textViewOriginalLanguage, textViewTagline, textViewPopularity, textViewVoteCount, textViewBuget, textViewRevenue, textViewGenres;
    private TextView textViewNetworkStatus, textViewNoMovieDetails;
    private LinearLayout linearLayoutContainerDetails, linearLayoutAdditionalInfo;

    private Movie movieData = new Movie();
    private CompanyListAdapter companyListAdapter;
    private final ArrayList<MovieProductionCompany> companies = new ArrayList<>();
    private String movieHomepageUrl;

    private SQLiteDatabase sqLiteDatabase;
    private BookmarkDbHelper bookmarkDbHelper = new BookmarkDbHelper(this);

    private FloatingActionButton floatingBookmarkButton = null;
    private FloatingActionButton floatingShareButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        bookmarkDbHelper = new BookmarkDbHelper(this);
        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();
        // Get the movie ID and Title from the clicked item on the RecyclerView item.
        getIncomingIntent();

        // UI REFERENCES
        imageViewMoviePoster     = findViewById(R.id.iv_movie_poster);
        imageViewMovieBackdrop   = findViewById(R.id.iv_movie_backdrop);
        textViewOverview         = findViewById(R.id.tv_overview);
        textViewReleaseDate      = findViewById(R.id.tv_release_date);
        textViewRuntime          = findViewById(R.id.tv_runtime);
        textViewTitle            = findViewById(R.id.tv_title);
        textViewOriginalLanguage = findViewById(R.id.tv_original_language);
        textViewTagline          = findViewById(R.id.tv_tagline);
        textViewPopularity       = findViewById(R.id.tv_popularity);
        textViewVoteCount        = findViewById(R.id.tv_vote_count);
        textViewBuget            = findViewById(R.id.tv_buget);
        textViewRevenue          = findViewById(R.id.tv_revenue);
        textViewVoteAverage      = findViewById(R.id.tv_vote_average);
        textViewGenres           = findViewById(R.id.tv_genres);
        // Warnings UI View references.
        textViewNetworkStatus    = findViewById(R.id.tv_network_status);
        textViewNoMovieDetails   = findViewById(R.id.tv_no_movie_details);
        linearLayoutContainerDetails = findViewById(R.id.container_details);
        linearLayoutAdditionalInfo = findViewById(R.id.container_additional_content);

        // RecyclerView for the list of companies
        RecyclerView recyclerViewCompanies = findViewById(R.id.rv_companies);
        companyListAdapter = new CompanyListAdapter(this, companies);
        recyclerViewCompanies.setAdapter(companyListAdapter);
        recyclerViewCompanies.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompanies.setHasFixedSize(true);

        // Button inside Overview card that send user to the movie homepage in the browser if URL is not null
        Button buttonHomepage = findViewById(R.id.bt_home_page);
        buttonHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieData.getMovieHomepage().equals("null")) {
                    doToast(getString(R.string.warning_homepage_not_available));
                } else if (!NetworkUtils.isDeviceConnected(getApplicationContext())){
                    doToast(getString(R.string.warning_you_are_not_connected));
                }else {
                    openWebPage(movieHomepageUrl);
                }
            }
        });

        Button buttonReviews = findViewById(R.id.bt_reviews);
        buttonReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MovieReviewsActivity.class);
                doToast(String.valueOf(movieData.getMovieId()));
                startActivity(intent);
            }
        });

        Button buttonVideos = findViewById(R.id.bt_videos);
        buttonVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MovieVideosActivity.class);
                doToast(String.valueOf(movieData.getMovieId()));
                startActivity(intent);
            }
        });

        floatingBookmarkButton = findViewById(R.id.float_save_bookmark);
        floatingBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBookmarkOnDatabase()) {
                    // if the Movie is already bookmarked, remove it from the database and update icon status to unsaved icon
                    deleteBookmark(movieData.getMovieId());
                    floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_unsaved);
                    doToast(getResources().getString(R.string.movie_removed));
                }else {
                    saveBookmark();
                    floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_saved);
                    doToast(getResources().getString(R.string.movie_saved));
                }
            }
        });

        // FloatButton to share movie homepage to other app available on device.
        floatingShareButton = findViewById(R.id.float_share_button);
        floatingShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWebUrl(movieData.getMovieHomepage(), getString(R.string.warning_homepage_not_available));
            }
        });

        LanguageUtils.checkSystemLanguage(loadApiLanguage);
        // Hide the Tagline TextView due to API to provide tagline to english language only or the
        // movie occasionally may have a empty tagline value. Then the space will be shown.
        if (loadApiLanguage.equals("")) {
            textViewTagline.setVisibility(View.GONE);
        } else {
            textViewTagline.setVisibility(View.VISIBLE);
        }

        // Setup ToolBar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Setup CollapsingToolbar with movie name
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(movieOriginalTitle);

        checkConnectionAndStartLoader();

    } // Close onCreate

    // It's called inside onCreate method and get the movie ID and Title sent from MovieListActivity.
    private void getIncomingIntent() {
        if (getIntent().hasExtra(ApiConfig.JsonKey.ID) && getIntent().hasExtra(ApiConfig.JsonKey.ORIGINAL_TITLE)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                movieId = bundle.getInt(ApiConfig.JsonKey.ID);
                movieOriginalTitle = bundle.getString(ApiConfig.JsonKey.ORIGINAL_TITLE);
            }
        }
    }

    /**
     *  Instantiate and return a new Loader for the given ID.
     * @param id is the loader id.
     * @param args A mapping from String keys to various Parcelable values.
     * @return a new MovieListLoader object. This loader will receive the request URL and
     * make this request to the server in a background thread.
     */
    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {

        Uri baseUrl = Uri.parse(ApiConfig.getBaseMovieDetailsUrl() + String.valueOf(movieId));
        Uri.Builder uriBuilder = baseUrl.buildUpon();
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
        uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);

        return new MovieLoader(this, uriBuilder.toString());
    }

    /**
     * Called when a previously created loader has finished its work.
     *
     * @param loader The Loader that has finished.
     * @param movie The data generated by the Loader. In this case, details of a movie.
     */
    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        // Get the movie returned from the loader and add to a global movie instance.
        // So it can be accessed from another methods.
        movieData = movie;

        if (!isMovieValid(movieData)) {
            textViewNetworkStatus.setVisibility(View.GONE);
            // Updates the UI with details of the movie
            updateUI(movieData);

            if (checkBookmarkOnDatabase()) {
                floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_saved);
            }else {
                floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_unsaved);
            }

            movieHomepageUrl = movieData.getMovieHomepage();
            // Clear the companies list before add new data. It's avoid memory leaks.
            companies.clear();
            // Add new data to the list.
            companies.addAll(movieData.getCompaniesArrayList());
            companyListAdapter.notifyDataSetChanged();
        } else {
            textViewNoMovieDetails.setVisibility(View.VISIBLE);
        }
    }

    /**
     *  Check uf the movie returned from the Loader is valid
     * @param movie is the {@link Movie} object with details.
     * @return return true is the movie is
     */
    private boolean isMovieValid(Movie movie) {
        return movie == null;
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * @param loader The Loader that is being reset.
     */
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

    /**
     *  When called receives the movie object with movie details and updates the UI.
     * @param movie is the {@link Movie} object with movie details.
     */
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

    }// Closes updateUI method

    /**
     * When called must receive a number that will be converted to a String with the pattern $###,###,###.##
     * @param numberToBeFormated is the number (Buget or Revenue) to be formated.
     * @return a the number as String properly formated.
     */
    private String formatNumber(int numberToBeFormated) {
        DecimalFormat myFormatter = new DecimalFormat("$###,###,###.##");
        return myFormatter.format(numberToBeFormated);
    }

    /**
     * When called must receive a String url and will open default browser on device or ask to the
     * user about what application he wants to uses to open the URL.
     * @param url is the url to be open in the browser.
     */
    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private long saveBookmark() {
        ContentValues contentValues = new ContentValues();
            contentValues.put(BookmarkEntry.COLUMN_API_ID,         movieData.getMovieId());
            contentValues.put(BookmarkEntry.COLUMN_ORIGINAL_TITLE, movieData.getMovieOriginalTitle());
            contentValues.put(BookmarkEntry.COLUMN_RELEASE_DATE,   movieData.getMovieReleaseDate());
            contentValues.put(BookmarkEntry.COLUMN_RUNTIME,        movieData.getMovieRunTime());
            contentValues.put(BookmarkEntry.COLUMN_GENRES,         movieData.getMovieGenres());
            contentValues.put(BookmarkEntry.COLUMN_HOMEPAGE,       movieData.getMovieHomepage());
        return sqLiteDatabase.insert(BookmarkEntry.TABLE_NAME, null, contentValues);
    }

    private boolean deleteBookmark(long id) {
        return sqLiteDatabase.delete(BookmarkContract.BookmarkEntry.TABLE_NAME,
                BookmarkEntry.COLUMN_API_ID + "=" + id, null) > 0;
    }

    private boolean checkBookmarkOnDatabase() {

        Cursor cursor = sqLiteDatabase.query(
            BookmarkEntry.TABLE_NAME,   // table
            new String[] { BookmarkEntry.COLUMN_API_ID, BookmarkEntry.COLUMN_ORIGINAL_TITLE },  // columns
            BookmarkEntry.COLUMN_API_ID + "=?",   // selection
            new String[] { String.valueOf(movieData.getMovieId()) },   // selectionArgs
            null,      // groupBy
            null,       // having
            null);     // orderBy

        boolean isOnDatabase = cursor.getCount() > 0;
        cursor.close();

        return isOnDatabase;
    }

    private void shareWebUrl(String webUrl, String warningString) {
        if (webUrl.equals("null")) {
            doToast(warningString);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, webUrl);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_to)));
        }
    }

    private void showConnectionWarning() {
        textViewNetworkStatus.setVisibility(View.VISIBLE);
        textViewNoMovieDetails.setVisibility(View.VISIBLE);
        linearLayoutContainerDetails.setVisibility(View.GONE);
        linearLayoutAdditionalInfo.setVisibility(View.GONE);
        floatingBookmarkButton.setVisibility(View.GONE);
        floatingShareButton.setVisibility(View.GONE);
        imageViewMoviePoster.setVisibility(View.GONE);
    }

    private void hideConnectionWarning() {
        textViewNetworkStatus.setVisibility(View.GONE);
        textViewNoMovieDetails.setVisibility(View.GONE);
        linearLayoutContainerDetails.setVisibility(View.VISIBLE);
        linearLayoutAdditionalInfo.setVisibility(View.VISIBLE);
        floatingBookmarkButton.setVisibility(View.VISIBLE);
        floatingShareButton.setVisibility(View.VISIBLE);
        imageViewMoviePoster.setVisibility(View.VISIBLE);
    }

    private void checkConnectionAndStartLoader(){
        // Check for internet connection before start the loader
        if (!NetworkUtils.isDeviceConnected(this)) {
            doToast(getString(R.string.warning_you_are_not_connected));
            showConnectionWarning();
        } else {
            hideConnectionWarning();
            // Shows loading indicator and Kick off the loader
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }
}
