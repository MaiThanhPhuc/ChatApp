package com.android.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.chatapp.R;
import com.android.chatapp.adapter.ChatAdapter;
import com.android.chatapp.databinding.ActivityChatBinding;
import com.android.chatapp.databinding.ActivitySignUpBinding;
import com.android.chatapp.model.ChatMessage;
import com.android.chatapp.model.User;
import com.android.chatapp.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class  ChatActivity extends AppCompatActivity {
    private ActivityChatBinding chatBinding;
    private FirebaseDatabase database;
    private FirebaseAuth myAuth;
    private User receiverUser ;
    List<ChatMessage> chatMessages = new ArrayList<>();;
    ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());
        loadReceiverDetails();
        init();
        setListeners();
        getDataMessage();
    }

    private void init(){
        chatAdapter = new ChatAdapter(chatMessages,this.receiverUser.getProfilePictureLink(),myAuth.getUid());
        chatBinding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        chatBinding.chatRecyclerView.setLayoutManager(layoutManager);
    }

    private void sendMessage(){
        String senderRoom = myAuth.getUid() + receiverUser.getUserID();
        String receiverRoom = receiverUser.getUserID() + myAuth.getUid();
        String senderID = myAuth.getUid();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(chatBinding.inputMessage.getText().toString());
        chatMessage.setDateTime(new Date().getTime());
        chatMessage.setSenderId(senderID);
        chatBinding.inputMessage.setText(null);

        database.getReference().child(Constants.KEY_COLLECTION_CHAT).child(senderRoom).push().setValue(chatMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child(Constants.KEY_COLLECTION_CHAT)
                                .child(receiverRoom).push()
                                .setValue(chatMessage)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                    }
                });

    }
    private void getDataMessage(){
        String senderRoom = myAuth.getUid() + receiverUser.getUserID();

        database.getReference().child(Constants.KEY_COLLECTION_CHAT).child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        chatMessages.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                            message.setChatId(dataSnapshot.getKey());
                            chatMessages.add(message);
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatBinding.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadReceiverDetails() {
        database = FirebaseDatabase.getInstance();
        myAuth = FirebaseAuth.getInstance();
        String receiverUserName = getIntent().getStringExtra("usrName");
        String email = getIntent().getStringExtra("email");
        String proPicLink = getIntent().getStringExtra("proPicLink");
        String receiverUID = getIntent().getStringExtra("usrID");
        receiverUser = new User();
        receiverUser.setUserID(receiverUID);
        receiverUser.setEmail(email);
        receiverUser.setProfilePictureLink(proPicLink);
        receiverUser.setUsername(receiverUserName);

        chatBinding.textName.setText(receiverUserName);

    }

    private void setListeners(){
        chatBinding.imageBack.setOnClickListener(e -> backBtn());
        chatBinding.layoutSend.setOnClickListener(e -> sendMessage());
    }


    private void backBtn() {
        Intent back= new Intent(ChatActivity.this, MainActivity.class);
        startActivity(back);
    }
}