package com.mazefernandez.uplbtrade.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.activities.MessageActivity;
import com.mazefernandez.uplbtrade.activities.OfferActivity;
import com.mazefernandez.uplbtrade.models.Message;
import com.mazefernandez.uplbtrade.models.User;

import java.util.List;


/* Binds values of item information to views */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ItemViewHolder> {
    List<User> userList;
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();


        /* retrieve customer data */
        holder.user = userList.get(position);
        holder.lastMessage.setText(userList.get(position).getLastMessage());
        if (userList.get(position).getTime() != null) {
            holder.time.setText(String.format(userList.get(position).getTime()));
        }
        holder.customerEmail.setText(userList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /* Holds the values for individual views on the recycler */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView customerEmail, lastMessage, time;
        Context context;
        LinearLayout card;
        User user;


    /* View attributes */
        ItemViewHolder(View view) {
            super(view);
            customerEmail = view.findViewById(R.id.customer_email);
            lastMessage = view.findViewById(R.id.message_text);
            time = view.findViewById(R.id.time);
            card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }

/* Redirects to the message page with the details */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MessageActivity.class);
            Bundle userInfo = new Bundle();
            userInfo.putString("TEXT", lastMessage.getText().toString());
            userInfo.putString("TIME", time.getText().toString());
            userInfo.putString("EMAIL", customerEmail.getText().toString());
            intent.putExtra("MESSAGE", userInfo);
            context.startActivity(intent);
        }
    }
}
