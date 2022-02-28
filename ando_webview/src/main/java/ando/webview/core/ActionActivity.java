package ando.webview.core;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * # ActionActivity
 *
 * @author javakam
 * 2020/8/24 15:51
 */
public final class ActionActivity extends Activity {

    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String KEY_URI = "KEY_URI";
    public static final String KEY_FROM_INTENTION = "KEY_FROM_INTENTION";
    private static RationaleListener mRationaleListener;
    private static PermissionListener mPermissionListener;

    public static final String[] LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public static void start(Activity activity, int requestCode) {
        Intent mIntent = new Intent(activity, ActionActivity.class);
        mIntent.putExtra(KEY_ACTION, "location");
        mIntent.putExtra(KEY_FROM_INTENTION, requestCode);
        activity.startActivity(mIntent);
    }

    public static void setPermissionListener(PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
    }

    private void cancelAction() {
        mPermissionListener = null;
        mRationaleListener = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //Log.i(TAG, "savedInstanceState:" + savedInstanceState);
            return;
        }
        Intent intent = getIntent();
        String mAction = intent.getStringExtra(KEY_ACTION);
        if (mAction == null) {
            cancelAction();
            this.finish();
            return;
        }
        if (TextUtils.equals("location", mAction)) {
            permission();
        }
    }

    private void permission() {
        if (mRationaleListener != null) {
            boolean rationale = false;
            for (String permission : LOCATION) {
                rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (rationale) {
                    break;
                }
            }
            mRationaleListener.onRationaleResult(rationale, new Bundle());
            mRationaleListener = null;
            finish();
            return;
        }
        if (mPermissionListener != null) {
            ActivityCompat.requestPermissions(this, LOCATION, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionListener != null) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_FROM_INTENTION, getIntent().getIntExtra(KEY_FROM_INTENTION, 1));
            mPermissionListener.onRequestPermissionsResult(permissions, grantResults, mBundle);
        }
        mPermissionListener = null;
        finish();
    }

    public interface RationaleListener {
        void onRationaleResult(boolean showRationale, Bundle extras);
    }

    public interface PermissionListener {
        void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}