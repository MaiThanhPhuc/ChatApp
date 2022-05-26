package com.topic6gr2.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.topic6gr2.chatapp.databinding.ActivityChatBinding;
import com.topic6gr2.chatapp.adapter.ChatAdapter;

import com.topic6gr2.chatapp.model.ChatMessage;
import com.topic6gr2.chatapp.model.User;
import com.topic6gr2.chatapp.utilities.Constants;
import com.android.volley.Request;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  ChatActivity extends AppCompatActivity {
    private ActivityChatBinding chatBinding;
    private FirebaseDatabase database;
    private FirebaseAuth myAuth;
    private User receiverUser ;
    long count;
    List<ChatMessage> chatMessages = new ArrayList<>();;
    ChatAdapter chatAdapter;
    private User senderUser;

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
        database.getReference().child("User").child(myAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                senderUser = task.getResult().getValue(User.class);
            }
        });
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

        DatabaseReference databaseReference =
                database.getReference().child(Constants.KEY_COLLECTION_CHAT).child(senderRoom);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null){
                    database.getReference().child("User").child(myAuth.getUid()).child("recentChat").push().setValue(receiverUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference =
                database.getReference().child(Constants.KEY_COLLECTION_CHAT).child(receiverRoom);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null){
                    database.getReference().child("User").child(receiverUser.getUserID()).child("recentChat").push().setValue(senderUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                                        getToken(chatMessage.getMessage());
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
                        chatBinding.chatRecyclerView.scrollToPosition((int) snapshot.getChildrenCount());
                        chatMessages.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
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

        DatabaseReference databaseReference =
                database.getReference().child("User").child(receiverUID).child("isOnline");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue() == null) {
                    chatBinding.textAvailability.setText("Offline");
                    chatBinding.textAvailability.setBackgroundColor(Color.rgb(255, 0, 0));
                }
                else {
                    if (task.isSuccessful() && task.getResult().getValue() != null) {
                        boolean isOnline = (boolean) task.getResult().getValue();
                        if (isOnline) chatBinding.textAvailability.setText("Online");
                        else {
                            chatBinding.textAvailability.setText("Offline");
                            chatBinding.textAvailability.setBackgroundColor(Color.rgb(255, 0, 0));
                        }
                    }
                }

            }
        });

        chatBinding.textName.setText(receiverUserName);

    }

    private void getToken(String message){
        DatabaseReference databaseReference = database.getReference("User").child(receiverUser.getUserID());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String name = senderUser.getUsername();

                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();

                try {
                    data.put("title", name);
                    data.put("message",message);

                    to.put("to",token);
                    to.put("data",data);
                    
                    sendNotification(to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.NOTIFICATION_URL,to,
                response -> {
                    Log.d("notification","sendNotification" + response);
                }, error ->{
                    Log.d("notification","sendNotification" + error);
        }){
            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> map = new HashMap<>();
                map.put("Authorization", "key=" + Constants.SERVER_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

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