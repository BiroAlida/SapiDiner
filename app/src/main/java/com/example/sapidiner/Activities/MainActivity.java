package com.example.sapidiner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sapidiner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_login;
    private TextView tw_register;
    private EditText et_loginPhone, et_password;
    private String phoneNum, password;
    private CheckBox cb;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean saveLogin;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.button_login);
        et_loginPhone = findViewById(R.id.editText_phoneNumber);
        et_password = findViewById(R.id.editText_password);
        cb = findViewById(R.id.checkBox);
        tw_register = findViewById(R.id.tw_register);

        btn_login.setOnClickListener(this);
        tw_register.setOnClickListener(this);


        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        saveLogin = sharedPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            et_loginPhone.setText(sharedPreferences.getString("userphone", ""));
            et_password.setText(sharedPreferences.getString("password", ""));
            cb.setChecked(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_loginPhone.getWindowToken(), 0);

                phoneNum = et_loginPhone.getText().toString().trim();
                password = et_password.getText().toString().trim();


                if (phoneNum.isEmpty()) {
                    et_loginPhone.setError("Phone number is required");
                    et_loginPhone.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    et_password.setError("Password is required");
                    et_password.requestFocus();
                    return;
                }

                if (cb.isChecked()) {
                    editor.putBoolean("saveLogin", true);
                    editor.putString("userphone", phoneNum);
                    editor.putString("password", password);

                    editor.commit();
                } else {
                    editor.clear();
                    editor.commit();
                }

                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                break;
            case R.id.tw_register:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
        }
    }
}
