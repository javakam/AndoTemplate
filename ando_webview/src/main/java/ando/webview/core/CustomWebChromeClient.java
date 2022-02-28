package ando.webview.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ando.webview.indicator.WebIndicatorController;

import static ando.webview.core.ActionActivity.LOCATION;

/**
 * # CustomWebChromeClient
 *
 * @author javakam
 * 2020/7/24  10:43
 */
public class CustomWebChromeClient extends WebChromeClient {

    /**
     * Activity's WeakReference
     */
    private final WeakReference<Activity> mActivityWeakReference;

    private final WebIndicatorController mIndicatorController;
    /**
     * Web端触发的定位 mOrigin
     */
    private String mOrigin = null;
    /**
     * Web 端触发的定位 Callback 回调成功，或者失败
     */
    private GeolocationPermissions.Callback mCallback = null;
    /**
     * 标识当前是获取定位权限
     */
    public static final int FROM_CODE_INTENTION_LOCATION = 0x16;

    public CustomWebChromeClient(Activity activity, WebIndicatorController controller) {
        this.mActivityWeakReference = new WeakReference<Activity>(activity);
        this.mIndicatorController = controller;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mIndicatorController != null) {
            mIndicatorController.progress(view, newProgress);
        }
    }

    //location
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        onGeolocationPermissionsShowPromptInternal(origin, callback);
    }

    private void onGeolocationPermissionsShowPromptInternal(String origin, GeolocationPermissions.Callback callback) {
        Activity mActivity = mActivityWeakReference.get();
        if (mActivity == null) {
            callback.invoke(origin, false, false);
            return;
        }
        List<String> deniedPermissions = null;
        if ((deniedPermissions = getDeniedPermissions(mActivity, LOCATION)).isEmpty()) {
            //Log.i(TAG, "onGeolocationPermissionsShowPromptInternal:" + true);
            callback.invoke(origin, true, false);
        } else {
            ActionActivity.setPermissionListener(mPermissionListener);
            this.mCallback = callback;
            this.mOrigin = origin;
            ActionActivity.start(mActivity, FROM_CODE_INTENTION_LOCATION);
        }
    }

    private final ActionActivity.PermissionListener mPermissionListener = new ActionActivity.PermissionListener() {
        @Override
        public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
            if (extras.getInt(ActionActivity.KEY_FROM_INTENTION) == FROM_CODE_INTENTION_LOCATION) {
                boolean hasPermission = hasPermission(mActivityWeakReference.get(), permissions);
                if (mCallback != null) {
                    mCallback.invoke(mOrigin, hasPermission, false);
                    mCallback = null;
                    mOrigin = null;
                }
            }
        }
    };

    public static List<String> getDeniedPermissions(Activity activity, String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return null;
        }
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(activity, permission)) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }

    public static boolean hasPermission(@NonNull Context context, @NonNull List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) {
                continue;
            }
            result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result != AppOpsManagerCompat.MODE_ALLOWED) {
                return false;
            }
        }
        return true;
    }


}