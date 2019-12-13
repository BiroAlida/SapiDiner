package com.example.sapidiner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Fragments.BlankFragment;
import com.example.sapidiner.Fragments.SetMenuFragment;
import com.example.sapidiner.Fragments.ViewMenuFragment;
import com.example.sapidiner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener {

    private String currentUserId;
    private DatabaseReference database;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        readCurrentUserData(new FirebaseCallback() {
            @Override
            public void onCallback(User user) {
                userType = user.getUserType();

                if (userType == 1) { // check if admin

                    loadFragment(new SetMenuFragment());

                }

                else{

                    Bundle bundle=new Bundle();
                    bundle.putString("currentUserId", currentUserId);
                    ViewMenuFragment viewMenuFragment = new ViewMenuFragment();
                    viewMenuFragment.setArguments(bundle);
                    loadFragment(viewMenuFragment);
                }

            }
        });

    }

    public void readCurrentUserData(final FirebaseCallback callback) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance().getReference("Clients");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User u = dataSnapshot.child(currentUserId).getValue(User.class);
                callback.onCallback(u);

            }
            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("MenuActivity::onCancelled", databaseError.getMessage());
            }
        });
    }


    public void onFragmentInteraction(Uri uri) {

    }

    public interface FirebaseCallback{
        void onCallback(User user);
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
