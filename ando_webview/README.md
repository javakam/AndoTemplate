> **Ando 项目汇总**👉<https://juejin.cn/post/6934981195583356965/>

## AndoWebview

- 🚀 修复了原生`WebView`的一些`Bug`
- 🚀 `NestedScrollWebView` + 顶部进度显示`Indicator`
- 🚀 `WebViewUtils`, `WebView`通用配置
- 🚀 `NestedScrollWebView` ,修复了`Android 5.0/5.1`打开`WebView`闪退问题; 修复了嵌套在`NestedScrollView`中高度异常问题;
- 🚀 修复了`Android WebView Bug : Resources$NotFoundException:String resource ID #0x2040003`, 详细介绍👉 <https://juejin.im/post/6844904179350110216>

### Gradle
```groovy
implementation 'com.github.javakam:webview:3.0.0@aar'
```

### Usage

1. 布局文件中引入(建议动态创建`NestedScrollWebView`) :

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <ando.webview.indicator.WebIndicator
        android:id="@+id/webViewIndicator"
        android:layout_width="match_parent"
        android:layout_height="3dp" />

    <ando.webview.core.NestedScrollWebView
        android:id="@+id/webView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```    
    
2. Activity/Fragment

简单方式:
```kotlin
fun letsGoSimplify() {
    WebViewUtils.initWebView(this, mWebView, mWebViewIndicator)
    mWebView.loadUrl(url)
}
```
自定义:
```kotlin
private fun letsGoCustom() {
    WebViewUtils.initWebView(this, mWebView)
    mWebView.webViewClient = CustomWebClient(this)
    val controller: WebIndicatorController = WebIndicatorController().inject(mWebViewIndicator)
    mWebView.webChromeClient = CustomWebChromeClient(this, controller)
    mWebView.loadUrl(url)
    //mWebView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null)
}
```
🍎 此外, 还要注意返回事件和防止泄露 :
```kotlin
//返回键处理
override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    return WebViewUtils.performKeyDown(this, mWebView, keyCode, event)
}

//及时销毁防止泄露
override fun onDestroy() {
    super.onDestroy()
    mWebView.clearHistory()
    mWebView.removeAllViews()
}
```

### `Url` for testing
```
https://fhy.bgy.com.cn/fhyH5/

https://3gimg.qq.com/lightmap/v1/marker/?marker=coord:40.4419729170,115.9685142526;title:%E5%BB%B6%E5%BA%86%E7%A2%A7%E6%A1%82%E5%9B%AD%E4%BA%AC%E6%BA%90%E8%91%97;addr:%E5%8C%97%E4%BA%AC%E5%B8%82%E5%BB%B6%E5%BA%86%E5%8C%BA%E4%B8%96%E5%9B%AD%E4%BC%9A%E6%99%AF%E5%8C%BA%E4%B8%9C%E4%BE%A7;&referer=myapp&key=GILBZ-WQCLF-DDXJQ-NK66Q-G3H7S-4GBR5

qqmap://map/nearby?coord=40.4419729170,115.9685142526&placeName=%E5%BB%B6%E5%BA%86%E7%A2%A7%E6%A1%82%E5%9B%AD%E4%BA%AC%E6%BA%90%E8%91%97

https://map.qq.com/m/nearby/search/centerX=115.9685142526&centerY=40.4419729170&placename=%E5%BB%B6%E5%BA%86%E7%A2%A7%E6%A1%82%E5%9B%AD%E4%BA%AC%E6%BA%90%E8%91%97&refer=comMarker&key=NRFBZ-673AV-HUTPI-UCYIF-4LV4S-CLBJA 
```

### Thanks
<https://github.com/Justson/AgentWeb>
