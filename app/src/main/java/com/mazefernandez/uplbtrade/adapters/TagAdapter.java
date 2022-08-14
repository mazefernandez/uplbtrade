package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.activities.OfferActivity;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;
import com.mazefernandez.uplbtrade.models.Tag;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.BitmapFactory.decodeByteArray;


/* Binds values of offer information to views */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ItemViewHolder> {
    private final ArrayList<Tag> tagList;
    public TagAdapter(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tag_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        /* retrieve tag name */
        holder.tagName.setText(tagList.get(position).getTagName());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    /* Holds the values for individual views on the recycler */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;

        /* View attributes */
        ItemViewHolder(View view) {
            super(view);
            tagName = view.findViewById(R.id.tag_name);
        }
    }
}