package com.example.zensmartbikes.NotificationChannels;

import android.content.Context;

import androidx.core.app.NotificationCompat;

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
        Building the builder
        */
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,ChannelId)
                .setSmallIcon(R.drawable.zenlogosvg)
                .setContentTitle("Zentorc SmartBikes")
                .setContentText("Device Connected")
                ;

        return builder;

    }
}
