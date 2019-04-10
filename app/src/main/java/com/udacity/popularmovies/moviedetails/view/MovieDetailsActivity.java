package com.udacity.popularmovies.moviedetails.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import com.udacity.popularmovies.moviedetails.models.Reviews;
import com.udacity.popularmovies.moviedetails.models.Video;
import com.udacity.popularmovies.moviedetails.models.Videos;
import com.udacity.popularmovies.moviesfeed.models.Movie;
import com.udacity.popularmovies.service.MovieDataService;
import com.udacity.popularmovies.service.RetrofitInstance;
import com.udacity.popularmovies.shared.DateUtils;
import com.udacity.popularmovies.shared.LanguageUtils;
import com.udacity.popularmovies.shared.NetworkUtils;
import com.udacity.popularmovies.shared.WebViewActivity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private MovieDetailsResponse movieDetailsResponse;
    private int movieId;
    private String movieHomepageUrl;
    private String movieOriginalTitle = "";

    private final ArrayList<ProductionCompany> productionCompaniesList = new ArrayList<>();
    private CompanyListAdapter companyListAdapter;

    private final ArrayList<Video> movieVideosList = new ArrayList<>();
    private MovieVideosAdapter movieVideosAdapter;

    private final ArrayList<Review> movieReviewsList = new ArrayList<>();
    private MovieReviewsAdapter movieReviewsAdapter;

    private Toast toast;
    private boolean isBookmarkOnDatabase;
    private boolean isMovieWatched;

    private int deviceWidth;
    private ActivityMovieDetailsBinding activityMovieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Log.d(LOG_TAG,"===>>> onCreate called");

        activityMovieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        // Get the movie ID and Title from the clicked item on the RecyclerView item.
        getIncomingIntent();
        progressBarStatus(SHOW);
        setupToolbar();
        initViews();
        getMovieDetails();

        // Button inside Overview card that send user to the movie homepage in the browser if URL is not null
        activityMovieDetailsBinding.btHomePage.setOnClickListener(new View.OnClickListener() {
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

        activityMovieDetailsBinding.floatIsWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //                if (!isBookmarkOnDatabase && !isMovieWatched) {
//                    new SaveBookmarkAsyncTask().execute();
//                }
//                new UpdateBookmarkAsyncTask().execute(movieDetailsResponse.getId());
            }
        });

        activityMovieDetailsBinding.floatSaveBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //                if (isBookmarkOnDatabase) {
//                    // if the Movie is already bookmarked, remove it from the database and update icon status to unsaved icon
//                    new DeleteBookmarkAsyncTask().execute(movieDetailsResponse.getId());
//                } else {
//                    new SaveBookmarkAsyncTask().execute();
//                }
            }
        });


        // FloatButton to share movie homepage to other app available on device.
        activityMovieDetailsBinding.floatShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWebUrl();
            }
        });

        LanguageUtils.checkSystemLanguage(loadApiLanguage);
        // Hide the Tagline TextView due to API to provide tagline to english language only or the
        // movie occasionally may have a empty tagline value. Then the space will be shown.
        if (loadApiLanguage.equals("")) activityMovieDetailsBinding.tvTagline.setVisibility(View.GONE);
        else activityMovieDetailsBinding.tvTagline.setVisibility(View.VISIBLE);


        // RecyclerView for the list of companies
        companyListAdapter = new CompanyListAdapter(this, productionCompaniesList);
        activityMovieDetailsBinding.rvCompanies.setAdapter(companyListAdapter);
        activityMovieDetailsBinding.rvCompanies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        activityMovieDetailsBinding.rvCompanies.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(activityMovieDetailsBinding.rvCompanies, false);

        // RecyclerView for the list of movie videos
        movieVideosAdapter = new MovieVideosAdapter(this, movieVideosList);
        activityMovieDetailsBinding.rvMoviesVideosDetails.setAdapter(movieVideosAdapter);
        activityMovieDetailsBinding.rvMoviesVideosDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        activityMovieDetailsBinding.rvMoviesVideosDetails.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // Author: https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(activityMovieDetailsBinding.rvMoviesVideosDetails, false);

        // RecyclerView references and setups
        movieReviewsAdapter = new MovieReviewsAdapter(this, movieReviewsList);
        activityMovieDetailsBinding.rvMoviesReviewsDetails.setAdapter(movieReviewsAdapter);
        activityMovieDetailsBinding.rvMoviesReviewsDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        activityMovieDetailsBinding.rvMoviesReviewsDetails.setHasFixedSize(true);
        // The method ViewCompat.setNestedScrollingEnabled allows the recycler view scroll smoothly.
        // Author: https://medium.com/@mdmasudparvez/where-to-put-this-line-viewcompat-setnestedscrollingenabled-recyclerview-false-b87ff2c7847e
        ViewCompat.setNestedScrollingEnabled(activityMovieDetailsBinding.rvMoviesReviewsDetails, false);
    } // Close onCreate

    private void setupToolbar() {
        // Setup ToolBar
        setSupportActionBar(activityMovieDetailsBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Setup CollapsingToolbar with movie name
        activityMovieDetailsBinding.collapsingToolbar.setTitle(movieOriginalTitle);
    }


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


    private void getMovieDetails() {

        MovieDataService movieDataService = RetrofitInstance.getService();

        Call<MovieDetailsResponse> call = movieDataService.getMovieDetails(movieId, loadApiLanguage, BuildConfig.API_KEY);
        call.enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                if (response.body() != null) {

                    movieDetailsResponse = response.body();

                    if (movieDetailsResponse != null) {
                            progressBarStatus(HIDE);
                            updateUI(movieDetailsResponse);
                        } else {
                            progressBarStatus(HIDE);
//                            warningDetails(SHOW);
                        }
                    }

                }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
            }
        });

    }


    public class MovieDetailsActivityHandlers {

        Context context;

        public MovieDetailsActivityHandlers(Context context) {
            this.context = context;
        }

        private void openWebPage(String urlHomepage) {
        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
            intent.putExtra(ApiConfig.JsonKey.HOMEPAGE, urlHomepage);
            intent.putExtra(ApiConfig.JsonKey.ORIGINAL_TITLE, movieDetailsResponse.getOriginalTitle());
        startActivity(intent);
        }

    }

    private void initViews() {

        // TODO: Review the use of this method

    }

    /* === UPDATE UI === */

    /**
     * When called receives the movieDetailsResponse object with movieDetailsResponse details and updates the UI.
     *
     * @param movieDetailsResponse is the {@link Movie} object with movieDetailsResponse details.
     */
    private void updateUI(MovieDetailsResponse movieDetailsResponse) {

        Picasso.get()
                .load(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W500 + movieDetailsResponse.getPosterPath())
                .placeholder(R.drawable.poster_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.poster_image_place_holder)
                .into(activityMovieDetailsBinding.ivMoviePoster);

        Picasso.get()
                .load(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W500 + movieDetailsResponse.getBackdropPath())
                .placeholder(R.drawable.backdrop_image_place_holder)
                .fit().centerInside()
                .error(R.drawable.backdrop_image_place_holder)
                .into(activityMovieDetailsBinding.ivMovieBackdrop);

        activityMovieDetailsBinding.tvOverview.setText(movieDetailsResponse.getOverview());
        activityMovieDetailsBinding.tvVoteAverage.setText(String.valueOf(movieDetailsResponse.getVoteAverage()));

        activityMovieDetailsBinding.tvRuntime.setText(String.valueOf(movieDetailsResponse.getRuntime() + getString(R.string.min)));

        String formatedDate = DateUtils.simpleDateFormat(movieDetailsResponse.getReleaseDate());
        activityMovieDetailsBinding.tvReleaseDate.setText(formatedDate);

        activityMovieDetailsBinding.tvTitle.setText(movieDetailsResponse.getTitle());
        activityMovieDetailsBinding.tvOriginalLanguage.setText(movieDetailsResponse.getOriginalLanguage());

        String taglineWithQuotes = movieDetailsResponse.getTagline();
        activityMovieDetailsBinding.tvTagline.setText(taglineWithQuotes);

        activityMovieDetailsBinding.tvPopularity.setText(String.valueOf(movieDetailsResponse.getPopularity()));
        activityMovieDetailsBinding.tvVoteCount.setText(String.valueOf(movieDetailsResponse.getVoteCount()));

        if (movieDetailsResponse.getBudget() == 0) activityMovieDetailsBinding.tvBuget.setText(R.string.unavailable);
        else activityMovieDetailsBinding.tvBuget.setText(formatNumber(movieDetailsResponse.getBudget()));

        if (movieDetailsResponse.getRevenue() == 0) activityMovieDetailsBinding.tvRevenue.setText(R.string.unavailable);
        else activityMovieDetailsBinding.tvRevenue.setText(formatNumber(movieDetailsResponse.getRevenue()));

        for (Genre genre : movieDetailsResponse.getGenres()) {
            activityMovieDetailsBinding.tvGenres.append(genre.getName() + " ");
        }

        movieVideosList.clear();
        movieVideosList.addAll(movieDetailsResponse.getVideos().getVideos());
        movieVideosAdapter.notifyDataSetChanged();

        movieReviewsList.clear();
        movieReviewsList.addAll(movieDetailsResponse.getReviews().getReviews());
        movieReviewsAdapter.notifyDataSetChanged();

        productionCompaniesList.clear();
        productionCompaniesList.addAll(movieDetailsResponse.getProductionCompanies());
        companyListAdapter.notifyDataSetChanged();


        // TODO: Just logging \o/
//        for (Review review : reviewsList) {
//            Log.d(LOG_TAG, ">>>> Review Author and Content:  " + review.getAuthor() + " : " + review.getContent());
//        }
//        for (ProductionCompany productionCompany : productionCompanyList) {
//            Log.d(LOG_TAG, ">>>> Prodution Company Name:  " + productionCompany.getName());
//        }

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
                activityMovieDetailsBinding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_saved);
                Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.movie_saved), Toast.LENGTH_SHORT).show();
                // isBookmarkOnDatabase = true;
            } else {
                doToast(getResources().getString(R.string.warning_could_not_be_saved));
                activityMovieDetailsBinding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
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
                activityMovieDetailsBinding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
                activityMovieDetailsBinding.floatIsWatched.setImageResource(R.drawable.ic_watched_not);
                activityMovieDetailsBinding.tvLabelWatched.setVisibility(HIDE);
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

//            isBookmarkOnDatabase = cursor.getCount() > 0;
//            if (isBookmarkOnDatabase) {
//                activityMovieDetailsBinding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_saved);
//            } else {
//                activityMovieDetailsBinding.floatSaveBookmark.setImageResource(R.drawable.ic_bookmark_unsaved);
//            }
//
//            if (cursor.moveToFirst()) {
//                int WATCHED = cursor.getInt(cursor.getColumnIndex(BookmarkEntry.COLUMN_IS_WATCHED));
//                if (WATCHED == 0) {
//                    isMovieWatched = false;
//                    activityMovieDetailsBinding.floatIsWatched.setImageResource(R.drawable.ic_watched_not);
//                    animateTextFadeOut(activityMovieDetailsBinding.tvLabelWatched);
//                }if (WATCHED == 1){
//                    isMovieWatched = true;
//                    activityMovieDetailsBinding.floatIsWatched.setImageResource(R.drawable.ic_watched_yes);
//                    animateTextFadeIn(activityMovieDetailsBinding.tvLabelWatched);
//                }
//            }
//            progressBarStatus(INVISIBLE);
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
                    activityMovieDetailsBinding.floatIsWatched.setImageResource(R.drawable.ic_watched_not);
                    doToast(getResources().getString(R.string.not_watched));
                    animateTextFadeOut(activityMovieDetailsBinding.tvLabelWatched);
                    isMovieWatched = false;
                }else {
                    activityMovieDetailsBinding.floatIsWatched.setImageResource(R.drawable.ic_watched_yes);
                    Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.watched), Toast.LENGTH_SHORT).show();
                    animateTextFadeIn(activityMovieDetailsBinding.tvLabelWatched);
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
        activityMovieDetailsBinding.tvWarningNoData.setVisibility(VISIBILITY);
        activityMovieDetailsBinding.ivWarningNoData.setVisibility(VISIBILITY);
        if (VISIBILITY == SHOW) {
//            setContentVisibility(HIDE);
            doToast(getString(R.string.warning_you_are_not_connected));
        } else {
//            setContentVisibility(SHOW);
        }
    }

    private void setContentVisibility(int VISIBILITY) {
        activityMovieDetailsBinding.floatSaveBookmark.setVisibility(VISIBILITY);
        activityMovieDetailsBinding.floatShareButton.setVisibility(VISIBILITY);
        activityMovieDetailsBinding.ivMoviePoster.setVisibility(VISIBILITY);
        activityMovieDetailsBinding.sectionFullContent.setVisibility(VISIBILITY);
    }

    private void warningDetails(int VISIBILITY) {
        activityMovieDetailsBinding.tvWarningNoData.setText(getResources().getText(R.string.warning_no_details));
        activityMovieDetailsBinding.tvWarningNoData.setVisibility(VISIBILITY);
        activityMovieDetailsBinding.ivWarningNoData.setImageResource(R.drawable.ic_details);
        activityMovieDetailsBinding.ivWarningNoData.setVisibility(VISIBILITY);

    }

    private void progressBarStatus(int VISIBILITY) {
        if (VISIBILITY == SHOW) activityMovieDetailsBinding.indeterminateBar.setIndeterminate(true);
        else activityMovieDetailsBinding.indeterminateBar.setIndeterminate(false);
        activityMovieDetailsBinding.indeterminateBar.setVisibility(VISIBILITY);
    }


    /* === Lifecycle methods === */

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("===>>> onStart", " called");
        if (NetworkUtils.isDeviceConnected(this)) {
//            new CheckBookmarkAsyncTask().execute();
//            warningConnection(HIDE);
        } else {
//            warningConnection(SHOW);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("===>>> onPause", " called");
        if (NetworkUtils.isDeviceConnected(this)) {
//            warningConnection(HIDE);
        } else {
//            warningConnection(SHOW);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("===>>> onResume", " called");
        // Check internet connection when activity is resumed.
        if (NetworkUtils.isDeviceConnected(this)) {
//            warningConnection(HIDE);

            // TODO: Call get data again

        } else {
//            warningConnection(SHOW);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("===>>> onRestart", " called");
    }
}
