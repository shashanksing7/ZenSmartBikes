package com.example.zensmartbikes.Ride;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zensmartbikes.NotificationChannels.ZenNotificationsBuilder;
import com.example.zensmartbikes.NotificationChannels.ZenSmartBikesNotificationVariables;
import com.example.zensmartbikes.SplashScreen.HardwareNotFound;
import com.example.zensmartbikes.databinding.FragmentRideBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class RideFragment extends Fragment {

    /*
    Creating the Object of the Binding class of the Binding class for this fragment.
     */
    FragmentRideBinding rideBinding;

    /*
    Creating the  object of the  Service connection class that will be used to establish connection with our service.
     */
    private ServiceConnection serviceConnection;

    /*
    Object of the Background service that will be used to perform task in our app.
     */
   private  OnGoingRideService onGoingRideService;

   /*
   Creating the object of the bluetooth adapter.
    */
    private BluetoothAdapter adapter;
    private FirebaseFirestore firestore;
    private static final String TAG = "mytag";
    String UUID;

    /*
    for bluetooth permission.
     */
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String ACTION_PERMISSIONS_GRANTED = "your_package_name.ACTION_PERMISSIONS_GRANTED";
    private static final String ACTION_PERMISSIONS_DENIED = "your_package_name.ACTION_PERMISSIONS_DENIED";

    /*
    Creating Object of the ActivityResultLauncher object.
     */
    private ActivityResultLauncher<Intent>resultLauncher;
        /*
    These are the Variables that will be used for oue notification.
     */
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
       /*
       Notification Id.
      */
    private final int NotificationId=1;

    public RideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         Inflate the layout for this fragment.
         */
        rideBinding=FragmentRideBinding.inflate(inflater,container,false);

        rideBinding.ConnectButton.setEnabled(false);
        // Initialize the Bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        UUID=currentUser.getUid();
        /*
        Getting the method that build the notification for ongoing ride
         */
        builder=new ZenNotificationsBuilder().getRideNotificationBuilder(getContext(), ZenSmartBikesNotificationVariables.rideChannelId);
        /*
        Getting the Notifications Manager.
         */
        manager =(NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);

        /*
        Initializing the  ServiceConnection object.
         */
        serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                /*
                Service Connected getting the object of the Binder class then finally getting the object of the Service.
                 */

                OnGoingRideService.OnGoingRideServicBinder rideServicBinder=(OnGoingRideService.OnGoingRideServicBinder) service;
                onGoingRideService=rideServicBinder.getOnGoingRideService();
                 /*
                    Starting the service if it's not already started;
                 */
                if(!RideVariables.RideServiceStarted){
                    if(onGoingRideService!=null){
                        Intent intent=new Intent(getContext(),OnGoingRideService.class);
                        if(!RideVariables.RideServiceStarted){
                            RideVariables.RideServiceStarted=true;
                            getContext().startService(intent);
                        }
                    }
                    else{
                        Toast.makeText(onGoingRideService, "Restart Mobile App", Toast.LENGTH_SHORT).show();
                    }
                }
//                              /*
//                        Disabling buttons initially,Calling the methods to check the buttons.
//                         */
//                CheckButtons();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                /*
                Service Disconnected.
                 */

            }
        };

        /*
        Calling the Function to initialize the object of ActivityResultLauncher<Intent>.
         */
        resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode()== Activity.RESULT_OK){
                    /*
                    Permission Granted,Bluetooth opened.
                     */
                                    /*
                if adapter is enabled proceed with next task.
                 */
                    bindToService();
                    rideBinding.ConnectButton.setEnabled(true);
                }
                else{
                    /*
                    Bluetooth Off.
                     */
                    rideBinding.ConnectButton.setEnabled(false);
                    Toast.makeText(getContext(), "Turn on BlueTooth and Restart App", Toast.LENGTH_SHORT).show();
                }

            }
        });

                /*
        Request bluetooth permission.
         */
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {

            /*
            permission granted.
             */

                  /*
           Method to check if Bluetooth is enabled or not
            */
            isAdapterEnabled();
            Log.d(TAG, "onCreateView: permission garnted");
        } else {
           requestPermission();
        }

        /*
        Adding listener to the Connect button.
         */
        rideBinding.ConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rideBinding.ConnectButton.setEnabled(false);
                ConnectToDevice();
            }
        });
        /*
             Adding listener to the Start ride button.
         */
        rideBinding.StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideBinding.StartButton.setEnabled(false);
                StartRide();

            }
        });

        /*
           Adding listener to the Pause/Resume ride button.
         */
        rideBinding.PauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PauseResume();
            }
        });

        /*
            Adding listener to Stop ride button.
         */
        rideBinding.StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideBinding.StopButton.setEnabled(false);
                StopRide();
            }
        });

        return rideBinding.getRoot();

    }

    private void isAdapterEnabled() {
         /*
        Checking if bluetooth adapter is available or not.if not redirect to new  educative ui.
         */
        if(adapter!=null){
            /*
            adapter available  request permission.
             */
            if(adapter.isEnabled()){
                /*
                if adapter is enabled proceed with next task.
                 */
                bindToService();
                rideBinding.ConnectButton.setEnabled(true);

            }
            else {
                /*
                Request to enable Bluetooth adapter.
                 */
                rideBinding.ConnectButton.setEnabled(false);
                RequestBluetooth();
            }

        }
        else{
            /*
            Adapter not available redirect to educative UI.
             */
            Intent intent=new Intent(getActivity(), HardwareNotFound.class);
            startActivity(intent);

        }
    }

    /*
    This method will request the Bluetooth permission for bluetooth.
     */
    private  void RequestBluetooth(){
        /*
        Intent to request the Enable bluetooth.
         */
        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        resultLauncher.launch(intent);
    }

    /*
    Utility method to check if a service is running.
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    /*
   Method to manipulate the Buttons.
  */
    private void CheckButtons() {
        // If the ride is resumed, set the text of the Pause/Resume button to "Pause"
        if (RideVariables.RideResumed&&!RideVariables.RidePaused) {
            rideBinding.PauseResumeButton.setText("Pause");
        }
        // If the ride is paused, set the text of the Pause/Resume button to "Resume"
        else if (RideVariables.RidePaused&&!RideVariables.RideResumed) {
            rideBinding.PauseResumeButton.setText("Resume");
        }
        // If the ride has not started, disable the Pause/Resume and Stop buttons
        if (!RideVariables.RideStarted) {
            rideBinding.PauseResumeButton.setEnabled(false);
            rideBinding.StopButton.setEnabled(false);

            // If the device is not connected, enable the Connect button and disable the Start button
            if (!RideVariables.DeviceConnected) {
                rideBinding.ConnectButton.setEnabled(true);
                rideBinding.StartButton.setEnabled(false);
            } else {
                // If the device is connected, disable the Start button and Connect button
                rideBinding.StartButton.setEnabled(false);
                rideBinding.ConnectButton.setEnabled(false);
            }
        } else {
            // If the ride has started, disable the Start button and Connect button
            rideBinding.StartButton.setEnabled(false);
            rideBinding.ConnectButton.setEnabled(false);
        }

        if(RideVariables.DeviceConnected&&!RideVariables.RideStarted){
            rideBinding.StartButton.setEnabled(true);
        }
    }
    /*
     Method to bind to the service.
     */
    private void bindToService() {
        Intent intent = new Intent(getContext(), OnGoingRideService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    /*
    Unbinding the service in onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serviceConnection!=null){
            getContext().unbindService(serviceConnection);
        }

        // Stop the service if it has started and the device is not connected
        if (RideVariables.RideServiceStarted && onGoingRideService != null && !onGoingRideService.IsConnected()) {
            if(onGoingRideService!=null){
                stopService();
                onGoingRideService=null;
            }
        }
    }
      /*
      This method will be used to Connect to the Smart Device ,it wll call The HandlerConnectMethod of service;
      */
//    public void ConnectToDevice(){
//        /*
//        Starting the service if it's not already started;
//         */
//        if(!isServiceRunning(OnGoingRideService.class)){
//            if(onGoingRideService!=null){
//                Intent intent=new Intent(getContext(),OnGoingRideService.class);
//                getContext().startService(intent);
//            }
//        }
//    /*
//    Calling the HandlerConnectMethod() from service.
//    */
//        if(serviceConnection != null && onGoingRideService != null) {
//            onGoingRideService.HandlerConnectMethod(new OngoingRideCallback() {
//                @Override
//                public void GetRideResult(boolean IsSuccessful) {
//                    /*
//                        If Device is connected it will disable the connect button else it will enable the connect button.
//                     */
//                    Log.d("mytag", "GetRideResult: callback");
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (IsSuccessful) {
//                                if (onGoingRideService.CheckFirstBit()) {
//                                    rideBinding.ConnectButton.setEnabled(false);
//                                    rideBinding.StartButton.setEnabled(true);
//                                    Log.d("mytag", "ConnectToDevice: setting start enabled");
//                                } else {
//                                    try {
//                                        Thread.sleep(1000);
//                                    } catch (Exception e) {
//                                        Log.d("mytag", "ConnectToDevice: " + e.toString());
//                                    }
//                                    rideBinding.ConnectButton.setEnabled(true);
//                                    rideBinding.StartButton.setEnabled(false);
//                                    Log.d("mytag", "ConnectToDevice: setting start enabled");
//                                }
//                            } else {
//                                rideBinding.ConnectButton.setEnabled(true);
//                                Log.d("mytag", "not connected: ");
//                            }
//                        }
//                    });
//                }
//            });
//        }
//        else {
//            rideBinding.ConnectButton.setEnabled(true);
//            Toast.makeText(getContext(), "Wait for service to start", Toast.LENGTH_SHORT).show();
//        }
//    }
    public void ConnectToDevice() {
        // Show progress dialog
        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "connecting...", true);
        // Starting the service if it's not already started
        if (!isServiceRunning(OnGoingRideService.class)) {
            Intent intent = new Intent(getContext(), OnGoingRideService.class);
            getContext().startService(intent);
        }

        // Calling the HandlerConnectMethod() from the service
        if (serviceConnection != null && onGoingRideService != null) {

            onGoingRideService.HandlerConnectMethod(new OngoingRideCallback() {

                @Override
                public void GetRideResult(boolean IsSuccessful) {
                    // If the device is connected, it will disable the connect button; otherwise, enable the connect button
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (IsSuccessful) {
                                if (onGoingRideService.CheckFirstBit()) {
                                    rideBinding.ConnectButton.setEnabled(false);
                                    rideBinding.StartButton.setEnabled(true);
                                    progressDialog.dismiss();
                                    Log.d("mytag", "ConnectToDevice: setting start enabled");
                                } else {
                                    progressDialog.dismiss();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        Log.d("mytag", "ConnectToDevice: " + e.toString());
                                        Thread.currentThread().interrupt();
                                    }

                                    rideBinding.ConnectButton.setEnabled(true);
                                    rideBinding.StartButton.setEnabled(false);
                                    Log.d("mytag", "ConnectToDevice: setting start enabled");
                                }
                            } else {
                                Toast.makeText(getContext(), "Couldn't Connect", Toast.LENGTH_SHORT).show();
                                rideBinding.ConnectButton.setEnabled(true);
                                Log.d("mytag", "not connected: ");
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });
        } else {
            rideBinding.ConnectButton.setEnabled(true);
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Wait for the service to start", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    This method will be used to start Ride of the Smart Device ,it wll call The StartRideHandlerMethod() of service;
     */
    public void StartRide(){

        if(serviceConnection!=null){
            onGoingRideService.StartRideHandlerMethod(new OngoingRideCallback() {
                @Override
                public void GetRideResult(boolean IsSuccessful) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                /*
                                If Device is connected it will disable the connect button else it will enable the connect button.
                                 */
                            if(IsSuccessful){

                                rideBinding.StartButton.setEnabled(false);
                                rideBinding.PauseResumeButton.setEnabled(true);
                                rideBinding.StopButton.setEnabled(true);
                                updateRidesField(UUID,1);
                            }
                            else {
                                /*
                                   Checking Reason and enabling button.
                                    */
                                if(!onGoingRideService.IsConnected()){
                                       /*
                                       Disconnected,stop service,Manipulate buttons.
                                        */
                                    Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                                    rideBinding.StartButton.setEnabled(false);
                                    stopService();
                                    DisconnectedInMiddle();
                                }
                                else{
                                    /*
                                    enable button
                                     */
                                    rideBinding.StartButton.setEnabled(true);
                                }

                            }

                        }
                    });
                }
            });

        }
        else{
            rideBinding.StartButton.setEnabled(true);
            Toast.makeText(getContext(), "Could not Start,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    This method will be used to Pause/Resume Ride of the Smart Device ,it wll call The StartRideHandlerMethod() of service;
     */
    public void PauseResume(){

        /*
        Getting the text of the button and calling the method from service respectively;
         */
        String PauseResumeId=rideBinding.PauseResumeButton.getText().toString();
        if(PauseResumeId.equals("Pause")){

            if(serviceConnection!=null){
                /*
                Disabling the button.
                 */
                rideBinding.PauseResumeButton.setEnabled(false);
               onGoingRideService.PauseRideHandlerMethod(new OngoingRideCallback() {
                   @Override
                   public void GetRideResult(boolean IsSuccessful) {
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               if(IsSuccessful){
                                   /*
                                   Enabling button and changing text;
                                    */
                                   rideBinding.PauseResumeButton.setText("Resume");
                                   rideBinding.PauseResumeButton.setEnabled(true);
                               }
                               else {
                                   /*
                                   Checking Reason and enabling button.
                                    */
                                   if(!onGoingRideService.IsConnected()){
                                       /*
                                       Disconnected,stop service,Manipulate buttons.
                                        */
                                       Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                                       rideBinding.PauseResumeButton.setEnabled(false);
                                       stopService();
                                       DisconnectedInMiddle();
                                   }
                                   else{
                                       rideBinding.PauseResumeButton.setEnabled(true);
                                   }
                               }
                           }
                       });
                   }
               });

            }
            else{
                Toast.makeText(getContext(), "Could not Pause,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
            }

        }
        else{

            if(serviceConnection!=null){
                /*
                Disabling the button.
                 */
                rideBinding.PauseResumeButton.setEnabled(false);
                onGoingRideService.ResumeRideHandlerMethod(new OngoingRideCallback() {
                    @Override
                    public void GetRideResult(boolean IsSuccessful) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(IsSuccessful){
                                   /*
                                   Enabling button and changing text;
                                    */
                                    rideBinding.PauseResumeButton.setText("Pause");
                                    rideBinding.PauseResumeButton.setEnabled(true);

                                }
                                else {
                                    /*
                                   Checking Reason and enabling button.
                                    */
                                    if(!onGoingRideService.IsConnected()){
                                       /*
                                       Disconnected,stop service,Manipulate buttons.
                                        */
                                        Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                                        rideBinding.PauseResumeButton.setEnabled(false);
                                        stopService();
                                        DisconnectedInMiddle();
                                    }
                                    else{
                                        rideBinding.PauseResumeButton.setEnabled(true);
                                    }
                                }
                            }
                        });
                    }
                });
            }
            else{
                Toast.makeText(getContext(), "Could not Resume,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
        This method will be used to Stop Ride of the Smart Device ,it wll call The StartRideHandlerMethod() of service;
     */
    public void  StopRide(){

        if(serviceConnection!=null){
            /*
                Disabling the button.
             */
            rideBinding.PauseResumeButton.setEnabled(false);
            onGoingRideService.StopRideHandlerMethod(new OngoingRideCallback() {
                @Override
                public void GetRideResult(boolean IsSuccessful) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(IsSuccessful){
                                /*
                                Navigate to the home fragment.
                                 */
                                if(ServiceStopped()){
                                    CheckButtons();
                                    builder.setAutoCancel(true);
                                    manager.notify(NotificationId,builder.build());
                                }
                            }
                            else {
                                /*
                                   Checking Reason and enabling button.
                                    */
                                if(!onGoingRideService.IsConnected()){
                                       /*
                                       Disconnected,stop service,Manipulate buttons.
                                        */
                                    rideBinding.StopButton.setEnabled(false);
                                    Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                                    stopService();
                                    DisconnectedInMiddle();

                                }
                                else{
                                    rideBinding.StopButton.setEnabled(true);
                                }
                            }
                        }
                    });
                }
            });

        }
        else{
            rideBinding.PauseResumeButton.setEnabled(true);
            Toast.makeText(getContext(), "Could not Stop,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    method to stop service.
     */
    public boolean ServiceStopped(){
        try {
            onGoingRideService.stopForegroundService();
            onGoingRideService.stopSelf();
            RideVariables.RideServiceStarted=false;
            return  true;
        }
        catch (Exception e){
            Log.d("mytag", "ServiceStopped: "+e.toString());
            return  false;
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        CheckButtons();
    }
    /*
    Stop the service.
     */
    private void stopService() {
        if (onGoingRideService != null) {
            // Stop the service
            onGoingRideService.stopForegroundService();
            onGoingRideService.stopSelf();
            RideVariables.RideServiceStarted = false;

        }
    }

    /*
    Method will be executed when the device gets disconnected  in middle.
     */
    private void DisconnectedInMiddle(){

        /*
        Setting the variables.
         */
        RideVariables.DeviceConnected=false;
        RideVariables.RideServiceStarted=false;
        RideVariables.RidePaused=false;
        RideVariables.RideResumed=false;
        /*
        Calling the Methods to manipulate buttons.
         */
        CheckButtons();
    }


    // Method to update the number of rides
    private void updateRidesField(String UUID, int ridesToAdd) {
        // Get the reference to the user's profile document
        DocumentReference userRef = firestore.collection("Rider").document(UUID);

        // Retrieve the current value of the 'Rides' field
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get the current number of rides
                    int currentRides = documentSnapshot.getLong("Rides").intValue();

                    // Update the 'Rides' field with the new value
                    userRef.update("Rides", currentRides + ridesToAdd)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Handle successful update
                                    Log.d(TAG, "Number of rides updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failed update
                                    Log.w(TAG, "Error updating number of rides", e);
                                }
                            });
                } else {
                    Log.d(TAG, "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to retrieve document
                Log.w(TAG, "Error getting document", e);
            }
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        Log.d(TAG, "onRequestPermissionsResult: ");
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                isAdapterEnabled();
//
//            } else {
//                // Permission denied
//                Toast.makeText(getContext(), "Bluetooth permission denied. Please grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    /*
    Request permission for foreground services type.
     */
    private  void requestPermission() {
//        /*
//        Using dexter to get permission.
//         */
//        Log.d(TAG, "requestForeGroundType: ");
//        Dexter.withContext(getContext())
//                .withPermissions(Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE,Manifest.permission.BLUETOOTH_CONNECT)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                        isAdapterEnabled();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
//    }
    }
}