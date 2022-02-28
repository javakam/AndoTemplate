package ando.repo.ui.webview

import ando.repo.R
import ando.webview.core.CustomWebChromeClient
import ando.webview.core.CustomWebClient
import ando.webview.core.NestedScrollWebView
import ando.webview.core.WebViewUtils
import ando.webview.indicator.WebIndicator
import ando.webview.indicator.WebIndicatorController
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView

/**
 * # WebViewScrollViewActivity
 *
 * @author javakam
 * 2021/3/3  15:47
 */
class WebViewScrollViewActivity : AppCompatActivity() {

    private lateinit var mScrollView: NestedScrollView
    private lateinit var mWebViewContainer: FrameLayout
    private lateinit var mWebViewIndicator: WebIndicator
    private lateinit var mWebView: NestedScrollWebView
    private val url = adHtml2// adHtml

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_scrollview)

        mWebViewContainer = findViewById(R.id.container)
        mScrollView = findViewById(R.id.scrollView)

        //Indicator
        mWebViewIndicator = findViewById(R.id.webViewIndicator)
        mWebViewIndicator.setColor(ContextCompat.getColor(this, R.color.color_web_indicator))

        //WebView
        mWebView = NestedScrollWebView(this)
        //mWebView.overScrollMode = View.OVER_SCROLL_NEVER
        mWebView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mWebViewContainer.addView(mWebView)

        //解决滑动冲突 Fix Scroll Conflict
        //注意: FrameLayout不能加上属性 minHeight 并且 layout_height 必须为 match_parent
        mScrollView.isFillViewport = true
        mWebView.isNestedScrollingEnabled = true

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
        mWebViewContainer.removeView(mWebView)
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