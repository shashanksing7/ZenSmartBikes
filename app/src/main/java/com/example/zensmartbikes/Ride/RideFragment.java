package com.example.zensmartbikes.Ride;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.SplashScreen.HardwareNotFound;
import com.example.zensmartbikes.databinding.FragmentRideBinding;

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

    /*
    Creating Object of the ActivityResultLauncher object.
     */
    private ActivityResultLauncher<Intent>resultLauncher;

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

        // Initialize the Bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();
        /*
        Disabling buttons initially,Calling the methods to check the buttons.
         */
        CheckButtons();

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
                If service is not running then start it.
                 */
                if(!isServiceRunning(OnGoingRideService.class)){
                    Intent intent=new Intent(getContext(),OnGoingRideService.class);
                    getContext().startService(intent);

                }

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
                    rideBinding.ConnectButton.setEnabled(true);
                }
                else{
                    /*
                    Bluetooth Off.
                     */
                    rideBinding.ConnectButton.setEnabled(false);
                    Toast.makeText(onGoingRideService, "Turn on BlueTooth and Restart App", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                rideBinding.PauseResumeButton.setEnabled(false);
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
    /*
    Checking buttons and disabling them.
    */

        // If the ride is resumed, set the text of the Pause/Resume button to "Pause"
        if (RideVariables.RideResumed) {
            rideBinding.PauseResumeButton.setText("Pause");
        }
        // If the ride is paused, set the text of the Pause/Resume button to "Resume"
        else if (RideVariables.RidePaused) {
            rideBinding.PauseResumeButton.setText("Resume");
        }

        // If the ride has not started, disable the Pause/Resume and Stop buttons
        if (!RideVariables.RideStarted) {
            rideBinding.PauseResumeButton.setEnabled(false);
            rideBinding.StopButton.setEnabled(false);
        } else {
            // If the ride has started, disable the Start button
            rideBinding.StartButton.setEnabled(false);
        }

        // If the device is not connected, disable the Start button and Connect button
        if (!RideVariables.DeviceConnected) {
            rideBinding.StartButton.setEnabled(false);
        }
        else {
            rideBinding.ConnectButton.setEnabled(false);
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

    }
      /*
      This method will be used to Connect to the Smart Device ,it wll call The HandlerConnectMethod of service;
      */
    public void ConnectToDevice(){

        /*
        this variable will tell if the device is connected or not.
         */
        boolean IsConnected=false;
        /*
           Calling the HandlerConnectMethod() from service.
         */
        if(serviceConnection!=null){
           IsConnected= onGoingRideService.HandlerConnectMethod();
           /*
           If Device is connected it will disable the connect button else it will enable the connect button.
            */
           if(IsConnected){
               rideBinding.ConnectButton.setEnabled(false);
               rideBinding.StartButton.setEnabled(true);
           }
           else {
               rideBinding.ConnectButton.setEnabled(true);
           }
        }else {
            Toast.makeText(onGoingRideService, "wait for service to start", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    This method will be used to start Ride of the Smart Device ,it wll call The StartRideHandlerMethod() of service;
     */
    public void StartRide(){

        /*
        this variable will tell if the device is connected or not.
         */
        boolean IsRideStarted=false;
        if(serviceConnection!=null){
            IsRideStarted=onGoingRideService.StartRideHandlerMethod();
            /*
           If Device is connected it will disable the connect button else it will enable the connect button.
            */
            if(IsRideStarted){
                rideBinding.StartButton.setEnabled(false);
                rideBinding.PauseResumeButton.setEnabled(true);
                rideBinding.StopButton.setEnabled(true);
            }
            else {
                rideBinding.StartButton.setEnabled(true);
            }
        }
        else{
            Toast.makeText(onGoingRideService, "Could not Start,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
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
         /*
        this variable will tell if the ride is pwaused or not.
         */
            boolean IsRidePaused=false;
            if(serviceConnection!=null){

                IsRidePaused=onGoingRideService.PauseRideHandlerMethod();
                if(IsRidePaused){
                    rideBinding.PauseResumeButton.setText("Resume");

                }


            }
            else{
                Toast.makeText(onGoingRideService, "Could not Pause,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
            }


        }
        else{

            /*
        this variable will tell if the ride is Resumed or not.
         */
            boolean IsRideResumed=false;
            if(serviceConnection!=null){
                IsRideResumed=onGoingRideService.ResumeRideHandlerMethod();
                if(IsRideResumed){
                    rideBinding.PauseResumeButton.setText("Pause");
                }

            }
            else{
                Toast.makeText(onGoingRideService, "Could not Resume,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
            }

        }

    }

    /*
        This method will be used to Stop Ride of the Smart Device ,it wll call The StartRideHandlerMethod() of service;
     */

    public void  StopRide(){
        /*
        this variable will tell if the ride is Resumed or not.
         */
        boolean IsRideSTopped=false;
        if(serviceConnection!=null){
            IsRideSTopped=onGoingRideService.StopRideHandlerMethod();
            if(IsRideSTopped){
                /*
                Navigate to the home fragment.
                 */
                CheckButtons();

            }
            else {
                rideBinding.StopButton.setEnabled(true);
            }
        }
        else{
            Toast.makeText(onGoingRideService, "Could not Stop,the Ride  Check Connection", Toast.LENGTH_SHORT).show();
        }
    }
}