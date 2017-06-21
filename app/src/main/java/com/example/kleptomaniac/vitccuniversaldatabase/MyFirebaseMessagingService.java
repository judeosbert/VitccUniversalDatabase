package com.example.kleptomaniac.vitccuniversaldatabase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by kleptomaniac on 16/6/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private final String TAG = "VITCC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.e(TAG,"FROM"+remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0)
        {
            Log.e(TAG,"Payload"+remoteMessage.getData());
            handleNow();
        }

        if(remoteMessage.getNotification() != null)
        {
            Log.e(TAG,"Body"+remoteMessage.getNotification().getBody());
        }
    }


    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, UserDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_check)
                .setContentTitle("VITCC Database Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
