package com.example.zensmartbikes.Ride;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.zensmartbikes.NotificationChannels.ZenNotificationsBuilder;
import com.example.zensmartbikes.NotificationChannels.ZenSmartBikesNotificationVariables;

public class OnGoingRideService extends Service {

    /*
    Creating the Object of the OnGoingRideServiceBinder class.
     */
    private  IBinder onGoingRideServiceBinder=new OnGoingRideServicBinder();

    /*
    Creating the Variable of the OnGoingRideThreadHandler.
     */
    private  OngoingRideHelperThread rideHelperThread;

    private final String TAG="mytag";

    /*
   This will be used to differentiate the message form each other.
    */
    public static final int MESSAGE_START_RIDE = 1;
    public static final int MESSAGE_RESUME_RIDE = 2;
    public static final int MESSAGE_PAUSE_RIDE = 3;
    public static final int MESSAGE_STOP_RIDE = 4;
    public static final  int MESSAGE_CONNECT_DEVICE=5;
    public static final  int MESSAGE_CHECK_FIRST_BIT=6;

    /*
    OnCreate Method.
     */
    @Override
    public void onCreate() {
        super.onCreate();

    /*
    Initializing the Variable of the helperThread.
     */
        rideHelperThread = new OngoingRideHelperThread();
        rideHelperThread.start();

    }

    /*
    OnStartCommand Method.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
        Starting the Notification Channel
         */
        final int NotificationId=1;
        /*
        Getting the method that build the notification for ongoing ride
         */
        NotificationCompat.Builder builder=new ZenNotificationsBuilder().getRideNotificationBuilder(this, ZenSmartBikesNotificationVariables.rideChannelId);
        /*
        Getting the Notifications Manager.
         */
        NotificationManager manager=(NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NotificationId,builder.build());
        Toast.makeText(this, "onstart excuetd", Toast.LENGTH_SHORT).show();

        return START_REDELIVER_INTENT;
    }

    /*
    OnDestroy Method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
    OnUnbind Method.
     */

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    /*
    OnRebind Method.
     */

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    /*
      OnBind Method.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return onGoingRideServiceBinder;
    }

    /*
    Creating the Binder Class whose Object will be shared to communicate with the RideFragment and send command.
     */
    public  class  OnGoingRideServicBinder extends Binder{
        /*
        This Method will return the Object of the OnGoingRideService Class.
         */
        public  OnGoingRideService getOnGoingRideService(){
            return OnGoingRideService.this;
        }
    }

    /*
    This method will be called to connect the device to Bike by calling the ConnectToSmartBike() method of the Handler.
     */
    public boolean HandlerConnectMethod(){
        /*
         Connecting Smart Device.
         */
        String ConnectDeviceMessage = "MacAddress";
        Message ConnectDeviceMessageObj = rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_CONNECT_DEVICE, ConnectDeviceMessage);
        return rideHelperThread.OnGoingRideThreadHandler.sendMessage(ConnectDeviceMessageObj);

    }

    /*
    This method will be used to start ride of the device.
     */
    public boolean StartRideHandlerMethod(){
        String StartRideMessage="1";
        Message StartRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_START_RIDE,StartRideMessage);
        return  rideHelperThread.OnGoingRideThreadHandler.sendMessage(StartRideMessageObject);
    }

    /*
    This method will be used to pause  the ride of the device.
     */
    public boolean PauseRideHandlerMethod(){
        String PauseRideMessage="2";
        Message PauseRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_PAUSE_RIDE,PauseRideMessage);
        return rideHelperThread.OnGoingRideThreadHandler.sendMessage(PauseRideMessageObject);

    }
    /*
    This method will be used to stop the ride of the device.
     */
    public boolean StopRideHandlerMethod(){
        String StopRideMessage="2";
        Message StopRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_STOP_RIDE,StopRideMessage);
        return rideHelperThread.OnGoingRideThreadHandler.sendMessage(StopRideMessageObject);
    }

    /*
    The method will be used to Resume the ride of the device.
     */
    public boolean ResumeRideHandlerMethod(){
        String ResumeRideMessage="1";
        Message ResumeRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_RESUME_RIDE,ResumeRideMessage);
        return  rideHelperThread.OnGoingRideThreadHandler.sendMessage(ResumeRideMessageObject);
    }

    /*
    Method to check if first bit is already sent or not
     */
    public boolean CheckFirstBit(){
        String ResumeRideMessage="9";
        Message CheckFirstBitMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_CHECK_FIRST_BIT,ResumeRideMessage);
        return rideHelperThread.OnGoingRideThreadHandler.sendMessage(CheckFirstBitMessageObject);
    }


}
