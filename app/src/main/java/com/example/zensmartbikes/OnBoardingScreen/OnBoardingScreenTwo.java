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

public class OnBoardingScreenTwo extends Fragment {

   /*
   Creating the Objet of the Binding class which has references to all the views of our layout file.
    */
    FragmentOnBoardingScreenTwoBinding fragmentOnBoardingScreenTwoBinding;


    public OnBoardingScreenTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentOnBoardingScreenTwoBinding=FragmentOnBoardingScreenTwoBinding.inflate(inflater,container,false);
        /*
        Setting a click listener to our Button and taking the user to the home screen .
         */
        fragmentOnBoardingScreenTwoBinding.onBoardingScreenTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController=Navigation.findNavController(fragmentOnBoardingScreenTwoBinding.getRoot());
                navController.navigate(R.id.homeFragment,null,new NavOptions.Builder()
                        .setPopUpTo(R.id.onBoardingScreenTwo,true)
                        .build());
            }
        });
        return fragmentOnBoardingScreenTwoBinding.getRoot();
    }
}