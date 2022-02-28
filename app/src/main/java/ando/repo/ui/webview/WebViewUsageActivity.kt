package ando.repo.ui.webview

import ando.repo.R
import ando.webview.core.CustomWebChromeClient
import ando.webview.core.CustomWebClient
import ando.webview.core.WebViewUtils
import ando.webview.indicator.WebIndicator
import ando.webview.indicator.WebIndicatorController
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * # WebViewUsageActivity
 *
 * @author javakam
 * 2021/3/3  15:47
 */
class WebViewUsageActivity : AppCompatActivity() {

    private lateinit var mWebViewIndicator: WebIndicator
    private lateinit var mWebView: WebView
    private val url = adHtml2// adHtml

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_usage)
        mWebView = findViewById(R.id.webView)
        mWebViewIndicator = findViewById(R.id.webViewIndicator)
        mWebViewIndicator.setColor(ContextCompat.getColor(this, R.color.color_web_indicator))

        //1.简单使用
        //letsGoSimplify()
        //2.自定义
        letsGoCustom()
    }

    //返回键处理
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return WebViewUtils.performKeyDown(this, mWebView, keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView.clearHistory()
        mWebView.removeAllViews()
    }

    private fun letsGoSimplify() {
        WebViewUtils.initWebView(this, mWebView, mWebViewIndicator)
        mWebView.loadUrl(url)
    }

    private fun letsGoCustom() {
        WebViewUtils.initWebView(this, mWebView)
        mWebView.webViewClient = CustomWebClient(this)

        val controller: WebIndicatorController = WebIndicatorController().inject(mWebViewIndicator)
        mWebView.webChromeClient = CustomWebChromeClient(this, controller)

        mWebView.loadUrl(url)
        //mWebView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null)
    }
}