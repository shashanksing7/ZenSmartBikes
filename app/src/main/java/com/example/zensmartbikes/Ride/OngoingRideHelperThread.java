package com.example.zensmartbikes.Ride;


import android.os.Looper;

    /*
    This thread will be used with the OnGoingRideService to run task in background.
     */
public class OngoingRideHelperThread extends Thread{

    /*
    Creating Variable of the OnGoingRideThreadHandler.
     */
    public  OnGoingRideThreadHandler OnGoingRideThreadHandler;
    /*
    Attaching a handler to the Looper of the thread.
     */
    @Override
    public void run() {
        Looper.prepare();
        OnGoingRideThreadHandler=new OnGoingRideThreadHandler();
        Looper.loop();
    }
}
