package com.bjtcsj.jyt.wrapper;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    private View mWelcomeView;
    private WebView mBrowserView;
    private Boolean mExitOnNextBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mWelcomeView = findViewById(R.id.welcome_view);
        mBrowserView = (WebView) findViewById(R.id.browser_view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        hideSystemControls();
        loadTargetWebsite();
    }

    @Override
    public void onBackPressed() {
        if (mBrowserView.canGoBack()) {
            mBrowserView.goBack();
        } else if (mExitOnNextBackPressed) {
            super.onBackPressed();
        } else {
            mExitOnNextBackPressed = true;

            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExitOnNextBackPressed = false;
                }
            }, 2000);
        }
    }

    protected void hideSystemControls() {
        // Hide UI
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Remove the status and navigation bar
        mBrowserView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    protected void loadTargetWebsite() {
        WebSettings webSettings = mBrowserView.getSettings();
//        webSettings.setAllowContentAccess(true);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setSaveFormData(true);
//        webSettings.setUseWideViewPort(true);

        mBrowserView.loadUrl("http://m.jyt.bjtcsj.com");
        mBrowserView.setWebViewClient(new WrapperClient());
    }

    protected void fadeOutWelcomeView() {
        if (mWelcomeView.getVisibility() != View.VISIBLE) return;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWelcomeView.animate().alpha(0).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mWelcomeView.setVisibility(View.GONE);
                    }
                });
            }
        }, 300);
    }

    private class WrapperClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            fadeOutWelcomeView();
        }
    }
}
