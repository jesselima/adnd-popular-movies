package com.udacity.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapters.CompanyListAdapter;
import com.udacity.popularmovies.adapters.MovieReviewsAdapter;
import com.udacity.popularmovies.adapters.MovieVideosAdapter;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.udacity.popularmovies.loaders.MovieLoader;
import com.udacity.popularmovies.loaders.ReviewListLoader;
import com.udacity.popularmovies.loaders.VideoListLoader;
import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;
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

@SuppressWarnings("ALL")
public class MovieDetailsActivity extends AppCompatActivity implements LoaderCallbacks {

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    // The Loader ID to be used by the LoaderManager
    private static final int MOVIE_DETAILS_LOADER_ID = 100;
    private static final int MOVIE_VIDEOS_LOADER_ID = 200;
    private static final int MOVIE_REVIEWS_LOADER_ID = 300;

    private static final int HIDE = View.GONE;
    private static final int SHOW = View.VISIBLE;
    private static final int INVISIBLE = View.INVISIBLE;

    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;
    private int page = 1;

    private Movie movieData = new Movie();
    private int movieId;
    private String movieHomepageUrl;
    private String movieOriginalTitle = "";

    private ArrayList<MovieProductionCompany> companies = new ArrayList<>();
    private CompanyListAdapter companyListAdapter;

    private final ArrayList<MovieVideo> movieVideosList = new ArrayList<>();
    private MovieVideosAdapter movieVideosAdapter;
    private List<MovieVideo> movieVideoList = new ArrayList<>();

    private final ArrayList<MovieReview> movieReviewsList = new ArrayList<>();
    private MovieReviewsAdapter movieReviewsAdapter;
    private List<MovieReview> movieReviewList = new ArrayList<>();

    private Toast toast;
    private boolean isBookmarkOnDatabase;
    private boolean isMovieWatched;

    private int deviceWidth;
    ActivityMovieDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Log.d(LOG_TAG,"===>>> onCreate called");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        // Get the movie ID and Title from the clicked item on the RecyclerView item.
        getIncomingIntent();
        progressBarStatus(SHOW);

        // RecyclerView for the list of companies
        companyListAdapter = new CompanyListAdapter(companies);
        binding.rvCompanies.setAdapter(companyListAdapter);
        binding.rvCompanies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvCompanies.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(binding.rvCompanies, false);

        // RecyclerView for the list of movie videos
        movieVideosAdapter = new MovieVideosAdapter(this, movieVideosList);
        binding.rvMoviesVideosDetails.setAdapter(movieVideosAdapter);
        binding.rvMoviesVideosDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvMoviesVideosDetails.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // Author: https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(binding.rvMoviesVideosDetails, false);

        // RecyclerView references and setups
        movieReviewsAdapter = new MovieReviewsAdapter(this, movieReviewsList);
        binding.rvMoviesReviewsDetails.setAdapter(movieReviewsAdapter);
        binding.rvMoviesReviewsDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvMoviesReviewsDetails.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // Author: https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(binding.rvMoviesReviewsDetails, false);

        // Button inside Overview card that send user to the movie homepage in the browser if URL is not null
        binding.btHomePage.setOnClickListener(new View.OnClickListener() {
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

        binding.floatIsWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the Bookmarks
                if (!isBookmarkOnDatabase && !isMovieWatched) {
                    new SaveBookmarkAsyncTask().execute();
                }
                new UpdateBookmarkAsyncTask().execute(movieData.getMovieId());
            }
        });

        binding.floatSaveBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBookmarkOnDatabase) {
                    // if the Movie is already bookmarked, remove it from the database and update icon status to unsaved icon
                    new DeleteBookmarkAsyncTask().execute(movieData.getMovieId());
                } else {
                    new SaveBookmarkAsyncTask().execute();
                }
            }
        });


        // FloatButton to share movie homepage to other app available on device.
        binding.floatShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWebUrl();
            }
        });

        LanguageUtils.checkSystemLanguage(loadApiLanguage);
        // Hide the Tagline TextView due to API to provide tagline to english language only or the
        // movie occasionally may have a empty tagline value. Then the space will be shown.
        if (loadApiLanguage.equals("")) binding.tvTagline.setVisibility(View.GONE);
        else binding.tvTagline.setVisibility(View.VISIBLE);

        // Setup ToolBar
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Setup CollapsingToolbar with movie name
        binding.collapsingToolbar.setTitle(movieOriginalTitle);

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
            warningConnection(SHOW);
            progressBarStatus(INVISIBLE);
        } else {
            warningConnection(HIDE);
            progressBarStatus(SHOW);
            // Shows loading indicator and Kick off the loader
            getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(MOVIE_VIDEOS_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER_ID, null, this);
        }
    }


    /* === LOADER CALLBACKS METHODS === */

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param loaderId is the loader id.
     * @param args     A mapping from String keys to various Parcelable values.
     * @return a new MovieListLoader object. This loader will receive the request URL and
     * make this request to the server in a background thread.
     * Helper article: https://stackoverflow.com/questions/15643907/multiple-loaders-in-same-activity
     */
    @NonNull
    @SuppressWarnings("ConstantConditions")
    @Override
    public Loader onCreateLoader(int loaderId, Bundle args) {

        progressBarStatus(SHOW);

        switch (loaderId) {
            case MOVIE_DETAILS_LOADER_ID: {

                Uri baseUrl = Uri.parse(ApiConfig.getBaseMovieDetailsUrl() + String.valueOf(movieId));
                Uri.Builder uriBuilder = baseUrl.buildUpon();
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
                return new MovieLoader(this, uriBuilder.toString());

            }
            case MOVIE_VIDEOS_LOADER_ID: {
                Uri stringBaseUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + String.valueOf(movieId) + getString(R.string.slash_videos));
                Uri.Builder uriBuilder = stringBaseUrl.buildUpon();
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.PAGE, String.valueOf(page));
                return new VideoListLoader(this, uriBuilder.toString());

            }
            case MOVIE_REVIEWS_LOADER_ID: {
                Uri stringBaseUrl = Uri.parse(ApiConfig.getBaseUrlV3Default() + String.valueOf(movieId) + getString(R.string.slash_reviews));
                Uri.Builder uriBuilder = stringBaseUrl.buildUpon();
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.API_KEY, API_KEY);
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.LANGUAGE, loadApiLanguage);
                uriBuilder.appendQueryParameter(ApiConfig.UrlParamKey.PAGE, String.valueOf(page));
                return new ReviewListLoader(this, uriBuilder.toString());
            }
        }
        return null;

    }

    /**
     * Called when a previously created loader has finished its work.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader. In this case, details of a movie.
     */
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        int id = loader.getId();// find which loader you called

        switch (id) {
            case MOVIE_DETAILS_LOADER_ID:
                movieData = (Movie) data;
                // Get the movie returned from the loader and add to a global movie instance.
                // So it can be accessed from another methods.
                if (isMovieValid(movieData)) {
                    warningConnection(HIDE);
                    // Updates the UI with details of the movie
                    updateUI(movieData);
                    // if the movie bookmark is on the database update the floating Bookmark Button icon
                    new CheckBookmarkAsyncTask().execute();

                    movieHomepageUrl = movieData.getMovieHomepage();
                    // Clear the companies list before add new data. It's avoid memory leaks.
                    companies.clear();
                    // Add new data to the list.
                    if (movieData.getCompaniesArrayList() == null || movieData.getCompaniesArrayList().size() == 0) {
                        binding.rvCompanies.setVisibility(HIDE);
                        binding.textViewNoCompanies.setVisibility(View.VISIBLE);
                    }else {
                        companies.addAll(movieData.getCompaniesArrayList());
                        companyListAdapter.notifyDataSetChanged();
                    }
                } else {
                    warningDetails(SHOW);
                    binding.sectionFullContent.setVisibility(HIDE);
                }

                progressBarStatus(INVISIBLE);
                break;

            case MOVIE_VIDEOS_LOADER_ID:
                movieVideoList = (List<MovieVideo>) data;
                // When the onCreateLoader finish its job, it will pass the data do this method.
                if (movieVideoList == null || movieVideoList.isEmpty() || movieVideoList.size() == 0) {
                    // If there is no movie to show give a warning to the user in the UI.
                    binding.rvMoviesVideosDetails.setVisibility(HIDE);
                    binding.textViewNoVideos.setVisibility(View.VISIBLE);
                } else {
                    warningConnection(HIDE);
                }
                movieVideosList.clear();
                movieVideosList.addAll(movieVideoList);
                movieVideosAdapter.notifyDataSetChanged();
                progressBarStatus(INVISIBLE);
                break;

            case MOVIE_REVIEWS_LOADER_ID:
                movieReviewList = (List<MovieReview>) data;
                // When the onCreateLoader finish its job, it will pass the data do this method.
                // If there is no movie to show give a warning to the user in the UI.
                if (movieVideoList == null || movieVideoList.isEmpty() || movieVideoList.size() == 0) {
                    binding.rvMoviesReviewsDetails.setVisibility(HIDE);
                    binding.textViewNoReviews.setVisibility(View.VISIBLE);
                }
                movieReviewsList.clear();
                movieReviewsList.addAll(movieReviewList);
                movieReviewsAdapter.notifyDataSetChanged();
                progressBarStatus(INVISIBLE);
                break;
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

    /**
     * Check uf the movie returned from the Loader is valid
     *
     * @param movie is the {@link Movie} object with details.
     * @return return true is the movie object is not null.
     */
    private boolean isMovieValid(Movie movie) {
        // Avoid error with when movie has no release date available and replace with "-"
        if (movie.getMovieReleaseDate() == null) movie.setMovieReleaseDate("-");
        // return true ir the movie object is not null.
        return movie != null;
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
                .into(binding.ivMoviePoster);

        Picasso.get()
                .load(movie.getMovieBackdropPath())
                .placeholder(R.drawable.backdrop_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.backdrop_image_place_holder)
                .into(binding.ivMovieBackdrop);

        binding.tvOverview.setText(movie.getMovieOverview());
        binding.tvVoteAverage.setText(String.valueOf(movie.getMovieVoteAverage()));

        binding.tvRuntime.setText(String.valueOf(movie.getMovieRunTime() + getString(R.string.min)));

        String formatedDate = DateUtils.simpleDateFormat(movie.getMovieReleaseDate());
        binding.tvReleaseDate.setText(formatedDate);

        binding.tvTitle.setText(movie.getMovieTitle());
        binding.tvOriginalLanguage.setText(movie.getMovieSpokenLanguage());

        String taglineWithQuotes = movie.getMovieTagline();
        binding.tvTagline.setText(taglineWithQuotes);

        binding.tvPopularity.setText(String.valueOf(movie.getMoviePopularity()));
        binding.tvVoteCount.setText(String.valueOf(movie.getMovieVoteCount()));

        if (movie.getMovieBuget() == 0) binding.tvBuget.setText(R.string.unavailable);
        else binding.tvBuget.setText(formatNumber(movie.getMovieBuget()));

        if (movie.getMovieRevenue() == 0) binding.tvRevenue.setText(R.string.unavailable);
        else binding.tvRevenue.setText(formatNumber(movie.getMovieRevenue()));

        binding.tvGenres.setText(movie.getMovieGenres());

        progressBarStatus(INVISIBLE);

    }// Closes updateUI method


    /* === SQLITE DATABASE MANAGEMENT METHODS === */

    private class SaveBookmarkAsyncTask extends AsyncTask<Void, Void, Uri> {

        @Override
        protected void onPreExecute() {
            progressBarStatus(SHOW);
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            int watched = 0;

            ContentValues contentValues = new ContentValues();
            contentValues.put(BookmarkEntry.COLUMN_API_ID,          movieData.getMovieId());
            contentValues.put(BookmarkEntry.COLUMN_ORIGINAL_TITLE,  movieData.getMovieOriginalTitle());
            contentValues.put(BookmarkEntry.COLUMN_RELEASE_DATE,    movieData.getMovieReleaseDate());
            contentValues.put(BookmarkEntry.COLUMN_RUNTIME,         movieData.getMovieRunTime());
            contentValues.put(BookmarkEntry.COLUMN_GENRES,          movieData.getMovieGenres());
            contentValues.put(BookmarkEntry.COLUMN_HOMEPAGE,        movieData.getMovieHomepage());
            contentValues.put(BookmarkEntry.COLUMN_TAGLINE,         movieData.getMovieTagline());
            contentValues.put(BookmarkEntry.COLUMN_OVERVIEW,        movieData.getMovieOverview());
            contentValues.put(BookmarkEntry.COLUMN_SPOKEN_LANGUAGES, movieData.getMovieSpokenLanguage());
            contentValues.put(BookmarkEntry.COLUMN_VOTE_AVERAGE,    movieData.getMovieVoteAverage());
            contentValues.put(BookmarkEntry.COLUMN_VOTE_COUNT,      movieData.getMovieVoteCount());
            contentValues.put(BookmarkEntry.COLUMN_POPULARITY,      movieData.getMoviePopularity());
            contentValues.put(BookmarkEntry.COLUMN_BUDGET,          movieData.getMovieBuget());
            contentValues.put(BookmarkEntry.COLUMN_REVENUE,         movieData.getMovieRevenue());
            contentValues.put(BookmarkEntry.COLUMN_IS_WATCHED,      watched);
            contentValues.put(BookmarkEntry.COLUMN_HOMEPAGE,        movieData.getMovieHomepage());
            contentValues.put(BookmarkEntry.COLUMN_MOVIE_IMAGE,     convertImageViewToBytes(binding.ivMoviePoster));

            return getContentResolver().insert(BookmarkEntry.CONTENT_URI, contentValues);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_saved);
                Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.movie_saved), Toast.LENGTH_SHORT).show();
                isBookmarkOnDatabase = true;
            } else {
                doToast(getResources().getString(R.string.warning_could_not_be_saved));
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
            }
            progressBarStatus(INVISIBLE);
        }
    }

    private class DeleteBookmarkAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBarStatus(SHOW);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            long id = integers[0];
            String stringId = Long.toString(id);

            return getContentResolver().delete(
                    BookmarkEntry.CONTENT_URI,
                    BookmarkEntry.COLUMN_API_ID + "=?",
                    new String[]{stringId}
            );
        }

        @Override
        protected void onPostExecute(Integer rowsDeleted) {
            if (rowsDeleted > 0) {
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
                binding.floatIsWatched.setImageResource(R.drawable.ic_watched_not);
                binding.tvLabelWatched.setVisibility(HIDE);
                doToast(getResources().getString(R.string.movie_removed));
                isBookmarkOnDatabase = false;
                isMovieWatched = false;
            } else {
                doToast(getResources().getString(R.string.warning_could_not_be_removed));
            }
            progressBarStatus(INVISIBLE);
        }
    }

    private class CheckBookmarkAsyncTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            progressBarStatus(SHOW);
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            String stringId = String.valueOf(movieData.getMovieId());
            Uri uri = BookmarkEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            Cursor cursor = getContentResolver().query(
                    BookmarkEntry.CONTENT_URI,
                    new String[]{BookmarkEntry.COLUMN_IS_WATCHED},          // projection (colunms).
                    BookmarkEntry.COLUMN_API_ID + "=?",            // selection
                    new String[]{String.valueOf(movieData.getMovieId())},   // selectionArgs
                    null);                                         // sortOrder
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {

            isBookmarkOnDatabase = cursor.getCount() > 0;
            if (isBookmarkOnDatabase) {
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_saved);
            } else {
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
            }

            if (cursor.moveToFirst()) {
                int WATCHED = cursor.getInt(cursor.getColumnIndex(BookmarkEntry.COLUMN_IS_WATCHED));
                if (WATCHED == 0) {
                    isMovieWatched = false;
                    binding.floatIsWatched.setImageResource(R.drawable.ic_watched_not);
                    animateTextFadeOut(binding.tvLabelWatched);
                }if (WATCHED == 1){
                    isMovieWatched = true;
                    binding.floatIsWatched.setImageResource(R.drawable.ic_watched_yes);
                    animateTextFadeIn(binding.tvLabelWatched);
                }
            }
            progressBarStatus(INVISIBLE);
        }
    }

    private class UpdateBookmarkAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBarStatus(SHOW);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            int NOT_WATCHED = 0;
            int IS_WATCHED = 1;
            // Default value is Not Watched == 0
            int WATCHED_STATUS = 0;

            if (isMovieWatched) WATCHED_STATUS = NOT_WATCHED;
            else  WATCHED_STATUS = IS_WATCHED;

            ContentValues contentValues = new ContentValues();
            contentValues.put(BookmarkEntry.COLUMN_IS_WATCHED,   WATCHED_STATUS);

            long id = integers[0];
            String stringId = Integer.toString(movieData.getMovieId());
            Uri uri = BookmarkEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            return getContentResolver()
                    .update(
                        uri,
                        contentValues,
                        null,
                        null
            );
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated) {

            if (rowsUpdated > 0) {
                if (isMovieWatched){
                    binding.floatIsWatched.setImageResource(R.drawable.ic_watched_not);
                    doToast(getResources().getString(R.string.not_watched));
                    animateTextFadeOut(binding.tvLabelWatched);
                    isMovieWatched = false;
                }else {
                    binding.floatIsWatched.setImageResource(R.drawable.ic_watched_yes);
                    Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.watched), Toast.LENGTH_SHORT).show();
                    animateTextFadeIn(binding.tvLabelWatched);
                    isMovieWatched = true;
                }
            }
            progressBarStatus(INVISIBLE);
        }
    }

    /* === HELPER METHODS === */

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
            intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, movieData.getMovieOriginalTitle());
        startActivity(intent);
    }

    /**
     * When called do a fade in effect on a TextView.
     * @param textToFadeIn is any TextView object to be animated using fade effect.
     */
    private void animateTextFadeIn(TextView textToFadeIn) {
        textToFadeIn.setVisibility(SHOW);
        textToFadeIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_animation));
    }
    /**
     * When called do a fade out effect on a TextView.
     * @param textToFadeOut is any TextView object to be animated using fade effect.
     */
    private void animateTextFadeOut(TextView textToFadeOut) {
        textToFadeOut.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_animation));
        textToFadeOut.setVisibility(HIDE);
    }


    /* === UI HIDE/SHOW WARNING === */

    private void warningConnection(int VISIBILITY) {
        binding.tvWarningNoData.setVisibility(VISIBILITY);
        binding.ivWarningNoData.setVisibility(VISIBILITY);
        if (VISIBILITY == SHOW) {
            setContentVisibility(HIDE);
            doToast(getString(R.string.warning_you_are_not_connected));
        } else {
            setContentVisibility(SHOW);
        }
    }

    private void setContentVisibility(int VISIBILITY) {
        binding.floatSaveBookmark.setVisibility(VISIBILITY);
        binding.floatShareButton.setVisibility(VISIBILITY);
        binding.ivMoviePoster.setVisibility(VISIBILITY);
        binding.sectionFullContent.setVisibility(VISIBILITY);
    }

    private void warningDetails(int VISIBILITY) {
        binding.tvWarningNoData.setText(getResources().getText(R.string.warning_no_details));
        binding.tvWarningNoData.setVisibility(VISIBILITY);
        binding.ivWarningNoData.setImageResource(R.drawable.ic_details);
        binding.ivWarningNoData.setVisibility(VISIBILITY);

    }

    private void progressBarStatus(int VISIBILITY) {
        if (VISIBILITY == SHOW) binding.indeterminateBar.setIndeterminate(true);
        else binding.indeterminateBar.setIndeterminate(false);
        binding.indeterminateBar.setVisibility(VISIBILITY);
    }


    /* === Lifecycle methods === */

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("===>>> onStart", " called");
        if (NetworkUtils.isDeviceConnected(this)) {
            new CheckBookmarkAsyncTask().execute();
            warningConnection(HIDE);
        } else {
            warningConnection(SHOW);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("===>>> onPause", " called");
        if (NetworkUtils.isDeviceConnected(this)) {
            warningConnection(HIDE);
        } else {
            warningConnection(SHOW);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("===>>> onResume", " called");
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
            warningConnection(HIDE);
            getSupportLoaderManager().restartLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        } else {
            warningConnection(SHOW);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("===>>> onRestart", " called");
    }
}
