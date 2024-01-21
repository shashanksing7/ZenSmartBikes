package com.example.zensmartbikes.SplashScreen;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.zensmartbikes.MainActivity;
import com.example.zensmartbikes.NotificationChannels.ZenSmartBikesNotificationVariables;
import com.example.zensmartbikes.NotificationChannels.ZenSmartBikesNotifications;
import com.example.zensmartbikes.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SplashScreen extends AppCompatActivity {

    /*
    Determine Hardware Availability of GPS in User's Smart Phone,
    If hardware is Available then Proceed
    to Check For permission,if not redirect user to educational UI.

     */

    /*
    this variable is used in the CheckLocPermission() method
     */
    private boolean PermissionAlreadyGranted=false;


    /*
    Creating a RequestCode Variable
     */
    private final int RequestCode=5000;


    private static final String TAG = "hellotag";

    /*
    Creating Object of the LocationRequest.
     */
    private LocationRequest locationRequest;

    /*
    Creating variables to define the property of the LocationRequest object.
     */
    private final int TimeInterval=2000;
    private  final int MinimumDistance=2;

    private  final  int ResolutionRequestCode=1000;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /*
        Checking if the Notifications channel is already created or not if yes then create it
         */

        CheckNotificationChannel();



        /*
        Initialising the object of the LocationRequest.
         */
        locationRequest= new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,TimeInterval)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true)
                .setMinUpdateDistanceMeters(MinimumDistance)
        .build();

              /*
        Checking if device has required hardware or not
         */
        if(getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)){

            //Call methods to check for permissions and request permission
            ReqLocPermission();

        }
        else {

            //Redirect user to Hardware not found UI
            Intent intent=new Intent(this, HardwareNotFound.class);
            startActivity(intent);
            finish();
           
        }
        
    }

    /*  Calling OnRequestPermissionResult() method to get user response to our request to permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permission Granted,Redirect User to Home Fragment
//                SharedPreferences preferences=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
//                boolean HasVisited=preferences.getBoolean(HAS_VISITED_B_KEY,false);
//                Intent intent=new Intent(this, MainActivity.class);
//                startActivity(intent);
//                finish();
                checkGPS(locationRequest);


            } else {

                /*
                permission Denied,Redirect Users to Educational UI
                and checking if view hierarchy has been created or not
                 */
//                Log.d(TAG, "onRequestPermissionsResult: published second time ");
                Intent intent=new Intent(this, EducationlaUI.class);
                startActivity(intent);
                finish();

            }

        }

    }
    /*
    This method will be used to check if the user has already granted permission or not if yes ,then it will
    direct the user to next screen,else it will request for permission.
     */
    private void ReqLocPermission() {
        /*
        checking if permission is already granted or not,if true redirect the user to new screen
        else request permission.
         */
        if(CheckLocRequestPermission()){

            /*
            Check if GPS is On or not ..
             */
            checkGPS(locationRequest);

        }
        else {
            /*
            request permission from user,show dialog box.
             */
            requestPermissions(new String[]{ACCESS_FINE_LOCATION},RequestCode);
        }

    }

    /*
    Creating a method to check if the user has already granted permission to the app or the user has blocked the app from using the
    GPS.this method will return the boolean value that will be uased to perfrom further operations.
     */
    public  boolean CheckLocRequestPermission(){

        /*
        Checking if permission has been already granted or not.
         */
        if(ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            PermissionAlreadyGranted=true;
        }
        else {

            /*
            checking if the users denied permission to the request or
            the request is being asked first time
            this will return TRue if user has denied permission but has not ticked "Don't ask again"
           ,it will return False if it's first ever request for app or the  user has
           denied permission and has  ticked "Don't ask again"
             */
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){

                /*
                 user has denied permission but has not ticked "Don't ask again"
                 Request
                 */
                PermissionAlreadyGranted=false;

            }
            else {

                /*

                it will return False if it's first ever request for app or the  user has
                  denied permission and has  ticked "Don't ask again"
                 */

                PermissionAlreadyGranted=false;

            }

        }
        return  PermissionAlreadyGranted;
    }

    /*
    The below method will be called to check if the device settings matched the Requested Location Settings or not if not then it will be
    display the pop up dialog and will store the result of the dialog with the user.
     */
    public void checkGPS(LocationRequest locationRequest){
        /*
        Creating the object of the class LocationSettingRequest that will be used to check if our requirement is matched by device or not.
         */
        LocationSettingsRequest builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        /*
        Calling the LocationServices.getSettingsClient(context) method which will return the
         SettingsClient object to check the setting of the device and match it with our requirement.
         */
        Task<LocationSettingsResponse> locationSettingsResponse= LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response=task.getResult(ApiException.class);
                    /*
                    GPS is on Redirect User to another screen
                     */
                    Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
//                    Toast.makeText(SplashScreen.this, "already", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {

                    /*
                    the provided code snippet, it appears that there's a check for location settings satisfaction, and
                     if the settings are not satisfied, a ResolvableApiException is thrown. This exception indicates that
                     the current state of location settings is not suitable for the requested operation, and it can be
                     resolved by initiating an action, typically prompting the user to adjust their device's settings.
                     */
                    if(e.getStatusCode()== LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                        ResolvableApiException resolvableApiException=(ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(SplashScreen.this,ResolutionRequestCode);
                        } catch (IntentSender.SendIntentException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    /*
                     indicating that the required location settings change is not possible or unavailable.
                     */
                    if(e.getStatusCode()==LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){
                        /*
                        goto Hardware not found UI.
                         */
                    }
                }

            }
        });

    }

         /*
             To handle the result of the resolution initiated by startResolutionForResult,
             you need to override the onActivityResult method in your SplashScreen activity.
             The onActivityResult method is called when the resolution activity is finished,
             and it provides the result of the user's interaction with the resolution dialog.
         */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        Handling Success and Failure
         */
        if(requestCode==ResolutionRequestCode){

            /*
            Checking if the Resolution was success or failure.
             */
            if(resultCode== Activity.RESULT_OK){

                /*
                Success, Redirect User to Home Screen.
                 */

                Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
//                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

            }
            else {
                /*
                Failure,Redirect To Educational UI.
                 */
                Intent intent=new Intent(this,EducationlaUI.class);
                startActivity(intent);
                finish();

//                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
    Method to check and create Notification Channel.
     */
    public void CheckNotificationChannel(){
        /*
        Checking if the Notifications channel is already created or not if yes then create it
         */
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(ZenSmartBikesNotificationVariables.rideChannelId)==null){
                ZenSmartBikesNotifications smartBikesNotifications=new ZenSmartBikesNotifications(this);
                smartBikesNotifications.CreateRideNotificationChannel();
            }
        }
    }
}