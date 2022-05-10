package com.android.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.chatapp.R;
import com.android.chatapp.databinding.ActivitySignUpBinding;
import com.android.chatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding signUpBinding;
    private FirebaseAuth myAuth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    FirebaseStorage storage;
    Uri file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        controlsHandle();
        setListener();
        eventsHandle();
    }
    private void setListener(){
        signUpBinding.textSignIn.setOnClickListener(e -> onBackPressed());
    }
    private void eventsHandle() {

        signUpBinding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 23);
            }
        });

        signUpBinding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = signUpBinding.inputName.getText().toString();
                String password = signUpBinding.inputPassword.getText().toString();
                String rePassword = signUpBinding.inputRePassword.getText().toString();
                String email = signUpBinding.inputEmail.getText().toString();

                if (!username.isEmpty() &&
                        !password.isEmpty() &&
                        !email.isEmpty() && rePassword.equals(password)){
                    dialog.show();
                    myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                User user = new User(username,uid,password, email);
                                database.getReference().child("User").child(uid).setValue(user);

                                Toast.makeText(SignUpActivity.this, "Create account successfully", Toast.LENGTH_LONG).show();

                                //Up Image profile
                                StorageReference ref = storage.getReference().child("ProfilePicture")
                                        .child(uid);
                                ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                database.getReference().child("User").child(uid)
                                                        .child("profilePictureLink").setValue(uri.toString());
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.e("Image",e.getMessage());
                                    }
                                });
                                signIn();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Missing info", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            file = data.getData();
            signUpBinding.imageProfile.setImageURI(file);
            signUpBinding.textAddImg.setVisibility(View.INVISIBLE);
        }
    }
    private void controlsHandle() {
        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setTitle("Pending");
        dialog.setCanceledOnTouchOutside(false);

    }
    private void signIn() {
        Intent signIn= new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(signIn);
    }
}