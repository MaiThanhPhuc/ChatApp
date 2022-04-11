package com.android.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;

import com.android.chatapp.R;
import com.android.chatapp.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.textSignIn.setOnClickListener(e -> onBackPressed());
    }
}