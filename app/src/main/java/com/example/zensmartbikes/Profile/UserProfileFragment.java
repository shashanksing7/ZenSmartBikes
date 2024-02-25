package com.example.zensmartbikes.Profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zensmartbikes.R;
import com.example.zensmartbikes.databinding.FragmentUserProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
/*
This profiles is used to handle an display the user information.
 */

public class UserProfileFragment extends Fragment {

    /*
    creating the object of the binding class.
     */
    FragmentUserProfileBinding userProfileBinding;

    /*
     creating the object of the FirebaseAuth.
     */
    private FirebaseAuth firebaseAuth;

    /*
    creating the object of the FireStore
     */
    private FirebaseFirestore firestore;
    /*
    Creating the variables to store the UUID of current user.
     */
    private  String UUID;
    /*
    Static variables to act as keys.
     */
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_EMAIL = "Email";
    public static final String FIELD_GENDER = "Gender";
    public static final String FIELD_RIDES = "Rides";

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userProfileBinding=FragmentUserProfileBinding.inflate(inflater,container,false);
        /*
        Getting the instance of the FirebaseAuth.
         */
        firebaseAuth=FirebaseAuth.getInstance();
        /*
        Getting instance of the fireStore;
         */
        firestore=FirebaseFirestore.getInstance();
        /*
        Getting the data from the fireStore.
         */
        UUID=firebaseAuth.getCurrentUser().getUid();
        DocumentReference reference=firestore.collection("Rider").document(UUID);
        /*
        Adding snapshot listener to listen to any change in the data .
         */
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    /*
                    Error
                     */

                }else {
                    /*
                    Successful.
                     */
                    userProfileBinding.usernameTextView.setText(value.getString(FIELD_NAME));
                    userProfileBinding.userEmailTextView.setText(value.getString(FIELD_EMAIL));
                    userProfileBinding.numberOfRidesTextView.setText("Number of Rides: "+value.getLong(FIELD_RIDES));
                }
            }
        });

        /*
        Adding listener to logout button.
         */
        userProfileBinding.LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Logout user and Navigate to Login Screen.
                 */
                firebaseAuth.signOut();
                /*
                Getting the Nav-controller and navigate to login screen;
                 */
                NavController controller= Navigation.findNavController(userProfileBinding.getRoot());
                controller.navigate(R.id.loginFragment,null,new NavOptions.Builder().
                        setPopUpTo(R.id.homeFragment,true).
                        setPopUpTo(R.id.userProfileFragment,true).
                        build());

            }
        });

        return userProfileBinding.getRoot();
    }
}