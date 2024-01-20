package com.example.zensmartbikes.Ride;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    /*
    Creating boolean Vlaues that will let the user know if the operation were successful or not.
     */
    private boolean RideStarted=false;
    private boolean RidePaused=false;
    private boolean RideResumed=false;
    private boolean RideStopped=false;



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
                StartRide(startRideMessage);
                break;

            case MESSAGE_RESUME_RIDE:
                String resumePauseRideMessage = (String) msg.obj;
                ResumeRide(resumePauseRideMessage);
                break;

            case MESSAGE_STOP_RIDE:
                String stopRideMessage = (String) msg.obj;
                StopRide(stopRideMessage);
                break;
            case MESSAGE_PAUSE_RIDE:
                String pauseRideMessage=(String) msg.obj;
                PauseRide(pauseRideMessage);
                break;
            case MESSAGE_CONNECT_DEVICE:
                ConnectToSmartBike();
                break;
            default:
                Log.d(TAG, "handleMessage: Unidentified call ");
                break;
        }

    }
    /*
    This method will be used to Start the Ride.
    */
    public void StartRide(String message) {
        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes("UTF-8"));
                RideStarted=true;
            } catch (IOException e) {
                Log.e(TAG, "startRide: Unable to start Ride", e);
            }
        } else {
            Log.e(TAG, "startRide: Output stream is null");
        }
    }

    /*
    This method will be used to Resume the ride.
    */
    public void ResumeRide(String message) {
        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes("UTF-8"));
                RideResumed=true;
            } catch (IOException e) {
                Log.e(TAG, "resumeRide: Unable to resume Ride", e);
            }
        } else {
            Log.e(TAG, "resumeRide: Output stream is null");
        }
    }

    /*
    This method will be used to Stop the ride.
    */
    public void StopRide(String message) {
        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes("UTF-8"));
                RideStopped=true;
                /*
                Setting the Variables of RideVariables class.
                 */
                
            } catch (IOException e) {
                Log.e(TAG, "stopRide: Unable to stop Ride", e);
            }
        } else {
            Log.e(TAG, "stopRide: Output stream is null");
        }
    }

    /*
    This method will be used to Pause the Ride.
    */
    public void PauseRide(String message) {
        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes("UTF-8"));
                RidePaused=true;
                /*
                Setting the Variables of RideVariables class.
                 */
                RideVariables.RidePaused=true;
            } catch (IOException e) {
                Log.e(TAG, "pauseRide: Unable to pause Ride", e);
            }
        } else {
            Log.e(TAG, "pauseRide: Output stream is null");
        }
    }


    /*
    This method will be called to connect to the bluetooth device.
     */
    @SuppressLint("MissingPermission")
    public void ConnectToSmartBike() {

         /*
        Getting the Bluetooth Adapter.
         */

        adapter=BluetoothAdapter.getDefaultAdapter();

        /*
        Check the Adapter is existing or not.
         */

        if(adapter==null){
            /*
            Adapter Not found.
             */
            return;
        }
        /*
        Adapter Found and creating a device and returning it.
         */

        device=adapter.getRemoteDevice(MacAddress);

        Log.d(TAG, "CreateDevice: device created");

        /*
        This variable represents the number of attempts to connect to the device;
         */
        int NumberOfConnectionAttempts=0;

        /*
        Creating a socket from the Device.
         */
        BluetoothSocket mySocket=null;
        /*
        Trying to create a Socket from the .
         */

        try{
            mySocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.d(TAG, "ConnectToSmartBike: unable to create socket  "+e.toString());
        }
        socket=mySocket;
        if(socket!=null){
            while (NumberOfConnectionAttempts <= 5 && !socket.isConnected()) {
                try {
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                    /*
                    Setting the Variables of RideVariables class.
                     */
                    RideVariables.DeviceConnected=true;
                } catch (Exception e) {
                    Log.e(TAG, "ConnectToSmartBike: Unable to connect " + e.toString());
                    /*
                    Adding a delay before the next connection attempt
                     */
                    try {
                        Thread.sleep(1000); // 1 second delay,can be  adjust as needed
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                NumberOfConnectionAttempts++;
            }

        }

    }

    /*
        Closing the Socket of device.
    */
    public void closeBluetoothConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "closeBluetoothConnection: Error closing Bluetooth socket", e);
        }
    }

    /*
    Check if device is connected or not.
     */
    public  boolean CheckIfConnected(){
        if(socket.isConnected()){
            return  true;
        }
        return  false;
    }

    /*
    Getter method for boolean variables
     */

    public boolean isRideStarted() {
        return RideStarted;
    }

    public boolean isRidePaused() {
        return RidePaused;
    }

    public boolean isRideResumed() {
        return RideResumed;
    }

    public boolean isRideStopped() {
        return RideStopped;
    }
}


