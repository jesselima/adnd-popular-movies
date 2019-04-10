package com.udacity.popularmovies.moviedetails.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.udacity.popularmovies.localdatabase.BookmarkContract.BookmarkEntry;
import com.udacity.popularmovies.moviedetails.models.Genre;
import com.udacity.popularmovies.moviedetails.models.MovieDetailsResponse;
import com.udacity.popularmovies.moviedetails.models.ProductionCompany;
import com.udacity.popularmovies.moviedetails.models.Review;
import com.udacity.popularmovies.moviedetails.models.Video;
import com.udacity.popularmovies.moviesfeed.models.Movie;
import com.udacity.popularmovies.shared.utils.DateUtils;
import com.udacity.popularmovies.shared.utils.LanguageUtils;
import com.udacity.popularmovies.shared.utils.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class MovieDetailsActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    // The Loader ID to be used by the LoaderManager

    private static final int HIDE = View.GONE;
    private static final int SHOW = View.VISIBLE;
    private static final int INVISIBLE = View.INVISIBLE;

    private String loadApiLanguage = ApiConfig.UrlParamValue.LANGUAGE_DEFAULT;
    private int page = 1;

    private MovieDetailsResponse movieDetailsResponse = new MovieDetailsResponse();
    private int movieId;
    private String movieHomepageUrl;
    private String movieOriginalTitle = "";

    private List<ProductionCompany> companies = new ArrayList<>();
    private CompanyListAdapter companyListAdapter;
    private List<ProductionCompany> companyLis = new ArrayList<>();

    private final List<Video> videosList = new ArrayList<>();
    private MovieVideosAdapter movieVideosAdapter;
    private List<Video> videoList = new ArrayList<>();

    private final List<Review> reviewsList = new ArrayList<>();
    private MovieReviewsAdapter movieReviewsAdapter;
    private List<Review> reviewList = new ArrayList<>();

    private Toast toast;
    private boolean isBookmarkOnDatabase;
    private boolean isMovieWatched;

    private int deviceWidth;
    private ActivityMovieDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Log.d(LOG_TAG,"===>>> onCreate called");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        // Get the movie ID and Title from the clicked item on the RecyclerView item.
        getIncomingIntent();
        progressBarStatus(SHOW);




        // Button inside Overview card that send user to the movie homepage in the browser if URL is not null
        binding.btHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (movieDetailsResponse.getMovieHomepage().equals("null")) {
//                    doToast(getString(R.string.warning_homepage_not_available));
//                } else if (!NetworkUtils.isDeviceConnected(getApplicationContext())) {
//                    doToast(getString(R.string.warning_you_are_not_connected));
//                } else {
//                    openWebPage(movieHomepageUrl);
//                }
            }
        });

        binding.floatIsWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the Bookmarks
                if (!isBookmarkOnDatabase && !isMovieWatched) {
                    new SaveBookmarkAsyncTask().execute();
                }
                new UpdateBookmarkAsyncTask().execute(movieDetailsResponse.getId());
            }
        });

        binding.floatSaveBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBookmarkOnDatabase) {
                    // if the Movie is already bookmarked, remove it from the database and update icon status to unsaved icon
                    new DeleteBookmarkAsyncTask().execute(movieDetailsResponse.getId());
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

        // TODO:Call method to load movie details from tha API

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

    private void initViews() {

        // RecyclerView for the list of companies
        companyListAdapter = new CompanyListAdapter(companies);
        binding.rvCompanies.setAdapter(companyListAdapter);
        binding.rvCompanies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvCompanies.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(binding.rvCompanies, false);

        // RecyclerView for the list of movie videos
        movieVideosAdapter = new MovieVideosAdapter(this, videosList);
        binding.rvMoviesVideosDetails.setAdapter(movieVideosAdapter);
        binding.rvMoviesVideosDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvMoviesVideosDetails.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(binding.rvMoviesVideosDetails, false);

        // RecyclerView references and setups
        movieReviewsAdapter = new MovieReviewsAdapter(this, reviewsList);
        binding.rvMoviesReviewsDetails.setAdapter(movieReviewsAdapter);
        binding.rvMoviesReviewsDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvMoviesReviewsDetails.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(binding.rvMoviesReviewsDetails, false);

    }

    /* === UPDATE UI === */

    /**
     * When called receives the movie object with movie details and updates the UI.
     *
     * @param movie is the {@link Movie} object with movie details.
     */
    private void updateUI(MovieDetailsResponse movie) {

        Picasso.get()
                .load(movie.getPosterPath()) // TODO: Set the poster path properly
                .placeholder(R.drawable.poster_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.poster_image_place_holder)
                .into(binding.ivMoviePoster);

        Picasso.get()
                .load(movie.getBackdropPath())// TODO: Set the backdrop path properly
                .placeholder(R.drawable.backdrop_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.backdrop_image_place_holder)
                .into(binding.ivMovieBackdrop);

        binding.tvOverview.setText(movie.getOverview());
        binding.tvVoteAverage.setText(String.valueOf(movie.getVoteAverage()));

        binding.tvRuntime.setText(String.valueOf(movie.getRuntime() + getString(R.string.min)));

        String formatedDate = DateUtils.simpleDateFormat(movie.getReleaseDate());
        binding.tvReleaseDate.setText(formatedDate);

        binding.tvTitle.setText(movie.getTitle());
        binding.tvOriginalLanguage.setText(movie.getOriginalLanguage());

        String taglineWithQuotes = movie.getTagline();
        binding.tvTagline.setText(taglineWithQuotes);

        binding.tvPopularity.setText(String.valueOf(movie.getPopularity()));
        binding.tvVoteCount.setText(String.valueOf(movie.getVoteCount()));

        if (movie.getBudget() == 0) binding.tvBuget.setText(R.string.unavailable);
        else binding.tvBuget.setText(formatNumber(movie.getBudget()));

        if (movie.getRevenue() == 0) binding.tvRevenue.setText(R.string.unavailable);
        else binding.tvRevenue.setText(formatNumber(movie.getRevenue()));

        for (Genre genre : movie.getGenres()) {
            binding.tvGenres.append(genre.getName() + " ");
        }

        progressBarStatus(INVISIBLE);

    }// Closes updateUI method


    /* === SQLITE DATABASE MANAGEMENT METHODS === */
    private class SaveBookmarkAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBarStatus(SHOW);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int watched = 0;

            // Implement save method

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isBookmarkOnDatabase) {
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_saved);
                Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.movie_saved), Toast.LENGTH_SHORT).show();
                // isBookmarkOnDatabase = true;
            } else {
                doToast(getResources().getString(R.string.warning_could_not_be_saved));
                binding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
                isBookmarkOnDatabase = false;
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
            String stringId = String.valueOf(movieDetailsResponse.getId());
            Uri uri = BookmarkEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            Cursor cursor = getContentResolver().query(
                    BookmarkEntry.CONTENT_URI,
                    new String[]{BookmarkEntry.COLUMN_IS_WATCHED},          // projection (colunms).
                    BookmarkEntry.COLUMN_API_ID + "=?",            // selection
                    new String[]{String.valueOf(movieDetailsResponse.getId())},   // selectionArgs
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
            String stringId = Integer.toString(movieDetailsResponse.getId());
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
//        if (movieDetailsResponse.getMovieHomepage().equals("null")) {
//            doToast(getString(R.string.warning_homepage_not_available));
//        } else {
//            String movieInfoToShare = getString(R.string.checkout_this_movie);
//            movieInfoToShare += "\n" + movieDetailsResponse.getMovieOriginalTitle();
//            movieInfoToShare += "\n" + movieDetailsResponse.getMovieHomepage();
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_TEXT, movieInfoToShare);
//            intent.setType("text/plain");
//            startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_to)));
//        }
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
//        Intent intent = new Intent(this, WebViewActivity.class);
//            intent.putExtra(ApiConfig.JsonKey.HOMEPAGE, urlHomepage);
//            intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, movieDetailsResponse.getMovieOriginalTitle());
//        startActivity(intent);
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

            // TODO: Call get data again

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
