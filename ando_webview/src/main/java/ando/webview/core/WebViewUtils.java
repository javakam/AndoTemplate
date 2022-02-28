package ando.webview.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import ando.webview.indicator.WebIndicator;
import ando.webview.indicator.WebIndicatorController;
import androidx.fragment.app.Fragment;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY;

/**
 * # WebView Config
 *
 * @author javakam
 * 2019-05-20 16:48:25
 */
public class WebViewUtils {

    public static void initWebView(Activity activity, WebView webview) {
        initWebView(activity, webview, null);
    }

    public static void initWebView(Fragment fragment, WebView webview) {
        initWebView(fragment.getActivity(), webview, null);
    }

    public static void initWebView(Fragment fragment, WebView webView, WebIndicator indicator) {
        initWebView(fragment.getActivity(), webView, indicator);
    }

    @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
    public static void initWebView(Activity activity, WebView webView, WebIndicator indicator) {
        if (activity == null || webView == null || activity.isFinishing()) {
            return;
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        //noinspection deprecation
        settings.setSavePassword(false);
        if (checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        settings.setTextZoom(100);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportMultipleWindows(true);
        // 是否阻塞加载网络图片  协议http or https
        settings.setBlockNetworkImage(false);
        // 允许加载本地文件html  file协议
        settings.setAllowFileAccess(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //noinspection deprecation
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        settings.setDomStorageEnabled(true);
        settings.setNeedInitialFocus(true);
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        settings.setDefaultFontSize(16);
        settings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        settings.setGeolocationEnabled(true);
        String dir = activity.getCacheDir().getAbsolutePath() + File.separator + "web_cache";
        //Log.i("123", "dir:" + dir + "   appcache:" + dir);

        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        settings.setGeolocationDatabasePath(dir);
        //noinspection deprecation
        settings.setDatabasePath(dir);
        settings.setAppCachePath(dir);
        //缓存文件最大值
        //noinspection deprecation
        settings.setAppCacheMaxSize(Long.MAX_VALUE);

        //设置是否打开 WebView 表单数据的保存功能
        settings.setSaveFormData(true);
        //设置 WebView 的默认 userAgent 字符串
        settings.setUserAgentString("");
        // 支持PC上的Chrome调试WebView，具体方法请百度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 只支持Debug模式下调试
            if (0 != (webView.getContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        Log.i("123", "UserAgentString : " + settings.getUserAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 安卓9.0后不允许多进程使用同一个数据目录，需设置前缀来区分
            // 参阅 https://blog.csdn.net/lvshuchangyin/article/details/89446629
            Context context = webView.getContext();
            String processName = getCurrentProcessName(context);
            if (!context.getApplicationContext().getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        //--------------------
        fixAndroidLollipop(webView);
        //WebViewClient
        webView.setWebViewClient(new CustomWebClient(activity));
        //WebChromeClient
        if (indicator != null) {
            //WebIndicatorController
            webView.setWebChromeClient(new CustomWebChromeClient(activity, new WebIndicatorController().inject(indicator)));
        }

    }

    /**
     * WebView 嵌套在 ScrollView 中崩溃的问题, 关闭硬件加速
     * <p>
     * https://my.oschina.net/onlykc/blog/2050590
     */
    private static void fixAndroidLollipop(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    public static boolean performKeyDown(Object context, WebView webView, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                if (context instanceof Activity) {
                    ((Activity) context).onBackPressed();
                } else if (context instanceof Fragment) {
                    final Activity activity = ((Fragment) context).getActivity();
                    if (activity != null) {
                        activity.onBackPressed();
                    }
                }
                return false;
            }
        }
        if (context instanceof Activity) {
            return ((Activity) context).onKeyDown(keyCode, event);
        } else if (context instanceof Fragment) {
            final Activity activity = ((Fragment) context).getActivity();
            if (activity != null) {
                return activity.onKeyDown(keyCode, event);
            }
        }
        return false;
    }

    /**
     * WebView 加载图片过宽问题 -> https://blog.csdn.net/minwenping/article/details/56878254
     * <pre>
     * override fun onPageFinished(webView: WebView?, url: String?) {
     *      super.onPageFinished(webView, url)
     *      autoFitImage(webView)
     * }
     */
    public static void autoFitImage(WebView webView) {
        final String fitRule = "javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                " img.style.maxWidth = '100%';img.style.height='auto';" +
                "}" +
                "})()";
        webView.evaluateJavascript(fitRule, null);
    }

    public static void loadContent(WebView webView, String source) {
        loadContentWithBaseUrl(webView, null, source);
    }

    /**
     * <pre>
     *      webView.loadData(newSource, "text/html", "UTF-8");  //中文乱码问题
     *      val html =
     *              "< p >< script type='text / javascript' " +
     *              "  src='http://vd.zqrb.cn/admin/getvod/getvideo?key=b99dd7e5cfc1dadbb486326c68c8eb42&videoId=098ae9c0336f44f7b0990ea8433f45ff&isRePlay=1&isautoplay=1&id=358'> " +
     *              "< / script >< /p >< p >（策划 XXX音视频中心）< /p >"
     *      mWebView.loadUrl(html)
     *
     *      mWebView.loadDataWithBaseURL("", html, "text/html", "UTF-8", null)
     * </pre>
     */
    public static void loadContentWithBaseUrl(WebView webView, String baseUrl, String source) {
        if (webView != null) {
            final String newSource = noNull(source);
            String url = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                url = Html.fromHtml(newSource, FROM_HTML_MODE_LEGACY).toString();// for 24 api and more
            } else {
                url = Html.fromHtml(newSource).toString();// or for older api
            }

            //检查路径对否合法
            if (Patterns.WEB_URL.matcher(url).matches()) {
                webView.loadUrl(url);
            } else {
                //webView.loadData(newSource, "text/html", "UTF-8");  //中文乱码问题
                webView.loadDataWithBaseURL(baseUrl, newSource, "text/html", "UTF-8", null);
            }
        }
    }

    @SuppressLint({"MissingPermission"})
    private static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static String noNull(String source) {
        if (source == null || TextUtils.isEmpty(source.trim())) {
            return "";
        } else {
            return source;
        }
    }

    private static String getCurrentProcessName(Context context) {
        String name = getCurrentProcessNameByFile();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        name = getCurrentProcessNameByAms(context);
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        name = getCurrentProcessNameByReflect(context);
        return name;
    }

    private static String getCurrentProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentProcessNameByAms(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "";
        }
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) {
            return "";
        }
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName;
                }
            }
        }
        return "";
    }

    private static String getCurrentProcessNameByReflect(Context context) {
        String processName = "";
        try {
            Application app = (Application) context.getApplicationContext();
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);

            Field activityThreadField;
            if (loadedApk != null) {
                activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
                activityThreadField.setAccessible(true);
                Object activityThread = activityThreadField.get(loadedApk);
                Method getProcessName;
                if (activityThread != null) {
                    getProcessName = activityThread.getClass().getDeclaredMethod("getProcessName");
                    processName = (String) getProcessName.invoke(activityThread);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }
}