package com.example.zensmartbikes.Ride;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


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
                return true;
            } catch (IOException e) {
                Log.e(TAG, "StartRide: Unable to start Ride", e);
                // Return false if there is an exception while writing to the outputStream
                return false;
            }
        } else {
            Log.e(TAG, "StartRide: Output stream is null");
            // Return false if outputStream is null
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
                return true; // Return true on success
            } catch (IOException e) {
                Log.e(TAG, "ResumeRide: Unable to resume Ride", e);
                return false; // Return false if there is an exception while writing to the outputStream
            }
        } else {
            Log.e(TAG, "ResumeRide: Output stream is null");
            return false; // Return false if outputStream is null
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
                RideVariables.RideStarted = false;
                RideVariables.RidePaused = false;
                RideVariables.RideResumed = false;
                closeBluetoothConnection();
                return true; // Return true on success
            } catch (IOException e) {
                Log.e(TAG, "StopRide: Unable to stop Ride", e);
                return false; // Return false if there is an exception while writing to the outputStream
            }
        } else {
            Log.e(TAG, "StopRide: Output stream is null");
            return false; // Return false if outputStream is null
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
                return true; // Return true on success
            } catch (IOException e) {
                Log.e(TAG, "PauseRide: Unable to pause Ride", e);
                return false; // Return false if there is an exception while writing to the outputStream
            }
        } else {
            Log.e(TAG, "PauseRide: Output stream is null");
            return false; // Return false if outputStream is null
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

            while (numberOfConnectionAttempts <= 5 && !mySocket.isConnected()) {
                try {
                    mySocket.connect();
                    outputStream = mySocket.getOutputStream();
                    inputStream = mySocket.getInputStream();

                    // Setting the Variables of RideVariables class.
                    RideVariables.DeviceConnected = true;
                    socket = mySocket; // Assigning the created socket to the class variable

                    // Connection successful
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
        if (mySocket != null && !mySocket.isConnected()) {
            try {
                mySocket.close();
            } catch (IOException e) {
                Log.e(TAG, "ConnectToSmartBike: Error closing Bluetooth socket", e);
            }
        }

        // Connection failed
        return false;
    }
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

}


