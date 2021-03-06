package com.topic6gr2.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.topic6gr2.chatapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding signInBinding;
    private FirebaseAuth myAuth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        setListeners();
        controlsHandle();
        eventsHandle();
    }
    private void setListeners(){
        signInBinding.textCreateNewAccount.setOnClickListener(e ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
    }
    private void eventsHandle() {

        signInBinding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = signInBinding.inputEmail.getText().toString();
                String password = signInBinding.inputPassword.getText().toString();
                if (!userName.isEmpty() && !password.isEmpty()) {
                    dialog.show();
                    myAuth.signInWithEmailAndPassword(userName, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog.dismiss();

                                    if (task.isSuccessful()) {
                                        FirebaseMessaging.getInstance()
                                                .getToken()
                                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String token) {
                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("token", token);
                                                        database.getReference()
                                                                .child("User")
                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                .updateChildren(map);
                                                    }
                                                });
                                        Intent accessApp = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(accessApp);
                                    }
                                    else {
                                        Toast.makeText(SignInActivity.this,  "Incorrect email or password", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(SignInActivity.this, "Missing info", Toast.LENGTH_LONG).show();
                }

            }
        });
        if (myAuth.getCurrentUser()!=null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
    private void controlsHandle() {
        dialog = new ProgressDialog(SignInActivity.this);
        dialog.setTitle("Logging...");
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }
}