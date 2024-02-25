package com.example.zensmartbikes.NotificationChannels;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.zensmartbikes.MainActivity;
import com.example.zensmartbikes.R;

/*
This class will be used to build notifications for our app
 */
public class ZenNotificationsBuilder {

    /*
    This method will return the Builder object for the ongoing ride notification
     */
    public NotificationCompat.Builder getRideNotificationBuilder(Context context,String ChannelId){

        /*
        Intent for Actions
         */
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent ContentIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent ContentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        /*
        Building the builder.
        */
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,ChannelId)
                .setSmallIcon(R.drawable.zenlogosvg)
                .setContentTitle("Zentorc SmartBikes")
                .setContentText("Ready to Connect")
                .setContentIntent(ContentIntent)
                ;
        return builder;

    }
}
