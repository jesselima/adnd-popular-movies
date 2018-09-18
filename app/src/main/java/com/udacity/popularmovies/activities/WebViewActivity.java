package com.udacity.popularmovies.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    String urlHomepage;
    ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getIncomingIntent();

        progressBar =findViewById(R.id.loading_page_indicator);
        webView = findViewById(R.id.webview);
        Toolbar toolbar = findViewById(R.id.toolbar_web_view);

        int loadingProgress = webView.getProgress();
        String title = webView.getTitle();
        toolbar.setTitle(title);

        progressBar.setProgress(loadingProgress);
        progressBar.setVisibility(View.GONE);

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
        webView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        webView.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page

        // Configure the client to use when opening URLs
        webView.setWebViewClient(new WebViewClient());
        // Load the initial URL
        webView.loadUrl(urlHomepage);
        Log.d("===>>> urlHomepage", urlHomepage);



    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(ApiConfig.JsonKey.HOMEPAGE)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                urlHomepage = bundle.getString(ApiConfig.JsonKey.HOMEPAGE);
            }
        }
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

}
