package com.example.sapidiner.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.example.sapidiner.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_login;
    private TextView tw_register;
    private EditText et_password, et_loginEmail;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.button_login);
        et_loginEmail = findViewById(R.id.editText_email);
        et_password = findViewById(R.id.editTextPassword);
        tw_register = findViewById(R.id.tw_register);

        btn_login.setOnClickListener(this);
        tw_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean validateInputs(){
        email = et_loginEmail.getText().toString().trim();
        password = et_password.getText().toString().trim();
        if(email.isEmpty())
        {
            et_loginEmail.setError(getString(R.string.regNameError));
            et_loginEmail.requestFocus();
            return false;
        }
        if(password.isEmpty())
        {
            et_password.setError(getString(R.string.regEmailError));
            et_password.requestFocus();
            return false;
        }
        return true;
    }

    public void onClick(final View v) {
        if (v == btn_login) {
            if (validateInputs()){
                FirebaseDatabaseManager.Instance.getFirebaseAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Utilities.displayErrorSnackbar(findViewById(R.id.container), getString(R.string.loginError));
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


}

