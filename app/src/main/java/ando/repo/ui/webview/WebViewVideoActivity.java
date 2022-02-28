package ando.repo.ui.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import ando.repo.R;
import ando.webview.core.NestedScrollWebView;
import ando.webview.core.WebViewUtils;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static ando.repo.ui.webview.WebDataKt.videoStr;

public class WebViewVideoActivity extends AppCompatActivity {
    private FrameLayout mWebViewContainer;
    private WebView mWebView;

    private String originStr = videoStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_video);

        mWebViewContainer = findViewById(R.id.webViewContainer);
        mWebView = new NestedScrollWebView(this);
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        WebViewUtils.initWebView(this, mWebView);
        //settings(mWebView);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        mWebViewContainer.addView(mWebView);

        //正常1
//        mWebView.loadUrl(htmlPath, null);

        //也可以2
//        mWebView.loadDataWithBaseURL(urlScript, htmlStr, "text/html", "utf-8", null);

        /*
         错误信息:
         onConsoleMessage.....Uncaught SecurityError: Failed to read the 'cookie' property from 'Document': Cookies are disabled inside 'data:' URLs.

         StackOverflow :
         https://stackoverflow.com/questions/28371812/java-webview-failed-to-read-the-cookie-property-from-document-access-is-d
         https://stackoverflow.com/questions/36174570/javascript-test-selenium-cookies-data-url

         产生原因:
         在页面完全加载之前，不能使用Chrome驱动来设置cookie。等待页面加载，然后设置cookie。
         */
        //mWebView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);
        //还可以3  http://vd.zqrb.cn ,http://www.zqrb.cn, http://www.baidu.com http://www.zqrb.cn 都行
        //甚至关闭的网站都行... 👉 http://appcms.zqrb.cn
//        mWebView.loadDataWithBaseURL("http://vd.zqrb.cn", htmlStr, "text/html", "utf-8", null);

        //以上都不是好的解决方式:
        //!!!!!!!!!!!!!推荐方案!!!!!!!!!!!!!!!!!
//        if (CookieManager.getInstance().hasCookies()) {
//            CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
//                @Override
//                public void onReceiveValue(Boolean value) {
//                    Log.w("123", "removeAllCookies  onReceiveValue = " + value);
//                }
//            });
//        }

//        CookieManager.setAcceptFileSchemeCookies(true);
//        CookieManager.allowFileSchemeCookies();
//        CookieManager.getInstance().setAcceptCookie(true);
//        CookieManager.getInstance().acceptCookie();
//        CookieManager.getInstance().setAcceptCookie(true);

        //CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);// 跨域cookie读取

        //htmlStr  originStr
        originStr = originStr.replace("\\", "");//也就是 htmlStr

        mWebView.loadDataWithBaseURL("https://player.alicdn.com", originStr, "text/html", "utf-8", null);
        //mWebView.loadDataWithBaseURL("https://player.alicdn.com", DataProvider.testCrash(), "text/html", "utf-8", null);
//        mWebView.loadUrl(originStr);


        //行不通...
        /* String url = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            url = Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY).toString();// for 24 api and more
        } else {
            url = Html.fromHtml(htmlStr).toString();// or for older api
        }
        mWebView.loadUrl(url, null); */

    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("123", "onPageStarted.............." + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("123", "onPageFinished.............." + url);
            //view.loadUrl(originStr);

            //mWebView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);
            WebViewUtils.autoFitImage(view);

//          view.loadUrl("javascript:callAndroid(" + "\"" + originStr + "\"" + ")", null);
//          view.evaluateJavascript("javascript:callAndroid(" + "\"" + originStr + "\"" + ")", new ValueCallback<String>() {
//              @Override
//              public void onReceiveValue(String value) {
//                  //此处为 js 返回的结果
//                  Log.i("123", "evaluateJavascript result " + value);
//              }
//          });

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

    public static class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

//        @Override
//        public void onRequestFocus(WebView view) {
//            super.onRequestFocus(view);
//            Log.e("123", "onRequestFocus..............................");
//        }

        /**
         * 点击全屏的时候执行
         */
        @Override
        public void onShowCustomView(View view, final CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            Log.e("123", "onShowCustomView..............................");
        }

        /**
         * 退出全屏的时候执行
         */
        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }

        @Override
        public View getVideoLoadingProgressView() {
            Log.e("123", "getVideoLoadingProgressView..............................");
            return super.getVideoLoadingProgressView();
        }

//        @Override
//        public void getVisitedHistory(ValueCallback<String[]> callback) {
//            super.getVisitedHistory(callback);
//            Log.e("123", "getVisitedHistory.............................."+callback.toString());
//        }

        /**
         * 给视频返回一张默认图片作为封面
         */
        @Override
        public Bitmap getDefaultVideoPoster() {
            Log.e("123", "getDefaultVideoPoster..............................");
            // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demo);
            return null;
        }

        /**
         * 打印浏览器输出控制台输出内容，一般可以监听到视频是否开始播放，和播放是否成功信息
         */
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.e("123", "onConsoleMessage.............................." + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebViewContainer.removeAllViews();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }

}