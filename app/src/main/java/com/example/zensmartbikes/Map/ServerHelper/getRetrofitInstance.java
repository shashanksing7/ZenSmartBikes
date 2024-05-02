package com.example.zensmartbikes.Map.ServerHelper;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
This class will be used to get the retrofit instance and make request to the server
for the data.
 */
public class getRetrofitInstance {

    /*
    Variables
     */
    private Retrofit retrofit;
    private  getDataService getDataService;
    private ScheduledExecutorService scheduler;

    /*
    creating the retrofit instance in constructor
     */
    public getRetrofitInstance(){

         /*
        Configuring okhttp client used by retrofit
         */
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS);

        /*
        Building the retrofit instance.
         */
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .baseUrl("http://aditya024.000webhostapp.com/")
                .build();


        /*
        Getting the instance of the service class to get the data from the server.
         */
        getDataService = retrofit.create(getDataService.class);

    }

    /*
    This method will periodically implement send the  network request.
     */

    public void startSendingRequests(getLonLatCallBack callBack) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getData("2024-04-17 07:25:45",callBack);
            }
        }, 0, 3500, TimeUnit.MILLISECONDS);
    }


    /*
    This method will be used to get the longitude and latitude data from the server.
     */
    public void getData(String datetime,getLonLatCallBack callBack){
        /*
        Creating the call object.
         */
        Call<LocationData> call= getDataService.getGPSData(datetime);
        /*
        Performing the network operation in background.
         */
        call.enqueue(new Callback<LocationData>() {
            @Override
            public void onResponse(Call<LocationData> call, Response<LocationData> response) {
                /*
                Checking if successful or not
                 */
                if(response.isSuccessful()){
                    /*
                    successful,return data
                     */
//                    Log.d("mytag2", "onResponse: "+response.body().getLongitude()+"  "+response.body().getHumidity());
                    callBack.onSuccess(response.body());
                }
                else {
                    /*
                    Failure
                     */
                    callBack.onFailure(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<LocationData> call, Throwable throwable) {
                  /*
                Failure.
                 */
                if (throwable instanceof IOException) {
                    // Handle timeout error here
                    System.err.println("Timeout occurred while communicating with the server.");
                    callBack.onFailure("Timeout occurred while communicating with the server.");
                } else {
                    callBack.onFailure("Failed");
                }
            }
        });

    }

    /*
    This class will be used to get the current time and date
     */
    public  String getLocalTimeStamp() {
        // Set the desired time zone
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Karachi");

        // Get current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Create a date object with the current time
        Date currentDate = new Date(currentTimeMillis);

        // Create a date format object for the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Set the time zone for the date format
        dateFormat.setTimeZone(timeZone);

        // Format the date to get the local timestamp
        return dateFormat.format(currentDate);
    }

}
