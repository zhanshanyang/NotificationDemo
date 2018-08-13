package com.yzs.demo.notificationdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotification(View view) {
        String id = "channel_0";
        String des = "111";
        NotificationChannel channel = new NotificationChannel(id, des, NotificationManager.IMPORTANCE_MIN);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(MainActivity.this, id)
                .setContentTitle("Base Notification View")
                .setContentText("您有一条新通知")
                .setSmallIcon(R.drawable.jd_icon)
                .setStyle(new Notification.MediaStyle())
                .setAutoCancel(false)
                .addExtras(new Bundle())
                .build();
        notificationManager.notify(1, notification);
    }

    public void sendNotificationBigText(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotification_O();
        } else {
            sendNotification_N();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification_O() {
        intent = new Intent(MainActivity.this, LoginActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
        String id = "channel_1";
        String description = "123";
        NotificationChannel mChannel = new NotificationChannel(id, "123", NotificationManager.IMPORTANCE_HIGH);
        mChannel.setDescription(description);
        notificationManager.createNotificationChannel(mChannel);
        Notification notification = new Notification.Builder(MainActivity.this, id).setContentTitle("Title")
                .setSmallIcon(R.drawable.jd_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jingdong_icon))
                .setContentTitle("您有一条新通知")
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentText("这是一条逗你玩的消息")
                .setStyle(new Notification.BigTextStyle()
                        .bigText(getString(R.string.dialog_message)))
                .setAutoCancel(false)
                .addExtras(new Bundle())
                .setContentIntent(pintent)
                .build();
        notificationManager.notify(1, notification);
    }

    private void sendNotification_N() {
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.jd_icon)             //一定要设置
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jingdong_icon))
                .setContentTitle("您有一条新通知")
                .setContentText("这是一条逗你玩的消息")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_LOW)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setLights(Color.RED, 1000, 1000)
                .build();
        notificationManager.notify(1, notification);
    }

    public void sendNotificationBigPic(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(MainActivity.this, LoginActivity.class);
            PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
            Icon icon = Icon.createWithResource(this, R.drawable.jd_icon);
            Notification.Action action1 = new Notification.Action.Builder(icon, "Action1", pintent).build();
            String cancelId = "channel_2";
            NotificationChannel cannel = new NotificationChannel(cancelId, "456", NotificationManager.IMPORTANCE_MIN);
            cannel.setDescription("456_des");
            notificationManager.createNotificationChannel(cannel);
            Notification notification = new Notification.Builder(this, cancelId)
                    .setSmallIcon(R.drawable.jd_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jingdong_icon))
                    .setContentTitle("您有一条新通知1")
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentText("这是一条逗你玩的消息1")
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.fm_ting_card)))
                    .setAutoCancel(false)
                    .setContentIntent(pintent)
                    .addAction(action1)
//                    .addExtras()
//                    .setCustomBigContentView()
                    .build();
            notificationManager.notify(2, notification);
        }
    }

    private String CHANNEL_ID = "CHANNEL_ID";

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotificationInput(View view) {
        // Key for the string that's delivered in the action's intent.
        String KEY_TEXT_REPLY = "key_text_reply";

        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        intent = new Intent(MainActivity.this, LoginActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(getApplicationContext(),
                        100,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input.
        Notification.Action action =
                new Notification.Action.Builder(R.drawable.jd_icon,
                        getString(R.string.label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();



        // Build the notification and add the action.
        Notification newMessageNotification = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.jd_icon)
                .setContentTitle(getString(R.string.title_activity_login))
                .setContentText(getString(R.string.error_invalid_email))
                .addAction(action)
                .build();

        // Issue the notification.
        notificationManager.notify(3, newMessageNotification);
    }

    public void sendNotificationProgress(View view) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.jd_icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_EMAIL);

// Issue the initial notification with zero progress
        final int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(4, mBuilder.build());

// Do the job here that tracks the progress.
// Usually, this should be in a worker thread
// To show progress, update PROGRESS_CURRENT and update the notification with:
// mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
// notificationManager.notify(notificationId, mBuilder.build());

// When done, update the notification one more time to remove the progress bar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBuilder.setContentText("Download complete")
                        .setProgress(PROGRESS_MAX,50,false);
                notificationManager.notify(4, mBuilder.build());
            }
        }, 4000);


    }

    public void sendNotificationInbox(View view) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.jd_icon)
                .setContentTitle("5 New mails from 110")
                .setContentText("展示inbox 模式")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jingdong_icon))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("messageSnippet1")
                        .addLine("messageSnippet2"))
                .build();
        notificationManager.notify(5, notification);
    }


    public void sendNotificationMessages(View view) {

        NotificationCompat.MessagingStyle.Message message1 =
                new NotificationCompat.MessagingStyle.Message("messages0.Text",
                        System.currentTimeMillis(),
                        "messages0.Sender");
        NotificationCompat.MessagingStyle.Message message2 =
                new NotificationCompat.MessagingStyle.Message("messages1.Text",
                        System.currentTimeMillis() - 2000,
                        "messages1.Sender");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.jd_icon)
                .setStyle(new NotificationCompat.MessagingStyle("reply_name")
                        .addMessage(message1)
                        .addMessage(message2))
                .build();
        notificationManager.notify(6, notification);
    }

    public void sendNotificationCustomView(View view) {
        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);

// Apply the layouts to the notification
        Notification customNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.jd_icon)
                //如果不显示icon和title，那么就把下面这句去掉
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .build();
        notificationManager.notify(7, customNotification);
    }
}
