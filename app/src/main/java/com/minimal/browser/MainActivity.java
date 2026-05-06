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

public class MainActivity extends Activity {

    private WebView webView;
    private EditText urlBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlBar = findViewById(R.id.urlBar);
        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        // Cache sirf RAM mein rahega, storage mein nahi jayega
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); 

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

    // Yeh method App ka 100% data udayega (Cache + Cookies + Phantom Storage)
    private void nukeAllData() {
        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            webView.clearSslPreferences();
            
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            WebStorage.getInstance().deleteAllData();

            deletePhantomStorage();
        }
    }

    // Android System WebView ke hidden folders ko forcefully delete karne ka logic
    private void deletePhantomStorage() {
        try {
            // Normal cache delete
            deleteDir(getCacheDir());
            
            // WebView ka alag se banne wala chhipa hua data folder delete
            java.io.File dataDir = new java.io.File(getApplicationInfo().dataDir);
            java.io.File webviewDir = new java.io.File(dataDir, "app_webview");
            if (webviewDir.exists()) {
                deleteDir(webviewDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new java.io.File(dir, child));
                    if (!success) { return false; }
                }
            }
        }
        return dir != null && dir.delete();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            nukeAllData();
            if (webView != null) {
                webView.destroy();
            }
            super.onBackPressed();
        }
    }

    // Jab app Recents se hataya jayega ya completely band hoga
    @Override
    protected void onDestroy() {
        nukeAllData();
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
                }
