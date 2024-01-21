package com.example.zensmartbikes.OnBoardingScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.databinding.FragmentOnBoardingScreenTwoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OnBoardingScreenTwo extends Fragment {

   /*
   Creating the Objet of the Binding class which has references to all the views of our layout file.
    */
    FragmentOnBoardingScreenTwoBinding fragmentOnBoardingScreenTwoBinding;
        /*
    Instance of firebaseAuth
     */
    FirebaseAuth auth;


    public OnBoardingScreenTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentOnBoardingScreenTwoBinding=FragmentOnBoardingScreenTwoBinding.inflate(inflater,container,false);

        /*
        Getting the Instance of firebaseAuth
         */
        auth=FirebaseAuth.getInstance();

        /*
        Setting a click listener to our Button and taking the user to the home screen .
         */
        fragmentOnBoardingScreenTwoBinding.onBoardingScreenTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             NavController navController=Navigation.findNavController(fragmentOnBoardingScreenTwoBinding.getRoot());

                /*
            taking user to the Home Screen.
             */
                FirebaseUser user=auth.getCurrentUser();
                if(user==null){
                /*
                No user is signed in.
                 */
                    navController.navigate(R.id.loginFragment,null,new NavOptions.Builder()
                            .setPopUpTo(R.id.onBoardingScreenTwo,true)
                            .build());
                }
                else {
                /*
                UserSigned in
                 */
                    navController.navigate(R.id.homeFragment,null,new NavOptions.Builder()
                            .setPopUpTo(R.id.onBoardingScreenTwo,true)
                            .build());
                }
            }
        });
        return fragmentOnBoardingScreenTwoBinding.getRoot();
    }
}