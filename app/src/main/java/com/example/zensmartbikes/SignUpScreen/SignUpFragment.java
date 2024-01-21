package com.example.zensmartbikes.SignUpScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    public void PerformSignUp(){
        /*
        Checking if user Entered Data is valid or not
         */
        if(validateInputs()){
            /*
            Input valid,perform signup.
             */
            firebaseAuth.createUserWithEmailAndPassword(UserEmail,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    /*
                    Checking if task is successful or not;
                     */
                    if(task.isSuccessful()){
                                                /*
                        Successful,take user to home fragment.
                         */
                        NavController navController= Navigation.findNavController(signUpBinding.getRoot());
                        navController.navigate(R.id.homeFragment,null,new NavOptions.Builder()
                                .setPopUpTo(R.id.onBoardingScreenTwo,true)
                                .build());
                    }
                    else {
                        /*
                        UnSuccessful
                         */
                        signUpBinding.loginconfirm.setEnabled(true);
                        Toast.makeText(getContext(), "signup unsuccessful", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else{
            /*
            Invalid input.
             */
            signUpBinding.loginconfirm.setEnabled(true);
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }


}