package com.topic6gr2.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.topic6gr2.chatapp.databinding.ActivityUsersBinding;
import com.topic6gr2.chatapp.adapter.UserAdapter;

import com.topic6gr2.chatapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersActivity extends AppCompatActivity {
    ActivityUsersBinding binding;
    private FirebaseDatabase database;
    List<User> users = new ArrayList<>();
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userAdapter = new UserAdapter(users,this);
        binding.usersRecyclerView.setAdapter(userAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.usersRecyclerView.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance();
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(e -> onBackPressed());
    }

    private  void getUsers(){
        loading(true);
        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    users.add(user);
                }
                userAdapter.notifyDataSetChanged();
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.usersRecyclerView.setVisibility(View.VISIBLE);

    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
    }
    private  void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}