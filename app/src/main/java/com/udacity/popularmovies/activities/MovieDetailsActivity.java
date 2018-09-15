package com.udacity.popularmovies.activities;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.CompanyListAdapter;
import com.udacity.popularmovies.adapters.MovieReviewsAdapter;
import com.udacity.popularmovies.adapters.MovieVideosAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.loaders.MovieLoader;
import com.udacity.popularmovies.loaders.ReviewListLoader;
import com.udacity.popularmovies.loaders.VideoListLoader;
import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;
import com.udacity.popularmovies.localdatabase.BookmarkDbHelper;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.models.MovieProductionCompany;
import com.udacity.popularmovies.models.MovieReview;
import com.udacity.popularmovies.models.MovieVideo;
import com.udacity.popularmovies.utils.DateUtils;
import com.udacity.popularmovies.utils.LanguageUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderCallbacks {

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    // The Loader ID to be used by the LoaderManager
    private static final int MOVIE_DETAILS_LOADER_ID = 100;
    private static final int MOVIE_VIDEOS_LOADER_ID = 200;
    private static final int MOVIE_REVIEWS_LOADER_ID = 300;

    private Movie movieData = new Movie();

    private int movieId;
    private String movieOriginalTitle = "";
    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;
    private int page = 1;
    private TabLayout tabLayout;
    private Toast toast;

    // Object for UI references
    private ImageView imageViewMoviePoster;
    private ImageView imageViewMovieBackdrop;
    private TextView textViewOverview;
    private TextView textViewReleaseDate;
    private TextView textViewRuntime;
    private TextView textViewTitle;
    private TextView textViewVoteAverage;
    private TextView textViewOriginalLanguage;
    private TextView textViewTagline;
    private TextView textViewPopularity;
    private TextView textViewVoteCount;
    private TextView textViewBuget;
    private TextView textViewRevenue;
    private TextView textViewGenres;
    private TextView textViewNetworkStatus;
    private TextView textViewNoMovieDetails;
    private LinearLayout linearLayoutContainerDetails, linearLayoutAdditionalInfo;
    private String movieHomepageUrl;
    private ImageView imageViewArrowRight;

    private SQLiteDatabase sqLiteDatabase;
    private BookmarkDbHelper bookmarkDbHelper = new BookmarkDbHelper(this);

    private FloatingActionButton floatingBookmarkButton = null;
    private FloatingActionButton floatingShareButton = null;
    private ProgressBar progressBar;

    private ArrayList<MovieProductionCompany> companies = new ArrayList<>();
    private CompanyListAdapter companyListAdapter;

    private RecyclerView recyclerViewVideos;
    private MovieVideosAdapter movieVideosAdapter;
    private final ArrayList<MovieVideo> movieVideosList = new ArrayList<>();
    private List<MovieVideo> movieVideoList = new ArrayList<>();

    private RecyclerView recyclerViewReviews;
    private MovieReviewsAdapter movieReviewsAdapter;
    private final ArrayList<MovieReview> movieReviewsList = new ArrayList<>();
    private List<MovieReview> movieReviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Log.d("===>>> onCreate", " called");

        tabLayout = findViewById(R.id.tab_layout);

        progressBar = findViewById(R.id.indeterminateBar);
        showProgressBar();

        sqLiteDatabase = bookmarkDbHelper.getWritableDatabase();
        // Get the movie ID and Title from the clicked item on the RecyclerView item.
        getIncomingIntent();

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
        linearLayoutContainerDetails = findViewById(R.id.container_details);
        linearLayoutAdditionalInfo = findViewById(R.id.container_additional_content);
        imageViewArrowRight = findViewById(R.id.image_view_arrow_right);

        // RecyclerView for the list of companies
        RecyclerView recyclerViewCompanies = findViewById(R.id.rv_companies);
        companyListAdapter = new CompanyListAdapter(this, companies);
        recyclerViewCompanies.setAdapter(companyListAdapter);
        recyclerViewCompanies.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompanies.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(recyclerViewCompanies, false);

        // RecyclerView for the list of movie videos
        recyclerViewVideos = findViewById(R.id.rv_movies_videos_details);
        movieVideosAdapter = new MovieVideosAdapter(this, movieVideosList);
        recyclerViewVideos.setAdapter(movieVideosAdapter);
        recyclerViewVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVideos.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // Author: https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(recyclerViewVideos, false);

        // RecyclerView references and setups
        recyclerViewReviews = findViewById(R.id.rv_movies_reviews_details);
        movieReviewsAdapter = new MovieReviewsAdapter(this, movieReviewsList);
        recyclerViewReviews.setAdapter(movieReviewsAdapter);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // Author: https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(recyclerViewReviews, false);

        // Button inside Overview card that send user to the movie homepage in the browser if URL is not null
        Button buttonHomepage = findViewById(R.id.bt_home_page);
        buttonHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieData.getMovieHomepage().equals("null")) {
                    doToast(getString(R.string.warning_homepage_not_available));
                } else if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
                    doToast(getString(R.string.warning_you_are_not_connected));
                } else {
                    openWebPage(movieHomepageUrl);
                }
            }
        });

        Button buttonReviews = findViewById(R.id.bt_reviews);
        buttonReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MovieReviewsActivity.class);
                intent.putExtra(ApiConfig.JsonKey.ID, movieData.getMovieId());
                intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, movieData.getMovieOriginalTitle());
                startActivity(intent);
            }
        });

        Button buttonVideos = findViewById(R.id.bt_videos);
        buttonVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MovieVideosActivity.class);
                intent.putExtra(ApiConfig.JsonKey.ID, movieData.getMovieId());
                intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, movieData.getMovieOriginalTitle());
                startActivity(intent);
            }
        });

        floatingBookmarkButton = findViewById(R.id.float_save_bookmark);
        floatingBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBookmarkOnDatabase()) {
                    // if the Movie is already bookmarked, remove it from the database and update icon status to unsaved icon
                    deleteBookmark(/*movieData.getMovieId()*/);
                    floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_unsaved);
                    doToast(getResources().getString(R.string.movie_removed));
                } else {
                    saveBookmark(convertImageViewToBytes(imageViewMoviePoster));
                    floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_saved);
                    doToast(getResources().getString(R.string.movie_saved));
                }
            }
        });

        // TODO === <SETUP TABS> ====
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_details)/*.setText(R.string.details)*/);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_movie)/*.setText(R.string.videos)*/);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_comments)/*.setText(R.string.reviews)*/);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                doToast("Tab selected at position:" + String.valueOf(tab.getPosition()));
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        doToast("Tab selected at position: " + String.valueOf(tab.getPosition()) + " - Details");
                        break;
                    case 1:
                        doToast("Tab selected at position: " + String.valueOf(tab.getPosition()) + " - Videos");
                        break;
                    default:
                        doToast("Tab selected at position:  " + String.valueOf(tab.getPosition()) + " - Reviews");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        // TODO === <SETUP TABS> ====


        // FloatButton to share movie homepage to other app available on device.
        floatingShareButton = findViewById(R.id.float_share_button);
        floatingShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWebUrl();
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

    private void checkConnectionAndStartLoader() {
        // Check for internet connection before start the loader
        if (!NetworkUtils.isDeviceConnected(this)) {
            doToast(getString(R.string.warning_you_are_not_connected));
            showConnectionWarning();
            hideProgressBar();
        } else {
            hideConnectionWarning();
            showProgressBar();
            // Shows loading indicator and Kick off the loader
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
            loaderManager.initLoader(MOVIE_VIDEOS_LOADER_ID, null, this);
            loaderManager.initLoader(MOVIE_REVIEWS_LOADER_ID, null, this);
        }
    }


    /* === LOADER CALLBACKS === */

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param loaderId   is the loader id.
     * @param args A mapping from String keys to various Parcelable values.
     * @return a new MovieListLoader object. This loader will receive the request URL and
     * make this request to the server in a background thread.
     * Helper article: https://stackoverflow.com/questions/15643907/multiple-loaders-in-same-activity
     */
    @Override
    public Loader onCreateLoader(int loaderId, Bundle args) {

        if (loaderId == MOVIE_DETAILS_LOADER_ID ) {

            Uri baseUrl = Uri.parse(ApiConfig.getBaseMovieDetailsUrl() + String.valueOf(movieId));
            Uri.Builder uriBuilder = baseUrl.buildUpon();
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
            return new MovieLoader(this, uriBuilder.toString());

        } else if (loaderId == MOVIE_VIDEOS_LOADER_ID ) {
            Uri stringBaseUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + String.valueOf(movieId) + "/videos");
            Uri.Builder uriBuilder = stringBaseUrl.buildUpon();
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.PAGE, String.valueOf(page));
            return new VideoListLoader(this, uriBuilder.toString());

        } else if (loaderId == MOVIE_REVIEWS_LOADER_ID) {
            Uri stringBaseUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + String.valueOf(movieId) + "/reviews");
            Uri.Builder uriBuilder = stringBaseUrl.buildUpon();
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
            uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.PAGE, String.valueOf(page));
            return new ReviewListLoader(this,uriBuilder.toString());
        }
        return null;

    }

    /**
     * Called when a previously created loader has finished its work.
     *
     * @param loader The Loader that has finished.
     * @param data  The data generated by the Loader. In this case, details of a movie.
     */
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();// find which loader you called

        if (id == MOVIE_DETAILS_LOADER_ID ) {
            movieData = (Movie) data;
            // Get the movie returned from the loader and add to a global movie instance.
            // So it can be accessed from another methods.
            if (!isMovieValid(movieData)) {

                textViewNetworkStatus.setVisibility(View.GONE);
                // Updates the UI with details of the movie
                updateUI(movieData);

                if (checkBookmarkOnDatabase()) {
                    floatingBookmarkButton.setImageResource(R.drawable.ic_bookmark_saved);
                } else {
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

        } else if (id == MOVIE_VIDEOS_LOADER_ID ) {

            movieVideoList = (List<MovieVideo>)data;
            // When the onCreateLoader finish its job, it will pass the data do this method.
            if (movieVideoList == null || movieVideoList.isEmpty()) {
                // If there is no movie to show give a warning to the user in the UI.
//                showNoResultsWarning();
                doToast("No videos to show");
            } else {

                hideProgressBar();
                hideConnectionWarning();
//                hideNoResultsWarning();
                if (movieVideoList.size() > 1){
                    imageViewArrowRight.setVisibility(View.VISIBLE);
                }else {
                    imageViewArrowRight.setVisibility(View.GONE);
                }
            }
            movieVideosList.clear();
            movieVideosList.addAll(movieVideoList);
            movieVideosAdapter.notifyDataSetChanged();

        } else if (id == MOVIE_REVIEWS_LOADER_ID) {
            movieReviewList = (List<MovieReview>)data;

            // When the onCreateLoader finish its job, it will pass the data do this method.
            if (movieReviewList == null || movieReviewList.isEmpty()) {
                // If there is no movie to show give a warning to the user in the UI.
//                showNoResultsWarning();
                hideProgressBar();
                hideConnectionWarning();
            } else {
                hideProgressBar();
                hideConnectionWarning();
//                hideNoResultsWarning();
            }

            movieReviewsList.clear();
            movieReviewsList.addAll(movieReviewList);
            movieReviewsAdapter.notifyDataSetChanged();
        }

    }

    /**
     * Check uf the movie returned from the Loader is valid
     *
     * @param movie is the {@link Movie} object with details.
     * @return return true is the movie is
     */
    private boolean isMovieValid(Movie movie) {
        return movie == null;
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader loader) {
    }


    /* === UPDATE UI === */

    /**
     * When called receives the movie object with movie details and updates the UI.
     *
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
        textViewOriginalLanguage.setText(movie.getMovieSpokenLanguage());

        String taglineWithQuotes = movie.getMovieTagline();
        textViewTagline.setText(taglineWithQuotes);

        textViewPopularity.setText(String.valueOf(movie.getMoviePopularity()));
        textViewVoteCount.setText(String.valueOf(movie.getMovieVoteCount()));

        if(movie.getMovieBuget() == 0) textViewBuget.setText(R.string.unavailable);
        else textViewBuget.setText(formatNumber(movie.getMovieBuget()));

        if(movie.getMovieRevenue() == 0) textViewRevenue.setText(R.string.unavailable);
        else textViewRevenue.setText(formatNumber(movie.getMovieRevenue()));

        textViewGenres.setText(movie.getMovieGenres());

        progressBar.setVisibility(View.INVISIBLE);

    }// Closes updateUI method


    /* SQLite Database management methods */
    // TODO save bookmarks in async task
    private boolean saveBookmark(byte[] imageInBytesArray) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookmarkEntry.COLUMN_API_ID, movieData.getMovieId());
        contentValues.put(BookmarkEntry.COLUMN_ORIGINAL_TITLE, movieData.getMovieOriginalTitle());
        contentValues.put(BookmarkEntry.COLUMN_RELEASE_DATE, movieData.getMovieReleaseDate());
        contentValues.put(BookmarkEntry.COLUMN_RUNTIME, movieData.getMovieRunTime());
        contentValues.put(BookmarkEntry.COLUMN_GENRES, movieData.getMovieGenres());
        contentValues.put(BookmarkEntry.COLUMN_HOMEPAGE, movieData.getMovieHomepage());
        contentValues.put(BookmarkEntry.COLUMN_TAGLINE, movieData.getMovieTagline());
        contentValues.put(BookmarkEntry.COLUMN_OVERVIEW, movieData.getMovieOverview());
        contentValues.put(BookmarkEntry.COLUMN_SPOKEN_LANGUAGES, movieData.getMovieSpokenLanguage());
        contentValues.put(BookmarkEntry.COLUMN_VOTE_AVERAGE, movieData.getMovieVoteAverage());
        contentValues.put(BookmarkEntry.COLUMN_VOTE_COUNT, movieData.getMovieVoteCount());
        contentValues.put(BookmarkEntry.COLUMN_POPULARITY, movieData.getMoviePopularity());
        contentValues.put(BookmarkEntry.COLUMN_BUDGET, movieData.getMovieBuget());
        contentValues.put(BookmarkEntry.COLUMN_REVENUE, movieData.getMovieRevenue());
        contentValues.put(BookmarkEntry.COLUMN_HOMEPAGE, movieData.getMovieHomepage());
        contentValues.put(BookmarkEntry.COLUMN_MOVIE_IMAGE, imageInBytesArray);

        Uri uri = getContentResolver().insert(BookmarkEntry.CONTENT_URI, contentValues);
        // Return TRUE if the uri returned uri is not null.
        return uri != null;

    }

    // TODO delete bookmarks in async task
    private boolean deleteBookmark(/*long id*/) {
        return getContentResolver().delete(BookmarkEntry.CONTENT_URI, BookmarkEntry._ID, null) > 0;
    }

    // TODO check bookmarks in async task
    private boolean checkBookmarkOnDatabase() {

        Cursor cursor = sqLiteDatabase.query(
                BookmarkEntry.TABLE_NAME,   // table
                new String[]{BookmarkEntry.COLUMN_API_ID, BookmarkEntry.COLUMN_ORIGINAL_TITLE},  // columns
                BookmarkEntry.COLUMN_API_ID + "=?",   // selection
                new String[]{String.valueOf(movieData.getMovieId())},   // selectionArgs
                null,      // groupBy
                null,       // having
                null);     // orderBy

        boolean isOnDatabase = cursor.getCount() > 0;
        cursor.close();

        return isOnDatabase;
    }


    /* Helper methods */

    public byte[] convertImageViewToBytes(ImageView imageView) {
        /* Get ImageView to Bitmap from Greg Giacovelli
        https://stackoverflow.com/questions/4715044/android-how-to-convert-whole-imageview-to-bitmap */
        imageView.buildDrawingCache();
        Bitmap imageBitmap = imageView.getDrawingCache();
        /* */

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void shareWebUrl() {
        if (movieData.getMovieHomepage().equals("null")) {
            doToast(getString(R.string.warning_homepage_not_available));
        } else {
            String movieInfoToShare = getString(R.string.checkout_this_movie);
            movieInfoToShare += "\n" + movieData.getMovieOriginalTitle();
            movieInfoToShare += "\n" + movieData.getMovieHomepage();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, movieInfoToShare);
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

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
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

    /**
     * When called must receive a number that will be converted to a String with the pattern $###,###,###.##
     *
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
     *
     * @param urlHomepage is the url to be open in the browser.
     */
    private void openWebPage(String urlHomepage) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(ApiConfig.JsonKey.HOMEPAGE, urlHomepage);
        startActivity(intent);
    }


    /* Lifecycle methods */

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("===>>> onStart", " called");
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("===>>> onPause", " called");
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
        } else {
            showConnectionWarning();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("===>>> onResume", " called");
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
            hideConnectionWarning();
            getLoaderManager().restartLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        } else {
            showConnectionWarning();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("===>>> onRestart", " called");
    }

}
