package ando.repo.ui.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ando.repo.R;
import ando.webview.core.WebViewUtils;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static ando.repo.ui.webview.WebDataKt.htmlStr;

//https://www.jianshu.com/p/9b84f75b3b93
public class WebViewCoordinatorActivity extends AppCompatActivity {

    private String mHtmlStr = htmlStr;
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_coordinator);

        mWebView = findViewById(R.id.webView);

        WebViewUtils.initWebView(this, mWebView);
        mWebView.setWebChromeClient(new WebViewVideoActivity.MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        //mHtmlStr  originStr
        mHtmlStr = mHtmlStr.replace("\\", "");//也就是 mHtmlStr

        //https://player.alicdn.com
        mWebView.loadDataWithBaseURL("", mHtmlStr, "text/html", "utf-8", null);
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("123", "onPageStarted.............." + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("123", "onPageFinished.............." + url);
            //mWebView.loadDataWithBaseURL(null, mHtmlStr, "text/html", "utf-8", null);

            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("123", "shouldOverrideUrlLoading.............." + request.getUrl());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            super.onReceivedClientCertRequest(view, request);
            Log.i("123", "onReceivedClientCertRequest request " + request.toString());
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            Log.e("123", "onReceivedHttpAuthRequest.............." + handler + "   " + host + "   " + realm);
        }
    }

}