package com.example.zensmartbikes.NotificationChannels;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

/*
This class will be used to create Notification channels.
 */
public class ZenSmartBikesNotifications extends ContextWrapper {



    public ZenSmartBikesNotifications(Context base) {
        super(base);
    }

    /*
        This class will be used to create notification channel.
         */
    public void CreateRideNotificationChannel(){

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ridenotificationChannel=new NotificationChannel(ZenSmartBikesNotificationVariables.rideChannelId, ZenSmartBikesNotificationVariables.RideChannelName,NotificationManager.IMPORTANCE_HIGH);
           /*
           will be visible even after screen lock.
            */
            ridenotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ridenotificationChannel.setDescription("Notifications for your Ongoing Ride");
            ridenotificationChannel.enableLights(true);
            ridenotificationChannel.setLightColor(Color.YELLOW);
            ridenotificationChannel.enableVibration(true);
            ridenotificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            getNotificationManager().createNotificationChannel(ridenotificationChannel);
        }

    }

    /*
    Returns Notification Manager.
     */
    protected NotificationManager getNotificationManager(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }
}
