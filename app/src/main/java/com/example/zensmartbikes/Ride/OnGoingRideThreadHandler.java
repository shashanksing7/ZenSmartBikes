package com.example.zensmartbikes.Ride;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.example.zensmartbikes.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/*
This is the Handler class that will be used to handle message and perform different operations a

a)Connect to cycle
b)Start Ride
c)Pause Ride
d)Resume Ride
e)Stop Ride

 */
public class OnGoingRideThreadHandler extends Handler {

    /*
    Variables for Bluetooth Connection and Rides.
     */
    private BluetoothAdapter adapter;

    private BluetoothSocket socket;

    private BluetoothDevice device;

    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String MacAddress = "00:00:13:10:48:13";

    private static final String TAG = "mytag";

    private OutputStream outputStream;
    private InputStream inputStream;
    /*
    This will be used to differentiate the message form each other.
     */
    private static final int MESSAGE_START_RIDE = 1;
    private static final int MESSAGE_RESUME_RIDE = 2;
    private static final int MESSAGE_PAUSE_RIDE = 3;
    private static final int MESSAGE_STOP_RIDE = 4;
    private static final  int MESSAGE_CONNECT_DEVICE=5;
    public static final  int MESSAGE_CHECK_FIRST_BIT=6;
    /*
    Object of the service class.
     */
    private OnGoingRideService onGoingRideService;
    /*
    Object of the Call back interface.
     */
    private  OngoingRideCallback rideCallback;

    /*
    These are the Variables that will be used for oue notification.
     */
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
        /*
    Notification Id.
    */
    private final int NotificationId=1;

    /*
        Creating Intent for start,Pause/Resume,Stop Action
         */
    PendingIntent StartIntent=null;
    PendingIntent StopIntent=null;
    PendingIntent PauseResume=null;

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
    Context Object.
     */
    private Context context;
    /*
    This is will handle the message and call the Different methods to perform operations.
     */
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        /*
        Getting the message and calling the message respectively and passing the message.
         */
        switch (msg.what) {
            case MESSAGE_START_RIDE:
                String startRideMessage = (String) msg.obj;
                boolean IsStarted=StartRide(startRideMessage);
                rideCallback.GetRideResult(IsStarted);
                break;

            case MESSAGE_RESUME_RIDE:
                String resumePauseRideMessage = (String) msg.obj;
                boolean IsResumed=ResumeRide(resumePauseRideMessage);
                rideCallback.GetRideResult(IsResumed);
                break;

            case MESSAGE_STOP_RIDE:
                String stopRideMessage = (String) msg.obj;
                boolean IsStopped=StopRide(stopRideMessage);
                rideCallback.GetRideResult(IsStopped);
                break;
            case MESSAGE_PAUSE_RIDE:
                String pauseRideMessage=(String) msg.obj;
                boolean InPaused=PauseRide(pauseRideMessage);
                rideCallback.GetRideResult(InPaused);
                break;
            case MESSAGE_CONNECT_DEVICE:
                boolean IsConnected=ConnectToSmartBike();
                rideCallback.GetRideResult(IsConnected);
                break;

            case MESSAGE_CHECK_FIRST_BIT:
                SendFirstBit();
                break;
            default:
                Log.d(TAG, "handleMessage: Unidentified call ");
                break;
        }

    }

        /*
    This method will be used to Start the Ride.
    */
        public boolean StartRide(String message) {
            if (outputStream != null) {
                try {
                    outputStream.write(message.getBytes("UTF-8"));

                    // Setting the Variables of RideVariables class.
                    RideVariables.RideStarted = true;
                    Log.d(TAG, "StartRide: data sent");

                    // Update Notification.
                    builder.setContentText("Ride Started");
                    manager.notify(NotificationId, builder.build());

                    return true;
                } catch (IOException e) {
                    Log.e(TAG, "StartRide: Unable to start Ride", e);
                    closeBluetoothConnection();
                    return false;
                }
            } else {
                Log.e(TAG, "StartRide: Output stream is null");
                closeBluetoothConnection();
                return false;
            }
        }

    /*
    This method will be used to Resume the ride.
    */
        public boolean ResumeRide(String message) {
            if (outputStream != null) {
                try {
                    outputStream.write(message.getBytes("UTF-8"));

                    // Setting the Variables of RideVariables class.
                    RideVariables.RideResumed = true;
                    RideVariables.RidePaused=false;
                    // Update Notification.
                    builder.setContentText("Ride Resumed");
                    manager.notify(NotificationId, builder.build());
                    return true;
                } catch (IOException e) {
                    Log.e(TAG, "ResumeRide: Unable to resume Ride", e);
                    closeBluetoothConnection();
                    return false;
                }
            } else {
                Log.e(TAG, "ResumeRide: Output stream is null");
                closeBluetoothConnection();
                return false;
            }
        }
    /*
This method will be used to Stop the ride.
*/
    public boolean StopRide(String message) {
        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes("UTF-8"));

                // Setting the Variables of RideVariables class.
                closeBluetoothConnection();
                RideVariables.RideStarted = false;
                RideVariables.RidePaused = false;
                RideVariables.RideResumed = false;

                // Update Notification.
                builder.setContentText("Ready to Connect")
                        .setAutoCancel(true);
                manager.notify(NotificationId, builder.build());
                return true;
            } catch (IOException e) {
                Log.e(TAG, "StopRide: Unable to stop Ride", e);
                closeBluetoothConnection();
                return false;
            }
        } else {
            Log.e(TAG, "StopRide: Output stream is null");
            closeBluetoothConnection();
            return false;
        }
    }

    /*
    This method will be used to Pause the Ride.
    */
    public boolean PauseRide(String message) {
            if (outputStream != null) {
                try {
                    outputStream.write(message.getBytes("UTF-8"));

                    // Setting the Variables of RideVariables class.
                    RideVariables.RidePaused = true;
                    RideVariables.RideResumed=false;
                    // Update Notification.
                    builder.setContentText("Ride Paused");
                    manager.notify(NotificationId, builder.build());
                    return true;
                } catch (IOException e) {
                    Log.e(TAG, "PauseRide: Unable to pause Ride", e);
                    closeBluetoothConnection();
                    return false;
                }
            } else {
                Log.e(TAG, "PauseRide: Output stream is null");
                closeBluetoothConnection();
                return false;
            }
        }

    /*
   This method will be called to connect to the Bluetooth device.
   */
    @SuppressLint("MissingPermission")
    public boolean ConnectToSmartBike() {
        // Getting the Bluetooth Adapter.
        adapter = BluetoothAdapter.getDefaultAdapter();

        // Check if the Adapter exists.
        if (adapter == null) {
            // Adapter Not found.
            return false;
        }

        // Adapter Found, create a device and return it.
        device = adapter.getRemoteDevice(MacAddress);

        Log.d(TAG, "CreateDevice: device created");

        // Creating a socket from the Device.
        BluetoothSocket mySocket = null;

        // Trying to create a Socket.
        try {
            mySocket = device.createRfcommSocketToServiceRecord(uuid);

            // This variable represents the number of attempts to connect to the device;
            int numberOfConnectionAttempts = 0;

            while (numberOfConnectionAttempts <= 5) {
                try {
                    mySocket.connect();
                    outputStream = mySocket.getOutputStream();
                    inputStream = mySocket.getInputStream();

                    socket = mySocket; // Assigning the created socket to the class variable
                    // Setting the Variables of RideVariables class.
                    RideVariables.DeviceConnected = true;
                    // Connection successful
                    /*
                    Update Notification.
                     */
                    builder.setContentText("Connected");
                    manager.notify(NotificationId,builder.build());
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "ConnectToSmartBike: Unable to connect " + e.toString());

                    // Adding a delay before the next connection attempt
                    try {
                        Thread.sleep(1000); // 1-second delay, can be adjusted as needed
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                numberOfConnectionAttempts++;
            }

        } catch (IOException e) {
            Log.d(TAG, "ConnectToSmartBike: unable to create socket  " + e.toString());
        }

        // Close the socket if it's not connected
        if (mySocket != null && mySocket.isConnected()) {
            try {
                mySocket.close();
            } catch (IOException e) {
                Log.e(TAG, "ConnectToSmartBike: Error closing Bluetooth socket", e);
            }
        }

        // Connection failed
        RideVariables.DeviceConnected = false; // Set to false if connection attempt fails
        return false;
    }


//    @SuppressLint("MissingPermission")
//    public boolean ConnectToSmartBike() {
//        // Getting the Bluetooth Adapter.
//        adapter = BluetoothAdapter.getDefaultAdapter();
//
//        // Check if the Adapter exists.
//        if (adapter == null) {
//            // Adapter Not found.
//            return false;
//        }
//
//        // Adapter Found, create a device and return it.
//        device = adapter.getRemoteDevice(MacAddress);
//
//        Log.d(TAG, "CreateDevice: device created");
//
//        // Creating a socket from the Device.
//        BluetoothSocket mySocket = null;
//
//        // Trying to create a Socket.
//        try {
//            mySocket = device.createRfcommSocketToServiceRecord(uuid);
//
//            // This variable represents the number of attempts to connect to the device;
//            int numberOfConnectionAttempts = 0;
//
//            while (numberOfConnectionAttempts <= 5 && !mySocket.isConnected()) {
//                try {
//                    mySocket.connect();
//                    outputStream = mySocket.getOutputStream();
//                    inputStream = mySocket.getInputStream();
//
//                    // Setting the Variables of RideVariables class.
//                    RideVariables.DeviceConnected = true;
//                    socket = mySocket; // Assigning the created socket to the class variable
//
//                    // Connection successful
//                    return true;
//                } catch (Exception e) {
//                    Log.e(TAG, "ConnectToSmartBike: Unable to connect " + e.toString());
//
//                    // Adding a delay before the next connection attempt
//                    try {
//                        Thread.sleep(1000); // 1-second delay, can be adjusted as needed
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                numberOfConnectionAttempts++;
//            }
//
//        } catch (IOException e) {
//            Log.d(TAG, "ConnectToSmartBike: unable to create socket  " + e.toString());
//        }
//
//        // Close the socket if it's not connected
//        if (mySocket != null && !mySocket.isConnected()) {
//            try {
//                mySocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "ConnectToSmartBike: Error closing Bluetooth socket", e);
//            }
//        }
//
//        // Connection failed
//        return false;
//    }
    /*
   This method will be called to connect to the bluetooth device.
    */
//    @SuppressLint("MissingPermission")
//    public void ConnectToSmartBike() {
//
//         /*
//        Getting the Bluetooth Adapter.
//         */
//
//        adapter=BluetoothAdapter.getDefaultAdapter();
//
//        /*
//        Check the Adapter is existing or not.
//         */
//
//        if(adapter==null){
//            /*
//            Adapter Not found.
//             */
//            return;
//        }
//        /*
//        Adapter Found and creating a device and returning it.
//         */
//
//       try{
//           device=adapter.getRemoteDevice(MacAddress);
//           Log.d(TAG, "ConnectToSmartBike: got device");
//       }
//       catch (Exception e){
//           Log.d(TAG, "ConnectToSmartBike: device not created"+e.toString());
//       }
//
//        Log.d(TAG, "CreateDevice: device created");
//
//        /*
//        This variable represents the number of attempts to connect to the device;
//         */
//        int NumberOfConnectionAttempts=0;
//
//        /*
//        Creating a socket from the Device.
//         */
//        BluetoothSocket mySocket=null;
//        /*
//        Trying to create a Socket from the .
//         */
//
//        try{
//            mySocket = device.createRfcommSocketToServiceRecord(uuid);
//            Log.d(TAG, "ConnectToSmartBike:  createed socket  ");
//        } catch (IOException e) {
//            Log.d(TAG, "ConnectToSmartBike: unable to create socket  "+e.toString());
//        }
//        socket=mySocket;
//        if(socket!=null){
//            do{
//                /*
//                Connecting to the device
//                 */
//                try {
//                    socket.connect();
//                   /*
//                   Getting the inputStream and outputStream.
//                    */
//                    outputStream=socket.getOutputStream();
//                    inputStream=socket.getInputStream();
//
//
//                }
//                catch (Exception e){
//                    Log.d(TAG, "ConnectToSmartBike: Unable to connect "+e.toString());
//                }
//
//            }while (NumberOfConnectionAttempts<=5&&!socket.isConnected());
//        }
//
//    }


    /*
        Closing the Socket of device.
    */
    public void closeBluetoothConnection() {
        try {
            if (socket != null) {
                socket.close();
                RideVariables.DeviceConnected=false;
                RideVariables.RideStarted = false;
                RideVariables.RidePaused = false;
                RideVariables.RideResumed = false;
            }
        } catch (IOException e) {
            Log.e(TAG, "closeBluetoothConnection: Error closing Bluetooth socket", e);
        }
    }

    /*
    Check if device is connected or not.
     */
    public boolean CheckIfConnected() {
        // Check if the BluetoothSocket is initialized
        if (socket != null) {
            // Check if the BluetoothSocket is connected
            if (socket.isConnected()) {
                // Your existing code for handling the connected state
                return true;
            } else {
                Log.d(TAG, "CheckIfConnected: not connected");
                return false;
            }
        } else {
            Log.d(TAG, "CheckIfConnected: socket null,try again");
            return false;
        }
    }

    /*
    method to check if sent first bit is sent or not;
     */
    public boolean SendFirstBit(){
        String message="FirstBit";
        try {
            outputStream.write(message.getBytes("UTF-8"));
            return  true;
        } catch (IOException e) {
            Log.d(TAG, "SendFirstBit: "+e.toString());
            return  false;
        }
    }

    /*
    This method will be used to set the value of the
     */
    public void setRideCallback(OngoingRideCallback rideCallback) {
        this.rideCallback = rideCallback;
    }

    /*
    Method to set the Notification Manager and Notification Builder.
     */
    public void SetNotificationObjects(NotificationManager manager,NotificationCompat.Builder builder){
        /*
        Setting the variables.
         */
        this.manager=manager;
        this.builder=builder;
    }

    /*
    Method to set the Context.
     */
    protected   void  SetContext(Context context){
        this.context=context;
    }
}


