package com.example.zensmartbikes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.zensmartbikes.Map.AnimationHelper;
import com.example.zensmartbikes.Ride.OnGoingRideService;
import com.example.zensmartbikes.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /*
    These Key will be used in the shared preferences.
     */

    private static final int SPLASH_DELAY = 2000;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String HAS_VISITED_B_KEY = "hasVisitedB";

    /*
    Instance of firebaseAuth
     */
    FirebaseAuth auth;


    /*
    ViewBinding Object.
     */
    private ActivityMainBinding activityMainBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Inflating the View
         */
        activityMainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        /*
        Getting the Instance of firebaseAuth
         */
        auth=FirebaseAuth.getInstance();



        /*
        Getting the Nav Controller.
         */
        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController=navHostFragment.getNavController();
        /*
        setting up the Bottom Navigation Bar.
         */
        NavigationUI.setupWithNavController(activityMainBinding.bottomNavigationView,navController);


        /*
        Hiding the Bottom navigation view in on boarding screen fragment.
         */
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

                if (navDestination.getId() == R.id.onBoardingScreenOne||navDestination.getId()==R.id.onBoardingScreenTwo
                        ||navDestination.getId()==R.id.signUpFragment||navDestination.getId()==R.id.loginFragment) {
                    // Hide the Bottom Navigation View
                    activityMainBinding.bottomNavigationView.setVisibility(View.GONE);
                } else {
                    // Show the Bottom Navigation View for other fragments
                    activityMainBinding.bottomNavigationView.setVisibility(View.VISIBLE);
                }

            }
        });


    }
    /*
    We are calling the  navigateToOnBoardingScreen() method in OnStart() and not in OnCreate()
    because some of the UI component are not properly initialized in the OnCreate() including the NavHostFragment
    in that phase.
     */
    @Override
    protected void onStart() {

        super.onStart();
        navigateToOnBoardingScreen();

    }
//    /*
//    This method finds the associated navController and navigates ti the appropriate Screen
//    on the basis of shared preferences data.
//     */
//    private void navigateToOnBoardingScreen() {
//
//        /*
//        finding the NavController associated with the MainActivity.
//         */
//        NavController navController=Navigation.findNavController(this,R.id.nav_host_fragment);
//        /*
//        Getting the shared preferences and finding if the user has visited the Onboarding screen before.
//         */
//        SharedPreferences preferences=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
//        boolean hasVisited=preferences.getBoolean(HAS_VISITED_B_KEY,false);
//        /*
//        The user has visited before so navigate to home.
//         */
//        if(hasVisited){
//
//            /*
//            taking user to the Home Screen.
//             */
//            FirebaseUser user=auth.getCurrentUser();
//            if(user==null){
//                /*
//                No user is signed in.
//                 */navController.navigate(R.id.loginFragment,null,new NavOptions.Builder().setPopUpTo(R.id.homeFragment,true).build());
//            }
//            else {
//
//                /*
//                    UserSigned in
//                 */
//
//                /*
//                    Navigate to RideFragment if service is running.
//                */
//                if(isServiceRunning(OnGoingRideService.class)){
//                    navController.navigate(R.id.rideFragment,null,new NavOptions.Builder().setPopUpTo(R.id.homeFragment,true).build());
//                }
//                else {
//                    navController.navigate(R.id.homeFragment);
//                }
//            }
//
//        }
//        else{
//            /*
//            This is the first time user installed the app,take the user to walkthrough(onBoardingScreen).
//             */
//            //////////////////////////////////////
//            /*
//            Setting the Shared preferences value to true so that next time user won't be taken to the
//            Onboarding screen.
//             */
//            SharedPreferences.Editor editor= preferences.edit();
//            editor.putBoolean(HAS_VISITED_B_KEY,true);
//            editor.apply();
//
//            /*
//            Taking the user to OnBoardingScreen.
//             */
//            navController.navigate(R.id.onBoardingScreenOne);
//
//        }
//
//    }
private void navigateToOnBoardingScreen() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    boolean hasVisited = preferences.getBoolean(HAS_VISITED_B_KEY, false);
    boolean userSignedIn = auth.getCurrentUser() != null;
    boolean serviceRunning = isServiceRunning(OnGoingRideService.class);

    if (hasVisited) {
        if (userSignedIn) {
            // User is signed in
            if (serviceRunning) {
                // Clear back stack up to HomeFragment and navigate to RideFragment
                navController.navigate(R.id.rideFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build());
            } else {
                // Clear back stack up to HomeFragment and navigate to HomeFragment
                navController.navigate(R.id.homeFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build());
            }
        } else {
            // User is not signed in, navigate to LoginFragment
            navController.navigate(R.id.loginFragment, null,
                    new NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build());
        }
    } else {
        // First-time user, navigate to OnBoardingScreenOne
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HAS_VISITED_B_KEY, true);
        editor.apply();
        navController.navigate(R.id.onBoardingScreenOne);
    }
}

    /*
    Utility method to check if a service is running.
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
