package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sapidiner.Classes.User;
import com.example.sapidiner.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText et_email, et_password, et_firstName, et_lastname, et_phone;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        et_firstName = (EditText) findViewById(R.id.firstName);
        et_lastname = (EditText) findViewById(R.id.lastName);
        et_email = (EditText) findViewById(R.id.editText_useremail);
        et_password = (EditText) findViewById(R.id.editText_password);
        et_phone = (EditText) findViewById(R.id.phoneNumber);
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.registrationButton).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void registerUser() {
        final String firstname = et_firstName.getText().toString().trim();
        final String lastname = et_lastname.getText().toString().trim();
        final String email = et_email.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();

        if (firstname.isEmpty()) {
            et_firstName.setError("missing information");
            et_firstName.requestFocus();
            return;
        }

        if (lastname.isEmpty()) {
            et_lastname.setError("missing information");
            et_lastname.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            et_phone.setError("missing information");
            et_phone.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("invalid email");
            et_email.requestFocus();
            return;
        }


        if (password.length() < 6) {
            et_password.setError("invalid password");
            et_password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // we will store the additional fields in firebase db
                    User user = new User(firstname, lastname, phone, email, password, 0);
                    FirebaseDatabase.getInstance().getReference("Clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                            } else {
                                Toast.makeText(RegisterActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                //Snackbar error = Snackbar.make(findViewById(R.id.container), getString(R.string.registerError), Snackbar.LENGTH_SHORT);
                                //error.getView().setBackgroundColor(getResources().getColor(R.color.RED));
                                // TextView snackbarText = error.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                                // snackbarText.setBackgroundColor(getResources().getColor(R.color.RED));
                                //error.show();
                            }
                        }
                    });
                } else {
                    //Toast.makeText(RegisterActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
                    Log.d("KKK", "hiba");
                }
            }
        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registrationButton:
                registerUser();
                break;
        }
    }
}
