package com.example.android.dyfragmentdata;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onesignal.OneSignal;

public class MerchantLoginActivity extends AppCompatActivity {

    private Session session;
    private String sessionToken;
    private String android_id;
    private WebView myWebView;
    private String authToken;
    private String ramdomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_merchant);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        session = new Session(this);
        sessionToken = session.getusertoken();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("https://www.godprice.com/merchant/");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //  if (Uri.parse(url).getHost().equals("http://hqsales.net/mobileappdev/makeadminlogin/")) {
            if (Uri.parse(url).getHost().equals("www.godprice.com")) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}


