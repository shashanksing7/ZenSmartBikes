package com.example.zensmartbikes.Ride;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncRequest;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

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
    Notification Manager Object.
     */
    NotificationManager manager;

    /*
    Notification Builder object.
     */
    NotificationCompat.Builder builder;
    /*
 Notification Id.
  */
    private final int NotificationId=1;
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
    This is the action for our broadcastReceiver.
     */
    public static final String ACTION_CONTROL_RIDE = "com.example.zensmartbikes.ACTION_CONTROL_RIDE";

    /*
    Variable for intent extra.
     */
    private String IntentExtra="RideControl";
    /*
    Variables for Receivers.
     */
    private final String Start_Ride="start";
    private  final String Stop_Ride="stop";
    private final  String Pause_Ride="pause";
    private final String Resume_Ride="resume";


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
//        IntentFilter filter=new IntentFilter(ACTION_CONTROL_RIDE);
//        registerReceiver(rideReceiver,filter);

    }
//    /*
//    This is the BroadcastReceiver;
//     */
//    private BroadcastReceiver rideReceiver=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            /*
//            Getting the Action for our receiver.
//             */
//            String action=intent.getAction();
//            /*
//            Checking if the Action is same.
//             */
//            if(ACTION_CONTROL_RIDE.equals(action)){
//                /*
//                same,getting the extra message from intent.
//                 */
//                String Message=intent.getStringExtra(IntentExtra);
//                switch (Message){
//                    case Start_Ride:
//                        /*
//                        Call method to start ride.
//                         */
//                        StartRideNotification();
//                        break;
//                    case Stop_Ride:
//                        /*
//                        call method to stop ride
//                         */
//                        StopRideNotification();
//                        break;
//                    case  Pause_Ride:
//                        /*
//                        call method to pause ride.
//                         */
//                        PauseRideNotification();
//                        break;
//                    case Resume_Ride:
//                        /*
//                        call method to resume ride.
//                         */
//                        ResumeRideNotification();
//                        break;
//                    default:
//                        break;
//                }
//
//
//            }
//
//        }
//    };

    /*
    OnStartCommand Method.
     */
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        /*
//        Starting the Notification Channel
//         */
//
//        /*
//        Getting the method that build the notification for ongoing ride
//         */
//         builder=new ZenNotificationsBuilder().getRideNotificationBuilder(this, ZenSmartBikesNotificationVariables.rideChannelId);
//        /*
//        Getting the Notifications Manager.
//         */
//        manager =(NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
//        startForeground(NotificationId,builder.build());
//        return START_REDELIVER_INTENT;
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        builder = new ZenNotificationsBuilder().getRideNotificationBuilder(this, ZenSmartBikesNotificationVariables.rideChannelId);
//        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // Start the service as a foreground service with a notification
////        startForeground(NotificationId, builder.build());
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//            startForeground(NotificationId, builder.build());
//        } else {
//            startForeground(NotificationId, builder.build(),ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
//        }
//
//
//        return START_NOT_STICKY; // Indicate that the service should not be restarted if terminated by the system
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        builder = new ZenNotificationsBuilder().getRideNotificationBuilder(this, ZenSmartBikesNotificationVariables.rideChannelId);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//            startForeground(NotificationId, builder.build());
//        } else {
//            startForeground(NotificationId, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForegroundService(new Intent(this,OnGoingRideService.class));
            startForeground(NotificationId, builder.build(),ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);

            Log.d(TAG, "onStartCommand: startforeground");
        } else {
            startForeground(NotificationId, builder.build());
        }


        return START_NOT_STICKY;
    }


    /*
    OnDestroy Method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(rideReceiver);
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
    public void HandlerConnectMethod(OngoingRideCallback rideCallback){
        /*
        Setting the context.
         */
        rideHelperThread.OnGoingRideThreadHandler.SetContext(getApplication());
        /*
        Setting the object oft eh call back interface in Handler class.
         */
        rideHelperThread.OnGoingRideThreadHandler.setRideCallback(rideCallback);
        /*
        Setting the Notification variables in Handler class.
         */
        rideHelperThread.OnGoingRideThreadHandler.SetNotificationObjects(manager,builder);
        /*
         Connecting Smart Device.
         */
        String ConnectDeviceMessage = "MacAddress";
        Message ConnectDeviceMessageObj = rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_CONNECT_DEVICE, ConnectDeviceMessage);
       try{
           rideHelperThread.OnGoingRideThreadHandler.sendMessage(ConnectDeviceMessageObj);
       }
       catch(Exception e){
           Toast.makeText(this, "Couldn't connect", Toast.LENGTH_SHORT).show();
       }
    }

    /*
    This method will be used to start ride of the device.
     */
    public void StartRideHandlerMethod(OngoingRideCallback rideCallback){

        /*
        Setting the object oft eh call back interface in Handler class.
         */
        rideHelperThread.OnGoingRideThreadHandler.setRideCallback(rideCallback);
        /*
        Starting the ride.
         */
        String StartRideMessage="1";
        Message StartRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_START_RIDE,StartRideMessage);
        try{
            rideHelperThread.OnGoingRideThreadHandler.sendMessage(StartRideMessageObject);
        }
        catch (Exception e){
            Toast.makeText(this, "Couldn't start Ride", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    This method will be used to pause  the ride of the device.
     */
    public void PauseRideHandlerMethod(OngoingRideCallback rideCallback){
        /*
        Setting the object oft eh call back interface in Handler class.
         */
        rideHelperThread.OnGoingRideThreadHandler.setRideCallback(rideCallback);
        /*
        Pause the ride.
         */
        String PauseRideMessage="2";
        Message PauseRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_PAUSE_RIDE,PauseRideMessage);
         try{
             rideHelperThread.OnGoingRideThreadHandler.sendMessage(PauseRideMessageObject);
         }
         catch (Exception e){
             Toast.makeText(this, "Couldn't pause Ride", Toast.LENGTH_SHORT).show();
         }
    }
    /*
    This method will be used to stop the ride of the device.
     */
    public void StopRideHandlerMethod(OngoingRideCallback rideCallback){
        /*
        Setting the object oft eh call back interface in Handler class.
         */
        rideHelperThread.OnGoingRideThreadHandler.setRideCallback(rideCallback);
        String StopRideMessage="2";
        Message StopRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_STOP_RIDE,StopRideMessage);
        try{
            rideHelperThread.OnGoingRideThreadHandler.sendMessage(StopRideMessageObject);
        }
        catch (Exception e){
            Toast.makeText(this, "Couldn't Stop Ride", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    The method will be used to Resume the ride of the device.
     */
    public void ResumeRideHandlerMethod(OngoingRideCallback rideCallback){
        /*
        Setting the object oft eh call back interface in Handler class.
         */
        rideHelperThread.OnGoingRideThreadHandler.setRideCallback(rideCallback);
        String ResumeRideMessage="1";
        Message ResumeRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_RESUME_RIDE,ResumeRideMessage);
         try{
             rideHelperThread.OnGoingRideThreadHandler.sendMessage(ResumeRideMessageObject);
         }
         catch (Exception e){
             Toast.makeText(this, "Couldn't Resume Ride", Toast.LENGTH_SHORT).show();
         }
    }

    /*
    Method to check if first bit is already sent or not
     */
    public boolean CheckFirstBit(){
        String ResumeRideMessage="9";
        Message CheckFirstBitMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_CHECK_FIRST_BIT,ResumeRideMessage);
        return rideHelperThread.OnGoingRideThreadHandler.sendMessage(CheckFirstBitMessageObject);
    }
    /*
    Check if device is connectedor not.
     */
    public boolean IsConnected(){
        return rideHelperThread.OnGoingRideThreadHandler.CheckIfConnected();
    }

    /* Method to stop the foreground service
     */
    public void stopForegroundService() {
        /*
         Stop the foreground service
         */
        stopForeground(true);

    }

//    /*
//    Methods that will be called when the actions in notification are pressed.
//     */
//    /*
//    Method to start ride.
//     */
//    private void StartRideNotification(){
//        /*
//        Starting the ride.
//         */
//        String StartRideMessage="1";
//        Message StartRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_START_RIDE,StartRideMessage);
//        try{
//            rideHelperThread.OnGoingRideThreadHandler.sendMessage(StartRideMessageObject);
//        }
//        catch (Exception e){
//            Toast.makeText(this, "Couldn't start Ride", Toast.LENGTH_SHORT).show();
//        }
//    };
//    /*
//    Method to Stop ride.
//     */
//    private  void StopRideNotification(){
//        /*
//        Stop Ride.
//         */
//        String StopRideMessage="2";
//        Message StopRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_STOP_RIDE,StopRideMessage);
//        try{
//            rideHelperThread.OnGoingRideThreadHandler.sendMessage(StopRideMessageObject);
//        }
//        catch (Exception e){
//            Toast.makeText(this, "Couldn't Stop Ride", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//    /*
//    method to pause ride
//     */
//    private  void PauseRideNotification(){
//
//        /*
//        Pause the ride.
//         */
//        String PauseRideMessage="2";
//        Message PauseRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_PAUSE_RIDE,PauseRideMessage);
//        try{
//            rideHelperThread.OnGoingRideThreadHandler.sendMessage(PauseRideMessageObject);
//        }
//        catch (Exception e){
//            Toast.makeText(this, "Couldn't pause Ride", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//    /*
//    method to resume ride
//      */
//    private  void ResumeRideNotification(){
//        /*
//        Resume Ride.
//         */
//        String ResumeRideMessage="1";
//        Message ResumeRideMessageObject=rideHelperThread.OnGoingRideThreadHandler.obtainMessage(MESSAGE_RESUME_RIDE,ResumeRideMessage);
//        try{
//            rideHelperThread.OnGoingRideThreadHandler.sendMessage(ResumeRideMessageObject);
//        }
//        catch (Exception e){
//            Toast.makeText(this, "Couldn't Resume Ride", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//
}
