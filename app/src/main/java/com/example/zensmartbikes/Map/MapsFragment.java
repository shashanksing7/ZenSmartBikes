package com.example.zensmartbikes.Map;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.zensmartbikes.Map.ServerHelper.LocationData;
import com.example.zensmartbikes.Map.ServerHelper.getLonLatCallBack;
import com.example.zensmartbikes.Map.ServerHelper.getRetrofitInstance;
import com.example.zensmartbikes.databinding.FragmentMapsBinding;
import com.mapbox.maps.MapboxMap;



/*
This fragment will be used to show tha mao to the user
 */

public class MapsFragment extends Fragment {


    /*
    Variable to store the viewBinding class object
     */
    private FragmentMapsBinding binding;
    private MapboxMap mapboxMap;
    private getRetrofitInstance instance;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String HAS_VISITED_B_KEY = "hasVisitedB";
    private  int animationNumber=1;
    private  boolean alreadyEnabled =false;
    private int numberOfServerResponse=0;


    private  AnimationHelper helper;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper=new AnimationHelper();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMapsBinding.inflate(inflater);

        /*
        getting the last know location
         */
        LatLng lng=getLastLocation();
        helper.setVariables(binding.mapView,lng.getLongitude(), lng.getLatitude(), requireContext());
        /*
        Disabling and Hiding the currentLocation button
         */

        if(!alreadyEnabled){
            Log.d("mytag", "onCreateView:diabled ");
            binding.currentlocation.setEnabled(false);
            binding.currentlocation.setVisibility(View.GONE);
        }
      /*
      Adding listener to the myLocation button and calling updateCentreCameraOnBike of helper and
      updating the  centreCameraOnBike variable.
       */

        binding.currentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.updateCentreCameraOnBike();

            }
        });

       instance=new getRetrofitInstance();

       instance.startSendingRequests( new getLonLatCallBack() {
          @Override
            public void onSuccess(LocationData data) {

              Log.d("mytag", "data got: "+data.getLongitude()+" ++"+data.getLatitude());
              /*
              Checking if the currentLocation button is Enabled or not and calling th function to enable it.
               */
              if(!alreadyEnabled){
                  enableCurrentLocationButton();
              }
              /*
              Incrementing the numberOfServerResponse.
               */
              numberOfServerResponse++;
              /*
              Checking if map is loaded or not,if yes start animating.
               */
              if(helper.checkIfMapLoaded()){
                  /*
                  call the animate method to start animation.
                   */
                  if ((getLastLocation().getLatitude()== Double.parseDouble(data.getLatitude())&&(getLastLocation().getLongitude()== Double.parseDouble(data.getLongitude())))){
                      /*
                      Both point same no need for animation,skip
                       */
                      Log.d("mytag", "points same ");

                  }
                  else{
                      /*
                      Points not same animating.
                       */
                      Log.d("mytag", "onSuccess: entered else block");
                      if(helper.checkIfFirstAnimationCompleted()||animationNumber==1){
                          animationNumber++;
                          helper.animate( Double.parseDouble(data.getLongitude()), Double.parseDouble(data.getLatitude()));
                          Log.d("mytag", "onSuccess: callin animate");
                      }
                      else {
                          /*
                          calling to enable to attempt the first animation again.
                           */
                          reRunAnimation();
                      }

                  }

              }

              /*
              Updating the last location of bikes in shared preferences .
               */
              saveLastLocation( Double.parseDouble(data.getLongitude()), Double.parseDouble(data.getLatitude()));
            }


            @Override
            public void onFailure(String data) {
                Log.d("mytag", "onFailure: "+data);
                /*
              Checking if the currentLocation button is Enabled or not and calling th function to enable it.
               */
                if(!alreadyEnabled){
                    enableCurrentLocationButton();
                }
            }
     });

     return binding.getRoot();


    }

    /*
    Method to save  the shared preferences.
     */
    private void saveLastLocation(double lng,double lat){

        SharedPreferences.Editor editor=requireContext().getSharedPreferences("location_data", MODE_PRIVATE).edit();
        editor.putString("longitude",String.valueOf(lng));
        editor.putString("latitude",String.valueOf(lat));
        editor.apply();
    }

    /*
 Method to get the shared preferences and return latitude and longitude as LatLng object.
 */
    private LatLng getLastLocation() {
        SharedPreferences prefs = requireContext().getSharedPreferences("location_data",MODE_PRIVATE);
        String latitudeStr = prefs.getString("latitude", "");
        String longitudeStr = prefs.getString("longitude", "");

        double latitude = 0.0;
        double longitude = 0.0;

        if (!latitudeStr.isEmpty() && !longitudeStr.isEmpty()) {
            try {
                latitude = Double.parseDouble(latitudeStr);
                longitude = Double.parseDouble(longitudeStr);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Handle parsing error, if needed
            }
        }

        return new LatLng(latitude, longitude);
    }

    private void enableCurrentLocationButton(){
        if(helper.checkIfFirstAnimationCompleted()){
            Log.d("mytag", "enableCurrentLocationButton: ");
            binding.currentlocation.setEnabled(true);
            binding.currentlocation.setVisibility(View.VISIBLE);
            alreadyEnabled=true;
        }
    }

    /*
    This method will check if the animation has completed or not if not the it will start new animation again.
     */

    private  void  reRunAnimation(){
        if(numberOfServerResponse>2&&!helper.checkIfFirstAnimationCompleted()){
            animationNumber=1;
            numberOfServerResponse=0;
            Log.d("mytag", "reRunAnimation: ");
        }
    }
}