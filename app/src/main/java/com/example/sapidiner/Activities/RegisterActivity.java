package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sapidiner.Classes.User;
import com.example.sapidiner.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameInput, lastNameInput, phoneInput, verificationCode;
    private String firstName, lastName, phone, verificationId;
    private Button registrationButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initVariables();

    }

    private void initVariables() {
        firstNameInput = findViewById(R.id.firstName);
        lastNameInput = findViewById(R.id.lastName);
        phoneInput = findViewById(R.id.phoneNumber);
        registrationButton = findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()){
                    checkIfRegistered(new FirebaseCallback(){
                        @Override
                        public void onCallback(boolean isRegistered) {
                            if (isRegistered){
                                Snackbar.make(findViewById(R.id.viewContainer), getString(R.string.registrationError), Snackbar.LENGTH_SHORT).show();
                            } else {
                                sendVerificationCode();
                                showDialog();
                            }
                        }
                    });
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Snackbar.make(findViewById(R.id.viewContainer), e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
            }
        };
    }

    private boolean validateInputs() {
        firstName = firstNameInput.getText().toString().trim();
        lastName = lastNameInput.getText().toString().trim();
        phone = phoneInput.getText().toString().trim();

        if (firstName.isEmpty()){
            firstNameInput.setError(getString(R.string.nameInputError));
            firstNameInput.requestFocus();
            return false;
        }

        if (lastName.isEmpty()){
            lastNameInput.setError(getString(R.string.nameInputError));
            lastNameInput.requestFocus();
            return false;
        }

        if (phone.isEmpty() || phone.length() < 10){
            phoneInput.setError(getString(R.string.phoneInputError));
            phoneInput.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkIfRegistered(final FirebaseCallback firebaseCallback) {
        DatabaseReference userRef = FirebaseDatabaseManager.Instance.getUsersReference();
        userRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    //phone number is already registered
                    firebaseCallback.onCallback(true);
                } else {
                    firebaseCallback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return false;
    }

    interface FirebaseCallback{
        void onCallback(boolean isRegistered);
    }

    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+4" + phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
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
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
            registerWithPhoneAuthCredential(credential);
        }
    }

    private void registerWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseDatabaseManager.Instance.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //add to database
                    String key = task.getResult().getUser().getUid();
                    FirebaseDatabaseManager.Instance.addNewUser(key, new User(firstName,lastName,phone,0));

                    //login user
                    Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                    startActivity(intent);
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Snackbar.make(findViewById(R.id.viewContainer),getString(R.string.validationCodeError),Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
