package com.mazefernandez.uplbtrade.adapters;

import android.content.Context;
import android.content.Intent;
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
        holder.customerName.setText(userList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /* Holds the values for individual views on the recycler */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView customerName;
        private final Context context;


    /* View attributes */
        ItemViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.customer_name);
            LinearLayout card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }


/* Redirects to an item page with the details */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}
