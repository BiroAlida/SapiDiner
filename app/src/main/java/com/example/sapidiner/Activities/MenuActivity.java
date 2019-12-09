package com.example.sapidiner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Fragments.BlankFragment;
import com.example.sapidiner.Fragments.SetMenuFragment;
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
    private ArrayList<String> userList = new ArrayList<>();
    private FirebaseCallback callback;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // why?
        readCurrentUserData(new FirebaseCallback() {
            @Override
            public void onCallback(User user) {
                userType = user.getUserType();
                if (userType == 1) { // check if admin

                    loadFragment(new SetMenuFragment());

                }

                else{

                   // TODO: Implement me.
                    loadFragment(new BlankFragment());
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
                // TODO: implement me
                Log.d("MenuActivity::onCancelled", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface FirebaseCallback{
        void onCallback(User user);   // custom callback that waits for the data
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment,fragment);
        fragmentTransaction.addToBackStack(null); //By calling addToBackStack(), the replace transaction is saved to the back stack so the user can reverse the transaction and bring back the previous fragment by pressing the Back button.
        fragmentTransaction.commit();
    }

}
