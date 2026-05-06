package com.minimal.browser;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends Activity {

    private WebView webView;
    private EditText urlBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ APP KHOLTE HI SAB KUCH NUKE — RELIABLE HAI
        nukePreviousSession();

        setContentView(R.layout.activity_main);

        urlBar = findViewById(R.id.urlBar);
        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // ❌ DOM Storage band — login tokens save nahi honge
        settings.setDomStorageEnabled(false);

        // ❌ Disk cache bilkul nahi
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // ❌ Form data save nahi hoga
        settings.setSaveFormData(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        urlBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String url = urlBar.getText().toString().trim();
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://" + url;
                    }
                    webView.loadUrl(url);
                    return true;
                }
                return false;
            }
        });
    }

    private void nukePreviousSession() {
        // Cookies nuke
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // WebStorage nuke
        WebStorage.getInstance().deleteAllData();

        // Cache folder manually delete
        deleteDir(getCacheDir());
        deleteDir(new File(getApplicationInfo().dataDir, "app_webview"));
    }

    private void deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File child : dir.listFiles() != null ? dir.listFiles() : new File[0]) {
                deleteDir(child);
            }
        }
        if (dir != null) dir.delete();
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
