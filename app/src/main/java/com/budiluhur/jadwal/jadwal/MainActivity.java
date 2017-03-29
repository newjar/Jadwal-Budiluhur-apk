package com.budiluhur.jadwal.jadwal;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ProgressBar progressBar;
    private WebView webView;
    private SwipeRefreshLayout swipe;
    private String currentURL = "http://jadwal.budiluhur.ac.id/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webView);
        swipe   = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);

        if(savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            webView.setWebViewClient(new OurViewClient());

            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    progressBar.setProgress(progress);
                    if(progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    }
                    if (progress == 100) {
                        progressBar.setVisibility((ProgressBar.GONE));
                    }
                }
            });

            String data = getIntent().getDataString();

            if(Intent.ACTION_VIEW.equals(getIntent())) {
                webView.loadUrl(data);
            } else {
                webView.loadUrl("http://jadwal.budiluhur.ac.id/");
            }
        }
    }

    public class OurViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView v, String url) {
            if (url.contains("jadwal.budiluhur.ac.id")) {
                v.loadUrl(url);
                CookieManager.getInstance().setAcceptCookie(true);
            } else {
                Uri uri = Uri.parse(url);
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Choose browser"));
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            swipe.setRefreshing(false);
            super.onPageFinished(view, url);
        }

    }

    @Override
    public void onRefresh() {
        swipe.setRefreshing(true);
        ReLoadWebView(currentURL);
    }
    private void ReLoadWebView(String currentURL) {
        webView.loadUrl(currentURL);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
