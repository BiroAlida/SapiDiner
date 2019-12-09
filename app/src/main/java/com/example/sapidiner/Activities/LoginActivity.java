package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sapidiner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_login;
    private TextView tw_register;
    private EditText et_password, et_loginEmail;
    private CheckBox cb;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean saveLogin;
    private FirebaseAuth auth;
    private String number, verification_code, email, password;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rootRef, itemsRef;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btn_login = findViewById(R.id.button_login);
        et_loginEmail = findViewById(R.id.editText_email);
        et_password = findViewById(R.id.editTextPassword);
        tw_register = findViewById(R.id.tw_register);
        auth = FirebaseAuth.getInstance();


        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference();
        itemsRef = rootRef.child("Clients");


        btn_login.setOnClickListener(this);


        tw_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onClick(final View v) {
        if (v == btn_login) {
            email = et_loginEmail.getText().toString().trim();
            password = et_password.getText().toString().trim();
            if(email.isEmpty())
            {
                et_loginEmail.setError(getString(R.string.regNameError));
                et_loginEmail.requestFocus();
                return;
            }
            if(password.isEmpty())
            {
                et_password.setError(getString(R.string.regEmailError));
                et_password.requestFocus();
                return;
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful())
                    {
                        Snackbar error = Snackbar.make(v, getString(R.string.loginError), Snackbar.LENGTH_SHORT);
                        // error.getView().setBackgroundColor(getResources().getColor(R.color.RED));
                        TextView snackbarText = error.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                        // snackbarText.setBackgroundColor(getResources().getColor(R.color.RED));
                        error.show();
                    }
                    else{
                        startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                        finish();
                    }
                }
            });

        }
    }


}

