package com.example.zensmartbikes.LoginScreen;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    /*
    Creating the object for the binding class for our fragment.
     */
    private FragmentLoginBinding loginBinding;

    /*
    Creating the Object of the FireBaseAuth.
     */
    private FirebaseAuth auth;
    /*
    Creating the variables to store user input.
     */
    private  String UserEmail;
    private  String UserPassword;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginBinding=FragmentLoginBinding.inflate(inflater,container,false);
        /*
        Initialising the FireBaseAuth object.
         */
        auth=FirebaseAuth.getInstance();

        /*
        Adding listener to our SinupText.
         */
        loginBinding.loginsignuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Navigating to the SignupScreen.
                 */
                NavController navController= Navigation.findNavController(loginBinding.getRoot());
                navController.navigate(R.id.signUpFragment);
            }
        });
        /*
        Adding listener to Login button.
         */
        loginBinding.loginconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBinding.loginconfirm.setEnabled(false);
                performLogin();
            }
        });
        return loginBinding.getRoot();
    }

    /*
    method to validate user input
     */
    private boolean validateInputs() {
        loginBinding.EmailTextInputLayout.setError(null);
        loginBinding.passwordTextInputLayout.setError(null);
        UserEmail = loginBinding.RiderEmail.getText().toString().trim();

        if (UserEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
            loginBinding.EmailTextInputLayout.setError("Enter a valid email address");
            return false;
        }
        UserPassword = loginBinding.passwordEditText.getText().toString().trim();
        if (UserPassword.isEmpty()) {
            loginBinding.passwordTextInputLayout.setError("Password is required");
            return false;
        }

        return true;
    }

    /*
    Method to perform login
     */
//    private void performLogin() {
//        /*
//        Validate user input.
//         */
//        if (validateInputs()) {
//            /*
//            Valid,perform login
//             */
//            auth.signInWithEmailAndPassword(UserEmail,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    /*
//                    Check if login was successful or not
//                     */
//                    if(task.isSuccessful()){
//                        /*
//                        Successful,take user to home fragment.
//                         */
//                            NavController  navController= Navigation.findNavController(loginBinding.getRoot());
//                            navController.navigate(R.id.homeFragment,null,new NavOptions.Builder()
//                                .setPopUpTo(R.id.loginFragment,true)
//                                .build());
//                    }
//                    else{
//                        /*
//                        Unsuccessful
//                         */
//                        loginBinding.loginconfirm.setEnabled(true);
//                        Toast.makeText(getContext(), "Login UnSuccessful", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        } else {
//            /*
//            Invalid Input
//             */
//            loginBinding.loginconfirm.setEnabled(true);
//            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void performLogin() {

    /*
    Validate user input.
     */
        if (validateInputs()) {
            // Show progress dialog
            ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Logging in...", true);
        /*
        Valid, perform login
         */
            auth.signInWithEmailAndPassword(UserEmail, UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                /*
                Check if login was successful or not
                 */
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                    /*
                    Successful, clear back stack and navigate to home fragment.
                     */
                        NavController navController = Navigation.findNavController(loginBinding.getRoot());
                        navController.navigate(R.id.homeFragment, null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.loginFragment, true)
                                        .setPopUpTo(R.id.signUpFragment, true)
                                        .build());
                    } else {
                    /*
                    Unsuccessful
                     */
                        progressDialog.dismiss();
                        loginBinding.loginconfirm.setEnabled(true);
                        Toast.makeText(getContext(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
        /*
        Invalid Input
         */
            loginBinding.loginconfirm.setEnabled(true);
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }

}