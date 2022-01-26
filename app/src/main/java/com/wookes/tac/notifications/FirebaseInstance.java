package com.wookes.tac.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wookes.tac.R;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.util.c;

import java.util.Random;

public class FirebaseInstance extends FirebaseMessagingService implements a {

    private RemoteMessage remoteMessage;
    private GetBitmap getBitmap;
    private Bitmap temp;
    private CircleBitmap circleBitmap;
    private static final String IMPORTANCE_DEFAULT = "default";
    private static final String IMPORTANCE_HIGH = "high";
    private static final String IMPORTANCE_LOW = "low";
    private static final String IMPORTANCE_MAX = "max";
    private static final String IMPORTANCE_MIN = "min";
    private static final String IMPORTANCE_NONE = "none";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(IMPORTANCE_LOW, "Likes, Comments", NotificationManager.IMPORTANCE_LOW);
            createNotificationChannel(IMPORTANCE_MAX, "Inform", NotificationManager.IMPORTANCE_MAX);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        new c(this).refreshNotificationToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.remoteMessage = remoteMessage;
        circleBitmap = new CircleBitmap();
        String cancelId = remoteMessage.getData().get("cancel_id");
        if (cancelId != null) {
            cancelNotification(Integer.parseInt(cancelId));
            return;
        }
        getBitmap = new GetBitmap(FirebaseInstance.this, FirebaseInstance.this);
        getImage();
    }

    private void sendNotification(Bitmap profilePic, Bitmap contentImage) {
        String notifyId = remoteMessage.getData().get("notify_id");
        String priority = remoteMessage.getData().get("priority");
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getApplicationContext(), LandingUi.class);
        intent.putExtra("hasNotifications", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(priority == null ? IMPORTANCE_LOW : priority).canBypassDnd();
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, priority == null ? IMPORTANCE_LOW : priority)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .setLargeIcon(circleBitmap.getCircleBitmap(profilePic))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);
        if (priority == null || priority.equals(IMPORTANCE_LOW)) {
            notificationBuilder.setSound(null);
            notificationBuilder.setVibrate(null);
            notificationBuilder.setPriority(Notification.PRIORITY_LOW);
        }
        if (contentImage != null)
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(contentImage));
        notificationManager.notify(notifyId == null ? new Random().nextInt(6) + 1000 : Integer.parseInt(notifyId), notificationBuilder.build());
    }

    private void getImage() {
        if (remoteMessage.getData().get("profile_pic") == null) {
            temp = circleBitmap.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_profile_avatar));
            if (remoteMessage.getData().get("content_image") == null) {
                sendNotification(temp, null);
            } else {
                getBitmap.retrieveBitmap(remoteMessage.getData().get("content_image"), false);
            }
        } else {
            getBitmap.retrieveBitmap(remoteMessage.getData().get("profile_pic"), true);
        }
    }

    private void cancelNotification(int notifyId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyId);
    }

    @Override
    public void onLoadProfileBitmap(Bitmap bitmap) {
        temp = bitmap;
        if (remoteMessage.getData().get("content_image") == null) {
            sendNotification(temp, null);
        } else {
            getBitmap.retrieveBitmap(remoteMessage.getData().get("content_image"), false);
        }
    }

    @Override
    public void onLoadContentBitmap(Bitmap bitmap) {
        sendNotification(temp, bitmap);
    }
}