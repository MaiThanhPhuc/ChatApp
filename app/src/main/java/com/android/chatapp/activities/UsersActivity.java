package com.android.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.android.chatapp.R;
import com.android.chatapp.databinding.ActivityUsersBinding;


public class UsersActivity extends AppCompatActivity {
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(e -> onBackPressed());
    }

    private  void getUsers(){

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