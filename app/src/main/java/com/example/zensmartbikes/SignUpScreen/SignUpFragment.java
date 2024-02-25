package com.example.zensmartbikes.SignUpScreen;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
This Class will be used to signup on Firebase.
 */
public class SignUpFragment extends Fragment {

    /*
    Creating the object of the Binding class.
     */
    FragmentSignUpBinding signUpBinding;

    /*
    creating variables for getting user entered information.
     */
    private  String UserEmail;
    private String UserName;
    private  String UserPassword;
    private String UserGender;
    /*
    Creating the ArrayAdapter for the DropDown.
     */
    private ArrayAdapter<String>GenderAdapter;
    /*
    Creating instance of the firebase FireBAseAuth class;
     */
    FirebaseAuth firebaseAuth;
    /*
    fireSTORE OBJECT.
     */
    FirebaseFirestore firestore;

    /*
    String variable to store UUId of the signed-in user
     */
    String UUID;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        Inflating the views of the layout.
         */
        signUpBinding=FragmentSignUpBinding.inflate(inflater,container,false);

        /*
        Initialising the firebaseAuth  object.
         */
        firebaseAuth=FirebaseAuth.getInstance();
        /*
        getting instance.
         */
        firestore=FirebaseFirestore.getInstance();

        /*
        Creating the String Array for the gender drop down spinner;
         */
        String[] GenderList={"male","Female","Others"};

        /*
             Setting the Adapter.
         */
        GenderAdapter=new ArrayAdapter<>(getContext(),R.layout.gender_list,GenderList);
        signUpBinding.SignupScreenGender.setAdapter(GenderAdapter);
        /*
        Setting the OnItemClickListener for Spinner.
         */
        signUpBinding.SignupScreenGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserGender=parent.getItemAtPosition(position).toString();
            }
        });

        /*
        Adding listener to our signup button.
         */
        signUpBinding.loginconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Calling method to signup
                 */
                signUpBinding.loginconfirm.setEnabled(false);
                PerformSignUp();
            }
        });

        return signUpBinding.getRoot();
    }

    // Validate user inputs
    private boolean validateInputs() {
        // Reset previous errors
        signUpBinding.EmailTextInputLayout.setError(null);
        signUpBinding.UserNameTextInputLayout.setError(null);
        signUpBinding.SecuirtyKeyTextInputLayout.setError(null);
        signUpBinding.GenderTextInputLayout.setError(null);

        // Validate Name
        UserName = signUpBinding.UserName.getText().toString().trim();
        if (UserName.isEmpty()) {
            signUpBinding.UserNameTextInputLayout.setError("Name is required");
            return false;
        }

        // Validate Phone
        UserEmail = signUpBinding.RiderEmail.getText().toString().trim();
        if (UserEmail.isEmpty() || UserEmail.length() < 10) {
            signUpBinding.EmailTextInputLayout.setError("Enter a valid phone number");
            return false;
        }

        // Validate Password
        UserPassword = signUpBinding.SecurityKey.getText().toString().trim();
        if (UserPassword.isEmpty()) {
            signUpBinding.SecuirtyKeyTextInputLayout.setError("Password is required");
            return false;
        }

        // Validating the uSer Gender
        if (UserGender.isEmpty()) {
            signUpBinding.GenderTextInputLayout.setError("Gender is required");
            return false;
        }

        return true;
    }

    /*
    This method will perform the signup after validation.
     */
//    public void PerformSignUp(){
//        /*
//        Checking if user Entered Data is valid or not
//         */
//        if(validateInputs()){
//            /*
//            Input valid,perform signup.
//             */
//            firebaseAuth.createUserWithEmailAndPassword(UserEmail,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    /*
//                    Checking if task is successful or not;
//                     */
//                    if(task.isSuccessful()){
//                                                /*
//                        Successful,Save User Data.
//                         */
//                        UUID=firebaseAuth.getCurrentUser().getUid();
//                        DocumentReference documentReference=firestore.collection("Rider").document(UUID);
//                        /*
//                        Creating a map object to represent the User.
//                         */
//                        Map<String,Object> user=new HashMap<>();
//                        /*
//                        Adding user details
//                         */
//                        user.put("Name",UserName);
//                        user.put("Email",UserEmail);
//                        user.put("Password",UserPassword);
//                        user.put("Gender",UserGender);
//                        user.put("Rides",0);
//                        /*
//                        Populating the database and adding an event listener.
//                         */
//                        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    /*
//                                    Successful navigate user to home fragment.
//                                     */
//                                    NavController navController = Navigation.findNavController(signUpBinding.getRoot());
//                                    navController.navigate(R.id.homeFragment, null, new NavOptions.Builder()
//                                            .setPopUpTo(R.id.onBoardingScreenTwo, true)
//                                            .build());
//                                } else {
//
//                                    firebaseAuth.signOut();
//
//
//                                    Log.d("mytag", "onComplete: " + task.toString());
//                                    Toast.makeText(getContext(), "Failed saving data", Toast.LENGTH_SHORT).show();
//
//                                    // Since saving data failed, you may want to enable the signup button again
//                                    signUpBinding.loginconfirm.setEnabled(true);
//                                }
//                            }
//                        });
//                    }
//                    else {
//                        /*
//                        UnSuccessful
//                         */
//                        signUpBinding.loginconfirm.setEnabled(true);
//                        Toast.makeText(getContext(), "signup unsuccessful", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//        }
//        else{
//            /*
//            Invalid input.
//             */
//            signUpBinding.loginconfirm.setEnabled(true);
//            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void PerformSignUp() {
//    /*
//    Checking if user Entered Data is valid or not
//    */
//        if (validateInputs()) {
//        /*
//        Input valid,perform signup.
//        */
//            firebaseAuth.createUserWithEmailAndPassword(UserEmail, UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                /*
//                Checking if task is successful or not;
//                */
//                    if (task.isSuccessful()) {
//                    /*
//                    Successful,Save User Data.
//                    */
//                        UUID = firebaseAuth.getCurrentUser().getUid();
//                        DocumentReference documentReference = firestore.collection("Rider").document(UUID);
//                    /*
//                    Creating a map object to represent the User.
//                    */
//                        Map<String, Object> user = new HashMap<>();
//                    /*
//                    Adding user details
//                    */
//                        user.put("Name", UserName);
//                        user.put("Email", UserEmail);
//                        user.put("Password", UserPassword);
//                        user.put("Gender", UserGender);
//                        user.put("Rides", 0);
//                    /*
//                    Populating the database and adding an event listener.
//                    */
//                        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                /*
//                                Successful navigate user to home fragment.
//                                */
//                                    NavController navController = Navigation.findNavController(signUpBinding.getRoot());
//                                    navController.navigate(R.id.homeFragment, null, new NavOptions.Builder()
//                                            .setPopUpTo(R.id.onBoardingScreenTwo, true)
//                                            .build());
//                                } else {
//                                    // If saving data fails, sign out the user and delete the account
//                                    firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> taskDeleteAccount) {
//                                            if (taskDeleteAccount.isSuccessful()) {
//                                                // User account deleted successfully
//                                                Log.d(TAG, "onComplete: User account deleted");
//                                            } else {
//                                                // Failed to delete user account
//
//                                                Log.e(TAG, "onComplete: Failed to delete user account", taskDeleteAccount.getException());
//                                            }
//
//                                            Log.d("mytag", "onComplete:sss " + task.getException().toString());
//                                            Toast.makeText(getContext(), "Failed saving data", Toast.LENGTH_SHORT).show();
//
//                                            // Since saving data failed, you may want to enable the signup button again
//                                            signUpBinding.loginconfirm.setEnabled(true);
//                                        }
//                                    });
//                                }
//                            }
//                        });
//                    } else {
//                    /*
//                    UnSuccessful
//                    */
//                        signUpBinding.loginconfirm.setEnabled(true);
//                        Toast.makeText(getContext(), "signup unsuccessful", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//        } else {
//        /*
//        Invalid input.
//        */
//            signUpBinding.loginconfirm.setEnabled(true);
//            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void PerformSignUp() {
        if (validateInputs()) {
            // Show progress dialog
            ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Signing up...", true);

            firebaseAuth.createUserWithEmailAndPassword(UserEmail, UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss(); // Dismiss progress dialog

                    if (task.isSuccessful()) {
                        // Sign up successful
                        UUID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("Rider").document(UUID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("Name", UserName);
                        user.put("Email", UserEmail);
                        user.put("Password", UserPassword);
                        user.put("Gender", UserGender);
                        user.put("Rides", 0);

                        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss(); // Dismiss progress dialog

                                if (task.isSuccessful()) {
                                    NavController navController = Navigation.findNavController(signUpBinding.getRoot());
                                    navController.navigate(R.id.homeFragment, null, new NavOptions.Builder()
                                            .setPopUpTo(R.id.onBoardingScreenTwo, true)
                                            .build());
                                } else {
                                    // Handle failure to save data
                                    firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> taskDeleteAccount) {
                                            // Handle failure to delete user account
                                            Log.e(TAG, "Failed to delete user account", taskDeleteAccount.getException());
                                            Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                                            signUpBinding.loginconfirm.setEnabled(true);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        // Sign up unsuccessful
                        progressDialog.dismiss(); // Dismiss progress dialog
                        Toast.makeText(getContext(), "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                        signUpBinding.loginconfirm.setEnabled(true);
                    }
                }
            });
        } else {
            // Invalid input
            signUpBinding.loginconfirm.setEnabled(true);
            Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }



}