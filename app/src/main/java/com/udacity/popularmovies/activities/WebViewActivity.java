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

        getIncomingIntent();
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

    private void getIncomingIntent() {
        if (getIntent().hasExtra(ApiConfig.JsonKey.HOMEPAGE)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                urlHomepage = bundle.getString(ApiConfig.JsonKey.HOMEPAGE);
                originalTitle = bundle.getString(ApiConfig.JsonKey.ORIGINAL_TITLE);
            }
        }
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_web_view);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(originalTitle);
        toolbar.setSubtitle(R.string.homepage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view, menu);
        return true;
    }

    /**
     * This method handles the clicked item menu
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

    private void openWebPage(String url) {
        Uri uriWebPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriWebPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    class LoadHomepageWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadingIndicator.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loadingIndicator.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }

}
