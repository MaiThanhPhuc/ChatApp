package com.topic6gr2.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.topic6gr2.chatapp.R;
import com.topic6gr2.chatapp.databinding.ActivityMainBinding;
import com.topic6gr2.chatapp.adapter.RecentConversationAdapter;
import com.topic6gr2.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        database.getReference().child("User").child(myAuth.getUid()).child("recentChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    User user = data.getValue(User.class);
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
        binding.imageProfile.setOnClickListener(e -> startActivity(
                new Intent(getApplicationContext(), SettingActivity.class)));

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
        database.getReference().child("User").child(myAuth.getUid()).child("isOnline").setValue(false);
        myAuth.signOut();
        Intent signOut = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(signOut);
    }
    private void controlsHandle() {
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }
}