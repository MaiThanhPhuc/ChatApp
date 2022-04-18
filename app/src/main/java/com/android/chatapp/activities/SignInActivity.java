package com.android.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.chatapp.R;
import com.android.chatapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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
                                        Intent accessApp = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(accessApp);
                                    }
                                    else {
                                        Toast.makeText(SignInActivity.this,  task.getException().toString(), Toast.LENGTH_LONG).show();
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