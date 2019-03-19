package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.activities.ItemActivity;
import com.mazefernandez.uplbtrade.models.Item;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* Binds values of item information to views */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.itemImg.setImageResource(R.drawable.placeholder);
        /* Format price into decimal */
        holder.itemName.setText(itemList.get(position).getItemName());
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",itemList.get(position).getPrice());
        holder.itemPrice.setText(price);
        holder.item = itemList.get(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView itemImg;
        private TextView itemName, itemPrice;
        private LinearLayout card;
        private final Context context;
        private Item item;

        ItemViewHolder(View view) {
            super(view);
            itemImg = view.findViewById(R.id.item_img);
            itemName = view.findViewById(R.id.item_name);
            itemPrice = view.findViewById(R.id.item_price);
            card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra("ITEM", item);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}