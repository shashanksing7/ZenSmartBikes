package com.example.zensmartbikes.Map.ServerHelper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface getDataService {
    @GET("getdata.php")
    Call<LocationData> getGPSData(@Query("datetime")String datetime);
}
