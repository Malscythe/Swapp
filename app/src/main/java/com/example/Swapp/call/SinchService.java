package com.example.Swapp.call;

import static com.example.Swapp.call.IncomingCallScreenActivity.ACTION_ANSWER;
import static com.example.Swapp.call.IncomingCallScreenActivity.ACTION_IGNORE;
import static com.example.Swapp.call.IncomingCallScreenActivity.EXTRA_ID;
import static com.example.Swapp.call.IncomingCallScreenActivity.MESSAGE_ID;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.Swapp.JWT;
import com.example.Swapp.call.fcm.FcmListenerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Internals;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import java.util.List;
import java.util.Map;

import Swapp.R;


public class SinchService extends Service {

    /*
     IMPORTANT!

     This sample application was designed to provide the simplest possible way
     to evaluate Sinch Android SDK right out of the box, omitting crucial feature of handling
     incoming calls via managed push notifications, which requires registering in FCM console and
     procuring google-services.json in order to build and work.

     Android 8.0 (API level 26) imposes limitation on background services and we strongly encourage
     you to use Sinch Managed Push notifications to handle incoming calls when app is closed or in
     background or phone is locked.

     DO NOT USE THIS APPLICATION as a skeleton of your project!

     Instead, use:
     - sinch-rtc-sample-push (for audio calls) and
     - sinch-rtc-sample-video-push (for video calls)


     To ensure that SinchClient is not created more than once, make sure that SinchService lifecycle is managed correctly.
     E.g. in this sample apps we demonstrate that the service is bound in the BaseActivity, which is the parent
     class for all activities that uses SinchClient. So, whenever a new activity starts, it's being bound to SinchService.
     Thus, SinchService always has at least one `client` activity and is never terminated while the relevant activities are
     on the foreground. That ensures that there wwould be at most one instance of the SinchClient, which is especially important
     for the registration step.

     SinchClient entry points are 'start()' to make an outbound call, and `relayRemotePushNotificationPayload()` to handle
     an incoming call received via push notification.

     Useful links:

     Activity lifecycle: https://developer.android.com/guide/components/activities/activity-lifecycle
     Bound services lifecycle: https://developer.android.com/guide/components/bound-services#Lifecycle
     Navigating between activities: https://developer.android.com/guide/components/activities/activity-lifecycle.html#tba

     */

    public static final String APP_KEY = "ea982823-cc94-4f19-b063-8af4b03e2bfc";
    public static final String APP_SECRET = "l2Xdo2leA0CliIGOklUAIw==";
    public static final String ENVIRONMENT = "ocra.api.sinch.com";

    private static final String CHANNEL_ID = "Sinch Incoming Call";
    public static final int MESSAGE_PERMISSIONS_NEEDED = 1;
    public static final String REQUIRED_PERMISSION = "REQUIRED_PERMISSION";
    public static final String MESSENGER = "MESSENGER";
    private Messenger messenger;

    public static final String CALL_ID = "CALL_ID";
    static final String TAG = SinchService.class.getSimpleName();

    private PersistedSettings mSettings;
    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private SinchClient mSinchClient;

    private StartFailedListener mListener;
    private String mUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        mSettings = new PersistedSettings(getApplicationContext());
        attemptAutoStart();
    }

    private void attemptAutoStart() {
        if (messenger != null) {
            start();
        }
    }

    private void createClient(String username) {
        mUserId = username;
        mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(username)
                .applicationKey(APP_KEY)
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportManagedPush(true);

        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        mSinchClient.setPushNotificationDisplayName("User " + username);
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminateGracefully();
        }
        super.onDestroy();
    }

    private boolean hasUsername() {
        if (mSettings.getUsername().isEmpty()) {
            Log.e(TAG, "Can't start a SinchClient as no username is available!");
            return false;
        }
        return true;
    }

    private void createClientIfNecessary() {
        if (mSinchClient != null)
            return;
        if (!hasUsername()) {
            throw new IllegalStateException("Can't create a SinchClient as no username is available!");
        }
        createClient(mSettings.getUsername());
    }

    private void start() {
        boolean permissionsGranted = true;
        createClientIfNecessary();
        try {
            //mandatory checks
            mSinchClient.checkManifest();
        } catch (MissingPermissionException e) {
            permissionsGranted = false;
            if (messenger != null) {
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString(REQUIRED_PERMISSION, e.getRequiredPermission());
                message.setData(bundle);
                message.what = MESSAGE_PERMISSIONS_NEEDED;
                try {
                    messenger.send(message);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (permissionsGranted) {
            Log.d(TAG, "Starting SinchClient");
            try {
                mSinchClient.start();
            } catch (IllegalStateException e) {
                Log.w(TAG, "Can't start SinchClient - " + e.getMessage());
            }
        }
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminateGracefully();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        messenger = intent.getParcelableExtra(MESSENGER);
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public Call callUser(String userId) {
            return mSinchClient.getCallClient().callUser(userId);
        }

        public String getUsername() { return mSettings.getUsername(); }

        public void setUsername(String username) { mSettings.setUsername(username);}

        public void retryStartAfterPermissionGranted() { SinchService.this.attemptAutoStart(); }

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient() { start(); }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient != null ? mSinchClient.getCallClient().getCall(callId) : null;
        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        public NotificationResult relayRemotePushNotificationPayload(final Map payload) {
            if (!hasUsername()) {
                Log.e(TAG, "Unable to relay the push notification!");
                return null;
            }
            createClientIfNecessary();
            return mSinchClient.relayRemotePushNotificationPayload(payload);
        }
    }

    public interface StartFailedListener {

        void onFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onFailed(error);
            }
            Internals.terminateForcefully(mSinchClient);
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onCredentialsRequired(ClientRegistration clientRegistration) {
            // Normally, should not be asked again, since the user / push token is registered via UserController,
            // but you have to provide valid registration here anyway, in case previous registration
            // is somehow compromised.
            clientRegistration.register(JWT.create(APP_KEY, APP_SECRET, mUserId));
        }

        @Override
        public void onUserRegistered() {
            Log.d(TAG, "User registered.");
        }

        @Override
        public void onUserRegistrationFailed(SinchError sinchError) {
            Log.e(TAG, "User registration failed: " + sinchError.getMessage());
        }

        @Override
        public void onPushTokenRegistered() {
            Log.d(TAG, "Push token registered.");
        }

        @Override
        public void onPushTokenRegistrationFailed(SinchError sinchError) {
            Log.e(TAG, "Push token registration failed." + sinchError.getMessage());
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "onIncomingCall: " + call.getCallId());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child("users").child(call.getRemoteUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("TAG", call.getRemoteUserId() + " " + snapshot.getChildrenCount());
                    String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                    Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
                    intent.putExtra(EXTRA_ID, MESSAGE_ID);
                    intent.putExtra(CALL_ID, call.getCallId());
                    intent.putExtra("userName", name);
                    boolean inForeground = isAppOnForeground(getApplicationContext());
                    if (!inForeground) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !inForeground) {
                        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(MESSAGE_ID, createIncomingCallNotification(name, intent));
                    } else {
                        SinchService.this.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
        private Bitmap getBitmap(Context context, int resId) {
            int largeIconWidth = 40;
            int largeIconHeight = 40;
            Drawable d = context.getResources().getDrawable(resId);
            Bitmap b = Bitmap.createBitmap(largeIconWidth, largeIconHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            d.setBounds(0, 0, largeIconWidth, largeIconHeight);
            d.draw(c);
            return b;
        }

        private PendingIntent getPendingIntent(Intent intent, String action) {
            intent.setAction(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 111, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

        @TargetApi(29)
        private Notification createIncomingCallNotification(String userId, Intent fullScreenIntent) {
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 112, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), FcmListenerService.CHANNEL_ID)
                            .setContentTitle("Incoming call")
                            .setContentText(userId)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(getBitmap(getApplicationContext(), R.drawable.call_pressed))
                            .setSmallIcon(R.drawable.call_pressed)
                            .setFullScreenIntent(pendingIntent, true)
                            .addAction(R.drawable.button_accept, "Answer",  getPendingIntent(fullScreenIntent, ACTION_ANSWER))
                            .addAction(R.drawable.button_decline, "Ignore", getPendingIntent(fullScreenIntent, ACTION_IGNORE))
                            .setOngoing(true);
            return builder.build();
        }
    }


    private class PersistedSettings {

        private SharedPreferences mStore;

        private static final String PREF_KEY = "Sinch";

        public PersistedSettings(Context context) {
            mStore = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        }

        public String getUsername() {
            return mStore.getString("Username", "");
        }

        public void setUsername(String username) {
            SharedPreferences.Editor editor = mStore.edit();
            editor.putString("Username", username);
            editor.commit();
        }
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