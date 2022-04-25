package com.android.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatapp.R;
import com.android.chatapp.activities.ChatActivity;
import com.android.chatapp.databinding.ItemContainerRecentConversionBinding;
import com.android.chatapp.model.ChatMessage;
import com.android.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder>  {
    List<User> users;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public RecentConversationAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_recent_conversion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        String senderRoom = FirebaseAuth.getInstance().getUid().toString() + user.getUserID();
        Picasso.get().load(user.getProfilePictureLink()).placeholder(R.drawable.avatar3).into(holder.imgProfile);
        holder.name.setText(user.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("usrID", user.getUserID());
                intent.putExtra("usrName", user.getUsername());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("proPicLink", user.getProfilePictureLink());
                context.startActivity(intent);
            }
        });
        database.getReference().child("chat")
                .child(senderRoom).orderByChild("dateTime")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            holder.lastMessage.setText(dataSnapshot.child("message").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgProfile;
        TextView name, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imageProfile);
            name = itemView.findViewById(R.id.textName);
            lastMessage = itemView.findViewById(R.id.textRecentMessage);
        }
    }
}
