package com.example.sapidiner.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.Fragments.SetMenuFragment;
import com.example.sapidiner.Fragments.ViewMenuFragment;
import com.example.sapidiner.R;
import com.example.sapidiner.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {
    private String currentUserId;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

        readCurrentUserData(user -> {
            if (user.getUserType() == Utilities.ADMIN) { // check if admin
                loadFragment(new SetMenuFragment());
            }
            else{
                Bundle bundle=new Bundle();
                bundle.putSerializable("currentUser",user);
                bundle.putString("currentUserId",currentUserId);
                ViewMenuFragment viewMenuFragment = new ViewMenuFragment();
                viewMenuFragment.setArguments(bundle);
                loadFragment(viewMenuFragment);
            }

        });

    }

    public void readCurrentUserData(final FirebaseCallback callback) {
        currentUserId = FirebaseDatabaseManager.Instance.getFirebaseAuth().getCurrentUser().getUid();
        FirebaseDatabaseManager.Instance.getClientsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child(currentUserId).getValue(User.class);
                loadingDialog.dismiss();
                callback.onCallback(u);

            }
            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MenuActivity::onCancelled", databaseError.getMessage());
            }
        });
    }


    public interface FirebaseCallback{
        void onCallback(User user);
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
