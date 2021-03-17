package ando.toolkit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * # NotificationUtils
 *
 * @author javakam
 * 2020/3/20  16:40
 */

public class NotificationUtils {

    /**
     * 判断权限是否打开
     */
    public static boolean isNotificationEnabled(Context context) {
        //判断应用是否开启了通知权限 4.4以上可用，4.4以下默认返回true
        boolean isOpened = false;
        try {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            isOpened = manager.areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpened;
    }

    /**
     * 跳转到设置权限的页面
     */
    public static void gotoSet(Context context) {
        try {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            Intent intent = new Intent();
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
            }

            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
            if ("MI 6".equals(Build.MODEL)) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                intent.setAction("com.android.settings/.SubSettings");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();
            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


    private Context mContext;
    private Notification mNotification;
    //通知栏id
    private static final int NOTIFICATION_ID = 1;
    //兼容8.0
    private static final String NOTIFICATION_CHANNEL_ID = String.format("app_channel_%s", "ando");
    //通知栏name -> 用户可见
    private static final String NOTIFICATION_NAME = "后台播放课程";

    public NotificationUtils(Context context) {
        this.mContext = context;
    }

    private NotificationManager mNotificationManager;

    private NotificationManager getManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    private void initNotification() {

        initNotificationChannel();

        Intent intent = new Intent(mContext, mContext.getClass());
        //同通知栏跳转进入 Activity 不触发 onNewIntent 中清除数据的方法
        //intent.putExtra(INTENT_ACTION, INTENT_ACTION_NOTIFICATION);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification 必须设置 : setContentText（） setContentTitle（）setSmallIcon（）
        //8.0以下必须设置 setContent 才会展示两级状态栏
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, null)
                .setContentTitle("课程正在后台播放")            // 设置标题，必要
                .setContentText("")                           // 设置内容，必要
//                .setSmallIcon(R.mipmap.ic_logo)// 设置小图标，必要  todo 2020-11-04 14:15:16
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())          // 设置通知发送的时间戳
//                .setContent(mRemoteSmallView)
//                .setCustomBigContentView(mRemoteLargeView)    // 设置自定义的RemoteView，需要API最低为24
//                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(false);                           // 滑动删除通知

        //8.0必须设置 NOTIFICATION_CHANNEL_ID 否则不显示通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        //锁屏显示 -> 注：8.0无效，需要通过创建锁屏Activity或锁屏悬浮窗在锁屏时显示。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        }

        mNotification = builder.build();
        // 设置默认的提示音、振动方式、灯光等
        // FLAG_SHOW_LIGHTS         控制闪光
        // FLAG_NO_CLEAR            清除按钮无法取消
        // FLAG_FOREGROUND_SERVICE  前台服务标记
        // FLAG_ONLY_ALERT_ONCE     标记声音或者震动一次
        // FLAG_ONGOING_EVENT       正在进行中通知，将flag设置为这个属性那么通知就会像QQ一样一直在状态栏显示
//        mNotification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT |
//            Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS;
    }

    private void initNotificationChannel() {
        //8.0需要配置Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //通知栏重要性要设置为 IMPORTANCE_LOW ，当用户直接从后台直接退出APP时，状态栏跟着App的退出而销毁 -》 此时Activity.onDestroy不会被调用
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);

            //手动打开通知权限
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel  channel = getManager().getNotificationChannel(VideoNotificationService.NOTIFICATION_CHANNEL_ID);
//                if (channel != null) {
//                    int importance = channel.getImportance();
//                    if (importance == NotificationManager.IMPORTANCE_NONE || importance == NotificationManager.IMPORTANCE_MIN || importance == NotificationManager.IMPORTANCE_LOW) {
//                        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
//                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
//                        openMediaActivity(intent);
//                        Toast.makeText(mContext, "请手动将通知打开", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
        }
    }


}