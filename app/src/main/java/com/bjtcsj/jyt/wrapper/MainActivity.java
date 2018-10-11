package com.bjtcsj.jyt.wrapper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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

        mBrowserView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        WebSettings webSettings = mBrowserView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);

        mBrowserView.setWebViewClient(new WrapperClient());
        mBrowserView.loadUrl("http://m.jyt.bjtcsj.com");
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

    public void hideWelcome() {
        mWelcomeView.animate().alpha(0).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                mWelcomeView.setVisibility(View.GONE);
            }
        });
    }

    public void showWarning() {
        Toast.makeText(MainActivity.this, "网络出错，请检查", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3500);
    }

    private class WrapperClient extends WebViewClient {
        private String mStatus = "created";
        private Handler mHandler = new Handler();
        private final Runnable mShowWarning2Runnable = new Runnable() {
            @Override
            public void run() {
                showWarning();
            }
        };
        private final Runnable mHideWelcome2Runnable = new Runnable() {
            @Override
            public void run() {
                hideWelcome();
            }
        };

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mHandler.postDelayed(mShowWarning2Runnable, 10000);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mStatus = "error";
            showWarning();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            mStatus = "error";
            showWarning();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mStatus.equals("created")) {
                mHandler.postDelayed(mHideWelcome2Runnable,300);
            }

            mHandler.removeCallbacks(mShowWarning2Runnable);
        }
    }
}
