package com.udacity.popularmovies.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    private String urlHomepage;
    private String originalTitle;
    private ProgressBar loadingIndicator;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        // Get the Movie Homepage url and title from the MovieDetailsActivity
        getIncomingIntent();
        // Setup the toolbar with title and subtitle
        setToolbar();

        loadingIndicator = findViewById(R.id.loading_page_indicator);
        loadingIndicator.setIndeterminate(true);

        webView = findViewById(R.id.webview);

        // Configure related browser settings
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // enable the page to load with responsive layouts
        webView.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the viewport
        webView.getSettings().setLoadWithOverviewMode(true);
        // enable the ability to zoom-in controls on the page
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true); // allow pinch to zoom
        webView.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page
        // Configure the client to use when opening URLs
        webView.setWebViewClient(new LoadHomepageWebClient());
        // Load the initial URL
        webView.loadUrl(urlHomepage);

    }

    /**
     * Get the Intent Object sent from the MovieDetailsActivity
     */
    private void getIncomingIntent() {
        if (getIntent().hasExtra(ApiConfig.JsonKey.HOMEPAGE)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                urlHomepage = bundle.getString(ApiConfig.JsonKey.HOMEPAGE);
                originalTitle = bundle.getString(ApiConfig.JsonKey.ORIGINAL_TITLE);
            }
        }
    }

    /**
     * Setup the toolbar with movie title and subtitle "homepage"
     */
    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_web_view);
        toolbar.setTitle(originalTitle);
        toolbar.setSubtitle(R.string.homepage);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view, menu);
        return true;
    }

    /**
     * This method handles the clicked item menu.
     * When clicked starts a Intent that will get the current movie homepage url look for the
     * default browser on device to open this url.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_in_the_browser:
                openWebPage(urlHomepage);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When called must receive a String url and will open default browser on device or ask to the
     * user about what application he wants to uses to open the URL.
     *
     * @param url is the url to be open in the browser.
     */
    private void openWebPage(String url) {
        Uri uriWebPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriWebPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * This class is used to control tasks when the url starts to load and when it finished.
     *
     */
    class LoadHomepageWebClient extends WebViewClient {
        // Whe page loading starts the loading indicator is visible and the WebView hidden.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadingIndicator.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }

        // Once the page finishes to load tha loading indicator is hidden and tha WebView is shown.
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loadingIndicator.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}
