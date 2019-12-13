package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.sapidiner.Classes.User;
import com.example.sapidiner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


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
            et_firstName.setError(getString(R.string.missingInfo));
            et_firstName.requestFocus();
            return;
        }

        if (lastname.isEmpty()) {
            et_lastname.setError(getString(R.string.missingInfo));
            et_lastname.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            et_phone.setError(getString(R.string.missingInfo));
            et_phone.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError(getString(R.string.invalidEmail));
            et_email.requestFocus();
            return;
        }


        if (password.length() < 6) {
            et_password.setError(getString(R.string.invalidPassword));
            et_password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // we will store the additional fields in firebase db
                    String userId = mAuth.getCurrentUser().getUid();
                    User user = new User(userId,firstname, lastname, phone, email, password, 0);
                    FirebaseDatabase.getInstance().getReference("Clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            } else {
                                Toast.makeText(RegisterActivity.this, getString(R.string.regFailed), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {
                    //Toast.makeText(RegisterActivity.this,"something went wrong",Toast.LENGTH_LONG).show();

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
