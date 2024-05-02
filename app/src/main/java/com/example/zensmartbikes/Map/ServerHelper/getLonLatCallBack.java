package com.example.zensmartbikes.Map.ServerHelper;


/*
This interface will act as call back to get data from the server in background
 */
public interface getLonLatCallBack {
    /*
    On Success.
     */
    void onSuccess(LocationData data);
    void onFailure(String data);

}
