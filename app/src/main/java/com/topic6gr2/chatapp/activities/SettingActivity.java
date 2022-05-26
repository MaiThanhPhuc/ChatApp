package com.topic6gr2.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.topic6gr2.chatapp.R;
import com.topic6gr2.chatapp.databinding.ActivitySettingBinding;
import com.topic6gr2.chatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private FirebaseAuth myAuth;
    private FirebaseDatabase database;
    Uri file;
    FirebaseStorage storage;
    boolean changeImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controlsHandle();
        LoadUserDetails();
        eventHandle();
    }

    private void LoadUserDetails(){
        final User[] user = {new User()};
        database.getReference().child("User").child(myAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user[0] = snapshot.getValue(User.class);
                binding.inputName.setHint(user[0].getUsername());
                Picasso.get().load(user[0].getProfilePictureLink()).placeholder(R.drawable.avatar3).into(binding.imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void eventHandle(){
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImg = true;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 23);
            }
        });
        binding.buttonChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.inputName.getText().toString();
                String password = binding.inputPassword.getText().toString();
                String currentPassword = binding.inputCurrentPassword.getText().toString();
                if (!userName.equals("")){
                    database.getReference().child("User").child(myAuth.getUid()).child("username").setValue(userName);
                    Toast.makeText(SettingActivity.this, "Change username successfully!!!", Toast.LENGTH_SHORT).show();
                }
                if (!password.equals("") && !currentPassword.equals("")){
                    AuthCredential credential = EmailAuthProvider.getCredential(myAuth.getCurrentUser().getEmail(),currentPassword);
                    FirebaseUser user = myAuth.getCurrentUser();
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            database.getReference().child("User").child(myAuth.getUid()).child("password").setValue(password);
                                            Toast.makeText(SettingActivity.this, "Change password successfully!!!", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                if (changeImg){
                    //Up Image profile
                    StorageReference ref = storage.getReference().child("ProfilePicture")
                            .child(myAuth.getUid());
                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("User").child(myAuth.getUid())
                                            .child("profilePictureLink").setValue(uri.toString());
                                }
                            });
                            Toast.makeText(SettingActivity.this, "Change image profile successfully!!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e("Image",e.getMessage());
                        }
                    });
                }
            }
        });
        binding.imageBack.setOnClickListener(e -> onBackPressed());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            file = data.getData();
            binding.imageProfile.setImageURI(file);
            binding.textAddImg.setVisibility(View.INVISIBLE);
        }
    }
    private void controlsHandle() {
        changeImg = false;
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }
}