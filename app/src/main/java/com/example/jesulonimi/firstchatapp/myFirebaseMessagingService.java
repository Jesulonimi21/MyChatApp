package com.example.jesulonimi.firstchatapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class myFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title=remoteMessage.getNotification().getTitle();
        String notification_message=remoteMessage.getNotification().getBody();
        String click_action=remoteMessage.getNotification().getClickAction();
        String from_user_id=remoteMessage.getData().get("fromTheUser");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.user_avatar_good)
                .setContentTitle(notification_title)
                .setContentText(notification_message);

        Intent resultIntent=new Intent(click_action);
        resultIntent.putExtra("theId",from_user_id);
        PendingIntent pendingIntent=PendingIntent.getActivity(
                this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(pendingIntent);





        int mNotificationId=(int) System.currentTimeMillis();
        NotificationManager mNotifyMgr=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId,mBuilder.build());
    }
}
