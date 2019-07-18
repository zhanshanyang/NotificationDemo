package com.yzs.demo.notificationdemo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yzs.demo.notificationdemo.R;

public class ForegroundServiceDemo extends Service {
    public static final String EXTRAS_NOTIFICATION_SHOW = "android.chj.notification.show";
    public static final int DEFAULT = 0;    // 不隐藏(默认)
    public static final int NO_SHOW = 1;    // 隐藏

    private NotificationManager mNotificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = "service_channel_id";
        NotificationChannel notificationChannel = new NotificationChannel(channelId,
                "foreground des",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(notificationChannel);
        // put
        Bundle extras = new Bundle();
        extras.putInt(EXTRAS_NOTIFICATION_SHOW, NO_SHOW);
        Notification notification = new Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.jd_icon)
                .setContentTitle("yang")
                .addExtras(extras)
                .build();
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
