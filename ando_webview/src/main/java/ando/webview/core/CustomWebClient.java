package ando.webview.core;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.List;

/**
 * # CustomWebClient
 *
 * @author javakam
 * 2020/7/24  9:42
 */
public class CustomWebClient extends WebViewClient {
    /**
     * Activity's WeakReference
     */
    private WeakReference<Activity> mWeakReference;
    /**
     * intent ' s scheme
     */
    public static final String INTENT_SCHEME = "intent://";
    /**
     * Wechat pay scheme ，用于唤醒微信支付
     */
    public static final String WEBCHAT_PAY_SCHEME = "weixin://wap/pay?";
    /**
     * 支付宝
     */
    public static final String ALIPAYS_SCHEME = "alipays://";
    /**
     * http scheme
     */
    public static final String HTTP_SCHEME = "http://";
    /**
     * https scheme
     */
    public static final String HTTPS_SCHEME = "https://";
    /**
     * SMS scheme
     */
    public static final String SCHEME_SMS = "sms:";

    private static final String TAG = "web";

    public CustomWebClient(Activity activity) {
        this.mWeakReference = new WeakReference<Activity>(activity);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        WebViewUtils.autoFitImage(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (handleCommonLink(url)) {
            return true;
        }
        // intent
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url);
            Log.i(TAG, "intent url ");
            return true;
        }
        // 微信支付
        if (url.startsWith(WEBCHAT_PAY_SCHEME)) {
            Log.i(TAG, "lookup wechat to pay ~~");
            startActivity(url);
            return true;
        }
        if (url.startsWith(ALIPAYS_SCHEME) && lookup(url)) {
            Log.i(TAG, "alipays url lookup alipay ~~ ");
            return true;
        }
        if (queryActivitiesNumber(url) > 0 && deepLink(url, null)) {
            Log.i(TAG, "intercept url:" + url);
            return true;
        }
        if (!url.startsWith(HTTP_SCHEME) && !url.startsWith(HTTPS_SCHEME)) {
            Log.i(TAG, "intercept UnkownUrl :" + request.getUrl());
            return true;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //电话 ， 邮箱 ， 短信
        if (handleCommonLink(url)) {
            return true;
        }
        //Intent scheme
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url);
            return true;
        }
        //微信支付
        if (url.startsWith(WEBCHAT_PAY_SCHEME)) {
            startActivity(url);
            return true;
        }
        //支付宝
        if (url.startsWith(ALIPAYS_SCHEME) && lookup(url)) {
            return true;
        }
        //打开url 相对应的页面
        if (queryActivitiesNumber(url) > 0 && deepLink(url, null)) {
            Log.i(TAG, "intercept OtherAppScheme");
            return true;
        }
        if (!url.startsWith(HTTP_SCHEME) && !url.startsWith(HTTPS_SCHEME)) {
            Log.i(TAG, "intercept UnKnowUrl :" + url);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    private boolean handleCommonLink(String url) {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)) {
            try {
                Activity activity;
                if ((activity = mWeakReference.get()) == null) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void handleIntentUrl(String intentUrl) {
        try {
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME)) {
                return;
            }
            lookup(intentUrl);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private boolean lookup(String url) {
        try {
            Intent intent;
            Activity activity;
            if ((activity = mWeakReference.get()) == null) {
                return true;
            }
            PackageManager packageManager = activity.getPackageManager();
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            // 跳到该应用
            if (info != null) {
                activity.startActivity(intent);
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private int queryActivitiesNumber(String url) {
        try {
            if (mWeakReference.get() == null) {
                return 0;
            }
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            PackageManager mPackageManager = mWeakReference.get().getPackageManager();
            List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return resolveInfoList == null || resolveInfoList.isEmpty() ? 0 : resolveInfoList.size();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void startActivity(String url) {
        try {
            if (mWeakReference.get() == null) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mWeakReference.get().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlertDialog mAskOpenOtherAppDialog;

    private boolean deepLink(String url, final Handler.Callback callback) {
        return false;

        // 直接打开其他App
//        lookup(url);
//        return  true;

        // 咨询用户是否打开其他App
//        Activity mActivity = null;
//        if ((mActivity = mWeakReference.get()) == null) {
//            return false;
//        }
//        ResolveInfo resolveInfo = lookupResolveInfo(url);
//        if (null == resolveInfo) {
//            return false;
//        }
//        ActivityInfo activityInfo = resolveInfo.activityInfo;
//        Log.e(TAG, "resolve package:" + resolveInfo.activityInfo.packageName + " app package:" + mActivity.getPackageName());
//        if (!TextUtils.isEmpty(activityInfo.packageName)
//                && activityInfo.packageName.equals(mActivity.getPackageName())) {
//            return lookup(url);
//        }
//
//        Log.i(TAG, "onOpenPagePrompt");
//
//        if (mAskOpenOtherAppDialog == null) {
//            mAskOpenOtherAppDialog = new AlertDialog
//                    .Builder(mActivity)
//                    .setMessage(mActivity.getString(R.string.agentweb_leave_app_and_go_other_page,
//                            getApplicationName(mActivity)))
//                    .setTitle(mActivity.getString(R.string.agentweb_tips))
//                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (callback != null) {
//                                callback.handleMessage(Message.obtain(null, -1));
//                            }
//                        }
//                    })//
//                    .setPositiveButton(mActivity.getString(R.string.agentweb_leave), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (callback != null) {
//                                callback.handleMessage(Message.obtain(null, 1));
//                            }
//                        }
//                    })
//                    .create();
//        }
//        mAskOpenOtherAppDialog.show();
//        return true;
    }

    /**
     * 获取应用的名称
     */
    private String getApplicationName(Context context) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    private ResolveInfo lookupResolveInfo(String url) {
        try {
            Intent intent;
            Activity activity;
            if ((activity = mWeakReference.get()) == null) {
                return null;
            }
            PackageManager packageManager = activity.getPackageManager();
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            return packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
