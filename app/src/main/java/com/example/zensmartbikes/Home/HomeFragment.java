package com.example.zensmartbikes.Home;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.Ride.OnGoingRideService;
import com.example.zensmartbikes.databinding.FragmentHomeBinding;

/*
This fragment will be used as Home Fragment for the app .
 */
public class HomeFragment extends Fragment {


    /*
    Creating the Object of the binding class created.
     */
    FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        Inflating the view of the layout file.
         */
        binding=FragmentHomeBinding.inflate(inflater,container,false);

        /*
        Checking if service is running,if yes taking user to ride fragment.
         */
//        if(isServiceRunning(OnGoingRideService.class)){
////            NavController controller=Navigation.findNavController(binding.getRoot());
////            controller.navigate(R.id.rideFragment);
//            NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//            controller.navigate(R.id.rideFragment);
//        }

        /*
        Listening to the click of Smart Lock Button.
         */
        binding.smartlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller=Navigation.findNavController(binding.getRoot());
                controller.navigate(R.id.rideFragment);
            }
        });

        /*
        Listening to the Maps Fragment.
         */
        binding.FetchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller=Navigation.findNavController(binding.getRoot());
                controller.navigate(R.id.mapsFragment);
            }
        });

        return  binding.getRoot();
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

}