package com.example.zensmartbikes.Ride;
/*
This interface will be used as call back to get data from the Handler to ride fragment.
 */
public interface OngoingRideCallback {
    /*
    This method will give the boolean value
     */
    public  void GetRideResult(boolean IsSuccessful);
}
