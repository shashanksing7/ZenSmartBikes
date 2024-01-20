package com.example.zensmartbikes.SplashScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zensmartbikes.MainActivity;
import com.example.zensmartbikes.R;
import com.example.zensmartbikes.databinding.ActivityEducationlaUiBinding;
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

/*
This Activity will be used to Educate the User and ask him/her to turn on the GPS.
 */
public class EducationlaUI extends AppCompatActivity {

    /*
    Creating the Object of the Binding class.
     */
    ActivityEducationlaUiBinding educationlaUiBinding;

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
        /*
        Inflating the Views of the Layout.
         */
        educationlaUiBinding=ActivityEducationlaUiBinding.inflate(getLayoutInflater());
        setContentView(educationlaUiBinding.getRoot());
        /*
        Initialising the object of the LocationRequest.
         */
        locationRequest= new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,TimeInterval)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true)
                .setMinUpdateDistanceMeters(MinimumDistance)
                .build();

        /*
        Adding click Listener to the Button.
         */
        educationlaUiBinding.educationaluibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Calling the checkGps() method.
                 */
                checkGPS(locationRequest);
            }
        });

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
                    Intent intent=new Intent(EducationlaUI.this, MainActivity.class);
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
                            resolvableApiException.startResolutionForResult(EducationlaUI.this,ResolutionRequestCode);
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

                Intent intent=new Intent(EducationlaUI.this, MainActivity.class);
                startActivity(intent);
                finish();
//                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

            }
            else {
                /*
                Failure,Redirect To Educational UI.
                 */

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }

        }
    }

}