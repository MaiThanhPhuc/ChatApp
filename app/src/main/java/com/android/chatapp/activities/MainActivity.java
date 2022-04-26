package com.android.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.chatapp.R;
import com.android.chatapp.adapter.RecentConversationAdapter;
import com.android.chatapp.adapter.UserAdapter;
import com.android.chatapp.databinding.ActivityMainBinding;
import com.android.chatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth myAuth;
    private FirebaseDatabase database;
    List<User> users = new ArrayList<>();
    RecentConversationAdapter recentConversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        recentConversationAdapter = new RecentConversationAdapter(users, this);
        binding.usersRecentRecyclerView.setAdapter(recentConversationAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.usersRecentRecyclerView.setLayoutManager(layoutManager);
        setContentView(binding.getRoot());
        controlsHandle();
        setListeners();
        LoadUserDetails();
        getRecentChat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        database.getReference().child("User").child(myAuth.getUid()).child("isOnline").setValue(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference().child("User").child(myAuth.getUid()).child("isOnline").setValue(false);
    }

    private void getRecentChat(){
        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    String senderRoom = myAuth.getUid() + user.getUserID();
                    DatabaseReference databaseReference = database.getReference().child("chat").child(senderRoom);
//                    final boolean[] temp = {true};
////                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
////                        @Override
////                        public void onComplete(@NonNull Task<DataSnapshot> task) {
////                            if (task!= null)
////                                temp[0] = true;
////                            else temp[0] =false;
////                        }
////                    });
                    users.add(user);
                }

                recentConversationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(e -> signOut());
        binding.fabNewChat.setOnClickListener(e ->
                startActivity(new Intent(getApplicationContext(),UsersActivity.class)));
    }
    private void LoadUserDetails(){
        final User[] user = {new User()};
        database.getReference().child("User").child(myAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user[0] = snapshot.getValue(User.class);
                binding.textName.setText(user[0].getUsername());
                Picasso.get().load(user[0].getProfilePictureLink()).placeholder(R.drawable.avatar3).into(binding.imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void showToast(String mess){
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
    }
    private void signOut() {
        myAuth.signOut();
        database.getReference().child("User").child(myAuth.getUid()).child("isOnline").setValue(false);
        Intent signOut = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(signOut);
    }
    private void controlsHandle() {
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }
}