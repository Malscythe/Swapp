package com.example.Swapp.call.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.Swapp.SplashScreen;
import com.example.Swapp.call.SinchService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;

import java.util.Map;

import Swapp.R;

public class FcmListenerService extends FirebaseMessagingService {

    public static String CHANNEL_ID = "Sinch Push Notification Channel";
    private static final String TAG = FcmListenerService.class.getSimpleName();
    NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Map data = remoteMessage.getData();

        // Optional: inspect the payload w/o starting Sinch Client and thus avoiding onIncomingCall()
        // e.g. useful to fetch user related polices (blacklist), resources (to show a picture, etc).
        NotificationResult result = SinchHelpers.queryPushNotificationPayload(getApplicationContext(), data);
        if (result.isValid() && result.isCall()) {
            CallNotificationResult callResult = result.getCallResult();
            Log.d(TAG, "queryPushNotificationPayload() -> display name: " + result.getDisplayName());
            if (callResult != null) {
                Log.d(TAG, "queryPushNotificationPayload() -> headers: " + result.getCallResult().getHeaders());
                Log.d(TAG, "queryPushNotificationPayload() -> remote user ID: " + result.getCallResult().getRemoteUserId());
            }
        }

        // Mandatory: forward payload to the SinchClient.
        if (SinchHelpers.isSinchPushPayload(data)) {
            new ServiceConnection() {
                private Map payload;

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (payload != null) {
                        SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) service;
                        if (sinchService != null) {
                            NotificationResult result = sinchService.relayRemotePushNotificationPayload(payload);
                            if (result.isValid() && result.isCall()) {
                                // Optional: handle result, e.g. show a notification or similar.
                            }
                        }
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {}

                public void relayMessageData(Map<String, String> data) {
                    payload = data;
                    createNotificationChannel(NotificationManager.IMPORTANCE_MAX);
                    getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
                }
            }.relayMessageData(data);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.swapplogoonly);
        } else {
            builder.setSmallIcon(R.drawable.swapplogoonly);
        }

        Intent resultIntent = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(mNotificationManager.IMPORTANCE_DEFAULT);

        mNotificationManager.notify(100, builder.build());


    }

    private void createNotificationChannel(int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sinch";
            String description = "Incoming Sinch Push Notifications.";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}