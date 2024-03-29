package com.example.zensmartbikes.OnBoardingScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.databinding.FragmentOnBoardingScreenOneBinding;

/*
This fragment will act as the First screen of the two onBoarding screen.
 */
public class OnBoardingScreenOne extends Fragment {
    /*
    Creating the object of the class generated by android
    for view binding.

     */
    FragmentOnBoardingScreenOneBinding fragmentOnBoardingScreenOneBinding;

    public OnBoardingScreenOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentOnBoardingScreenOneBinding=FragmentOnBoardingScreenOneBinding.inflate(inflater,container,false);
        fragmentOnBoardingScreenOneBinding.onBoardingScreenOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController= Navigation.findNavController(fragmentOnBoardingScreenOneBinding.getRoot());
                navController.navigate(R.id.action_onBoardingScreenOne_to_onBoardingScreenTwo, null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.onBoardingScreenOne, true)
                                .build());
            }
        });
        return  fragmentOnBoardingScreenOneBinding.getRoot();
    }

}