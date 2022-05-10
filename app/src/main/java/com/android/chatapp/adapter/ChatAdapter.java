package com.android.chatapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.chatapp.R;
import com.android.chatapp.databinding.ItemContainerReceivedMessageBinding;
import com.android.chatapp.databinding.ItemContainerSentMessageBinding;
import com.android.chatapp.model.ChatMessage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String imageReceiverLink;
    private final String senderId;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVER = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String imageReceiverLink, String senderId) {
        this.chatMessages = chatMessages;
        this.imageReceiverLink = imageReceiverLink;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false
            ));
        }
        else {
            return new ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }
        else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position),imageReceiverLink);
        }
// Delete message
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                new AlertDialog.Builder(context)
//                        .setTitle("Delete")
//                        .setMessage("Are you sure you want to delete this message")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                }).show();
//                return false;
//            }
//        });
        // End Delete message

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(this.senderId)){
            return VIEW_TYPE_SENT;
        }
        else return VIEW_TYPE_RECEIVER;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        public SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            Log.i("Send message",chatMessage.getMessage());
            Date date = new Date(chatMessage.getDateTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(format.format(date));
        }
    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;

        ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding binding) {
            super(binding.getRoot());
             this.binding = binding;
        }
        void setData(ChatMessage chatMessage,String profilePictureLink ){
            Date date = new Date(chatMessage.getDateTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(format .format(date));
            Picasso.get().load(profilePictureLink).placeholder(R.drawable.avatar3).into(binding.imageProfile);
        }
    }
}
