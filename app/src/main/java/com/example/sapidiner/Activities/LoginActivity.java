package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sapidiner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private Button btn_login;
    private TextView tw_register;
    private EditText et_loginPhone,verificationCode;
    private CheckBox cb;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean saveLogin;
    private FirebaseAuth auth;
    private String number, verification_code;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.button_login);
        et_loginPhone = findViewById(R.id.editText_phoneNumber);
        cb = findViewById(R.id.checkBox);
        tw_register = findViewById(R.id.tw_register);
        auth = FirebaseAuth.getInstance();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number = et_loginPhone.getText().toString();

               /* if (cb.isChecked()) {
                    editor.putBoolean("saveLogin", true);
                    editor.putString("userphone", number);

                    editor.commit();
                } else {
                    editor.clear();
                    editor.commit();
                }*/

                send_sms();
                showDialog();

            }
        });


        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        saveLogin = sharedPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {

            et_loginPhone.setText(sharedPreferences.getString("userphone", ""));
            cb.setChecked(true);
        }

       mCallback  = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
           @Override
           public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

           }

           @Override
           public void onVerificationFailed(@NonNull FirebaseException e) {

           }

           @Override
           public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
               super.onCodeSent(s, forceResendingToken);
               verification_code = s;
               Toast.makeText(getApplicationContext(),"Code sent to the number",Toast.LENGTH_SHORT).show();
           }
       };

        tw_register.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });


        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        saveLogin = sharedPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            et_loginPhone.setText(sharedPreferences.getString("userphone", ""));

            cb.setChecked(true);
        }

    }

    private void send_sms() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+4" + number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback);
    }


    public void signInWithPhone(PhoneAuthCredential credintial)
    {
        auth.signInWithCredential(credintial)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            //Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent i  = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(i);
                        }
                    }
                });
    }


    private void showDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.verifyPhoneNumber));

        verificationCode = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        verificationCode.setLayoutParams(layoutParams);

        alertDialog.setView(verificationCode);
        alertDialog.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                verifyCode();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void verifyCode(){
        String code = verificationCode.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6){
            verificationCode.setError(getString(R.string.validationCodeError));
            verificationCode.requestFocus();
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code ,code);
            //registerWithPhoneAuthCredential(credential);
            verifyPhoneNumber(verification_code, code);
        }
    }

    public void verifyPhoneNumber(String verifyCode, String input_code)
    {
        PhoneAuthCredential credintial  = PhoneAuthProvider.getCredential(verifyCode, input_code);
        signInWithPhone(credintial);
    }


}
