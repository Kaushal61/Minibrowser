package com.minimal.browser;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();

        // Login aur session data save rahega
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Desktop mode — WhatsApp Web ke liye zaroori
        settings.setUserAgentString(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                    WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        // Seedha WhatsApp Web khulega
        webView.loadUrl("https://web.whatsapp.com");
    }

    // Sirf cache clear — close hone par — login safe rahega
    private void clearCacheOnly() {
        if (webView != null) {
            webView.clearCache(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearCacheOnly(); // Recents se swipe karne par bhi cache clean
    }

    @Override
    protected void onDestroy() {
        clearCacheOnly();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
                                 }
